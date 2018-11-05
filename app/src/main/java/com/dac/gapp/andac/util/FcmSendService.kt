package com.dac.gapp.andac.util

import com.dac.gapp.andac.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

package com.teamcore.android.core.Util

import com.teamcore.android.core.BuildConfig
import com.teamcore.android.core.Entity.User
import com.teamcore.android.core.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import org.json.JSONObject

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Administrator on 2018-02-21.
 */

object FirebaseSendPushMsg {

    private val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = BuildConfig.SERVER_KEY


    fun sendPostToFCM(type: String, targetUuid: String, name: String, message: String) {

        FirebaseFirestore.getInstance().collection("token").document(targetUuid).get().addOnCompleteListener {

            val user = dataSnapshot.getValue(User::class.java)
            Thread(Runnable {
                try {
                    // FMC 메시지 생성 start
                    val root = JSONObject()
                    //JSONObject notification = new JSONObject();
                    val data = JSONObject()
                    //notification.put("body", message);
                    //notification.put("title", currentUserNick);
                    data.put("message", message)
                    data.put("type", type)
                    data.put("nick", currentUserNick)
                    root.put("data", data)
                    //root.put("notification", notification);
                    root.put("to", user.getToken())
                    // FMC 메시지 생성 end

                    val Url = URL(FCM_MESSAGE_URL)
                    val conn = Url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.doOutput = true
                    conn.doInput = true
                    conn.addRequestProperty("Authorization", "key=$SERVER_KEY")
                    conn.setRequestProperty("Accept", "application/json")
                    conn.setRequestProperty("Content-type", "application/json")
                    val os = conn.outputStream
                    os.write(root.toString().toByteArray(charset("utf-8")))
                    os.flush()
                    conn.responseCode
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        }

    }
}
