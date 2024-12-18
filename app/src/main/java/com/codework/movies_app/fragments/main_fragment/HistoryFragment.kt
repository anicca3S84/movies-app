package com.codework.movies_app.fragments.main_fragment

import HorizontalItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codework.movies_app.R
import com.codework.movies_app.adapters.FavFilmAdapter
import com.codework.movies_app.adapters.HistoryAdapter
import com.codework.movies_app.databinding.FragmentHistoryBinding
import com.codework.movies_app.dialogs.deleteCommentDialog
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val historyAdapter by lazy { FavFilmAdapter() }
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRvHistory()
        observeHistory()
        binding.imgClose.setOnClickListener{
            findNavController().navigateUp()
        }
        setUpRefreshLayout()

        historyAdapter.onClick = {
            val bundle = Bundle().apply {
                putString("slug", it.slug)
            }
            findNavController().navigate(R.id.action_historyFragment_to_filmDetailFragment, bundle)
        }

        historyAdapter.onLongClick = { film, position ->
            val currentUserName = Constants.getUsername(requireContext())
            deleteCommentDialog(requireContext()) {
                lifecycleScope.launch {
                    viewModel.deleteHistory(currentUserName!!, film.id)
                    Log.d("CurrentUserName", currentUserName)
                    Log.d("FilmSlug", film.slug)

                    // Remove the item from the current list safely
                    val updatedList = historyAdapter.differ.currentList.toMutableList()
                    if (position < updatedList.size && updatedList[position].id == film.id) {
                        updatedList.removeAt(position)
                        historyAdapter.differ.submitList(updatedList) {
                            // Ensure the adapter is updated
                            historyAdapter.notifyDataSetChanged()
                        }

                        // Handle empty state UI
                        if (updatedList.isEmpty()) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.imageEmptyBox.visibility = View.VISIBLE
                            binding.tvEmptyMessage.text = "Chưa có lịch sử xem"
                            binding.progressBar.visibility = View.GONE
                            binding.rvHistory.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                        }

                        Toast.makeText(
                            requireContext(),
                            "Xóa thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("Error", "Invalid position or item mismatch!")
                    }
                }
            }
        }
    }

    private fun setUpRefreshLayout() {
        val username = Constants.getUsername(requireContext())
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getHistory(username!!)
        }
    }

    override fun onResume() {
        super.onResume()
        val username = Constants.getUsername(requireContext())

        lifecycleScope.launch {
            if(username.isNullOrEmpty()){
                binding.tvEmptyMessage.text = "Vui lòng đăng nhập để sử dụng tính năng này"
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.imageEmptyBox.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                return@launch
            }
            viewModel.getHistory(username)

            binding.tvClearAll.setOnClickListener {
                val username = Constants.getUsername(requireContext())
                if (username.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Show confirmation dialog before deleting all history
                deleteCommentDialog(requireContext()) {
                    lifecycleScope.launch {
                        // Call the ViewModel to delete all history
                        viewModel.deleteAllHistory(username)

                        // Collect and update UI after deletion
                        viewModel.deleteAllHistory.collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Resource.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.rvHistory.visibility = View.GONE
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    binding.tvEmptyMessage.visibility = View.VISIBLE
                                    binding.imageEmptyBox.visibility = View.VISIBLE
                                    binding.tvEmptyMessage.text = "Chưa có lịch sử xem"
                                }
                                is Resource.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }



            viewModel.history.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvEmptyMessage.visibility = View.GONE
                        binding.imageEmptyBox.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val filmList = it.data?.items
                        historyAdapter.differ.submitList(filmList)

                        if (filmList.isNullOrEmpty()) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.imageEmptyBox.visibility = View.VISIBLE
                            binding.rvHistory.visibility = View.GONE
                            binding.tvEmptyMessage.text = "Chưa có phim nào được thêm vào danh sách"
                        } else {
                            binding.tvEmptyMessage.visibility = View.GONE
                            binding.imageEmptyBox.visibility = View.GONE
                            binding.rvHistory.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvHistory.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.imageEmptyBox.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "Lỗi khi tải danh sách: ${it.message}"
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun observeHistory() {
        lifecycleScope.launch {
            viewModel.history.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val items = resource.data?.items.orEmpty()
                        Log.d("HistoryFragment", "Fetched items: $items") // Debug log
                        historyAdapter.differ.submitList(items)
                        binding.rvHistory.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Log.e("HistoryFragment", "Error: ${resource.message}")
                        binding.rvHistory.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        Log.d("HistoryFragment", "Loading...")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpRvHistory() {
        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            addItemDecoration(
                DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
                    setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
                }
            )
        }
    }
}
