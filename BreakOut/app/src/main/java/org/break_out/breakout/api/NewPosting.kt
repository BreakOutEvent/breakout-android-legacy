package org.break_out.breakout.api

/**
 * Created by florianschmidt on 07/12/2016.
 */


import android.util.Log

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.break_out.breakout.model.BOLocation
import org.break_out.breakout.model.BOMedia
import org.break_out.breakout.sync.model.Posting

class NewPosting {

    var id: Int? = null
    var text: String? = null
    var date: Int? = null
    var postingLocation: PostingLocation? = null
    var media: List<Medium>? = null
    var user: User? = null
    var comments: List<Comment>? = null
    var likes: Int? = null
    var hasLiked: Boolean? = null
    var hashtags: List<Any>? = null
    var proves: Any? = null

    fun transformToPosting(): Posting {

        val teamName = this.user?.participant?.teamName
        val message = this.text

        val location: BOLocation? = this.postingLocation?.transform {
            BOLocation(it.date!!.toLong(), it.latitude, it.longitude)
        }

        return Posting(teamName, message, location, null)
    }
}

fun<A, B> A.transform(f: (A) -> B): B {
    return f(this)
}