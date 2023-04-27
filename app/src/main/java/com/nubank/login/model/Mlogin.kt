package com.nubank.login.model

import android.util.Log
import com.nubank.login.Aplicacao
import com.nubank.login.Util
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.media.session.MediaSession.Token as Token1

class Mlogin {
    private val client = OkHttpClient().newBuilder()
        .readTimeout(3000, TimeUnit.MILLISECONDS)
        .build()

    companion object {
        val MEDIA_TYPE_MARKDOWN = "application/json".toMediaType()
    }

    fun login(json: JSONObject): Pair<String, String> {
        val request = Request.Builder()
            .url(Util.url() + "auth/login")
            .post(json.toString().trimMargin().toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return Pair(response.code.toString(), response.body.string())
            }
        } catch (e: IOException) {
            Log.d("okk", e.message.toString())
            return Pair("erro", "erro")
        }
    }

    fun createUser(json: JSONObject): Pair<String, String> {
        //Log.d("testeJsonCadastro", json.toString())
        val request = Request.Builder()
            .url(Util.url() + "auth/cadastro")
            .post(json.toString().trimMargin().toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return Pair(response.code.toString(), response.body.string())
            }
        } catch (e: IOException) {
            return Pair("erro", "erro")
        }
    }

    fun enviarEmail(json: JSONObject): Pair<String, String> {
        Log.d("testeJsonEnvioEmail", json.toString())
        val request = Request.Builder()
            .url(Util.url() + "auth/recuperarsenha")
            .post(json.toString().trimMargin().toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return Pair(response.code.toString(), response.body.string())
            }
        } catch (e: IOException) {
            return Pair("erro", "erro")
        }
    }

    fun resetarSenha(json: JSONObject): Pair<String, String> {
        Log.d("testeJsonResetSenha", json.toString())
        val request = Request.Builder()
            .url(Util.url() + "auth/recuperarsenhaToken")
            .patch(json.toString().trimMargin().toRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return Pair(response.code.toString(), response.body.string())
            }
        } catch (e: IOException) {
            return Pair("erro", "erro")
        }
    }

    fun logout(token:String): Pair<String, String> {
        val request = Request.Builder()
            .url(Util.url() + "auth/logout")
            .post("".trimMargin().toRequestBody(MEDIA_TYPE_MARKDOWN))
            .addHeader("Authorization", token)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                return Pair(response.code.toString(), response.body.string())
            }
        } catch (e: IOException) {
            return Pair("erro", "erro")
        }
        val response = client.newCall(request).execute()
    }
}