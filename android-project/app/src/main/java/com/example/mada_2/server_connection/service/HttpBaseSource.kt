package com.example.mada_2.server_connection.service

import com.example.mada_2.dto.CaptchaDto
import com.example.mada_2.dto.PersonalDataDto
import com.example.mada_2.dto.ResponseDto
import com.example.mada_2.dto.ResponseMeterDataDto
import com.example.mada_2.exceptions.ConnectionException
import com.example.mada_2.exceptions.ServerException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HttpBaseSource private constructor() : BaseSource {
    private var JSESSIONID: String? = null

    override suspend fun authorize(account: String, district: String): ResponseDto {
        val dto = PersonalDataDto(district, account)
        val request = baseRequestBuilder()
                .post(dto.toJsonRequestBody())
                .endpoint("/run")
                .build()
        val response = buildClient()
                .newCall(request)
                .suspendEnqueue()
        handleSessionFromResponse(response)
        return response.parseJsonResponse()
    }


    override suspend fun submitCaptcha(value: String): ResponseMeterDataDto {
        val request = baseRequestBuilder()
                .post(CaptchaDto(value).toJsonRequestBody())
                .endpoint("/captcha")
                .build()
        val response = buildClient()
                .newCall(request)
                .suspendEnqueue()
        handleSessionFromResponse(response)
        return response.parseJsonResponse()
    }

    override suspend fun getDistricts(): List<String> {
        val request = baseRequestBuilder()
                .get()
                .endpoint("/districts")
                .build()
        val response = buildClient()
                .newCall(request)
                .suspendEnqueue()
        handleSessionFromResponse(response)
        return response.parseJsonResponse(object : TypeToken<List<String>>() {})
    }

    private fun handleSessionFromResponse(response: Response) {
        val cookies = response.headers.values("Set-Cookie")
        if (cookies.isNotEmpty()) {
            JSESSIONID = cookies[0].split(";")[0]
        }
    }

    private fun baseRequestBuilder(): Request.Builder {
        return Request.Builder().also {
            if (JSESSIONID != null) {
                it.header(COOKIE, JSESSIONID!!)
            }
        }
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        )
                .connectTimeout(Duration.ofSeconds(30))
                .build()
    }

    companion object {
        private val COOKIE = "Cookie"
        private var httpClient: HttpBaseSource? = null

        val gson = Gson()
        val client: BaseSource?
            get() {
                if (httpClient == null) {
                    httpClient = HttpBaseSource()
                }
                return httpClient
            }
    }

    override fun getDistrictsAsync() = GlobalScope.future { getDistricts() }

    override fun authorizeAsync(account: String, district: String) =
            GlobalScope.future { authorize(account = account, district = district) }

    override fun submitCaptchaAsync(value: String) = GlobalScope.future { submitCaptcha(value) }

}

private val BASE_URL = "http://10.171.75.71:8080/api"
private val contentType = "application/json; charset=utf-8".toMediaType()

suspend fun Call.suspendEnqueue(): Response {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(ConnectionException(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    continuation.resume(response)
                } else {
                    continuation.resumeWithException(ServerException("Server error", response))
                }
            }
        })
    }
}

fun Request.Builder.endpoint(endpoint: String) = also { url("${BASE_URL}$endpoint") }

fun <T> T.toJsonRequestBody() = HttpBaseSource.gson.toJson(this).toRequestBody(contentType)

fun <T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
    return try {
        HttpBaseSource.gson.fromJson(this.body!!.string(), typeToken.type)
    } catch (e: Exception) {
        throw ServerException("parseJsonResponse exception", e)
    }
}

inline fun <reified T> Response.parseJsonResponse(): T {
    try {
        return HttpBaseSource.gson.fromJson(this.body!!.string(), T::class.java)
    } catch (e: Exception) {
        throw ServerException("parseJsonResponse exception", e)
    }
}