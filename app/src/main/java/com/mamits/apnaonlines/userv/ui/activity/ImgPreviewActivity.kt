package com.mamits.apnaonlines.userv.ui.activity

import android.content.Intent
import android.os.Bundle
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.util.ImageGlideUtils
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*

class ImgPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_preview)
        toolbar_title.text = ""
        var imgPath = intent?.getStringExtra("img")
        var docName = intent?.getStringExtra("docName")
        if(!imgPath?.isNullOrEmpty()!! && (imgPath?.endsWith(".jpg",true) || imgPath?.endsWith(".jpeg",true) || imgPath?.endsWith(".png",true))){
            ImageGlideUtils.loadLocalImage(this, imgPath, image_preview)
        }

        text_cancel?.setOnClickListener {
            finish()
        }
        text_save?.setOnClickListener {
            var intent:Intent = Intent()
            intent?.putExtra("img", imgPath)
            intent?.putExtra("docName", docName)
            intent?.putExtra("txt", edt_caption.text.toString().trim())
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}