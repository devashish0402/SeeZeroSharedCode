package com.seezero.clientapi.implementation
var iserrorEnabled = false
var isDebugEnabled = true
var isInfoEnabled = true


//private val logger = KotlinLogging.logger {}


fun main() {
 
    debug("Hello")
    info("Hello")
}

fun error(errStr: String) {
    if (iserrorEnabled) {
        println(errStr )
    }
}

fun debug(debugStr: String) {
    if (iserrorEnabled || isDebugEnabled) {
        println( debugStr )
    }
}

fun info(infoStr: String) {
    if (iserrorEnabled || isDebugEnabled || isInfoEnabled) {
        println(  infoStr )
    }
}