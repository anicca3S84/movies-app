package com.codework.movies_app.fragments.login_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.codework.movies_app.R
import com.codework.movies_app.databinding.FragmentRegisterBinding
import com.codework.movies_app.data.User
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.viewmodes.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment: Fragment(){
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnRegister.setOnClickListener{
                val email = edEmail.text.toString().trim()
                val username = edUserName.text.toString().trim()
                val password = edPassword.text.toString().trim()
                val confirmPassword = edConfirmPassword.text.toString().trim()
                val user = User(email,username,password,"")
                viewModel.register(user,username,confirmPassword)
            }
        }

        binding.apply {
            //chỉ tồn tại khi Fragment's view tồn tại
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.register.collect{
                    when(it){
                        is Resource.Loading -> {
                            btnRegister.isEnabled = false
                            btnRegister.alpha = 0.2f
                        }
                        is Resource.Success -> {
                            btnRegister.isEnabled = true
                            btnRegister.alpha = 1.0f
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is Resource.Error -> {
                            btnRegister.isEnabled = true
                            btnRegister.alpha = 1.0f
                        }
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            //Activity chứa fragment chưa bị hủy thì các đoạn mã trong luồng
            //sẽ tự động bắt đầu lại khi Fragment's view quay lại trạng
            //thái STARTED
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.validation.collect { validation ->
                    if(validation.email is ValidationInfo.Error){
                        withContext(Dispatchers.Main) { //các đoạn mã trong này chạy trên luồng chính
                            binding.edEmail.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                        }
                    }
                    if(validation.username is ValidationInfo.Error){
                        withContext(Dispatchers.Main){
                            binding.edUserName.apply {
                                requestFocus()
                                error = validation.username.message
                            }
                        }
                    }
                    if(validation.password is ValidationInfo.Error){
                        withContext(Dispatchers.Main){
                            binding.edPassword.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                    if(validation.confirmPassword is ValidationInfo.Error){
                        withContext(Dispatchers.Main){
                            binding.edConfirmPassword.apply {
                                requestFocus()
                                error = validation.confirmPassword.message
                            }
                        }
                    }
                }
            }
        }


    }
}
