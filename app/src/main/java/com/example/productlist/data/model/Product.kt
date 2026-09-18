package com.example.productlist.data.model

import kotlinx.serialization.Serializable

/**
 * Domain/data model representing a single product.
 *
 * Only the fields required by the assignment are decoded here: id, title,
 * description, and thumbnail. The Json instance used by Retrofit
 * (see ProductListApp) is configured with ignoreUnknownKeys = true, so
 * the many other fields dummyjson.com returns (price, brand, rating, ...)
 * are simply skipped instead of causing a parse failure.
 */
@Serializable
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val thumbnail: String
)
