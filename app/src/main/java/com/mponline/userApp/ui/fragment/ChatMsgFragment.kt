package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnImgPreviewListener
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.ImgPreviewPojo
import com.mponline.userApp.model.response.ChatListDataItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.FilePreviewActivity
import com.mponline.userApp.ui.adapter.*
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.*
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.DateUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat_msg.*
import kotlinx.android.synthetic.main.fragment_chat_msg.view.*
import kotlinx.android.synthetic.main.fragment_chat_msg.view.relative_frag
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.item_msg_incomming.view.*
import kotlinx.android.synthetic.main.item_msg_outgoing.view.*
import kotlinx.android.synthetic.main.layout_empty.view.*
import kotlinx.android.synthetic.main.layout_progress.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class ChatMsgFragment : BaseFragment(), OnItemClickListener, CameraGalleryFragment.Listener,
    OnImgPreviewListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    private var cameraUtils: CameraGalleryUtils = CameraGalleryUtils()
    var mPictureType = ""
    var orderId = ""
    var storeId = ""
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var chatMsgList: ArrayList<ChatListDataItem>? = ArrayList()
    var mAdapter: ChatMsgAdapter? = null
    var mHandler: Handler? = null

    override fun onCameraGalleryClicked(position: Int) {
        when (position) {
            0 -> {
                if (isCameraStoragePermissionGranted(activity!!)) {
                    mPictureType = Constants.CAMERA
                    dispatchTakePictureIntent(activity!!)
                } else {
                    checkCameraStoragePermissions(activity!!)
                }
            }
            1 -> {
                if (isCameraStoragePermissionGranted(activity!!)) {
                    mPictureType = Constants.GALLERY
                    choosePhotoFromGallary(activity!!)
                } else {
                    checkCameraStoragePermissions(activity!!)
                }
            }
        }
    }

    fun showAttachOptions() {
        val instance = CameraGalleryFragment.newInstance(2)
        instance.show(childFragmentManager, "camera_gallery")
    }

    var mTicker: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_chat_msg, container, false)
        val mHandler = Handler()
        mTicker = Runnable {
            callGetUpdatedChatlist()
            mHandler.postDelayed(mTicker, 10000)
        }
        mHandler.postDelayed(mTicker, 10000)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (arguments?.containsKey("data")!!) {
                mOrderHistoryDataItem = arguments?.getParcelable("data")
                callGetChatList()
            }
            if (arguments?.containsKey("order")!! && arguments?.containsKey("store")!!) {
                storeId = arguments?.getString("store")!!
                orderId = arguments?.getString("order")!!
                callGetChatList()
            }
        }
        view?.relative_frag?.setOnClickListener { }
        view?.image_send_msg?.setOnClickListener {
            if (!view?.edit_msg?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
                //API hit
                callSaveMsg(
                    "",
                    orderId = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.id!! else orderId,
                    vendorId = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.storedetail?.userId!! else storeId,
                    msg = edit_msg.text.toString().trim()!!,
                    selectedPos = 0
                )
                view?.text_empty_chat?.visibility = View.GONE
                view?.edit_msg?.setText("")
            } else {
                //show error
            }
        }

        view?.image_file_picker?.setOnClickListener {
            showAttachOptions()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        if (obj is ChatListDataItem) {
            when (view?.id) {
                R.id.image_download_file_incomming -> {
                    var intent: Intent = Intent(activity!!, FilePreviewActivity::class.java)
                    intent?.putExtra("file", obj?.attachment)
                    activity?.startActivity(intent)
                }
                R.id.image_download_file -> {
                    var intent: Intent = Intent(activity!!, FilePreviewActivity::class.java)
                    intent?.putExtra("file", obj?.attachment)
                    activity?.startActivity(intent)
                }

            }
        }
    }

    //Download file


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CAMERA -> {
                    var image = cameraUtils.mCurrentPhotoPath
                    CommonUtils.printLog("RESULT_PATH", image)
                    image?.let {
                        try {
                            ImageOrientationChecker.imagePreviewCamera(File(image))
                            mSwichFragmentListener?.onStartNewActivity(this@ChatMsgFragment, it)
                        } catch (e: Exception) {
                        }
                    }
                }
                Constants.REQUEST_GALLERY -> {
                    data?.let {
                        try {
                            var contentStr = it?.data
                            if (contentStr != null) {
                                var extension =
                                    CommonUtils.getFileExtension(
                                        activity!!,
                                        contentStr
                                    )
                                var fileRealPath = FileUtils.getLocalPath(
                                    activity,
                                    it?.data
                                )//DocUtils().getRealPathFromUri(this@BusinessLoanDocVerificationActivity, it?.data)//PathUtils.getPath(this@BusinessLoanDocVerificationActivity, it?.data)
                                CommonUtils.printLog(
                                    "RESULT_PATH",
                                    contentStr?.toString()!! + "  " + extension + "  " + fileRealPath
                                )
                                try {
                                    if (true/*CommonUtils.isFileLimitNotExceddedEarlySalary(fileRealPath)*/) {
                                        if (extension?.equals(
                                                ".jpg",
                                                true
                                            ) || extension?.equals(
                                                ".jpeg",
                                                true
                                            ) || extension?.equals(".png", true)
                                        ) {
                                            val file = cameraUtils.createImageFile(
                                                activity!!,
                                                extension
                                            )
                                            ImageOrientationChecker.getCopyOfImage(
                                                File(fileRealPath),
                                                file
                                            )
                                            cameraUtils.mCurrentPhotoPath = file.absolutePath
                                        } else {
                                            cameraUtils.mCurrentPhotoPath = fileRealPath
                                        }
                                        var image = cameraUtils.mCurrentPhotoPath
                                        mSwichFragmentListener?.onStartNewActivity(
                                            this@ChatMsgFragment,
                                            image
                                        )
                                    } else {

                                    }
//                                }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    CommonUtils.printLog("ex", e.toString())
                                }
                            }
                        } catch (e: Exception) {
                            e?.printStackTrace()
                        }
                    }
                }
            }
        } else {

        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    rv_chat_msg?.visibility = View.GONE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    rv_chat_msg?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    rv_chat_msg?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    rv_chat_msg?.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        fun newInstance(
            context: Activity,
            mOrderHistoryDataItem: Any
        ): Fragment {
            val fragment = ChatMsgFragment()
            if (mOrderHistoryDataItem is OrderHistoryDataItem) {
                val bundle = Bundle()
                bundle.putParcelable("data", mOrderHistoryDataItem)
                fragment.arguments = bundle
            }
            return fragment
        }

        fun newInstance(
            context: Activity,
            orderId: Any,
            storeId: Any
        ): Fragment {
            val fragment = ChatMsgFragment()
            val bundle = Bundle()
            if (orderId is String && storeId is String) {
                bundle.putString("order", orderId)
                bundle.putString("store", storeId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }


    fun dispatchTakePictureIntent(activity: Activity) {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            try {
                val photoFile: File = createImageFile(activity, cameraUtils = cameraUtils)
                val photoURI: Uri =
                    FileProvider.getUriForFile(activity, Constants.APP_FILEPROVIDER, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    takePictureIntent,
                    Constants.REQUEST_CAMERA
                )
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("zxfsdG", e.printStackTrace().toString())
            }
        }
    }

    fun choosePhotoFromGallary(activity: Activity) {
//        val gallery = Intent(
//            Intent.ACTION_PICK,
//            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
//        )
//        gallery.type = "image/*"

        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
        gallery.addCategory(Intent.CATEGORY_OPENABLE)
        gallery.type = "*/*"

        if (gallery.resolveActivity(activity.packageManager) != null) {
            gallery.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(gallery, Constants.REQUEST_GALLERY)
        }
    }

    override fun onImgPreview(imgPreviewPojo: ImgPreviewPojo) {
        CommonUtils.printLog(
            "IMG_PREVIEW",
            "${imgPreviewPojo?.caption}, ${imgPreviewPojo?.filePath}"
        )
        callSaveMsg(
            imgPreviewPojo?.filePath,
            orderId = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.id!! else orderId,
            vendorId = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.storedetail?.userId!! else storeId,
            msg = imgPreviewPojo?.caption,
            selectedPos = 0
        )
    }

    private fun callGetChatList() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                orderid = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.id!! else orderId
            )
            viewModel?.getChatList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        view?.rv_chat_msg?.setHasFixedSize(true)
                        view?.rv_chat_msg?.layoutManager =
                            LinearLayoutManager(
                                activity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        mAdapter = ChatMsgAdapter(
                            activity,
                            this@ChatMsgFragment,
                            data,
                            mPreferenceUtils
                        )
                        view?.rv_chat_msg?.adapter = mAdapter
                        chatMsgList = data
                        view?.rv_chat_msg?.scrollToPosition(chatMsgList?.size!! - 1)
                        if(mOrderHistoryDataItem!=null && mOrderHistoryDataItem?.status == 5){
                            rl_send_msg.visibility = View.GONE
                        }else{
                            rl_send_msg.visibility = View.VISIBLE
                        }
                        if(data!=null && data?.size!! >0){
                            view?.text_empty_chat?.visibility = View.GONE
                        }else{
                            view?.text_empty_chat?.visibility = View.VISIBLE
                        }
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun callGetUpdatedChatlist() {
        if (activity != null && CommonUtils.isOnline(activity!!)) {
//            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                orderid = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.id!! else orderId,
                vendorid = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.storedetail?.userId!! else storeId
            )
            viewModel?.getUpdatedChatList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
//                        switchView(1, "")
                        chatMsgList = data!!
                        mAdapter?.refreshList(chatMsgList!!)
                        view?.rv_chat_msg?.scrollToPosition(chatMsgList?.size!! - 1)
                    } else {
//                        switchView(0, "")
//                        CommonUtils.createSnackBar(
//                            activity?.findViewById(android.R.id.content)!!,
//                            resources?.getString(R.string.no_net)!!
//                        )
                    }
                }
            })
        } else {
//            CommonUtils.createSnackBar(
//                activity?.findViewById(android.R.id.content)!!,
//                resources?.getString(R.string.no_net)!!
//            )
        }
    }

    fun callSaveMsg(
        mFilePath: String,
        orderId: String,
        vendorId: String,
        msg: String,
        selectedPos: Int
    ) {
        if (CommonUtils.isOnline(activity!!)) {
            chatMsgList?.add(
                ChatListDataItem(
                    toUser = if (mOrderHistoryDataItem != null) mOrderHistoryDataItem?.storedetail?.userId!! else storeId,
                    fromUser = mPreferenceUtils?.getValue(Constants.USER_ID),
                    attachment = mFilePath,
                    updatedAt = DateUtils.getCurrentDate("dd MMM yyyy hh:mm a"),
                    fileType = CommonUtils.getFileExt(mFilePath!!)!!,
                    message = msg,
                    orderId = orderId
                )
            )
            mAdapter?.refreshList(chatMsgList!!)
            view?.rv_chat_msg?.scrollToPosition(chatMsgList?.size!! - 1)
            var multipartBody: MultipartBody.Part? = null
            var fileName = ""
            var orderIdStr = orderId
            var orderIdObj = RequestBody.create("text/plain".toMediaTypeOrNull(), orderIdStr)
            var vendorIdObj = RequestBody.create("text/plain".toMediaTypeOrNull(), vendorId)
            var msgObj = RequestBody.create("text/plain".toMediaTypeOrNull(), msg)
            if (!TextUtils.isEmpty(mFilePath)) {
                var imageFile = File(mFilePath)
                var filePathName = CommonUtils.getFileName(mFilePath)
                //////
                var requestFile: RequestBody? = null
                if (mFilePath.contains(".jpg") || mFilePath.contains(".png") || mFilePath.contains(
                        ".jpeg"
                    )
                ) {
                    requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
                    fileName =
                        if (filePathName.contains(".jpg")) "Test.jpg"
                        else if (filePathName.contains(".png")) "Test.png"
                        else "Test.jpg"
                } else {
                    fileName =
                        if (mFilePath.contains(".pdf")) "Test.pdf"
                        else if (mFilePath.contains(".doc")) "Test.doc"
                        else if (mFilePath.contains(".xls")) "Test.xls"
                        else if (mFilePath.contains(".xlsx")) "Test.xls"
                        else "Test.txt"
                    requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)
                }
                multipartBody = MultipartBody.Part.createFormData("chatfile", fileName, requestFile)
            }
            viewModel?.saveChat(
                "Bearer " + mPreferenceUtils?.getValue(Constants.USER_TOKEN),
                multipartBody,
                orderIdObj,
                vendorIdObj,
                msgObj
            )?.observe(this, androidx.lifecycle.Observer {
                it?.run {
                    CommonUtils.printLog("RESPONSE", Gson().toJson(this))
                    if (status) {
                        chatMsgList = data
                        mAdapter?.refreshList(chatMsgList!!)
                    } else {
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }


//    callGetUpdatedChatlist()

}