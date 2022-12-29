package edu.bluejack22_1.Jejewegs.Model

class User {
    var user_email:String? = ""
    var user_fav_sneaker:String? = ""
    var user_fullname:String? = ""
    var user_id:String? = ""
    var user_location:String? = ""
    var user_followers:List<String>? = emptyList()
    var user_followings:List<String>? = emptyList()
    var user_reviews:List<String>? = emptyList()
    var user_wishlists:List<String>? = emptyList()
    var user_liked_review:List<String>? = emptyList()
}