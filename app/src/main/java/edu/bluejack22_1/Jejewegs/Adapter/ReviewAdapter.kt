package edu.bluejack22_1.Jejewegs.Adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.R
import java.lang.reflect.Field

class ReviewAdapter(private val reviewList:ArrayList<Review>, private val review_id:ArrayList<String>, private val x : String) : RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {
    private var db = Firebase.firestore
    class ReviewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val reviewer_title : TextView = itemView.findViewById(R.id.tvReviewTitle)
        val review_brand : TextView = itemView.findViewById(R.id.tvReviewBrand)
        val review_description : TextView = itemView.findViewById(R.id.tvReviewDescription)
        val review_image : ImageView = itemView.findViewById(R.id.ivReviewImage)
        val review_rate : TextView = itemView.findViewById(R.id.tvReviewRate)
        val reviewer_id : TextView = itemView.findViewById(R.id.tvReviewerId)
        val review_likes : TextView = itemView.findViewById(R.id.tvReviewLikeCount)
        val review_comments : TextView = itemView.findViewById(R.id.tvReviewCommentCount)
        val review_wishlists : ImageView = itemView.findViewById(R.id.ivWishlist)
        val review_wishlists_colored : ImageView = itemView.findViewById(R.id.ivWishlistColored)
        var review_id : String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        Log.d("dr mana", x)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_card, parent, false)
        return ReviewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        Log.d("dr mana", x)
        val likeCtr = reviewList[position].review_likes as? List<*>
        val commentCtr = reviewList[position].review_comments as? List<*>

        holder.reviewer_title.text = reviewList[position].reviewer_title
        holder.review_brand.text = reviewList[position].review_brand
        holder.review_description.text = reviewList[position].review_description
        Glide.with(holder.review_image.context).load(reviewList[position].review_image).into(holder.review_image)
        Log.d("imagereview",reviewList[position].review_image.toString())
        holder.review_rate.text = reviewList[position].review_rate
        holder.reviewer_id.text = reviewList[position].reviewer_id
        holder.review_likes.text = (likeCtr)?.size.toString()
        holder.review_comments.text = (commentCtr)?.size.toString()
        holder.review_id = review_id[position]
        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(uid).get().addOnSuccessListener {
            if (it != null) {
                val wishlists = it.data?.get("user_wishlists") as? List<*>
                if (wishlists != null) {
                    for(data in wishlists){

                        if(data.toString().equals(holder.review_id)){
                            Log.d("review_id", data.toString())
                            Log.d("review_iddd", review_id[position])
                            holder.review_wishlists.visibility=View.GONE
                            holder.review_wishlists_colored.visibility = View.VISIBLE
                            break;
                        }
                    }
                }
            }
        }

        holder.review_wishlists.setOnClickListener{
            db.collection("users").document(uid).update("user_wishlists", FieldValue.arrayUnion(holder.review_id))
            holder.review_wishlists.visibility=View.GONE
            holder.review_wishlists_colored.visibility = View.VISIBLE
        }
        holder.review_wishlists_colored.setOnClickListener{
            db.collection("users").document(uid).update("user_wishlists", FieldValue.arrayRemove(holder.review_id))
            holder.review_wishlists.visibility=View.VISIBLE
            holder.review_wishlists_colored.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

}