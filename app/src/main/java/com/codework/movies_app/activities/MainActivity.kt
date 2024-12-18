package com.codework.movies_app.activities

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.codework.movies_app.R
import com.codework.movies_app.databinding.ActivityMainBinding
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.viewmodes.NotificationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val notificationViewModel : NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        setContentView(binding.root)
        val navController = findNavController(R.id.mainNavigationHost)
        binding.bottomNavigation.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
            when (destination.id) {
                R.id.homeFragment -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    bottomNavigationView?.visibility = View.VISIBLE
                }
                R.id.filmDetailFragment -> {
                    bottomNavigationView?.visibility = View.GONE
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
                R.id.searchFragment -> {
                    bottomNavigationView?.visibility = View.GONE
                }

                else -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    bottomNavigationView?.visibility = View.VISIBLE
                }
            }
        }
        observeNotificationDataMainActivity()
        notificationViewModel.refreshNotifications()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeNotificationDataMainActivity() {
        lifecycleScope.launch {
            notificationViewModel.notification.collectLatest { resource ->
                when(resource) {
                    is Resource.Loading -> {
                        updateNotificationBadge(0)
                    }
                    is Resource.Success -> {
                        val notificationCount = resource.data?.size ?: 0
                        updateNotificationBadge(notificationCount)
                        Log.d("notificationCount: ", notificationCount.toString())
                    }
                    is Resource.Error -> {
                        updateNotificationBadge(0)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun updateNotificationBadge(notificationCount: Int) {
        val badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.notificationFragment)

        if (notificationCount > 0) {
            badgeDrawable.isVisible = true
            badgeDrawable.number = notificationCount
        } else {
            badgeDrawable.isVisible = false
        }
//        val notificationItem = binding.bottomNavigation.menu.findItem(R.id.notificationFragment)
//
//        if (notificationCount > 0) {
//            Log.d("NotificationBadge", "Action View: ${notificationItem.actionView?.isVisible}")
//            // Show badge with notification count
//            notificationItem.actionView = layoutInflater.inflate(R.layout.layout_notification_badge, null)
//            val badgeTextView = notificationItem.actionView?.findViewById<TextView>(R.id.badge_count)
//            badgeTextView?.apply {
//                text = notificationCount.toString()
//                visibility = View.VISIBLE // Explicitly set to VISIBLE
//            }
//            notificationItem.actionView?.isVisible = true
//            Log.d("BadgeVisibility", "TextView: ${badgeTextView?.visibility}, Text: ${badgeTextView?.text}")
//        } else {
//            // Hide badge
//            notificationItem.actionView?.isVisible = false
//        }
    }


}