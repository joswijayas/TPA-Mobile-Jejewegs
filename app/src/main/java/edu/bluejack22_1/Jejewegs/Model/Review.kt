package edu.bluejack22_1.Jejewegs.Model

data class Review(
    var reviewer_id: String? = null,
    var review_brand: String? = null,
    var reviewer_title: String? = null,
    var review_description: String? = null,
    var review_image: String? = null,
    var review_rate: String? = null,
    var review_likes: List<String>? = emptyList(),
    var review_comments: List<String>? = emptyList(),
    var review_id: String? = null,
    var insensitive_data: String? = null
)
