package com.myedu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myedu.R
import com.myedu.databinding.FragmentLoginBinding
import com.myedu.utils.PrefManager
import com.myedu.utils.Validator.validateEmail


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var pref: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PrefManager(requireContext())
        auth = Firebase.auth

        binding.editEmail.doOnTextChanged { text, _, _, _ ->
            if (validateEmail(text.toString()))
                with(binding.emailInputLayout) {
                    error = null
                    isHelperTextEnabled = false
                }
            else
                binding.emailInputLayout.error = getString(R.string.error_email)
        }

        binding.editPassword.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty())
                with(binding.passwordInputLayout) {
                    error = null
                    isHelperTextEnabled = false
                }
            else
                binding.passwordInputLayout.error = getString(R.string.invalid_password)
        }

        binding.btnLogIn.setOnClickListener {
            validateFields()
            when {
                !validateEmail(binding.editEmail.text.toString()) -> binding.editEmail.requestFocus()
                binding.editPassword.text.toString()
                    .isEmpty() -> binding.editPassword.requestFocus()
                else -> login()
            }
        }

        binding.txtForgotPassword.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(pref.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            getString(R.string.password_recovery),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.progress.visibility = View.GONE
                }
        }

        binding.txtSignUp.setOnClickListener {
            Navigation
                .createNavigateOnClickListener(R.id.action_loginFragment_to_signUpFragment)
                .onClick(it)
        }
    }

    private fun login() {
        binding.progress.visibility = View.VISIBLE
        activity?.let {
            auth.signInWithEmailAndPassword(
                binding.editEmail.text.toString(),
                binding.editPassword.text.toString()
            )
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            with(pref) {
                                email = user.email.toString()
                                name = user.displayName.toString()
                            }
                        }
                        Toast.makeText(
                            context,
                            getString(R.string.login_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        Navigation
                            .createNavigateOnClickListener(R.id.action_loginFragment_to_mainFragment)
                            .onClick(view)
                    } else {
                        Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                    binding.progress.visibility = View.GONE
                }
        }
    }

    private fun validateFields() {
        if (!validateEmail(binding.editEmail.text.toString()))
            with(binding.emailInputLayout) {
                requestFocus()
                error = getString(R.string.helper_email)
            }
        else
            with(binding.emailInputLayout) {
                error = null
                isHelperTextEnabled = false
            }

        if (binding.editPassword.text.toString().isEmpty())
            with(binding.passwordInputLayout) {
                requestFocus()
                error = getString(R.string.helper_password)
            }
        else
            with(binding.passwordInputLayout) {
                error = null
                isHelperTextEnabled = false
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}