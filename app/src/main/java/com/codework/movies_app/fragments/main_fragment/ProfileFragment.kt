package com.codework.movies_app.fragments.main_fragment

import HorizontalItemDecoration
import android.content.Intent
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
import com.codework.movies_app.activities.LoginRegisterActivity
import com.codework.movies_app.adapters.HistoryAdapter
import com.codework.movies_app.data.User
import com.codework.movies_app.databinding.FragmentProfileBinding
import com.codework.movies_app.dialogs.showLoginDialog
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.viewmodes.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private var user: User? = null
    private val historyAdapter by lazy { HistoryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val username = Constants.getUsername(requireContext())
        if (username.isNullOrEmpty()) {
            historyAdapter.differ.submitList(emptyList())
            binding.rvHistory.visibility = View.GONE
        } else {
            viewModel.getHistory(username)
            binding.tvName.text = username
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRvHistory()

        binding.layoutHistory.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_historyFragment)
        }

        historyAdapter.onClick = {
            val bundle = Bundle().apply {
                putString("slug", it.slug)
            }
            findNavController().navigate(R.id.action_profileFragment_to_filmDetailFragment, bundle)
        }

        lifecycleScope.launch {
            viewModel.history.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val items = resource.data?.items.orEmpty()
                        if (items.isEmpty()) {
                            binding.rvHistory.visibility = View.GONE
                        } else {
                            binding.rvHistory.visibility = View.VISIBLE
                            historyAdapter.differ.submitList(items)
                        }
                    }
                    is Resource.Error -> {
                        binding.rvHistory.visibility = View.GONE
                        historyAdapter.differ.submitList(emptyList())
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.rvHistory.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launch {
            viewModel.user.collectLatest { resources ->
                when (resources) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resources.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        user = resources.data!!
                        binding.tvName.text = user!!.username
                        Log.d("UserProfile", user!!.email)
                    }

                    else -> Unit
                }
            }
        }

        binding.layoutInformation.setOnClickListener{
            user?.let {
                val bundle = Bundle().apply {
                    putParcelable("user", user)
                }
                findNavController().navigate(
                    R.id.action_profileFragment_to_userAccountFragment,
                    bundle
                )
            } ?: showLoginDialog(requireContext())
        }



        binding.layoutLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.checkLogin.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        is Resource.Success -> {
                            viewModel.logout()
                            val intent =
                                Intent(requireActivity(), LoginRegisterActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }

                        else -> Unit
                    }
                }
            }
        }


    }

    private fun setUpRvHistory() {
        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}