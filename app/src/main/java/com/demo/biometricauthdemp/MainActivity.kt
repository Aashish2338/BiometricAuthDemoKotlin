package com.demo.biometricauthdemp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var mContext: Context
    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var manager: BiometricManager? = null
    private var info: String = ""
    private var promptInfo: PromptInfo.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

        checkBioMetricSupported()
        executor = ContextCompat.getMainExecutor(mContext)

        getAuthenticateBiometrics()

        promptInfo = dialogMetric()!!
        promptInfo!!.setNegativeButtonText("Cancel")
        biometricPrompt!!.authenticate(promptInfo!!.build())

        promptInfo = dialogMetric()
        promptInfo!!.setDeviceCredentialAllowed(true)
        biometricPrompt!!.authenticate(promptInfo!!.build())

    }

    private fun getAuthenticateBiometrics() {
        biometricPrompt = BiometricPrompt(mContext as MainActivity,
            executor!!,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun dialogMetric(): PromptInfo.Builder? {
        //Show prompt dialog
        return PromptInfo.Builder().setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
    }

    private fun checkBioMetricSupported() {
        manager = BiometricManager.from(mContext)
        when (manager!!.canAuthenticate(BIOMETRIC_WEAK or BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                info = "App can authenticate using biometrics."
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                info = "No biometric features available on this device."
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                info = "Biometric features are currently unavailable."
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                info = "Need register at least one finger print"
            }
            else -> {
                info = "Unknown cause"
            }
        }
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show()
    }
}