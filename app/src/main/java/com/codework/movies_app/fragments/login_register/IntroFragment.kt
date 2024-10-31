package com.codework.movies_app.fragments.login_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codework.movies_app.R
import com.codework.movies_app.databinding.ActivityMainBinding
import com.codework.movies_app.databinding.FragmentIntroBinding

class IntroFragment: Fragment() {
    private lateinit var binding: FragmentIntroBinding

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
        binding.btnGetIn.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_registerFragment)
        }
    }
}