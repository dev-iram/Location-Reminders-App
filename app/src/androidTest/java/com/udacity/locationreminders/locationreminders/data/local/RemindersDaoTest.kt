package com.udacity.locationreminders.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.locationreminders.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private lateinit var myDao: RemindersDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val dummyListDataTest = mutableListOf(
        ReminderDTO("title 1", "description1", "my location 1", 123.0, 321.0),
        ReminderDTO("title 2", "description2", "my location 2", 456.0, 125.0),
        ReminderDTO("title 3", "description3", "my location 3", 456.0, 789.0)
    )

    @Before
    fun initTestConfig() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        myDao = database.reminderDao()
        setUpInsertAll()
    }

    @Test
    fun testSaveAndGetByIdData() = runBlockingTest {
        val dataMock =
        ReminderDTO("title 4", "description4", "my location 4", 111.0, 222.0)
        myDao.saveReminder(dataMock)
        val actual = myDao.getReminderById(dataMock.id)
        assertThat(actual, `is`(dataMock))
    }

    @Test
    fun testGetReminders() = runBlockingTest {
        val actual = myDao.getReminders()
        assertThat(actual, `is`(dummyListDataTest))
    }

    @Test
    fun testDeleteAllReminders() = runBlockingTest {
         myDao.deleteAllReminders()
        val actual = myDao.getReminders()
        assertThat(actual, `is`(emptyList()))
    }


    fun setUpInsertAll() {
        runBlocking {
            myDao.saveReminder(dummyListDataTest[0])
            myDao.saveReminder(dummyListDataTest[1])
            myDao.saveReminder(dummyListDataTest[2])
        }
    }

}