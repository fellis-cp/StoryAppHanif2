package com.dicoding.storyapphanif.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.databinding.ActivityLoginBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.responseLogin.observe(this) { result ->
            when (result) {
                is Result.Loading -> loadingVisible(true)
                is Result.Success -> showSuccessDialog()
                is Result.Error -> showErrorDialog()
            }
        }

        animation()
        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            binding.apply {
                if (emailEditText.error.isNullOrEmpty() && passwordEditText.error.isNullOrEmpty()) {
                    val email = emailEditText.text.toString().trim()
                    val password = passwordEditText.text.toString().trim()
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun loadingVisible(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage(getString(R.string.login_message))
            setCancelable(false)
            setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()

        }
        loadingVisible(false)
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.login_failed_title))
            setMessage(getString(R.string.login_failed_dialog))
            create()
            show()
        }
        loadingVisible(false)
    }

    private fun animation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}
