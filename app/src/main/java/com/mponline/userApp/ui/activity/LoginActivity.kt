package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.ui.adapter.NotificationAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.*
import kotlinx.android.synthetic.main.layout_otp.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class LoginActivity : BaseActivity(), OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    val viewModel: UserListViewModel by viewModels()
    var mPin: String = ""
    var mFcmToken: String = "sdf sdfsdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!mPreferenceUtils?.getValue(Constants.USER_MOBILE)?.isNullOrEmpty()){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        text_login.setOnClickListener {
            callLoginApi()
        }
        text_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        init()
    }

    fun init() {
//        CommonUtils.openKeyboard(this)
        edt_1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_2.requestFocus()
                }
            }
        })
        edt_2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_3.requestFocus()
                } else {
                    edt_1.requestFocus()
                }
            }
        })
        edt_3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_4.requestFocus()
                } else {
                    edt_2.requestFocus()
                }
            }
        })
        edt_4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_5.requestFocus()
                } else {
                    edt_3.requestFocus()
                }
            }
        })
        edt_5.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_6.requestFocus()
                } else {
                    edt_4.requestFocus()
                }
            }
        })
        edt_6.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    edt_5.requestFocus()
                }
            }
        })

    }

    private fun callLoginApi() {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = edit_mobile_no.text.toString().trim(),
            pin = edt_1.text.toString().trim() + edt_2.text.toString()
                .trim() + edt_3.text.toString().trim() + edt_4.text.toString()
                .trim() + edt_5.text.toString().trim() + edt_6.text.toString().trim(),
            device_type = Constants.DEVICE_TYPE,
            device_token = mFcmToken
        )
        val messageId = viewModel?.validateLogin(commonRequestObj, this@LoginActivity)

        if (messageId != resources?.getString(R.string.valid)) {
            messageId.let { it2 ->
                CommonUtils.createSnackBar(findViewById(android.R.id.content), it2)
            }
        } else if (CommonUtils.isOnline(this)) {
            relative_progress.visibility = View.VISIBLE
            viewModel?.login(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status!!) {
                        relative_progress.visibility = View.GONE
                        mPreferenceUtils?.setValue(Constants.USER_NAME, data?.user?.name!!)
                        mPreferenceUtils?.setValue(Constants.USER_MOBILE, data?.user?.phone!!)
                        mPreferenceUtils?.setValue(Constants.USER_ID, data?.user?.id!!)
                        mPreferenceUtils?.setValue(Constants.USER_TOKEN, data?.token!!)
                        mPreferenceUtils?.setValue(Constants.USER_INFO, Gson().toJson(this))
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        relative_progress.visibility = View.GONE
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {

    }



}