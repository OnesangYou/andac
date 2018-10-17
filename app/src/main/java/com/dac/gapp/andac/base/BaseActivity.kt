@file:Suppress("DEPRECATION")

package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.dac.gapp.andac.BuildConfig
import com.dac.gapp.andac.LoginActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.SplashActivity
import com.dac.gapp.andac.dialog.ConsultContentDialog
import com.dac.gapp.andac.enums.AdCountType
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.model.firebase.*
import com.dac.gapp.andac.util.Common
import com.dac.gapp.andac.util.RxBus
import com.dac.gapp.andac.util.UiUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonParser
import com.gun0912.tedonactivityresult.TedOnActivityResult
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_base.*
import org.jetbrains.anko.alert
import timber.log.Timber
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    val KBJ = "KBJ"
    val GOTO_MYPAGE = "goToMyPage"
    val OBJECT_KEY = "OBJECT_KEY"

    val profilePicJpgStr = "profilePic.jpg"
    val bankAccountPicJpgStr = "bankAccountPic.jpg"
    val busiRegiPicJpgStr = "busiRegiPic.jpg"

    val PageListSize: Long = 3


    fun getUid(): String? {
        return getCurrentUser()?.uid
    }

    fun getDb(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // hospital
    fun getHospitals(): CollectionReference = getDb().collection("hospitals")

    fun getHospital(): DocumentReference? = getUid()?.let { getHospitals().document(it) }
    fun getHospital(key: String) = getHospitals().document(key)
    fun getHospitalsStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("hospitals")
    fun getHospitalContents(uid: String? = getUid()) = uid?.let {
        getDb().collection("hospitalContents").document(it)
    }

    fun getHospitalLikeUsers(hospitalKey: String) = getHospitalContents(hospitalKey)?.collection("likeUsers")

    fun getEventLikeUsers(eventKey: String) = getEvent(eventKey)?.collection("likeUsers")

    fun getHospitalEvent(eventKey: String) = getHospitalEvents()?.document(eventKey)

    fun getHospitalColumn(columnKey: String) = getHospitalColumns()?.document(columnKey)

    fun getAdRequests(): CollectionReference = getDb().collection("adRequests")

    fun getAdRequest(): DocumentReference? = getUid()?.let { getAdRequests().document(it) }

    fun getUsers(): CollectionReference = getDb().collection("users")

    fun getUser(): DocumentReference? = getUid()?.let { getUsers().document(it) }

    fun getUser(uuid: String): DocumentReference? = getUsers().document(uuid)

    fun getAuth(): FirebaseAuth? = FirebaseAuth.getInstance()

    fun isLogin() = getAuth()?.currentUser != null

    fun getCurrentUser(): FirebaseUser? = getAuth()?.currentUser
            .also {
//                it?:goToLogin() // 로그아웃 상태
                if (!isExistPhoneNumber()) goToLogin()
            } // 폰등록 안되 있는 경우는 login 이동

    fun isExistPhoneNumber(): Boolean {
        return true // TODO : 문자인증 게시판 채우기 위해 한시적으로 막아놓음, 10월 초에 풀기
        return !(getAuth()?.currentUser?.phoneNumber.isNullOrEmpty())
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        mProgressDialog?.let {
            if (it.isShowing) it.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    private var mToast: Toast? = null
    // Context 클래스에 toast 함수 추가
    fun toast(message: CharSequence) {
        // this는 Context 인스턴스!
        Toast.makeText(this, message, Toast.LENGTH_SHORT).let {
            mToast = it
            it.show()
        }
        Timber.d(message.toString())
    }

    private var backKeyPressedTime: Long = 0

    fun backPressed() {

        val time = 2000
        if (System.currentTimeMillis() <= backKeyPressedTime + time) {
            mToast?.cancel()
            ActivityCompat.finishAffinity(this@BaseActivity)
        } else {
            showGuide()
            backKeyPressedTime = System.currentTimeMillis()
        }

    }

    private fun showGuide() {
        toast("한번 더 누르면 종료됩니다.")
    }

    fun getUserInfo(uid: String? = getUid()): Task<UserInfo>? {
        return uid?.let { it -> getUser(it)?.get()?.continueWith { it.result.toObject(UserInfo::class.java) } }
    }

    fun isHospital(): Boolean = BuildConfig.FLAVOR.startsWith("hospital")

    fun isUser(): Boolean = BuildConfig.FLAVOR.startsWith("user")

    private val RC_TAKE_PICTURE = 101
    fun getAlbumImage(): Observable<Uri>? {
        // Pick an image from storage
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, RC_TAKE_PICTURE)

        return RxBus.listen(Uri::class.java).take(1)
    }

    fun getAlbumImage(function: (uri : Uri) -> Unit): Disposable? {
        return getAlbumImage()?.subscribe{
            function.invoke(it)
        }?.apply { disposables.add(this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { mFileUri ->
                    RxBus.publish(mFileUri)
                }
            } else {
                toast("Taking picture failed.")
            }
        }
    }

    // Ads
    fun getAdStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("ads")

    // Boards
    fun getBoardStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("boards")
    fun getBoards(): CollectionReference = getDb().collection("boards")
    fun getBoard(key: String): DocumentReference? = if (key.isEmpty()) null else getBoards().document(key)
    fun getReplies(boardKey: String) = getBoard(boardKey)?.collection("replies")
    fun getLikeUsers(boardKey: String) = getBoard(boardKey)?.collection("likeUsers")


    // User Contents
    fun getUserContents(uid: String? = getUid()) = uid?.let { if(isUser()) getDb().collection("userContents").document(it) else null }
    fun getUserBoards() = getUserContents()?.collection("boards")
    fun getUserLikeBoards() = getUserContents()?.collection("likeBoards")
    fun getUserLikeBoard(boardKey: String) = getUserContents()?.collection("likeBoards")?.document(boardKey)

    fun getViewedColumns() = getUserContents()?.collection("viewedColumns")
    fun getUserEvents() = getUserContents()?.collection("events")
    fun getUserEvent(eventKey: String) = getUserEvents()?.document(eventKey)

    fun getLikeHospitals() = getUserContents()?.collection("likeHospitals")
    fun getLikeHospital(hospitalKey : String) = getLikeHospitals()?.document(hospitalKey)

    fun getLikeEvents() = getUserContents()?.collection("likeEvents")
    fun getLikeEvent(eventKey : String) = getLikeEvents()?.document(eventKey)


    // Column
    fun getColumnStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("columns")

    fun getColumns(): CollectionReference = getDb().collection("columns")
    fun getColumn(key: String): DocumentReference? = if (key.isEmpty()) null else getColumns().document(key)
    fun getHospitalColumns() = getHospitalContents()?.collection("columns")
    fun getColumnViewedUsers(columnKey : String) = getColumn(columnKey)?.collection("viewedUsers")

    // Event
    fun getEventStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("events")

    fun getEvents(): CollectionReference = getDb().collection("events")
    fun getEvent(key: String): DocumentReference? = if (key.isEmpty()) null else getEvents().document(key)
    fun getHospitalEvents() = getHospitalContents()?.collection("events")

    // Event Applicants
    fun getEventApplicants(eventKey: String) = getEvent(eventKey)?.collection("applicants")

    fun getEventApplicant(eventKey: String) = getUid()?.let { getEventApplicants(eventKey)?.document(it) }  // user 용


    private fun restartApp() {
        val mStartActivity = Intent(this, SplashActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    fun goToLogin(gotoMyPage: Boolean = false) {
        Intent(this, LoginActivity::class.java).let {
            if (gotoMyPage) it.putExtra(GOTO_MYPAGE, true)
            startActivity(it)
        }
    }

    fun goToLogin(function: () -> Unit) {
        Intent(this, LoginActivity::class.java).let {
            TedOnActivityResult.with(this)
                .setIntent(it).setListener { resultCode, data ->
                    if (resultCode == RESULT_OK) {
                        function.invoke()
                    }
                }.startActivityForResult()
        }
    }

    fun afterCheckLoginDo(function: () -> Unit){
        if(!isLogin())goToLogin(function)
        else function.invoke()
    }

    fun getHospitalInfo(uid: String? = getUid()) = uid?.let { s -> getHospital(s).get().continueWith { it.result.toObject(HospitalInfo::class.java) } }

    private var viewDataBinding: ViewDataBinding? = null
    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(this), layoutResID, layoutRoot, true)

        setSupportActionBar(layoutToolbar)
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewDataBinding> getBinding(): T {
        return viewDataBinding as T
    }

    fun showActionBar() {
        supportActionBar?.show()
    }

    fun hideActionBar() {
        supportActionBar?.hide()
    }

    fun showActionBarLeft() {
        UiUtil.visibleOrGone(true, layoutLeft, imgviewLeft, txtviewLeft)
    }

    fun hideActionBarLeft() {
        UiUtil.visibleOrGone(false, layoutLeft, imgviewLeft, txtviewLeft)
    }

    fun showActionBarCenter() {
        UiUtil.visibleOrGone(true, imgviewCenter, txtviewCenter)
    }

    fun hideActionBarCenter() {
        UiUtil.visibleOrGone(false, imgviewCenter, txtviewCenter)
    }

    fun showActionBarRight() {
        UiUtil.visibleOrGone(true, layoutRight, imgviewRight, txtviewRight)
    }

    fun hideActionBarRight() {
        UiUtil.visibleOrGone(false, layoutRight, imgviewRight, txtviewRight)
    }

    fun setActionBarLeftImage(resId: Int) {
        setActionBarImage(resId, imgviewLeft, txtviewLeft)
    }

    fun setActionBarCenterImage(resId: Int) {
        setActionBarImage(resId, imgviewCenter, txtviewCenter)
    }

    fun setActionBarRightImage(resId: Int) {
        setActionBarImage(resId, imgviewRight, txtviewRight)
    }

    private fun setActionBarImage(resId: Int, imageView: ImageView, textView: TextView) {
        textView.apply { text = "" }
        Glide.with(this).load(resId).into(imageView); UiUtil.visibleOrGone(true, imageView); UiUtil.visibleOrGone(false, textView)
    }

    fun setActionBarLeftText(resId: Int) {
        setActionBarLeftText(getString(resId))
    }

    fun setActionBarLeftText(text: String) {
        setActionBarText(text, txtviewLeft, imgviewLeft)
    }

    fun setActionBarCenterText(resId: Int) {
        setActionBarCenterText(getString(resId))
    }

    fun setActionBarCenterText(text: String) {
        setActionBarText(text, txtviewCenter, imgviewCenter)
    }

    fun setActionBarRightText(resId: Int) {
        setActionBarRightText(getString(resId))
    }

    fun setActionBarRightText(text: String) {
        setActionBarText(text, txtviewRight, imgviewRight)
    }

    private fun setActionBarText(text: String, textView: TextView, imageView: ImageView) {
        imageView.apply { setImageResource(0); setImageBitmap(null) }
        textView.text = text; UiUtil.visibleOrGone(true, textView); UiUtil.visibleOrGone(false, imageView)
    }

    fun setOnActionBarLeftClickListener(listener: View.OnClickListener) {
        layoutLeft.setOnClickListener(listener)
    }

    fun setOnActionBarRightClickListener(listener: View.OnClickListener) {
        layoutRight.setOnClickListener(listener)
    }

    fun changeFragment(newFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.layoutFragmentContainer, newFragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

    @SuppressLint("SetTextI18n")
    fun findPassword() {
        val email = AutoCompleteTextView(this@BaseActivity)
        email.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val builder = AlertDialog.Builder(this@BaseActivity)


        val title = TextView(this@BaseActivity)
        title.text = "[비밀번호 찾기]\n\n가입하신 메일을 알려주세요.\n비밀번호 변경 페이지를 메일로 보내드립니다."
        title.gravity = Gravity.CENTER
        title.setPadding(0, 90, 0, 40)
        title.textSize = 15f

        builder.setView(email)
                .setCustomTitle(title)
                .setPositiveButton("확인") { _, _ ->
                    val emailAddress = email.text.toString()

                    if (emailAddress != "") {
                        val auth = getAuth()
                        auth?.sendPasswordResetEmail(emailAddress)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        toast("메일 전송 완료")
                                    } else {
                                        toast("메일 주소가 올바르지 않습니다")
                                    }

                                }
                    } else {
                        toast("다시 시도해주세요")
                    }

                }.show()
    }

    // 중복 메일 검사
    fun checkDuplicatedEmail(emailStr: String): Task<Boolean>? {
        showProgressDialog()
        return Tasks.whenAllSuccess<QuerySnapshot>(
                getHospitals().whereEqualTo("email", emailStr).get(),
                getUsers().whereEqualTo("email", emailStr).get()
        )
                .addOnCompleteListener { hideProgressDialog() }
                .continueWith { task -> task.result.map { querySnapshot -> querySnapshot.isEmpty }.all { it } }
    }

    // 중복 닉네임 검사
    fun checkDuplicatedNickName(NickNameStr: String): Task<Boolean>? {
        showProgressDialog()
        return Tasks.whenAllSuccess<QuerySnapshot>(
                getUsers().whereEqualTo("nickName", NickNameStr).get()
        )
                .addOnCompleteListener { hideProgressDialog() }
                .continueWith { task -> task.result.map { querySnapshot -> querySnapshot.isEmpty }.all { it } }
    }

    // 텍스트가 변하면 tag reset
    fun resetTagEditTextChanged(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 입력되는 텍스트에 변화가 있을 때
                editText.tag = false
            }

            override fun afterTextChanged(arg0: Editable) {
                // 입력이 끝났을 때
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 입력하기 전에
            }
        })
    }

    fun showDeleteObjectDialog(objectStr: String, objectRef: DocumentReference) {

        alert(title = "$objectStr 삭제", message = "${objectStr} 삭제하시겠습니까?") {
            positiveButton("YES") { it ->
                // 삭제 진행
                showProgressDialog()
                objectRef.delete().addOnCompleteListener { hideProgressDialog() }.addOnSuccessListener { setResult(Activity.RESULT_OK); finish() }
            }

            negativeButton("NO") {}
        }.show()
    }

    fun isObjectModify() = !intent.getStringExtra(OBJECT_KEY).isNullOrBlank()
    fun dateFieldStr() = if (isObjectModify()) "updatedDate" else "createdDate"

    fun eventCancelDialog(objectId: String, function: () -> Unit) {
        alert(title = "이벤트 신청 취소", message = "이벤트 신청 취소하시겠습니까?") {
            positiveButton("YES") { _ ->
                showProgressDialog()
                FirebaseFirestore.getInstance().batch().run {
                    getUserEvent(objectId)?.let { delete(it) }
                    getEventApplicant(objectId)?.let { delete(it) }
                    commit()
                }
                        .addOnSuccessListener { function.invoke() }
                        .addOnCompleteListener { hideProgressDialog() }
            }
            negativeButton("NO") {}
        }.show()
    }

    private val listListenerRegistration = mutableListOf<ListenerRegistration>()
    // isNeedAllClear 가 True면, 이벤트 등록 전 이전에 등록된걸 모두 Clear한다
    fun addListenerRegistrations(listener: ListenerRegistration, isNeedAllClear : Boolean = false) {
        if(isNeedAllClear)  clearAllListenerRegistration()
        listListenerRegistration.add(listener)
    }
    fun clearAllListenerRegistration() = listListenerRegistration.forEach { it.remove() }

    override fun onDestroy() {
        disposables.clear()
        listListenerRegistration.forEach { it.remove() }
        super.onDestroy()
    }

     fun startReplyMenu(view : View, replyInfo : ReplyInfo) {
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.reply_menu, this.menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.deleteBtn -> deleteReply(replyInfo)
                }
                false
            }
        }.show()
    }

    private fun deleteReply(replyInfo: ReplyInfo) {
        alert(title = "댓글 삭제", message = "댓글을 삭제하시겠습니까?") {
            positiveButton("YES") { _ ->
                // 삭제 진행
                showProgressDialog()
                getReplies(replyInfo.boardId)?.document(replyInfo.objectId)
                        ?.delete()
                        ?.onSuccessTask { _ ->
                            // 댓글 카운트 감소
                            runTransaction<BoardInfo>(getBoard(replyInfo.boardId)?:throw IllegalStateException()){boardInfo ->
                                boardInfo.replyCount--
                                if(boardInfo.replyCount < 0) throw IllegalStateException("Reply Count is Zero")
                            }
                        }
                        ?.addOnSuccessListener { toast("댓글이 삭제되었습니다") }
                        ?.addOnCompleteListener { hideProgressDialog() } ?: hideProgressDialog()
            }
            negativeButton("NO") {}
        }.show()
    }

    fun Activity.hideSoftKeyboard() {
        val inputMethodManager = getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken, 0)
    }

    // 트랜잭션
    private inline fun <reified T> runTransaction(ref : DocumentReference, crossinline function: (info : T) -> Unit) =
        FirebaseFirestore.getInstance().runTransaction { transaction ->
        //val ref = getBoard(key)?:throw IllegalStateException()  // getBoard(key)?:throw IllegalStateException() // getHospital()
        val info = transaction.get(ref).toObject(T::class.java)?:throw IllegalStateException()
        transaction.set(ref, info.also { function.invoke(it) } as Any)
    }

    fun addReplyCount(boardKey : String) =
            runTransaction<BoardInfo>(getBoard(boardKey)?:throw IllegalStateException()) { boardInfo ->
            boardInfo.replyCount++
            if(boardInfo.replyCount < 0) throw IllegalStateException("Reply Count is Zero")
        }

    fun showDeleteBoardDialog(boardId: String, function: (() -> Unit)? = null){
        showProgressDialog()
        alert(title = "게시물 삭제", message = "게시물을 삭제하시겠습니까?") {
            positiveButton("YES"){ _ ->
                // 삭제 진행
                showProgressDialog()
                getBoard(boardId)?.delete()?.addOnCompleteListener {
                    hideProgressDialog()
                    RxBus.publish(ActivityResultEvent(
                            requestCode = RequestCode.OBJECT_ADD.value,
                            resultCode = Activity.RESULT_OK
                    ))
                }?.addOnSuccessListener { function?.invoke() }
            }

            negativeButton("NO"){hideProgressDialog()}
        }.show()
    }

    fun clickBoardLikeBtn(boardKey : String, setLike : Boolean): Task<MutableList<Task<*>>>? {
        val uid = getUid()?:return null
        return if(setLike){
            Tasks.whenAllComplete(
                    // 게시물 하위 컬렉션 추가 {유저키 : 날짜}
                    getLikeUsers(boardKey)?.document(uid)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 유저 컨텐츠 도큐먼트  컬렉션 추가 {게시물키 : 날짜}
                    getUserLikeBoard(boardKey)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 카운트 증가
                    runTransaction<BoardInfo>(getBoard(boardKey)?:throw IllegalStateException()) { boardInfo ->
                        boardInfo.likeCount++
                        if(boardInfo.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }

            )
        } else {
            Tasks.whenAllComplete(
                    // 게시물 하위 컬렉션 삭제
                    getLikeUsers(boardKey)?.document(uid)?.delete(),
                    // 유저 컨텐츠 도큐먼트  컬렉션 삭제
                    getUserLikeBoard(boardKey)?.delete(),
                    // 카운트 감소
                    runTransaction<BoardInfo>(getBoard(boardKey)?:throw IllegalStateException()) { boardInfo ->
                        boardInfo.likeCount--
                        if(boardInfo.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }
            )
        }
    }

    fun clickHospitalLikeBtn(hospitalKey: String, setLike: Boolean): Task<MutableList<Task<*>>>?{
        val uid = getUid()?:return null
        return if(setLike) {
            Tasks.whenAllComplete(
                    // 병원 하위 컬렉션 추가 {유저키 : 날짜}
                    getHospitalLikeUsers(hospitalKey)?.document(uid)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 유저 컨텐츠 도큐먼트  컬렉션 추가 {병원키 : 날짜}
                    getLikeHospital(hospitalKey)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 카운트 증가
                    runTransaction<HospitalInfo>(getHospital(hospitalKey)){info ->
                        info.likeCount++
                        if(info.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }
            )
        } else {
            Tasks.whenAllComplete(
                    // 병원 하위 컬렉션 삭제
                    getHospitalLikeUsers(hospitalKey)?.document(uid)?.delete(),
                    // 유저 컨텐츠 도큐먼트  컬렉션 삭제
                    getLikeHospital(hospitalKey)?.delete(),
                    // 카운트 감소
                    runTransaction<HospitalInfo>(getHospital(hospitalKey)){info ->
                        info.likeCount--
                        if(info.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }
            )
        }
    }

    fun clickEventLikeBtn(eventKey: String, setLike: Boolean): Task<MutableList<Task<*>>>?{
        val uid = getUid()?:return null
        return if(setLike) {
            Tasks.whenAllComplete(
                    // 이벤트 하위 컬렉션 추가 {유저키 : 날짜}
                    getEventLikeUsers(eventKey)?.document(uid)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 유저 컨텐츠 도큐먼트  컬렉션 추가 {이벤트키 : 날짜}
                    getLikeEvent(eventKey)?.set(Common.getCreateDate(), SetOptions.merge()),
                    // 카운트 증가
                    runTransaction<EventInfo>(getEvent(eventKey)?:throw IllegalStateException()){ info ->
                        info.likeCount++
                        if(info.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }
            )
        } else {
            Tasks.whenAllComplete(
                    // 이벤트 하위 컬렉션 삭제
                    getEventLikeUsers(eventKey)?.document(uid)?.delete(),
                    // 유저 컨텐츠 도큐먼트  컬렉션 삭제
                    getLikeEvent(eventKey)?.delete(),
                    // 카운트 감소
                    runTransaction<EventInfo>(getEvent(eventKey)?:throw IllegalStateException()){ info ->
                        info.likeCount--
                        if(info.likeCount < 0) throw IllegalStateException("Like Count is Zero")
                    }
            )
        }
    }

    fun toastVersion() = toast( "Version : ${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}")

    fun getOpenConsults() = getDb().collection("openConsults")
    fun getOpenConsult(uid : String? = getUid()): DocumentReference? = uid?.let { getOpenConsults().document(it) }

    fun getSelectConsults() = getDb().collection("selectConsults")
    fun getSelectConsult(hospitalId : String, userId : String) = getSelectConsults().whereEqualTo("hospitalId", hospitalId).whereEqualTo("userId", userId)
    fun getSelectConsultInfo(hospitalId : String, userId : String) =
        getSelectConsult(hospitalId, userId).get().continueWith {task ->
            task.result.toObjects(ConsultInfo::class.java).let {
                if(it.isEmpty()) return@let null
                else return@let it[0]
            }
        }

    fun getAnalytics() = FirebaseAnalytics.getInstance(this)


    val disposables by lazy {
        CompositeDisposable()
    }

    fun addCountHospitalVisitants(hospitalId: String): Task<HttpsCallableResult>? = FirebaseFunctions.getInstance()
            .getHttpsCallable("addCountHospitalVisitants")
            .call(mapOf("hospitalId" to hospitalId))

    fun addCountCounselors(hospitalId: String): Task<HttpsCallableResult>? = FirebaseFunctions.getInstance()
            .getHttpsCallable("addCountCounselors")
            .call(mapOf("hospitalId" to hospitalId))

    fun addCountEventApplicant(hospitalId: String): Task<HttpsCallableResult>? = FirebaseFunctions.getInstance()
            .getHttpsCallable("addCountEventApplicant")
            .call(mapOf("hospitalId" to hospitalId))

    fun addCountAdClick(hospitalId: String, type: AdCountType): Task<HttpsCallableResult>? = FirebaseFunctions.getInstance()
            .getHttpsCallable("addCountAdClick")
            .call(mapOf("hospitalId" to hospitalId, "type" to type.value))

    fun checkMarketVersion(function : () -> Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val storeVersion = remoteConfig.getLong(if(isHospital()) "latest_hospital_app_version" else "latest_user_app_version")
        val deviceVersion = BuildConfig.VERSION_CODE
        Timber.d("storeVersion : $storeVersion, deviceVersion : $deviceVersion")

        if (storeVersion > deviceVersion) {
            // 업데이트 필요
            alert(title = "업데이트 필요", message = "버전 업데이트를 하시겠습니까?") {
                positiveButton("업데이트") { _ ->
                    val appPackageName = packageName
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                        finish()

                    } catch (anfe: android.content.ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                        finish()
                    }

                }
                negativeButton("앱 종료") {finish()}
            }.show()

        } else {
            // 업데이트 불필요
            function.invoke()
        }
    }

    fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            val dialog = googleApiAvailability.getErrorDialog(this, status, -1)
            dialog.setOnDismissListener { _ -> finish() }
            dialog.show()

            googleApiAvailability.showErrorNotification(this, status)
        }
    }

//    [ getDate Example ]
//    context?.getDate()?.addOnSuccessListener {
//        Timber.d("KBJ, Date : $it")
//    }
    fun getServerTime(): Task<Date> = FirebaseFunctions.getInstance()
            .getHttpsCallable("date")
            .call().continueWith {
                val rstStr = it.result.data.toString()
                val milliseconds = JsonParser().parse(rstStr).asJsonObject.get("milliseconds").asLong
                Date(milliseconds)
            }

    fun selectConsultDialog(hUid: String?, uUid: String): Boolean {
        hUid ?: return true
        getSelectConsult(hUid, uUid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) return@addOnSuccessListener
                    val list = querySnapshot.toObjects(ConsultInfo::class.java)
                    list.also { if (it.isEmpty()) return@addOnSuccessListener }.let { it[0] }.let { consultInfo ->
                        val dialog = ConsultContentDialog(this, consultInfo)
                        dialog.show()
                    }
                }
        return false
    }

}