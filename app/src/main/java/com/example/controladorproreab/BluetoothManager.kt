package com.example.controladorproreab

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.UUID

object BluetoothManager {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    var socket: BluetoothSocket? = null

    var connectedDeviceName: String = ""

    private val UUID_SPP: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }

    @SuppressLint("MissingPermission")
    fun connectToPairedDevice(): Boolean {
        try {
            val adapter = bluetoothAdapter ?: return false

            if (!adapter.isEnabled) return false

            val bondedDevices = adapter.bondedDevices
            if (bondedDevices.isEmpty()) return false

            // Procura primeiro pelo atuador
            val device = bondedDevices.firstOrNull {
                it.name == "ESP32" ||
                        it.name == "HC-05" ||
                        it.name == "HC-06"
            } ?: bondedDevices.first()


            socket?.close() // Isso vai fechar a anterior

//            adapter.cancelDiscovery() // Cancela descoberta antes de conectar    SE DER ERRO MUDAR AQUI

            socket = device.createRfcommSocketToServiceRecord(UUID_SPP)  // Cria socket

            socket?.connect() // Conecta

            connectedDeviceName = device.name ?: "Dispositivo Bluetooth"

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            disconnect()
            return false
        }
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

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<String> {
        val adapter = bluetoothAdapter ?: return emptyList()

        if (!adapter.isEnabled) return emptyList()

        return adapter.bondedDevices.map { it.name ?: "Sem nome" }
    }
}
