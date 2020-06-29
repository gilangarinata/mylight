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
import com.codaholic.mylight.model.ResultItem2
import com.codaholic.mylight.view.HomeFragmentDetail
import kotlinx.android.synthetic.main.item_device.view.*
import kotlinx.android.synthetic.main.item_schedule.view.*


class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.Holder> {
    private lateinit var items: List<ResultItem2>
    private lateinit var context: Context
    private val VIEW_DOCUMENT: Int = 1
    private val VIEW_PROGRESS: Int = 2
    private lateinit var adapterCallback: AdapterCallback

    constructor(
        items: List<ResultItem2>,
        context: Context,
        adapterCallback: AdapterCallback
    ) : super() {
        this.items = items
        this.context = context
        this.adapterCallback = adapterCallback
    }

    interface AdapterCallback {
        fun clickItem(item : ResultItem2)
        fun longClicks(item : ResultItem2)
        fun switchToggle(item: ResultItem2, lamp:Boolean)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_schedule,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.hour.text = items[position].hour + ":" + items[position].minute
        holder.view.tv_brightness.text = items[position].brightness.toString() + " %"
//        holder.view.lyt_parent_schedule.setOnClickListener {
//            adapterCallback.clickItem(items[position])
//        }

        holder.view.lyt_parent_schedule.setOnLongClickListener {
            adapterCallback.longClicks(items[position])
            true
        }

    }
}