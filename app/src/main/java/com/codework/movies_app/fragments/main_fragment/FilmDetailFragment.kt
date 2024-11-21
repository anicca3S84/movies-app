package com.codework.movies_app.fragments.main_fragment

import HorizontalItemDecoration
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codework.movies_app.R
import com.codework.movies_app.activities.MediaPlayerActivity
import com.codework.movies_app.adapters.CommentAdapter
import com.codework.movies_app.adapters.FavFilmAdapter
import com.codework.movies_app.adapters.SameCategoryAdapter
import com.codework.movies_app.databinding.FragmentFilmDetailBinding
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.VerticalItemDecoration
import com.codework.movies_app.viewmodes.FavoriteViewModel
import com.codework.movies_app.viewmodes.FilmDetailViewModel
import com.codework.movies_app.viewmodes.SharedCountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmDetailFragment : Fragment() {
    private lateinit var binding: FragmentFilmDetailBinding
    private val sameCategoryAdapter: SameCategoryAdapter by lazy { SameCategoryAdapter() }
    private val viewModel by viewModels<FilmDetailViewModel>()
    private val countViewModel by viewModels<SharedCountViewModel>()
    private val args by navArgs<FilmDetailFragmentArgs>()
    private val favViewModel: FavoriteViewModel by viewModels()
    private val favFilmAdapter: FavFilmAdapter by lazy { FavFilmAdapter() }
    private val commentAdapter: CommentAdapter by lazy { CommentAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slug = args.slug
        slug.let {
            viewModel.getFilmDetailBySlug(it)
            observeFilmDetail(it)
            updateFavOnclick(it)
            updatePlayButton(it) // Thiết lập ban đầu cho nút Play
            addComment(it)
        }

        setUpSameCategoryRv()
        setCommentRcv()
        setUpSameCategoryOnclick()

        commentAdapter.onLongClick = {
            viewModel.deleteComment(it.id)
        }

        lifecycleScope.launch {
            viewModel.comment.collectLatest {
                binding.apply {
                    when (it) {
                        is Resource.Loading -> {
                            sendProgressBar.visibility = View.VISIBLE
                            imgSend.visibility = View.INVISIBLE
                        }

                        is Resource.Success -> {
                            Toast.makeText(requireContext(), "Gửi thành công", Toast.LENGTH_SHORT)
                                .show()
                            sendProgressBar.visibility = View.GONE
                            imgSend.visibility = View.VISIBLE
                            edtComment.text.clear()
                            edtComment.clearFocus()
                        }

                        is Resource.Error -> {
                            sendProgressBar.visibility = View.GONE
                            imgSend.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                "Vui lòng đăng nhập",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.sameCategory.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        sameCategoryAdapter.differ.submitList(it.data?.items)
                        binding.progressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.listComment.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        commentAdapter.differ.submitList(it.data)
                        Log.d("listComment", it.data.toString())
                        binding.progressBar.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setCommentRcv() {
        binding.rvComment.apply {
            adapter = commentAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun addComment(slug: String) {
        binding.imgSend.setOnClickListener {
            val content = binding.edtComment.text.toString().trim()
            viewModel.addComment(slug, content)

            lifecycleScope.launch {
                viewModel.comment.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            val newComment = it.data
                            val currentList = commentAdapter.differ.currentList.toMutableList()
                            newComment?.let {
                                currentList.add(0, it)
                                commentAdapter.differ.submitList(currentList)
                            }

                            binding.edtComment.text.clear()
                            binding.edtComment.clearFocus()
                            Toast.makeText(requireContext(), "Gửi thành công", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "Vui lòng đăng nhập",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun updateFavOnclick(slug: String) {
        var isColorChanged = false
        val username = Constants.getUsername(requireContext())
        if (username != null) {
            Log.d("updateFavOnclick", username)
            binding.imgFav.setOnClickListener {
                if (isColorChanged) {
                    binding.imgFav.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.g_white)
                    )
                    viewModel.deleteFavFilm(slug)
                    Toast.makeText(requireContext(), "Đã bỏ thích", Toast.LENGTH_SHORT).show()
                } else {
                    binding.imgFav.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.g_red)
                    )
                    viewModel.addFavFilm(slug)
                    Toast.makeText(requireContext(), "Đã thích", Toast.LENGTH_SHORT).show()
                }

                isColorChanged = !isColorChanged
            }
        } else {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }


    private fun observeFilmDetail(slug: String) {
        lifecycleScope.launch {
            viewModel.filmDetail.collect { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.apply {
                            Glide.with(requireContext()).load(response.data?.thumbUrl)
                                .into(imgPoster)
                            tvFilmTitle.text = response.data?.name
                            tvActors.text = response.data?.actor?.joinToString(", ")
                            Log.d("actor", response.data?.actor.toString())
                            tvDirectors.text = response.data?.director?.joinToString(", ")
                            tvSummary.text = response.data?.content
                            tvTime.text = response.data?.time.toString()
                            btnEpisode.text = response.data?.episodeTotal
                            tvGenre.text = response.data?.categories?.joinToString(", ") { it.name }
                            Log.d("genre", response.data?.categories?.joinToString(", ") { it.name }.toString())
                            tvNation.text = response.data?.countries?.joinToString(", ") { it.name }
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Error ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Error", "Error: ${response.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpSameCategoryOnclick() {
        sameCategoryAdapter.onClick = { item ->
            val newSlug = item.slug
            binding.progressBar.visibility = View.VISIBLE

            viewModel.getFilmDetailBySlug(newSlug)
            observeFilmDetail(newSlug)

            // Cập nhật nút Play
            updatePlayButton(newSlug)
            binding.imgFav.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            updateFavOnclick(newSlug)
            addComment(newSlug)
        }
    }

    private fun setUpSameCategoryRv() {
        binding.rvSameType.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sameCategoryAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun updatePlayButton(slug: String) {
        lifecycleScope.launch {
            viewModel.videoUrl.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val videoLink = resource.data
                        binding.btnPlay.setOnClickListener {
                            countViewModel.increaseCount()
                            Log.d("increaseCount", countViewModel.count.value.toString())
                            val intent =
                                Intent(requireContext(), MediaPlayerActivity::class.java).apply {
                                    putExtra("url", videoLink)
                                    putExtra("slug", slug)
                                    putExtra("count", countViewModel.count.value)
                                }
                            startActivity(intent)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
