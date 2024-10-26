package com.example.currencycalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val currencyRates = mapOf(
        "USD" to Pair(1.0, "$"),
        "EUR" to Pair(0.93, "€"),
        "JPY" to Pair(152.28, "¥"),
        "VND" to Pair(25369.96, "₫")
    )

    private lateinit var sourceAmountSymbol: TextView
    private lateinit var targetAmountSymbol: TextView
    private lateinit var sourceAmountEditText: EditText
    private lateinit var targetAmountEditText: EditText
    private lateinit var sourceCurrencySpinner: Spinner
    private lateinit var targetCurrencySpinner: Spinner
    private var isConverting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sourceAmountSymbol = findViewById(R.id.currency1)
        targetAmountSymbol = findViewById(R.id.currency2)
        sourceAmountEditText = findViewById(R.id.editText1)
        targetAmountEditText = findViewById(R.id.editText2)
        sourceCurrencySpinner = findViewById(R.id.spinner1)
        targetCurrencySpinner = findViewById(R.id.spinner2)

        val currencies = currencyRates.keys.toList()
        val adapter = ArrayAdapter(this, R.layout.spinner_item, currencies)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_list)

        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter

        sourceCurrencySpinner.setSelection(0)
        targetCurrencySpinner.setSelection(1)

        sourceAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertCurrency(1)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        targetAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convertCurrency(0)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        sourceCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                convertCurrency(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        targetCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                convertCurrency(1)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun convertCurrency(type: Int) {
        if(isConverting) return

        isConverting = true

        val sourceAmount = sourceAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val targetAmount = targetAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = targetCurrencySpinner.selectedItem.toString()

        val (sourceRate, sourceSymbol) = currencyRates[sourceCurrency] ?: Pair(1.0, "")
        val (targetRate, targetSymbol) = currencyRates[targetCurrency] ?: Pair(1.0, "")

        sourceAmountSymbol.text = sourceSymbol
        targetAmountSymbol.text = targetSymbol

        val convertedAmount = if (type == 1) {
            sourceAmount * (targetRate / sourceRate)
        } else {
            targetAmount * (sourceRate / targetRate)
        }

        // Format the amount as currency
        val decimalFormat = DecimalFormat("#,##0.0", DecimalFormatSymbols(Locale.getDefault()))
        val formattedAmount = decimalFormat.format(convertedAmount)

        if (type == 1) {
            targetAmountEditText.setText(formattedAmount)
        } else {
            sourceAmountEditText.setText(formattedAmount)
        }

        isConverting = false

    }
}