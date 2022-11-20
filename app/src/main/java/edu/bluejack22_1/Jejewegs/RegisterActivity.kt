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
                binding.edtEmailRegister.error = "Email must be filled"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailRegister.error = "Email format wrong"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPasswordRegister.error = "Password must be filled"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }
            if(password.length < 5 || password.length> 20){
                binding.edtPasswordRegister.error = "Password must between 5 - 20 characters"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){
                    task->
                    if(task.isSuccessful){

                        val id = auth.currentUser?.uid
                        Log.d("IDdd", id.toString())
                        val data = hashMapOf(
                            "user_id" to id,
                            "user_email" to email,
                            "user_fullname" to "",
                            "user_fav_sneaker" to "",
                            "user_location" to ""
                        )
                        Log.d("data_user", data.toString())
                        db.collection("users").document(id.toString())
                            .set(data)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
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