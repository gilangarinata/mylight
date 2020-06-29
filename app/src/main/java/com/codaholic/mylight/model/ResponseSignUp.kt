package com.codaholic.mylight.model

import com.google.gson.annotations.SerializedName

data class ResponseSignUp(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userCreated")
	val userCreated: UserCreated? = null
)

data class UserCreated(

	@field:SerializedName("position")
	val position: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
