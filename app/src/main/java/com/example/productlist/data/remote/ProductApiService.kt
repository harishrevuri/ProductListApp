package com.example.productlist.data.remote

import com.example.productlist.data.model.ProductsResponse
import retrofit2.http.GET

/**
 * Retrofit endpoint definition -- the thinnest possible layer over the
 * network call. Kept isolated from the rest of the app so the HTTP
 * client/library could be swapped without touching the repository or
 * ViewModel (see ProductRemoteDataSource, which is what callers actually
 * depend on).
 */
interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): ProductsResponse
}
