package com.dicoding.picodiploma.SkinMate.view.ui.fragment.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.SkinMate.R
import com.dicoding.picodiploma.SkinMate.databinding.FragmentHomeBinding
import com.dicoding.picodiploma.SkinMate.model.DataBlog
import com.dicoding.picodiploma.SkinMate.view.ListBlogAdapter

class HomeFragment : Fragment() {
    private lateinit var rvBlog: RecyclerView
    private val list = ArrayList<DataBlog>()

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return root
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