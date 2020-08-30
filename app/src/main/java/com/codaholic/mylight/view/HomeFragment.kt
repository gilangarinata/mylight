package com.codaholic.mylight.view

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codaholic.mylight.R
import com.codaholic.mylight.adapter.DeviceAdapter
import com.codaholic.mylight.bluetooth.bt_available
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.ResponseDeleteDevice
import com.codaholic.mylight.model.ResponseFetchDevice
import com.codaholic.mylight.model.ResponseWeather
import com.codaholic.mylight.model.ResultItem
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.utils.SpacingItemDecoration
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.HomeViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(),HomeViewModel.MainCallBack,CallBackClient,DeviceAdapter.AdapterCallback, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var prefManager: PrefManager
    val items = arrayListOf<ResultItem>()
    private lateinit var adapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.init(context,this,this)
        prefManager = PrefManager(context!!)

        val layoutManager : GridLayoutManager = GridLayoutManager(context,2);
        adapter = DeviceAdapter(items,context!!,this)
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(SpacingItemDecoration(2,Tools().dpToPx(context!!,8),true))
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter

        ib_bluetooth.setOnClickListener {
            val intent = Intent(context, bt_available::class.java)
            startActivity(intent)
        }

        swipe_layout.setOnRefreshListener(this)

        initProfile()
        fetchDevice()
    }

    fun initProfile(){
        tv_name.text = prefManager.username
        tv_position.text = prefManager.position
        tv_referal.text = prefManager.referal

        image2.setOnClickListener {
            showDialogProfile()
        }
    }

    private fun showDialogProfile() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_profile)
        (dialog.findViewById<View>(R.id.layout_logout) as LinearLayout).setOnClickListener {
            prefManager.logout()
            getActivity()?.finish();
            startActivity(Intent(activity,DashboardActivity::class.java))
        }

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }



    fun fetchDevice(){
        items.clear()
        viewModel.fetchDevice().observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseFetchDevice(t) })
    }

    fun deleteDevice(deviceId:String){
        viewModel.deleteDevice(deviceId).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseDeleteDevice(t) })
    }

    fun updateLamp(hardwareId:String, lamp:Boolean){
        viewModel.updateLamp(hardwareId,lamp).observe(this, Observer<ResponseAPI> { t: ResponseAPI? ->  viewModel.processResponseUpdateLamp(t)})
    }

    fun refreshList() {
        fetchDevice()
    }

    override fun responseFetchDeviceVM(responseFetchDevice: ResponseFetchDevice?) {
        if (responseFetchDevice != null) {
            items.addAll(responseFetchDevice.result as Collection<ResultItem>)
            adapter.notifyDataSetChanged()
            tv_device_count.text = items.size.toString()
        }
    }

    override fun responseDeleteDeviceVM(responseDeleteDevice: ResponseDeleteDevice) {
        responseDeleteDevice.hardwareId?.let {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("seti-app-"+it)
                .addOnCompleteListener { task ->
                    var msg = "UNSubscribe Success"
                    if (!task.isSuccessful) {
                        msg = "UNSubscribe Failed"
                    }
                    Log.d("UNSUBSCRIBE", msg)
                }
        }
        fetchDevice()
    }

    override fun responseUpdateLampVM() {

    }

    override fun loading() {
        showProgress(true)
    }

    override fun success(message: String?, idsuccess: Int) {
        showProgress(false)
    }

    override fun failed(message: String?) {
        if(message != null){
            Tools().showSnackbar(parent_layout, Status.FAILED,message).show()
        }
        showProgress(false)
    }

    override fun errorConnection(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR_CONNECTION,it).show() }
        }
        showProgress(false)
    }

    override fun error(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR,it).show() }
        }
        showProgress(false)
    }

    private fun showProgress(isLoading:Boolean){
        swipe_layout.isRefreshing = isLoading
    }

    private fun showConfirmDialog(id:String) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setTitle("Are you sure want to delete this device?")
        builder.setPositiveButton("Delete",
            DialogInterface.OnClickListener { dialogInterface, i ->
                deleteDevice(id)
            })
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    override fun onRefresh() {
        fetchDevice()
    }

    override fun clickItem(item: ResultItem) {
        if (item.id != null) {
            showConfirmDialog(item.id)
        }
    }

    override fun switchToggle(item: ResultItem, lamp: Boolean) {
        item.hardware?.hardwareId?.let { updateLamp(it,lamp) }
    }
}
