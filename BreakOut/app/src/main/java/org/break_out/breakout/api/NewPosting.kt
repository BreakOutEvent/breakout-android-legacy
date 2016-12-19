package org.break_out.breakout.api

/**
 * Created by florianschmidt on 07/12/2016.
 */
import org.break_out.breakout.model.BOLocation
import org.break_out.breakout.sync.model.Posting

class NewPosting(
        val id: Int,
        val text: String?,
        val date: Int?,
        val postingLocation: PostingLocation?,
        val user: User,
        val comments: List<Comment> = listOf(),
        val media: List<Medium> = listOf(),
        val hashtags: List<Any> = listOf(),
        val proof: Proof?,
        val likes: Int = 0,
        val hasLiked: Boolean = false)

class Proof(
        val status: String,
        val amount: Double = 0.0,
        val description: String = ""
)

fun NewPosting.transfromToPosting(): Posting {

    val teamName = this.user.participant?.teamName
    val message = this.text

    val location: BOLocation? = this.postingLocation?.transform {
        BOLocation(it.date!!.toLong(), it.latitude, it.longitude)
    }

    return Posting(teamName, message, location, null)
}

fun <A, B> A.transform(f: (A) -> B): B {
    return f(this)
}