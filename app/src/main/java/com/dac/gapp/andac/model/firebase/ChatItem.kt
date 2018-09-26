package com.dac.gapp.andac.model.firebase

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class ChatItem(var uid: String? = null,
               var content: String? = null,
               var imageUrl: String? = null,
               @ServerTimestamp var time: Date? = null) {

    @Exclude
    var isMine: Boolean = uid.isNullOrEmpty().not()
    @Exclude
    var isImage: Boolean = imageUrl.isNullOrEmpty().not()


    @Exclude
    var type: Int = 0
        get() = if (isMine && !isImage)
            MY_MESSAGE
        else if (!isMine && !isImage)
            OTHER_MESSAGE
        else if (isMine && isImage)
            MY_IMAGE
        else
            OTHER_IMAGE

    companion object {
        private val MY_MESSAGE = 0
        private val OTHER_MESSAGE = 1
        private val MY_IMAGE = 2
        private val OTHER_IMAGE = 3
    }

    override fun toString(): String {
        return "uid : " + uid + "content : " + content + "imageUrl : " + imageUrl + "time : " + time + "isMine : " + isMine + "isImeage : " + isImage + "type : " + type
    }

}
