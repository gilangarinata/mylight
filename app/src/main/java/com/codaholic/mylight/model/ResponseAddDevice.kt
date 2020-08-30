package com.codaholic.mylight.model

data class ResponseAddDevice(
	val createdDevice: CreatedDevice? = null,
	val message: String? = null,
	val hardwareId: String? = null
)

data class CreatedDevice(
	val name: String? = null,
	val id: String? = null
)

