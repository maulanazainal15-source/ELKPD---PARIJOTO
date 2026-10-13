package com.example.domain.repository

import com.example.domain.model.AdminRoleAccessPolicy
import kotlinx.coroutines.flow.Flow

interface AdminPolicyRepository {
    fun observePolicy(): Flow<AdminRoleAccessPolicy>
    suspend fun getPolicy(): AdminRoleAccessPolicy
    suspend fun savePolicy(policy: AdminRoleAccessPolicy)
    suspend fun resetToDefaultPolicy(): AdminRoleAccessPolicy
}
