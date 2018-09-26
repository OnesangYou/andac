package com.dac.gapp.andac.model.firebase

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class NoticeInfo(
        var name: String? = null,
        var title: String? = null,
        var text: String? = null,
        var pictureUrl: String? = null,
        @ServerTimestamp var writeDate: Date? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as Date?) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeString(pictureUrl)
        parcel.writeSerializable(writeDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoticeInfo> {
        override fun createFromParcel(parcel: Parcel): NoticeInfo {
            return NoticeInfo(parcel)
        }

        override fun newArray(size: Int): Array<NoticeInfo?> {
            return arrayOfNulls(size)
        }
    }
}