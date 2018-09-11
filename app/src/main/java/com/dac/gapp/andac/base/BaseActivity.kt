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
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.ReplyInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.dac.gapp.andac.util.RxBus
import com.dac.gapp.andac.util.UiUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_base.*
import org.jetbrains.anko.alert
import timber.log.Timber


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
                it?:goToLogin() // 로그아웃 상태
                it?.phoneNumber?.isEmpty()?.let { isEmpty ->
                    if (isEmpty) goToLogin()
                }
            } // 폰등록 안되 있는 경우는 login 이동


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

    fun isHospital(): Boolean = BuildConfig.FLAVOR == "hospital"

    fun isUser(): Boolean = BuildConfig.FLAVOR == "user"

    private val RC_TAKE_PICTURE = 101
    fun getAlbumImage(): Observable<Uri>? {
        // Pick an image from storage
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, RC_TAKE_PICTURE)

        return RxBus.listen(Uri::class.java).take(1)
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
    fun getUserContents(uid: String? = getUid()) = uid?.let { getDb().collection("userContents").document(it) }
    fun getUserBoards() = getUserContents()?.collection("boards")
    fun getViewedColumns() = getUserContents()?.collection("viewedColumns")
    fun getUserEvents() = getUserContents()?.collection("events")
    fun getUserEvent(eventKey: String) = getUserEvents()?.document(eventKey)
    fun getReplies(boardKey: String) = getBoard(boardKey)?.collection("replies")
    fun getlikeUsers(boardKey: String) = getBoard(boardKey)?.collection("likeUsers")



    // Column
    fun getColumnStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("columns")

    fun getColumns(): CollectionReference = getDb().collection("columns")
    fun getColumn(key: String): DocumentReference? = if (key.isEmpty()) null else getColumns().document(key)
    fun getHospitalColumns() = getHospitalContents()?.collection("columns")

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

    fun hidActionBarLeft() {
        UiUtil.visibleOrGone(false, layoutLeft, imgviewLeft, txtviewLeft)
    }

    fun showActionBarCenter() {
        UiUtil.visibleOrGone(true, imgviewCenter, txtviewCenter)
    }

    fun hidActionBarCenter() {
        UiUtil.visibleOrGone(false, imgviewCenter, txtviewCenter)
    }

    fun showActionBarRight() {
        UiUtil.visibleOrGone(true, layoutRight, imgviewRight, txtviewRight)
    }

    fun hidActionBarRight() {
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

        alert(title = "$objectStr 삭제", message = "${objectStr}을 삭제하시겠습니까?") {
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
    fun addListenerRegistrations(listener: ListenerRegistration) = listListenerRegistration.add(listener)

    override fun onDestroy() {
        super.onDestroy()
        listListenerRegistration.forEach { it.remove() }
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
                            boardRunTransaction(replyInfo.boardId) { boardInfo ->
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

    fun boardRunTransaction(boardKey : String, function: (boardInfo : BoardInfo) -> Unit) =
        FirebaseFirestore.getInstance().runTransaction { transaction ->
        val boardRef = getBoard(boardKey)?:throw IllegalStateException()
        val boardInfo = transaction.get(boardRef).toObject(BoardInfo::class.java)?:throw IllegalStateException()
        transaction.set(boardRef, boardInfo.also { function.invoke(it) })
    }


    fun addReplyCount(boardKey : String) =
        boardRunTransaction(boardKey) { boardInfo ->
            boardInfo.replyCount++
            if(boardInfo.replyCount < 0) throw IllegalStateException("Reply Count is Zero")
        }

    fun addLikeCount(boardKey : String) =
        boardRunTransaction(boardKey) { boardInfo ->
            boardInfo.likeCount++
            if(boardInfo.likeCount < 0) throw IllegalStateException("Like Count is Zero")
        }

    fun subLikeCount(boardKey : String) =
        boardRunTransaction(boardKey) { boardInfo ->
            boardInfo.likeCount--
            if(boardInfo.likeCount < 0) throw IllegalStateException("Like Count is Zero")
        }
}