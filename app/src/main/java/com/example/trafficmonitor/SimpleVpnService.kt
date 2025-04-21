package com.example.trafficmonitor

import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket

class SimpleVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var running = true

    override fun onCreate() {
        super.onCreate()
        Thread { runVpn() }.start()
    }

    private fun runVpn() {
        vpnInterface = Builder()
            .addAddress("10.0.0.2", 32)
            .addDnsServer("8.8.8.8")
            .addRoute("0.0.0.0", 0)
            .setSession("SimpleVPN")
            .establish()

        val fileOutput = File(filesDir, "traffic_log.txt")
        val logStream = FileOutputStream(fileOutput, true)

        val socket = DatagramSocket()
        val buffer = ByteArray(1024)

        while (running) {
            try {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val address = packet.address.hostName
                val url = "https://${address}"
                if (!fileOutput.readText().contains(url)) {
                    logStream.write("$url
".toByteArray())
                    logStream.flush()
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroy() {
        running = false
        vpnInterface?.close()
        super.onDestroy()
    }
}
