package com.example.productlist.data.remote

import com.example.productlist.data.model.Product

/**
 * Fake implementation of ProductRemoteDataSource used across tests.
 * Lets each test dictate exactly what "the network" returns or throws
 * without ever touching Retrofit/OkHttp -- this is the fakeable data
 * source the assignment asks for.
 */
class FakeProductRemoteDataSource(
    private var products: List<Product> = emptyList(),
    private var error: Throwable? = null
) : ProductRemoteDataSource {

    fun setProducts(newProducts: List<Product>) {
        products = newProducts
        error = null
    }

    fun setError(throwable: Throwable) {
        error = throwable
    }

    override suspend fun fetchProducts(): List<Product> {
        error?.let { throw it }
        return products
    }
}
