package com.example.semesterproject.models

data class Machine(
    val name: String = "",
    val machineType: String = "",
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    val ownerFirstName: String = "",
    val ownerLastName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = ""
)
