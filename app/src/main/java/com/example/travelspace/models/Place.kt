package com.example.travelspace.models

class Place(
    val id: String="",
    var title: String="",
    var image: String="",
    var description: String="",
    var date:String="",
    val location: String="",
    val latitude: Double=0.0,
    val longitude: Double=0.0) {
}