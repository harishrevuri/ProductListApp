package com.example.productlist.data.remote

import com.example.productlist.data.model.Product

interface ProductRemoteDataSource {
    suspend fun fetchProducts(): List<Product>
}

/**
 * Production implementation, backed by Retrofit/OkHttp.
 */
class RetrofitProductRemoteDataSource(
    private val api: ProductApiService
) : ProductRemoteDataSource {

    override suspend fun fetchProducts(): List<Product> {
        return api.getProducts().products
    }
}
