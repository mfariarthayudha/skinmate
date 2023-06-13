package com.dicoding.picodiploma.SkinMate.view.ui.fragment.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.databinding.FragmentHomeBinding
import com.dicoding.picodiploma.SkinMate.model.DataBlog
import com.dicoding.picodiploma.SkinMate.model.UserPreference
import com.dicoding.picodiploma.SkinMate.view.ListBlogAdapter
import com.dicoding.picodiploma.SkinMate.view.ViewModelFactory
import com.dicoding.picodiploma.SkinMate.view.ui.activity.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File

private val Context.dataStore by preferencesDataStore("app_preferences")
class HomeFragment : Fragment() {
    private lateinit var rvBlog: RecyclerView
    private val list = ArrayList<DataBlog>()

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvBlog = _binding!!.rvBlog
        rvBlog.setHasFixedSize(true)

        list.addAll(getListBlog())
        showRecyclerList()

        auth = Firebase.auth

        setUpViewModel()

        return root
    }

    private fun setUpViewModel() {
        val pref = requireContext().dataStore

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(pref))
        )[MainViewModel::class.java]

        activity.let {
            val user = auth.currentUser

            Glide.with(this@HomeFragment)
                .load(user?.photoUrl)
                .into(binding.imageProfile)

            binding.username.text = user?.displayName

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

    private fun showRecyclerList() {
        activity.let {
            rvBlog.layoutManager = LinearLayoutManager(it)
            val listBlogAdapter = ListBlogAdapter(list)
            rvBlog.adapter = listBlogAdapter
        }
    }

    private fun getListBlog(): Collection<DataBlog> {
        val dataName = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)

        val listBLog = ArrayList<DataBlog>()

        for(i in dataName.indices){
            val blog = DataBlog(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listBLog.add(blog)
        }
        return listBLog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}