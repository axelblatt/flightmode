package com.tip31.flightmode

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL

class SendPost(body: String, val path: String) {
    private val mediaType = "application/json; charset=utf-8".toMediaType()
    private val requestBody = body.toRequestBody(mediaType)
    private val url = URL("http://192.168.197.196:8000/$path")
    private val request = Request.Builder().url(url).post(requestBody).build()
    private val response = OkHttpClient().newCall(request).execute()
    private val text = response.body?.string()
    val json = JSONObject(text.toString())
}