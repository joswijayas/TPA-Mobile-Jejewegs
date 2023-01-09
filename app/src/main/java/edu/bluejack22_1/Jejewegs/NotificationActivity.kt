package edu.bluejack22_1.Jejewegs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.NotificationAdapter
import edu.bluejack22_1.Jejewegs.databinding.ActivityNotificationBinding
import edu.bluejack22_1.Jejewegs.databinding.ActivityReviewDetailBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private var db = Firebase.firestore

    private lateinit var recyclerView : RecyclerView
    private lateinit var notificationList: ArrayList<String>
    private lateinit var adapterNotification: NotificationAdapter
    private lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        recyclerView = binding.recycleNotification
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        notificationList = arrayListOf()

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).addSnapshotListener{ it, err->
            if(it != null && it.exists()){
                notificationList.clear()

                var user_notifications = it?.get("user_notifications") as? List<String>
                if (user_notifications != null) {
                    for(x in user_notifications.reversed()){
                        Log.d("user_notif", x)
            //                    review_ids.add(it.id)
            //                    reviewList.add(review)
            //                    adapterReview = ReviewAdapter(reviewList, review_ids, "1")
            //                    recyclerView.adapter = adapterReview
            //                    val notif: Notification? = x.toObject(Review::class.java)
                        var mystring = x
                        var arr = mystring.split(" ".toRegex(), 2).toTypedArray()
                        var arr2 = mystring.split(",".toRegex(), 2).toTypedArray()
                        var firstWord = arr[0] //the
                        var theRest = arr[1]
                        var value = theRest.split(",".toRegex(), 2).toTypedArray()
                        var title = arr2[1]
                        theRest = value[0]

                        Log.d("mystring", mystring)
                        Log.d("firstWord", firstWord)
                        Log.d("mystring", mystring)
                        Log.d("mystring", mystring)
                        if(theRest.equals("notif_liked")){
                            var res = firstWord.plus(getString(R.string.notif_liked)).plus(title)
                            notificationList.add(res)
                        }else if(theRest.equals("notif_commented")){
                            var res = firstWord.plus(getString(R.string.notif_commented)).plus(title)
                            notificationList.add(res)
                        }else if(theRest.equals("notif_followed")){
                            var res = firstWord.plus(getString(R.string.notif_followed))
                            notificationList.add(res)
                        } else if(theRest.equals("notif_new_review")){
                            var res = firstWord.plus(getString(R.string.notif_new_review).plus(title))
                            notificationList.add(res)
                        }
                        adapterNotification = NotificationAdapter(notificationList)
                        recyclerView.adapter = adapterNotification

                    }
                }
            }
        }

        setContentView(binding.root)
    }
}