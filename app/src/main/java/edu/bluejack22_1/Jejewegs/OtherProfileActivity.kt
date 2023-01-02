package edu.bluejack22_1.Jejewegs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.databinding.ActivityOtherProfileBinding

class OtherProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtherProfileBinding
    private lateinit var targetUid: String
    private lateinit var currentUid: String

    private var db = Firebase.firestore
    private var followed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setIntentValues()
        setUserData()
        followBtnListener()
        setContentView(binding.root)
    }

    private fun setIntentValues(){
        currentUid = intent.getStringExtra("currentUid").toString()
        targetUid = intent.getStringExtra("target").toString()
    }

    private fun setUserData(){
        val ref = db.collection("users").document(targetUid)
        ref.get().addOnSuccessListener {
            if(it != null){
                val fullName = it.data?.get("user_fullname")?.toString()
                val email = it.data?.get("user_email")?.toString()
                val location = it.data?.get("user_location")?.toString()
                val favoriteSneaker = it.data?.get("user_fav_sneaker")?.toString()
                val image = it.data?.get("user_image")?.toString()
                val followers = it.data?.get("user_followers") as? List<*>
                val followings = it.data?.get("user_followings") as? List<*>
                val reviews = it.data?.get("user_reviews") as? List<*>

                if(image != null && !image.isEmpty()){
                    Glide.with(binding.profileImage.context).load(image).into(binding.profileImage)
                }

                if(followers == null){
                    binding.followersCount.text = 0.toString()
                }else{
                    binding.followersCount.text = (followers)?.size.toString()
                }
                if(followings == null){
                    binding.followingsCount.text = 0.toString()
                }else{
                    binding.followingsCount.text = (followings)?.size.toString()
                }
                if(reviews == null){
                    binding.reviewsCount.text = 0.toString()
                }else{
                    binding.reviewsCount.text = (reviews)?.size.toString()
                }


                binding.etFullName.setText(fullName.toString())
                binding.etEmailProfile.setText(email.toString())
                binding.etLocation.setText(location.toString())
                binding.etFavoriteSneaker.setText(favoriteSneaker.toString())
                binding.tvReviewerId.text = targetUid
                db.collection("users").document(targetUid).get().addOnSuccessListener {
                    if (it != null) {
                        val followers = it.data?.get("user_followers") as? List<*>
                        if (followers != null) {
                            for(data in followers){

                                if(data.toString().equals(currentUid)){
                                    Log.d("test", "followed")
                                    binding.btnFollow.visibility = View.GONE
                                    binding.btnUnfollow.visibility = View.VISIBLE
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }

    }


    private fun followBtnListener(){
        binding.btnFollow.setOnClickListener {
            db.collection("users").document(currentUid).update("user_followings", FieldValue.arrayUnion(targetUid))
            db.collection("users").document(targetUid).update("user_followers", FieldValue.arrayUnion(currentUid))
            binding.btnFollow.visibility = View.GONE
            binding.btnUnfollow.visibility = View.VISIBLE
            setUserData()
        }

        binding.btnUnfollow.setOnClickListener {
            db.collection("users").document(currentUid).update("user_followings", FieldValue.arrayRemove(targetUid))
            db.collection("users").document(targetUid).update("user_followers", FieldValue.arrayRemove(currentUid))
            binding.btnUnfollow.visibility = View.GONE
            binding.btnFollow.visibility = View.VISIBLE
            setUserData()
        }
    }
}