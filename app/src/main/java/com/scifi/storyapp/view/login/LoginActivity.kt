package com.scifi.storyapp.view.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.scifi.storyapp.databinding.ActivityLoginBinding
import com.scifi.storyapp.view.AuthViewModelFactory
import com.scifi.storyapp.view.main.MainActivity
import com.scifi.storyapp.view.utils.InterfaceUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        setupAction()
        setupObservers()
    }

    private fun setupAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnLogin.setOnClickListener {
            InterfaceUtils.showLoading(binding.root, true)
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun setupObservers() {
        viewModel.loginResponse.observe(this) { loginResponse ->
            InterfaceUtils.showLoading(binding.root, false)
            if (loginResponse.error == true) {
                InterfaceUtils.showAlert(
                    context = this,
                    message = loginResponse.message
                )
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}