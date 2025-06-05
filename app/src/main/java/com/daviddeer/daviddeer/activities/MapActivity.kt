package com.daviddeer.daviddeer.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import android.app.AlertDialog
import com.amap.api.maps2d.model.MyLocationStyle
import android.graphics.Color
import android.widget.ImageButton
import com.daviddeer.daviddeer.R

class MapActivity : ComponentActivity(), AMapLocationListener, AMap.OnMarkerClickListener {

    private lateinit var mapView: MapView
    private var aMap: AMap? = null
    private var mLocationClient: AMapLocationClient? = null
    private var currentLocation: LatLng? = null
    private val generatedBeasts = mutableListOf<BeastMarker>()
    private var lastGeneratedTime: Long = 0
    private val generationCooldown = 30000 // 30 seconds cooldown
    private val random = Random()
    private var isPermissionRequestInProgress = false
    private var isSatelliteMode = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isPermissionRequestInProgress = false

        when {
            // Case 1: Fine location permission granted
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                initLocation()
            }

            // Case 2: Coarse location permission granted (fallback)
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                initLocation()
            }

            // Case 3: All permissions denied
            else -> {
                // Only show guidance dialog if user didn't select "Don't ask again"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // Show permanent denial dialog explaining how to enable in settings
                    showPermissionDeniedDialog()
                } else {
                    // Show temporary denial message (user can try again)
                    Toast.makeText(
                        this,
                        "Location permission is required to use this feature",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Data class to hold beast and its marker
    private data class BeastMarker(val beast: Beast, val marker: Marker)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Load unlocked state
        BeastRepository.loadUnlockedState(this)

        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)

        // Back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Generate beast button
        findViewById<Button>(R.id.generateButton).setOnClickListener {
            if (hasLocationPermission()) {
                generateBeastsInRange()
            } else {
                requestLocationPermission()
            }
        }

        findViewById<ImageButton>(R.id.nightModeButton).setOnClickListener {
            toggleMapMode()
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

        // Check and request location permission
        checkLocationPermission()
    }

    /**
     * Toggles between standard and satellite map modes
     *
     * This method:
     * 1. Flips the current map mode state
     * 2. Updates the map display type
     * 3. Changes the mode toggle button icon
     * 4. Shows a status toast notification
     */
    private fun toggleMapMode() {
        // Toggle the satellite mode flag
        isSatelliteMode = !isSatelliteMode

        // Switch the actual map type
        aMap?.mapType = if (isSatelliteMode) {
            AMap.MAP_TYPE_SATELLITE  // Satellite map
        } else {
            AMap.MAP_TYPE_NORMAL     // Standard map
        }

        // Update the mode toggle button icon
        val modeToggleButton = findViewById<ImageButton>(R.id.nightModeButton)
        modeToggleButton.setImageResource(
            if (isSatelliteMode) R.drawable.ic_normal_map else R.drawable.ic_satellite_map
        )

        // Show mode change notification
        Toast.makeText(
            this,
            if (isSatelliteMode) "Satellite mode activated" else "Standard mode activated",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Checks and handles location permission status
     *
     * This function:
     * 1. Checks if permission is already granted
     * 2. Prevents duplicate permission requests
     * 3. Decides whether to show explanation dialog
     * 4. Initiates permission request when appropriate
     */
    private fun checkLocationPermission() {
        if (hasLocationPermission()) {
            initLocation()
            return
        }

        if (isPermissionRequestInProgress) {
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showPermissionExplanationDialog()
        } else {
            requestLocationPermission()
        }
    }

    /**
     * Displays an explanatory dialog about location permission requirements
     *
     * Shows a dialog that:
     * 1. Explains why location permission is needed
     * 2. Provides option to proceed with permission request
     * 3. Handles user's acceptance or rejection
     */
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs location permission to show beasts near you")
            .setPositiveButton("OK") { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    this,
                    "Feature limited without location",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }

    /**
     * Displays a dialog when location permission is permanently denied
     *
     * This dialog:
     * 1. Informs user about denial
     * 2. Provides direct access to app settings
     * 3. Handles user's decision to proceed or exit
     */
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("You've permanently denied location permission. Please enable it in settings to use this feature.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Opens the system settings page for this application
     */
    private fun openAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        })
    }

    /**
     * Checks if the app has either fine or coarse location permission
     *
     * @return Boolean - true if either location permission is granted, false otherwise
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests location permissions from the user
     */
    private fun requestLocationPermission() {
        isPermissionRequestInProgress = true
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Initializes the location service with appropriate configuration
     */
    private fun initLocation() {
        try {
            // Initialize AMap location client
            mLocationClient = AMapLocationClient(applicationContext).apply {
                // Special configuration for Huawei devices
                if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)) {
                    setLocationOption(getHuaweiLocationOption())
                } else {
                    // Apply standard location settings
                    setLocationOption(getDefaultLocationOption())
                }

                // Register this activity as location listener
                setLocationListener(this@MapActivity)

                startLocation()
            }
        } catch (e: Exception) {
            // Display error message
            Toast.makeText(
                this,
                "Location service initialization failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Creates and configures default location tracking parameters
     * @return AMapLocationClientOption - Configured location options with:
     */
    private fun getDefaultLocationOption(): AMapLocationClientOption {
        return AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isOnceLocation = false
            // Set update interval to 5000ms (5 seconds)
            interval = 5000
            isNeedAddress = true
            // Actively scan WiFi networks
            isWifiActiveScan = true
            // Disable location caching
            isLocationCacheEnable = false
        }
    }

    /**
     * Creates and configures Huawei-optimized location tracking parameters
     *
     * @return AMapLocationClientOption - Configured with Huawei-specific optimizations:
     */
    private fun getHuaweiLocationOption(): AMapLocationClientOption {
        return AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
            isOnceLocation = false
            interval = 8000
            isNeedAddress = true
            isWifiActiveScan = true
            isLocationCacheEnable = true
            httpTimeOut = 30000
        }
    }

    /**
     * Configures the map with custom location display settings
     *
     * This function:
     * 1. Creates a custom location marker style
     * 2. Applies the style to the map
     * 3. Enables location layer functionality
     */
    private fun setupMap() {
        aMap?.apply {
            // 1. Create custom location marker style
            val myLocationStyle = MyLocationStyle().apply {
                // Set custom marker icon
                myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.explorer))

                // Set location tracking mode
                myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)

                strokeColor(Color.TRANSPARENT)      // Transparent border
                radiusFillColor(Color.TRANSPARENT)  // Transparent fill

                // Set icon anchor point
                anchor(0.5f, 0.5f)
            }

            // 2. Apply the custom style
            setMyLocationStyle(myLocationStyle)

            // 3. Enable location layer functionality
            isMyLocationEnabled = true               // Show current location
            uiSettings.isMyLocationButtonEnabled = true // Show default location button
            uiSettings.isZoomControlsEnabled = true   // Show zoom controls
        }
    }

    /**
     * Handles location update events from AMap
     */
    override fun onLocationChanged(location: AMapLocation?) {
        // Only proceed with valid location data
        if (location != null && location.errorCode == 0) {
            // Store current coordinates
            currentLocation = LatLng(location.latitude, location.longitude)

            // Show detection range circle if no beasts have been generated yet
            if (generatedBeasts.isEmpty()) {
                showRange()
            }
            // Check distance to all nearby beasts
            checkBeastProximity()
        }
    }

    /**
     * Displays the detection range circle
     */
    private fun showRange() {
        currentLocation?.let { location ->
            // Move camera to current position with street-level zoom
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
        }
    }


    /**
     * Generates unlocked beasts within a specified range around the current location
     *
     * Features:
     * - Cooldown timer to prevent spamming
     * - Evenly distributed beast positions
     * - Random selection from unlocked beasts
     * - Visual feedback for generation status
     */
    private fun generateBeastsInRange() {
        // Check cooldown timer
        val now = System.currentTimeMillis()
        if (now - lastGeneratedTime < generationCooldown) {
            val remainingTime = (generationCooldown - (now - lastGeneratedTime)) / 1000
            Toast.makeText(this, "Please wait ${remainingTime}s before trying again", Toast.LENGTH_SHORT).show()
            return
        }

        // Only proceed if we have valid current location
        currentLocation?.let { center ->
            clearGeneratedBeasts()

            // Get all unlocked beasts from repository
            val unlockedBeasts = BeastRepository.getBeasts().filter { it.isUnlocked }
            if (unlockedBeasts.isEmpty()) {
                Toast.makeText(this, "No unlocked beasts available", Toast.LENGTH_SHORT).show()
                return
            }

            // Generate 1-3 random beasts
            val count = random.nextInt(3) + 1
            val earthRadius = 6371.0 // Earth's radius in km
            val minRadius = 2  // Minimum spawn distance (km)
            val maxRadius = 6  // Maximum spawn distance (km)

            // Calculate equal angle spacing
            val angleStep = 2 * Math.PI / count
            var baseAngle = random.nextDouble() * 2 * Math.PI // Random starting angle

            // Generate each beast position
            repeat(count) { index ->
                // Calculate position using polar coordinates
                val angle = baseAngle + angleStep * index
                val distance = getRandomDistance(minRadius, maxRadius) // Gaussian distribution

                // Convert polar to Cartesian coordinates
                val dLat = distance * sin(angle) / earthRadius
                val dLon = distance * cos(angle) / (earthRadius * cos(Math.PI * center.latitude / 180))

                // Create final position
                val position = LatLng(
                    center.latitude + dLat,
                    center.longitude + dLon
                )

                // Select random unlocked beast
                val randomBeast = unlockedBeasts.random()

                // Add marker to map
                val marker = aMap?.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(randomBeast.name)
                        .icon(BitmapDescriptorFactory.fromResource(randomBeast.imageResId))
                )

                // Store generated beast reference
                marker?.let {
                    generatedBeasts.add(BeastMarker(randomBeast, it))
                }
            }

            // Update cooldown timer
            lastGeneratedTime = System.currentTimeMillis()
            Toast.makeText(this, "Generated $count beasts in your area!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Generates random distances using Gaussian distribution within specified range
     */
    private fun getRandomDistance(minRadius: Int, maxRadius: Int): Double {
        var distance: Double
        do {
            // Generate Gaussian value with:
            // Mean = midpoint between min and max
            // StdDev = (max-min)/4 (so ~95% values within min-max)
            distance = random.nextGaussian() * (maxRadius - minRadius) / 4 +
                    (maxRadius + minRadius) / 2.0
        } while (distance < minRadius || distance > maxRadius)

        return distance
    }

    /**
     * Clears all generated beast markers from the map
     */
    private fun clearGeneratedBeasts() {
        // Remove each marker from the map
        generatedBeasts.forEach { beastMarker ->
            beastMarker.marker.remove()
        }

        // Clear the tracking list
        generatedBeasts.clear()
    }

    /**
     * Checks if user is close enough to capture any generated beasts
     */
    private fun checkBeastProximity() {
        currentLocation?.let { userLocation ->
            // Check each generated beast
            generatedBeasts.forEach { beastMarker ->
                val beastLocation = beastMarker.marker.position

                // Calculate distance
                val distance = calculateDistance(
                    userLocation.latitude, userLocation.longitude,
                    beastLocation.latitude, beastLocation.longitude
                )

                // Check if within capture range (5 meters)
                if (distance <= 0.005) {
                    showCaptureDialog(beastMarker.beast)

                    // Remove marker from map
                    beastMarker.marker.remove()
                    // Remove from tracking list
                    generatedBeasts.remove(beastMarker)
                }
            }
        }
    }

    /**
     * Calculates the distance between two creatures on Earth using the Haversine formula
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius

        // Convert coordinate differences to radians
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // Haversine formula components
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        // Calculate angular distance in radians
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Convert to kilometers
        return earthRadius * c
    }

    /**
     * Displays a capture dialog with beast details
     */
    private fun showCaptureDialog(beast: Beast) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_capture_beast, null)

        // Set beast details in dialog
        dialogView.findViewById<ImageView>(R.id.dialogBeastImage).setImageResource(beast.imageResId)
        dialogView.findViewById<TextView>(R.id.dialogBeastName).text = beast.name
        dialogView.findViewById<TextView>(R.id.dialogBeastStory).text = beast.story

        if (beast.isCaptured) {
            // Already captured beast - show informational dialog
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Already Captured")
                .setMessage("You've already captured ${beast.name} before!")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            // First-time capture - show celebration dialog
            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Congratulations!")
                .setMessage("You've captured ${beast.name}!")
                .setPositiveButton("OK") { dialog, _ ->
                    // Update state for new capture
                    BeastRepository.captureBeast(beast.id)
                    BeastRepository.saveAllStates(this)
                    dialog.dismiss()
                }
                .show()
        }
    }

    /**
     * Handles marker click events on the map
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        // Find the beast associated with clicked marker
        generatedBeasts.find { it.marker == marker }?.let { beastMarker ->
            // Show detailed info for the matched beast
            showBeastInfo(beastMarker.beast)
            return true
        }
        // No matching beast found
        return false
    }

    /**
     * Displays information about a nearby beast that hasn't been captured yet
     */
    private fun showBeastInfo(beast: Beast) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_capture_beast, null)

        // Set the beast's image and name
        dialogView.findViewById<ImageView>(R.id.dialogBeastImage).setImageResource(beast.imageResId)
        dialogView.findViewById<TextView>(R.id.dialogBeastName).text = beast.name

        // Display capture instructions
        dialogView.findViewById<TextView>(R.id.dialogBeastStory).text =
            "This beast is nearby! Walk closer to capture it."

        // Build and show the dialog
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(beast.name)
            .setPositiveButton("OK", null)
            .show()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()

        // Only check permissions if not currently requesting them
        if (!isPermissionRequestInProgress && !hasLocationPermission()) {
            checkLocationPermission()
        }
        // Re-enable location display
        aMap?.isMyLocationEnabled = true
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
        // Stop location updates when not visible
        mLocationClient?.stopLocation()
    }

    /**
     * Saves map state during configuration changes
     */
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