package com.scifi.storyapp.view.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.scifi.storyapp.R
import com.scifi.storyapp.databinding.ActivityRegisterBinding
import com.scifi.storyapp.view.AuthViewModelFactory
import com.scifi.storyapp.view.login.LoginActivity
import com.scifi.storyapp.view.utils.InterfaceUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.btnSignup.setOnClickListener {
            InterfaceUtils.showLoading(binding.root, true)
            val name = binding.etRegisterName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            viewModel.register(name, email, password)
        }
    }

    private fun setupObservers() {
        viewModel.registerResponse.observe(this) { registerResponse ->
            if (registerResponse.error == true) {
                InterfaceUtils.showAlert(
                    context = this,
                    message = registerResponse.message,
                )
            } else {
                InterfaceUtils.showAlert(
                    context = this,
                    message = getString(R.string.login_message),
                    primaryButtonText = getString(R.string.login),
                    onPrimaryButtonClick = {
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    },
                    secondaryButtonText = getString(R.string.cancel)
                )
            }
            InterfaceUtils.showLoading(binding.root, false)
        }
    }
}