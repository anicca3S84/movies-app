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

        setUpFlag()
        initializePlayer()
        adjustPlayerViewHeight()

        binding.imgBack.setOnClickListener{
            releasePlayer()
            finish()
        }
    }

    // Phương thức xử lý Intent mới khi Activity đã tồn tại
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            videoUrl = it.getStringExtra("url")  // Lấy URL mới từ Intent
            updatePlayerWithNewUrl() // Cập nhật lại player với URL mới
        }
    }

    // Cập nhật ExoPlayer với URL mới
    private fun updatePlayerWithNewUrl() {
        videoUrl?.let {
            player?.apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(it))) // Cập nhật MediaItem mới
                prepare() // Chuẩn bị lại Player
                playWhenReady = true
            }
        }
    }



    private fun adjustPlayerViewHeight() {
        val playerView = binding.playerView

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Khi xoay ngang (landscape), PlayerView chiếm toàn bộ chiều cao
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.imgBack.visibility = View.INVISIBLE

        } else {
            // Khi ở chế độ dọc (portrait), PlayerView có chiều cao cố định 300dp
            playerView.layoutParams.height = (300 * resources.displayMetrics.density).toInt()
            binding.imgBack.visibility = View.VISIBLE
        }

        // Cập nhật lại layout của PlayerView
        playerView.requestLayout()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Gọi lại khi thay đổi hướng màn hình
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
                playWhenReady = this@MediaPlayerActivity.playWhenReady
                prepare()
            }
            binding.playerView.player = player
        }
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
    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
            player = null
        }
    }
}