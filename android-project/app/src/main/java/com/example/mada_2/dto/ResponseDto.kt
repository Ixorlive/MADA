package com.example.mada_2.dto


data class ResponseDto(
        val success: Boolean,
        val captcha: ByteArray,
)