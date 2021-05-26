package com.mponline.userApp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mponline.userApp.R
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.item_service.view.*

class ImgPreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_preview)
        toolbar_title.text = ""
        var imgPath = intent?.getStringExtra("img")
        if(!imgPath?.isNullOrEmpty()!! && (imgPath?.endsWith(".jpg",true) || imgPath?.endsWith(".jpeg",true) || imgPath?.endsWith(".png",true))){
            ImageGlideUtils.loadLocalImage(this, imgPath, image_preview)
        }

        text_cancel?.setOnClickListener {
            finish()
        }
        text_save?.setOnClickListener {
            var intent:Intent = Intent()
            intent?.putExtra("img", imgPath)
            intent?.putExtra("txt", edt_caption.text.toString().trim())
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}