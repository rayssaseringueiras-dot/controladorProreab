package com.example.controladorproreab

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var btnConectarBluetooth: MaterialButton
    private lateinit var btnMenu: ImageView

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (bluetoothAdapter?.isEnabled == true) {
                conectarBluetooth()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConectarBluetooth = findViewById(R.id.btnConectarBluetooth)
        btnMenu = findViewById(R.id.btnMenu)

        btnConectarBluetooth.setOnClickListener {
            verificarBluetooth()
        }

        btnMenu.setOnClickListener {
            MenuBottomSheet().show(supportFragmentManager, "menu")
        }
    }

    private fun verificarBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                100
            )
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
            return
        }

        conectarBluetooth()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            verificarBluetooth()
        }
    }

    private fun conectarBluetooth() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        if (pairedDevices.isNullOrEmpty()) {
            Toast.makeText(this, "Nenhum dispositivo pareado", Toast.LENGTH_SHORT).show()
            return
        }

        // Procura primeiro por HC-05 ou HC-06; se não encontrar, usa o primeiro.
        val device = pairedDevices.firstOrNull {
            it.name == "HC-05" || it.name == "HC-06"
        } ?: pairedDevices.first()

        thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                )

                bluetoothAdapter?.cancelDiscovery()
                socket.connect()

                BluetoothManager.socket = socket
                BluetoothManager.connectedDeviceName = device.name ?: "Dispositivo"

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Conectado em ${device.name}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this, ControladorActivity::class.java)
                    )
                }

            } catch (e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Falha ao conectar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
