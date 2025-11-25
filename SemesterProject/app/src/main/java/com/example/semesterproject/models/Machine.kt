package com.example.semesterproject.models

data class Machine(
    val id: String = "",
    val name: String = "",
    val machineType: String = "",
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    val ownerFirstName: String = "",
    val ownerLastName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = "",
    val ownerId: String = "",  // ADD THIS
    val createdAt: com.google.firebase.Timestamp? = null  // ADD THIS
)
