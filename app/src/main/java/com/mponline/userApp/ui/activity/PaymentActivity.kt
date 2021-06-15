package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cashfree.pg.CFPaymentService
import com.cashfree.pg.ui.gpay.GooglePayStatusListener
import com.mponline.userApp.R
import com.mponline.userApp.model.request.CashfreeObj
import com.mponline.userApp.model.request.SavePaymentRequest
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.PaymentActivity
import com.mponline.userApp.ui.adapter.SearchHomeAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.util.*

@AndroidEntryPoint
class PaymentActivity : BaseActivity() {
    internal enum class SeamlessMode {
        CARD, WALLET, NET_BANKING, UPI_COLLECT, PAY_PAL
    }

    private var currentMode = SeamlessMode.CARD
    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var mToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        if (intent?.hasExtra("data")!!) {
            mOrderHistoryDataItem = intent?.getParcelableExtra("data")
            if (mOrderHistoryDataItem != null) {
                callCashfreeToken(
                    orderId = mOrderHistoryDataItem?.id!!,
                    orderAmt = mOrderHistoryDataItem?.orderAmount!!
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Same request code for all payment APIs.
//        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        CommonUtils.printLog("PAYMENT_RES", "API Response : ")
        //Prints all extras. Replace with app logic.
        if (data != null) {
            val bundle = data.extras
            var req: SavePaymentRequest = SavePaymentRequest()
            if (bundle != null) for (key in bundle.keySet()) {
                if (bundle.getString(key) != null) {
                    if(key?.equals("txStatus")!! && !(bundle.getString(key)?.equals("SUCCESS")!!)){
                        finish()
                    }
                    CommonUtils.printLog("PAYMENT_RES2", key + " : " + bundle.getString(key))
                    when (key) {
                        "orderId" -> {
                            req.orderId = bundle.getString(key)!!
                        }
                        "txTime" -> {
                            req.txTime = bundle.getString(key)!!
                        }
                        "referenceId" -> {
                            req.referenceId = bundle.getString(key)!!
                        }
                        "txMsg" -> {
                            req.txMsg = bundle.getString(key)!!
                        }
                        "paymentMode" -> {
                            req.paymentMode = bundle.getString(key)!!
                        }
                        "orderAmount" -> {
                            req.orderAmount = bundle.getString(key)!!
                        }
                        "txStatus" -> {
                            req.txStatus = bundle.getString(key)!!
                        }
                    }
                }
            }
            callSavePayment(req)
        }
    }

    private fun callCashfreeToken(orderId: String, orderAmt: String) {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            var cashfreeObj: CashfreeObj = CashfreeObj(
                orderId = orderId,
                orderAmount = orderAmt
            )
            viewModel?.cashfreeToken(
                "Bearer ${mPreferenceUtils?.getValue(Constants.USER_TOKEN)}",
                cashfreeObj
            )?.observe(this, androidx.lifecycle.Observer {
                switchView(1, "")
                if (it?.status!!) {
                    mToken = it?.data
                    onClick(web)
                } else {
                    finish()
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun callSavePayment(savePaymentRequest: SavePaymentRequest) {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            viewModel?.savePayment(
                "Bearer ${mPreferenceUtils?.getValue(Constants.USER_TOKEN)}",
                savePaymentRequest
            )?.observe(this, androidx.lifecycle.Observer {
                switchView(1, "")
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    it?.message!!
                )
                if (it?.status!!) {
                    var intent: Intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    finish()
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun onClick(view: View) {
        /*
         * stage allows you to switch between sandboxed and production servers
         * for CashFree Payment Gateway. The possible values are
         *
         * 1. TEST: Use the Test server. You can use this service while integrating
         *      and testing the CashFree PG. No real money will be deducted from the
         *      cards and bank accounts you use this stage. This mode is thus ideal
         *      for use during the development. You can use the cards provided here
         *      while in this stage: https://docs.cashfree.com/docs/resources/#test-data
         *
         * 2. PROD: Once you have completed the testing and integration and successfully
         *      integrated the CashFree PG, use this value for stage variable. This will
         *      enable live transactions
         */
        val stage = "TEST"

        //Show the UI for doGPayPayment and phonePePayment only after checking if the apps are ready for payment
        if (view.id == R.id.phonePe_exists) {
            Toast.makeText(
                this@PaymentActivity,
                CFPaymentService.getCFPaymentServiceInstance()
                    .doesPhonePeExist(this@PaymentActivity, stage).toString() + "",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else if (view.id == R.id.gpay_ready) {
            CFPaymentService.getCFPaymentServiceInstance()
                .isGPayReadyForPayment(this@PaymentActivity, object : GooglePayStatusListener {
                    override fun isReady() {
                        Toast.makeText(this@PaymentActivity, "Ready", Toast.LENGTH_SHORT).show()
                    }

                    override fun isNotReady() {
                        Toast.makeText(this@PaymentActivity, "Not Ready", Toast.LENGTH_SHORT).show()
                    }
                })
            return
        }

        /*
         * token can be generated from your backend by calling cashfree servers. Please
         * check the documentation for details on generating the token.
         * READ THIS TO GENERATE TOKEN: https://bit.ly/2RGV3Pp
         */
        val token = mToken
        // "QX9JCN4MzUIJiOicGbhJCLiQ1VKJiOiAXe0Jye.VM0nIhZTY3UDO4YjYwIGM2IiOiQHbhN3XiwCMwgTN4cDNyYTM6ICc4VmIsIiUOlkI6ISej5WZyJXdDJXZkJ3biwiIxIiOiQnb19WbBJXZkJ3biwiIxADMwIXZkJ3TiojIklkclRmcvJye.YsOmtLbQ4F4zhLcAx6Pjdtb7TExIsj9KqARiXuJOo0Zy-LpnpjC38UO8UlIK32H-MK"
        val cfPaymentService = CFPaymentService.getCFPaymentServiceInstance()
        cfPaymentService.setOrientation(0)
        cfPaymentService.doPayment(
            this@PaymentActivity,
            inputParams,
            token,
            stage,
            "#784BD2",
            "#FFFFFF",
            false
        )
//        when (view.id) {
//            R.id.web -> {
//                cfPaymentService.doPayment(
//                    this@PaymentActivity,
//                    inputParams,
//                    token,
//                    stage,
//                    "#784BD2",
//                    "#FFFFFF",
//                    false
//                )
//            }
//            R.id.upi -> {
//
////                                cfPaymentService.selectUpiClient("com.google.android.apps.nbu.paisa.user");
//                cfPaymentService.upiPayment(this@PaymentActivity, inputParams, token, stage)
//            }
//            R.id.amazon -> {
//                cfPaymentService.doAmazonPayment(this@PaymentActivity, inputParams, token, stage)
//            }
//            R.id.gpay -> {
//                cfPaymentService.gPayPayment(this@PaymentActivity, inputParams, token, stage)
//            }
//            R.id.phonePe -> {
//                cfPaymentService.phonePePayment(this@PaymentActivity, inputParams, token, stage)
//            }
//            R.id.web_seamless -> {
//                cfPaymentService.doPayment(
//                    this@PaymentActivity,
//                    seamlessCheckoutParams,
//                    token,
//                    stage
//                )
//            }
//        }
    }

    /*
         * appId will be available to you at CashFree Dashboard. This is a unique
         * identifier for your app. Please replace this appId with your appId.
         * Also, as explained below you will need to change your appId to prod
         * credentials before publishing your app.
         */
    private val inputParams: MutableMap<String, String>
        private get() {

            /*
         * appId will be available to you at CashFree Dashboard. This is a unique
         * identifier for your app. Please replace this appId with your appId.
         * Also, as explained below you will need to change your appId to prod
         * credentials before publishing your app.
         */
//            val appId = "134441f4914d787610a43c13f44431"
            val appId = "936476e4b0e75a0300a64fc14639"
            val orderId = mOrderHistoryDataItem?.id!!
            val orderAmount = mOrderHistoryDataItem?.orderAmount!!
            val orderNote = "Test Order"
            val customerName = mPreferenceUtils?.getValue(Constants.USER_NAME)
            val customerPhone = mPreferenceUtils?.getValue(Constants.USER_MOBILE)
            val customerEmail = "test@gmail.com"
            val params: MutableMap<String, String> = HashMap()
            params[CFPaymentService.PARAM_APP_ID] = appId
            params[CFPaymentService.PARAM_ORDER_ID] = orderId
            params[CFPaymentService.PARAM_ORDER_AMOUNT] = orderAmount
            params[CFPaymentService.PARAM_ORDER_NOTE] = orderNote
            params[CFPaymentService.PARAM_CUSTOMER_NAME] = customerName
            params[CFPaymentService.PARAM_CUSTOMER_PHONE] = customerPhone
            params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = customerEmail
            params[CFPaymentService.PARAM_ORDER_CURRENCY] = "INR"
            return params
        }// Put one of the bank codes mentioned here https://dev.cashfree.com/payment-gateway/payments/netbanking

    // Put one of the wallet codes mentioned here https://dev.cashfree.com/payment-gateway/payments/wallets
    private val seamlessCheckoutParams: Map<String, String>
        private get() {
            val params = inputParams
            when (currentMode) {
                SeamlessMode.CARD -> {
                    params[CFPaymentService.PARAM_PAYMENT_OPTION] = "card"
                    params[CFPaymentService.PARAM_CARD_NUMBER] = "VALID_CARD_NUMBER"
                    params[CFPaymentService.PARAM_CARD_YYYY] = "YYYY"
                    params[CFPaymentService.PARAM_CARD_MM] = "MM"
                    params[CFPaymentService.PARAM_CARD_HOLDER] = "CARD_HOLDER_NAME"
                    params[CFPaymentService.PARAM_CARD_CVV] = "CVV"
                }
                SeamlessMode.WALLET -> {
                    params[CFPaymentService.PARAM_PAYMENT_OPTION] = "wallet"
                    params[CFPaymentService.PARAM_WALLET_CODE] =
                        "4007" // Put one of the wallet codes mentioned here https://dev.cashfree.com/payment-gateway/payments/wallets
                }
                SeamlessMode.NET_BANKING -> {
                    params[CFPaymentService.PARAM_PAYMENT_OPTION] = "nb"
                    params[CFPaymentService.PARAM_BANK_CODE] =
                        "3333" // Put one of the bank codes mentioned here https://dev.cashfree.com/payment-gateway/payments/netbanking
                }
                SeamlessMode.UPI_COLLECT -> {
                    params[CFPaymentService.PARAM_PAYMENT_OPTION] = "upi"
                    params[CFPaymentService.PARAM_UPI_VPA] = "VALID_VPA"
                }
                SeamlessMode.PAY_PAL -> params[CFPaymentService.PARAM_PAYMENT_OPTION] = "paypal"
            }
            return params
        }

    companion object {
        private const val TAG = "MainActivity"
    }

    fun switchView(i: Int, msg: String) {
        when (i) {
            0 -> {
                relative_progress?.visibility = View.GONE
            }
            1 -> {
                relative_progress?.visibility = View.GONE
            }
            2 -> {
                relative_progress?.visibility = View.GONE
            }
            3 -> {
                relative_progress?.visibility = View.VISIBLE
            }
            4 -> {
                relative_progress?.visibility = View.GONE
            }
        }
    }

}