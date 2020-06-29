package com.codaholic.mylight.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.Config
import com.codaholic.mylight.model.ResponseHardwareDetails
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.DetailHomeViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_home_fragment_detail.*


class HomeFragmentDetail : AppCompatActivity(), DetailHomeViewModel.MainCallBack, CallBackClient, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var hardwareId : String
    private lateinit var viewModel : DetailHomeViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mMap: GoogleMap

    private var latitude: Double = 114.21221
    private var longitude: Double = 21.21221


    private val handler: Handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            fetchHardwareDetail()
            handler.postDelayed(this, 5000)
        }
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_fragment_detail)
        hardwareId = intent.getStringExtra(Config.HARDWARE_ID)
        viewModel = ViewModelProviders.of(this).get(DetailHomeViewModel::class.java)
        viewModel.init(this,this,this)
        fetchHardwareDetail()
        initBsMap()
        initSlider()
        handler.postDelayed(runnable, 5000)

        btn_schedule.setOnClickListener {
            var i = Intent(this,ScheduleActivity::class.java)
            i.putExtra(Config.HARDWARE_ID, intent.getStringExtra("HID") )
            startActivity(i)
        }
    }



    fun fetchHardwareDetail(){
        viewModel.getHardwareDetail(hardwareId).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseHardwareDetail(t) })
    }

    fun updateBrightness(value : Int){
        viewModel.updateBrightness(intent.getStringExtra("HID"),value).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseUpdateBrightness(t) })
    }

    private fun initBsMap() {
        val llBottomSheet =
            findViewById<View>(R.id.bottom_sheet) as LinearLayout

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        })
    }

    private fun initSlider(){
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateBrightness(progressChangedValue)
            }
        })
    }

    private fun initMapFragment() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync { googleMap ->
            mMap = Tools().configActivityMaps(googleMap)!!
            val markerOptions =
                MarkerOptions().position(LatLng(latitude, longitude))
            mMap.addMarker(markerOptions)
            mMap.moveCamera(zoomingLocation())
            mMap.setOnMarkerClickListener(OnMarkerClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                try {
                    mMap.animateCamera(zoomingLocation())
                } catch (e: Exception) {
                }
                true
            })
        }
    }

    private fun zoomingLocation(): CameraUpdate? {
        return CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f)
    }

    override fun responseHardwareDetailVM(responseHardwareDetails: ResponseHardwareDetails) {

        tv_device_id.text = intent.getStringExtra("HID")
        responseHardwareDetails.result?.capacity?.let { tv_capacity.setText(it.toString()) }
        responseHardwareDetails.result?.chargingTime?.let { tv_charging_time.setText(it.toString()) }
        responseHardwareDetails.result?.dischargingTime?.let { tv_discharging_time.setText(it.toString()) }
        responseHardwareDetails.result?.betteryHealth?.let { tv_battery_health.setText(it.toString()) }
        responseHardwareDetails.result?.alarm?.let { tv_alarm.setText(it) }
        responseHardwareDetails.result?.latitude?.let { tv_latitude.setText(it) }
        responseHardwareDetails.result?.longitude?.let { tv_longitude.setText(it) }

        latitude = responseHardwareDetails.result?.latitude?.toDouble()?: 12.331
        longitude = responseHardwareDetails.result?.longitude?.toDouble()?: 133.021

        seekBar.progress = responseHardwareDetails.result?.brightness?: 0

        initMapFragment()
    }

    override fun responseUpdateBrightness() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loading() {

    }

    override fun success(message: String?, idsuccess: Int) {

    }

    override fun failed(message: String?) {
        if(message != null){
            Tools().showSnackbar(parent_layout, Status.FAILED,message).show()
        }
    }

    override fun errorConnection(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR_CONNECTION,it).show() }
        }
    }

    override fun error(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR,it).show() }
        }
    }


    override fun onRefresh() {
        fetchHardwareDetail()
    }
}
