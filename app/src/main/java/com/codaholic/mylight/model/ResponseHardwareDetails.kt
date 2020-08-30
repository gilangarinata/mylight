package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseHardwareDetails(

	@field:SerializedName("result")
	val result: Result? = null
)

data class Result(

	@field:SerializedName("hardwareId")
	val hardwareId: String? = null,

	@field:SerializedName("chargingTime")
	val chargingTime: Int? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("alarm")
	val alarm: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("dischargingTime")
	val dischargingTime: Int? = null,

	@field:SerializedName("betteryHealth")
	val betteryHealth: Int? = null,

	@field:SerializedName("capacity")
	val capacity: Int? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("brightness")
	val brightness: Int? = null,

	@field:SerializedName("photoPath")
	val photoPath: String? = null
)
