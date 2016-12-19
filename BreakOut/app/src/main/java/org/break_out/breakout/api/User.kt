package org.break_out.breakout.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by florianschmidt on 07/12/2016.
 */
class User {
    var firstname: String? = null
    var lastname: String? = null
    var gender: String? = null
    var id: Int? = null
    var participant: Participant? = null
    var profilePic: ProfilePic? = null
    var roles: List<String>? = null
    var blocked: Boolean? = null
}
