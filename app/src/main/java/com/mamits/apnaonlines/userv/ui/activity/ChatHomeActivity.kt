package com.mamits.apnaonlines.userv.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.ui.adapter.ChatAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.ui.fragment.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.*


class ChatHomeActivity : BaseActivity(), OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        image_back.setOnClickListener {
            onBackPressed()
        }
//        image_search.setOnClickListener {
//
//        }
        toolbar_title.text = resources?.getString(R.string.chat)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {

            }
            true
        }


        //Chat
        rv_chat_list?.setHasFixedSize(true)
        rv_chat_list?.layoutManager =
            LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            )
        rv_chat_list?.adapter = ChatAdapter(
            this,
            this
        )



    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }


}