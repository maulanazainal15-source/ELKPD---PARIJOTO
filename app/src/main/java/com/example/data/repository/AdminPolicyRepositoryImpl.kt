package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.ConfigDao
import com.example.data.local.entity.ConfigEntity
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.repository.AdminPolicyRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class AdminPolicyRepositoryImpl(
    private val configDao: ConfigDao,
    private val firestore: FirebaseFirestore? = null
) : AdminPolicyRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var snapshotListener: ListenerRegistration? = null

    init {
        startRealtimePolicySync()
    }

    private fun startRealtimePolicySync() {
        val fs = firestore ?: return
        try {
            snapshotListener = fs.collection("app_config").document("admin_policy")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AdminPolicyRepo", "Firestore policy error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        scope.launch {
                            val school = snapshot.getString("schoolName") ?: "SMA Negeri 1 Gunung Muria"
                            val year = snapshot.getString("academicYear") ?: "2026/2027"
                            val pin = snapshot.getString("adminPin") ?: "2026"
                            val pinHash = snapshot.getString("adminPinHash") ?: hashPin(pin)
                            val kkm = snapshot.getLong("minimumPassingGradeKkm")?.toInt() ?: 75
                            val reqPin = snapshot.getBoolean("requireAdminPinForLogin") ?: true
                            val reqNip = snapshot.getBoolean("requireApprovedNipPrefix") ?: false
                            val nipPrefix = snapshot.getString("approvedNipPrefix") ?: "19"
                            val classes = snapshot.getString("allowedStudentClasses") ?: "X-1, X-2, X-3, X E-1, X E-2"
                            val evalOpen = snapshot.getBoolean("isEvaluationOpen") ?: true
                            val aiTutor = snapshot.getBoolean("isAiTutorEnabled") ?: true
                            val banner = snapshot.getString("announcementBanner") ?: ""

                            val entries = listOf(
                                ConfigEntity(KEY_SCHOOL_NAME, school),
                                ConfigEntity(KEY_ACADEMIC_YEAR, year),
                                ConfigEntity(KEY_ADMIN_PIN, pin),
                                ConfigEntity(KEY_ADMIN_PIN_HASH, pinHash),
                                ConfigEntity(KEY_PASSING_GRADE_KKM, kkm.toString()),
                                ConfigEntity(KEY_REQUIRE_ADMIN_PIN, reqPin.toString()),
                                ConfigEntity(KEY_REQUIRE_NIP_PREFIX, reqNip.toString()),
                                ConfigEntity(KEY_APPROVED_NIP_PREFIX, nipPrefix),
                                ConfigEntity(KEY_ALLOWED_CLASSES, classes),
                                ConfigEntity(KEY_IS_EVALUATION_OPEN, evalOpen.toString()),
                                ConfigEntity(KEY_IS_AI_TUTOR_ENABLED, aiTutor.toString()),
                                ConfigEntity(KEY_ANNOUNCEMENT, banner)
                            )
                            configDao.setConfigs(entries)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("AdminPolicyRepo", "Gagal memulai realtime policy sync: ${e.message}", e)
        }
    }

    override fun observePolicy(): Flow<AdminRoleAccessPolicy> {
        return configDao.observeAllConfigs().map { list ->
            mapConfigsToPolicy(list.associate { it.key to it.value })
        }
    }

    override suspend fun getPolicy(): AdminRoleAccessPolicy {
        val map = configDao.getAllConfigs().associate { it.key to it.value }
        return mapConfigsToPolicy(map)
    }

    override suspend fun savePolicy(policy: AdminRoleAccessPolicy) {
        val pinHash = hashPin(policy.adminPin.ifBlank { "2026" })
        val entries = listOf(
            ConfigEntity(KEY_ADMIN_PIN, policy.adminPin.ifBlank { "2026" }),
            ConfigEntity(KEY_ADMIN_PIN_HASH, pinHash),
            ConfigEntity(KEY_REQUIRE_ADMIN_PIN, policy.requireAdminPinForLogin.toString()),
            ConfigEntity(KEY_REQUIRE_NIP_PREFIX, policy.requireApprovedNipPrefix.toString()),
            ConfigEntity(KEY_APPROVED_NIP_PREFIX, policy.approvedNipPrefix),
            ConfigEntity(KEY_ALLOW_STUDENT_REG, policy.allowStudentSelfRegistration.toString()),
            ConfigEntity(KEY_REQUIRE_CLASS_CODE, policy.requireClassAccessCode.toString()),
            ConfigEntity(KEY_CLASS_ACCESS_CODE, policy.classAccessCode.ifBlank { "MURIA2026" }),
            ConfigEntity(KEY_ALLOWED_CLASSES, policy.allowedStudentClasses.ifBlank { "X-1, X-2, X-3, X E-1, X E-2" }),
            ConfigEntity(KEY_ALLOW_INDIVIDU, policy.allowIndividuMode.toString()),
            ConfigEntity(KEY_ALLOW_KELOMPOK, policy.allowKelompokMode.toString()),
            ConfigEntity(KEY_PASSING_GRADE_KKM, policy.minimumPassingGradeKkm.coerceIn(50, 95).toString()),
            ConfigEntity(KEY_IS_EVALUATION_OPEN, policy.isEvaluationOpen.toString()),
            ConfigEntity(KEY_IS_AI_TUTOR_ENABLED, policy.isAiTutorEnabled.toString()),
            ConfigEntity(KEY_SCHOOL_NAME, policy.schoolName.ifBlank { "SMA Negeri 1 Gunung Muria" }),
            ConfigEntity(KEY_ACADEMIC_YEAR, policy.academicYear.ifBlank { "2026/2027" }),
            ConfigEntity(KEY_ANNOUNCEMENT, policy.announcementBanner)
        )
        configDao.setConfigs(entries)

        val fs = firestore
        if (fs != null) {
            try {
                val map = hashMapOf<String, Any>(
                    "schoolName" to policy.schoolName.ifBlank { "SMA Negeri 1 Gunung Muria" },
                    "academicYear" to policy.academicYear.ifBlank { "2026/2027" },
                    "adminPin" to policy.adminPin.ifBlank { "2026" },
                    "adminPinHash" to pinHash,
                    "minimumPassingGradeKkm" to policy.minimumPassingGradeKkm.coerceIn(50, 95),
                    "requireAdminPinForLogin" to policy.requireAdminPinForLogin,
                    "requireApprovedNipPrefix" to policy.requireApprovedNipPrefix,
                    "approvedNipPrefix" to policy.approvedNipPrefix,
                    "allowedStudentClasses" to policy.allowedStudentClasses,
                    "isEvaluationOpen" to policy.isEvaluationOpen,
                    "isAiTutorEnabled" to policy.isAiTutorEnabled,
                    "announcementBanner" to policy.announcementBanner,
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "updatedBy" to "Guru / Administrator"
                )
                fs.collection("app_config").document("admin_policy")
                    .set(map, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                Log.w("AdminPolicyRepo", "Gagal sinkron policy ke Firestore: ${e.message}")
            }
        }
    }

    override suspend fun resetToDefaultPolicy(): AdminRoleAccessPolicy {
        val defaultPolicy = AdminRoleAccessPolicy()
        savePolicy(defaultPolicy)
        return defaultPolicy
    }

    private fun mapConfigsToPolicy(map: Map<String, String>): AdminRoleAccessPolicy {
        val defaults = AdminRoleAccessPolicy()
        return AdminRoleAccessPolicy(
            adminPin = map[KEY_ADMIN_PIN] ?: defaults.adminPin,
            requireAdminPinForLogin = map[KEY_REQUIRE_ADMIN_PIN]?.toBooleanStrictOrNull() ?: defaults.requireAdminPinForLogin,
            requireApprovedNipPrefix = map[KEY_REQUIRE_NIP_PREFIX]?.toBooleanStrictOrNull() ?: defaults.requireApprovedNipPrefix,
            approvedNipPrefix = map[KEY_APPROVED_NIP_PREFIX] ?: defaults.approvedNipPrefix,
            allowStudentSelfRegistration = map[KEY_ALLOW_STUDENT_REG]?.toBooleanStrictOrNull() ?: defaults.allowStudentSelfRegistration,
            requireClassAccessCode = map[KEY_REQUIRE_CLASS_CODE]?.toBooleanStrictOrNull() ?: defaults.requireClassAccessCode,
            classAccessCode = map[KEY_CLASS_ACCESS_CODE] ?: defaults.classAccessCode,
            allowedStudentClasses = map[KEY_ALLOWED_CLASSES] ?: defaults.allowedStudentClasses,
            allowIndividuMode = map[KEY_ALLOW_INDIVIDU]?.toBooleanStrictOrNull() ?: defaults.allowIndividuMode,
            allowKelompokMode = map[KEY_ALLOW_KELOMPOK]?.toBooleanStrictOrNull() ?: defaults.allowKelompokMode,
            minimumPassingGradeKkm = map[KEY_PASSING_GRADE_KKM]?.toIntOrNull() ?: defaults.minimumPassingGradeKkm,
            isEvaluationOpen = map[KEY_IS_EVALUATION_OPEN]?.toBooleanStrictOrNull() ?: defaults.isEvaluationOpen,
            isAiTutorEnabled = map[KEY_IS_AI_TUTOR_ENABLED]?.toBooleanStrictOrNull() ?: defaults.isAiTutorEnabled,
            schoolName = map[KEY_SCHOOL_NAME] ?: defaults.schoolName,
            academicYear = map[KEY_ACADEMIC_YEAR] ?: defaults.academicYear,
            announcementBanner = map[KEY_ANNOUNCEMENT] ?: defaults.announcementBanner
        )
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val KEY_ADMIN_PIN = "policy_admin_pin"
        const val KEY_ADMIN_PIN_HASH = "policy_admin_pin_hash"
        const val KEY_REQUIRE_ADMIN_PIN = "policy_require_admin_pin"
        const val KEY_REQUIRE_NIP_PREFIX = "policy_require_nip_prefix"
        const val KEY_APPROVED_NIP_PREFIX = "policy_approved_nip_prefix"
        const val KEY_ALLOW_STUDENT_REG = "policy_allow_student_registration"
        const val KEY_REQUIRE_CLASS_CODE = "policy_require_class_code"
        const val KEY_CLASS_ACCESS_CODE = "policy_class_access_code"
        const val KEY_ALLOWED_CLASSES = "policy_allowed_classes"
        const val KEY_ALLOW_INDIVIDU = "policy_allow_individu_mode"
        const val KEY_ALLOW_KELOMPOK = "policy_allow_kelompok_mode"
        const val KEY_PASSING_GRADE_KKM = "policy_kkm_passing_grade"
        const val KEY_IS_EVALUATION_OPEN = "policy_is_evaluation_open"
        const val KEY_IS_AI_TUTOR_ENABLED = "policy_is_ai_tutor_enabled"
        const val KEY_SCHOOL_NAME = "policy_school_name"
        const val KEY_ACADEMIC_YEAR = "policy_academic_year"
        const val KEY_ANNOUNCEMENT = "policy_announcement_banner"
    }
}
