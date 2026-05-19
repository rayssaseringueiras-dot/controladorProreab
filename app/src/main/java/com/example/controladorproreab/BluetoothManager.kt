package com.example.controladorproreab

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.UUID

object BluetoothManager {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    var socket: BluetoothSocket? = null
        private set

    var connectedDeviceName: String = ""
        private set

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

            val device = bondedDevices.firstOrNull { bluetoothDevice ->
                val name = bluetoothDevice.name ?: ""
                name == "ESP32" ||
                        name == "ESP32_Atuador" ||
                        name == "HC-05" ||
                        name == "HC-06"
            } ?: bondedDevices.first()

            disconnect() // Fecha conexão anterior, se existir

            adapter.cancelDiscovery() // Cancela descoberta antes de conectar

            socket = device.createRfcommSocketToServiceRecord(UUID_SPP) // Tenta conectar usando o UUID padrão do Bluetooth clássico (SPP)
            socket?.connect()

            connectedDeviceName = device.name ?: "Dispositivo Bluetooth" // Salva o nome do dispositivo conectado

            return true

        } catch (e: Exception) {
            e.printStackTrace()
            try {
                disconnect()

                val adapter = bluetoothAdapter ?: return false
                val bondedDevices = adapter.bondedDevices

                val device = bondedDevices.firstOrNull { bluetoothDevice ->
                    val name = bluetoothDevice.name ?: ""
                    name == "ESP32" ||
                            name == "ESP32_Atuador" ||
                            name == "HC-05" ||
                            name == "HC-06"
                } ?: return false

                val method = BluetoothDevice::class.java.getMethod(
                    "createRfcommSocket",
                    Int::class.javaPrimitiveType
                )

                socket = method.invoke(device, 1) as BluetoothSocket
                socket?.connect()

                connectedDeviceName =
                    device.name ?: "Dispositivo Bluetooth"

                return true

            } catch (e2: Exception) {
                e2.printStackTrace()
                disconnect()
                return false
            }
        }
    }

    fun send(command: String) {
        try {
            if (isConnected()) {
                socket?.outputStream?.write(command.toByteArray())
                socket?.outputStream?.flush()
            }
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

        return adapter.bondedDevices.map { device ->
            device.name ?: "Sem nome"
        }
    }
}