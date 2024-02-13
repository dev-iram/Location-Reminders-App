package com.udacity.locationreminders.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.locationreminders.locationreminders.data.dto.ReminderDTO
import com.udacity.locationreminders.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var database: RemindersDatabase
    private lateinit var myDao: RemindersDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initTestConfig() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()

        myDao = database.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(myDao, Dispatchers.Unconfined)
    }

    @Test
    fun testGetRemindersWithError() = runBlockingTest {
        assertThat(
            remindersLocalRepository.getReminder("xxx"),
            `is`(Result.Error("Reminder not found!"))
        )
    }

    @Test
    fun testInsertGetRemindersSuccess() = runBlockingTest {
        val data = ReminderDTO(
            "title 4",
            "description4",
            "my location 4",
            111.0,
            222.0
        )
        remindersLocalRepository.saveReminder(data)
        val actual = remindersLocalRepository.getReminder(data.id)
        assertThat(
            actual,
            `is`(Result.Success(data))
        )
    }

    @Test
    fun testInsertGetAllRemindersSuccess() = runBlockingTest {
        val data = ReminderDTO(
        "title 4",
           "description4",
          "my location 4",
          111.0,
           222.0
        )
        val data2 = ReminderDTO(
            "title 5",
            "description5",
            "my location 5",
            11.0, 22.0
        )
        remindersLocalRepository.saveReminder(data)
        remindersLocalRepository.saveReminder(data2)
        val actual = remindersLocalRepository.getReminders()
        assertThat(
            actual,
            `is`(Result.Success(listOf(data,data2)))
        )
    }

    @Test
    fun testDeleteSuccess() = runBlockingTest {
        val data = ReminderDTO(
            "title 4",
            "description4",
            "my location 4",
            111.0,
            222.0
        )
        remindersLocalRepository.saveReminder(data)
        remindersLocalRepository.deleteAllReminders()
        val actual = remindersLocalRepository.getReminders()
        assertThat(
            actual,
            `is`(Result.Success(listOf()))
        )
    }
}