package com.codework.movies_app.fragments.main_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codework.movies_app.adapters.CommentAdapter
import com.codework.movies_app.adapters.NotificationAdapter
import com.codework.movies_app.databinding.FragmentNotificationBinding
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private val notificationAdapter: NotificationAdapter by lazy { NotificationAdapter() }

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

        observeViewModel()
        setUpRcv()
    }

    private fun setUpRcv() {
        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notification.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        notificationAdapter.differ.submitList(resource.data)
                    }
                    is Resource.Error -> {

                    }
                    else -> Unit
                }
            }
        }
    }
}
