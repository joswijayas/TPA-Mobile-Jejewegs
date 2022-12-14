package edu.bluejack22_1.Jejewegs.Fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import edu.bluejack22_1.Jejewegs.LoginActivity
import edu.bluejack22_1.Jejewegs.MainActivity
import edu.bluejack22_1.Jejewegs.databinding.FragmentCreateReviewBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateReview.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateReview : Fragment() {
    // TODO: Rename and change types of parameters



    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentCreateReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var createReviewButton : Button
    private lateinit var imageView : ImageView
    private lateinit var addImageButton : Button
    private lateinit var pathImage : Uri
    private lateinit var bitmap: Bitmap
    private lateinit var removeButton: Button
    private lateinit var userId : String
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("dddddd", "aaaaaa")
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
        _binding = FragmentCreateReviewBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val currUser = auth.currentUser
        createReviewButton = binding.btnCreateReview
        addImageButton = binding.addImage
        removeButton = binding.removeImage
        imageView = binding.imagePreview
        userId = currUser?.uid.toString()

        removeButton.visibility = View.GONE
        imageView.visibility = View.GONE
        Log.d("dddddd", "aaaaaa")

        createReviewButton.setOnClickListener{
            Log.d("dddddd", "aaaaaa")
            val reviewBrand = binding.spinnerBrand.selectedItem.toString()
            val reviewTitle = binding.edtReviewTitle.text.toString()
            val reviewDescripion = binding.edtReviewDescription.text.toString()
            val reviewRate = binding.edtReviewRate.text.toString()
            val intRate = binding.edtReviewRate.text.toString()

            Log.d("intRate", intRate.toString())
            if(reviewTitle.isEmpty() || reviewDescripion.isEmpty()){
                Log.d("validasi create review", "Masuk")

                Toast.makeText(context , "All field must be filled" , Toast.LENGTH_SHORT).show()
            }
            else if(!intRate.equals("1") && !intRate.equals("2") && !intRate.equals("3") && !intRate.equals("4") && !intRate.equals("5")){
                Log.d("validasi create review", "Masuk222")
                Toast.makeText(context , "Rate must between 1 and 5" , Toast.LENGTH_SHORT).show()
            }
            else if(removeButton.visibility == View.GONE){
                Toast.makeText(context , "Must add picture" , Toast.LENGTH_SHORT).show()
            }
            else{
                context?.let {
                    uploadImage(reviewBrand, reviewTitle, reviewDescripion, reviewRate)
                }
            }
        }

        addImageButton.setOnClickListener{

            imageView.visibility = View.GONE
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            chooseImage.launch(intent)
        }

        removeButton.setOnClickListener{
            removeImg()
        }


        return binding.root
    }

    private fun uploadImage(
        reviewBrand: String,
        reviewTitle: String,
        reviewDescripion: String,
        reviewRate: String
    ) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading Image...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val fileName = formatter.format(Date())
        var storageRef = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageRef.putFile(pathImage).addOnSuccessListener {
            taskSnapshot->
            var imgUri = ""
            if(progressDialog.isShowing) progressDialog.dismiss()
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                uriImg->
                imgUri = uriImg.toString()
                Log.d("imageeee", imgUri)

                val emptyArr : List<Int> = emptyList()
                val review = hashMapOf(
                    "reviewer_id" to userId,
                    "reviewer_title" to reviewTitle,
                    "review_brand" to reviewBrand,
                    "review_image" to imgUri,
                    "review_description" to reviewDescripion,
                    "review_rate" to reviewRate,
                    "review_likes" to emptyArr,
                    "review_comments" to emptyArr

                )

                FirebaseFirestore.getInstance().collection("reviews").add(review).addOnSuccessListener {
                    Toast.makeText(context , "Successful create review" , Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }

            }
            Log.d("imagwww", imgUri)

        }.addOnFailureListener{
            Log.d("cant create post", "cant create post")
            if(progressDialog.isShowing) progressDialog.dismiss()
        }

    }


    private var chooseImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ res->
        if(res.resultCode == Activity.RESULT_OK && res.data != null){
            pathImage = res.data!!.data!!
            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, pathImage)
            imageView.setImageBitmap(bitmap)
            removeButton.visibility = View.VISIBLE
            binding.imagePreview.visibility =View.VISIBLE
            binding.imagePreview.setImageBitmap(bitmap)
            Log.d("imageview", pathImage.toString())
        }
    }

    private fun removeImg(){
        binding.imagePreview.setImageBitmap(null)
        removeButton.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateReview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateReview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}