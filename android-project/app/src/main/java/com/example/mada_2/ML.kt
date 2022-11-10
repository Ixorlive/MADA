package com.example.mada_2

import android.net.Uri
import okhttp3.*
import java.io.File
import java.io.IOException

class ML {
    @Throws(IOException::class)
    fun getPrediction(imageUri: Uri): String? {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType = parse.parse("text/plain")
        val body: RequestBody = Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "123",
                RequestBody.create(
                    parse.parse("application/octet-stream"),
                    File(imageUri.path)
                )
            )
            .build()
        val request: Request = Builder()
            .url("127.0.0.1:8000/recognize_meters_data")
            .method("POST", body)
            .build()
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }
}