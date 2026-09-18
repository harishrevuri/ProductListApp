package com.example.productlist.data.local

import com.example.productlist.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Abstraction over local persistence of "saved" (favorited) products.
 *
 * NOTE ON SCOPE: for this exercise this is an in-memory store guarded by
 * a StateFlow<Map<Int, Product>>, keyed by product id. Duplicate saves
 * are structurally impossible because Map.put with an existing key just
 * overwrites the value -- it never creates a second entry for the same id.
 *
 * In a production build, this interface's implementation would be swapped
 * for a Room DAO-backed class (see README "Where Room would go") without
 * any change to ProductRepository or the ViewModel, because both only
 * depend on this interface.
 */
interface SavedProductsDataSource {
    val savedProducts: StateFlow<Map<Int, Product>>
    fun save(product: Product)
    fun unsave(productId: Int)
    fun isSaved(productId: Int): Boolean
}

/**
 * Default in-memory implementation used by the running app.
 */
class InMemorySavedProductsDataSource : SavedProductsDataSource {

    private val _savedProducts = MutableStateFlow<Map<Int, Product>>(emptyMap())
    override val savedProducts: StateFlow<Map<Int, Product>> = _savedProducts

    override fun save(product: Product) {
        // Keying by product.id is what prevents duplicate saves: saving
        // the same id twice just replaces the existing map entry.
        _savedProducts.update { current -> current + (product.id to product) }
    }

    override fun unsave(productId: Int) {
        _savedProducts.update { current -> current - productId }
    }

    override fun isSaved(productId: Int): Boolean =
        _savedProducts.value.containsKey(productId)
}
