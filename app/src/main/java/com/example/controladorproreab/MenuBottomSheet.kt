package com.example.controladorproreab

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton

class MenuBottomSheet : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.bottom_sheet_menu)

        // Fundo transparente
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Posiciona na lateral esquerda
        dialog.window?.setGravity(Gravity.START or Gravity.TOP)

        // Define largura e altura
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window ?: return

        // Remove margens padrão
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        // Cola no canto esquerdo
        window.setGravity(Gravity.START or Gravity.TOP)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val txtDevice = view.findViewById<TextView>(R.id.txtDevice)
        val btnAction = view.findViewById<MaterialButton>(R.id.btnAction)

        if (BluetoothManager.isConnected()) {
            txtDevice.text = "• ${BluetoothManager.connectedDeviceName}"
            btnAction.text = "Desconectar"

            btnAction.setOnClickListener {
                BluetoothManager.disconnect()
                dismiss()
                activity?.finish()
            }
        } else {
            txtDevice.text = "• NENHUM"
            btnAction.text = "Conectar dispositivo"

            btnAction.setOnClickListener {
                dismiss()

                activity?.let {
                    val intent = Intent(it, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        }
    }
}