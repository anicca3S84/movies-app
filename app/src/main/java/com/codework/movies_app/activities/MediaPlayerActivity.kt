package com.codework.movies_app.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.codework.movies_app.R
import com.codework.movies_app.data.Episode
import com.codework.movies_app.databinding.ActivityMediaPlayerBinding
import com.codework.movies_app.fragments.main_fragment.MediaPlayerFragment
import com.codework.movies_app.viewmodes.MediaPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaPlayerBinding
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var playbackPosition = 0L
    private var videoUrl: String? = null
    private val viewModel by viewModels<MediaPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUrl = intent.getStringExtra("url")
        viewModel.slug.value = intent.getStringExtra("slug")

        setUpFlag()
        initializePlayer()
        adjustPlayerViewHeight()

        binding.imgBack.setOnClickListener {
            releasePlayer()
            finish()
        }
    }

    // Handle new intent for switching episodes
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            videoUrl = it.getStringExtra("url")
            restartPlayerForNewEpisode() // Restart the player for the new episode
        }
    }

    // Restart the player for a new episode
    private fun restartPlayerForNewEpisode() {
        videoUrl?.let {
            playbackPosition = 0L // Reset playback position to the beginning
            player?.apply {
                clearMediaItems() // Clear previous episode's media items
                setMediaItem(MediaItem.fromUri(Uri.parse(it))) // Set new episode
                seekTo(playbackPosition) // Start from the beginning
                playWhenReady = true // Auto-play new episode
                prepare() // Prepare the player
            }
        }
    }

    private fun adjustPlayerViewHeight() {
        val playerView = binding.playerView
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.imgBack.visibility = View.INVISIBLE
        } else {
            playerView.layoutParams.height = (300 * resources.displayMetrics.density).toInt()
            binding.imgBack.visibility = View.VISIBLE
        }
        playerView.requestLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustPlayerViewHeight()
    }

    private fun setUpFlag() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this).build().apply {
                videoUrl?.let {
                    setMediaItem(MediaItem.fromUri(Uri.parse(it)))
                }
                seekTo(playbackPosition) // Resume from saved position
                playWhenReady = this@MediaPlayerActivity.playWhenReady
                prepare()
            }
            binding.playerView.player = player
        }
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition // Save playback position
            playWhenReady = it.playWhenReady // Save play state
            it.release()
            player = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            initializePlayer() // Reinitialize if player is null
        }
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        } else {
            player?.playWhenReady = playWhenReady
        }
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            playbackPosition = it.currentPosition // Save current playback position
            playWhenReady = it.playWhenReady // Save whether video was playing
        }
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}


