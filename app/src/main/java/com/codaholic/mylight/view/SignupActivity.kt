package com.codaholic.mylight.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codaholic.mylight.R
import com.codaholic.mylight.model.ResponseSignUp
import com.codaholic.mylight.network.CallBackClient
import com.codaholic.mylight.network.ResponseAPI
import com.codaholic.mylight.network.Status
import com.codaholic.mylight.network.repository.HashClientRepository
import com.codaholic.mylight.utils.Tools
import com.codaholic.mylight.viewmodel.SignUpViewModel
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.et_password
import kotlinx.android.synthetic.main.activity_signup.et_username
import kotlinx.android.synthetic.main.activity_signup.parent_layout
import kotlinx.android.synthetic.main.activity_signup.progress_circular
import java.util.*


class SignupActivity : AppCompatActivity(), SignUpViewModel.MainCallBack, CallBackClient {
    private lateinit var viewModel: SignUpViewModel
    private lateinit var sheenValidator: SheenValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        viewModel.init(this,this,this)

        signup.setOnClickListener {
            validate()
        }
    }

    fun validate(){
        sheenValidator = SheenValidator(this)
        sheenValidator.setOnValidatorListener {
            processSignUp()
        }

        sheenValidator.registerAsRequired(et_username)
        sheenValidator.registerAsRequired(et_email)
        sheenValidator.registerAsEmail(et_email)
        sheenValidator.registerAsRequired(et_password)
        sheenValidator.registerAsRequired(et_work)
        sheenValidator.registerHasMinLength(et_password,6)
        sheenValidator.validate()
    }

    fun processSignUp() {
        val hashMap: HashMap<String, String> = HashClientRepository().signUp(
            et_username.text.toString(),
            et_email.text.toString(),
            et_password.text.toString(),
            et_work.text.toString()
        )
        viewModel.signUp(hashMap).observe(this, Observer<ResponseAPI> { responseAPI: ResponseAPI? -> viewModel.processResponseRegister(responseAPI) })
    }

    fun goToLogin(){
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun responseSignUpVM(responseSignUp: ResponseSignUp?) {

    }

    override fun loading() {
        progress_circular.visibility = View.VISIBLE
        signup.visibility = View.INVISIBLE
    }

    override fun success(message: String?, idsuccess: Int) {
        Tools().showSnackbar(parent_layout, Status.SUCCESS,"Sign Up Success.").show()
        progress_circular.visibility = View.INVISIBLE
        signup.visibility = View.VISIBLE
        goToLogin();
    }

    override fun failed(message: String?) {
        if(message != null){
            Tools().showSnackbar(parent_layout, Status.FAILED,message).show()
        }
        progress_circular.visibility = View.INVISIBLE
        signup.visibility = View.VISIBLE
    }

    override fun errorConnection(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR_CONNECTION,it).show() }
        }
        progress_circular.visibility = View.INVISIBLE
        signup.visibility = View.VISIBLE
    }

    override fun error(t: Throwable?) {
        if (t != null) {
            t.message?.let { Tools().showSnackbar(parent_layout, Status.ERROR,it).show() }
        }
        progress_circular.visibility = View.INVISIBLE
        signup.visibility = View.VISIBLE
    }
}
