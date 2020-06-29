package com.codaholic.mylight.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codaholic.mylight.R
import com.codaholic.mylight.model.ResponseSignUp
import com.codaholic.mylight.network.*
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlDevelopment
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlProduction
import com.codaholic.mylight.network.BaseEndPoin.Companion.signUp
import com.codaholic.mylight.network.repository.ClientHandleRepository
import com.codaholic.mylight.network.repository.MyLightClient
import com.google.gson.Gson
import java.util.*

class SignUpViewModel : ViewModel(), BaseEndPoin {
    private lateinit var context: Context
    private lateinit var callBackClient: CallBackClient
    private lateinit var mainCallBack: MainCallBack
    private lateinit var myLightClient: MyLightClient
    fun init(
        context: Context?,
        callBackClient: CallBackClient?,
        mainCallBack: MainCallBack?
    ) {
        this.context = context!!
        this.callBackClient = callBackClient!!
        this.mainCallBack = mainCallBack!!
        myLightClient = MyLightClient()
    }

    private fun getToken(): String {
        if (StateProduction().isProduction)
            return context!!.resources.getString(R.string.TOKEN_PRODUCTION)
        else
            return context!!.resources.getString(R.string.TOKEN_DEVELOPMENT)
    }

    private fun getBaseUrl(): String {
        if (StateProduction().isProduction) return baseUrlProduction else return baseUrlDevelopment
    }

    fun signUp(hashMap: HashMap<String, String>?): LiveData<ResponseAPI> {
        return myLightClient.postStringClient(
            getBaseUrl(),
            signUp,
            hashMap,
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun processResponseRegister(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseSignUp: ResponseSignUp = Gson().fromJson(
                        data,
                        ResponseSignUp::class.java
                    )
                    mainCallBack!!.responseSignUpVM(responseSignUp)
                    callBackClient.success("Success", 22)
                } else {
                    Log.d(
                        "REGISTER",
                        "processResponseRegister: " + responseAPI.data?.errorBody().toString() + " "
                    )
                    callBackClient.failed(responseAPI.data?.errorBody()?.string())
                }
            } catch (e: Exception) {
                callBackClient.error(e)
            }
            Status.LOADING -> callBackClient.loading()
            Status.ERROR -> try {
                callBackClient.errorConnection(responseAPI.error)
            } catch (e: Exception) {
                callBackClient.error(e)
            }
        }
    }

    interface MainCallBack {
        fun responseSignUpVM(responseSignUp: ResponseSignUp?)
    }
}
