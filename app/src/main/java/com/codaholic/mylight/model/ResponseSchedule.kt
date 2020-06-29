package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseSchedule(

	@field:SerializedName("result")
	val result: List<ResultItem2>? = null,

	@field:SerializedName("count")
	val count: Int? = null
)

data class ResultItem2(

	@field:SerializedName("brightness")
	val brightness: Int? = null,

	@field:SerializedName("hour")
	val hour: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("day")
	val day: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("minute")
	val minute: String? = null
)
