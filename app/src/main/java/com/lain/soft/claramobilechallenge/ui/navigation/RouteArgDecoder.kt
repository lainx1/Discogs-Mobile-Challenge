package com.lain.soft.claramobilechallenge.ui.navigation

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

fun interface RouteArgDecoder {
    fun decode(value: String): String
}

class UrlRouteArgDecoder @Inject constructor() : RouteArgDecoder {
    override fun decode(value: String): String =
        URLDecoder.decode(value, StandardCharsets.UTF_8.name())
}
