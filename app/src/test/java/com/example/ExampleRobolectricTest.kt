package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.ElpkdDatabase
import com.example.domain.ElpkdCurriculumData
import com.example.model.AnswerEntity
import com.example.model.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("E-LKPD Parijoto", appName)
    }

    @Test
    fun `verify curriculum data and room persistence`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, ElpkdDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.elpkdDao()

        val userId = dao.insertUser(
            UserEntity(
                nama = "Siti Aisyah",
                nis = "10293",
                kelas = "X E-1",
                kelompok = "Kelompok Muria",
                modeKerja = "KELOMPOK"
            )
        ).toInt()

        dao.upsertAnswer(
            AnswerEntity(
                userId = userId,
                questionId = "akt1_k1_q1",
                answer = "Eksploitasi berlebihan menyebabkan penurunan populasi alami Parijoto di Gunung Muria."
            )
        )

        val savedAnswers = dao.getAnswersForUser(userId).first()
        assertEquals(1, savedAnswers.size)
        assertTrue(ElpkdCurriculumData.defaultMcqQuestions.size == 5)
        assertTrue(ElpkdCurriculumData.tujuanPembelajaran.size == 6)
        db.close()
    }
}
