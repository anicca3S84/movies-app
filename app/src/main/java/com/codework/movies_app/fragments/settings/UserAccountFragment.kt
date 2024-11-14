package com.codework.movies_app.fragments.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.codework.movies_app.R
import com.codework.movies_app.databinding.FragmentUserAccountBinding

class UserAccountFragment: Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val args by navArgs<UserAccountFragmentArgs>()

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
        binding.imgClose.setOnClickListener{
            findNavController().navigateUp()
        }
        setInformation()
    }

    private fun setInformation() {
        val user = args.user
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user.imagePath).error(ColorDrawable(Color.WHITE)).into(imageUser)
            edEmail.setText(user.email)
            edPhone.setText(user.phone)
            edGender.setText(user.gender)
            edUsername.setText(user.username)
        }
    }
}