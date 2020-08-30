package com.codaholic.mylight.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.model.ResponseLogin
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.LoginViewModel
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.progress_circular
import java.util.HashMap

class LoginActivity : AppCompatActivity(), LoginViewModel.MainCallBack,CallBackClient {
    private lateinit var viewModel:LoginViewModel
    private lateinit var sheenValidator: SheenValidator
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.init(this,this,this)
        prefManager = PrefManager(this)

        login.setOnClickListener {
            validate()
        }
    }

    fun validate(){
        sheenValidator = SheenValidator(this)
        sheenValidator.registerAsRequired(et_username)
        sheenValidator.registerAsRequired(et_password)
        sheenValidator.registerHasMinLength(et_password,6)
        sheenValidator.setOnValidatorListener {
            processLogin()
        }
        sheenValidator.validate()
    }

    fun processLogin(){
        val hashMap: HashMap<String,String> = HashClientRepository().login(
            et_username.text.toString(),
            et_password.text.toString()
        )
        viewModel.doLogin(hashMap).observe(this, Observer<ResponseAPI> { t: ResponseAPI? -> viewModel.processResponseLogin(t) })
    }

    override fun responseLoginVM(responseLogin: ResponseLogin?) {
        if(responseLogin != null){
            prefManager.setLoginPref(
                responseLogin.token,
                responseLogin.userInfo?.username,
                responseLogin.userInfo?.email,
                responseLogin.userInfo?.position,
                responseLogin.userInfo?.id,
                responseLogin.userInfo?.referal
            )
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun loading() {
        showProgress(true)
    }

    override fun success(message: String?, idsuccess: Int) {
        Tools().showSnackbar(parent_layout, Status.SUCCESS,"Sign Up Success.").show()
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
            progress_circular.visibility = View.VISIBLE
            login.visibility = View.INVISIBLE
        }else{
            progress_circular.visibility = View.INVISIBLE
            login.visibility = View.VISIBLE
        }
    }
}
