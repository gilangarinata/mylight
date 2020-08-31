package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseDeleteDevice(

	@field:SerializedName("hardwareId")
	val hardwareId: String? = null,

	@field:SerializedName("scheduleDeleted")
	val scheduleDeleted: ScheduleDeleted? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Status? = null
)

data class Status(

	@field:SerializedName("deletedCount")
	val deletedCount: Int? = null,

	@field:SerializedName("ok")
	val ok: Int? = null,

	@field:SerializedName("n")
	val N: Int? = null
)

data class ScheduleDeleted(

	@field:SerializedName("deletedCount")
	val deletedCount: Int? = null,

	@field:SerializedName("ok")
	val ok: Int? = null,

	@field:SerializedName("n")
	val N: Int? = null
)
