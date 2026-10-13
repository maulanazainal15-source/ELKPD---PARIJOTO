package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole
import com.example.ui.components.ParijotoTextField
import com.example.ui.components.PrimaryButton
import com.example.ui.navigation.Screen

@Composable
fun IdentityScreen(
    navController: NavController,
    viewModel: IdentityViewModel = viewModel(factory = IdentityViewModel.Factory),
    onRegisteredCallback: ((String, String, String, String, String) -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    var pendingQuickLoginUser by remember { mutableStateOf<User?>(null) }
    var quickLoginPin by remember { mutableStateOf("") }

    LaunchedEffect(state.isSuccessRegister) {
        if (state.isSuccessRegister) {
            onRegisteredCallback?.invoke(
                state.nama,
                state.nis,
                state.kelas,
                if (state.mode == UserMode.KELOMPOK) state.kelompok else "INDIVIDU",
                state.mode.name
            )
            val destination = if (state.role == UserRole.TEACHER) {
                Screen.TeacherDashboard.route
            } else {
                Screen.Home.route
            }
            navController.navigate(destination) {
                popUpTo(Screen.Identity.route) { inclusive = true }
            }
        }
    }

    val allowedClasses = state.policy.getAllowedClassesList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header dengan Identitas Sekolah dari Dashboard Admin
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(top = 36.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${state.policy.schoolName} • TA ${state.policy.academicYear}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Portal Akses Peran E-LKPD",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Masuk sebagai Siswa atau Guru/Administrator sesuai ketentuan yang diatur pada Dashboard Admin.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Card Pemilihan Peran Pengguna (Siswa vs Guru/Administrator)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pilih Peran Pengguna",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Setiap peran memiliki hak akses dan ketentuan verifikasi tersendiri:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RoleSelectionCard(
                            title = "Siswa",
                            subtitle = "Akses Modul PBL, AR & Evaluasi",
                            icon = Icons.Default.Person,
                            selected = state.role == UserRole.STUDENT,
                            onClick = { viewModel.onRoleChanged(UserRole.STUDENT) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_chip_student")
                        )
                        RoleSelectionCard(
                            title = "Guru / Admin",
                            subtitle = "Monitoring, Analitik & Kebijakan",
                            icon = Icons.Default.AdminPanelSettings,
                            selected = state.role == UserRole.TEACHER,
                            onClick = { viewModel.onRoleChanged(UserRole.TEACHER) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_chip_teacher")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Banner Ketentuan Aktif dari Dashboard Admin
                    AdminRequirementsNoticeCard(
                        role = state.role,
                        requireAdminPin = state.policy.requireAdminPinForLogin,
                        adminPinHint = state.policy.adminPin,
                        requireNipPrefix = state.policy.requireApprovedNipPrefix,
                        nipPrefix = state.policy.approvedNipPrefix,
                        requireClassCode = state.policy.requireClassAccessCode,
                        allowedClasses = allowedClasses,
                        kkm = state.policy.minimumPassingGradeKkm
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Input Dinamis Berdasarkan Peran & Ketentuan Admin
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (state.role == UserRole.STUDENT) {
                            "Formulir Identitas Peserta Didik"
                        } else {
                            "Formulir Otorisasi Guru / Administrator"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ParijotoTextField(
                        value = state.nama,
                        onValueChange = viewModel::onNamaChanged,
                        label = if (state.role == UserRole.STUDENT) "Nama Lengkap Siswa" else "Nama Lengkap Guru / Admin",
                        placeholder = if (state.role == UserRole.STUDENT) "Contoh: Siti Aminah" else "Contoh: Ahzami, S.Pd., Gr",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ParijotoTextField(
                        value = state.nis,
                        onValueChange = viewModel::onNisChanged,
                        label = if (state.role == UserRole.STUDENT) "NIS / NISN" else "NIP / NUPTK / ID Administrator",
                        placeholder = if (state.role == UserRole.STUDENT) {
                            "Masukkan NIS / NISN"
                        } else if (state.policy.requireApprovedNipPrefix) {
                            "Wajib diawali '${state.policy.approvedNipPrefix}' (Contoh: ${state.policy.approvedNipPrefix}8901...)"
                        } else {
                            "Masukkan NIP atau ID Guru"
                        },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                    )

                    if (state.role == UserRole.STUDENT) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Pilihan Kelas Sesuai Ketentuan Dashboard Admin
                        Text(
                            text = "Pilih Kelas (Sesuai Ketentuan Admin)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (allowedClasses.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allowedClasses.forEach { cls ->
                                    FilterChip(
                                        selected = state.kelas == cls,
                                        onClick = { viewModel.onKelasChanged(cls) },
                                        label = { Text(cls) },
                                        leadingIcon = if (state.kelas == cls) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null
                                    )
                                }
                            }
                        } else {
                            ParijotoTextField(
                                value = state.kelas,
                                onValueChange = viewModel::onKelasChanged,
                                label = "Kelas",
                                placeholder = "Contoh: X-1, X-2",
                                leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pemilihan Mode Pengerjaan (Dikontrol oleh Ketentuan Admin)
                        Text(
                            text = "Mode Pengerjaan E-LKPD",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.policy.allowIndividuMode) {
                                RadioButton(
                                    selected = state.mode == UserMode.INDIVIDU,
                                    onClick = { viewModel.onModeChanged(UserMode.INDIVIDU) }
                                )
                                Text(text = "Individu", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            if (state.policy.allowKelompokMode) {
                                RadioButton(
                                    selected = state.mode == UserMode.KELOMPOK,
                                    onClick = { viewModel.onModeChanged(UserMode.KELOMPOK) }
                                )
                                Text(text = "Kelompok", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        if (state.mode == UserMode.KELOMPOK && state.policy.allowKelompokMode) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ParijotoTextField(
                                value = state.kelompok,
                                onValueChange = viewModel::onKelompokChanged,
                                label = "Nama / Nomor Kelompok",
                                placeholder = "Contoh: Kelompok 1 (Sunan Muria)",
                                leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) }
                            )
                        }

                        if (state.policy.requireClassAccessCode) {
                            Spacer(modifier = Modifier.height(12.dp))
                            ParijotoTextField(
                                value = state.classAccessCodeInput,
                                onValueChange = viewModel::onClassAccessCodeChanged,
                                label = "Kode Akses Kelas (Ditentukan Guru/Admin)",
                                placeholder = "Masukkan kode akses kelas (Contoh: ${state.policy.classAccessCode})",
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) }
                            )
                        }
                        // Input Khusus Guru / Administrator
                        if (state.policy.requireAdminPinForLogin) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = state.adminPinInput,
                                onValueChange = viewModel::onAdminPinChanged,
                                label = { Text("PIN Otorisasi Guru / Administrator") },
                                placeholder = { Text("Masukkan PIN Keamanan") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.errorMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = when {
                            state.isLoading -> "Memverifikasi..."
                            state.role == UserRole.TEACHER -> "Masuk ke Dashboard Admin / Guru"
                            else -> "Masuk sebagai Siswa & Mulai Belajar"
                        },
                        onClick = { viewModel.registerUser() },
                        enabled = state.isFormValid && !state.isLoading
                    )
                }
            }

            // Daftar Akun Tersimpan untuk Beralih Peran Cepat (Quick Role Switcher)
            if (state.existingUsers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Masuk Cepat dari Akun Tersimpan",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Pilih akun Siswa atau Guru/Admin yang pernah terdaftar di perangkat ini:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        state.existingUsers.take(5).forEach { user ->
                            val isTeacher = user.role == UserRole.TEACHER
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (isTeacher && state.policy.requireAdminPinForLogin) {
                                            pendingQuickLoginUser = user
                                            quickLoginPin = ""
                                        } else {
                                            viewModel.selectExistingUser(user)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isTeacher) MaterialTheme.colorScheme.secondaryContainer
                                                    else MaterialTheme.colorScheme.primaryContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isTeacher) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (isTeacher) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = user.nama,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = if (isTeacher) "Peran: GURU / ADMIN • ID: ${user.nis}"
                                                else "Peran: SISWA • Kelas ${user.kelas} • NIS: ${user.nis}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            if (isTeacher && state.policy.requireAdminPinForLogin) {
                                                pendingQuickLoginUser = user
                                                quickLoginPin = ""
                                            } else {
                                                viewModel.selectExistingUser(user)
                                            }
                                        }
                                    ) {
                                        Text("Masuk", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog Verifikasi PIN saat pilih Akun Guru/Admin Tersimpan
    if (pendingQuickLoginUser != null) {
        val targetUser = pendingQuickLoginUser!!
        AlertDialog(
            onDismissRequest = { pendingQuickLoginUser = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Verifikasi PIN Guru / Admin") },
            text = {
                Column {
                    Text(
                        text = "Masuk sebagai ${targetUser.nama} memerlukan PIN Keamanan sesuai ketentuan Dashboard Admin.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = quickLoginPin,
                        onValueChange = { quickLoginPin = it },
                        label = { Text("PIN Guru / Admin") },
                        placeholder = { Text("Masukkan PIN") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pinToUse = quickLoginPin
                        pendingQuickLoginUser = null
                        viewModel.selectExistingUser(targetUser, pinToUse)
                    }
                ) {
                    Text("Verifikasi & Masuk")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingQuickLoginUser = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Terpilih",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = contentColor,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun AdminRequirementsNoticeCard(
    role: UserRole,
    requireAdminPin: Boolean,
    adminPinHint: String,
    requireNipPrefix: Boolean,
    nipPrefix: String,
    requireClassCode: Boolean,
    allowedClasses: List<String>,
    kkm: Int
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (role == UserRole.STUDENT) {
                        "Ketentuan Masuk Peran Siswa (Dashboard Admin):"
                    } else {
                        "Ketentuan Masuk Peran Guru / Administrator:"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (role == UserRole.STUDENT) {
                    Text(
                        text = "• Kelas aktif: ${allowedClasses.joinToString().ifBlank { "Semua Kelas" }}\n" +
                            "• Target KKM Evaluasi HOTS: $kkm Poin\n" +
                            if (requireClassCode) "• Wajib memasukkan Kode Akses Kelas dari Guru/Admin"
                            else "• Pendaftaran mandiri terbuka tanpa kode akses khusus",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    Text(
                        text = (if (requireAdminPin) "• Wajib verifikasi PIN Keamanan Admin (PIN aktif: $adminPinHint)\n" else "• Login langsung tanpa PIN Admin\n") +
                            (if (requireNipPrefix) "• NIP/ID wajib diawali prefix '$nipPrefix'\n" else "• Semua format NIP/ID Guru diizinkan\n") +
                            "• Hak penuh mengelola kebijakan peran, rubrik & analitik kelas",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
