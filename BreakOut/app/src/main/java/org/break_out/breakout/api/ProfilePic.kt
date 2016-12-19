package org.break_out.breakout.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by florianschmidt on 07/12/2016.
 */
class ProfilePic {
    var id: Int? = null
    var type: String? = null
    var uploadToken: Any? = null
    var sizes: List<Size> = listOf()
}
