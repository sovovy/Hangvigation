package com.mobal.hangvigation.model

data class PostCoordData (
    var bssid:String,
    var rssi:Double
)

data class PostCoordResponse(
    var message:String,
    var data: PostCoordResponseData
)

data class PostCoordResponseData(
    var x:Int,
    var y:Int,
    var z:Int
)