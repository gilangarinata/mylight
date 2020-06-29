package com.codaholic.mylight.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codaholic.mylight.R
import kotlinx.android.synthetic.main.activity_bt_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class bt_main : AppCompatActivity() {

    private var mConnectedThread: ConnectedThread? = null
    var bluetoothIn: Handler? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private val handlerState = 0
    private val recDataString = StringBuilder()
    private val BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt_main)

//        bluetoothIn = object : Handler() {
//            override fun handleMessage(msg: Message) {
//                if (msg.what == handlerState) {                                     //if message is what we want
//                    val readMessage =
//                        msg.obj as String // msg.arg1 = bytes from connect thread
//                        recDataString.append(readMessage) //keep appending to string until ~
//                        val endOfLineIndex: Int = recDataString.indexOf("~") // determine the end-of-line
//                        if (endOfLineIndex > 0) {                                           // make sure there data before ~
//                            var dataInPrint: String =
//                                recDataString.substring(0, endOfLineIndex) // extract string
//                            val nA = dataInPrint.indexOf("A")
//                            val nB = dataInPrint.indexOf("B")
//                            val nC = dataInPrint.indexOf("C")
//                            val nD = dataInPrint.indexOf("D")
//                            if (recDataString.get(0) == '#') //if it starts with # we know it is what we are looking for
//                            {
//                                val sensor0: String = recDataString.substring(nA + 1, nB)
//                                val sensor1: String = recDataString.substring(nB + 1, nC)
//                                val sensor2: String = recDataString.substring(nC + 1, nD)
//                            }
//                            recDataString.delete(0, recDataString.length) //clear all string data
//                            // strIncom =" ";
//                            dataInPrint = " "
//                        }
//                }
//            }
//        }

        btAdapter = BluetoothAdapter.getDefaultAdapter()

        bt_send.setOnClickListener {
            var paketdata = et_ssid.text.toString() + ":" + et_pswd.text.toString() + ":" + et_sn.text.toString() + ":" +
                    et_lon.text.toString() + ":" + et_lat.text.toString()
            mConnectedThread!!.write(paketdata)
        }
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        address = intent.getStringExtra("device_address")
        val device: BluetoothDevice = btAdapter!!.getRemoteDevice(address)
        try {
            btSocket = createBluetoothSocket(device)
        } catch (e: IOException) {
            Toast.makeText(baseContext, "Socket creation failed", Toast.LENGTH_LONG).show()
        }
        try {
            btSocket!!.connect()
        } catch (e: IOException) {
            try {
                btSocket!!.close()
            } catch (e2: IOException) {
            }
        }
        mConnectedThread = ConnectedThread(btSocket!!)
        mConnectedThread!!.start()
        mConnectedThread!!.write("x")
    }

    override fun onPause() {
        super.onPause()
        try {
            btSocket!!.close()
        } catch (e2: IOException) {
        }
    }

    private inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        override fun run() {
            val buffer = ByteArray(256)
            var bytes: Int
            while (true) {
                try {
                    bytes = mmInStream!!.read(buffer)
                    val readMessage = String(buffer, 0, bytes)
                    bluetoothIn!!.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget()
                } catch (e: IOException) {
                    break
                }
            }
        }
        //write method
        fun write(input: String) {
            val msgBuffer =
                input.toByteArray()
            try {
                mmOutStream!!.write(msgBuffer)
            } catch (e: IOException) {
                finish()
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }
}
