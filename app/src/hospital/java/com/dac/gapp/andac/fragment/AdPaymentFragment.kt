package com.dac.gapp.andac.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.util.Common
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.UiUtil
import com.google.firebase.firestore.SetOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_ad_payment.*
import timber.log.Timber
import java.util.*

class AdPaymentFragment : BaseFragment() {

    companion object {
        private const val EXTRA_AD = "EXTRA_AD"

        fun newInstance(ad: Ad): AdPaymentFragment {
            val fragment = AdPaymentFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_AD, ad)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var ad: Ad
    private var photoUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ad_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            ad = it.getSerializable(EXTRA_AD) as Ad
            prepareUi()
            setupEventsOnViewCreated()
        } ?: run {
            Timber.e("필수 파라미터 누락")
            fragmentManager?.popBackStack()
        }
    }

    private fun prepareUi() {
        context!!.getToolBar().setTitle(getString(R.string.paying_for_a_hospital_ad))
        ad.let {
            txtviewAd.text = getString(it.titleResId)
            UiUtil.visibleOrGone(it.isAdTypeEvent(), btnSelectMyEvent)
        }
    }

    private fun setupEventsOnViewCreated() {
        btnSelectMyEvent.setOnClickListener {
            MyToast.showShort(requireContext(), "TODO : 원상아~ 광고중에 이벤트 관련한건 맨 나중에 해주라 그거 이벤트 리스트가 필요하자너 ㅎ")
        }

        layoutUploadPhoto.setOnClickListener {
            TedPermission.with(context)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            context?.startAlbumImageUri()
                                    ?.addOnSuccessListener {
                                        photoUri = it
                                        Glide.with(this@AdPaymentFragment).load(it).into(imgviewPhoto)
                                        txtviewUploadPhoto.setBackgroundResource(R.color.AF000000)
                                    }
                        }

                        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                            MyToast.showShort(context, "권한이 거절되어 이미지를 가져올 수 없습니다!!")
                        }
                    })
                    .setDeniedMessage("해당 권한이 없을 경우 이미지를 업로드 할 수 없습니다!!")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
        }

        btnPay.setOnClickListener {
            // TODO 광고 결제 모듈은 어떻게??
            MyToast.showShort(requireContext(), "TODO : 광고 결제 완료 후 사진 업로드 및 광고 기한 업로드 !!")
            context?.showProgressDialog()
            photoUri?.let { uri ->
                context?.getUid()?.let { uid ->
                    ad.uploadFileName.let { uploadFileName -> context?.getAdStorageRef()?.child(uid)?.child(uploadFileName)?.putFile(uri) }
                }

                context?.getUid()?.let {
                    context?.getAds()?.document(it)?.set(AdInfo(it, ad.adType.name, Date(), Common.getDate(2018, 8, 30)), SetOptions.merge())?.addOnSuccessListener {
                        Toast.makeText(context, "광고 정보 저장 성공", Toast.LENGTH_SHORT).show()
                    }?.addOnCanceledListener {
                        Toast.makeText(context, "광고 정보 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }?.addOnSuccessListener {
                MyToast.showShort(context, "광고 결제 완료")
            }?.addOnCompleteListener {
                context?.hideProgressDialog()
                fragmentManager?.popBackStack()
            }
        }

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