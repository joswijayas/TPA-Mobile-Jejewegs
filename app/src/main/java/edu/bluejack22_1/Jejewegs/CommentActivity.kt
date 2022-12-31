package edu.bluejack22_1.Jejewegs

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.CommentAdapter
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.Model.Comment
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.databinding.ActivityCommentBinding
import edu.bluejack22_1.Jejewegs.databinding.ActivityReviewDetailBinding

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var uid : String
//    private lateinit var reviewer_id : String
    private var db = Firebase.firestore
    private lateinit var recyclerView : RecyclerView
    private lateinit var commentList: ArrayList<Comment>
    private lateinit var comment_ids: ArrayList<String>
    private lateinit var adapterComment: CommentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ddid", intent.getStringExtra("dataid").toString())
        var reviewId = intent.getStringExtra("dataid").toString()
        Log.d("detail", "detailll")
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var docRef = db.collection("reviews").document(reviewId)
        recyclerView = binding.recycleComment
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        commentList = arrayListOf()
        comment_ids = arrayListOf()
        binding.sendCommentBtn.setOnClickListener{
            if(binding.etInputComment.text.toString().isEmpty()){
                binding.etInputComment.error = getString(R.string.must_be_filled)
                binding.etInputComment.requestFocus()
            }else{
                val newComment = Comment()
                newComment.comment_id = ""
                newComment.comment_text = binding.etInputComment.text.toString()
                newComment.commenter_id = uid
                newComment.review_id = reviewId
                val comment = hashMapOf(
                    "comment_id" to newComment.comment_id,
                    "commenter_id" to newComment.commenter_id,
                    "comment_text" to newComment.comment_text,
                    "review_id" to newComment.review_id,
                )

                FirebaseFirestore.getInstance().collection("comments").add(comment).addOnSuccessListener { docRef ->
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    Log.d("docRef", docRef.id)
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(userId)
                    userRef.update("user_comments_on_reviews", FieldValue.arrayUnion(reviewId)).addOnSuccessListener {
                        val reviewRef = db.collection("reviews").document(reviewId)
                        reviewRef.update("review_comments", FieldValue.arrayUnion(docRef.id)).addOnSuccessListener {
                            val commentRef = db.collection("comments").document(docRef.id)
                            commentRef.update("comment_id", docRef.id)
                        }
                    }
                    Toast.makeText(this , getString(R.string.create_comment_success) , Toast.LENGTH_SHORT).show()
                    binding.etInputComment.setText("")
                }
            }
        }

        db.collection("comments").whereEqualTo("review_id", reviewId).addSnapshotListener{ it, err ->
            if (err != null) {
                Log.d("errormes2", err.toString())
                Log.d("errorsize", it?.size().toString())
                return@addSnapshotListener
            }
            else{
                Log.d("errormes", it?.size().toString())
            }
            commentList.clear()
            comment_ids.clear()
            if(it?.size()!! >0){
                if (it != null) {
                    if(!it.isEmpty){
                        commentList.clear()
                        comment_ids.clear()
                        var i = 0
                        for(data in it.documents){
                            val comment:Comment? = data.toObject(Comment::class.java)
                            if (comment != null) {
                                comment.comment_id = data.id
                            }
                            if (comment != null) {
                                comment_ids.add(data.id)
                                commentList.add(comment)
                                adapterComment = CommentAdapter(commentList, comment_ids, "1")
                                recyclerView.adapter = adapterComment
                            }
                            i++
                        }
                    }
                }
            }
        }

        setContentView(binding.root)

    }

}