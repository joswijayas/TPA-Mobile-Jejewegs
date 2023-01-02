package edu.bluejack22_1.Jejewegs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.Adapter.UserAdapter
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var userAdapter: UserAdapter
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var reviewIdList: ArrayList<String>
    private lateinit var userLists: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        goToHome()
        searchReviewListener()
        searchUserListener()
        setContentView(binding.root)
    }

    private fun searchReviewListener(){
        binding.btnSearchReview.setOnClickListener {
            val searchedText = binding.etSearch.text.toString()
            searchReview(searchedText)
        }
    }

    private fun searchUserListener(){
        binding.btnSearchUser.setOnClickListener {
            val searchedText = binding.etSearch.text.toString()
            searchUser(searchedText)
        }
    }

    private fun searchReview(searched_review: String){
        Log.d("test", "searched: $searched_review")
        val db = Firebase.firestore
        db.collection("reviews").orderBy("reviewer_title")
            .startAt(searched_review).endAt(searched_review+"\uf8ff")
            .get().addOnSuccessListener {
//                Log.d("test", "it: $it")
//                Log.d("test", "it.doc.size: ${it.documents.size}")
                if(it.documents.size == 0){
                    binding.tvNotFound.visibility = View.VISIBLE
                }
                else{
                    binding.tvNotFound.visibility = View.GONE
                }
                reviewList = arrayListOf()
                reviewIdList = arrayListOf()
                reviewList.clear()
                reviewIdList.clear()
                for (x in it) {
//                    Log.d("test", "x.id: ${x.id}")
//                    val title = x.data["reviewer_title"].toString()
                    val review:Review? = x.toObject(Review::class.java)
//                    Log.d("test", "review title: ${review!!.reviewer_title}")
                    if(review != null){
                        review.review_id = x.id
                        reviewList.add(review)
                        reviewIdList.add(x.id)
                    }
//                    Log.d("test", "title: $title")
                }
                applyReviewAdapter()
            }.addOnFailureListener {
                Log.d("test", "error review adapter")
            }
    }

    private fun searchUser(searchedText: String){
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var user_id = ""
        db.collection("users").document(userId).get().addOnSuccessListener {
            if(it != null){
                user_id = it.data?.get("user_id").toString()
            }
        }

        db.collection("users").orderBy("user_fullname")
            .startAt(searchedText).endAt(searchedText+"\uf8ff")
            .get().addOnSuccessListener {
                userLists = arrayListOf()
                userLists.clear()
                for(x in it){
                    val user:User? = x.toObject(User::class.java)
                    if(user != null && !user.user_id.equals(user_id)){
                        userLists.add(user)
                    }
                }
                if(userLists.size == 0){
                    binding.tvNotFound.visibility = View.VISIBLE
                }
                else{
                    binding.tvNotFound.visibility = View.GONE
                }
                applyUserAdapter()
            }.addOnFailureListener {
                Log.d("test", "error user adapter")
            }
    }

    private fun applyReviewAdapter(){
        reviewAdapter = ReviewAdapter(reviewList, reviewIdList, "1")
        recyclerView = findViewById(R.id.recycleViewSearch)
        recyclerView.adapter = reviewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        reviewAdapter.onItemClicked = {
            val intent = Intent(this, ReviewDetailActivity::class.java)
            intent.putExtra("dataid", it.review_id)
            startActivity(intent)
        }
    }

    private fun applyUserAdapter(){
        userAdapter = UserAdapter(userLists)
        recyclerView = findViewById(R.id.recycleViewSearch)
        recyclerView.adapter = userAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun goToHome(){
        binding.btnHome.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
//            onResumeFragments()
            startActivity(intent)
        }
    }
}