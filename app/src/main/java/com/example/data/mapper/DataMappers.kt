package com.example.data.mapper

import com.example.data.local.entity.AnswerEntity
import com.example.data.local.entity.ImageEntity
import com.example.data.local.entity.ProgressEntity
import com.example.data.local.entity.ResultEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.ActivityProgress
import com.example.domain.model.Answer
import com.example.domain.model.EvaluationResult
import com.example.domain.model.ImageNote
import com.example.domain.model.ProgressStatus
import com.example.domain.model.User
import com.example.domain.model.UserMode
import com.example.domain.model.UserRole

// User Mapper
fun UserEntity.toDomain() = User(
    id = id,
    nama = nama,
    nis = nis,
    kelas = kelas,
    kelompok = kelompok,
    mode = runCatching { UserMode.valueOf(mode) }.getOrDefault(UserMode.KELOMPOK),
    role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.STUDENT),
    createdTimestamp = createdTimestamp,
    fullName = nama,
    schoolName = kelompok.ifBlank { "SMA/MA Gunung Muria" },
    className = kelas,
    nisn = nis
)

fun User.toEntity() = UserEntity(
    id = id,
    nama = if (fullName.isNotBlank() && fullName != nama) fullName else nama,
    nis = if (nisn.isNotBlank() && nisn != nis) nisn else nis,
    kelas = if (className.isNotBlank() && className != kelas) className else kelas,
    kelompok = if (schoolName.isNotBlank() && schoolName != kelompok) schoolName else kelompok,
    mode = mode.name,
    role = role.name,
    createdTimestamp = createdTimestamp
)

// Progress Mapper
fun ProgressEntity.toDomain() = ActivityProgress(
    id = id,
    userId = userId,
    activityId = activityId,
    phaseId = phaseId,
    status = runCatching { ProgressStatus.valueOf(status) }.getOrDefault(ProgressStatus.NOT_STARTED),
    percentage = percentage,
    lastUpdated = lastUpdated
)

fun ActivityProgress.toEntity() = ProgressEntity(
    id = id,
    userId = userId,
    activityId = activityId,
    phaseId = phaseId,
    status = status.name,
    percentage = percentage,
    lastUpdated = lastUpdated
)

// Answer Mapper
fun AnswerEntity.toDomain() = Answer(
    id = id,
    userId = userId,
    questionId = questionId,
    activityType = activityType,
    answerText = answerText,
    score = score,
    feedback = feedback,
    timestamp = timestamp
)

fun Answer.toEntity() = AnswerEntity(
    id = id,
    userId = userId,
    questionId = questionId,
    activityType = activityType,
    answerText = answerText,
    score = score,
    feedback = feedback,
    timestamp = timestamp
)

// Image Note Mapper
fun ImageEntity.toDomain() = ImageNote(
    id = id,
    userId = userId,
    sectionKey = sectionKey,
    filePath = filePath,
    caption = caption,
    timestamp = timestamp
)

fun ImageNote.toEntity() = ImageEntity(
    id = id,
    userId = userId,
    sectionKey = sectionKey,
    filePath = filePath,
    caption = caption,
    timestamp = timestamp
)

// Evaluation Result Mapper
fun ResultEntity.toDomain() = EvaluationResult(
    id = id,
    userId = userId,
    scoreMultipleChoice = scoreMultipleChoice,
    scoreEssay = scoreEssay,
    scoreFinal = scoreFinal,
    completedDate = completedDate,
    isSubmitted = isSubmitted,
    pdfExportPath = pdfExportPath,
    pgScore = scoreMultipleChoice.toDouble()
)

fun EvaluationResult.toEntity() = ResultEntity(
    id = id,
    userId = userId,
    scoreMultipleChoice = if (pgScore > 0.0 && scoreMultipleChoice == 0f) pgScore.toFloat() else scoreMultipleChoice,
    scoreEssay = scoreEssay,
    scoreFinal = if (scoreFinal == 0f && pgScore > 0.0) pgScore.toFloat() else scoreFinal,
    completedDate = completedDate.ifBlank {
        java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
    },
    isSubmitted = isSubmitted,
    pdfExportPath = pdfExportPath
)
