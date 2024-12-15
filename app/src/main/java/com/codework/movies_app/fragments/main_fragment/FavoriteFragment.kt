package com.codework.movies_app.fragments.main_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codework.movies_app.R
import com.codework.movies_app.adapters.FavFilmAdapter
import com.codework.movies_app.databinding.FragmentFavoriteBinding
import com.codework.movies_app.dialogs.deleteCommentDialog
import com.codework.movies_app.dialogs.showLoginDialog
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.FavoriteViewModel
import com.codework.movies_app.viewmodes.FilmDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favFilmAdapter by lazy { FavFilmAdapter() }
    private val viewModel by viewModels<FavoriteViewModel>()

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
        setUpRefreshLayout()
        setUpFavRcv()
        notifyListData()

        favFilmAdapter.onLongClick = { film, position ->
            val currentUserName = Constants.getUsername(requireContext())
            deleteCommentDialog(requireContext()) {
                lifecycleScope.launch {

                    viewModel.deleteFavFilm(currentUserName!!,film.slug)
                    Log.d("CurrentUserName", currentUserName.toString())
                    Log.d("FilmSlug", film.slug)

                    val currentList = favFilmAdapter.differ.currentList.toMutableList()
                    currentList.removeAt(position)
                    favFilmAdapter.differ.submitList(currentList)
                    if(favFilmAdapter.differ.currentList.size == 0){
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.imageEmptyBox.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        binding.rvFavFilm.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Toast.makeText(
                        requireContext(),
                        "Xóa thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        favFilmAdapter.onClick = {
            val bundle = Bundle().apply {
                putString("slug", it.slug)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_filmDetailFragment, bundle)
        }
    }


    private fun notifyListData() {
        lifecycleScope.launch {
            viewModel.status.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        favFilmAdapter.differ.submitList(it.data?.items)
                        if (favFilmAdapter.differ.currentList.size == 0) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.imageEmptyBox.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.rvFavFilm.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.tvEmptyMessage.text = "Chưa có phim nào được thêm vào danh sách"
                        } else {
                            binding.tvEmptyMessage.visibility = View.GONE
                            binding.imageEmptyBox.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            binding.rvFavFilm.visibility = View.VISIBLE
                            binding.swipeRefreshLayout.isRefreshing = false
                        }

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
        val username = Constants.getUsername(requireContext())

        lifecycleScope.launch {
            viewModel.getFavFilm(username!!)

            viewModel.status.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvEmptyMessage.visibility = View.GONE
                        binding.imageEmptyBox.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val filmList = it.data?.items
                        favFilmAdapter.differ.submitList(filmList)

                        if (filmList.isNullOrEmpty()) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.imageEmptyBox.visibility = View.VISIBLE
                            binding.rvFavFilm.visibility = View.GONE
                            binding.tvEmptyMessage.text = "Chưa có phim nào được thêm vào danh sách"
                        } else {
                            binding.tvEmptyMessage.visibility = View.GONE
                            binding.imageEmptyBox.visibility = View.GONE
                            binding.rvFavFilm.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvFavFilm.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.imageEmptyBox.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "Lỗi khi tải danh sách yêu thích: ${it.message}"
                    }

                    else -> Unit
                }
            }
    }

    }


    private fun setUpFavRcv() {
        binding.rvFavFilm.apply {
            adapter = favFilmAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            addItemDecoration(VerticalItemDecoration())
        }

    }


}