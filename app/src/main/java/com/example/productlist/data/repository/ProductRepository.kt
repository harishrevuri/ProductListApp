package com.example.productlist.data.repository

import com.example.productlist.data.model.Product

/**
 * The single source of truth the ViewModel talks to. This interface hides
 * whether data comes from the network or local storage behind one API
 * surface -- that boundary is what keeps UI / ViewModel / data-access
 * responsibilities separated, per the brief's requirements.
 */
interface ProductRepository {
    /** Fetches the product list. Never throws -- failures are wrapped in Result.failure. */
    suspend fun getProducts(): Result<List<Product>>

    /** True if [productId] is currently saved locally. */
    fun isSaved(productId: Int): Boolean

    /** Saves [product] if it isn't saved yet, otherwise unsaves it. */
    fun toggleSaved(product: Product)
}
