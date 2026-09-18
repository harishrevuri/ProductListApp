package com.example.productlist.data.repository

import com.example.productlist.data.local.FakeSavedProductsDataSource
import com.example.productlist.data.model.Product
import com.example.productlist.data.remote.FakeProductRemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for the repository (Model layer). Both dependencies are
 * fakes, so these tests run instantly with no real network/disk access.
 */
class ProductRepositoryImplTest {

    private lateinit var remote: FakeProductRemoteDataSource
    private lateinit var saved: FakeSavedProductsDataSource
    private lateinit var repository: ProductRepositoryImpl

    private val sampleProduct = Product(
        id = 1,
        title = "Phone",
        description = "A nice phone",
        thumbnail = "http://example.com/img.png"
    )

    @Before
    fun setUp() {
        remote = FakeProductRemoteDataSource()
        saved = FakeSavedProductsDataSource()
        repository = ProductRepositoryImpl(remote, saved)
    }

    @Test
    fun `getProducts returns success with products from remote data source`() = runTest {
        remote.setProducts(listOf(sampleProduct))

        val result = repository.getProducts()

        assertTrue(result.isSuccess)
        assertEquals(listOf(sampleProduct), result.getOrNull())
    }

    @Test
    fun `getProducts returns failure when remote data source throws`() = runTest {
        remote.setError(IOException("network down"))

        val result = repository.getProducts()

        assertTrue(result.isFailure)
    }

    @Test
    fun `toggleSaved saves a previously unsaved product`() {
        assertFalse(repository.isSaved(sampleProduct.id))

        repository.toggleSaved(sampleProduct)

        assertTrue(repository.isSaved(sampleProduct.id))
    }

    @Test
    fun `toggleSaved unsaves an already-saved product`() {
        repository.toggleSaved(sampleProduct) // save
        repository.toggleSaved(sampleProduct) // unsave

        assertFalse(repository.isSaved(sampleProduct.id))
    }

    @Test
    fun `toggling the same product id twice never creates a duplicate entry`() {
        repository.toggleSaved(sampleProduct)
        repository.toggleSaved(sampleProduct.copy(title = "Same id, different title"))

        // Ends unsaved (two toggles cancel out) and the backing map only
        // ever had one entry for this id, proving duplicates are impossible.
        assertFalse(repository.isSaved(sampleProduct.id))
        assertEquals(0, saved.savedProducts.value.size)
    }
}
