package com.dac.gapp.andac.model

import android.os.Parcel
import android.os.Parcelable

class EventDetail constructor(var title: String, var sub_title: String, var body: String, var deal_kind: String, var price: String, var buy_count: String) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(sub_title)
        parcel.writeString(body)
        parcel.writeString(deal_kind)
        parcel.writeString(price)
        parcel.writeString(buy_count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventDetail> {
        override fun createFromParcel(parcel: Parcel): EventDetail {
            return EventDetail(parcel)
        }

        override fun newArray(size: Int): Array<EventDetail?> {
            return arrayOfNulls(size)
        }
    }

}