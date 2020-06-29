package com.codaholic.mylight.network

interface BaseEndPoin {
    companion object {
        const val baseUrlProduction = "http://104.43.171.129"
        const val baseUrlDevelopment = "http://192.168.0.153:3000"
//        const val baseUrlDevelopment = "http://192.168.1.6:3000"

        const val hardwareDetail = "/hardware"
        const val signUp = "/users/signup"
        const val login = "/users/login"
        const val device = "/devices"
        const val schedule = "/schedule"
        const val updateLamp = "/devices/update_lamp"
        const val updateBrightness = "/devices/update_brightness"

    }
}
