package com.example.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

//recibe señales del sistema
class NotificationRecever : BroadcastReceiver(){
    companion object{
        const val ACTION_RECEIVED="action_received"
    }

    override fun onReceive(context:Context?, intent: Intent?) {
        if(intent?.action== ACTION_RECEIVED){
            Toast.makeText(
                context,
                "Conectado exitosamente",
                Toast.LENGTH_SHORT).show()
        }
    }


}
