package com.footballpredictor.ai

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var web: WebView
    private val executor = Executors.newSingleThreadExecutor()
    private val prefs by lazy {
        getSharedPreferences("football_predictor", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.addJavascriptInterface(Bridge(), "AndroidBridge")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class Bridge {
        @JavascriptInterface
        fun saveApiKey(key: String) {
            prefs.edit().putString("api_key", key.trim()).apply()
        }

        @JavascriptInterface
        fun hasApiKey(): Boolean =
            !prefs.getString("api_key", "").isNullOrBlank()

        @JavascriptInterface
        fun request(endpoint: String, query: String, callback: String) {
            executor.execute {
                val result = try {
                    val key = prefs.getString("api_key", "") ?: ""
                    if (key.isBlank()) throw Exception("Masukkan API key terlebih dahulu.")

                    val params = JSONObject(query)
                    val qs = params.keys().asSequence().map { k ->
                        URLEncoder.encode(k, "UTF-8") + "=" +
                        URLEncoder.encode(params.getString(k), "UTF-8")
                    }.joinToString("&")

                    val conn = URL(
                        "https://v3.football.api-sports.io/$endpoint?$qs"
                    ).openConnection() as HttpURLConnection

                    conn.requestMethod = "GET"
                    conn.setRequestProperty("x-apisports-key", key)
                    conn.connectTimeout = 15000
                    conn.readTimeout = 20000

                    val code = conn.responseCode
                    val stream = if (code in 200..299)
                        conn.inputStream else conn.errorStream
                    val body = stream.bufferedReader().use { it.readText() }
                    conn.disconnect()

                    if (code !in 200..299)
                        throw Exception("API error $code: $body")

                    body
                } catch (e: Exception) {
                    JSONObject()
                        .put("app_error", e.message ?: "Koneksi gagal")
                        .toString()
                }

                runOnUiThread {
                    web.evaluateJavascript(
                        "window.__apiCallback(${JSONObject.quote(callback)}, ${JSONObject.quote(result)})",
                        null
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        web.destroy()
        super.onDestroy()
    }
}
