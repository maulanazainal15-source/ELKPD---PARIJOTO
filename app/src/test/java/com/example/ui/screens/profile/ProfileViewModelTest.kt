package com.example.ui.screens.profile

import com.example.domain.model.ActivityProgress
import com.example.domain.model.Answer
import com.example.domain.model.EvaluationResult
import com.example.domain.model.ImageNote
import com.example.domain.model.LearningProgress
import com.example.domain.model.User
import com.example.domain.model.UserProgress
import com.example.domain.repository.EvaluationRepository
import com.example.domain.repository.LearningRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfileData successfully calculates analytics percentage`() = runTest {
        val mockUser = User(
            id = 1,
            fullName = "Siswa Test",
            schoolName = "SMA 1 Kudus",
            className = "XI IPA 1",
            nisn = "123456"
        )
        val mockAct1Progress = UserProgress(
            userId = 1,
            activityId = "ACT_1",
            percentage = 100,
            isCompleted = true
        )
        val mockAct2Progress = UserProgress(
            userId = 1,
            activityId = "ACT_2",
            percentage = 50,
            isCompleted = false
        )

        val fakeUserRepository = object : UserRepository {
            override suspend fun registerOrLoginUser(user: User): Long = 1L
            override suspend fun insertOrUpdateStudent(user: User): Long = 1L
            override suspend fun getUserById(userId: Int): User = mockUser
            override suspend fun getUserByNis(nis: String): User = mockUser
            override fun getAllStudents(): Flow<List<User>> = flowOf(listOf(mockUser))
            override fun getAllUsers(): Flow<List<User>> = flowOf(listOf(mockUser))
            override suspend fun deleteUser(userId: Int) {}
        }

        val fakeLearningRepository = object : LearningRepository {
            override suspend fun saveProgress(progress: ActivityProgress) {}
            override suspend fun insertOrUpdateProgress(progress: LearningProgress) {}
            override fun getProgressByActivity(userId: Int, activityId: String): Flow<List<ActivityProgress>> = flowOf(emptyList())
            override fun getProgressByUserAndActivity(userId: Int, activityId: String): Flow<LearningProgress?> {
                return when (activityId) {
                    "ACT_1" -> flowOf(mockAct1Progress)
                    "ACT_2" -> flowOf(mockAct2Progress)
                    else -> flowOf(null)
                }
            }
            override fun getAllProgressByUser(userId: Int): Flow<List<ActivityProgress>> = flowOf(emptyList())
            override suspend fun clearAllProgressByUser(userId: Int) {}
            override suspend fun saveAnswer(answer: Answer) {}
            override suspend fun getAnswerByQuestion(userId: Int, questionId: String): Answer? = null
            override fun observeAnswerByQuestion(userId: Int, questionId: String): Flow<Answer?> = flowOf(null)
            override fun getAnswersByActivity(userId: Int, activityType: String): Flow<List<Answer>> = flowOf(emptyList())
            override suspend fun saveImageNote(imageNote: ImageNote) {}
            override fun getImagesBySection(userId: Int, sectionKey: String): Flow<List<ImageNote>> = flowOf(emptyList())
            override suspend fun deleteImageNote(imageId: Int) {}
        }

        val fakeEvaluationRepository = object : EvaluationRepository {
            override suspend fun saveEvaluationResult(result: EvaluationResult): Long = 1L
            override suspend fun insertOrUpdateResult(result: EvaluationResult): Long = 1L
            override fun getResultByUser(userId: Int): Flow<EvaluationResult?> = flowOf(null)
            override fun getAllResults(): Flow<List<EvaluationResult>> = flowOf(emptyList())
            override suspend fun clearResultByUser(userId: Int) {}
        }

        val viewModel = ProfileViewModel(
            userRepository = fakeUserRepository,
            learningRepository = fakeLearningRepository,
            evaluationRepository = fakeEvaluationRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Siswa Test", state.user?.fullName)
        assertEquals(75, state.analytics.overallLearningPercentage) // (100 + 50) / 2 = 75%
        assertEquals(1, state.analytics.completedActivitiesCount)
    }
}
