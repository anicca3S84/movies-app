package com.codework.movies_app.fragments.login_register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.codework.movies_app.R
import com.codework.movies_app.activities.MainActivity
import com.codework.movies_app.databinding.ActivityMainBinding
import com.codework.movies_app.databinding.FragmentLoginBinding
import com.codework.movies_app.dialogs.setupBottomSheetDialog
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.viewmodes.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.notLogin.setOnClickListener{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvForgotPassword.setOnClickListener{
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
            Log.d("SetUpDialog","Success")
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.resetPassword.collect{
                    when(it) {
                        is Resource.Loading ->  {}
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), "Chúng tôi đã gửi link đặt lại\n mật khẩu đến email của bạn", Toast.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(),"Error ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            viewModel.login(email, password)
        }




        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.login.collect {
                        when (it) {
                            is Resource.Loading -> {
                                btnLogin.isEnabled = false
                                btnLogin.alpha = 0.5f
                            }

                            is Resource.Success -> {
                                btnLogin.isEnabled = true
                                btnLogin.alpha = 1.0f
                                Intent(requireActivity(), MainActivity::class.java)
                                    .also { intent ->
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                            }

                            is Resource.Error -> {
                                btnLogin.isEnabled = false
                                btnLogin.alpha = 1.0f
                                Snackbar.make(requireView(), it.message.toString(), Toast.LENGTH_LONG).show()
                            }

                            else -> Unit
                        }
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
                    if(validation.password is ValidationInfo.Error){
                        withContext(Dispatchers.Main){
                            binding.edPassword.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }
    }
}