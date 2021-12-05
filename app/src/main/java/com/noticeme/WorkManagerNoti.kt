package com.noticeme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.TimeUnit

class WorkManagerNoti(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val titulo = inputData.getString("titulo")
        val detalle = inputData.getString("detalle")
        val id = inputData.getLong("idnoti", 0).toInt()
        val email = inputData.getString("email")
        oreo(titulo, detalle, email)
        return Result.success()
    }

    private fun oreo(t: String?, d: String?, email: String?) {
        val id = "message"
        val nm =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder =
            NotificationCompat.Builder(applicationContext, id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc =
                NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH)
            nc.description = "Notificacion FOM "
            nc.setShowBadge(true)
            assert(nm != null)
            nm.createNotificationChannel(nc)
        }
        val intent = Intent(applicationContext, CrearNota::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        builder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(t)
            .setTicker("Nueva notificaion")
            .setSmallIcon(R.drawable.campana)
            .setContentText(d)
            .setContentIntent(pendingIntent)
            .setContentInfo("nuevo")
        val random = Random()
        val idNotify = random.nextInt(8000)
        assert(nm != null)
        nm.notify(idNotify, builder.build())

        val db = FirebaseFirestore.getInstance()
        var idS = ""

        Log.d("MEH", email.toString())

        db.collection("users").document(inputData.getString("email").toString())
            .collection("notificaciones").addSnapshotListener { value, error ->
                value?.forEach {
                    if (it["nombre"]?.equals(inputData.getString("titulo"))!!) {
                        idS = it.id
                    }
                }
                db.collection("users").document(inputData.getString("email").toString())
                    .collection("notificaciones").document(idS).delete()

            }

    }


    companion object {
        fun GuardarNoti(duracion: Long?, data: Data?, tag: String?) {
            val noti = OneTimeWorkRequest.Builder(WorkManagerNoti::class.java)
                .setInitialDelay(duracion!!, TimeUnit.MILLISECONDS).addTag(tag!!)
                .setInputData(data!!).build()
            val intance = WorkManager.getInstance()
            intance.enqueue(noti)
        }
    }

}