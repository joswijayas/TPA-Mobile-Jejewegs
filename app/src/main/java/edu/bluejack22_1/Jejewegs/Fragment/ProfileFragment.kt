package edu.bluejack22_1.Jejewegs.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack22_1.Jejewegs.Adapter.ReviewAdapter
import edu.bluejack22_1.Jejewegs.LoginActivity
import edu.bluejack22_1.Jejewegs.MainActivity
import edu.bluejack22_1.Jejewegs.Model.Review
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.NotificationActivity
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var review_ids: ArrayList<String>
    private lateinit var imagePath: Uri
    private lateinit var bitmap: Bitmap
    private var db = Firebase.firestore
    private var storage = FirebaseStorage.getInstance()

    private var isEdit = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        fetchUser()
        editBtnListener()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycleReviewList)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager=layoutManager
        reviewList = arrayListOf()
        review_ids = arrayListOf()
        db = FirebaseFirestore.getInstance()
//

        binding.notificationIV.setOnClickListener{
            val intent = Intent(context, NotificationActivity::class.java)
            startActivity(intent)
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val docRef = db.collection("users").document(uid)
        docRef.addSnapshotListener{ it, err ->
            if (err != null) {
                return@addSnapshotListener
            }
            reviewList.clear()
            review_ids.clear()
            val reviewlists = it?.data?.get("user_reviews") as? List<*>
            if (reviewlists != null) {
                Log.d("size", reviewlists.size.toString())
                if(reviewlists?.size == 0){
                    reviewList.clear()
                    review_ids.clear()
                    recyclerView.adapter = ReviewAdapter(reviewList, review_ids, "2")
                }
            }
            if (reviewlists != null) {
                for (x in reviewlists){
                    val docRef2 = db.collection("reviews").document(x.toString())
                    docRef2.get().addOnSuccessListener {
                            doc ->
                        if(doc != null){
                            val review:Review? = doc.toObject(Review::class.java)
                            if (review != null) {
                                Log.d("mana", doc.id)
                                recyclerView.adapter = ReviewAdapter(reviewList, review_ids, "2")
                                review_ids.add(doc.id)
                                reviewList.add(review)
                            }
                        }
                    }
                }
            }
            else{
                Log.d("mskk", "msk sini")
                reviewList.clear()
                review_ids.clear()
                recyclerView.adapter = ReviewAdapter(reviewList, review_ids, "2")
            }

        }
    }

    private fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null) {
                val fullName = it.data?.get("user_fullname")?.toString()
                val email = it.data?.get("user_email")?.toString()
                val location = it.data?.get("user_location")?.toString()
                val favoriteSneaker = it.data?.get("user_fav_sneaker")?.toString()
                val image = it.data?.get("user_image")?.toString()
                val followers = it.data?.get("user_followers") as? List<*>
                val followings = it.data?.get("user_followings") as? List<*>
                val reviews = it.data?.get("user_reviews") as? List<*>

                if(image != null && !image.isEmpty()){
                    Glide.with(binding.profileImage.context).load(image).into(binding.profileImage)
                }

                if(followers == null){
                    binding.followersCount.text = 0.toString()
                }else{
                    binding.followersCount.text = (followers)?.size.toString()
                }
                if(followings == null){
                    binding.followingsCount.text = 0.toString()
                }else{
                    binding.followingsCount.text = (followings)?.size.toString()
                }
                if(reviews == null){
                    binding.reviewsCount.text = 0.toString()
                }else{
                    binding.reviewsCount.text = (reviews)?.size.toString()
                }


                binding.etFullName.setText(fullName.toString())
                binding.etEmailProfile.setText(email.toString())
                binding.etLocation.setText(location.toString())
                binding.etFavoriteSneaker.setText(favoriteSneaker.toString())
            }
        }.addOnFailureListener{

        }
    }

    private fun editBtnListener() {
        binding.editBtn.setOnClickListener {
            if (isEdit) {
                binding.editBtn.text = getString(R.string.save);
                binding.etFullName.isEnabled = true
//                binding.etEmailProfile.isEnabled = true
                binding.etLocation.isEnabled = true
                binding.etFavoriteSneaker.isEnabled = true
                binding.profileImage.setOnClickListener { addChangeImageListener() }
                isEdit = false;
            } else {
                if(binding.etFullName.text.toString().isEmpty()){
                    binding.etFullName.error = getString(R.string.fullname_must_filled)
                    binding.etFullName.requestFocus()
                }
                else if(binding.etLocation.text.toString().isEmpty()){
                    binding.etLocation.error = getString(R.string.location_must_filled)
                    binding.etLocation.requestFocus()
                }
                else if(binding.etFavoriteSneaker.text.toString().isEmpty()){
                    binding.etFavoriteSneaker.error = getString(R.string.favoritesneaker_must_filled)
                    binding.etFavoriteSneaker.requestFocus()
                }
                else{
                    binding.editBtn.text = getString(R.string.edit)
                    binding.etFullName.isEnabled = false
                    binding.etLocation.isEnabled = false
                    binding.etFavoriteSneaker.isEnabled = false

                    val newUserData = User()
                    newUserData.user_email = binding.etEmailProfile.text.toString()
                    newUserData.user_fullname = binding.etFullName.text.toString()
                    newUserData.user_location = binding.etLocation.text.toString()
                    newUserData.user_fav_sneaker = binding.etFavoriteSneaker.text.toString()

                    newUserData.user_id = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = db.collection("users").document(newUserData.user_id!!)

                    ref.get().addOnSuccessListener {
                        if (it != null) {
                            val image = it.data?.get("user_image")?.toString()
                            val followers = it.data?.get("user_followers") as? List<String>
                            val followings = it.data?.get("user_followings") as? List<String>
                            val reviews = it.data?.get("user_reviews") as? List<String>

                            newUserData.user_image = image
                            newUserData.user_followers = followers
                            newUserData.user_followings = followings
                            newUserData.user_reviews = reviews

                        }

                        ref.set(newUserData).addOnSuccessListener {
                            isEdit = true
                            binding.profileImage.setOnClickListener(null)
//                            fetchUser()
                        }
                    }

                }


            }
        }

    }

    private fun addChangeImageListener(){
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        selectImage.launch(intent)
    }

    private var selectImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK && it.data != null) {
            imagePath = it.data!!.data!!
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imagePath)
            binding.profileImage.setImageBitmap(bitmap)
//            binding.profileImage.visibility = View.INVISIBLE
//            binding.previewProfileImage.visibility = View.VISIBLE
//            binding.previewProfileImage.setImageBitmap(bitmap)
            val storagePath = "profilePicture/$userId"
            val ref = storage.getReference(storagePath)
            ref.putFile(imagePath).addOnSuccessListener {
                storage.reference.child(storagePath).downloadUrl.addOnSuccessListener {
                    db.collection("users").document(userId).update("user_image", it.toString()).addOnSuccessListener { fetchUser() }
                    Log.d("test", "success uri")
                }.addOnFailureListener{
                    Log.d("test", "error uri")
                }
            }
//            fetchUser()
//            binding.profileImage.visibility = View.VISIBLE
//            binding.previewProfileImage.visibility = View.INVISIBLE
        }
    }

}