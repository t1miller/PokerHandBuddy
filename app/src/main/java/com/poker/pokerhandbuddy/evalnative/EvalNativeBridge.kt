package com.poker.pokerhandbuddy.evalnative

import timber.log.Timber


object EvalNativeBridge {
    // Used to load the 'native-lib' library on application startup.
    init {
        System.loadLibrary("ompeval")
    }


//    external fun evaluateCallback()

    fun callbackEquity(equity: DoubleArray) {
//        println("Got int from C++: $i")
        Timber.d("callbackEquity(${equity})")
    }

//    external fun nativeUpdateProgress(pl: EvalProgress?)

    external fun testJacksJNI(): String

    external fun evaluateJNI(
        stopTime: Double,
        threads: Int,
        stdError: Double,
        boardS: String,
        deadS: String,
        playerCards: Array<String>
    ): EvalResult
}


data class EvalResult(
    var time: Double,
    var hands: Int,
    var speed: Double,
    var std: Double,
    var equity: DoubleArray,
    var win: DoubleArray,
    var tie: DoubleArray
)

