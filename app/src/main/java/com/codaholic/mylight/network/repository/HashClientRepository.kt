package com.codaholic.mylight.network.repository

import okhttp3.RequestBody
import java.util.*

class HashClientRepository {
    fun defaultHash(): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put("keys", "");
        return map
    }

    fun signUp(
        username: String,
        email: String,
        password: String,
        position: String
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put(ConfigNetwork.USERNAME, username);
        map.put(ConfigNetwork.EMAIL, email);
        map.put(ConfigNetwork.PASSWORD, password);
        map.put(ConfigNetwork.POSITION, position);
        return map
    }

    fun updateLamp(
        hardwareId: String,
        lamp: Boolean
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put(ConfigNetwork.HARDWAREID, hardwareId);
        map.put(ConfigNetwork.LAMP, lamp.toString());
        return map
    }

    fun editSchedule(
        minute: String,
        hour:String,
        day:String,
        brightness: String,
        userId: String
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put("minute", minute)
        map.put("hour", hour)
        map.put("day", day)
        map.put("brightness", brightness)
        map.put("userId", userId)
        return map
    }

    fun addSchedule(
        minute: String,
        hour:String,
        day:String,
        brightness: String,
        userId: String,
        hardwareId: String
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put("minute", minute)
        map.put("hour", hour)
        map.put("day", day)
        map.put("brightness", brightness)
        map.put("userId", userId)
        map.put("hardwareId", hardwareId)
        return map
    }

    fun updateBrigtness(
        hardwareId: String,
        brightness: String
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put(ConfigNetwork.HARDWAREID, hardwareId);
        map.put(ConfigNetwork.BRIGHTNESS, brightness);
        return map
    }

    fun login(
        username: String,
        password: String
    ): HashMap<String, String> {
        val map: HashMap<String, String> =
            HashMap()
        map.put(ConfigNetwork.USERNAME, username);
        map.put(ConfigNetwork.PASSWORD, password);
        return map
    }


    fun addDevice(
        name: String,
        description: String,
        hardwareId: String,
        userId:String
    ) : HashMap<String,String> {
        val map : HashMap<String,String> = HashMap()
        map.put(ConfigNetwork.NAME,name)
        map.put(ConfigNetwork.DESCRIPTION,description)
        map.put(ConfigNetwork.HARDWAREID,hardwareId)
        map.put(ConfigNetwork.USERID,userId)
        return map
    }
}