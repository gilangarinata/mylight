package com.codaholic.mylight.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.codaholic.mylight.model.ResponseFetchDevice
import com.codaholic.mylight.model.ResultItem
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.utils.SpacingItemDecoration
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.HomeViewModel
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
        tv_name.setText(prefManager.username)
        tv_position.setText(prefManager.position)

        image2.setOnClickListener {
            prefManager.logout()
        }
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
            tv_device_count.setText(items.size.toString())
        }
    }

    override fun responseDeleteDeviceVM() {
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

    fun showProgress(isLoading:Boolean){
        if (isLoading) {
            swipe_layout.isRefreshing = true
        }else{
            swipe_layout.isRefreshing = false
        }
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
