package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.cashfree.pg.CFPaymentService
import com.cashfree.pg.ui.gpay.GooglePayStatusListener
import com.mponline.userApp.R
import com.mponline.userApp.model.PaytmModel
import com.mponline.userApp.model.request.CashfreeObj
import com.mponline.userApp.model.request.SavePaymentRequest
import com.mponline.userApp.model.response.OrderDetailItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_payment_summary.view.*
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
    var mPaymentResList:ArrayList<OrderDetailItem> = arrayListOf()
    var mPaymentGateway = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        if(intent?.hasExtra("paymentgateway")!!){
            mPaymentGateway = intent?.getStringExtra("paymentgateway")!!
        }
        if (intent?.hasExtra("data")!!) {
            mOrderHistoryDataItem = intent?.getParcelableExtra("data")
            if (mOrderHistoryDataItem != null) {
                callCashfreeToken(
                    orderId = mOrderHistoryDataItem?.orderId!!,
                    orderAmt = mOrderHistoryDataItem?.payableAmount!!
                )
                /*if(mPaymentGateway?.equals("cashfree")){
                    callCashfreeToken(
                        orderId = mOrderHistoryDataItem?.orderId!!,
                        orderAmt = mOrderHistoryDataItem?.payableAmount!!
                    )
                }else{
                    getPaytmChecksum()
                }*/
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
                    mPaymentResList?.clear()
                    CommonUtils.printLog("PAYMENT_RES2", key + " : " + bundle.getString(key))
                    when (key) {
                        "orderId" -> {
//                            req.orderId = bundle.getString(key)!!
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
            req.orderId = mOrderHistoryDataItem?.orderId
            req.paymentMethod = "CASHFREE"
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
                    mPaymentResList?.clear()
                    mPaymentResList?.add(OrderDetailItem(name = "Order ID", value = savePaymentRequest?.orderId))
                    mPaymentResList?.add(OrderDetailItem(name = "Order Amount", value = savePaymentRequest?.orderAmount))
                    mPaymentResList?.add(OrderDetailItem(name = "Reference ID", value = savePaymentRequest?.referenceId))
                    mPaymentResList?.add(OrderDetailItem(name = "Transaction Status", value = savePaymentRequest?.txStatus))
                    mPaymentResList?.add(OrderDetailItem(name = "Payment Mode", value = savePaymentRequest?.paymentMethod))
                    mPaymentResList?.add(OrderDetailItem(name = "Message", value = savePaymentRequest?.txMsg))
                    mPaymentResList?.add(OrderDetailItem(name = "Transaction Time", value = savePaymentRequest?.txTime))
                    var intent: Intent = Intent(this, FormPreviewActivity::class.java)
                    intent?.putExtra("paymentdone", "paymentdone")
                    intent?.putExtra("data", mPaymentResList)
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

    /**
     * Paytm process start
     */
    private fun getPaytmChecksum() {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            viewModel?.getPaytmChecksum(mPreferenceUtils?.getValue(Constants.USER_ID), mOrderHistoryDataItem?.orderId!!, mOrderHistoryDataItem?.payableAmount!!)?.observe(this, androidx.lifecycle.Observer {
                it?.let {
                    if(it?.status){
                        paytmProcess(it?.data)
                    }else{
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            "Something went wrong, please try again"
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
    fun paytmProcess(checksum:String) {
        var orderID = mOrderHistoryDataItem?.orderId
        var customerID = mPreferenceUtils?.getValue(Constants.USER_ID)
        var callbackURL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderID;

        Log.i(TAG, "paytmProcess_orderID = " + orderID);
        Log.i(TAG, "paytmProcess_customerID = " + customerID);
        Log.i(TAG, "paytmProcess_callbackURL = " + callbackURL);
       /* val paytmParams = TreeMap<String, String>()
        paytmParams["MID"] = "eRKZ%4P2n#_MoDCb"
        paytmParams["ORDERID"] = mOrderHistoryDataItem?.orderId!!*/
       /* *
         * Generate checksum by parameters we have
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys

        *
         * Generate checksum by parameters we have
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys*/

//        val paytmChecksum: String =
//            PaytmChecksum.generateSignature(paytmParams, "YOUR_MERCHANT_KEY")
//        println("generateSignature Returns: $paytmChecksum")

       /* var paytmModel =  PaytmModel(
            "eRKZ%4P2n#_MoDCb",
            mOrderHistoryDataItem?.orderId!!,
            customerID,
            "WAP",
            mOrderHistoryDataItem?.payableAmount!!,
            "WEBSTAGING",
            callbackURL,
            "Retail",
            checksum);


        var paramMap =  HashMap<String, String>();
        paramMap.put("MID", paytmModel.mId!!);
        // Key in your staging and production MID available in your dashboard
        paramMap.put("ORDER_ID", paytmModel.orderId);
        paramMap.put("CUST_ID", paytmModel.custId);
        paramMap.put("MOBILE_NO", "7777777777");
        paramMap.put("EMAIL", "username@emailprovider.com");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", mOrderHistoryDataItem?.payableAmount!!);
        paramMap.put("WEBSITE", "WEBSTAGING");
        // This is the staging value. Production value is available in your dashboard
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        // This is the staging value. Production value is available in your dashboard
        paramMap.put("CALLBACK_URL", paytmModel.callbackURL);
        paramMap.put("CHECKSUMHASH", paytmModel.checkSumHash);


        var paytmOrder =  PaytmOrder(paramMap);
        var pgService = PaytmPGService.getPreProductionService()
        pgService.initialize(paytmOrder, null);*/

        var varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp"
        val pgService = PaytmPGService.getStagingService()
        val paramMap = HashMap<String, String>()
        //these are mandatory parameters
        paramMap["MID"] = "eRKZ%4P2n#_MoDCb" //MID provided by paytm
        paramMap["ORDER_ID"] = mOrderHistoryDataItem?.orderId!!
        paramMap["CUST_ID"] = mPreferenceUtils?.getValue(Constants.USER_ID)
        paramMap["CHANNEL_ID"] = "WAP"
        paramMap["TXN_AMOUNT"] = mOrderHistoryDataItem?.payableAmount!!
        paramMap["WEBSITE"] = "WEBSTAGING"
        paramMap["CALLBACK_URL"] = varifyurl//"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${mOrderHistoryDataItem?.orderId}"
        //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
        // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
        paramMap["CHECKSUMHASH"] = checksum
        //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
        paramMap["INDUSTRY_TYPE_ID"] = "Retail"

        val Order = PaytmOrder(paramMap)
        Log.e("checksum ", "param $paramMap")
        pgService.initialize(Order, null)
        pgService.startPaymentTransaction(this, true, true,
            object : PaytmPaymentTransactionCallback {
                override fun onTransactionResponse(bundle: Bundle?) {
                    Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
                    Log.e("PAYTM_RES ", "${bundle.toString()}")
                }

                override fun networkNotAvailable() {

                }

                override fun clientAuthenticationFailed(p0: String?) {

                }

                override fun someUIErrorOccurred(p0: String?) {

                }

                override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {

                }

                override fun onBackPressedCancelTransaction() {

                }

                override fun onTransactionCancel(p0: String?, p1: Bundle?) {

                }

            })

    }

    fun generateRandomUUID():String {
        var uuid = UUID.randomUUID().toString();
        return uuid.replace("[-+.^:,|@_]", "");
    }


    /**
     * Cashfree process start
     */
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
            val orderId = mOrderHistoryDataItem?.orderId!!
            val orderAmount = mOrderHistoryDataItem?.payableAmount!!
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