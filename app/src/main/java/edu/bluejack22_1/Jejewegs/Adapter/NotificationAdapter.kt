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
import edu.bluejack22_1.Jejewegs.Model.NotificationApp
import edu.bluejack22_1.Jejewegs.R
import java.lang.reflect.Field

class NotificationAdapter(private val notificationList:ArrayList<String>) : RecyclerView.Adapter<NotificationAdapter.NotificationHolder>()  {
    class NotificationHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val notificationText : TextView = itemView.findViewById(R.id.tvNotifText)
    }
    private var db = Firebase.firestore
    private lateinit var tempCheckUser : String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_card, parent, false)
        return NotificationHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.notificationText.text = notificationList[position]
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}