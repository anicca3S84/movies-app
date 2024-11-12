package com.codework.movies_app.fragments.login_register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codework.movies_app.R
import com.codework.movies_app.activities.MainActivity
import com.codework.movies_app.databinding.FragmentIntroBinding
import com.codework.movies_app.viewmodes.IntroViewModel
import com.codework.movies_app.viewmodes.IntroViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.codework.movies_app.viewmodes.IntroViewModel.Companion.MAIN_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding
    private val viewModel by viewModels<IntroViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    MAIN_ACTIVITY -> {
                        Intent(requireActivity(), MainActivity::class.java)
                            .also { intent ->
                                intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                                startActivity(intent)
                            }
                    }

                    ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }

        binding.btnGetIn.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
    }

}