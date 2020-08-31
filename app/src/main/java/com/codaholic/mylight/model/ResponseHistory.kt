package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseHistory(

	@field:SerializedName("history")
	val history: List<HistoryItem?>? = null
)

data class HistoryItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("hardwareId")
	val hardwareId: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("batteryCapacity")
	val batteryCapacity: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("chargeCapacity")
	val chargeCapacity: Int? = null,

	@field:SerializedName("dischargeCapacity")
	val dischargeCapacity: Int? = null,

	@field:SerializedName("batteryLife")
	val batteryLife: Int? = null
)
