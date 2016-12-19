package org.break_out.breakout.api

import com.google.gson.annotations.SerializedName

class LocationData {
    @SerializedName("ADMINISTRATIVE_AREA_LEVEL_1")
    var administrativeAreaLevel1: String? = null

    @SerializedName("POLITICAL")
    var political: String? = null

    @SerializedName("ROUTE")
    var route: String? = null

    @SerializedName("ADMINISTRATIVE_AREA_LEVEL_2")
    var administrativeAreaLevel2: String? = null

    @SerializedName("LOCALITY")
    var locality: String? = null

    @SerializedName("SUBLOCALITY_LEVEL_1")
    var subLocalityLevel1: String? = null

    @SerializedName("COUNTRY")
    var country: String? = null

    @SerializedName("POSTAL_CODE")
    var postalcode: String? = null

    @SerializedName("STREET_NUMBER")
    var streetnumber: String? = null

    @SerializedName("SUBLOCALITY")
    var sublocality: String? = null
}