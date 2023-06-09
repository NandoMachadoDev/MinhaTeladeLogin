package com.nubank.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nubank.login.databinding.ActivityAplicacaoBinding
import com.nubank.login.model.Mlogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class Aplicacao : AppCompatActivity() {

    private lateinit var binding: ActivityAplicacaoBinding
    private var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAplicacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idButtonSair.setOnClickListener { logoutObject() }
    }

    fun logoutObject() {
        var logout = JSONObject()

        logout(logout)
    }

    fun logout(logout: JSONObject) {
        val sharedPreferences =  getSharedPreferences("teste",MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null).toString()
        CoroutineScope(Dispatchers.IO).launch {
            val restLogout: Deferred<Pair<String, String>> = async {
                Mlogin().logout(token)
            }

            val responseLogout = restLogout.await()

            withContext(Dispatchers.Main) {
                if (responseLogout.first == "200") {
                    Util.menssagemToast(context, context.getString(R.string.logout))
                    var intentLogout = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intentLogout)
                } else {
                    var respostaErro = responseLogout.second

                    var jsonErro = JSONObject(respostaErro)

                    if (jsonErro.has("message")) {
                        var erro = Util.removeCaracteresErro(jsonErro.getString("message"))
                        Util.menssagemToast(context, erro)
                    } else {
                        Util.menssagemToast(context, context.getString(R.string.erro_geral))
                    }
                }
            }
        }

    }

}