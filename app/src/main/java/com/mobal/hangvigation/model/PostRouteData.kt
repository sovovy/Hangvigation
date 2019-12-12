package com.mobal.hangvigation.model

import android.os.Parcel
import android.os.Parcelable

data class PostRouteData (
    var x1: Int,
    var y1: Int,
    var z1: Int,
    var x2: Int,
    var y2: Int,
    var z2: Int
)

data class PostRouteResponse(
    var message:String,
    var data: ArrayList<PostRouteResponseData>
)

class PostRouteResponseData(var x:Int, var y:Int, var z:Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(x)
        parcel.writeInt(y)
        parcel.writeInt(z)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostRouteResponseData> {
        override fun createFromParcel(parcel: Parcel): PostRouteResponseData {
            return PostRouteResponseData(parcel)
        }

        override fun newArray(size: Int): Array<PostRouteResponseData?> {
            return arrayOfNulls(size)
        }
    }
}