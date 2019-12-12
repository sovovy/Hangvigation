package com.mobal.hangvigation.model

import android.os.Parcel
import android.os.Parcelable

data class PostOutdoorData (
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double
)

data class PostOutdoorResponse (
    var message: String,
    var data: ArrayList<PostOutdoorResponseData>
)

class PostOutdoorResponseData (var type:String, var x:Double, var y:Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeDouble(x)
        parcel.writeDouble(y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostOutdoorResponseData> {
        override fun createFromParcel(parcel: Parcel): PostOutdoorResponseData {
            return PostOutdoorResponseData(parcel)
        }

        override fun newArray(size: Int): Array<PostOutdoorResponseData?> {
            return arrayOfNulls(size)
        }
    }
}