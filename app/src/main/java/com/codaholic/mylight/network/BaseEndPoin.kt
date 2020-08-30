package com.codaholic.mylight.network

interface BaseEndPoin {
    companion object {
        const val baseUrlProduction = "http://139.0.89.171:8000"
//        const val baseUrlDevelopment = "http://192.168.0.153:8000"
        const val baseUrlDevelopment = "http://mylamp.ddns.net"
//        const val baseUrlDevelopment = "http://192.168.1.32:3000"

        const val uploadImage = "/devices/upload"
        const val hardwareDetail = "/hardware"
        const val signUp = "/users/signup"
        const val login = "/users/login"
        const val device = "/devices"
        const val schedule = "/schedule"
        const val history = "/hardware/history"
        const val updateLamp = "/devices/update_lamp"
        const val updateBrightness = "/devices/update_brightness"

    }
}
