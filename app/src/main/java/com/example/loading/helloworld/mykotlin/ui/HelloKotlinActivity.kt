package com.example.loading.helloworld.mykotlin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.loading.helloworld.R
import com.loading.common.component.BaseActivity
import com.loading.common.widget.TipsToast

import kotlinx.android.synthetic.main.activity_kotlin_test.*;

class HelloKotlinActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)

        initView()
    }

    private fun initView() {
        misc_test_01.setOnClickListener { TipsToast.getInstance().showTipsText("Button is clicked!") }
    }

    companion object {
        fun startActivity(context : Context?){
            val intent = Intent(context, HelloKotlinActivity::class.java);
            context?.startActivity(intent)
        }
    }
}