package com.practice.francisco.checkins.Foursquare

import kotlin.collections.ArrayList


class FoursquareAPIRequestVenues {
    var meta: Meta? = null
    var response: FoursquareResponseVenues? = null
}

class FoursquareAPInuevoCheckin{
    var meta: Meta? = null
}

class Meta {
    var code: Int = 0
    var errorDetail: String = ""
}

class FoursquareResponseVenues {
    var venues: ArrayList<Venue>? = null
}

class Venue {
    var id: String = ""
    var name: String = ""
    var location: Location? = null
    var categories: ArrayList<Category>? = null
    var stats: Stats? = null
}

class Location {
    var lat: Double = 0.0
    var lng: Double = 0.0
    var state: String = ""
    var country: String = ""
}

class Category {
    var id: String = ""
    var name: String = ""
    var icon: Icon? = null

    var pluralName:String = ""
    var shortName:String = ""
}

open class Icon {
    var prefix: String = ""
    var suffix: String = ""
}

class Stats {
    var checkinsCount = 0
    var usersCount = 0
    var tipCount = 0
}

class FoursquareAPISelfUser{
    var meta: Meta? = null
    var response: FoursquareResponseSelfUSer? = null
}

class FoursquareResponseSelfUSer{
    var user: User?=null
}

class User{
    var id = ""
    var firstName = ""
    var lastName = ""
    var photo: Photo? =null
    var friends: Friends? =null
    var tips: Tips? = null
    var photos: Photos? = null
    var checkin : Checkins? = null
}

class Photo: Icon(){
    var id = ""
    var width = 0
    var height = 0
}

class Friends{
    var count = 0
}

class Photos{
    var count = 0
    var items:ArrayList<Photo>? = null
}

class Checkins{
    var count = 0
    var items:ArrayList<Checkin>? = null
}

class Checkin{
    var shout = ""
    var venue: Venue? = null
}

class Tips{
    var count= 0
}

class FoursquareAPICategorias{
    var meta:Meta? = null
    var response:CategoriasResponse? = null
}

class CategoriasResponse{
    var categories:ArrayList<Category>? = null
}

class LikeResponse{
    var meta:Meta? = null
}

class VenuesDeLikes{
    var meta: Meta? = null
    var response: VenuesDeLikesResponse? = null
}

class VenuesDeLikesResponse{
    var venues:VenuesDeLikesObject? = null
}

class VenuesDeLikesObject{
    var items:ArrayList<Venue> ? = null
}
