package org.break_out.breakout.api

class PostingLocation {

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    
    var date: Int? = null
    var id: Int? = null
    var distance: Double? = null
    var team: String? = null
    var teamId: Int? = null
    var event: String? = null
    var eventId: Int? = null
    var locationData: LocationData? = null
    var duringEvent: Boolean? = null
}