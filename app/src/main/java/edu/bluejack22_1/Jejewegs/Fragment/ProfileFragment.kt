package edu.bluejack22_1.Jejewegs.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.LoginActivity
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    private var db = Firebase.firestore

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

    private fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(userId)

        ref.get().addOnSuccessListener {
            if (it != null) {
                val fullName = it.data?.get("user_fullname")?.toString()
                val email = it.data?.get("user_email")?.toString()
                val location = it.data?.get("user_location")?.toString()
                val favoriteSneaker = it.data?.get("user_fav_sneaker")?.toString()
                val followers = it.data?.get("user_followers") as? List<*>
                val followings = it.data?.get("user_following") as? List<*>
                val reviews = it.data?.get("user_reviews") as? List<*>

                binding.followersCount.text = (followers)?.size.toString()
                binding.followingsCount.text = (followings)?.size.toString()
                binding.reviewsCount.text = (reviews)?.size.toString()

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
                            val followers = it.data?.get("user_followers") as? List<String>
                            val followings = it.data?.get("user_followings") as? List<String>
                            val reviews = it.data?.get("user_reviews") as? List<String>

                            newUserData.user_followers = followers
                            newUserData.user_followings = followings
                            newUserData.user_reviews = reviews

                        }

                        ref.set(newUserData).addOnSuccessListener {
                            isEdit = true
                            fetchUser()
                        }
                    }

                }


            }
        }

    }

}