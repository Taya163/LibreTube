package com.github.libretube.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.libretube.R
import com.github.libretube.api.RetrofitInstance
import com.github.libretube.api.obj.Login
import com.github.libretube.databinding.DialogLoginBinding
import com.github.libretube.extensions.TAG
import com.github.libretube.util.PreferenceHelper
import com.github.libretube.util.TextUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginDialog : DialogFragment() {
    private lateinit var binding: DialogLoginBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLoginBinding.inflate(layoutInflater)

        binding.login.setOnClickListener {
            if (isInsertionValid()) {
                signIn(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            } else {
                Toast.makeText(context, R.string.empty, Toast.LENGTH_SHORT).show()
            }
        }
        binding.register.setOnClickListener {
            if (isEmail(binding.username.text.toString())) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.privacy_alert)
                    .setMessage(R.string.username_email)
                    .setNegativeButton(R.string.proceed) { _, _ ->
                        signIn(
                            binding.username.text.toString(),
                            binding.password.text.toString(),
                            true
                        )
                    }
                    .setPositiveButton(R.string.cancel, null)
                    .show()
            } else if (isInsertionValid()) {
                signIn(
                    binding.username.text.toString(),
                    binding.password.text.toString(),
                    true
                )
            } else {
                Toast.makeText(context, R.string.empty, Toast.LENGTH_SHORT).show()
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .show()
    }

    private fun isInsertionValid(): Boolean {
        return binding.username.text.toString() != "" && binding.password.text.toString() != ""
    }

    private fun signIn(username: String, password: String, createNewAccount: Boolean = false) {
        val login = Login(username, password)
        lifecycleScope.launchWhenCreated {
            val response = try {
                if (createNewAccount) {
                    RetrofitInstance.authApi.register(login)
                } else {
                    RetrofitInstance.authApi.login(login)
                }
            } catch (e: Exception) {
                Log.e(TAG(), e.toString())
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            }

            if (response.error != null) {
                Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            }
            if (response.token == null) return@launchWhenCreated

            Toast.makeText(
                context,
                if (createNewAccount) R.string.registered else R.string.loggedIn,
                Toast.LENGTH_SHORT
            ).show()

            PreferenceHelper.setToken(response.token!!)
            PreferenceHelper.setUsername(login.username!!)

            dialog?.dismiss()
            activity?.recreate()
        }
    }

    private fun isEmail(text: String): Boolean {
        return TextUtils.EMAIL_REGEX.toRegex().matches(text)
    }
}
