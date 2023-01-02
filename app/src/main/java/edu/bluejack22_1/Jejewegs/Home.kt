package edu.bluejack22_1.Jejewegs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private lateinit var choicesSpinner: Spinner
    private lateinit var choices: String
    private lateinit var ordersSpinner: Spinner
    private lateinit var ordersCategory: String
    private lateinit var ordersType: String
    private lateinit var searchBtn : ImageButton
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

        setInitValues()
        setRecyclerView()
        setReviewListener()
        setOrderListener()
        goToSearch()
    }

    private fun setInitValues(){
        choices = ""
        ordersCategory = ""
        ordersType = ""
    }

    private fun setRecyclerView(){
        recyclerView = requireView().findViewById(R.id.recycleView)
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
    }

    private fun setReviewListener(){
        choicesSpinner = requireView().findViewById(R.id.spinner_choices)
        val choicesArray = resources.getStringArray(R.array.choices)
        val choicesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, choicesArray)
        choicesSpinner.adapter = choicesAdapter
        choicesSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(choicesArray[p2].equals(getString(R.string.all_rated))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.all_rated)
                    allRatedListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.top_rated))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.top_rated)
                    topRatedListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.nike))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.nike)
                    nikeListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.adidas))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.adidas)
                    adidasListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.puma))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.puma)
                    pumaListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.skechers))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.skechers)
                    skechersListener(ordersCategory, ordersType)
                }
                else if(choicesArray[p2].equals(getString(R.string.reebok))){
//                    Log.d("test", "choice: ${choicesArray[p2]}")
                    choices = getString(R.string.reebok)
                    reebokListener(ordersCategory, ordersType)
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setOrderListener(){
        ordersSpinner = requireView().findViewById(R.id.spinner_order)
        val ordersArray = resources.getStringArray(R.array.orders)
        val ordersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, ordersArray)
        ordersSpinner.adapter = ordersAdapter
        ordersSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(ordersArray[p2].equals(getString(R.string.none))){
                    ordersCategory = getString(R.string.none)
                }
                else if(ordersArray[p2].equals(getString(R.string.like_asc))){
                    ordersCategory = getString(R.string.like)
                    ordersType = getString(R.string.asc)
                }
                else if(ordersArray[p2].equals(getString(R.string.rate_asc))){
                    ordersCategory = getString(R.string.rate)
                    ordersType = getString(R.string.asc)
                }
                else if(ordersArray[p2].equals(getString(R.string.like_desc))){
                    ordersCategory = getString(R.string.like)
                    ordersType = getString(R.string.desc)
                }
                else if(ordersArray[p2].equals(getString(R.string.rate_desc))){
                    ordersCategory = getString(R.string.rate)
                    ordersType = getString(R.string.desc)
                }
                if(choices.equals(getString(R.string.all_rated))){
                    allRatedListener(ordersCategory, ordersType)
                }
                else if(choices.equals(getString(R.string.top_rated))){
                    topRatedListener(ordersCategory, ordersType)
                }
                else if(choices.equals(getString(R.string.nike))){
                    nikeListener(ordersCategory, ordersType)
                }
                else if (choices.equals(getString(R.string.adidas))){
                    adidasListener(ordersCategory, ordersType)
                }
                else if(choices.equals(getString(R.string.puma))){
                    pumaListener(ordersCategory, ordersType)
                }
                else if(choices.equals(getString(R.string.skechers))){
                    skechersListener(ordersCategory, ordersType)
                }
                else if(choices.equals(getString(R.string.reebok))){
                    reebokListener(ordersCategory, ordersType)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun topRatedListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereGreaterThanOrEqualTo("review_rate", "4").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else {
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereGreaterThanOrEqualTo("review_rate", "4").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereGreaterThanOrEqualTo("review_rate", "4").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }
    }

    private fun allRatedListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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

                    }
                }
            }
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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

                        }
                    }
                }
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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

                        }
                    }
                }
            }
        }
    }

    private fun nikeListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereEqualTo("review_brand", "Nike").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereEqualTo("review_brand", "Nike").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereEqualTo("review_brand", "Nike").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }

    }

    private fun adidasListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereEqualTo("review_brand", "Adidas").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereEqualTo("review_brand", "Adidas").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereEqualTo("review_brand", "Adidas").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }
    }

    private fun pumaListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereEqualTo("review_brand", "Puma").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereEqualTo("review_brand", "Puma").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereEqualTo("review_brand", "Puma").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }
    }

    private fun skechersListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereEqualTo("review_brand", "Skechers").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereEqualTo("review_brand", "Skechers").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereEqualTo("review_brand", "Skechers").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }
    }

    private fun reebokListener(orderCategory: String, orderType: String){
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        if(orderCategory.equals(getString(R.string.none))){
            val ref = db.collection("reviews")
            ref.whereEqualTo("review_brand", "Reebok").addSnapshotListener{ it, err ->
                if (it != null) {
                    if(!it.isEmpty){
                        reviewList.clear()
                        review_ids.clear()
                        var i = 0
                        for(data in it.documents){
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
        }
        else{
            if(orderType == "asc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.ASCENDING)
                ref.whereEqualTo("review_brand", "Reebok").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
            else if(orderType == "desc"){
                val ref = db.collection("reviews").orderBy(orderCategory, Query.Direction.DESCENDING)
                ref.whereEqualTo("review_brand", "Reebok").addSnapshotListener{ it, err ->
                    if (it != null) {
                        if(!it.isEmpty){
                            reviewList.clear()
                            review_ids.clear()
                            var i = 0
                            for(data in it.documents){
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
            }
        }
    }

    private fun goToSearch(){
        searchBtn = requireView().findViewById(R.id.btnSearch)
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