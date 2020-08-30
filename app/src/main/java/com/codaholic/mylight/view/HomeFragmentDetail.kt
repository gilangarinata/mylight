package com.codaholic.mylight.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.Config
import com.codaholic.mylight.model.ResponseHardwareDetails
import com.codaholic.mylight.model.ResponseWeather
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ProgressResponseBody
import com.codaholic.mylight.network.ProgressResponseBody.UploadCallbacks
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.utils.GlideLoadHandle
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
import com.mlsdev.rximagepicker.RxImageConverters
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import kotlinx.android.synthetic.main.activity_home_fragment_detail.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.util.*


class HomeFragmentDetail : AppCompatActivity(), DetailHomeViewModel.MainCallBack, CallBackClient, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var hardwareId : String
    private lateinit var viewModel : DetailHomeViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mMap: GoogleMap

    private var latitude: Double = 114.21221
    private var longitude: Double = 21.21221
    private var stateInitMap: Boolean = false
    private lateinit var dialogUpload : Dialog
    private lateinit var imagePath : String
    private val appIdOpenWeather = "815168ce4992ad1ee04830a8556bedf9";


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
        initDialogPercentage()
        handler.postDelayed(runnable, 5000)

        btn_schedule.setOnClickListener {
            var i = Intent(this,ScheduleActivity::class.java)
            i.putExtra(Config.HARDWARE_ID, intent.getStringExtra("HID") )
            startActivity(i)
        }

        fab_directions.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            try {
                mMap.animateCamera(zoomingLocation())
            } catch (e: java.lang.Exception) {
            }
        }

        back.setOnClickListener { finish() }

        btn_add_image.setOnClickListener {
            showDialogMedia()
        }

        device_image.setOnLongClickListener {
            showDialogDelete()
            true
        }

        device_image.setOnClickListener {
            var i = Intent(this,ShowImageFullscreen::class.java)
            i.putExtra("URL",imagePath)
            startActivity(i)
        }

        btn_history.setOnClickListener {
            var i = Intent(this,HistoryActivity::class.java)
            i.putExtra("HID",intent.getStringExtra("HID"))
            startActivity(i)
        }
    }

    fun fetchWeather(long:String,lat:String){
        viewModel.getWeather(long,lat,appIdOpenWeather).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseWeather(t) })
    }

    fun deleteImage(){
        viewModel.deleteImage(intent.getStringExtra("HID")).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseDeleteImage(t) })
    }

    fun fetchHardwareDetail(){
        viewModel.getHardwareDetail(hardwareId).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseHardwareDetail(t) })
    }

    fun updateBrightness(value : Int){
        viewModel.updateBrightness(intent.getStringExtra("HID"),value).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseUpdateBrightness(t) })
    }



    fun uploadImage(image : File){
        var reqPic : RequestBody? = null
        if(image != null){
            reqPic = ProgressResponseBody(image, object : UploadCallbacks {
                override fun onProgressUpdate(percentage: Int) {
                    (dialogUpload.findViewById<View>(R.id.tv_percentage) as TextView).text = percentage.toString() + " %"
                }
                override fun onError() {
                    dialogUpload.dismiss()
                    Tools().showSnackbar(parent_layout, Status.FAILED,"Upload failed").show()
                }
                override fun onFinish() {
                    dialogUpload.dismiss()
                    fetchHardwareDetail()
                }
            })

            val reqHardwareId: RequestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                intent.getStringExtra("HID")
            )

            var hashMap : HashMap<String, RequestBody> = HashClientRepository().uploadImage(
                reqHardwareId, reqPic, image.name
            )
            viewModel.uploadImage(hashMap).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseUploadImage(t) })
        }



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
                try {
                    mMap.animateCamera(zoomingLocation())
                    showDialogMapFull()
                } catch (e: Exception) {
                }
                true
            })
        }
    }

    private fun zoomingLocation(): CameraUpdate? {
        return CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f)
    }

    private fun showDialogMapFull() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_map)
        (dialog.findViewById<View>(R.id.layout_open_maps) as LinearLayout).setOnClickListener {
            var uri = String.format(Locale.ENGLISH,"geo:%f,%f",latitude,longitude)
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(uri)))
        }
        (dialog.findViewById<View>(R.id.layout_add_image) as LinearLayout).setOnClickListener {
            dialog.dismiss()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showDialogDelete() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_delete)
        (dialog.findViewById<View>(R.id.layout_delete) as LinearLayout).setOnClickListener {
            dialog.dismiss()
            deleteImage()
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showDialogMedia() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_media)
        (dialog.findViewById<View>(R.id.layout_open_gallery) as LinearLayout).setOnClickListener {
            dialog.dismiss()
            getImagePicker(Sources.GALLERY)
        }
        (dialog.findViewById<View>(R.id.layout_take_photo) as LinearLayout).setOnClickListener {
            dialog.dismiss()
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicture,0)

        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun initDialogPercentage() {
        dialogUpload = Dialog(this)
        dialogUpload.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialogUpload.setContentView(R.layout.dialog_percentage)
            dialogUpload.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogUpload.setCancelable(false)
    }

    private fun getImagePicker(source: Sources){
        RxImagePicker.with(supportFragmentManager).requestImage(source, "Pick Image")
            .flatMap { uri -> createTempFile()?.let {
                RxImageConverters.uriToFile(this@HomeFragmentDetail, uri,
                    it
                )
            } }
            .subscribe({
                if(it != null){
                    uploadImage(it)
                    dialogUpload.show()
                }
            }, {
                Tools().showSnackbar(parent_layout, Status.FAILED,java.lang.String.format("Error Image Picker: %s", it)).show()
            })


//        RxImagePicker.with(fragmentManager).requestImage(source)
//            .flatMap(
//                Function<Uri?, ObservableSource<File>> { uri: Uri? ->
//                    RxImageConverters.uriToFile(
//                        applicationContext,
//                        uri,
//                        createTempFile()
//                    )
//                })
//            .subscribe { file: File? ->
//                if(file != null){
//                    uploadImage(file)
//                    dialogUpload.show()
//                }
//            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            0 -> {
                if (resultCode == Activity.RESULT_OK) {
                    var selectedImage: Uri = data!!.data
                    uploadImage(File(selectedImage.path))
                    dialogUpload.show()
                }
            }
        }
    }

    private fun createTempFile(): File? {
        return File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "mypics.jpeg"
        )
    }



    override fun responseHardwareDetailVM(responseHardwareDetails: ResponseHardwareDetails) {
        tv_device_id.text = intent.getStringExtra("HID")
        responseHardwareDetails.result?.capacity?.let { tv_capacity.setText(it.toString()) }
        responseHardwareDetails.result?.chargingTime?.let { tv_charging_time.setText(it.toString()) }
        responseHardwareDetails.result?.dischargingTime?.let { tv_discharging_time.setText(it.toString()) }
        responseHardwareDetails.result?.betteryHealth?.let { tv_battery_health.setText(it.toString()) }
        responseHardwareDetails.result?.alarm?.let {
            if(it.length > 0){
                circle_notif.visibility = View.VISIBLE
            }else{
                circle_notif.visibility = View.GONE
            }
        }
        responseHardwareDetails.result?.latitude?.let { tv_latitude.setText(it) }
        responseHardwareDetails.result?.longitude?.let { tv_longitude.setText(it) }

        latitude = responseHardwareDetails.result?.latitude?.toDouble()?: 12.331
        longitude = responseHardwareDetails.result?.longitude?.toDouble()?: 133.021

        seekBar.progress = responseHardwareDetails.result?.brightness?: 0

        if(!stateInitMap){
            initMapFragment()
            stateInitMap = true
        }

        if(responseHardwareDetails.result!!.photoPath == null){
            cv_image.visibility = View.GONE
            btn_add_image.visibility = View.VISIBLE
        }else{
            cv_image.visibility = View.VISIBLE
            btn_add_image.visibility = View.GONE
            GlideLoadHandle(this).intoCacheNews(responseHardwareDetails.result.photoPath,device_image)
            imagePath = responseHardwareDetails.result.photoPath.toString()
        }

        fetchWeather(longitude.toString(),latitude.toString())
    }

    override fun responseUpdateBrightness() {

    }

    override fun responseUploadImage() {
        dialogUpload.dismiss()
    }

    override fun responseDeleteImage() {
        fetchHardwareDetail()
    }

    override fun responseWeatherVM(responseWeather: ResponseWeather) {
        try {
            suhu.text = responseWeather.main?.temp.toString()
            location.text = responseWeather.name
        }catch (e : java.lang.Exception){

        }
    }

    override fun loading() {

    }

    override fun success(message: String?, idsuccess: Int) {
        status_info.setCardBackgroundColor(resources.getColor(R.color.green_600))
    }

    override fun failed(message: String?) {
        dialogUpload.dismiss()
        if(message != null){
            Tools().showSnackbar(parent_layout, Status.FAILED,message).show()
        }
    }

    override fun errorConnection(t: Throwable?) {
        dialogUpload.dismiss()
        status_info.setCardBackgroundColor(resources.getColor(R.color.red_600))
    }

    override fun error(t: Throwable?) {
        dialogUpload.dismiss()
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR,it).show() }
        }
    }


    override fun onRefresh() {
        fetchHardwareDetail()
    }
}
