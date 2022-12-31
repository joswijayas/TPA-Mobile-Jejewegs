package edu.bluejack22_1.Jejewegs.Adapter

import android.media.Image
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Model.Comment
import edu.bluejack22_1.Jejewegs.R
import java.lang.reflect.Field

class CommentAdapter(private val commentList:ArrayList<Comment>, private val comment_id:ArrayList<String>, private val x : String) : RecyclerView.Adapter<CommentAdapter.CommentHolder>()  {
    class CommentHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var commentId = ""
        val commenterEmail : TextView = itemView.findViewById(R.id.tvCommenterEmail)
        val commentText : EditText = itemView.findViewById(R.id.etCommentText)
        val editButton : Button = itemView.findViewById(R.id.editCommentButton)
        val saveButton : Button = itemView.findViewById(R.id.saveCommentButton)
        val removeCommentIV : ImageView = itemView.findViewById(R.id.ivDelete)
    }
    private var db = Firebase.firestore
    private lateinit var tempCheckUser : String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        Log.d("dr mana", x)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_card, parent, false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.commentId = comment_id[position]
        holder.commentText.setText(commentList[position].comment_text.toString())
        var commenter_id = commentList[position].commenter_id.toString()

        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(commenter_id).get().addOnSuccessListener {
            if (it != null) {
                val commenterEmail = it.data?.get("user_email").toString()
                holder.commenterEmail.text = commenterEmail
            }
        }

        val revRef = db.collection("reviews").document(commentList[position].review_id.toString())
        revRef.get().addOnSuccessListener {
            it->
            if(it != null && it.exists()){
                tempCheckUser = it.get("reviewer_id").toString()
                Log.d("tempcheck", tempCheckUser)
                if(tempCheckUser == uid) {
                    Log.d("tempcheck2", tempCheckUser)
                    holder.removeCommentIV.visibility = View.VISIBLE
                }
            }else{

            }
        }

        Log.d("uid", uid)
        if(commentList[position].commenter_id.toString() == uid) {
                holder.editButton.visibility = View.VISIBLE
                holder.removeCommentIV.visibility = View.VISIBLE
        }



        holder.editButton.setOnClickListener{
            holder.commentText.isEnabled = true
            holder.editButton.visibility = View.GONE
            holder.saveButton.visibility = View.VISIBLE
        }

        holder.saveButton.setOnClickListener{
            if(holder.commentText.text.isEmpty()){
                holder.commentText.error = holder.removeCommentIV.context.getString(R.string.must_be_filled)
                holder.commentText.requestFocus()
            }
            else{
                db.collection("comments").document(holder.commentId.toString()).update(
                    "comment_text", holder.commentText.text.toString()
                ).addOnSuccessListener {
                    holder.commentText.isEnabled = false
                    holder.saveButton.visibility = View.GONE
                    holder.editButton.visibility = View.VISIBLE
                }
            }
        }
        var comment_has_review_id = commentList[position].review_id.toString()
        var comment_has_commenter_id = commentList[position].commenter_id.toString()
        holder.removeCommentIV.setOnClickListener{
            Log.d("pencet", "pencett")
            Log.d("idxxx", comment_id[position])
            db.collection("reviews").document(commentList[position].review_id.toString()).update(
                "review_comments", FieldValue.arrayRemove(comment_id[position])
            ).addOnSuccessListener {
                db.collection("comments").document(comment_id[position]).delete().addOnSuccessListener {
                    notifyDataSetChanged()
                    db.collection("comments").whereEqualTo("review_id", comment_has_review_id).whereEqualTo("commenter_id", comment_has_commenter_id).get().addOnSuccessListener {
                        it->
                        if(it.size() == 0){
                            db.collection("users").document(comment_has_commenter_id).update(
                                "user_comments_on_reviews", FieldValue.arrayRemove(comment_has_review_id)
                            )
                        }
                    }
                }
            }

        }



    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}