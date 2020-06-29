package com.codaholic.mylight.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codaholic.mylight.R
import com.codaholic.mylight.adapter.ScheduleAdapter
import com.codaholic.mylight.fragment.DialogFullscreenFragment
import com.codaholic.mylight.manage.Config
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.Event
import com.codaholic.mylight.model.ResponseSchedule
import com.codaholic.mylight.model.ResultItem2
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.utils.SpacingItemDecoration
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.ScheduleViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_schedule.*

class ScheduleActivity : AppCompatActivity(), ScheduleViewModel.MainCallBack, CallBackClient,ScheduleAdapter.AdapterCallback {
    private lateinit var parent_view: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScheduleAdapter
    private lateinit var actionMode: ActionMode
    private lateinit var viewModel: ScheduleViewModel
    val items = arrayListOf<ResultItem2>()
    private lateinit var prefManager: PrefManager
    private lateinit var hardwareId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.init(this,this,this)
        prefManager = PrefManager(this)
        hardwareId = intent.getStringExtra(Config.HARDWARE_ID)

        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        adapter = ScheduleAdapter(items,this,this)
        rv_schedule.layoutManager = layoutManager
        rv_schedule.addItemDecoration(SpacingItemDecoration(2,Tools().dpToPx(this,8),true))
        rv_schedule.setHasFixedSize(true)
        rv_schedule.adapter = adapter

        initToolbar()
        fetchSchedule()
    }

    private fun fetchSchedule(){
        viewModel.fetchSchedule(hardwareId).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseFetchSchedule(t) })
    }

    private fun deleteSchedule(id : String){
        viewModel.deleteSchedule(id).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseDeleteSchedule(t) })
    }

    private fun addSchedule(minute:String, hour:String, day:String, brightness:String){
        var hash = HashClientRepository().addSchedule(minute,hour,day,brightness,prefManager.userId,hardwareId)
        viewModel.addSchedule(hash).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseAddSchedule(t) })
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_white)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Schedule"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun responseFetchScheduleVM(responseSchedule: ResponseSchedule?) {
        if (responseSchedule != null) {
            items.addAll(responseSchedule.result as Collection<ResultItem2>)
            adapter.notifyDataSetChanged()
        }
    }

    override fun responseDeleteScheduleVM() {
        items.clear()
        fetchSchedule()
    }

    override fun responseEditScheduleVM() {
        items.clear()
        fetchSchedule()
    }

    override fun responseAddScheduleVM() {
        items.clear()
        fetchSchedule()
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
            progress.visibility = View.VISIBLE
        }else{
            progress.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            showAddDialog(false)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddDialog(isEdit : Boolean) {
        val fragmentManager = supportFragmentManager
        val newFragment = DialogFullscreenFragment(isEdit)
        newFragment.setRequestCode(300)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
        newFragment.setOnCallbackResult(object : DialogFullscreenFragment.CallbackResult {
            override fun sendResult(requestCode: Int, obj: Any?) {
                if (requestCode == 300) {
                    var minute = ((obj as Event).time).substringAfterLast(":")
                    var hour = ((obj as Event).time).substringBeforeLast(":")
                    var brightness = ((obj as Event).brightness)
                    addSchedule(minute,hour,"",brightness)
                }
            }
        })
    }

    override fun clickItem(item: ResultItem2) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun longClicks(item: ResultItem2) {
        item.id?.let { showConfirmDialog(it) }
    }

    override fun switchToggle(item: ResultItem2, lamp: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun showConfirmDialog(id : String) {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Anda ingin menghapus schedule ini?")
        builder.setPositiveButton("YES",
            { dialogInterface, i ->
                deleteSchedule(id)
            })
        builder.setNegativeButton("NO", null)
        builder.show()
    }
}
