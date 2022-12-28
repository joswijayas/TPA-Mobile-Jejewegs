package edu.bluejack22_1.Jejewegs.Fragment

import android.os.Bundle
import android.util.Log
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
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WishlistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WishlistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var wishLists: ArrayList<Review>
    private lateinit var review_ids: ArrayList<String>
    private var db = Firebase.firestore

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
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycleWishlist)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        wishLists = arrayListOf()
        review_ids = arrayListOf()
        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = db.collection("users").document(uid)
        docRef.addSnapshotListener{ it, err ->
            if (it != null && it.exists()) {
//                if(!it.isEmpty){
//                    reviewList.clear()
//                    for(data in it.documents){
////                        Log.d("data_id", data.id)
//                        val review:Review? = data.toObject(Review::class.java)
//                        if (review != null) {
//                            review_ids.add(data.id)
//                            reviewList.add(review)
//                        }
//                    }
//                    recyclerView.adapter = ReviewAdapter(reviewList, review_ids)
//                }
                val wishlists = it.data?.get("user_wishlists") as? List<*>
                if (wishlists != null) {
                    for (x in wishlists){
                        val docRef2 = db.collection("reviews").document(x.toString())
                        docRef2.get().addOnSuccessListener {
                            doc ->
                                if(doc != null){
                                    val review:Review? = doc.toObject(Review::class.java)
                                    if (review != null) {
                                        Log.d("mana", doc.id)
                                        review_ids.add(doc.id)
                                        wishLists.add(review)
                                    }
                                }else{

                                }
                        }
                    }
                    Log.d("skkss", "sdsd")
                    recyclerView.adapter = ReviewAdapter(wishLists, review_ids, "2")
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
         * @return A new instance of fragment WishlistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishlistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}