package edu.bluejack22_1.Jejewegs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.databinding.ActivityLogin2Binding
import edu.bluejack22_1.Jejewegs.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogin2Binding

    val db = Firebase.firestore
    private val TAG = "RegisterActivity"
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()
            if (email.isEmpty()){
                binding.edtEmailLogin.error = "Email must be filled"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailLogin.error = "Email format wrong"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.edtPasswordLogin.error = "Password must be filled"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this , "INVALID CREDENTIAL" , Toast.LENGTH_SHORT).show()
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