package com.dac.gapp.andac.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.HospitalEventListActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentHospitalAdApplicationBinding
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.Extra
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.extension.visibleOrGone
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.RxBus
import com.dac.gapp.andac.util.toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.jetbrains.anko.startActivityForResult
import timber.log.Timber
import java.util.*

class HospitalAdApplicationFragment : BaseFragment() {

    companion object {
        private const val EXTRA_AD = "EXTRA_AD"
        private const val EXTRA_FOR_WHAT = "EXTRA_FOR_WHAT"
        private const val EXTRA_FOR_PAY = "EXTRA_FOR_PAY"
        private const val EXTRA_FOR_EDIT = "EXTRA_FOR_EDIT"
        private const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
        private const val REQUEST_EVENT_SELECT = 1111
        fun newInstanceForPay(ad: Ad): HospitalAdApplicationFragment {
            val fragment = HospitalAdApplicationFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_AD, ad)
            bundle.putSerializable(EXTRA_FOR_WHAT, EXTRA_FOR_PAY)
            fragment.arguments = bundle
            return fragment
        }


        fun newInstanceForEdit(ad: Ad, photoUrl: String?): HospitalAdApplicationFragment {
            val fragment = HospitalAdApplicationFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_AD, ad)
            bundle.putSerializable(EXTRA_FOR_WHAT, EXTRA_FOR_EDIT)
            bundle.putSerializable(EXTRA_PHOTO_URL, photoUrl)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mAd: Ad
    private lateinit var forWhat: String
    private lateinit var mBinding: FragmentHospitalAdApplicationBinding

    private var photoUrl: String? = null
    private var photoUri: Uri? = null

    private var eventObjectId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater, R.layout.fragment_hospital_ad_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            mAd = it.getSerializable(EXTRA_AD) as Ad
            forWhat = it.getSerializable(EXTRA_FOR_WHAT) as String
            photoUrl = it.getSerializable(EXTRA_PHOTO_URL) as String?
            prepareUi()
            setupEventsOnViewCreated()
        } ?: run {
            Timber.e("필수 파라미터 누락")
            fragmentManager?.popBackStack()
        }
    }

    private fun prepareUi() {
        mBinding = getBinding()
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.back)
            forWhat.let { forWhat ->
                mAd.let { ad ->
                    if (ad == Ad.SEARCH_HOSPITAL_BANNER_AD) mBinding.layoutUploadPhoto.visibleOrGone(false)
                    mBinding.txtviewAd.text = getString(ad.titleResId)
                    mBinding.btnSelectMyEvent.visibleOrGone(ad.isAdTypeEvent())
                    if (forWhat == EXTRA_FOR_PAY) {
                        context.setActionBarCenterText(R.string.apply_for_a_hospital_ad)
                        mBinding.btnNext.text = getString(R.string.apply)
                    } else if (forWhat == EXTRA_FOR_EDIT) {
                        context.setActionBarCenterText(R.string.edit_for_hospital_ad)
                        mBinding.btnNext.text = getString(R.string.edit)
                        photoUrl?.let {
                            mBinding.imgviewPhoto.loadImageAny(it)
                            mBinding.txtviewUploadPhoto.setBackgroundResource(R.color.AF000000)
                        }
                    }
                }
            }
        }
    }

    private fun setupEventsOnViewCreated() {
        mBinding.btnSelectMyEvent.setOnClickListener {
            context?.startActivityForResult<HospitalEventListActivity>(REQUEST_EVENT_SELECT, Extra.IS_EVENT_SELECT.name to true)
        }

        mBinding.layoutUploadPhoto.setOnClickListener { _ ->
            TedPermission.with(context)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            context?.getAlbumImage{
                                photoUri = it
                                mBinding.imgviewPhoto.loadImageAny(it)
                                mBinding.txtviewUploadPhoto.setBackgroundResource(R.color.AF000000)
                            }?.apply { context?.disposables?.add(this) }
                        }

                        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                            MyToast.showShort(context, "권한이 거절되어 이미지를 가져올 수 없습니다!!")
                        }
                    })
                    .setDeniedMessage("해당 권한이 없을 경우 이미지를 업로드 할 수 없습니다!!")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
        }

        mBinding.btnNext.setOnClickListener { _ ->
            if (mAd == Ad.SEARCH_HOSPITAL_BANNER_AD) {
                context?.showProgressDialog()
                saveAd("")
                        ?.addOnCompleteListener {
                            toast(if (it.isSuccessful) "광고 신청이 완료되었습니다." else "광고 신청에 실패했습니다. 관리자에게 문의 해주세요.")
                            context?.hideProgressDialog()
                            fragmentManager?.popBackStack()
                        }
            } else {
                photoUri?.let { uri ->
                    context?.showProgressDialog()
                    savePhoto(uri)
                            ?.addOnSuccessListener { photoUrl ->
                                saveAd(photoUrl)
                                        ?.addOnCompleteListener {
                                            toast(if (it.isSuccessful) "광고 신청이 완료되었습니다." else "광고 신청에 실패했습니다. 관리자에게 문의 해주세요.")
                                            context?.hideProgressDialog()
                                            fragmentManager?.popBackStack()
                                        }
                            }?.addOnFailureListener {
                                toast("광고 신청에 실패했습니다. 관리자에게 문의 해주세요.")
                                context?.hideProgressDialog()
                                fragmentManager?.popBackStack()
                            }
                } ?: toast("광고 사진을 업로드 해주세요!!")
            }
        }

        // RxBus Listen
        RxBus.listen(ActivityResultEvent::class.java).subscribe { activityResultEvent ->
            activityResultEvent?.apply {
                if (requestCode == REQUEST_EVENT_SELECT && resultCode == Activity.RESULT_OK) {
                    eventObjectId = data?.getStringExtra(Extra.OBJECT_KEY.name)
                }
            }
        }.apply { context?.disposables?.add(this) }
    }

    private fun savePhoto(uri: Uri): Task<String>? {
        return context?.run {
            getUid()?.let { uid ->
                getAdStorageRef().child(uid).child(mAd.uploadFileName).putFile(uri)
                        .continueWith {
                            it.result.downloadUrl.toString()
                        }
            }
        }
    }

    private fun saveAd(photoUrl: String): Task<Void>? {
        return context?.run {
            getUid()?.let { uid ->
                getDb().collection(mAd.collectionName).document(uid)
                        .set(
                                AdInfo(uid, photoUrl, false, Date(), Date(), eventObjectId ?: ""),
                                SetOptions.merge()
                        )
            }
        }
    }

}