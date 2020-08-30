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
import com.codaholic.mylight.network.BaseEndPoin.Companion.login
import com.codaholic.mylight.network.BaseEndPoin.Companion.updateLamp
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.ClientHandleRepository
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.network.repository.MyLightClient
import com.google.gson.Gson
import java.util.*

class HomeViewModel : ViewModel(), BaseEndPoin {
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

    fun fetchDevice(): LiveData<ResponseAPI> {
        return myLightClient.getClient(
            getBaseUrl(),
            device + "/${prefManager.userId}",
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun deleteDevice(deviceId:String): LiveData<ResponseAPI> {
        return myLightClient.deleteClient(
            getBaseUrl(),
            device + "/$deviceId",
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun updateLamp(hardwareId:String, lamp : Boolean): LiveData<ResponseAPI> {
        return myLightClient.postStringClient(
            getBaseUrl(),
            updateLamp,
            HashClientRepository().updateLamp(hardwareId,lamp),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }



    fun processResponseFetchDevice(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseFetchDevice: ResponseFetchDevice = Gson().fromJson(
                        data,
                        ResponseFetchDevice::class.java
                    )
                    mainCallBack!!.responseFetchDeviceVM(responseFetchDevice)
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


    fun processResponseDeleteDevice(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseDeleteDevice: ResponseDeleteDevice = Gson().fromJson(
                        data,
                        ResponseDeleteDevice::class.java
                    )
                    mainCallBack!!.responseDeleteDeviceVM(responseDeleteDevice)
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

    fun processResponseUpdateLamp(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    mainCallBack!!.responseUpdateLampVM()
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
        fun responseFetchDeviceVM(responseFetchDevice: ResponseFetchDevice?)
        fun responseDeleteDeviceVM(responseDeleteDevice: ResponseDeleteDevice)
        fun responseUpdateLampVM()
    }
}
