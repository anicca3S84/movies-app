package com.codework.movies_app.fragments.main_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codework.movies_app.activities.MainActivity
import com.codework.movies_app.adapters.CommentAdapter
import com.codework.movies_app.adapters.NotificationAdapter
import com.codework.movies_app.databinding.FragmentNotificationBinding
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private val notificationAdapter: NotificationAdapter by lazy { NotificationAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRcv()
        setUpRefreshLayout()
        observeNotificationData()



        notificationAdapter.onLongClick = {
            viewModel.deleteNotification(it.id)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshNotifications()
    }



    private fun observeNotificationData() {
        lifecycleScope.launch {
            viewModel.notification.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        notificationAdapter.differ.submitList(resource.data) {
                            notificationAdapter.notifyDataSetChanged()
                        }
                        (requireActivity() as MainActivity).updateNotificationBadge(resource.data?.size ?: 0)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun setUpRcv() {
        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun setUpRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshNotifications()
        }
    }
}
