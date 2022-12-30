package edu.bluejack22_1.Jejewegs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Model.User
import edu.bluejack22_1.Jejewegs.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private val TAG = "RegisterActivity"
    lateinit var binding : ActivityRegisterBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener{
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            if (email.isEmpty()){
                binding.edtEmailRegister.error = getString(R.string.email_must_filled)
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailRegister.error = getString(R.string.email_format_wrong)
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPasswordRegister.error = getString(R.string.password_must_filled)
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }
            if(password.length < 5 || password.length> 20){
                binding.edtPasswordRegister.error = getString(R.string.password_minimum_length)
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){
                    task->
                    if(task.isSuccessful){
                        val newUserData = User()
                        newUserData.user_id = auth.currentUser?.uid
                        newUserData.user_email = email
                        Log.d("IDdd", newUserData.user_id.toString())
                        val data = hashMapOf(
                            "user_id" to newUserData.user_id,
                            "user_email" to newUserData.user_email,
                            "user_fullname" to newUserData.user_fullname,
                            "user_fav_sneaker" to newUserData.user_fav_sneaker,
                            "user_location" to newUserData.user_location,
                            "user_image" to newUserData.user_image,
                            "user_followers" to newUserData.user_followers,
                            "user_following" to newUserData.user_followings,
                            "user_reviews" to newUserData.user_reviews,
                            "user_wishlists" to newUserData.user_wishlists
                        )
                        Log.d("data_user", data.toString())
                        db.collection("users").document(newUserData.user_id.toString())
                            .set(data)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, getString(R.string.register_sucess), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.d("asdfgh", e.toString())
                                Toast.makeText(this , e.toString() , Toast.LENGTH_SHORT).show()
                            }
                    }else{

                        Toast.makeText(baseContext, "${task.exception?.message}", Toast.LENGTH_SHORT).show()

                    }
                }

            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

        }
    }
}