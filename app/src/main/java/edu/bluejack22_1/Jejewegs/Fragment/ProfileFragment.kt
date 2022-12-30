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
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack22_1.Jejewegs.LoginActivity
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var user : User
    private lateinit var newUserData : User
    private lateinit var pathImage : Uri
    private lateinit var bitmap : Bitmap

    private var db = Firebase.firestore

    private var isEdit = true
    private var imgUri = ""

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

    private fun fetchUser() {
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//        val ref = db.collection("users").document(userId)
        user = User()
        user.user_id = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(user.user_id!!)

        ref.get().addOnSuccessListener {
            if (it != null) {
                user.user_fullname = it.data?.get("user_fullname")?.toString()
                user.user_email = it.data?.get("user_email")?.toString()
                user.user_location = it.data?.get("user_location")?.toString()
                user.user_fav_sneaker = it.data?.get("user_fav_sneaker")?.toString()
                user.user_image = it.data?.get("user_image")?.toString()
                user.user_followers = it.data?.get("user_followers") as? List<String>
                user.user_followings = it.data?.get("user_followings") as? List<String>
                user.user_reviews = it.data?.get("user_reviews") as? List<String>
                user.user_wishlists = it.data?.get("user_wishlists") as? List<String>
                user.user_liked_review = it.data?.get("review_likes") as? List<String>

                binding.followersCount.text = (user.user_followers)?.size.toString()
                binding.followingsCount.text = (user.user_followings)?.size.toString()
                binding.reviewsCount.text = (user.user_reviews)?.size.toString()

                binding.etFullName.setText(user.user_fullname.toString())
                binding.etEmailProfile.setText(user.user_email.toString())
                binding.etLocation.setText(user.user_location.toString())
                binding.etFavoriteSneaker.setText(user.user_fav_sneaker.toString())

                Glide.with(binding.profileImage.context).load(user.user_image).into(binding.profileImage)
            }
        }.addOnFailureListener{

        }
    }

    private fun editBtnListener() {
        binding.editBtn.setOnClickListener {
            newUserData = User()
            if (isEdit) {
                binding.editBtn.text = getString(R.string.save);
                binding.etFullName.isEnabled = true
//                binding.etEmailProfile.isEnabled = true
                binding.etLocation.isEnabled = true
                binding.etFavoriteSneaker.isEnabled = true
                addChangeImageListener()
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

                    newUserData.user_email = binding.etEmailProfile.text.toString()
                    newUserData.user_fullname = binding.etFullName.text.toString()
                    newUserData.user_location = binding.etLocation.text.toString()
                    newUserData.user_fav_sneaker = binding.etFavoriteSneaker.text.toString()

                    newUserData.user_id = FirebaseAuth.getInstance().currentUser!!.uid
                    val ref = db.collection("users").document(newUserData.user_id!!)

                    ref.get().addOnSuccessListener {
                        if (it != null) {

                            val followers = it.data?.get("user_followers") as? List<String>
                            val followings = it.data?.get("user_followings") as? List<String>
                            val reviews = it.data?.get("user_reviews") as? List<String>
                            val wishlists = it.data?.get("user_wishlists") as? List<String>
                            val liked_review = it.data?.get("review_likes") as? List<String>
                            newUserData.user_followers = followers
                            newUserData.user_followings = followings
                            newUserData.user_reviews = reviews
                            newUserData.user_wishlists = wishlists
                            newUserData.user_liked_review = liked_review
//                            Log.d("test", "profile updated")
//                            Log.d("test", "newuser image: ${newUserData.user_image}")
//                            newUserData.user_image = imgUri
                        }

                        ref.set(newUserData).addOnSuccessListener {
                            isEdit = true

                            fetchUser()
                            removeChangeImageListener()
                        }
                    }

                }


            }
        }

    }

    private fun addChangeImageListener(){
        binding.profileImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            chooseImage.launch(intent)
        }
    }

    private fun removeChangeImageListener(){
        binding.profileImage.setOnClickListener(null)
    }

    private var chooseImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK && it.data != null){

            pathImage = it.data!!.data!!
            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, pathImage)
            var ref = FirebaseStorage.getInstance().getReference("images/${newUserData.user_id}")
//            Log.d("test", "bitmap: $bitmap")
            ref.putFile(pathImage).addOnSuccessListener{
                it.storage.downloadUrl.addOnSuccessListener {
//                    Log.d("test", "uri: $it")
                    newUserData.user_image = it.toString()
//                    Log.d("test", "user image: ${newUserData.user_image}")
                    binding.profileImage.setImageBitmap(bitmap)
                }
            }
        }
    }

}