package com.example.mada_2.exceptions

import okhttp3.Response

class ServerException(
        msg: String,
        val response: Response?,
) : RuntimeException(msg) {

    var t: Throwable? = null
    constructor(msg: String, t: Throwable): this(msg, null) {
        this.t = t
    }
}