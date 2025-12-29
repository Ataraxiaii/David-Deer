package com.daviddeer.daviddeer.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.fragments.StatsFragment
import com.daviddeer.daviddeer.fragments.TodayFragment
import com.daviddeer.daviddeer.service.StepsSensorService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StepsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // 1. Setup ViewPager adapter to manage fragment switching
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TodayFragment()
                    else -> StatsFragment()
                }
            }
        }

        // 2. Bind TabLayout and ViewPager2 using TabLayoutMediator
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "TODAY" else "STATS"
        }.attach()

        // 3. Back button logic to return to the previous screen
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 4. Setup Help button to show the instruction dialog
        findViewById<ImageView>(R.id.btnHelp).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_help, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Set background to transparent to allow custom rounded corners from XML to show
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        // 5. Check necessary permissions and initiate the step tracking service
        checkPermissionAndStartService()
    }

    /**
     * Handles runtime permission checks for Activity Recognition (required for Android 10+)
     */
    private fun checkPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not already granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1001
                )
            } else {
                startStepService()
            }
        } else {
            // Permission is implicitly granted on older Android versions
            startStepService()
        }
    }

    /**
     * Starts the background service for step tracking as a foreground service for stability
     */
    private fun startStepService() {
        val intent = Intent(this, StepsSensorService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handles the user's response to the permission request dialog
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to start service
                startStepService()
            } else {
                // Permission denied, inform the user via Toast
                Toast.makeText(this, "Permission denied. Steps cannot be tracked.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}