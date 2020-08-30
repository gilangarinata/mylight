package com.codaholic.mylight.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.*
import com.codaholic.mylight.network.*
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlDevelopment
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlProduction
import com.codaholic.mylight.network.BaseEndPoin.Companion.device
import com.codaholic.mylight.network.BaseEndPoin.Companion.history
import com.codaholic.mylight.network.BaseEndPoin.Companion.login
import com.codaholic.mylight.network.BaseEndPoin.Companion.schedule
import com.codaholic.mylight.network.BaseEndPoin.Companion.updateLamp
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.ClientHandleRepository
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.network.repository.MyLightClient
import com.google.gson.Gson
import java.util.*

class HistoryViewModel : ViewModel(), BaseEndPoin {
    private lateinit var context: Context
    private lateinit var callBackClient: CallBackClient
    private lateinit var mainCallBack: MainCallBack
    private lateinit var myLightClient: MyLightClient
    private lateinit var prefManager: PrefManager

    fun init(
        context: Context?,
        callBackClient: CallBackClient?,
        mainCallBack: MainCallBack?
    ) {
        this.context = context!!
        this.callBackClient = callBackClient!!
        this.mainCallBack = mainCallBack!!
        myLightClient = MyLightClient()
        prefManager = PrefManager(context)
    }

    private fun getToken(): String {
        return prefManager.token
    }

    private fun getBaseUrl(): String {
        if (StateProduction().isProduction) return baseUrlProduction else return baseUrlDevelopment
    }

    fun fetchHistory(hardwareId : String): LiveData<ResponseAPI> {
        return myLightClient.getClient(
            getBaseUrl(),
            history + "/${hardwareId}",
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }


    fun processResponseFetchHistory(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseHistory: ResponseHistory = Gson().fromJson(
                        data,
                        ResponseHistory::class.java
                    )
                    mainCallBack!!.responseFetchHistoryVM(responseHistory)
                    callBackClient.success("Success", 22)
                } else {
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
        fun responseFetchHistoryVM(responseHistory: ResponseHistory)
    }
}
