package com.dicoding.picodiploma.SkinMate.view.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.SkinMate.model.UserModel
import com.dicoding.picodiploma.SkinMate.model.UserPreference
import kotlinx.coroutines.launch

class ProfileViewModel(private val pref: UserPreference) : ViewModel(){
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is profile Fragment"
//    }
//    val text: LiveData<String> = _text

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}