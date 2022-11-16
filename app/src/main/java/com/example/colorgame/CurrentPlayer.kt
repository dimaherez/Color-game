package com.example.colorgame

import java.io.Serializable

data class CurrentPlayer(
    var name: String? = null,
    var currentScore: Int = 0
): Serializable