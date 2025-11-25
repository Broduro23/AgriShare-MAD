package com.example.semesterproject.models

data class Booking(
      val id: String = "",
      val machineId: String = "",
      val machineName: String = "",
      val machineImageUrl: String = "",
      val clientId: String = "",
      val clientName: String = "",
      val ownerId: String = "",
      val startDate: Long = 0L,
      val endDate: Long = 0L,
      val totalPrice: Double = 0.0,
      val status: String = "PENDING",
      val createdAt: Long = System.currentTimeMillis()) {

}