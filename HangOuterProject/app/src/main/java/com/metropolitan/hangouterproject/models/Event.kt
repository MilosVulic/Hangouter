package com.metropolitan.hangouterproject.models

data class Event(
    var eventName: String = "",
    var phoneNumber: String = "",
    var capacity: Int = 0,
    var location: Location = Location("","",""),
    var imageName: String = "",
    var description: String = "",
    var startDate: Long = 0,
    var endDate: Long = 0,
    var price: Int = 0,
    var timeZone: String = "",
    var capacityReserved: Int = 0,
    var totalVotes: Int = 0,
    var totalRating: Double = 0.0
)
