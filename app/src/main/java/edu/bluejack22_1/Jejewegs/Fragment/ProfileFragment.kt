package edu.bluejack22_1.Jejewegs.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchUser()
        binding = FragmentProfileBinding.inflate(layoutInflater)

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

                binding.tvFullName.text = fullName
                binding.tvEmailProfile.text = email
                binding.tvLocation.text = location
                binding.tvFavoriteSneaker.text = favoriteSneaker
            }
        }.addOnFailureListener{

        }
    }

}