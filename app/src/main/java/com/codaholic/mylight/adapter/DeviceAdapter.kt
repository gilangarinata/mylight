package com.codaholic.mylight.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.Config
import com.codaholic.mylight.model.ResultItem
import com.codaholic.mylight.view.HomeFragmentDetail
import kotlinx.android.synthetic.main.item_device.view.*


class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.Holder> {
    private lateinit var items: List<ResultItem>
    private lateinit var context: Context
    private val VIEW_DOCUMENT: Int = 1
    private val VIEW_PROGRESS: Int = 2
    private lateinit var adapterCallback: AdapterCallback

    constructor(
        items: List<ResultItem>,
        context: Context,
        adapterCallback: AdapterCallback
    ) : super() {
        this.items = items
        this.context = context
        this.adapterCallback = adapterCallback
    }

    interface AdapterCallback {
        fun clickItem(item : ResultItem)
        fun switchToggle(item: ResultItem, lamp:Boolean)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_device,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.title.text = items[position].name
        holder.view.description.text = items[position].description
        holder.view.tv_hardware_id.text = items[position].hardware?.hardwareId ?: "Not Found"
        holder.view.sw_button.isChecked = items[position].hardware?.lamp ?: false

        holder.view.btn_delete.setOnClickListener {
            adapterCallback.clickItem(items[position])
        }

        holder.view.lyt_parent.setOnClickListener {
            var i = Intent(context,HomeFragmentDetail::class.java)
            i.putExtra(Config.HARDWARE_ID,items[position].hardware?.id)
            i.putExtra("HID",items[position].hardware?.hardwareId)
            context.startActivity(i)
        }

        holder.view.sw_button.setOnCheckedChangeListener { buttonView, isChecked -> adapterCallback.switchToggle(items[position],isChecked)}
    }
}