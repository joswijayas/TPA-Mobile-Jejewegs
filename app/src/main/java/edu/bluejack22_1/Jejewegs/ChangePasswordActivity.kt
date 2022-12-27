package edu.bluejack22_1.Jejewegs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.Jejewegs.Fragment.CreateReview
import edu.bluejack22_1.Jejewegs.Fragment.ProfileSettingFragment
import edu.bluejack22_1.Jejewegs.databinding.ActivityChangePasswordBinding
import edu.bluejack22_1.Jejewegs.databinding.ActivityMainBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        binding.submitVerifAcc.setOnClickListener{
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            val pass = binding.etVerifAcc.text.toString()
            if(pass.isEmpty()){
                binding.etVerifAcc.error = getString(R.string.password_must_filled)
                binding.etVerifAcc.requestFocus()
                return@setOnClickListener
            }

            user.let{
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener{
                    task->
                        when{
                            task.isSuccessful -> {
                                binding.accountVerif.visibility = View.GONE
                                binding.changePassword.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException->{
                                binding.etVerifAcc.error = getString(R.string.invalid_credential)
                                binding.etVerifAcc.requestFocus()
                            }
                            else ->{
                                Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        binding.submitChangePassword.setOnClickListener newChangePassword@{
                            val newPass = binding.newPassword.text.toString()
                            val confPass = binding.confirmNewPassword.text.toString()

                            if(newPass.isEmpty()){
                                binding.newPassword.error = getString(R.string.password_must_filled)
                                binding.newPassword.requestFocus()
                                return@newChangePassword
                            }
                            if(confPass.isEmpty()){
                                binding.confirmNewPassword.error = getString(R.string.password_must_filled)
                                binding.confirmNewPassword.requestFocus()
                                return@newChangePassword
                            }
                            if(newPass.length < 5 || newPass.length> 20 ){
                                binding.newPassword.error = getString(R.string.password_minimum_length)
                                binding.newPassword.requestFocus()
                                return@newChangePassword
                            }
                            if(confPass.length < 5 || confPass.length> 20 ){
                                binding.confirmNewPassword.error = getString(R.string.password_minimum_length)
                                binding.confirmNewPassword.requestFocus()
                                return@newChangePassword
                            }
                            if(newPass != confPass){
                                binding.confirmNewPassword.error = getString(R.string.pass_conf_pass)
                                binding.confirmNewPassword.requestFocus()
                                return@newChangePassword
                            }
                            user?.let {
                                user.updatePassword(newPass).addOnCompleteListener{
                                    if(it.isSuccessful){
                                        Toast.makeText(this, getString(R.string.success_change_pass), Toast.LENGTH_SHORT).show()
//                                        Firebase.auth.signOut()
//                                        Log.d("logout", "clicked")
//                                        val intent = Intent(this, LoginActivity::class.java)
//                                        startActivity(intent)
//                                        activity?.finish()
                                            successLogout()

                                    }else{
                                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                }

            }

        }

        setContentView(binding.root)
    }

    private fun successLogout() {
        Firebase.auth.signOut()
        Log.d("logout", "clicked")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

        Toast.makeText(this, getString(R.string.login_again), Toast.LENGTH_SHORT).show()
    }
}