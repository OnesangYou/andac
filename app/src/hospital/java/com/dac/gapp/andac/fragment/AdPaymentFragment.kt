package com.dac.gapp.andac.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.UiUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_column_write.*
import kotlinx.android.synthetic.main.fragment_ad_payment.*
import java.util.Collections.rotate

class AdPaymentFragment : BaseFragment() {

    companion object {
        private const val EXTRA_AD_NAME = "EXTRA_AD_NAME"

        fun newInstance(adName: String): AdPaymentFragment {
            val fragment = AdPaymentFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_AD_NAME, adName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ad_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnViewCreated()
    }

    private fun prepareUi() {
        context!!.getToolBar().setTitle(getString(R.string.paying_for_a_hospital_ad))
        txtviewAd.text = arguments!!.getString(EXTRA_AD_NAME)
    }

    private var photoUri: Uri? = null

    private fun setupEventsOnViewCreated() {
        layoutUploadPhoto.setOnClickListener({
            TedPermission.with(context)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            context?.startAlbumImageUri()
                                    ?.addOnSuccessListener {
                                        photoUri = it
                                        Glide.with(this@AdPaymentFragment).load(it).into(imgviewPhoto)
                                    }
                        }

                        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                            MyToast.showShort(context, "권한이 거절되어 이미지를 가져올 수 없습니다!!")
                        }
                    })
                    .setDeniedMessage("해당 권한이 없을 경우 이미지를 업로드 할 수 없습니다!!")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
        })
        btnPay.setOnClickListener({
            MyToast.showShort(requireContext(), "TODO : 광고 결제하기")
            context?.showProgressDialog()
            photoUri?.let { uri ->
                context?.getUid()?.let {
                    context?.getAdStorageRef()?.child(it)?.putFile(uri)
                }

            }
                    ?.addOnSuccessListener { MyToast.showShort(context, "광고 결제 완료"); }
                    ?.addOnCompleteListener {
                        context?.hideProgressDialog()
                        fragmentManager?.popBackStack()
                    }
        })

    }

    private fun startGalleryActivity() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        startActivityForResult(intent, RequestCode.GALLERY.value)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.GALLERY.value) {
            if (data != null)
                uploadPhoto(data.data)
        }
    }

    fun uploadPhoto(imgUri: Uri) {
        val imagePath = getRealPathFromURI(imgUri) // path 경로
        val exif = ExifInterface(imagePath)
        val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val exifDegree = exifOrientationToDegrees(exifOrientation)
        val bitmap = BitmapFactory.decodeFile(imagePath) //경로를 통해 비트맵으로 전환
        imgviewPhoto.setImageBitmap(rotate(bitmap, exifDegree)) //이미지 뷰에 비트맵 넣기
        txtviewUploadPhoto.setBackgroundResource(R.color.AF000000)
    }

    fun getRealPathFromURI(contentUri: Uri): String {
        var column_index = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(contentUri, proj, null, null, null)
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(column_index)
    }


    fun exifOrientationToDegrees(exifOrientation: Int): Float {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90F
            ExifInterface.ORIENTATION_ROTATE_180 -> 180F
            ExifInterface.ORIENTATION_ROTATE_270 -> 270F
            else -> 0F
        }
    }

    fun rotate(src: Bitmap, degree: Float): Bitmap {
        // Matrix 객체 생성
        val matrix = Matrix()
        matrix.postRotate(degree)
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

}