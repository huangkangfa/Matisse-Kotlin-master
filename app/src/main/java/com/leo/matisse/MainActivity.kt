package com.leo.matisse

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.matisse.Matisse
import com.matisse.MimeTypeManager
import com.matisse.entity.CaptureStrategy
import com.matisse.entity.ConstValue
import com.matisse.utils.Platform
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.btn_media_store).setOnClickListener {
            RxPermissions(this@MainActivity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe {
                    if (!it) {
                        Toast.makeText(
                            this@MainActivity, R.string.permission_request_denied, Toast.LENGTH_LONG
                        ).show()
                        return@subscribe
                    }
                    openMatisse()
                }
        }
    }

    private fun openMatisse() {
        Matisse.from(this@MainActivity)
            .choose(MimeTypeManager.ofAll())
            .countable(false)
            .capture(true)
            .isCrop(true)
            .isCircleCrop(true)
            .maxSelectable(1)
            .theme(R.style.JCStyle)
            .captureStrategy(
                CaptureStrategy(
                    true,
                    "${Platform.getPackageName(this@MainActivity)}.fileprovider"
                )
            )
            .thumbnailScale(0.8f)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .imageEngine(Glide4Engine())
            .forResult(ConstValue.REQUEST_CODE_CHOOSE)
    }

    private fun showToast(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return

        if (requestCode == ConstValue.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            var string = ""
            val uriList = Matisse.obtainResult(data) ?: return

            uriList.forEach {
                string += it.toString() + "\n"
            }

            Glide.with(this).load(uriList[0]).into(iv_image)
            string = "\n\n$string"

            text.text = "\n\n$string"
        }
    }
}
