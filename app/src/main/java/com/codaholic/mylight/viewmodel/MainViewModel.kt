package com.codaholic.mylight.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.ResponseAddDevice
import com.codaholic.mylight.model.ResponseLogin
import com.codaholic.mylight.network.*
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlDevelopment
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlProduction
import com.codaholic.mylight.network.BaseEndPoin.Companion.device
import com.codaholic.mylight.network.BaseEndPoin.Companion.login
import com.codaholic.mylight.network.repository.ClientHandleRepository
import com.codaholic.mylight.network.repository.MyLightClient
import com.google.gson.Gson
import java.util.*

class MainViewModel : ViewModel(), BaseEndPoin {
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

    fun addDevice(hashMap: HashMap<String, String>?): LiveData<ResponseAPI> {
        return myLightClient.postStringClient(
            getBaseUrl(),
            device,
            hashMap,
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun processResponseAddDevice(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseAddDevice: ResponseAddDevice = Gson().fromJson(
                        data,
                        ResponseAddDevice::class.java
                    )
                    mainCallBack!!.responseAddDeviceVM(responseAddDevice)
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
        fun responseAddDeviceVM(responseAddDevice: ResponseAddDevice?)
    }
}
