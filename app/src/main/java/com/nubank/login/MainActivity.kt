package com.nubank.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.nubank.login.databinding.ActivityMainBinding
import com.nubank.login.model.Mlogin
import kotlinx.coroutines.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idButtonLogin.setOnClickListener { login()
            var testepage = Intent(applicationContext, Aplicacao::class.java)

        }

        binding.idTextCriarConta.setOnClickListener {
            var intentCriarCadastro = Intent(applicationContext, CriarCadastro::class.java)
            startActivity(intentCriarCadastro)
        }

        binding.idTextResetarSenha.setOnClickListener {
            var intentResetSenha = Intent(applicationContext, ResetSenha::class.java)
            startActivity(intentResetSenha)
        }

    }

    fun login() {
        binding.etLogin.addTextChangedListener(
            textListener(
                binding.etLogin,
                binding.tiLogin
            )
        )
        binding.etSenha.addTextChangedListener(
            textListener(
                binding.etSenha,
                binding.tiSenha
            )
        )

        //limpa o erro quando o usuario começa a digitar no campo
        if (binding.etLogin.text.toString().isEmpty()) {
            binding.tiLogin.error = "Digite seu Email"
        } else if (binding.etSenha.text.toString().isEmpty()) {
            binding.tiSenha.error = "Digite sua Senha"
        } else {
            var login = JSONObject()

            login.put("email", binding.etLogin.text)
            login.put("senha", binding.etSenha.text)


            CoroutineScope(Dispatchers.IO).launch {
                val res: Deferred<String?> = async {
                    Mlogin().login(json = login)
                }

                val response = res.await().toString()

                withContext(Dispatchers.Main) {
                    var resposta = JSONObject(response)



                    if (resposta.has("statusCode")) {
                        Toast.makeText(
                            applicationContext,
                            resposta.getString("message").replace("[", "").replace("]", "")
                                .replace("\"f", ""),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (resposta.has("token")) {
                            var token = resposta.getString("token")
                            Toast.makeText(applicationContext, token, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Erro ao obter token",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun textListener(input: EditText, view: TextInputLayout): TextWatcher {
        return object : TextWatcher {

            override fun afterTextChanged(edt: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.isErrorEnabled = false
            }
        }
    }
}