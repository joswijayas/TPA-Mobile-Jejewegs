package edu.bluejack22_1.Jejewegs.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.R

class ReviewAdapter(private val reviewList:ArrayList<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {
    class ReviewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val reviewer_title : TextView = itemView.findViewById(R.id.tvReviewTitle)
        val review_brand : TextView = itemView.findViewById(R.id.tvReviewBrand)
        val review_description : TextView = itemView.findViewById(R.id.tvReviewDescription)
        val review_image : ImageView = itemView.findViewById(R.id.ivReviewImage)
        val review_rate : TextView = itemView.findViewById(R.id.tvReviewRate)
        val reviewer_id : TextView = itemView.findViewById(R.id.tvReviewerId)
        val review_likes : TextView = itemView.findViewById(R.id.tvReviewLikeCount)
        val review_comments : TextView = itemView.findViewById(R.id.tvReviewCommentCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_card, parent, false)
        return ReviewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        holder.reviewer_title.text = reviewList[position].reviewer_title
        holder.review_brand.text = reviewList[position].review_brand
        holder.review_description.text = reviewList[position].review_description
        holder.review_image.setImageURI(Uri.parse(reviewList[position].review_image))
        holder.review_rate.text = reviewList[position].review_rate
        reviewList[position].review_rate
        holder.reviewer_id.text = reviewList[position].reviewer_id
        reviewList[position].reviewer_id
        holder.review_likes.text = reviewList[position].reviewer_id
        reviewList[position].review_likes
        holder.review_comments.text = reviewList[position].review_rate
        reviewList[position].review_comments

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

}