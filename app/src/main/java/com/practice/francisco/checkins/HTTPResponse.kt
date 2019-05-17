package com.practice.francisco.checkins

import java.net.CacheResponse

interface HTTPResponse {
    fun httpResponseSuccess(response: String)
}