package com.codaholic.mylight.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codaholic.mylight.R
import com.codaholic.mylight.view.LoginActivity
import kotlinx.android.synthetic.main.activity_bt_available.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class bt_available : AppCompatActivity() {
    val mDeviceList = ArrayList<String>()
    lateinit var mBluetoothAdapter: BluetoothAdapter
    var EXTRA_ADDRESS = "device_address"
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    lateinit var pairedDevices:Set<BluetoothDevice>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt_available)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if(mBluetoothAdapter.isEnabled==false){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,1)
        }

        mBluetoothAdapter.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        pairedDevicesList()
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND ->{
                    val device:BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    mDeviceList.add(device.name + "\n" + device.address)
//                    lv_available!!.adapter = ArrayAdapter(context,android.R.layout.simple_list_item_1, mDeviceList)
//                    lv_available.onItemClickListener = myListClickListener
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    private inner class ConnectingThread(device: BluetoothDevice):Thread(){
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
            device.createRfcommSocketToServiceRecord(uuid)
        }

        public override fun run() {
            mBluetoothAdapter.cancelDiscovery()
            mmSocket?.use { socket ->
                socket.connect()
//                manageMyConnectedSocket(socket)
            }
        }
        fun cancel(){
            try {
                mmSocket?.close()
            }catch (e:IOException){
                Log.e("TAG","Could not close client socket",e)
            }
        }
    }

    private val myListClickListener =
        OnItemClickListener { av, v, arg2, arg3 ->
            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)
            val bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address)
            val t = ConnectingThread(bluetoothDevice)
            t.start()
        }

    private fun pairedDevicesList() {
        pairedDevices=mBluetoothAdapter.bondedDevices
        val list = ArrayList<String>()
        pairedDevices?.forEach { device ->
            list.add(device.name + "\n" + device.address)
            lv_paired!!.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, list)
            lv_paired.onItemClickListener = myListPairedClickListener
        }
    }

    private val myListPairedClickListener =
        OnItemClickListener { av, v, arg2, arg3 ->
            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)
            val i = Intent(this, bt_main::class.java)
            i.putExtra("device_address", address) //this will be received at ledControl (class) Activity
            startActivity(i)
        }
}
