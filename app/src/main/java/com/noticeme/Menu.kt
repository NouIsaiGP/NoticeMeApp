package com.noticeme

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

enum class ProviderType{
    BASIC
}

class Menu : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var prefs : SharedPreferences? = null
    var email : String = ""
    var provider : String = ""
    var listView : ListView? = null
    private val lista: MutableList<String?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.gradiat)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnCerrar = findViewById<Button>(R.id.btnCerrar)
        listView = findViewById(R.id.listView)
        val listView2 = findViewById<ListView>(R.id.listView)


        val bundle : Bundle? = intent.extras
        val email : String? = bundle?.getString("email")
        val provider : String?  = bundle?.get("provider").toString()

        //GUARDAR DATOS
        var prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_files), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        this.prefs = getSharedPreferences(getString(R.string.prefs_files), Context.MODE_PRIVATE)
        this.email = this.prefs?.getString("email",null).toString()
        this.provider = this.prefs?.getString("email",null).toString()

        btnCerrar.setOnClickListener{
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        //Recuperar()

        listView2.setOnItemClickListener { parent, view, position, id ->

            val selectedItem = parent.getItemAtPosition(position)
            var mensaje = ""
            var id = ""

            db.collection("users").document(this.email).collection("notificaciones").addSnapshotListener { value, error ->

                value?.forEach {
                    if (it["nombre"]?.equals(selectedItem)!!){
                        id = it.id
                    }
                }
                db.collection("users").document(this.email).collection("notificaciones").document(id).addSnapshotListener { value, error ->
                    mensaje = "Notificacion: " + value!!.get("nombre") + " \nMensaje: " + value.get("descripcion") + " \nFecha de Notificacion: " + value.get("fecha") + "\nHora: " + value.get("tiempo")
                    Log.d("MEH", mensaje)
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }

            }

        }


    }

    private fun showMenu(email: String, provider: ProviderType){
        val menuIntent: Intent = Intent(this, Menu::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(menuIntent)
    }

    fun CrearNota(view: View) {
        startActivity(Intent(this, CrearNota()::class.java))
    }

    fun Recuperar(view: View) {
        db.collection("users").document(email).collection("notificaciones").addSnapshotListener { value, error ->
            lista?.clear()
            value?.map {
                lista.add(it.get("nombre").toString())
            }

           val adapter = ArrayAdapter(applicationContext,  android.R.layout.simple_list_item_1, lista )
            adapter.notifyDataSetChanged()
            listView!!.adapter = adapter

        }

    }

    fun Detalles(view: View) {

    }
}