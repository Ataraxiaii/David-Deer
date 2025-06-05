package com.daviddeer.daviddeer.data

// Data structure for the beast
data class Beast(
    val id: Int,                 // Unique ID
    val name: String,            // Beast name
    val imageResId: Int,         // Image resource ID
    val story: String,           // Beast story content
    var isUnlocked: Boolean = false,   // Whether the beast is unlocked by the game (shows colored image)
    var isCaptured: Boolean = false    // Whether the beast is captured on the map (shows name and story)
)