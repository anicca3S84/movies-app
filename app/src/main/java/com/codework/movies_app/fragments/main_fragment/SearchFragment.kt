package com.codework.movies_app.fragments.main_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codework.movies_app.R
import com.codework.movies_app.adapters.SearchItemAdapter
import com.codework.movies_app.databinding.FragmentSearchBinding
import com.codework.movies_app.viewmodes.SearchItemViewModel

class SearchFragment: Fragment() {
    private lateinit var viewModel: SearchItemViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var backButton: ImageView
    private lateinit var searchItemAdapter: SearchItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        backButton = binding.searchBack
        searchView = binding.searchView
        searchItemAdapter = SearchItemAdapter(emptyList(), this)
        recyclerView.adapter = searchItemAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchItemViewModel::class.java)
        viewModel.itemResponse.observe(viewLifecycleOwner, Observer {
            searchResponse ->
            if(searchResponse.isNotEmpty()) {
                Log.d("Hàm Observer đã chạy và searchResponse không null:", "True")
                searchItemAdapter = SearchItemAdapter(searchResponse, this)
                recyclerView.adapter = searchItemAdapter
            } else {
                Log.d("Load Search Phim: ", "Ko co phim!")
            }
        })

        backButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
        searchView.setOnClickListener {
            searchView.requestFocus()
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getSearchFilms(it)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
}