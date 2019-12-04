package com.mobal.hangvigation.model

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

data class PostRouteResponseData(
    var x:Int,
    var y:Int,
    var z:Int
)