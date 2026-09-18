package com.example.productlist.data.repository

import com.example.productlist.data.local.SavedProductsDataSource
import com.example.productlist.data.model.Product
import com.example.productlist.data.remote.ProductRemoteDataSource

/**
 * Default implementation. Combines the remote data source (network) and
 * the local data source (saved products) but exposes neither of them to
 * callers -- the ViewModel only ever sees the ProductRepository interface,
 * never Retrofit or the in-memory saved-products store directly.
 */
class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
    private val savedProductsDataSource: SavedProductsDataSource
) : ProductRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        // runCatching converts any thrown exception (IOException for no
        // connectivity, HttpException for a 4xx/5xx, a serialization
        // exception for malformed JSON, etc.) into a Result.failure
        // instead of crashing the caller. That's what lets the ViewModel
        // show an Error UI state and offer a Retry action.
        return runCatching { remoteDataSource.fetchProducts() }
    }

    override fun isSaved(productId: Int): Boolean =
        savedProductsDataSource.isSaved(productId)

    override fun toggleSaved(product: Product) {
        if (savedProductsDataSource.isSaved(product.id)) {
            savedProductsDataSource.unsave(product.id)
        } else {
            savedProductsDataSource.save(product)
        }
    }
}
