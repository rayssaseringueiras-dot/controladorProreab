package com.example.controladorproreab

import android.bluetooth.BluetoothSocket
import java.io.IOException

object BluetoothManager {

    var socket: BluetoothSocket? = null
    var connectedDeviceName: String = ""

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }

    fun send(command: String) {
        try {
            socket?.outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        socket = null
        connectedDeviceName = ""
    }
}
