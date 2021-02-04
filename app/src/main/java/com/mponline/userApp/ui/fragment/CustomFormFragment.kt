package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.CustomFieldObj
import com.mponline.userApp.ui.adapter.CustomFileAdapter
import com.mponline.userApp.ui.adapter.CustomFormAdapter
import com.mponline.userApp.ui.adapter.InstructionAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.*
import com.mponline.userApp.util.FileUtils.getLocalPath
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_custom_form.view.*
import java.io.File
import java.lang.Exception

class CustomFormFragment : BaseFragment(), OnItemClickListener, CameraGalleryFragment.Listener  {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var customFormList:ArrayList<CustomFieldObj> = ArrayList()
    var customFilesList:ArrayList<CustomFieldObj> = ArrayList()
    private var cameraUtils: CameraGalleryUtils = CameraGalleryUtils()
    var mPictureType = ""
    var mSelectedPos = -1
    var mCustomFileAdapter:CustomFileAdapter? = null

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
        if(context!=null){
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.relative_frag?.setOnClickListener {

        }

        //Forms
        customFormList?.add(
            CustomFieldObj(
          fieldType = "edit", hintName = "First Name*", min = "3", max = "20", selectedFilePath = ""
        ))
        customFormList?.add(
            CustomFieldObj(
          fieldType = "edit", hintName = "Middle Name*", min = "3", max = "20", selectedFilePath = ""
        ))
        customFormList?.add(
            CustomFieldObj(
          fieldType = "edit", hintName = "Last Name*", min = "3", max = "20", selectedFilePath = ""
        ))
        customFormList?.add(
            CustomFieldObj(
          fieldType = "edit", hintName = "Mother Name*", min = "3", max = "20", selectedFilePath = ""
        ))
        customFormList?.add(
            CustomFieldObj(
          fieldType = "edit", hintName = "Class 10th percentage*", min = "2", max = "4", selectedFilePath = ""
        ))
        view?.rv_custom_form?.layoutManager =
            LinearLayoutManager(
                activity, RecyclerView.VERTICAL, false
            )
        view?.rv_custom_form?.adapter = CustomFormAdapter(
            activity,
            this, customFormList
        )

        //Custom files
        customFilesList?.add(
            CustomFieldObj(
                fieldType = "file", hintName = "Choose Aadhar file", min = "3", max = "20", selectedFilePath = ""
            ))
        customFilesList?.add(
            CustomFieldObj(
                fieldType = "file", hintName = "Choose 10th marksheet", min = "3", max = "20", selectedFilePath = ""
            ))

        view?.rv_custom_files?.layoutManager =
            LinearLayoutManager(
                activity, RecyclerView.VERTICAL, false
            )
        mCustomFileAdapter = CustomFileAdapter(
            activity,
            this, customFilesList
        )
        view?.rv_custom_files?.adapter = mCustomFileAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                Constants.REQUEST_CAMERA -> {
                    var image = cameraUtils.mCurrentPhotoPath
                    CommonUtils.printLog("RESULT_PATH", image)
                    image?.let {
                        try {
//                            ImageOrientationChecker.imagePreviewCamera(File(image))
                            if(mCustomFileAdapter!=null){
                                customFilesList?.get(mSelectedPos)?.selectedFilePath = image
                                mCustomFileAdapter?.onRefreshAdapter(customFilesList)
                            }
                        }catch (e:Exception){
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
                                        val file = cameraUtils.createImageFile(
                                            activity!!,
                                            extension
                                        )
                                        ImageOrientationChecker.getCopyOfImage(
                                            File(fileRealPath),
                                            file
                                        )
                                        cameraUtils.mCurrentPhotoPath = file.absolutePath
                                        var image = cameraUtils.mCurrentPhotoPath
                                        if(mCustomFileAdapter!=null){
                                            customFilesList?.get(mSelectedPos)?.selectedFilePath = image
                                            mCustomFileAdapter?.onRefreshAdapter(customFilesList)
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
        }else{

        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.text_file_upload->{
                mSelectedPos = pos
                showAttachOptions()
            }
            R.id.image_file_close->{
                customFilesList?.get(pos)?.selectedFilePath = ""
                mCustomFileAdapter?.onRefreshAdapter(customFilesList)
            }
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
                startActivityForResult(takePictureIntent,
                    Constants.REQUEST_CAMERA)
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
        gallery.type = "image/*"

        if (gallery.resolveActivity(activity.packageManager) != null) {
            gallery.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(gallery, Constants.REQUEST_GALLERY)
        }
    }


}