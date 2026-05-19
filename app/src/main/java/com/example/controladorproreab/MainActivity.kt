package com.example.controladorproreab

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var btnConectarBluetooth: MaterialButton
    private lateinit var btnMenu: ImageView

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    // Solicita ao usuário ativar o Bluetooth
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
        // Verifica se o aparelho suporta Bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(
                this,
                "Bluetooth não suportado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Verifica permissão (Android 12+)
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

        // Solicita ativação do Bluetooth, se necessário
        if (!bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
            return
        }

        // Tudo certo, tenta conectar
        conectarBluetooth()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            verificarBluetooth()
        }
    }

    private fun conectarBluetooth() {
        thread {
            val conectado = BluetoothManager.connectToPairedDevice()

            runOnUiThread {
                if (conectado) {
                    Toast.makeText(
                        this,
                        "Conectado em ${BluetoothManager.connectedDeviceName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            ControladorActivity::class.java
                        )
                    )
                } else {
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
