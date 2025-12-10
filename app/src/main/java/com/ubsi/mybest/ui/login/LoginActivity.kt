package com.ubsi.mybest.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ubsi.mybest.R
import com.ubsi.mybest.data.DataInitializer
import com.ubsi.mybest.databinding.ActivityLoginBinding
import com.ubsi.mybest.ui.main.MainActivity
import com.ubsi.mybest.util.PreferenceManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefManager = PreferenceManager(this)
        
        setupViews()
        loadSavedCredentials()
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordInfo()
        }

        binding.btnSkipLogin.setOnClickListener {
            startGuestMode()
        }
    }

    private fun loadSavedCredentials() {
        if (prefManager.rememberMe) {
            binding.etNim.setText(prefManager.savedNim)
            binding.cbRemember.isChecked = true
        }
    }

    private fun attemptLogin() {
        val nim = binding.etNim.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Validation
        if (nim.isEmpty()) {
            binding.tilNim.error = getString(R.string.error_nim_empty)
            return
        } else {
            binding.tilNim.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_empty)
            return
        } else {
            binding.tilPassword.error = null
        }

        // Save credentials if remember me checked
        if (binding.cbRemember.isChecked) {
            prefManager.rememberMe = true
            prefManager.savedNim = nim
        } else {
            prefManager.rememberMe = false
            prefManager.savedNim = ""
        }

        // TODO: Implement actual login API call
        // For now, just navigate to MainActivity
        showLoading(true)
        
        // Simulate login delay
        binding.root.postDelayed({
            showLoading(false)
            prefManager.isGuestMode = false
            prefManager.isLoggedIn = true
            navigateToMain()
        }, 1500)
    }

    private fun showLoading(show: Boolean) {
        binding.btnLogin.isEnabled = !show
        binding.btnLogin.text = if (show) getString(R.string.processing) else getString(R.string.login_button)
    }

    private fun showForgotPasswordInfo() {
        Snackbar.make(
            binding.root,
            getString(R.string.contact_admin_reset),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun startGuestMode() {
        showLoading(true)
        
        prefManager.isGuestMode = true
        DataInitializer.initializeGuestData(this)
        
        // Small delay to ensure data is initialized
        binding.root.postDelayed({
            showLoading(false)
            navigateToMain()
        }, 500)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
