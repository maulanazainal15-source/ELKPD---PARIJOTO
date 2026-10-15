package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.di.FirebaseModule
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

data class AuthState(
    val isAuthenticated: Boolean = false,
    val firebaseUser: FirebaseUser? = null,
    val userProfile: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CloudAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseModule.provideFirebaseAuth(context)
    private val firestore = FirebaseModule.provideFirestore(context)
    private val credentialManager = CredentialManager.create(context)

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val currentFirebaseUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signInWithGoogle(): Result<FirebaseUser> {
        return try {
            val serverClientId = context.getString(R.string.default_web_client_id)
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user ?: throw IllegalStateException("Firebase user is null")

                // Synchronize or create profile in Firestore
                syncUserProfileOnSignIn(user, googleIdTokenCredential.displayName ?: user.displayName ?: "Pengguna Etnosains")

                Result.success(user)
            } else {
                Result.failure(IllegalStateException("Tipe kredensial tidak dikenali"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("CloudAuthManager", "Google Sign-In dibatalkan oleh pengguna", e)
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e("CloudAuthManager", "Gagal mendapatkan kredensial: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("CloudAuthManager", "Kesalahan autentikasi Google: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun syncUserProfileOnSignIn(firebaseUser: FirebaseUser, displayName: String): User {
        val uid = firebaseUser.uid
        val email = firebaseUser.email.orEmpty()
        val userDocRef = firestore.collection("users").document(uid)

        val isTeacherEmail = email.contains("guru") || email.endsWith("@guru.sma.belajar.id")

        val docSnapshot = try {
            userDocRef.get().await()
        } catch (e: Exception) {
            null
        }

        return if (docSnapshot != null && docSnapshot.exists()) {
            val roleStr = docSnapshot.getString("role") ?: if (isTeacherEmail) "TEACHER" else "STUDENT"
            val role = if (roleStr == "TEACHER" || roleStr == "ADMIN") UserRole.TEACHER else UserRole.STUDENT
            User(
                id = uid.hashCode(),
                nama = docSnapshot.getString("nama") ?: displayName,
                nis = docSnapshot.getString("nis") ?: "",
                kelas = docSnapshot.getString("kelas") ?: "X E-1",
                kelompok = docSnapshot.getString("kelompok") ?: "",
                mode = UserMode.INDIVIDU,
                role = role
            )
        } else {
            val assignedRole = if (isTeacherEmail) UserRole.TEACHER else UserRole.STUDENT
            val newUserMap = hashMapOf(
                "id" to uid,
                "email" to email,
                "nama" to displayName,
                "nis" to "",
                "kelas" to "X E-1",
                "kelompok" to "",
                "role" to assignedRole.name,
                "mode" to "INDIVIDU",
                "hotsScore" to 0.0,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userDocRef.set(newUserMap, SetOptions.merge()).await()

            User(
                id = uid.hashCode(),
                nama = displayName,
                nis = "",
                kelas = "X E-1",
                kelompok = "",
                mode = UserMode.INDIVIDU,
                role = assignedRole
            )
        }
    }

    suspend fun verifyTeacherPin(enteredPin: String): Boolean {
        return try {
            val policyDoc = firestore.collection("app_config").document("admin_policy").get().await()
            val storedHash = policyDoc.getString("adminPinHash") ?: hashPin("2026")
            val enteredHash = hashPin(enteredPin.trim())
            storedHash == enteredHash
        } catch (e: Exception) {
            Log.e("CloudAuthManager", "Gagal memverifikasi PIN cloud: ${e.message}", e)
            // Fallback securely checking against standard initial pin hash
            hashPin(enteredPin.trim()) == hashPin("2026")
        }
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun signOut() {
        auth.signOut()
    }
}
