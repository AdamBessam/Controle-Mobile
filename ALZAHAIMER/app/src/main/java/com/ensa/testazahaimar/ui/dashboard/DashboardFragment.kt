package com.ensa.testazahaimar.ui.dashboard

import android.animation.AnimatorSet
import android.content.Intent
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.ensa.testazahaimar.R
import com.ensa.testazahaimar.ui.MapActivity.CurrentLocationActivity
import com.ensa.testazahaimar.ui.MapActivity.HomeLocationActivity
import com.ensa.testazahaimar.ui.MapActivity.HospitalLocationActivity

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var currentButton: View
    private lateinit var hospitalButton: View
    private lateinit var homeButton: View
    private lateinit var imageCurrentLocation: View  // Assuming it's an ImageView or similar
    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize buttons and image
        currentButton = view.findViewById(R.id.Current)
        hospitalButton = view.findViewById(R.id.hospital)
        homeButton = view.findViewById(R.id.Home)
        imageCurrentLocation = view.findViewById(R.id.imageCurrentLocation) // Image for location

        // Set listeners for buttons
        currentButton.setOnClickListener {
            startActivity(Intent(requireContext(), CurrentLocationActivity::class.java))
        }

        hospitalButton.setOnClickListener {
            startActivity(Intent(requireContext(), HospitalLocationActivity::class.java))
        }

        homeButton.setOnClickListener {
            startActivity(Intent(requireContext(), HomeLocationActivity::class.java))
        }

        // Start vibration animations for both elements
        startVibrationAnimation(currentButton)
        startVibrationAnimation(hospitalButton)
        startVibrationAnimation(homeButton)

    }

    private fun startVibrationAnimation(view: View) {
        // Set up combined vibration animation in both X and Y axes
        val animatorX = ObjectAnimator.ofFloat(view, "translationX", -10f, 10f).apply {
            duration = 50 // Speed of each vibration (ms)
            repeatCount = 10 // 3 seconds total (30 * 50ms)
            repeatMode = ObjectAnimator.REVERSE
        }

        val animatorY = ObjectAnimator.ofFloat(view, "translationY", -10f, 10f).apply {
            duration = 50
            repeatCount = 10
            repeatMode = ObjectAnimator.REVERSE
        }

        // Animator set to play X and Y animations together for a circular effect
        val animatorSet = AnimatorSet().apply {
            playTogether(animatorX, animatorY)
        }

        // Runnable to handle vibration loop with pause
        val vibrationRunnable = object : Runnable {
            override fun run() {
                animatorSet.start()
                handler.postDelayed(this, 6000) // 3 sec vibrate + 5 sec pause
            }
        }

        // Start the initial cycle
        handler.post(vibrationRunnable)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}
