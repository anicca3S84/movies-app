package com.codework.movies_app.fragments.main_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codework.movies_app.adapters.FavFilmAdapter
import com.codework.movies_app.databinding.FragmentFavoriteBinding
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.FavoriteViewModel
import com.codework.movies_app.viewmodes.FilmDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favFilmAdapter by lazy { FavFilmAdapter() }
    private val viewModel by viewModels<FavoriteViewModel>()
    private val detailViewModel by viewModels<FilmDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFavOnclick()
        setUpRefreshLayout()
        setUpFavRcv()
        notifyListData()

        favFilmAdapter.onLongClick = { film ->
            detailViewModel.deleteFavFilm(film.slug)
            viewModel.getFavFilm(Constants.getUsername(requireContext())!!)
        }
    }

    private fun notifyListData() {
        lifecycleScope.launch {
            viewModel.status.collectLatest{
                when(it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        favFilmAdapter.differ.submitList(it.data?.items)
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvFavFilm.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpRefreshLayout() {
        val username = Constants.getUsername(requireContext())
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFavFilm(username!!)
        }
    }

    override fun onResume() {
        super.onResume()
        val username = Constants.getUsername(requireContext())!!
        viewModel.getFavFilm(username)
    }



    private fun setUpFavOnclick() {
        favFilmAdapter.onClick = {
        }
    }

    private fun setUpFavRcv() {
        binding.rvFavFilm.apply {
            adapter = favFilmAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }

    }
}