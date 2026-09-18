package com.example.productlist.data.local

import com.example.productlist.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory fake with behavior identical to InMemorySavedProductsDataSource,
 * kept as its own test double so production and test code can evolve
 * independently (e.g. once InMemorySavedProductsDataSource is replaced
 * with a Room-backed implementation, this fake still works unchanged).
 */
class FakeSavedProductsDataSource : SavedProductsDataSource {
    private val _savedProducts = MutableStateFlow<Map<Int, Product>>(emptyMap())
    override val savedProducts: StateFlow<Map<Int, Product>> = _savedProducts

    override fun save(product: Product) {
        _savedProducts.value += (product.id to product)
    }

    override fun unsave(productId: Int) {
        _savedProducts.value -= productId
    }

    override fun isSaved(productId: Int): Boolean =
        _savedProducts.value.containsKey(productId)
}
