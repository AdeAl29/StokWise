package com.ade.fuzzyrisk.ui.screens

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
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
        TextButton(onClick = onForgotPassword, modifier = Modifier.align(Alignment.End)) {
            Text("Lupa password?")
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Login", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRegister) {
                Text("Daftar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
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

    RegisterAuthScaffold {
        IconButton(onClick = onLogin) {
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
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Daftar", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sudah punya akun?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onLogin) {
                Text("Login", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
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
