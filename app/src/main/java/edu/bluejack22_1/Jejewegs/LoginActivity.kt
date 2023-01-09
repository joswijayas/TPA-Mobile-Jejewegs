package edu.bluejack22_1.Jejewegs

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.auth.User

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.databinding.ActivityLogin2Binding
import edu.bluejack22_1.Jejewegs.databinding.ActivityRegisterBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogin2Binding

    val db = Firebase.firestore
    private val TAG = "RegisterActivity"
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currUser = auth.currentUser

        if(currUser != null){
            currUser.displayName?.let { Log.d("aaa", it) }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("31969072633-tmtv8otf72i0lnsdkprihbsqb2min80j.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignin.setOnClickListener{
            signIn();
        }

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()
            if (email.isEmpty()) {
                binding.edtEmailLogin.error = getString(R.string.email_must_filled)
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailLogin.error = getString(R.string.email_format_wrong)
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edtPasswordLogin.error = getString(R.string.password_must_filled)
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this , getString(R.string.invalid_credential) , Toast.LENGTH_SHORT).show()
                    }
                }

            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun signIn(){
        val signInIntent = googleSigninClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA NAME"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e:ApiException){
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI()
                    if (user != null) {
                        Log.d("gmail", user.uid)
                        val docRef = db.collection("users").document(user.uid.toString())
                        Log.d("docRef", docRef.toString())
                        docRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                Log.d("document", document.toString())
                                Log.d("isexist", document.exists().toString())
                                if(document != null) {
                                    if (document.exists()) {
                                        Log.d("TAG", "Document already exists.")
                                    } else {
                                        Log.d("masukss", user.uid.toString())
                                        val newUserData = edu.bluejack22_1.Jejewegs.Model.User()
                                        val data = hashMapOf(
                                            "user_id" to user.uid.toString(),
                                            "user_email" to user.email.toString(),
                                            "user_fullname" to newUserData.user_fullname,
                                            "user_fav_sneaker" to newUserData.user_fav_sneaker,
                                            "user_location" to newUserData.user_location,
                                            "user_followers" to newUserData.user_followers,
                                            "user_following" to newUserData.user_followings,
                                            "user_reviews" to newUserData.user_reviews
                                        )
                                          Log.d("data_user", data.toString())
                                        db.collection("users").document(user.uid.toString())
                                            .set(data)
                                            .addOnSuccessListener { documentReference ->
                                                Toast.makeText(this, getString(R.string.success_register), Toast.LENGTH_SHORT).show()

                                                updateUI();
                                            }
                                            .addOnFailureListener { e ->
                                                Log.d("asdfgh", e.toString())
                                                Toast.makeText(this , e.toString() , Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                            } else {
                                Log.d("TAG", "Error: ", task.exception)
                            }
                        }

                    }
                    updateUI()
                }
                else{
                    Toast.makeText(baseContext, getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT).show()
                    updateUI()
                }

        }
    }

    private fun updateUI(){
        val user = auth.currentUser
        if(user != null){
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
            finish()
        }
    }


}