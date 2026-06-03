package com.ade.fuzzyrisk.auth

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface AuthActionResult {
    data object Success : AuthActionResult
    data class Error(val message: String) : AuthActionResult
}

object FirebaseAuthRepository {
    fun isSignedIn(context: Context): Boolean {
        return authOrNull(context)?.currentUser != null
    }

    suspend fun signIn(context: Context, email: String, password: String): AuthActionResult {
        val auth = authOrNull(context) ?: return missingConfigResult()
        return auth.signInWithEmailAndPassword(email, password).awaitAuthResult()
    }

    suspend fun register(context: Context, fullName: String, email: String, password: String): AuthActionResult {
        val auth = authOrNull(context) ?: return missingConfigResult()
        val result = auth.createUserWithEmailAndPassword(email, password).awaitAuthResult()

        if (result is AuthActionResult.Success && fullName.isNotBlank()) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)
        }

        return result
    }

    suspend fun sendPasswordReset(context: Context, email: String): AuthActionResult {
        val auth = authOrNull(context) ?: return missingConfigResult()
        return auth.sendPasswordResetEmail(email).awaitAuthResult()
    }

    fun signOut(context: Context) {
        authOrNull(context)?.signOut()
    }

    private fun authOrNull(context: Context): FirebaseAuth? {
        val appContext = context.applicationContext
        return try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                null
            } else {
                FirebaseAuth.getInstance()
            }
        } catch (_: IllegalStateException) {
            null
        }
    }

    private fun missingConfigResult(): AuthActionResult.Error {
        return AuthActionResult.Error(
            "Firebase belum terhubung. Letakkan google-services.json di folder app lalu sync Gradle."
        )
    }
}

private suspend fun Task<*>.awaitAuthResult(): AuthActionResult {
    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (!continuation.isActive) return@addOnCompleteListener

            if (task.isSuccessful) {
                continuation.resume(AuthActionResult.Success)
            } else {
                continuation.resume(AuthActionResult.Error(task.exception.toAuthMessage()))
            }
        }
    }
}

private fun Exception?.toAuthMessage(): String {
    return when (this) {
        is FirebaseAuthWeakPasswordException -> "Password minimal 6 karakter."
        is FirebaseAuthUserCollisionException -> "Email ini sudah terdaftar. Silakan login."
        is FirebaseAuthInvalidUserException -> "Akun tidak ditemukan atau sudah dinonaktifkan."
        is FirebaseAuthInvalidCredentialsException -> "Email atau password tidak valid."
        else -> this?.localizedMessage ?: "Autentikasi gagal. Coba lagi."
    }
}
