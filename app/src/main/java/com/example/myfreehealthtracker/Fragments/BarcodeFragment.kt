package com.example.myfreehealthtracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.foodOpenFacts.ClientFoodOpenFact
import com.example.myfreehealthtracker.foodOpenFacts.OpenFoodFactEntity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BarcodeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_barcode, container, false)
        val buttonBarcode = view?.findViewById<Button>(R.id.button_barcode)

        buttonBarcode?.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setPrompt("Scansiona un barcode") // Testo mostrato sopra l'area di scansione
            integrator.setCameraId(0) // Usa la fotocamera posteriore
            integrator.setOrientationLocked(true) // Imposta il blocco dell'orientamento
            integrator.setBeepEnabled(false) // Disabilita il segnale acustico alla scansione

            integrator.initiateScan()

        }


        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val outputBarcode = view?.findViewById<TextView>(R.id.output_barcode)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                outputBarcode?.text = "Scansione fallita"
            } else {
                // Il codice a barre è stato trovato
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    // Call the suspend function
                    callClient(result.contents)
                }
            }
        }
    }

    private suspend fun callClient(s: String): List<OpenFoodFactEntity> {
        return ClientFoodOpenFact().getFoodOpenFacts(s)
    }


}

