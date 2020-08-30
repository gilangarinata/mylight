package com.codaholic.mylight.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codaholic.mylight.R
import com.codaholic.mylight.model.ResponseHardwareDetails
import com.codaholic.mylight.model.ResponseLogin
import com.codaholic.mylight.model.ResponseWeather
import com.codaholic.mylight.network.*
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlDevelopment
import com.codaholic.mylight.network.BaseEndPoin.Companion.baseUrlProduction
import com.codaholic.mylight.network.BaseEndPoin.Companion.hardwareDetail
import com.codaholic.mylight.network.BaseEndPoin.Companion.login
import com.codaholic.mylight.network.BaseEndPoin.Companion.updateBrightness
import com.codaholic.mylight.network.BaseEndPoin.Companion.uploadImage
import com.codaholic.mylight.network.repository.ClientHandleRepository
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.network.repository.MyLightClient
import com.codaholic.mylight.utils.Tools
import com.google.gson.Gson
import okhttp3.RequestBody
import java.util.*

class DetailHomeViewModel : ViewModel(), BaseEndPoin {
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


    //    http://api.openweathermap.org/data/2.5/weather?lat=-7.24566&lon=112.7829&appid=815168ce4992ad1ee04830a8556bedf9&units=metric

    fun getWeather(long : String, lat: String, appId : String): LiveData<ResponseAPI>{
        return myLightClient.getClient(
            "http://api.openweathermap.org",
            "/data/2.5/weather?lat=${lat}&lon=${long}&appid=${appId}&units=metric",
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }


    fun getHardwareDetail(hardwareId : String): LiveData<ResponseAPI> {
        return myLightClient.getClient(
            getBaseUrl(),
            hardwareDetail + "/$hardwareId" ,
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun uploadImage(hashMap: HashMap<String,RequestBody>): LiveData<ResponseAPI> {
        return myLightClient.postMultipathClient(
            getBaseUrl(),
            uploadImage,
            hashMap,
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun deleteImage(hardwareId : String): LiveData<ResponseAPI> {
        return myLightClient.deleteClient(
            getBaseUrl(),
            uploadImage + "/$hardwareId",
            HashClientRepository().defaultHash(),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun updateBrightness(hardwareId:String, brightness : Int): LiveData<ResponseAPI> {
        Log.d("HID", hardwareId)
        Log.d("BGT", brightness.toString())

        return myLightClient.postStringClient(
            getBaseUrl(),
            updateBrightness,
            HashClientRepository().updateBrigtness(hardwareId,brightness.toString()),
            ClientHandleRepository().medium(
                context,
                getToken()
            )
        )
    }

    fun processResponseWeather(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    val responseWeather: ResponseWeather = Gson().fromJson(
                        data,
                        ResponseWeather::class.java
                    )
                    mainCallBack!!.responseWeatherVM(responseWeather)
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


    fun processResponseUpdateBrightness(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    mainCallBack!!.responseUpdateBrightness()
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


    fun processResponseUploadImage(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    mainCallBack!!.responseUploadImage()
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

    fun processResponseDeleteImage(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    mainCallBack!!.responseDeleteImage()
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




    fun processResponseHardwareDetail(responseAPI: ResponseAPI?) {
        when (responseAPI?.status) {
            Status.SUCCESS -> try {
                if (responseAPI.data!!.isSuccessful()) {
                    val data: String = responseAPI.data.body()!!.string()
                    Log.d("DETAIL HARDWARE",data)
                    val responseHardwareDetails: ResponseHardwareDetails = Gson().fromJson(
                        data,
                        ResponseHardwareDetails::class.java
                    )
                    mainCallBack!!.responseHardwareDetailVM(responseHardwareDetails)
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
        fun responseHardwareDetailVM(responseHardwareDetails: ResponseHardwareDetails)
        fun responseUpdateBrightness()
        fun responseUploadImage()
        fun responseDeleteImage()
        fun responseWeatherVM(responseWeather: ResponseWeather)
    }
}
