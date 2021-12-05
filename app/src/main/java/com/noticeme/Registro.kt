package com.noticeme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class Registro : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.gradiat)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val txtCorreo = findViewById<EditText>( R.id.txtCorreoR)
        val txtPassword = findViewById<EditText>( R.id.txtPasswordR)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        //
        title = "LogIn"
        btnRegistro.setOnClickListener {
            if ( txtCorreo.text.isNotBlank() && txtPassword.text.isNotBlank()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtCorreo.text.toString(), txtPassword.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        showLogIn(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        ShowAlert()
                    }
                }
            }
        }
        //

    }

    private fun ShowAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLogIn(email: String, provider: ProviderType){
        val LogInIntent: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }

        startActivity(LogInIntent)

    }

}