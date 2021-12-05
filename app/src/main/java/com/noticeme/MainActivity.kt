package com.noticeme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.gradiat)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val txtRegistro = findViewById<TextView>(R.id.txtRegistrartse)
        //
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion con firebase lista")
        analytics.logEvent("InitScreen", bundle)
        //
        txtRegistro.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
        session()
    }

    override fun onStart() {
        super.onStart()
        val layout = findViewById<LinearLayout>(R.id.auth)
        layout.visibility = View.VISIBLE
    }


    private fun session(){
        val prefs : SharedPreferences = getSharedPreferences(getString(R.string.prefs_files), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider", null)

        Log.d("DATOS", "Email: " + email + " Provider: " + provider)

        val authLayout = findViewById<LinearLayout>(R.id.auth)
        if (email != null && provider != null){
            authLayout.visibility = View.INVISIBLE
            showMenu(email, ProviderType.valueOf(provider))
        }

    }

    private fun ShowAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMenu(email: String, provider: ProviderType){
        val menuIntent: Intent = Intent(this, Menu::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(menuIntent)
    }

    fun iniciar(view: View) {

        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        if ( txtCorreo.text.isNotBlank() && txtPassword.text.isNotBlank()){

            FirebaseAuth.getInstance().signInWithEmailAndPassword(txtCorreo.text.toString(), txtPassword.text.toString()).addOnCompleteListener{
                if (it.isSuccessful){
                    showMenu(it.result?.user?.email ?: "", ProviderType.BASIC)
                } else {
                    ShowAlert()
                }
            }
        }
    }


}