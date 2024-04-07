package com.mamits.apnaonlines.userv.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.cashfree.pg.CFPaymentService
import com.cashfree.pg.ui.gpay.GooglePayStatusListener
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.model.request.CashfreeObj
import com.mamits.apnaonlines.userv.model.request.SavePaymentRequest
import com.mamits.apnaonlines.userv.model.response.GetSettingResponse
import com.mamits.apnaonlines.userv.model.response.OrderDetailItem
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment.web
import kotlinx.android.synthetic.main.layout_progress.relative_progress
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID

@AndroidEntryPoint
class PaymentActivity : BaseActivity() {
    internal enum class SeamlessMode {
        CARD, WALLET, NET_BANKING, UPI_COLLECT, PAY_PAL
    }

    private lateinit var PHONEPE_MERCHANT_TR_ID: String
    private var currentMode = SeamlessMode.CARD
    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var mToken = ""
    var mPaymentResList: ArrayList<OrderDetailItem> = arrayListOf()
    var mPaymentGateway = ""
    var mGetSettingResponse: GetSettingResponse? = null
    private val PHONEPE_MERCHANT_ID = "APNAONLINES"
    private val PHONEPE_SALT = "331a8273-c2d5-4590-9369-2b12f1bb1e1e"
    private val apiEndPoint = "/pg/v1/pay"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)/*phonepe sdk initialized*/
        PhonePe.init(this, PhonePeEnvironment.RELEASE, PHONEPE_MERCHANT_ID, null)
        if (intent?.hasExtra("paymentgateway")!!) {
            mPaymentGateway = intent?.getStringExtra("paymentgateway")!!
        }
        if (intent?.hasExtra("data")!!) {
            mOrderHistoryDataItem = intent?.getParcelableExtra("data")
            if (mOrderHistoryDataItem != null) {/* callCashfreeToken(
                     orderId = mOrderHistoryDataItem?.orderId!!,
                     orderAmt = mOrderHistoryDataItem?.payableAmount!!
                 )*/
                callGetting()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Same request code for all payment APIs.
//        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        CommonUtils.printLog("PAYMENT_RES", "API Response : ")
        //Prints all extras. Replace with app logic.
        var req: SavePaymentRequest = SavePaymentRequest()

        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            val bundle = data.extras
            if (bundle != null) for (key in bundle.keySet()) {
                if (bundle.getString(key) != null) {
                    if (key?.equals("txStatus")!! && !(bundle.getString(key)
                            ?.equals("SUCCESS")!!)
                    ) {
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
        } else if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                // The transaction was successful
                Log.d("response : ", "PhonePe Transaction Success. Data: ${data.data}")

                /*val response = data.getStringExtra("response")
                Log.d("response : ", "PhonePe Transaction Success. Response: $response")*/

                // Parse the response and handle it as needed
                // Example: You might want to check the status and other details
                try {/*val jsonResponse = response?.let { JSONObject(it) }
                    val status = jsonResponse?.optString("status")*/
                    checkStatus()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // The transaction was not successful
                Log.d("response : ", "PhonePe Transaction Failed.")
            }
        }
    }


    private fun checkStatus() {

        val xVerify =
            sha256("/pg/v1/status/$PHONEPE_MERCHANT_ID/${PHONEPE_MERCHANT_TR_ID}${PHONEPE_SALT}") + "###1"

        Log.d("phonepe", "onCreate  xverify : $xVerify")


        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to PHONEPE_MERCHANT_ID,
        )



        viewModel.checkPhonepeStatus(PHONEPE_MERCHANT_ID, PHONEPE_MERCHANT_TR_ID, headers)
            .observe(this, androidx.lifecycle.Observer {
                it?.run {

                    Log.d("phonepe", "onCreate: $it")

                    /*
                    * {"success":true,"code":"PAYMENT_SUCCESS","message":"Your payment is successful.","data":{"merchantId":"APNAONLINES","merchantTransactionId":"183301373","transactionId":"T2312192252126734210842","amount":100,"state":"COMPLETED","responseCode":"SUCCESS","paymentInstrument":{"type":"UPI","utr":"335376998756","upiTransactionId":"AXLe714bb03fb95416da5267c2f1a412c05","cardNetwork":null,"accountType":"SAVINGS"}}}*/

                    /*if (res.body() != null && res.body()!!.success) {
                        Log.d("phonepe", "onCreate: success")
                        Toast.makeText(this@PaymentActivity, res.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

        req.orderId = mOrderHistoryDataItem?.orderId
                req.paymentMethod = "CASHFREE"
                callSavePayment(req)
                    }*/

                    if (it.get("success").asBoolean) {

                        var dataObj = it.get("data").asJsonObject

                        var req = SavePaymentRequest()

                        val currentTime = System.currentTimeMillis()
                        val simpleDateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        req.txTime = simpleDateFormat.format(Date(currentTime))

                        req.referenceId = dataObj.get("transactionId").asString
                        req.txMsg = it.get("message").asString
                        req.paymentMode = "ONLINE"
//                        req.orderAmount = dataObj.get("amount").asInt.toString()
                        req.orderAmount = mOrderHistoryDataItem?.orderAmount
                        req.txStatus = dataObj.get("responseCode").asString

                        req.orderId = mOrderHistoryDataItem?.orderId
                        req.paymentMethod = "PHONEPE"
                        callSavePayment(req)
                    } else {
                        finish()
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!, it.get("message").asString
                        )
                    }
                }
            })
    }

    private fun callGetting() {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey()
            )
            viewModel.getSetting(commonRequestObj).observe(this, androidx.lifecycle.Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        mGetSettingResponse = this
                        if (mPaymentGateway?.equals("cashfree")) {
                            callCashfreeToken(
                                orderId = mOrderHistoryDataItem?.orderId!!,
                                orderAmt = mOrderHistoryDataItem?.payableAmount!!
                            )
                        } else if (mPaymentGateway?.equals("phonepe")) {
                            startPhonepe();
                        } else {
                            getPaytmChecksum()
                        }
                    } else {
                        finish()
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!, message
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!, resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun startPhonepe() {
        if (PHONEPE_MERCHANT_ID != null) {
            val data = JSONObject()
            val n = 10000 + Random().nextInt(90000)
            val m = Math.pow(10.0, (n - 1).toDouble()).toInt()
            PHONEPE_MERCHANT_TR_ID = (m + Random().nextInt(9 * m)).toString().replace("-", "")
            try {
                data.put("merchantTransactionId", PHONEPE_MERCHANT_TR_ID)
                data.put("merchantId", PHONEPE_MERCHANT_ID)
                data.put("merchantUserId", mOrderHistoryDataItem?.userId)
                data.put(
                    "amount",
                    (mOrderHistoryDataItem?.payableAmount!!.toDouble()).toInt() * 100
                )
                data.put("mobileNumber", "8602741312")
                data.put("callbackUrl", "https://webhook.site/callback-url")
                val paymentInstrument = JSONObject()
                paymentInstrument.put("type", "PAY_PAGE")//UPI_INTENT
                data.put("paymentInstrument", paymentInstrument)

                val payloadBase64: String
                payloadBase64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder()
                        .encodeToString(data.toString().toByteArray(Charset.defaultCharset()))
                } else {
                    android.util.Base64.encodeToString(
                        data.toString().toByteArray(), android.util.Base64.DEFAULT
                    )
                }
                val checksum: String = sha256(payloadBase64 + apiEndPoint + PHONEPE_SALT) + "###1"
                val checksum1: String =
                    sha256("/pg/v1/status/$PHONEPE_MERCHANT_ID/$PHONEPE_MERCHANT_TR_ID$PHONEPE_SALT") + "###1"
                Log.d(TAG, "merchantTransactionId : $PHONEPE_MERCHANT_TR_ID")
                Log.d(TAG, "payloadBase64 : $payloadBase64")
                Log.d(TAG, "checksum : $checksum")
                Log.d(TAG, "checksum1 : $checksum1")
                val b2BPGRequest: B2BPGRequest =
                    B2BPGRequestBuilder().setData(payloadBase64).setChecksum(checksum)
                        .setUrl(apiEndPoint).build()
                try {
                    val intent: Intent =
                        PhonePe.getImplicitIntent(this, b2BPGRequest, "com.phonepe.app")!!
                    startActivityForResult(intent, 1)
                } catch (e: PhonePeInitException) {
                    e.printStackTrace()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    private fun sha256(base64: String): String? {
        return try {
            val bytes = base64.toByteArray(StandardCharsets.UTF_8)
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val result = StringBuilder()
            for (b in digest) {
                result.append(String.format("%02x", b))
            }
            result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    private fun callCashfreeToken(orderId: String, orderAmt: String) {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            var cashfreeObj: CashfreeObj = CashfreeObj(
                orderId = orderId, orderAmount = orderAmt
            )
            viewModel.cashfreeToken(
                "Bearer ${mPreferenceUtils?.getValue(Constants.USER_TOKEN)}", cashfreeObj
            ).observe(this, androidx.lifecycle.Observer {
                if (mGetSettingResponse != null) switchView(1, "")
                if (it?.status!!) {
                    mToken = it?.data
                    onClick(web)
                } else {
                    finish()
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!, resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun callSavePayment(savePaymentRequest: SavePaymentRequest) {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            viewModel?.savePayment(
                "Bearer ${mPreferenceUtils?.getValue(Constants.USER_TOKEN)}", savePaymentRequest
            )?.observe(this, androidx.lifecycle.Observer {
                switchView(1, "")
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!, it?.message!!
                )
                if (it?.status!!) {
                    mPaymentResList?.clear()
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Order ID", value = savePaymentRequest?.orderId
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Order Amount", value = savePaymentRequest?.orderAmount
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Reference ID", value = savePaymentRequest?.referenceId
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Transaction Status", value = savePaymentRequest?.txStatus
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Payment Mode", value = savePaymentRequest?.paymentMethod
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Message", value = savePaymentRequest?.txMsg
                        )
                    )
                    mPaymentResList?.add(
                        OrderDetailItem(
                            name = "Transaction Time", value = savePaymentRequest?.txTime
                        )
                    )
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
                findViewById(android.R.id.content)!!, resources?.getString(R.string.no_net)!!
            )
        }
    }

    /**
     * Paytm process start
     */
    private fun getPaytmChecksum() {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            viewModel.getPaytmChecksum(
                mPreferenceUtils?.getValue(Constants.USER_MOBILE),
                "test@gmail.com",
                mPreferenceUtils?.getValue(Constants.USER_ID),
                mOrderHistoryDataItem?.orderId!!,
                mOrderHistoryDataItem?.payableAmount!!
            ).observe(this, androidx.lifecycle.Observer {
                it?.let {
                    if (it?.status) {
                        paytmProcess(it?.data)
                    } else {
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            "Something went wrong, please try again"
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!, resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun paytmProcess(checksum: String) {
        var orderID = mOrderHistoryDataItem?.orderId
        var customerID = mPreferenceUtils?.getValue(Constants.USER_ID)
        var callbackURL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderID;

        Log.i(TAG, "paytmProcess_orderID = " + orderID);
        Log.i(TAG, "paytmProcess_customerID = " + customerID);
        Log.i(TAG, "paytmProcess_callbackURL = " + callbackURL);

        var varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp"
        val pgService = PaytmPGService.getStagingService()
        val paramMap = HashMap<String, String>()
        //these are mandatory parameters
        paramMap["MID"] =
            mGetSettingResponse?.data?.get(0)?.paytmMerchantMid!!//"CAvRVp59211200813874" //MID provided by paytm
        paramMap["ORDER_ID"] = mOrderHistoryDataItem?.orderId!!
        paramMap["CUST_ID"] = mPreferenceUtils?.getValue(Constants.USER_ID)
        paramMap["CHANNEL_ID"] = "WAP"
        paramMap["TXN_AMOUNT"] = mOrderHistoryDataItem?.payableAmount!!
        paramMap["WEBSITE"] = "WEBSTAGING"
        paramMap["CALLBACK_URL"] =
            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${mOrderHistoryDataItem?.orderId}"
        paramMap.put("EMAIL", "test@gmail.com");   // no need
        paramMap.put("MOBILE_NO", mPreferenceUtils?.getValue(Constants.USER_MOBILE));  // no need
        paramMap["CHECKSUMHASH"] = checksum
        //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
        paramMap["INDUSTRY_TYPE_ID"] = "Retail"

        val Order = PaytmOrder(paramMap)
        Log.e("checksum ", "param $paramMap")
        pgService.initialize(Order, null)
        pgService.startPaymentTransaction(
            this,
            true,
            true,
            object : PaytmPaymentTransactionCallback {
                override fun onTransactionResponse(bundle: Bundle?) {
                    var req: SavePaymentRequest = SavePaymentRequest()
                    if (bundle != null) for (key in bundle.keySet()) {
                        if (bundle.getString(key) != null) {
                            if (key?.equals("STATUS")!! && !(bundle.getString(key)
                                    ?.equals("TXN_SUCCESS")!!)
                            ) {
                                finish()
                            }
                            mPaymentResList?.clear()
                            CommonUtils.printLog(
                                "PAYMENT_RES2", key + " : " + bundle.getString(key)
                            )
                            when (key) {
                                "orderId" -> {
//                            req.orderId = bundle.getString(key)!!
                                }

                                "TXNDATE" -> {
                                    req.txTime = bundle.getString(key)!!
                                }

                                "TXNID" -> {
                                    req.referenceId = bundle.getString(key)!!
                                }

                                "RESPMSG" -> {
                                    req.txMsg = bundle.getString(key)!!
                                }

                                "PAYMENTMODE" -> {
                                    req.paymentMode = bundle.getString(key)!!
                                }

                                "TXNAMOUNT" -> {
                                    req.orderAmount = bundle.getString(key)!!
                                }

                                "STATUS" -> {
                                    req.txStatus = bundle.getString(key)!!
                                }
                            }
                        }
                    }
                    req.orderId = mOrderHistoryDataItem?.orderId
                    req.paymentMethod = "PAYTM"
                    callSavePayment(req)
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

    fun generateRandomUUID(): String {
        var uuid = UUID.randomUUID().toString();
        return uuid.replace("[-+.^:,|@_]", "");
    }


    /**
     * Cashfree process start
     */
    fun onClick(view: View) {/*
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
        val stage = mGetSettingResponse?.data?.get(0)?.cashfreeMode

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
            this@PaymentActivity, inputParams, token, stage, "#784BD2", "#FFFFFF", false
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
//            val appId = "936476e4b0e75a0300a64fc14639"
            val appId =
                mGetSettingResponse?.data?.get(0)?.appid!!//"1196792a604ee2098128120f1d976911"
            val orderId = mOrderHistoryDataItem?.orderId!!
            val orderAmount = mOrderHistoryDataItem?.payableAmount!!
            val orderNote = "Order"
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