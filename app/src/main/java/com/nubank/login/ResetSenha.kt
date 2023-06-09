package com.nubank.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.nubank.login.databinding.ActivityEmailTokenBinding
import com.nubank.login.databinding.ActivityResetSenhaBinding
import com.nubank.login.model.Mlogin
import kotlinx.coroutines.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class ResetSenha : AppCompatActivity() {

    private lateinit var binding: ActivityResetSenhaBinding
    private var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.verificaPermissaoInternet()
        binding = ActivityResetSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idButtonResetar.setOnClickListener { resetar() }

        binding.idButtonBack.setOnClickListener {
            var backToken = Intent(applicationContext, EmailToken::class.java)
            startActivity(backToken)
        }

    }


    fun resetar() {
        binding.etEmailToken.addTextChangedListener(
            textListener(
                binding.etEmailToken, binding.itEmailToken
            )
        )
        binding.etToken.addTextChangedListener(
            textListener(
                binding.etToken, binding.itToken
            )
        )
        binding.etNovaSenha.addTextChangedListener(
            textListener(
                binding.etNovaSenha, binding.tiNovaSenha
            )
        )
        binding.etNovaSenhaConfirmar.addTextChangedListener(
            textListener(
                binding.etNovaSenhaConfirmar, binding.tiNovaSenhaConfirmar
            )
        )

        //Limpa o erro quando o usuário começa a digitar
        if (binding.etEmailToken.text.toString().isEmpty()) {
            binding.itEmailToken.error = "Digite o Email"
        } else if (binding.etToken.text.toString().isEmpty()) {
            binding.itToken.error = "Digite o Token"
        } else if (binding.etNovaSenha.text.toString().isEmpty()) {
            binding.tiNovaSenha.error = "Digite a Nova Senha"
        } else if (binding.etNovaSenhaConfirmar.text.toString().isEmpty()) {
            binding.tiNovaSenhaConfirmar.error = "Confirme sua Senha"
        } else if (binding.etNovaSenhaConfirmar.text.toString() != binding.etNovaSenha.text.toString()) {
            binding.tiNovaSenhaConfirmar.error = "Senhas Diferentes"
        } else {
            var resetar = JSONObject()

            resetar.put("email", binding.etEmailToken.text)
            resetar.put("token", binding.etToken.text)
            resetar.put("senha", binding.etNovaSenha.text)

            requisicaoResetar(resetar)

        }

    }

    fun requisicaoResetar(resetar: JSONObject) {
        ProgressBarUtils.show(context)
        CoroutineScope(Dispatchers.IO).launch {
            val resta: Deferred<Pair<String, String>> = async {
                Mlogin().resetarSenha(json = resetar)
            }

            val respostaResetSenha = resta.await()

            withContext(Dispatchers.Main) {
                ProgressBarUtils.close(context)
                if (respostaResetSenha.first != "erro") {
                    if (respostaResetSenha.first == "201") {
                        Util.menssagemToast(context, context.getString(R.string.reset_senha))
                    } else {
                        var respostaErro = respostaResetSenha.second

                        var jsonErro = JSONObject(respostaErro)

                        if (jsonErro.has("message")) {
                            var erro = Util.removeCaracteresErro(jsonErro.getString("message"))
                            Util.menssagemToast(context, erro)
                        } else {
                            Util.menssagemToast(context, context.getString(R.string.erro_geral))
                        }
                    }
                } else {
                    Util.menssagemToast(context, context.getString(R.string.erro_requisicao))
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