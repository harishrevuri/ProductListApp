package com.example.productlist.data.model

import kotlinx.serialization.Serializable

/**
 * Wrapper matching the top-level shape of GET /products:
 *   { "products": [...], "total": N, "skip": N, "limit": N }
 * Only "products" is needed for this screen; total/skip/limit would
 * become relevant if/when pagination is added (see README).
 */
@Serializable
data class ProductsResponse(
    val products: List<Product> = emptyList()
)
