package edu.bluejack22_1.Jejewegs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
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
    private lateinit var searchBtn : Button
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topRated = view?.findViewById(R.id.topRatedBtn)
        allRated = view?.findViewById(R.id.allRatedBtn)
        searchBtn = view?.findViewById(R.id.btnSearch)

        recyclerView = view.findViewById(R.id.recycleView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("reviews").addSnapshotListener{ it, err ->
            if (it != null) {
                if(!it.isEmpty){
                    reviewList.clear()
                    var i = 0
                    for(data in it.documents){
//                        Log.d("data_id", data.id)
                        val review:Review? = data.toObject(Review::class.java)
                        if (review != null) {
                            review.review_id = data.id
                        }
                        if (review != null) {
                            Log.d("dataxid", data.id)
                            review_ids.add(data.id)
                            Log.d("dataxids", review_ids[i])
                            reviewList.add(review)
                            adapterReview = ReviewAdapter(reviewList, review_ids, "1")
                            recyclerView.adapter = adapterReview
                            adapterReview.onItemClicked = {
                                Log.d("it", it.reviewer_title.toString())
                                Log.d("img click","clicked")
                                val intent = Intent(context, ReviewDetailActivity::class.java)
                                Log.d("dataid", data.id)
                                intent.putExtra("dataid", it.review_id)

                                startActivity(intent)
                            }
                        }
                        i++
                    }
//                    recyclerView.adapter = ReviewAdapter(reviewList, review_ids, "1")
                }
            }
        }

        topRated.setOnClickListener(View.OnClickListener {
            topRated.visibility = View.GONE
            allRated.visibility = View.VISIBLE
            Log.d("pencet", "pencett")
            reviewList = arrayListOf()
            review_ids = arrayListOf()
            db.collection("reviews").whereEqualTo("review_rate", "5").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        var i = 0
                        for(data in it.documents){
//                        Log.d("data_id", data.id)
                            val review:Review? = data.toObject(Review::class.java)
                            if (review != null) {
                                review.review_id = data.id
                            }
                            if (review != null) {
                                Log.d("dataxid", data.id)
                                review_ids.add(data.id)
                                Log.d("dataxids", review_ids[i])
                                reviewList.add(review)
                                adapterReview = ReviewAdapter(reviewList, review_ids, "1")
                                recyclerView.adapter = adapterReview
                                adapterReview.onItemClicked = {
                                    Log.d("it", it.reviewer_title.toString())
                                    Log.d("img click","clicked")
                                    val intent = Intent(context, ReviewDetailActivity::class.java)
                                    Log.d("dataid", data.id)
                                    intent.putExtra("dataid", it.review_id)
                                    startActivity(intent)
                                }
                            }
                            i++
                        }
                    }
                }
            }
        })

        allRated.setOnClickListener(View.OnClickListener {
            allRated.visibility = View.GONE
            topRated.visibility = View.VISIBLE
            Log.d("pencet", "pencett")
            reviewList = arrayListOf()
            review_ids = arrayListOf()
            db.collection("reviews").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        var i = 0
                        for(data in it.documents){
//                        Log.d("data_id", data.id)
                            val review:Review? = data.toObject(Review::class.java)
                            if (review != null) {
                                review.review_id = data.id
                            }
                            if (review != null) {
                                Log.d("dataxid", data.id)
                                review_ids.add(data.id)
                                Log.d("dataxids", review_ids[i])
                                reviewList.add(review)
                                adapterReview = ReviewAdapter(reviewList, review_ids, "1")
                                recyclerView.adapter = adapterReview
                                adapterReview.onItemClicked = {
                                    val intent = Intent(context, ReviewDetailActivity::class.java)
                                    intent.putExtra("dataid", it.review_id)
                                    startActivity(intent)
                                }
                            }
                            i++
                        }
//                        recyclerView.adapter = ReviewAdapter(reviewList, review_ids, "1")

                    }
                }
            }
        })

        goToSearch()

    }

    private fun goToSearch(){
        searchBtn.setOnClickListener {
            val intent = Intent(this.context, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}