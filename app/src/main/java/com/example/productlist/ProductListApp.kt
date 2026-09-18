package com.example.productlist

import android.app.Application
import com.example.productlist.data.local.InMemorySavedProductsDataSource
import com.example.productlist.data.local.SavedProductsDataSource
import com.example.productlist.data.remote.ProductApiService
import com.example.productlist.data.remote.ProductRemoteDataSource
import com.example.productlist.data.remote.RetrofitProductRemoteDataSource
import com.example.productlist.data.repository.ProductRepository
import com.example.productlist.data.repository.ProductRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Hand-rolled composition root ("Model" wiring in MVVM). There is no
 * Hilt/Koin dependency added for this exercise, so this Application
 * subclass builds and exposes the few singletons the app needs
 * (Retrofit client -> remote data source -> repository).
 *
 * In production this class's contents would be replaced by a Hilt
 * @Module / @InstallIn(SingletonComponent::class) providing the same
 * types, with @Inject constructors on the ViewModel.
 */
class ProductListApp : Application() {

    lateinit var repository: ProductRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // ignoreUnknownKeys = true lets Product/ProductsResponse decode
        // only the fields the brief asks for (id, title, description,
        // thumbnail) while safely discarding everything else dummyjson
        // returns (price, brand, rating, images, etc.).
        val json = Json { ignoreUnknownKeys = true }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val api = retrofit.create(ProductApiService::class.java)
        val remoteDataSource: ProductRemoteDataSource = RetrofitProductRemoteDataSource(api)
        val savedProductsDataSource: SavedProductsDataSource = InMemorySavedProductsDataSource()

        repository = ProductRepositoryImpl(remoteDataSource, savedProductsDataSource)
    }
}
