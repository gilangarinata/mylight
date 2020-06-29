package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseFetchDevice(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("count")
	val count: Int? = null
)

data class Hardware(

	@field:SerializedName("hardwareId")
	val hardwareId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("lamp")
	val lamp: Boolean? = null,

	@field:SerializedName("_id")
	val id: String? = null
)

data class ResultItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("user")
	val user: String? = null,

	@field:SerializedName("hardware")
	val hardware: Hardware? = null
)
