package com.daviddeer.daviddeer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.daviddeer.daviddeer.data.Beast
import com.daviddeer.daviddeer.data.BeastRepository
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import android.os.Build

class MapActivity : ComponentActivity(), AMapLocationListener, AMap.OnMarkerClickListener {
    private lateinit var mapView: MapView
    private var aMap: AMap? = null
    private var mLocationClient: AMapLocationClient? = null
    private var currentLocation: LatLng? = null
    private val generatedBeasts = mutableListOf<BeastMarker>()
    private var lastGeneratedTime: Long = 0
    private val generationCooldown = 30000 // 30 seconds cooldown
    private val random = Random()

    // Data class to hold beast and its marker
    private data class BeastMarker(val beast: Beast, val marker: Marker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Load unlocked state
        BeastRepository.loadUnlockedState(this)

        // Initialize map
        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)

        // Back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Generate beast button
        findViewById<Button>(R.id.generateBeastButton).setOnClickListener {
            generateBeastsInRange()
        }

        // Initialize map controller
        if (aMap == null) {
            aMap = mapView.map
            aMap?.uiSettings?.isZoomControlsEnabled = false
            aMap?.setOnMarkerClickListener(this)
            setupMap()
        }

        // Update privacy compliance
        AMapLocationClient.updatePrivacyShow(applicationContext, true, true)
        AMapLocationClient.updatePrivacyAgree(applicationContext, true)

        // Initialize location
        initLocation()
    }

    private fun initLocation() {
        try {
            mLocationClient = AMapLocationClient(applicationContext).apply {
                // 华为设备需要特殊设置
                if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)) {
                    setLocationOption(getHuaweiLocationOption())
                } else {
                    setLocationOption(getDefaultLocationOption())
                }
                setLocationListener(this@MapActivity)
                startLocation()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Location init failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDefaultLocationOption(): AMapLocationClientOption {
        return AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isOnceLocation = false
            interval = 5000
            isNeedAddress = true
            isWifiActiveScan = true
            isLocationCacheEnable = false
        }
    }

    private fun getHuaweiLocationOption(): AMapLocationClientOption {
        return AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
            isOnceLocation = false
            interval = 8000 // 华为设备建议稍长的间隔
            isNeedAddress = true
            isWifiActiveScan = true
            isLocationCacheEnable = true // 华为设备启用缓存
            httpTimeOut = 30000
        }
    }

    private fun setupMap() {
        aMap?.isMyLocationEnabled = true
        aMap?.uiSettings?.isMyLocationButtonEnabled = true
        aMap?.mapType = AMap.MAP_TYPE_NORMAL
    }

    override fun onLocationChanged(location: AMapLocation?) {
        if (location != null && location.errorCode == 0) {
            currentLocation = LatLng(location.latitude, location.longitude)

            if (generatedBeasts.isEmpty()) {
                show3kmRange()
            }

            checkBeastProximity()
        }
    }

    private fun show3kmRange() {
        currentLocation?.let {
            // 直接移动到当前位置并缩放到最大级别
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 19f))
        }
    }

    private fun generateBeastsInRange() {
        val now = System.currentTimeMillis()
        if (now - lastGeneratedTime < generationCooldown) {
            val remainingTime = (generationCooldown - (now - lastGeneratedTime)) / 1000
            Toast.makeText(this, "Please wait ${remainingTime}s before trying again", Toast.LENGTH_SHORT).show()
            return
        }

        currentLocation?.let { center ->
            clearGeneratedBeasts()

            val unlockedBeasts = BeastRepository.getBeasts().filter { it.isUnlocked }
            if (unlockedBeasts.isEmpty()) {
                Toast.makeText(this, "No unlocked beasts available", Toast.LENGTH_SHORT).show()
                return
            }

            val count = random.nextInt(3) + 1 // Generate 1-3 random beasts
            val radius = 2.0
            val earthRadius = 6371.0

            repeat(count) {
                val angle = random.nextDouble() * 2 * Math.PI
                val distance = random.nextDouble() * radius

                val dLat = distance * sin(angle) / earthRadius
                val dLon = distance * cos(angle) / (earthRadius * cos(Math.PI * center.latitude / 180))

                val beastPosition = LatLng(
                    center.latitude + dLat,
                    center.longitude + dLon
                )

                val randomBeast = unlockedBeasts.random()

                val marker = aMap?.addMarker(
                    MarkerOptions()
                        .position(beastPosition)
                        .title(randomBeast.name)
                        .icon(BitmapDescriptorFactory.fromResource(randomBeast.imageResId))
                )

                marker?.let {
                    generatedBeasts.add(BeastMarker(randomBeast, it))
                }
            }

            lastGeneratedTime = now
            Toast.makeText(this, "Generated $count beasts in your area!", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Getting location...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearGeneratedBeasts() {
        generatedBeasts.forEach { it.marker.remove() }
        generatedBeasts.clear()
    }

    private fun checkBeastProximity() {
        currentLocation?.let { userLocation ->
            generatedBeasts.forEach { beastMarker ->
                val beastLocation = beastMarker.marker.position
                val distance = calculateDistance(
                    userLocation.latitude, userLocation.longitude,
                    beastLocation.latitude, beastLocation.longitude
                )

                if (distance <= 0.005) { // Within 5 meters
                    showCaptureDialog(beastMarker.beast)
                    beastMarker.marker.remove()
                    generatedBeasts.remove(beastMarker)
                }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun showCaptureDialog(beast: Beast) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_capture_beast, null)

        dialogView.findViewById<ImageView>(R.id.dialogBeastImage).setImageResource(beast.imageResId)
        dialogView.findViewById<TextView>(R.id.dialogBeastName).text = beast.name
        dialogView.findViewById<TextView>(R.id.dialogBeastStory).text = beast.story

        if (beast.isCaptured) {
            // 已捕捉过的灵兽
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Already Captured")
                .setMessage("You've already captured ${beast.name} before!")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            // 未捕捉过的灵兽
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Congratulations!")
                .setMessage("You've captured ${beast.name}!")
                .setPositiveButton("OK") { dialog, _ ->
                    BeastRepository.captureBeast(beast.id)
                    BeastRepository.saveUnlockedState(this)
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        generatedBeasts.find { it.marker == marker }?.let { beastMarker ->
            showBeastInfo(beastMarker.beast)
            return true
        }
        return false
    }

    private fun showBeastInfo(beast: Beast) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_capture_beast, null)

        dialogView.findViewById<ImageView>(R.id.dialogBeastImage).setImageResource(beast.imageResId)
        dialogView.findViewById<TextView>(R.id.dialogBeastName).text = beast.name
        dialogView.findViewById<TextView>(R.id.dialogBeastStory).text = "This beast is nearby! Walk closer to capture it."

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(beast.name)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        mLocationClient?.onDestroy()
    }
}