package com.example.mada_2.server_connection;

import java.io.*;
import okhttp3.*;

public class ML {
    public static String getPrediction(android.net.Uri imageUri) throws IOException{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file","123",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(imageUri.getPath())))
                .build();
        Request request = new Request.Builder()
                .url("127.0.0.1:8000/recognize_meters_data")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}