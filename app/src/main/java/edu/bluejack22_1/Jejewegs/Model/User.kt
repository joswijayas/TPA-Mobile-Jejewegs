package edu.bluejack22_1.Jejewegs.Model

class User {
    var user_email:String? = null
    var user_fav_sneaker:String? = null
    var user_fullname:String? = null
    var user_id:String? = null
    var user_location:String? = null
    var user_followers:List<String>? = emptyList()
    var user_followings:List<String>? = emptyList()
    var user_reviews:List<String>? = emptyList()
}