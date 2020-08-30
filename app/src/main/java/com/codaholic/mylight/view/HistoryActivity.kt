package com.codaholic.mylight.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codaholic.mylight.R
import com.codaholic.mylight.adapter.HistoryAdapter
import com.codaholic.mylight.model.HistoryItem
import com.codaholic.mylight.model.ResponseHistory
import com.codaholic.mylight.model.ResultItem2
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.utils.SpacingItemDecoration
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.parent_layout
import kotlinx.android.synthetic.main.activity_history.toolbar
import kotlinx.android.synthetic.main.activity_schedule.*


class HistoryActivity : AppCompatActivity(), HistoryAdapter.AdapterCallback, HistoryViewModel.MainCallBack, CallBackClient, SwipeRefreshLayout.OnRefreshListener  {
    val items = arrayListOf<HistoryItem>()
    private lateinit var adapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel
    private lateinit var hardwareId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        viewModel.init(this,this,this)
        initToolbar()
        hardwareId = intent.getStringExtra("HID")

        swipe_refresh.setOnRefreshListener(this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        adapter = HistoryAdapter(items,this,this)
        rv_history.layoutManager = layoutManager
        rv_history.setHasFixedSize(true)
        rv_history.adapter = adapter

        fetchHistory()
    }

    private fun fetchHistory(){
        viewModel.fetchHistory(hardwareId).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseFetchHistory(t) })
    }


    override fun clickItem(item: ResultItem2) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun responseFetchHistoryVM(responseHistory: ResponseHistory) {
        items.clear()
        if (responseHistory != null) {
            items.addAll(responseHistory.history as Collection<HistoryItem>)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_white)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "History"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
            swipe_refresh.isRefreshing = true
        }else{
            swipe_refresh.isRefreshing = false
        }
    }

    override fun onRefresh() {
        fetchHistory()
    }

}
