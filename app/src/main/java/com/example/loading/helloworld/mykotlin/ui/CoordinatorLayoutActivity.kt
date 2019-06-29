package com.example.loading.helloworld.mykotlin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.loading.helloworld.R
import com.loading.common.component.BaseActivity
import com.loading.common.widget.TipsToast

import kotlinx.android.synthetic.main.activity_coordinator_layout.*

class CoordinatorLayoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_coordinator_layout)
        setContentView(R.layout.activity_coordinator_layout_backup)

        initView()
    }

    private fun initView() {
        coordinator_layout.setBackgroundColor(0x22ff0000)
    }

    fun onBtnClicked(view: View) {
        TipsToast.getInstance().showTipsText("Button is clicked.")
    }

    companion object {
        fun startActivity(context: Context?) {
            val intent = Intent(context, CoordinatorLayoutActivity::class.java)
            context?.startActivity(intent)
        }
    }
}