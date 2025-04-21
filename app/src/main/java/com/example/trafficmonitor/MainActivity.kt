package com.example.trafficmonitor

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import java.io.File

class MainActivity : Activity() {

    private lateinit var startVpnButton: Button
    private lateinit var showLogButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startVpnButton = Button(this).apply {
            text = "Iniciar VPN"
            setOnClickListener {
                val intent = VpnService.prepare(this@MainActivity)
                if (intent != null) {
                    startActivityForResult(intent, 0)
                } else {
                    onActivityResult(0, RESULT_OK, null)
                }
            }
        }

        showLogButton = Button(this).apply {
            text = "Mostrar Log"
            setOnClickListener {
                val logFile = File(filesDir, "traffic_log.txt")
                val content = if (logFile.exists()) logFile.readText() else "Nenhum log ainda"
                android.app.AlertDialog.Builder(this@MainActivity)
                    .setTitle("Domínios acessados")
                    .setMessage(content)
                    .setPositiveButton("Fechar", null)
                    .show()
            }
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(startVpnButton)
            addView(showLogButton)
        }

        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            startService(Intent(this, SimpleVpnService::class.java))
        }
    }
}
