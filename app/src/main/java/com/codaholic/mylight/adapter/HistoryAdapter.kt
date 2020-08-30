package com.codaholic.mylight.adapter

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.Config
import com.codaholic.mylight.model.HistoryItem
import com.codaholic.mylight.model.ResultItem2
import com.codaholic.mylight.view.HomeFragmentDetail
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_history.view.*
import kotlinx.android.synthetic.main.item_schedule.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.Holder> {
    private lateinit var items: List<HistoryItem>
    private lateinit var context: Context
    private val VIEW_DOCUMENT: Int = 1
    private val VIEW_PROGRESS: Int = 2
    private lateinit var adapterCallback: AdapterCallback

    constructor(
        items: List<HistoryItem>,
        context: Context,
        adapterCallback: AdapterCallback
    ) : super() {
        this.items = items
        this.context = context
        this.adapterCallback = adapterCallback
    }

    interface AdapterCallback {
        fun clickItem(item : ResultItem2)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_history,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val date = LocalDate.parse(items[position].date?.substring(0,10), DateTimeFormatter.ISO_DATE)

        holder.view.date.text = date.dayOfMonth.toString()
        holder.view.month.text = date.month.toString().substring(0,3).toUpperCase()
        holder.view.tv_charge_capacity.text = items[position].chargeCapacity.toString() + " %"
        holder.view.tv_discharge_capacity.text = items[position].dischargeCapacity.toString() + " %"
        holder.view.tv_battery_capacity.text = items[position].batteryCapacity.toString() + " %"
        holder.view.tv_battery_life.text = items[position].batteryLife.toString() + " %"

        var sdf = SimpleDateFormat("dd")
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        val currentDate = sdf.format(Date())
        if(date.dayOfMonth == currentDate.toInt()){
            holder.view.bullet.visibility = View.VISIBLE
        }else{
            holder.view.bullet.visibility = View.GONE
        }

    }
}