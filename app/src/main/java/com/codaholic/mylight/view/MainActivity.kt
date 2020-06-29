package com.codaholic.mylight.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.ResponseAddDevice
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.MainViewModel
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(), MainViewModel.MainCallBack,CallBackClient {
    private lateinit var viewModel: MainViewModel
    private lateinit var prefManager: PrefManager
    private lateinit var dialog: Dialog
    private lateinit var sheenValidator: SheenValidator
    private lateinit var fragmentHome: HomeFragment
    private lateinit var fragmentSetting: SettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init(this,this,this)
        prefManager = PrefManager(this)
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_add_device)
        dialog.setCancelable(true)

        fragmentHome = HomeFragment()
        fragmentSetting = SettingFragment()
        setFragment(fragmentHome)

        fab_add.setOnClickListener {
            showCustomDialog()
        }

        bottomAppBar.replaceMenu(R.menu.menu)
        bottomAppBar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.menuHome -> {
                    setFragment(fragmentHome)
                    true
                }
                R.id.menuSetting -> {
                    setFragment(fragmentSetting)
                    true
                }
                else -> false
            }
        }
    }

    fun validate(et_username: EditText, et_description: EditText, et_hardware_id : EditText){
        sheenValidator = SheenValidator(this)
        sheenValidator.registerAsRequired(et_username)
        sheenValidator.registerAsRequired(et_hardware_id)
        sheenValidator.setOnValidatorListener {
            addDevice(
                et_username.text.toString(),
                et_description.text.toString(),
                et_hardware_id.text.toString()
            )
        }
        sheenValidator.validate()
    }

    fun addDevice(name: String, description: String, hardwareId:String){
        var hashMap: HashMap<String,String> = HashClientRepository().addDevice(
            name,
            description,
            hardwareId,
            prefManager.userId
        )
        viewModel.addDevice(hashMap).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseAddDevice(t) })
    }

    private fun showCustomDialog() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        (dialog.findViewById<View>(R.id.bt_close) as ImageButton).setOnClickListener { dialog.dismiss() }

        (dialog.findViewById<View>(R.id.bt_save) as LinearLayout).setOnClickListener {
            validate(
                (dialog.findViewById<View>(R.id.et_device_name) as EditText),
                (dialog.findViewById<View>(R.id.et_description) as EditText),
                (dialog.findViewById<View>(R.id.et_hardware_id) as EditText)
            )
        }
        dialog.show()
        dialog.window.attributes = lp
    }

    protected fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layout_frame, fragment)
        fragmentTransaction.commit()
    }

    override fun responseAddDeviceVM(responseAddDevice: ResponseAddDevice?) {
        if (responseAddDevice != null) {
            responseAddDevice.message?.let {
                Tools().showSnackbar((dialog.findViewById<View>(R.id.parent_layout) as LinearLayout), Status.SUCCESS,
                    it
                ).show()
                dialog.dismiss()
                fragmentHome.refreshList()
            }
        }
    }

    override fun loading() {
        showProgress(true)
    }

    override fun success(message: String?, idsuccess: Int) {
        showProgress(false)
    }

    override fun failed(message: String?) {
        if(message != null){
            Tools().showSnackbar((dialog.findViewById<View>(R.id.parent_layout) as LinearLayout), Status.FAILED,message).show()
        }
        showProgress(false)
    }

    override fun errorConnection(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar((dialog.findViewById<View>(R.id.parent_layout) as LinearLayout), Status.ERROR_CONNECTION,it).show() }
        }
        showProgress(false)
    }

    override fun error(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar((dialog.findViewById<View>(R.id.parent_layout) as LinearLayout), Status.ERROR,it).show() }
        }
        showProgress(false)
    }

    fun showProgress(isLoading:Boolean){
        if (isLoading) {
            (dialog.findViewById<View>(R.id.progress_circular) as ProgressBar).visibility = View.VISIBLE
            (dialog.findViewById<View>(R.id.bt_save) as LinearLayout).visibility = View.INVISIBLE
        }else{
            (dialog.findViewById<View>(R.id.progress_circular) as ProgressBar).visibility = View.INVISIBLE
            (dialog.findViewById<View>(R.id.bt_save) as LinearLayout).visibility = View.VISIBLE
        }
    }

}
