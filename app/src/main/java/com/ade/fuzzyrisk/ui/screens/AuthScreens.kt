package com.ade.fuzzyrisk.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ade.fuzzyrisk.ui.components.AppLogo
import com.ade.fuzzyrisk.ui.components.AuthPickerField
import com.ade.fuzzyrisk.ui.components.AuthScaffold
import com.ade.fuzzyrisk.ui.components.AuthTextField
import com.ade.fuzzyrisk.ui.components.PasswordToggleField
import com.ade.fuzzyrisk.ui.components.RegisterAuthScaffold
import com.ade.fuzzyrisk.auth.AuthActionResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: suspend (String, String) -> AuthActionResult,
    onForgotPassword: suspend (String) -> AuthActionResult,
    onRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var isError by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    AuthScaffold {
        AppLogo(96.dp)
        Spacer(Modifier.height(20.dp))
        Text("Login", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text("Masuk untuk melanjutkan", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(34.dp))
        AuthTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "Masukkan email Anda",
            icon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        PasswordToggleField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "Masukkan password Anda",
            visible = passwordVisible,
            onToggleVisible = { passwordVisible = !passwordVisible }
        )
        AuthMessage(message = message, isError = isError)
        TextButton(
            onClick = {
                val trimmedEmail = email.trim()
                val validationMessage = validateResetPasswordInput(trimmedEmail)

                if (validationMessage != null) {
                    message = validationMessage
                    isError = true
                } else {
                    scope.launch {
                        isLoading = true
                        message = null
                        val result = onForgotPassword(trimmedEmail)
                        isLoading = false

                        when (result) {
                            is AuthActionResult.Success -> {
                                message = "Link reset password sudah dikirim ke email Anda."
                                isError = false
                            }
                            is AuthActionResult.Error -> {
                                message = result.message
                                isError = true
                            }
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa password?")
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val validationMessage = validateLoginInput(trimmedEmail, password)

                if (validationMessage != null) {
                    message = validationMessage
                    isError = true
                } else {
                    scope.launch {
                        isLoading = true
                        message = null
                        val result = onLogin(trimmedEmail, password)
                        isLoading = false

                        if (result is AuthActionResult.Error) {
                            message = result.message
                            isError = true
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(if (isLoading) "Memproses..." else "Login", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRegister, enabled = !isLoading) {
                Text("Daftar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegister: suspend (String, String, String) -> AuthActionResult,
    onLogin: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    RegisterAuthScaffold {
        IconButton(onClick = onLogin, enabled = !isLoading) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.height(28.dp))
        Text("Daftar", fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text("Buat akun baru untuk memulai", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(34.dp))
        AuthTextField(
            label = "Nama Lengkap",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "Masukkan nama lengkap Anda",
            icon = Icons.Filled.Person,
            keyboardType = KeyboardType.Text
        )
        Spacer(Modifier.height(16.dp))
        AuthTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "Masukkan email Anda",
            icon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        PasswordToggleField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "Buat password Anda",
            visible = passwordVisible,
            onToggleVisible = { passwordVisible = !passwordVisible }
        )
        Spacer(Modifier.height(16.dp))
        PasswordToggleField(
            label = "Konfirmasi Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Konfirmasi password Anda",
            visible = confirmPasswordVisible,
            onToggleVisible = { confirmPasswordVisible = !confirmPasswordVisible }
        )
        Spacer(Modifier.height(16.dp))
        AuthTextField(
            label = "No. Handphone",
            value = phone,
            onValueChange = { phone = it.filter(Char::isDigit) },
            placeholder = "Masukkan nomor handphone Anda",
            icon = Icons.Filled.PhoneAndroid,
            keyboardType = KeyboardType.Phone
        )
        Spacer(Modifier.height(16.dp))
        AuthPickerField(
            label = "Peran / Role",
            value = role,
            placeholder = "Pilih peran Anda",
            onClick = { role = if (role == "Pemilik Toko") "Admin Penjualan" else "Pemilik Toko" }
        )
        AuthMessage(message = message, isError = true)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                val trimmedName = fullName.trim()
                val trimmedEmail = email.trim()
                val validationMessage = validateRegisterInput(
                    fullName = trimmedName,
                    email = trimmedEmail,
                    password = password,
                    confirmPassword = confirmPassword
                )

                if (validationMessage != null) {
                    message = validationMessage
                } else {
                    scope.launch {
                        isLoading = true
                        message = null
                        val result = onRegister(trimmedName, trimmedEmail, password)
                        isLoading = false

                        if (result is AuthActionResult.Error) {
                            message = result.message
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(if (isLoading) "Memproses..." else "Daftar", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sudah punya akun?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onLogin, enabled = !isLoading) {
                Text("Login", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun AuthMessage(message: String?, isError: Boolean) {
    if (message == null) return

    Spacer(Modifier.height(10.dp))
    Text(
        text = message,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        lineHeight = 19.sp
    )
}

private fun validateLoginInput(email: String, password: String): String? {
    return when {
        email.isBlank() -> "Email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email belum valid."
        password.isBlank() -> "Password wajib diisi."
        else -> null
    }
}

private fun validateResetPasswordInput(email: String): String? {
    return when {
        email.isBlank() -> "Isi email dulu untuk reset password."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email belum valid."
        else -> null
    }
}

private fun validateRegisterInput(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String
): String? {
    return when {
        fullName.isBlank() -> "Nama lengkap wajib diisi."
        email.isBlank() -> "Email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email belum valid."
        password.length < 6 -> "Password minimal 6 karakter."
        password != confirmPassword -> "Konfirmasi password belum sama."
        else -> null
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onDone()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo(92.dp)
        Spacer(Modifier.height(24.dp))
        Text("FuzzyRisk", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(
            "Klasifikasi Risiko Penjualan Handphone",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(64.dp))
        androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
        Spacer(Modifier.height(12.dp))
        Text("Memuat...", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
