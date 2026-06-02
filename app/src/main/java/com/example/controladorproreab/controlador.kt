package com.example.controladorproreab

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ControladorActivity : AppCompatActivity() {

    lateinit var btnMenu: ImageView
    lateinit var btnStop: Button
    lateinit var btnLiberar: Button
    lateinit var btnTravar: Button

    lateinit var arcSlider: ArcSliderView
    lateinit var verticalSlider: VerticalSliderView
    private var lastAngle = 90
    private var currentHeightFromESP = 45

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirAlturaAtual()

        setContentView(R.layout.activity_controlador)

        btnMenu = findViewById(R.id.btnMenu)
        btnStop = findViewById(R.id.btn_stop)
        arcSlider = findViewById(R.id.arcSlider)
        verticalSlider = findViewById(R.id.verticalSlider)
        btnLiberar = findViewById(R.id.btn_liberar)
        btnTravar = findViewById(R.id.btn_travar)

        // ESSA PARTE ABRE O MENU
        btnMenu.setOnClickListener {
            MenuBottomSheet().show(supportFragmentManager, "menu")
        }

        btnTravar.setOnClickListener {
            BluetoothManager.send("t")
        }

        btnLiberar.setOnClickListener {
            BluetoothManager.send("l")
        }

        // BOTÃO DE STOP PARA PARAR EM CASO DE EMERGENCIA
        btnStop.setOnClickListener {
            BluetoothManager.send("p")
        }

    }

    fun onAngleChanged(newAngle: Int) {
        if (newAngle > lastAngle) {
            BluetoothManager.send("d")
        } else if (newAngle < lastAngle) {
                BluetoothManager.send("u")
        }
        lastAngle = newAngle
    }

    fun onHeightChanged(newHeight: Int) {

        val diferenca = newHeight - currentHeightFromESP

        /* Esse código de thread serve para rodar a comunicação em segundo plano e não travar o app para o usuário.
        Ele recebe os dados em segundo plano */
        Thread {

            try {
                if (diferenca > 0) {
                    repeat(diferenca) {
                        BluetoothManager.send("x")
                        Thread.sleep(40)
                    }

                } else if (diferenca < 0) {
                    repeat(kotlin.math.abs(diferenca)) {
                        BluetoothManager.send("y")
                        Thread.sleep(40)
                    }
                }

                currentHeightFromESP = newHeight

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    private fun pedirAlturaAtual() {

        Thread {

            try {
                BluetoothManager.send("h")
                val response = BluetoothManager.read()
                val altura = response?.trim()?.toIntOrNull()

                if (altura != null) {
                    currentHeightFromESP = altura
                    runOnUiThread {
                        verticalSlider.setHeightValue(altura)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


// Relação de envio de altura = 1 letra opara 1 giro / são 75 giros que corresponde a 75cm
// Relação de envio de inclinação = 1 letra endia a açteração de 1 mm, são 100 mm para 90º
// BTN_stop --> comando "S"
}


