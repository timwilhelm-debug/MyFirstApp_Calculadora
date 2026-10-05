package com.example.myfirstapp// Note: Check that this matches your actual package name


import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var switchAngle: Switch

    private var currentInput = ""
    private var firstOperand = 0.0
    private var pendingOperation = ""
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        switchAngle = findViewById(R.id.switchAngle)

        setupButtons()
    }

    private fun setupButtons() {
        // Number Buttons
        val numberIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        for (id in numberIds) {
            findViewById<Button>(id).setOnClickListener { view ->
                val button = view as Button
                if (isNewOperation) {
                    currentInput = ""
                    isNewOperation = false
                }
                currentInput += button.text.toString()
                tvDisplay.text = currentInput
            }
        }

        // Basic Math Operators
        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorSelected("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorSelected("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorSelected("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorSelected("/") }

        // Trigonometric Operators
        findViewById<Button>(R.id.btnSin).setOnClickListener { onTrigSelected("sin") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { onTrigSelected("cos") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { onTrigSelected("tan") }

        // Control Buttons
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsSelected() }
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            currentInput = ""
            firstOperand = 0.0
            pendingOperation = ""
            isNewOperation = true
            tvDisplay.text = "0"
        }
    }

    private fun onOperatorSelected(operator: String) {
        if (currentInput.isNotEmpty()) {
            // Evaluates left-to-right immediately if an operation is pending
            if (pendingOperation.isNotEmpty() && !isNewOperation) {
                calculateIntermediate()
            } else {
                firstOperand = currentInput.toDoubleOrNull() ?: 0.0
            }
        }
        pendingOperation = operator
        isNewOperation = true
    }

    private fun calculateIntermediate() {
        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        val result = when (pendingOperation) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "*" -> firstOperand * secondOperand
            "/" -> if (secondOperand != 0.0) firstOperand / secondOperand else 0.0
            else -> firstOperand
        }
        firstOperand = result
        tvDisplay.text = formatResult(result)
        currentInput = result.toString()
    }

    private fun onEqualsSelected() {
        if (pendingOperation.isNotEmpty()) {
            calculateIntermediate()
            pendingOperation = ""
            isNewOperation = true
        }
    }

    private fun onTrigSelected(function: String) {
        val value = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0

        // Check switch state. If checked, treat as Degrees and convert to Radians.
        val radians = if (switchAngle.isChecked) Math.toRadians(value) else value

        val result = when (function) {
            "sin" -> sin(radians)
            "cos" -> cos(radians)
            "tan" -> tan(radians)
            else -> 0.0
        }

        tvDisplay.text = formatResult(result)
        currentInput = result.toString()
        firstOperand = result
        pendingOperation = "" // reset basic math operations when trig is applied
        isNewOperation = true
    }

    // Removes trailing ".0" if the result is a whole number
    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }
}