package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("userInfo")
	val userInfo: UserInfo? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class UserInfo(

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("position")
	val position: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
