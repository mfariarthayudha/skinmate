package com.dicoding.picodiploma.SkinMate.view.ui.fragment.media

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.picodiploma.SkinMate.databinding.FragmentMediaBinding
import com.dicoding.picodiploma.SkinMate.ml.ModelDisease
import com.dicoding.picodiploma.SkinMate.ml.ModelSkinMate
import com.dicoding.picodiploma.SkinMate.reduceFileImage
import com.dicoding.picodiploma.SkinMate.retrofit.ApiConfig
import com.dicoding.picodiploma.SkinMate.retrofit.FileUploadResponse
import com.dicoding.picodiploma.SkinMate.rotateFile
import com.dicoding.picodiploma.SkinMate.uriToFile
import com.dicoding.picodiploma.SkinMate.view.ui.activity.camera.CameraActivity
import com.dicoding.picodiploma.SkinMate.view.ui.activity.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MediaFragment : Fragment() {
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private lateinit var auth: FirebaseAuth
    private var getFile: File? = null

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                activity.let {
                    Toast.makeText(
                    it,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show() }

                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity.let {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    it!!,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        auth = Firebase.auth

        if (auth.currentUser == null) {
            activity.let {
                startActivity(Intent(it, LoginActivity::class.java))
                activity?.finish()
            }
        }

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        return root
    }

    private fun uploadImage() {
        if (getFile != null){
            //deteksi
            val model = ModelDisease.newInstance(this.requireContext())
            val skinDisease = getSkinDisease()

            //prepare input
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

            //file to bitmap
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val resize = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

            //create tensorImage
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resize)

            //masukinnn
            inputFeature0.loadBuffer(tensorImage.buffer)

            //output
            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var maxIndex = 0
            var maxValue = outputFeature0.getFloatValue(0)
            for (i in 0 until 4){
                val value = outputFeature0.getFloatValue(i)
                if (value > maxValue){
                    maxValue = value
                    maxIndex = i
                }
            }

            val skin_Disease = skinDisease[maxIndex]

            binding.result.text = skin_Disease

        }else{
            activity.let {
                Toast.makeText(it, "Masukan gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSkinDisease(): List<String> {
        val inputString = this.requireContext().assets.open("labelSkinDeasease.txt").bufferedReader().use {
            it.readText()
        }

        return inputString.split("\n")
    }

//    private fun uploadImage() {
//        if (getFile != null) {
//            val file = reduceFileImage(getFile as File)
//
//            val description = "Ini adalah deksripsi gambar".toRequestBody("text/plain".toMediaType())
//            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
//            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//                "photo",
//                file.name,
//                requestImageFile
//            )
//
//            val apiService = ApiConfig().getApiService()
//            val uploadImageRequest = apiService.uploadImage(imageMultipart, description)
//
//            uploadImageRequest.enqueue(object : Callback<FileUploadResponse> {
//                override fun onResponse(
//                    call: Call<FileUploadResponse>,
//                    response: Response<FileUploadResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val responseBody = response.body()
//                        if (responseBody != null && !responseBody.error) {
//                            activity.let {
//                                Toast.makeText(it, responseBody.message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } else {
//                        activity.let {
//                            Toast.makeText(it, response.message(), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
//                    activity.let {
//                        Toast.makeText(it, t.message, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            })
//        } else {
//            activity.let {
//                Toast.makeText(it, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                activity.let {
                    val myFile = uriToFile(uri, it)
                    getFile = myFile
                    binding.previewImageView.setImageURI(uri)
                }
            }
        }
    }

    private fun startCameraX() {
        activity.let {
            val intent = Intent(it, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}