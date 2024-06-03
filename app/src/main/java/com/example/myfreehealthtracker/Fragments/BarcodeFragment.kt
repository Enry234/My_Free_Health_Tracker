package com.example.myfreehealthtracker.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R


class BarcodeFragment : Fragment() {
    private var image: String = ""
    lateinit var mainApplication : MainApplication

    @SuppressLint("UnrememberedMutableState")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainApplication = requireActivity().application as MainApplication
        val view = inflater.inflate(R.layout.fragment_barcode, container, false)
        val buttonBarcode = view?.findViewById<Button>(R.id.button_barcode)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view2)
        composeView.apply {
            setViewCompositionStrategy(
                androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
        }
        composeView.setContent {
            //WeightChart()
        }

//        buttonBarcode?.setOnClickListener {
//
//            val integrator = IntentIntegrator.forSupportFragment(this)
//            integrator.setPrompt("Scansiona un barcode") // Testo mostrato sopra l'area di scansione
//            integrator.setCameraId(0) // Usa la fotocamera posteriore
//            integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
//            integrator.setBeepEnabled(false) // Disabilita il segnale acustico alla scansione
//
//            integrator.initiateScan()
//        }

        return view
    }


}

