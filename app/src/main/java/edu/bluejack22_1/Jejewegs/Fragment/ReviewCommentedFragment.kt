package edu.bluejack22_1.Jejewegs.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewCommentedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewCommentedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var review_ids: ArrayList<String>
    private lateinit var adapterReview: ReviewAdapter
    private var db = Firebase.firestore
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRated : Button
    private lateinit var allRated : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    var containers : ViewGroup? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_commented, container, false)
    }
    private lateinit var uid : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycleViewCommented)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).addSnapshotListener{ it, err->
            if(it != null && it.exists()){
                reviewList.clear()
                review_ids.clear()
                var liked_reviews = it?.get("user_comments_on_reviews") as? List<String>
                if (liked_reviews != null) {
                    for(x in liked_reviews){
                        db.collection("reviews").document(x).addSnapshotListener{ it, err->
                            if(it != null && it.exists()){
                                val review: Review? = it.toObject(Review::class.java)
                                if (review != null) {
                                    review.review_id = it.id
                                    Log.d("dataxid", it.id)
                                    review_ids.add(it.id)
                                    reviewList.add(review)
                                    adapterReview = ReviewAdapter(reviewList, review_ids, "1")
                                    recyclerView.adapter = adapterReview

                                }
                            }
                        }
                    }
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
         * @return A new instance of fragment ReviewCommentedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewCommentedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}