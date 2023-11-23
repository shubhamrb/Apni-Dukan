package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.BuildConfig
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.response.DocumentData
import com.mamits.apnaonlines.userv.ui.adapter.DocumentAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CameraGalleryFragment
import com.mamits.apnaonlines.userv.util.CameraGalleryUtils
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.util.FilePathUtil
import com.mamits.apnaonlines.userv.util.ImageOrientationChecker
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.image_back
import kotlinx.android.synthetic.main.common_toolbar_normal.view.toolbar_title
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_container
import kotlinx.android.synthetic.main.fragment_stores.view.btn_next
import kotlinx.android.synthetic.main.fragment_update_documents.edt_document_name
import kotlinx.android.synthetic.main.fragment_update_documents.image_file_close
import kotlinx.android.synthetic.main.fragment_update_documents.ll_selected_file
import kotlinx.android.synthetic.main.fragment_update_documents.text_add_document
import kotlinx.android.synthetic.main.fragment_update_documents.text_file_upload
import kotlinx.android.synthetic.main.fragment_update_documents.text_filename
import kotlinx.android.synthetic.main.fragment_update_documents.view.rv_docs
import kotlinx.android.synthetic.main.layout_empty.relative_empty
import kotlinx.android.synthetic.main.layout_progress.relative_progress
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class UpdateDocumentsFragment : BaseFragment(), OnItemClickListener,
    CameraGalleryFragment.Listener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    private var documentAdapter: DocumentAdapter? = null
    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mPictureType = ""
    private var cameraUtils: CameraGalleryUtils = CameraGalleryUtils()
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    var mDocList: ArrayList<DocumentData>? = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_update_documents, container, false)
        cameraUtils?.setContext(requireActivity())
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
        view?.image_back?.setOnClickListener { }
        view?.toolbar_title?.text = "Documents"
        view?.image_back?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        view.run {
            text_file_upload?.setOnClickListener {
                val instance = CameraGalleryFragment.newInstance(2)
                instance.show(childFragmentManager, "camera_gallery")
            }
            image_file_close?.setOnClickListener {
                cameraUtils.mCurrentPhotoPath = ""
                text_filename.text = ""
                ll_selected_file.visibility = View.GONE
            }
            text_add_document?.setOnClickListener {
                if (edt_document_name.text.toString()?.isEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter document name"
                    )
                } else if (text_filename.text.toString()?.isEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please select your document"
                    )
                } else {
                    callAddDocument()
                }
            }
        }
        view.rv_docs?.setHasFixedSize(true)
        view.rv_docs?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        documentAdapter = DocumentAdapter(
            activity,
            false,
            this
        )
        view.rv_docs?.adapter = documentAdapter
        callDocuments(CURRENT_PAGE);

        view.btn_next.setOnClickListener {
            CURRENT_PAGE++
            callDocuments(CURRENT_PAGE)
        }
    }

    private fun callDocuments(current_page: Int) {
        if (CommonUtils.isOnline(requireActivity())) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                start = current_page.toString(),
                pagelength = LIMIT.toString()
            )
            viewModel.getDocuments(commonRequestObj)
                .observe(viewLifecycleOwner, Observer {
                    it?.run {
                        if (status) {
                            switchView(1, "")
                            mDocList?.addAll(it.data)
                            if (it.next) {
                                view?.btn_next!!.visibility = View.VISIBLE
                            } else {
                                view?.btn_next!!.visibility = View.GONE
                            }
                            setDataToUI(mDocList!!)
                        } else {
                            switchView(0, "")
                            CommonUtils.createSnackBar(
                                activity?.findViewById(android.R.id.content)!!,
                                resources.getString(R.string.no_net)!!
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

    fun setDataToUI(data: ArrayList<DocumentData>) {
        data.let {
            if (it.size >= 0) {
                documentAdapter?.setList(data!!)
            }
        }
    }

    private fun callAddDocument() {
        if (CommonUtils.isOnline(requireActivity())) {
            switchView(3, "")
            var fileData: MultipartBody.Part? = null
            var fileName = ""
            var mFilePath = text_filename.text.toString()

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
                fileData = MultipartBody.Part.createFormData("file", fileName, requestFile)
            }

            /* if (file != null) fileData = MultipartBody.Part.createFormData(
                 "file",
                 file!!.name,
                 RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
             )*/

            getCommonRequestObj()
            val docNameBody: RequestBody =
                RequestBody.create(MultipartBody.FORM, edt_document_name.text.toString().trim())

            viewModel.addDocument(
                getCommonRequestObj().headerInfo?.Authorization!!,
                fileData,
                docNameBody
            ).observe(viewLifecycleOwner, Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        cameraUtils.mCurrentPhotoPath = ""
                        edt_document_name.text?.clear()
                        text_filename.text = ""
                        ll_selected_file.visibility = View.GONE
                        /*update list*/
                        mDocList?.clear()
                        CURRENT_PAGE = START_PAGE
                        callDocuments(CURRENT_PAGE);
                    }
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        message
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

    private fun callDeleteDocument(obj: Any?) {
        if (CommonUtils.isOnline(requireActivity())) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                doc_id = obj.toString()
            )
            viewModel.deleteDocument(
                commonRequestObj
            ).observe(viewLifecycleOwner, Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        /*refresh list*/
                        mDocList?.clear()
                        CURRENT_PAGE=START_PAGE
                        callDocuments(CURRENT_PAGE);
                    }
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        message
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

    override fun onCameraGalleryClicked(position: Int) {
        when (position) {
            0 -> {
                if (isCameraStoragePermissionGranted(requireActivity())) {
                    mPictureType = Constants.CAMERA
                    dispatchTakePictureIntent(requireActivity())
                } else {
                    checkCameraStoragePermissions(activity!!)
                }
            }

            1 -> {
                if (isCameraStoragePermissionGranted(requireActivity())) {
                    mPictureType = Constants.GALLERY
                    choosePhotoFromGallary(requireActivity())
                } else {
                    checkCameraStoragePermissions(requireActivity())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_PERMISSIONS -> {
                if (mPictureType == Constants.CAMERA) {
                    if (isCameraStoragePermissionGranted(requireActivity())) {
                        mPictureType = Constants.CAMERA
                        dispatchTakePictureIntent(requireActivity())
                    } else {
                        checkCameraStoragePermissions(requireActivity())
                    }
                } else if (mPictureType == Constants.GALLERY) {
                    if (isCameraStoragePermissionGranted(requireActivity())) {
                        mPictureType = Constants.GALLERY
                        choosePhotoFromGallary(requireActivity())
                    } else {
                        checkCameraStoragePermissions(requireActivity())
                    }
                } else {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CAMERA -> {
                    var image = cameraUtils.mCurrentPhotoPath

                    image.let {
                        try {
                            CommonUtils.printLog("RESULT_PATH", image)
                            val file = cameraUtils.createImageFile(
                                requireActivity(),
                                ".jpg"
                            )
                            ImageOrientationChecker.getCopyOfImage(
                                File(image),
                                file!!
                            )
                            cameraUtils.mCurrentPhotoPath = file!!.absolutePath

                            ll_selected_file.visibility = View.VISIBLE
                            text_filename.text = cameraUtils.mCurrentPhotoPath
                        } catch (e: Exception) {
                            e.printStackTrace()
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
                                        requireActivity(),
                                        contentStr
                                    )
                                val fileRealPath = FilePathUtil.getPath(activity, it?.data)
                                CommonUtils.printLog(
                                    "RESULT_PATH",
                                    contentStr?.toString()!! + "  " + extension + "  " + fileRealPath
                                )
                                try {
                                    if (true/*CommonUtils.isFileLimitNotExceddedEarlySalary(fileRealPath)*/) {
                                        if (extension.equals(
                                                ".jpg",
                                                true
                                            ) || extension.equals(
                                                ".jpeg",
                                                true
                                            ) || extension.equals(".png", true)
                                        ) {
                                            val file = cameraUtils.createImageFile(
                                                requireActivity(),
                                                extension
                                            )
                                            ImageOrientationChecker.getCopyOfImage(
                                                File(fileRealPath),
                                                file!!
                                            )
                                            cameraUtils.mCurrentPhotoPath = file!!.absolutePath
                                        } else {
                                            cameraUtils.mCurrentPhotoPath = fileRealPath
                                        }
                                        var image = cameraUtils.mCurrentPhotoPath

                                        ll_selected_file.visibility = View.VISIBLE
                                        text_filename.text = image
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

    fun dispatchTakePictureIntent(activity: Activity) {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            try {
                val photoFile: File = createImageFile(activity, cameraUtils = cameraUtils)
                val photoURI: Uri =
                    FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    takePictureIntent,
                    Constants.REQUEST_CAMERA
                )
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("CAMERA", e.printStackTrace().toString())
            }
        } catch (e: Exception) {
            CommonUtils.printLog("CAMERA", e.printStackTrace().toString())
        }
    }

    fun choosePhotoFromGallary(activity: Activity) {

        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
        gallery.addCategory(Intent.CATEGORY_OPENABLE)
        gallery.type = "*/*"
        gallery.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/*", "application/pdf", "application/msword", "application/vnd.ms-excel")
        )
        try {
            gallery.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(gallery, Constants.REQUEST_GALLERY)
        } catch (e: Exception) {
            CommonUtils.printLog("Gallary", e.printStackTrace().toString())
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
                    ll_container?.visibility = View.VISIBLE
                }
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

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.btn_delete -> {
                /*delete doc*/
                callDeleteDocument(obj);
            }
        }
    }
}