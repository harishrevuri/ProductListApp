package com.example.productlist.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productlist.data.model.Product
import com.example.productlist.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Owns UI state for the product list screen. It talks only to
 * ProductRepository -- it knows nothing about Retrofit, JSON parsing, or
 * how "saved" is persisted. That separation is what makes this class
 * trivially unit-testable with a fake repository/data sources and is the
 * core of the MVVM split used throughout this project:
 *
 *   ProductListScreen (View)  -->  ProductListViewModel (ViewModel)  -->  ProductRepository (Model)
 */
class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Triggers (or re-triggers, e.g. from the Retry button in ErrorState)
     * a product fetch. Always shows Loading first so the UI has clear,
     * immediate feedback that a request is in progress.
     */
    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        viewModelScope.launch {
            repository.getProducts()
                .onSuccess { products -> _uiState.value = toUiState(products) }
                .onFailure { throwable ->
                    _uiState.value = ProductListUiState.Error(
                        throwable.message ?: "Something went wrong. Please try again."
                    )
                }
        }
    }

    /** Called when the user taps the save/unsave icon on a row. */
    fun toggleSaved(product: Product) {
        repository.toggleSaved(product)

        // Re-derive the saved flag for just that one row from the current
        // Content state so the icon updates instantly, with no new
        // network call and no full-list re-fetch.
        val current = _uiState.value
        if (current is ProductListUiState.Content) {
            _uiState.value = current.copy(
                products = current.products.map { uiModel ->
                    if (uiModel.product.id == product.id) {
                        uiModel.copy(isSaved = repository.isSaved(product.id))
                    } else {
                        uiModel
                    }
                }
            )
        }
    }

    /** Maps a raw product list from the repository into a renderable UiState. */
    private fun toUiState(products: List<Product>): ProductListUiState {
        if (products.isEmpty()) return ProductListUiState.Empty
        return ProductListUiState.Content(
            products = products.map { product ->
                ProductUiModel(product = product, isSaved = repository.isSaved(product.id))
            }
        )
    }
}
