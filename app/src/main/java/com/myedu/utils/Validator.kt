package com.myedu.utils

object Validator {
    fun validateName(name: String): Boolean = name.matches(Regex("^[a-zA-Z]{3,}+((\\s))+[a-zA-Z]{3,}+$"))

    fun validateEmail(email: String): Boolean =
        email.matches(Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$"))

    fun validatePassword(password: String): Boolean =
        password.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"))
}