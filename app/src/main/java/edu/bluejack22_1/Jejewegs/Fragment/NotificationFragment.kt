package edu.bluejack22_1.Jejewegs.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.NotificationAdapter
import edu.bluejack22_1.Jejewegs.Model.Notification
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentNotificationBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var db = Firebase.firestore

    private lateinit var recyclerView : RecyclerView
    private lateinit var notificationList: ArrayList<String>
    private lateinit var adapterNotification: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }
    private lateinit var uid : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycleNotification)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        notificationList = arrayListOf()

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).addSnapshotListener{ it, err->
            if(it != null && it.exists()){
                notificationList.clear()

                var user_notifications = it.get("user_notifications") as List<String>
                for(x in user_notifications){
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
                    var title = arr2[1]
                    if(theRest.equals("notif_liked")){
                        var res = firstWord.plus(" has liked your review ").plus(title)
                        notificationList.add(res)
                    }else if(theRest.equals("notif_commented")){
                        var res = firstWord.plus(" has commented your review ").plus(title)
                        notificationList.add(res)
                    }
                    adapterNotification = NotificationAdapter(notificationList)
                    recyclerView.adapter = adapterNotification

                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}