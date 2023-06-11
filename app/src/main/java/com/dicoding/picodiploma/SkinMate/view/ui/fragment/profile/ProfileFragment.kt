package com.dicoding.picodiploma.SkinMate.view.ui.fragment.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.databinding.FragmentProfileBinding
import com.dicoding.picodiploma.SkinMate.model.UserPreference
import com.dicoding.picodiploma.SkinMate.view.ViewModelFactory
import com.dicoding.picodiploma.SkinMate.view.ui.activity.main.MainViewModel
import com.dicoding.picodiploma.SkinMate.view.ui.activity.welcome.WelcomeActivity

private val Context.dataStore by preferencesDataStore("app_preferences")
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpViewModel()
        setUpAction()

//        val textView: TextView = binding.textProfile
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun setUpAction() {
        _binding!!.btnLogout.setOnClickListener{
            mainViewModel.logout()
        }
    }

    private fun setUpViewModel() {
        val pref = requireContext().dataStore

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(pref))
        )[MainViewModel::class.java]

        activity.let {
            mainViewModel.getUser().observe(it!!) { user ->
                if (user.isLogin) {
                    binding.FullName.text = user.name
                    binding.email.text = user.email
                } else {
                    startActivity(Intent(it, WelcomeActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}