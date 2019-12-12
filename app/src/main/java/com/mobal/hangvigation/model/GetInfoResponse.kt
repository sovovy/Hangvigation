package com.mobal.hangvigation.model

data class GetInfoResponse(
    var message:String,
    var data: GetInfoResponseData
)

data class GetInfoResponseData(
    val indoor_place_idx: Int?,
    val name: String?,
    val building: String?,
    val num: String?,
    val floor: String?,
    val tag1: String?,
    val tag2: String?,
    val tag3: String?,
    val info: String?,
    val x: Int?,
    val y: Int?,
    val x2: Int?,
    val y2: Int?
)