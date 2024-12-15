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
    private var videoUrl: String? = null
    private var playbackPosition = 0L
    private val viewModel by viewModels<MediaPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUrl = intent.getStringExtra("url")
        viewModel.slug.value = intent.getStringExtra("slug")

        // Phục hồi trạng thái phát lại nếu có
        playbackPosition = savedInstanceState?.getLong("playbackPosition") ?: 0L
        playWhenReady = savedInstanceState?.getBoolean("playWhenReady") ?: true

        setUpFlag()
        initializePlayer()
        adjustPlayerViewHeight()

        binding.imgBack.setOnClickListener {
            releasePlayer()
            finish()
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
        if (player == null && videoUrl != null) {
            player = ExoPlayer.Builder(this).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                seekTo(playbackPosition) // Bắt đầu từ vị trí phát lại
                playWhenReady = this@MediaPlayerActivity.playWhenReady
                prepare()
            }
            binding.playerView.player = player
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition // Lưu vị trí phát lại
            playWhenReady = exoPlayer.playWhenReady // Lưu trạng thái đang phát
            exoPlayer.release()
            player = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition) // Lưu vị trí phát lại
        outState.putBoolean("playWhenReady", playWhenReady) // Lưu trạng thái đang phát
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}
