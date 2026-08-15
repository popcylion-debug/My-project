package com.agon.app.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ImageHost {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun upload(context: Context, uri: Uri, suggestedName: String = "media.bin"): String =
        withContext(Dispatchers.IO) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Could not read the selected file")
            val mime = context.contentResolver.getType(uri) ?: guessMime(suggestedName)
            postBytes(bytes, suggestedName, mime)
        }

    suspend fun uploadBytes(bytes: ByteArray, name: String, mime: String): String =
        withContext(Dispatchers.IO) { postBytes(bytes, name, mime) }

    private fun postBytes(bytes: ByteArray, name: String, mime: String): String {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("reqtype", "fileupload")
            .addFormDataPart(
                "fileToUpload",
                name,
                bytes.toRequestBody(mime.toMediaType()),
            )
            .build()
        val request = Request.Builder()
            .url("https://catbox.moe/user/api.php")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty().trim()
            if (!response.isSuccessful || !text.startsWith("http")) {
                return uploadLitter(bytes, name, mime)
            }
            return text
        }
    }

    private fun uploadLitter(bytes: ByteArray, name: String, mime: String): String {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "fileToUpload",
                name,
                bytes.toRequestBody(mime.toMediaType()),
            )
            .addFormDataPart("time", "72h")
            .addFormDataPart("reqtype", "fileupload")
            .build()
        val request = Request.Builder()
            .url("https://litterbox.catbox.moe/resources/internals/api.php")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty().trim()
            if (response.isSuccessful && text.startsWith("http")) return text
            return upload0x0(bytes, name, mime)
        }
    }

    private fun upload0x0(bytes: ByteArray, name: String, mime: String): String {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                name,
                bytes.toRequestBody(mime.toMediaType()),
            )
            .build()
        val request = Request.Builder()
            .url("https://0x0.st")
            .header("User-Agent", "SalonNaWeYon/1.0")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty().trim()
            if (response.isSuccessful && text.startsWith("http")) return text
            error("Upload failed: ${response.code} $text")
        }
    }

    private fun guessMime(name: String): String = when {
        name.endsWith(".png", true) -> "image/png"
        name.endsWith(".jpg", true) || name.endsWith(".jpeg", true) -> "image/jpeg"
        name.endsWith(".webp", true) -> "image/webp"
        name.endsWith(".mp4", true) -> "video/mp4"
        name.endsWith(".m4a", true) -> "audio/mp4"
        name.endsWith(".3gp", true) -> "audio/3gpp"
        name.endsWith(".aac", true) -> "audio/aac"
        else -> "application/octet-stream"
    }

    @Suppress("unused")
    private fun parseJsonUrl(raw: String): String? = try {
        val obj = JSONObject(raw)
        obj.optString("url").ifBlank { null }
    } catch (_: Exception) {
        null
    }
}
