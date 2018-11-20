package com.dac.gapp.andac.util

import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by Administrator on 2018-02-21.
 */

object FirebaseSendPushMsg {

    private val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = "AAAAMPYlx1I:APA91bFoLD-KA-JkU6e1sCL_JpgtUoRX-CezOZkKR6NG3i5hlNNEliWNSHAHaVsl3hDXm49QqHLDn_Q4YIpgQxriRduf9acm4GExOAwokorDjak8PnOB06qnZ7BCQ27KbEbry4BlpzXE"


    fun sendPostToFCM(type: String, targetUuid: String, message: String) {

        FirebaseFirestore.getInstance().collection("token").document(targetUuid).get().addOnSuccessListener {
            it.get("value")
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
                    root.put("data", data)
                    root.put("to", it.get("value"))
                    //root.put("notification", notification);
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
