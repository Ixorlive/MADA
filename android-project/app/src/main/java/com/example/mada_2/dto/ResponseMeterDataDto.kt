package com.example.mada_2.dto

data class ResponseMeterDataDto(
        val success: Boolean,
        // массив счетчкиков
        val meters: List<String>,
        val dateFrom: Int,
        val dateTo: Int,
)
