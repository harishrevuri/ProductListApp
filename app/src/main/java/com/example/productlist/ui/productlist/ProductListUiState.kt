package com.example.productlist.ui.productlist

import com.example.productlist.data.model.Product

/**
 * Everything the screen needs to render, expressed as a sealed hierarchy
 * so the Composable can exhaustively "when" over it -- the compiler
 * enforces that Loading/Content/Empty/Error are all handled, satisfying
 * the brief's requirement to show all four states.
 */
sealed interface ProductListUiState {
    /** Initial state and state shown while a fetch is in flight. */
    data object Loading : ProductListUiState

    /** Fetch succeeded and returned at least one product. */
    data class Content(val products: List<ProductUiModel>) : ProductListUiState

    /** Fetch succeeded but returned zero products. */
    data object Empty : ProductListUiState

    /** Fetch failed; [message] is shown to the user alongside a Retry action. */
    data class Error(val message: String) : ProductListUiState
}

/**
 * Thin UI-facing wrapper around Product that also carries whether the
 * product is currently saved, so the Composable never needs to know
 * about the saved-products data source.
 */
data class ProductUiModel(
    val product: Product,
    val isSaved: Boolean
)
