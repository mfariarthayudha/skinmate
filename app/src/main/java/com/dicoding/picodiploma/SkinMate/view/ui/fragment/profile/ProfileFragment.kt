package com.dicoding.picodiploma.SkinMate.view.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.databinding.FragmentProfileBinding
import com.dicoding.picodiploma.SkinMate.model.UserPreference
import com.dicoding.picodiploma.SkinMate.view.ViewModelFactory
import com.dicoding.picodiploma.SkinMate.view.ui.activity.welcome.WelcomeActivity

class ProfileFragment(private val dataStore: DataStore<Preferences>) : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel
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
            profileViewModel.logout()
        }
    }

    private fun setUpViewModel() {
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfileViewModel::class.java]

        activity.let {
            profileViewModel.getUser().observe(it!!) { user ->
                if (user.isLogin) {
                    binding.FullName.text = getString(R.string.greeting, user.name)
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