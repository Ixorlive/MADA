package com.example.mada_2.server_connection.service

import com.example.mada_2.dto.ResponseDto
import com.example.mada_2.dto.ResponseMeterDataDto
import java.util.concurrent.CompletableFuture

/*
гайд, как вызывать suspend функции в java:
https://stackoverflow.com/questions/52869672/call-kotlin-suspend-function-in-java-class

also: implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4'

HttpBaseSource.Companion.getClient() - получить синглтон из java кода
 */
interface BaseSource {

    /*
    авторизация
    возвращает капчу в виде массива байтов
    далее надо вызвать submitCaptcha
     */
    suspend fun authorize(account: String, district: String): ResponseDto

    suspend fun submitCaptcha(value: String): ResponseMeterDataDto

    /*
        возвращает список районов
     */
    suspend fun getDistricts(): List<String>

    fun getDistrictsAsync(): CompletableFuture<List<String>>

    fun authorizeAsync(account: String, district: String): CompletableFuture<ResponseDto>

    fun submitCaptchaAsync(value: String): CompletableFuture<ResponseMeterDataDto>

}