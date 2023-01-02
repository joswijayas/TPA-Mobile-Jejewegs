package edu.bluejack22_1.Jejewegs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.databinding.ActivityReviewDetailBinding


class ReviewDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewDetailBinding
    private lateinit var uid : String
    private lateinit var reviewer_id : String
    private var sum_rate = 0
    private var rate_count = 0
    private var avege = 0

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ddid", intent.getStringExtra("dataid").toString())
        var reviewId = intent.getStringExtra("dataid").toString()
        Log.d("detail", "detailll")
        super.onCreate(savedInstanceState)
        binding = ActivityReviewDetailBinding.inflate(layoutInflater)
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var docRef = db.collection("reviews").document(reviewId)
        docRef.get().addOnSuccessListener {
            it->
            reviewer_id = it.get("reviewer_id").toString()
            if (!uid.equals(reviewer_id)){
//                Glide.with(binding.ivReviewImage.context).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                binding.overallRating.visibility = View.GONE
                binding.ratingViewer.visibility = View.VISIBLE
                binding.giveRateBtn.setOnClickListener{
                    val intRate =  binding.etRatings.text.toString()
                    Log.d("intrate", intRate)
                    if(!intRate.equals("1") && !intRate.equals("2") && !intRate.equals("3") && !intRate.equals("4") && !intRate.equals("5")){
                        binding.etRatings.error = getString(R.string.rate_min_1_max_5)
                        binding.etRatings.requestFocus()
                    }else{
                        db.collection("reviews").document(reviewId).update("review_people_rating", FieldValue.increment(intRate.toInt().toDouble())).addOnSuccessListener {
                            db.collection("reviews").document(reviewId).update("review_people_count", FieldValue.increment(1.toDouble())).addOnSuccessListener {
                                docRef.addSnapshotListener{ it, err->
                                    if (it != null && it.exists()) {
                                        Glide.with(binding.ivReviewImage.context).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                                    }
                                }
                                Toast.makeText(baseContext, "Success give rate", Toast.LENGTH_SHORT)
                            }
                        }
                    }
                }
            }else{
//                Glide.with(binding.ivReviewImage.context).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnEdit.setOnClickListener{
                    binding.btnSave.visibility = View.VISIBLE
                    binding.btnEdit.visibility = View.GONE
                    binding.tvReviewTitle.isEnabled = true
                    binding.tvReviewDescription.isEnabled = true
                }
                binding.btnSave.setOnClickListener{
                    if(binding.tvReviewTitle.text.toString().isEmpty()){
                        binding.tvReviewTitle.error = getString(R.string.allfield_must_filled)
                        binding.tvReviewTitle.requestFocus()

                    }
                    else if(binding.tvReviewDescription.text.toString().isEmpty()){
                        binding.tvReviewDescription.error = getString(R.string.allfield_must_filled)
                        binding.tvReviewDescription.requestFocus()
                    }
                    else{
                        var docRef = db.collection("reviews").document(reviewId)
                        docRef.update(
                            "reviewer_title",binding.tvReviewTitle.text.toString(),
                            "review_description",binding.tvReviewDescription.text.toString()
                        ).addOnSuccessListener {
                            Toast.makeText(baseContext, getString(R.string.success_update), Toast.LENGTH_SHORT)
                            binding.tvReviewTitle.isEnabled = false
                            binding.tvReviewDescription.isEnabled = false
                            binding.btnSave.visibility = View.GONE
                            binding.btnEdit.visibility = View.VISIBLE
                            docRef.addSnapshotListener{ it, err->
                                if (it != null && it.exists()) {
                                    Glide.with(binding.ivReviewImage.context).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                                }
                            }
                        }
                    }
                }
            }
        }
        docRef.addSnapshotListener{ it, err->
            if(it != null && it.exists()){
                Glide.with(applicationContext).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                val comments = it.data?.get("review_comments") as? List<*>
                val likes = it.data?.get("review_likes") as? List<*>
                Log.d("imageurl", it.get("review_image").toString())

                binding.ivReviewImage.setImageURI(Uri.parse(it.get("review_image").toString()))
                binding.tvReviewTitle.setText(it.get("reviewer_title").toString())
                binding.tvReviewDescription.setText(it.get("review_description").toString())
                binding.tvReviewBrand.text = it.get("review_brand").toString()
                binding.tvReviewRate.text = it.get("review_rate").toString()
                binding.tvReviewerId.text = it.get("reviewer_id").toString()
                reviewer_id = it.get("reviewer_id").toString()
                binding.tvReviewCommentCount.text = comments?.size.toString()
                binding.tvReviewLikeCount.text = likes?.size.toString()
                val rates = it.get("review_people_rating")
                val count = it.get("review_people_count")
                if (rates != null && count != null) {
                    avege = rates.toString().toDouble().toInt()/count.toString().toDouble().toInt()
                    binding.tvAvgRating.text = avege.toString()
                }else{
                    binding.tvAvgRating.text = 0.toString()
                }
                if (likes != null) {
                    if(likes.contains(uid)){
                        binding.ivLike.visibility = View.GONE
                        binding.ivLikeColored.visibility = View.VISIBLE
                    }else{
                        binding.ivLike.visibility = View.VISIBLE
                        binding.ivLikeColored.visibility = View.GONE
                    }
                }
                if(it.get("reviewer_id")?.equals(uid) == true){
                    binding.ivDelete.visibility = View.VISIBLE
                }
            }
        }



        binding.ivLike.setOnClickListener{
            db.collection("reviews").document(reviewId).update("review_likes", FieldValue.arrayUnion(uid))
            binding.ivLike.visibility = View.GONE
            binding.ivLikeColored.visibility = View.VISIBLE
            var name = ""
            db.collection("users").document(uid).get().addOnSuccessListener {
                name = it.get("user_fullname").toString()
                docRef.addSnapshotListener{ it, err->
                    if (it != null && it.exists()) {
                        Glide.with(applicationContext).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                        db.collection("users").document(it.get("reviewer_id").toString()).update("user_notifications", FieldValue.arrayUnion(name + " notif_liked," + it.get("reviewer_title").toString()))
                    }
                }
                db.collection("users").document(uid).update("review_likes", FieldValue.arrayUnion(reviewId))
            }

        }
        binding.ivLikeColored.setOnClickListener{
            db.collection("reviews").document(reviewId).update("review_likes", FieldValue.arrayRemove(uid))
            binding.ivLike.visibility = View.VISIBLE
            binding.ivLikeColored.visibility = View.GONE

            var name = ""
            db.collection("users").document(uid).get().addOnSuccessListener {
                name = it.get("user_fullname").toString()
                docRef.addSnapshotListener{ it, err->
                    if (it != null && it.exists()) {
                        Glide.with(applicationContext).load(Uri.parse(it.get("review_image").toString())).into(binding.ivReviewImage)
                        db.collection("users").document(it.get("reviewer_id").toString()).update("user_notifications", FieldValue.arrayRemove(name + " notif_liked," + it.get("reviewer_title").toString()))
                    }
                }
                db.collection("users").document(uid).update("review_likes", FieldValue.arrayRemove(reviewId))
            }


        }

        binding.ivDelete.setOnClickListener{
            db.collection("reviews").document(reviewId)
                .delete()
                .addOnSuccessListener {
                    db.collection("users").document(uid).update("review_likes", FieldValue.arrayRemove(reviewId)).addOnSuccessListener {
                        db.collection("users").document(uid).update("user_wishlists", FieldValue.arrayRemove(reviewId)).addOnSuccessListener {
                            db.collection("users").document(uid).update("user_reviews", FieldValue.arrayRemove(reviewId))
                        }
                    }
                    val i = Intent(this, Home::class.java)
                    startActivity(i)
                }
                .addOnFailureListener { e -> Log.w("err", "Error deleting document", e) }
            db.collection("users").document(uid).update("user_reviews", FieldValue.arrayRemove(reviewId))
        }

        binding.ivComment.setOnClickListener{
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("dataid", reviewId)
            startActivity(intent)
        }


        setContentView(binding.root)

    }
}