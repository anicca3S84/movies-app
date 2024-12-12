package com.codework.movies_app.fragments.main_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codework.movies_app.R
import com.codework.movies_app.adapters.HistoryAdapter
import com.codework.movies_app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val historyAdapter by lazy { HistoryAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener{
            val b = Bundle().apply { putString("slug", "truyen-thuyet-nang-nak")}
            findNavController().navigate(R.id.action_homeFragment_to_movieDetailFragment, b)
        }
    }


}