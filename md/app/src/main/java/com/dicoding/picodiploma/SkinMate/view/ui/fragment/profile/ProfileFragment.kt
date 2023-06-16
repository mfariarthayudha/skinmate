package com.dicoding.picodiploma.SkinMate.view.ui.fragment.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.SkinMate.databinding.FragmentProfileBinding
import com.dicoding.picodiploma.SkinMate.model.UserPreference
import com.dicoding.picodiploma.SkinMate.uriToFile
import com.dicoding.picodiploma.SkinMate.view.ViewModelFactory
import com.dicoding.picodiploma.SkinMate.view.ui.activity.login.LoginActivity
import com.dicoding.picodiploma.SkinMate.view.ui.activity.main.MainActivity
import com.dicoding.picodiploma.SkinMate.view.ui.activity.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

private val Context.dataStore by preferencesDataStore("app_preferences")
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth
        storage = Firebase.storage
        firestore = Firebase.firestore

        if (auth.currentUser == null) {
            activity.let {
                startActivity(Intent(it, LoginActivity::class.java))
                activity?.finish()
            }
        }

        setUpViewModel()
        setUpAction()

//        val textView: TextView = binding.textProfile
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun setUpAction() {
        binding.imageProfile.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        _binding!!.btnLogout.setOnClickListener{
            auth.signOut()
            activity.let {
                startActivity(Intent(it, LoginActivity::class.java))
                activity?.finish()
            }

        }

        binding.groupHelp.setOnClickListener {
            activity.let {
                Toast.makeText(it, "Fitur ini masih dalam pengembangan", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.groupQna.setOnClickListener {
            activity.let {
                Toast.makeText(it, "Fitur ini masih dalam pengembangan", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.groupAboutUs.setOnClickListener {
            activity.let {
                Toast.makeText(it, "Fitur ini masih dalam pengembangan", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.groupReport.setOnClickListener {
            activity.let {
                Toast.makeText(it, "Fitur ini masih dalam pengembangan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setUpViewModel() {
        val pref = requireContext().dataStore

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(pref))
        )[MainViewModel::class.java]

        activity.let {

            Glide.with(this@ProfileFragment)
                .load(auth.currentUser?.photoUrl)
                .into(binding.imageProfile)

            binding.FullName.text = auth.currentUser?.displayName
            binding.email.text = auth.currentUser?.email

//            mainViewModel.getUser().observe(it!!) { user ->
//                if (user.isLogin) {
//                    binding.FullName.text = user.name
//                    binding.email.text = user.email
//                } else {
//                    startActivity(Intent(it, WelcomeActivity::class.java))
//                    requireActivity().finish()
//                }
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                activity.let {
                    getFile = uriToFile(uri, it)

                    val storageRef = storage.reference
                    val profilePictureRef = storageRef.child("profile-picture/" + UUID.randomUUID().toString() + "." + getFile!!.extension)

                    var uploadTask = profilePictureRef.putFile(uri)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        profilePictureRef.downloadUrl.addOnCompleteListener {downloadUrl ->
                            firestore.collection("users").document(auth.currentUser?.uid.toString())
                                .set(hashMapOf("photoURL" to downloadUrl.result))
                                .addOnCompleteListener {
                                    Glide.with(this@ProfileFragment)
                                        .load(auth.currentUser?.photoUrl)
                                        .into(binding.imageProfile)
                                }
                        }
                    }
                }
            }
        }
    }
}