package com.example.data.repository

import com.example.data.local.dao.ConfigDao
import com.example.data.local.entity.ConfigEntity
import com.example.domain.model.AdminRoleAccessPolicy
import com.example.domain.repository.AdminPolicyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminPolicyRepositoryImpl(
    private val configDao: ConfigDao
) : AdminPolicyRepository {

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
        val entries = listOf(
            ConfigEntity(KEY_ADMIN_PIN, policy.adminPin.ifBlank { "2026" }),
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

    companion object {
        private const val KEY_ADMIN_PIN = "policy_admin_pin"
        private const val KEY_REQUIRE_ADMIN_PIN = "policy_require_admin_pin"
        private const val KEY_REQUIRE_NIP_PREFIX = "policy_require_nip_prefix"
        private const val KEY_APPROVED_NIP_PREFIX = "policy_approved_nip_prefix"
        private const val KEY_ALLOW_STUDENT_REG = "policy_allow_student_reg"
        private const val KEY_REQUIRE_CLASS_CODE = "policy_require_class_code"
        private const val KEY_CLASS_ACCESS_CODE = "policy_class_access_code"
        private const val KEY_ALLOWED_CLASSES = "policy_allowed_classes"
        private const val KEY_ALLOW_INDIVIDU = "policy_allow_individu"
        private const val KEY_ALLOW_KELOMPOK = "policy_allow_kelompok"
        private const val KEY_PASSING_GRADE_KKM = "policy_passing_grade_kkm"
        private const val KEY_IS_EVALUATION_OPEN = "policy_is_evaluation_open"
        private const val KEY_IS_AI_TUTOR_ENABLED = "policy_is_ai_tutor_enabled"
        private const val KEY_SCHOOL_NAME = "policy_school_name"
        private const val KEY_ACADEMIC_YEAR = "policy_academic_year"
        private const val KEY_ANNOUNCEMENT = "policy_announcement"
    }
}
