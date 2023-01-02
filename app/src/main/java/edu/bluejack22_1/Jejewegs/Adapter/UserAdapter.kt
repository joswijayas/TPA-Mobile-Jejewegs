package edu.bluejack22_1.Jejewegs.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.R

class UserAdapter(private val userLists:ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserHolder>(){
    private var db = Firebase.firestore

    class UserHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val user_image : ImageView = itemView.findViewById(R.id.profile_image)
        val user_fullName : TextView = itemView.findViewById(R.id.tvFullName)
        val user_id : TextView = itemView.findViewById(R.id.tvReviewerId)
        val btnFollow : Button = itemView.findViewById(R.id.btnFollow)
        val btnUnfollow : Button = itemView.findViewById(R.id.btnUnfollow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_card, parent, false)
        return UserHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        if(userLists[position].user_fullname.isNullOrEmpty()){
            holder.user_fullName.text = userLists[position].user_email
        }
        else{
            holder.user_fullName.text = userLists[position].user_fullname
        }
        holder.user_id.text = userLists[position].user_id
        var image = userLists[position].user_image
        if(image != null && !image.isEmpty()){
            Glide.with(holder.user_image.context).load(image).into(holder.user_image)
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(uid).get().addOnSuccessListener {
            if(it != null){
                val followings = it.data?.get("user_followings") as? List<*>
                if(followings != null){
                    for(x in followings){
                        if(x.toString().equals(holder.user_id)){
                            holder.btnFollow.visibility = View.GONE
                            holder.btnUnfollow.visibility = View.VISIBLE
                            break;
                        }
                    }
                }
            }
        }
        holder.btnFollow.setOnClickListener {
//            val target = holder.user_id.toString()
            val target = userLists[position].user_id!!
            Log.d("test", "uid: $uid")
            Log.d("test", "target: $target")
            db.collection("users").document(uid).update("user_followings", FieldValue.arrayUnion(target))
            db.collection("users").document(target).update("user_followers", FieldValue.arrayUnion(uid))
            holder.btnFollow.visibility = View.GONE
            holder.btnUnfollow.visibility = View.VISIBLE
        }

        holder.btnUnfollow.setOnClickListener {
            val target = userLists[position].user_id!!
            Log.d("test", "uid: $uid")
            Log.d("test", "target: $target")
            db.collection("users").document(uid).update("user_followings", FieldValue.arrayRemove(target))
            db.collection("users").document(target).update("user_followers", FieldValue.arrayRemove(uid))
            holder.btnUnfollow.visibility = View.GONE
            holder.btnFollow.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return userLists.size
    }

}