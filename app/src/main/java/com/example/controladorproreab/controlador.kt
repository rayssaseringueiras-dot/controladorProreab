package com.example.controladorproreab

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ControladorActivity : AppCompatActivity() {

//        Atribuições de variáveis:
    lateinit var btnMenu: ImageView
    lateinit var btnStop: Button
//    lateinit var btnStopInclinação: Button
//    lateinit var btnLiberarAltura: Button
//    lateinit var btnTravarAltura: Button
//    lateinit var btnLiberarInclinacao: Button
//    lateinit var btnTravarInclinacao: Button
//    lateinit var btnZ: Button
//    lateinit var btnN: Button
    lateinit var btnCima: Button
    lateinit var btnBaixo: Button
    lateinit var btnEsquerda: Button
    lateinit var btnDireita: Button
    lateinit var arcSlider: ArcSliderView
    lateinit var verticalSlider: VerticalSliderView
    private var lastAngle = 90
    private var currentHeightFromESP = 45

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirAlturaAtual()
        setContentView(R.layout.activity_controlador)

//         Atribundo ID:
        btnMenu = findViewById(R.id.btnMenu)
        btnStop = findViewById(R.id.btnStop)
        arcSlider = findViewById(R.id.arcSlider)
        verticalSlider = findViewById(R.id.verticalSlider)
//        Botões de direção:
        btnCima = findViewById(R.id.btnCima)
        btnBaixo = findViewById(R.id.btnBaixo)
        btnEsquerda = findViewById(R.id.btnEsquerda)
        btnDireita = findViewById(R.id.btnDireita)
//        Botões de teste
//        btnStopInclinação = findViewById(R.id.btn_stopInclinação)
//        btnLiberarAltura = findViewById(R.id.btn_liberar_Altura)
//        btnTravarAltura = findViewById(R.id.btn_travar_Altura)
//        btnLiberarInclinacao = findViewById(R.id.btn_Liberar_Inclinação)
//        btnTravarInclinacao = findViewById(R.id.btn_travar_inclinação)
//        btnZ = findViewById(R.id.btn_Z)
//        btnN = findViewById(R.id.btn_N)


//        Usagem das variáveis:

        // ESSA PARTE ABRE O MENU
        btnMenu.setOnClickListener {
            MenuBottomSheet().show(supportFragmentManager, "menu")
        }
        // BOTÃO DE STOP PARA PARAR EM CASO DE EMERGENCIA
        btnStop.setOnClickListener {
            BluetoothManager.send("p")
        }
        // Direção:
        btnCima.setOnClickListener {
            BluetoothManager.send("")
        }

        btnBaixo.setOnClickListener {
            BluetoothManager.send("")
        }

        btnEsquerda.setOnClickListener {
            BluetoothManager.send("")
        }

        btnDireita.setOnClickListener {
            BluetoothManager.send("")
        }
//        Botões de teste:
//        btnStopInclinação.setOnClickListener {
//            BluetoothManager.send("s")
//        }
//
//        btnLiberarAltura.setOnClickListener {
//            BluetoothManager.send("l")
//        }
//
//        btnTravarAltura.setOnClickListener {
//            BluetoothManager.send("t")
//        }
//
//        btnLiberarInclinacao.setOnClickListener {
//            BluetoothManager.send("j")
//        }
//
//        btnTravarInclinacao.setOnClickListener {
//            BluetoothManager.send("k")
//        }
//
//        btnZ.setOnClickListener {
//            BluetoothManager.send("z")
//        }
//
//        btnN.setOnClickListener {
//            BluetoothManager.send("n")
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

        val diferenca = newHeight - currentHeightFromESP

        /* Esse código de thread serve para rodar a comunicalão em segundo plano e não travar o app para o usuário.
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
}

// RELAÇÕES IMPORTANRTES:
// Relação de envio de altura = 1 letra opara 1 giro / são 75 giros que corresponde a 75cm
// Relação de envio de inclinação = 1 letra endia a açteração de 1 mm, são 100 mm para 90º
// BTN_stop --> comando "S"


