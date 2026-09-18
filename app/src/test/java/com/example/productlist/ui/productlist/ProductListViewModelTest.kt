package com.example.productlist.ui.productlist

import com.example.productlist.data.local.FakeSavedProductsDataSource
import com.example.productlist.data.model.Product
import com.example.productlist.data.remote.FakeProductRemoteDataSource
import com.example.productlist.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for the ViewModel layer. Uses a real ProductRepositoryImpl
 * wired to two fakes (remote + saved), which exercises the whole
 * Model -> ViewModel path while staying fully offline and deterministic.
 */
class ProductListViewModelTest {

    // A controllable coroutine dispatcher: nothing the ViewModel launches
    // actually runs until we call advanceUntilIdle(), which lets us
    // assert the intermediate Loading state deterministically.
    private val dispatcher = StandardTestDispatcher()

    private lateinit var remote: FakeProductRemoteDataSource
    private lateinit var saved: FakeSavedProductsDataSource

    private val product1 = Product(1, "Phone", "A nice phone", "http://img1")
    private val product2 = Product(2, "Laptop", "A fast laptop", "http://img2")

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        remote = FakeProductRemoteDataSource()
        saved = FakeSavedProductsDataSource()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): ProductListViewModel {
        val repository = ProductRepositoryImpl(remote, saved)
        return ProductListViewModel(repository)
    }

    @Test
    fun `initial state is Loading before the fetch completes`() {
        remote.setProducts(listOf(product1))

        val viewModel = buildViewModel()

        // The test dispatcher hasn't run any queued coroutines yet, so
        // the ViewModel must still be showing Loading right after init.
        assertEquals(ProductListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `successful load with products emits Content`() = runTest(dispatcher) {
        remote.setProducts(listOf(product1, product2))
        val viewModel = buildViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProductListUiState.Content)
        assertEquals(2, (state as ProductListUiState.Content).products.size)
    }

    @Test
    fun `successful load with an empty list emits Empty`() = runTest(dispatcher) {
        remote.setProducts(emptyList())
        val viewModel = buildViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(ProductListUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `failed load emits Error`() = runTest(dispatcher) {
        remote.setError(IOException("boom"))
        val viewModel = buildViewModel()

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProductListUiState.Error)
    }

    @Test
    fun `retry after an error re-fetches and can succeed`() = runTest(dispatcher) {
        remote.setError(IOException("boom"))
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ProductListUiState.Error)

        // Simulate the network recovering, then invoke the same function
        // the UI's Retry button calls.
        remote.setProducts(listOf(product1))
        viewModel.loadProducts()
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProductListUiState.Content)
    }

    @Test
    fun `toggleSaved marks a product as saved without a new network call`() = runTest(dispatcher) {
        remote.setProducts(listOf(product1))
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSaved(product1)

        val state = viewModel.uiState.value as ProductListUiState.Content
        assertTrue(state.products.first { it.product.id == product1.id }.isSaved)
    }

    @Test
    fun `toggleSaved twice on the same product returns it to unsaved`() = runTest(dispatcher) {
        remote.setProducts(listOf(product1))
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSaved(product1)
        viewModel.toggleSaved(product1)

        val state = viewModel.uiState.value as ProductListUiState.Content
        assertFalse(state.products.first { it.product.id == product1.id }.isSaved)
    }
}
