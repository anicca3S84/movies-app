package com.codework.movies_app.fragments.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codework.movies_app.R
import com.codework.movies_app.data.User
import com.codework.movies_app.databinding.FragmentUserAccountBinding
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.viewmodes.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val args by navArgs<UserAccountFragmentArgs>()
    private val viewModel by viewModels<UserAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // Gọi hàm setInformation lần đầu để hiển thị thông tin người dùng
        setInformation()

        binding.buttonSave.setOnClickListener {
            val updatedUser = User(
                email = binding.edEmail.text.toString(),
                username = binding.edUsername.text.toString(),
                phone = binding.edPhone.text.toString(),
                gender = binding.edGender.text.toString()
            )
            viewModel.updateUser(updatedUser)
            Constants.saveUsername(requireContext(), updatedUser.username)
        }

        // Lắng nghe trạng thái của việc cập nhật thông tin người dùng
        lifecycleScope.launch {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.isEnabled = false
                        binding.buttonSave.alpha = 0.5f
                    }
                    is Resource.Success -> {
                        binding.buttonSave.isEnabled = true
                        binding.buttonSave.alpha = 1f
                        it.data?.let { updatedUser ->
                            binding.edEmail.setText(updatedUser.email)
                            binding.edUsername.setText(updatedUser.username)
                            binding.edPhone.setText(updatedUser.phone)
                            binding.edGender.setText(updatedUser.gender)
                            Toast.makeText(
                                requireContext(),
                                "Updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                    }
                    is Resource.Error -> {
                        binding.buttonSave.isEnabled = true
                        binding.buttonSave.alpha = 1f
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    // Gọi lại hàm getUser mỗi khi fragment quay lại
    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun setInformation() {
        lifecycleScope.launch {
            viewModel.user.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        user?.let {
                            binding.apply {
                                edEmail.setText(it.email)
                                edPhone.setText(it.phone)
                                edGender.setText(it.gender)
                                edUsername.setText(it.username)
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}
