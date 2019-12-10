package com.mobal.hangvigation.model

data class GetDivisionResponse(
    var message:String,
    var data: ArrayList<GetDivisionResponseData>
)

class GetDivisionResponseData(val division_idx: Int?, val name: String?, val indoor_place_idx: Int?, val building: String?, val num: String?) : Comparable<GetDivisionResponseData> {
    override fun compareTo(other: GetDivisionResponseData): Int {
        val mName = this.name?:this.building!!
        val oName = other.name?:this.building!!

        return mName.compareTo(oName)
    }
}
// in case of division
// {"division_idx":34,"name":"항공우주센타"}
// in case of place
// {"indoor_place_idx":112,"name":null,"building":"항공우주센타","num":"204"}