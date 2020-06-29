package com.codaholic.mylight.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.codaholic.mylight.R
import com.codaholic.mylight.manage.cache.PrefManager
import com.codaholic.mylight.utils.Tools
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {
    private lateinit var prefManager: PrefManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prefManager = context?.let { PrefManager(it) }!!

        layout_logout.setOnClickListener {
            logoutAction()
        }
    }

    fun logoutAction(){
        prefManager.logout()
        startActivity(Intent(context,DashboardActivity::class.java))
        activity?.finish()
    }

}
