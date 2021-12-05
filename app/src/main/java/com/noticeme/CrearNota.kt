package com.noticeme

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CrearNota : AppCompatActivity(), View.OnClickListener  {

    private val db = FirebaseFirestore.getInstance()
    var prefs : SharedPreferences? = null
    var email : String = ""
    var provider : String = ""

    var btnDatePicker: Button? = null
    var btnTimePicker: Button? = null
    var txtDate: EditText? = null
    var txtTime: EditText? = null
    var txtNombre: EditText? = null
    var txtDescripcion: EditText? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.gradiat)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nota)

        btnDatePicker = findViewById<View>(R.id.btn_date) as Button
        btnTimePicker = findViewById<View>(R.id.btn_time) as Button
        txtDate = findViewById<View>(R.id.in_date) as EditText
        txtTime = findViewById<View>(R.id.in_time) as EditText
        txtNombre = findViewById<View>(R.id.txtNombre) as EditText
        txtDescripcion = findViewById<View>(R.id.txtDescripcion) as EditText
        btnDatePicker!!.setOnClickListener(this)
        btnTimePicker!!.setOnClickListener(this)

        prefs = getSharedPreferences(getString(R.string.prefs_files), Context.MODE_PRIVATE)
        email = prefs?.getString("email",null).toString()
        provider = prefs?.getString("provider", null).toString()



    }

    override fun onClick(v: View?) {
        var year2 = 0
        var month2 =0
        var day2 = 0
        if (v === btnDatePicker) {

            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    txtDate!!.setText(
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    )
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    year2 = year
                    month2 = monthOfYear
                    day2 = dayOfMonth

                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }
        if (v === btnTimePicker) {

            // Get Current Time
            val c = Calendar.getInstance()
            mHour = c[Calendar.HOUR_OF_DAY]
            mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute -> txtTime!!.setText("$hourOfDay:$minute")
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }
    }

    fun CrearNota(view: View) {
        if(txtDate?.text.isNullOrEmpty() || txtDescripcion?.text.isNullOrEmpty() || txtNombre?.text.isNullOrEmpty() || txtTime?.text.isNullOrEmpty()){
            Toast.makeText(this,"Hubo un problema, Uno o mas campos estan vacios", Toast.LENGTH_LONG).show()
        }else{
            db.collection("users").document(email).collection("notificaciones")

                    .add(
                            hashMapOf(
                                    "provider" to provider,
                                    "nombre" to txtNombre?.text.toString(),
                                    "descripcion" to txtDescripcion?.text.toString(),
                                    "fecha" to txtDate?.text.toString(),
                                    "tiempo" to txtTime?.text.toString()
                            )
                    ).addOnCompleteListener {
                        if (it.isSuccessful){

                            var tag = generateID()
                            var AlertTime = calendar.timeInMillis - System.currentTimeMillis()
                            var random = (Math.random() * 50 + 1).toInt()
                            var data : Data = GuardarData(txtNombre?.text.toString(), txtDescripcion?.text.toString(), random, email)

                            WorkManagerNoti.GuardarNoti(AlertTime,data,tag)

                            Toast.makeText(applicationContext,"Notificacion Creada", Toast.LENGTH_LONG).show()
                            showMenu(email, ProviderType.valueOf(provider))
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

    private fun generateID(): String {
        return UUID.randomUUID().toString()
    }

    //
    private fun GuardarData(titulo: String, detalle: String, idNoti: Int,
        email: String): Data {
        return Data.Builder()
            .putString("titulo", titulo)
            .putString("detalle", detalle)
            .putString("email", email)
            .putInt("idnoti", idNoti).build()
    }




}