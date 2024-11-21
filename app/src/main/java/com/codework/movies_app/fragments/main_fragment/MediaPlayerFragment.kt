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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.codework.movies_app.R
import com.codework.movies_app.activities.MainActivity
import com.codework.movies_app.activities.MediaPlayerActivity
import com.codework.movies_app.adapters.EpisodesAdapter
import com.codework.movies_app.adapters.SuggestFilmAdapter
import com.codework.movies_app.data.FilmDetail
import com.codework.movies_app.databinding.FragmentMediaPlayerBinding
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.viewmodes.MediaPlayerViewModel
import com.codework.movies_app.viewmodes.SharedCountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaPlayerFragment : Fragment() {
    private lateinit var binding: FragmentMediaPlayerBinding
    private val episodesAdapter by lazy { EpisodesAdapter() }
    private val suggestFilmAdapter by lazy { SuggestFilmAdapter() }
    private val viewModel: MediaPlayerViewModel by activityViewModels()
    private val countViewModel by activityViewModels<SharedCountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRvEpisode()
        setUpSuggestFilmRv()
        setUpCount()
        setUpEpisodeOnClick()
        setUpSuggestFilmOnClick()

        lifecycleScope.launch {
            viewModel.episodes.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.episodeProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        episodesAdapter.differ.submitList(it.data)
                        binding.episodeProgressBar.visibility = View.GONE
                        binding.tvTotalEpisodes.text =
                            episodesAdapter.differ.currentList.size.toString()

                    }

                    is Resource.Error -> {
                        binding.episodeProgressBar.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.suggestFilm.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.suggestProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        suggestFilmAdapter.differ.submitList(it.data?.items)
                        binding.suggestProgressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.suggestProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filmDetail.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        setUpInfo(it.data)
                        Log.d("_filmDetail", it.data.toString())
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpEpisodeOnClick() {
        episodesAdapter.onClick = { episode ->
            val videoUrl = episode.linkM3u8  // Lấy URL của tập phim
            binding.tvEpisode.text = episode.name

            Intent(requireContext(), MediaPlayerActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtra("url", videoUrl)  // Truyền URL vào intent
                startActivity(intent)
            }
        }
    }

    private fun setUpSuggestFilmOnClick(){
        suggestFilmAdapter.onClick = { film ->

        }
    }

    private fun setUpCount() {
        countViewModel.count.observe(viewLifecycleOwner) { count ->
            val initialCount = activity?.intent?.getIntExtra("count", 0) ?: 0
            binding.tvViews.text = initialCount.toString()
            Log.d("tvViews", count.toString())
        }
    }

    private fun setUpInfo(data: FilmDetail?) {
        binding.apply {
            tvEpisode.text = getString(R.string.defaultEpisode)
            tvFilmTitle.text = data?.name
            Log.d("tvFilmTitle", data?.originName.toString())
            tvTime.text = data?.time
            tvTotalEpisodes.text = data?.episodeTotal
        }
    }

    private fun setUpSuggestFilmRv() {
        binding.rvSuggest.apply {
            adapter = suggestFilmAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(HorizontalItemDecoration())
            hasFixedSize()
        }
    }

    private fun setUpRvEpisode() {
        binding.rvEpisode.apply {
            adapter = episodesAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
            hasFixedSize()
            addItemDecoration(HorizontalItemDecoration())

        }
    }
}