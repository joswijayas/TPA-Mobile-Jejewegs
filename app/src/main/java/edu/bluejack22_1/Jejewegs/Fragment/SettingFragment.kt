package edu.bluejack22_1.Jejewegs.Fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.ChangePasswordActivity
import edu.bluejack22_1.Jejewegs.LoginActivity
import edu.bluejack22_1.Jejewegs.R
import edu.bluejack22_1.Jejewegs.databinding.FragmentSettingBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var db = Firebase.firestore
    private lateinit var binding : FragmentSettingBinding
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
        binding = FragmentSettingBinding.inflate(layoutInflater)
        binding.logoutBtn.setOnClickListener{
            Firebase.auth.signOut()
            Log.d("logout", "clicked")
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googlesigninclient = context?.let { it1 -> GoogleSignIn.getClient(it1, gso) }
            if (googlesigninclient != null) {
                googlesigninclient.signOut()
            }
        }
        binding.changePasswordBtn.setOnClickListener{
            val intent = Intent(context, ChangePasswordActivity::class.java)
            startActivity(intent)

        }
        binding.deleteAccBtn.setOnClickListener{
            val user = Firebase.auth.currentUser
            val id = user?.uid
            user?.delete()?.addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(context, "Successfully delete account!", Toast.LENGTH_SHORT).show()

                    val rootRef = FirebaseFirestore.getInstance()
                    val itemsRef = rootRef.collection("reviews")
                    val query = itemsRef.whereEqualTo("reviewer_id", id)
                    query.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                itemsRef.document(document.id).delete()
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.exception)
                        }
                    }

                    db.collection("users").document(id.toString())
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    Log.d("Error delete acc", "err")
                }
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}