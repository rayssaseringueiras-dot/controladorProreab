package com.example.controladorproreab

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ControladorActivity : AppCompatActivity() {

    lateinit var btnMenu: ImageView
    lateinit var arcSlider: ArcSliderView
    lateinit var verticalSlider: VerticalSliderView

    private var lastAngle = 90
    private var lastHeight = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_controlador)

        btnMenu = findViewById(R.id.btnMenu)
        arcSlider = findViewById(R.id.arcSlider)
        verticalSlider = findViewById(R.id.verticalSlider)

        // Abre o menu
        btnMenu.setOnClickListener {
            MenuBottomSheet().show(supportFragmentManager, "menu")
        }
    }

    fun onAngleChanged(newAngle: Int) {
        if (newAngle > lastAngle) {
            BluetoothManager.send("u")
        } else if (newAngle < lastAngle) {
            BluetoothManager.send("d")
        }

        lastAngle = newAngle
    }

    fun onHeightChanged(newHeight: Int) {
        if (newHeight > lastHeight) {
            BluetoothManager.send("x")
        } else if (newHeight < lastHeight) {
            BluetoothManager.send("y")
        }

        lastHeight = newHeight
    }
}