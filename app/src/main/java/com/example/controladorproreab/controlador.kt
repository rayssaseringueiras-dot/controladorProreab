package com.example.controladorproreab

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ControladorActivity : AppCompatActivity() {

    lateinit var btnMenu: ImageView
//    lateinit var btnStop: Button
    lateinit var arcSlider: ArcSliderView
    lateinit var verticalSlider: VerticalSliderView
    private var lastAngle = 90
    private var lastHeight = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_controlador)

        btnMenu = findViewById(R.id.btnMenu)
//        btnStop = findViewById(R.id.btn_stop)
        arcSlider = findViewById(R.id.arcSlider)
        verticalSlider = findViewById(R.id.verticalSlider)

        // ESSA PARTE ABRE O MENU
        btnMenu.setOnClickListener {
            MenuBottomSheet().show(supportFragmentManager, "menu")
        }

        // BOTÃO DE STOP PARA PARAR EM CASO DE EMERGENCIA
//        btnStop.setOnClickListener {
//            BluetoothManager.send("s")
//        }
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
        if (newHeight > lastHeight) {
            var cont = 0
            while (cont < 5) {
                BluetoothManager.send("x")
                cont++
            }
        } else if (newHeight < lastHeight) {
            var cont = 0
            while (cont < 5) {
                BluetoothManager.send("y")
                cont++
            }

        }

        lastHeight = newHeight
    }

    fun inclinacaoS (newAngle: Int) {
        BluetoothManager.send(command = "d")
    }

    fun inclinacaoD (newAngle: Int) {
        BluetoothManager.send(command = "u")
    }

    fun subir (newAngle: Int) {
        BluetoothManager.send(command = "x")
    }

    fun descer (newAngle: Int) {
        BluetoothManager.send(command = "y")
    }

// Relação de envio de altura = 1 letra opara 1 giro / são 75 giros que corresponde a 75cm
// Relação de envio de inclinação = 1 letra endia a açteração de 1 mm, são 100 mm para 90º
// BTN_stop --> comando "S"
//
}


