package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.CustomFieldObj
import com.mponline.userApp.model.PrePlaceOrderPojo
import com.mponline.userApp.model.request.FormDataItem
import com.mponline.userApp.model.request.PlaceOrderRequest
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.model.response.StoreDetailDataItem
import com.mponline.userApp.ui.adapter.CustomFileAdapter
import com.mponline.userApp.ui.adapter.CustomFormAdapter
import com.mponline.userApp.ui.adapter.InstructionAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.*
import com.mponline.userApp.util.FileUtils.getLocalPath
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_custom_form.view.*
import kotlinx.android.synthetic.main.fragment_custom_form.view.relative_frag
import kotlinx.android.synthetic.main.item_btn.view.*
import kotlinx.android.synthetic.main.item_chkbox.view.*
import kotlinx.android.synthetic.main.item_edittext.view.*
import kotlinx.android.synthetic.main.item_radiobtn.view.*
import kotlinx.android.synthetic.main.item_spinner.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CustomFormFragment : BaseFragment(), OnItemClickListener, CameraGalleryFragment.Listener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var customFormList: ArrayList<CustomFieldObj> = ArrayList()
    private var cameraUtils: CameraGalleryUtils = CameraGalleryUtils()
    var mPictureType = ""
    var mSelectedPos = -1
    var mCustomFileAdapter: CustomFileAdapter? = null
    var mPrePlaceOrderPojo: PrePlaceOrderPojo? = null
    val viewModel: UserListViewModel by viewModels()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_custom_form, container, false)
        cameraUtils?.setContext(activity!!)
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
            if (it?.containsKey("obj")) {
                mPrePlaceOrderPojo = it?.getParcelable("obj")
                if (mPrePlaceOrderPojo != null && mPrePlaceOrderPojo?.mGetProductDetailResponse?.data?.size!! > 0 && mPrePlaceOrderPojo?.mGetProductDetailResponse?.data?.get(
                        0
                    )?.form?.size!! > 0
                ) {
                    customFormList?.clear()
                    mPrePlaceOrderPojo?.mGetProductDetailResponse?.data?.get(0)?.form?.forEachIndexed { index, formItem ->
                        var viewControlList:ArrayList<String> = arrayListOf()
                        formItem?.value?.forEach { valueObj->
                            if(valueObj?.hidefield!=null && valueObj?.hidefield?.size!!>0){
                                viewControlList?.addAll(valueObj?.hidefield)
                            }
                        }
                        customFormList?.add(
                            CustomFieldObj(
                                id = formItem?.id,
                                name = formItem?.name,
                                fieldType = formItem?.fieldType,
                                hintName = formItem?.name,
                                min = "3",
                                max = "20",
                                isRequired = formItem?.isRequired!!,
                                value = formItem?.value,
                                visibilityControlfield = viewControlList,
                                ansValue = ""
                            )
                        )
                    }
                    mCustomFileAdapter = CustomFileAdapter(
                        activity,
                        this@CustomFormFragment, customFormList
                    )
                    var mLayoutMgr = LinearLayoutManager(
                        activity, RecyclerView.VERTICAL, false
                    )
//                    mLayoutMgr.setAutoMeasureEnabled(false);
                    view?.rv_custom_form?.setHasFixedSize(false)
                    view?.rv_custom_form?.layoutManager = mLayoutMgr

                    view?.rv_custom_form?.swapAdapter(mCustomFileAdapter, true)
                }
            }
        }

        view?.relative_frag?.setOnClickListener {

        }
        view?.text_proceed?.setOnClickListener {
            var errormsg = isValidData()
            if (errormsg?.isNullOrEmpty()) {
                var formData: ArrayList<FormDataItem> = ArrayList()
                customFormList?.forEachIndexed { index, customFieldObj ->
                    formData?.add(
                        FormDataItem(
                            isRequired = customFieldObj?.isRequired!!,
                            name = customFieldObj?.name!!,
                            fieldType = customFieldObj?.fieldType!!,
                            ansValue = customFieldObj?.ansValue!!,
                            ext = customFieldObj?.ext!!
                        )
                    )
                }
                var placeOrderRequest: PlaceOrderRequest = PlaceOrderRequest(
                    storeId = mPrePlaceOrderPojo?.storeDetailDataItem?.id!!,
                    productId = mPrePlaceOrderPojo?.mGetProductDetailResponse?.data?.get(0)?.productId!!,
                    price = mPrePlaceOrderPojo?.mGetProductDetailResponse?.data?.get(0)?.price!!,
                    type = "form",
                    formData = formData
                )
                callPlaceOrder(placeOrderRequest)
            } else {
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    errormsg!!
                )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CAMERA -> {
                    var image = cameraUtils.mCurrentPhotoPath
                    CommonUtils.printLog("RESULT_PATH", image)
                    image?.let {
                        try {
//                            ImageOrientationChecker.imagePreviewCamera(File(image))
                            if (mCustomFileAdapter != null) {
                                customFormList?.get(mSelectedPos)?.ansValue = image
                                customFormList?.get(mSelectedPos)?.ansValue = image
                                mCustomFileAdapter?.onRefreshAdapter(
                                    customFormList,
                                    pos = mSelectedPos,
                                    flag = false
                                )
                                callUploadLkDocuments(
                                    customFormList?.get(mSelectedPos)?.name!!,
                                    customFormList?.get(mSelectedPos)?.ansValue!!,
                                    mSelectedPos
                                )
                            }
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
                                        if (mCustomFileAdapter != null) {
                                            customFormList?.get(mSelectedPos)?.ansValue = image
                                            mCustomFileAdapter?.onRefreshAdapter(
                                                customFormList,
                                                pos = mSelectedPos,
                                                flag = false
                                            )
                                            callUploadLkDocuments(
                                                customFormList?.get(mSelectedPos)?.name!!,
                                                customFormList?.get(mSelectedPos)?.ansValue!!,
                                                mSelectedPos
                                            )
                                        }
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

    fun isValidData(): String {
        var errorMsg = ""
        customFormList?.forEachIndexed { index, customFieldObj ->
            if (customFieldObj?.isRequired?.equals(
                    "Yes",
                    true
                ) && (customFieldObj?.ansValue?.isNullOrEmpty()!!&& (customFieldObj?.isVisible))
            ) {
                return "${customFieldObj?.name}"
            }
        }
        return errorMsg
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        if (obj != null && obj is CustomFieldObj) {
            when (view?.id) {
                R.id.text_file_upload -> {
                    mSelectedPos = pos
                    showAttachOptions()
                }
                R.id.image_file_close -> {
                    customFormList?.get(pos)?.ansValue = ""
                    mCustomFileAdapter?.onRefreshAdapter(customFormList, pos = pos, flag = false)
                }
                R.id.edt_custom_field -> {
                    if(customFormList?.get(pos)?.id?.equals(obj?.id)!!){
                        customFormList?.get(pos)?.ansValue = obj?.ansValue
                    }
//                    Handler().postDelayed(Runnable {
//                        mCustomFileAdapter?.onRefreshAdapter(customFormList, pos = pos, flag = true)
//                    }, 1000)
                }
                R.id.edt_custom_field_mult -> {
                    if(customFormList?.get(pos)?.id?.equals(obj?.id)!!) {
                        customFormList?.get(pos)?.ansValue = obj?.ansValue
                    }
//                    Handler().postDelayed(Runnable {
//                        mCustomFileAdapter?.onRefreshAdapter(customFormList, pos = pos, flag = true)
//                    }, 1000)
                }
                R.id.btn -> {
                    if (obj?.fieldType?.equals("location")!!) {
                        mSelectedPos = pos
                        //AutoComplete place
                    } else if (obj?.fieldType?.equals("date")!!) {
                        //datePicker
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)
                        val dpd = DatePickerDialog(
                            activity!!,
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                // Display Selected date in textbox
                                CommonUtils.printLog(
                                    "DATE_SELECTED",
                                    "${dayOfMonth}/${(monthOfYear + 1)}/${year}"
                                )
                                customFormList?.get(pos)?.ansValue =
                                    "${dayOfMonth}/${(monthOfYear + 1)}/${year}"
                                mCustomFileAdapter?.onRefreshAdapter(
                                    customFormList,
                                    pos = pos,
                                    flag = false
                                )
                            },
                            year,
                            month,
                            day
                        )
                        dpd.show()
                    }
                }
            }
        } else if (obj is CustomFieldObj.ValueObj) {
            when (view?.id) {
                R.id.spn_opt -> {
                    if (pos < customFormList.size) {
                        customFormList?.get(pos)?.ansValue = obj?.value
                        if (obj?.hidefield != null && obj?.hidefield?.size > 0) {
                            var visibilityControlFields = customFormList?.get(pos)?.visibilityControlfield
                            customFormList?.forEachIndexed { index, customFieldObj ->
                                if(visibilityControlFields?.contains(customFieldObj?.id)){
                                    if (obj?.hidefield?.contains(customFieldObj?.id)) {
                                        customFormList?.get(index)?.isVisible = false
                                    }else{
                                        customFormList?.get(index)?.isVisible = true
                                    }
                                }
                            }
                            mCustomFileAdapter?.onRefreshAdapter(
                                customFormList,
                                pos = pos,
                                flag = true
                            )
                        }
                    }

//                    mCustomFileAdapter?.onRefreshAdapter(customFilesList)
                }
                R.id.chkbox_item -> {
                    var itemOptList = customFormList?.get(pos)?.ansValue?.split(",")!!
                    var selectedAns = getItemCheckedPos(itemOptList, obj)
                    customFormList?.get(pos)?.ansValue = selectedAns
                    if (obj?.hidefield != null && obj?.hidefield?.size > 0) {
                        var visibilityControlFields = customFormList?.get(pos)?.visibilityControlfield
                        customFormList?.forEachIndexed { index, customFieldObj ->
                            if(visibilityControlFields?.contains(customFieldObj?.id)){
                                if (obj?.hidefield?.contains(customFieldObj?.id)) {
                                    customFormList?.get(index)?.isVisible = false
                                }else{
                                    customFormList?.get(index)?.isVisible = true
                                }
                            }
                        }
                    }
                        mCustomFileAdapter?.onRefreshAdapter(
                            customFormList,
                            pos = pos,
                            flag = false
                        )
                }
                R.id.rbtn_item -> {
                    customFormList?.get(pos)?.ansValue = obj?.value
                    if (obj?.hidefield != null && obj?.hidefield?.size > 0) {
                        var visibilityControlFields = customFormList?.get(pos)?.visibilityControlfield
                        customFormList?.forEachIndexed { index, customFieldObj ->
                            if(visibilityControlFields?.contains(customFieldObj?.id)){
                                if (obj?.hidefield?.contains(customFieldObj?.id)) {
                                    customFormList?.get(index)?.isVisible = false
                                }else{
                                    customFormList?.get(index)?.isVisible = true
                                }
                            }
                        }
                    }
                    mCustomFileAdapter?.onRefreshAdapter(customFormList, pos = pos, flag = false)
                }
            }

        }
//        view?.rv_custom_form?.invalidate()
//        view?.rv_custom_form?.adapter = mCustomFileAdapter
    }

    fun getItemCheckedPos(itemList: List<String>, selectedOpt: CustomFieldObj.ValueObj): String {
        var ansValue = ""
        var isItemAlreadySelected = false
        itemList?.forEachIndexed { index, s ->
            if (s?.equals(selectedOpt?.value)) {
                isItemAlreadySelected = true
            } else {
                ansValue = ansValue + "," + s
            }
        }
        if (!isItemAlreadySelected) {
            ansValue = ansValue + "," + selectedOpt?.value
        }
        return ansValue
    }

    private fun callPlaceOrder(placeOrderRequest: PlaceOrderRequest) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            viewModel?.placeOrder(
                "Bearer " + mPreferenceUtils?.getValue(Constants.USER_TOKEN),
                placeOrderRequest
            )?.observe(this, androidx.lifecycle.Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        //Redirect to Order History
                        mSwichFragmentListener?.onSwitchFragment(
                            Constants.ORDER_HISTORY_PAGE,
                            Constants.WITH_NAV_DRAWER,
                            null,
                            null
                        )
                    } else {
                        switchView(0, "")
                    }
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        message!!
                    )
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun callUploadLkDocuments(
        docName: String,
        mFilePath: String,
        selectedPos: Int
    ) {
        if (CommonUtils.isOnline(activity!!)) {
            var multipartBody: MultipartBody.Part? = null
            var fileName = ""
            var requestDocStr = docName
            var requestDocs = RequestBody.create("text/plain".toMediaTypeOrNull(), requestDocStr)
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
                multipartBody = MultipartBody.Part.createFormData("myfile", fileName, requestFile)
            }
            viewModel?.uploadFile(
                "Bearer " + mPreferenceUtils?.getValue(Constants.USER_TOKEN),
                multipartBody,
                requestDocs
            )?.observe(this, androidx.lifecycle.Observer {
                it?.run {
                    CommonUtils.printLog("RESPONSE", Gson().toJson(this))
                    if (status) {
                        customFormList?.get(selectedPos)?.ext = data?.ext
                        customFormList?.get(selectedPos)?.ansValue = data?.url
                        mCustomFileAdapter?.onRefreshAdapter(
                            customFormList,
                            pos = selectedPos,
                            flag = false
                        )
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

    companion object {
        fun newInstance(
            context: Activity,
            prePostOrderPojo: PrePlaceOrderPojo
        ): Fragment {
            val fragment = CustomFormFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", prePostOrderPojo)
            fragment.arguments = bundle
            return fragment
        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }


}