package edu.bluejack22_1.Jejewegs.Model

data class Review(
    val reviewer_id: String? = null,
    val review_brand: String? = null,
    val reviewer_title: String? = null,
    val review_description: String? = null,
    val review_image: String? = null,
    val review_rate: String? = null,
    val review_likes: List<String>? = null,
    val review_comments: List<String>? = null
)
