package com.udacity.locationreminders.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.locationreminders.locationreminders.data.FakeDataSource
import com.udacity.locationreminders.locationreminders.data.dto.ReminderDTO
import com.udacity.locationreminders.locationreminders.data.dto.Result
import com.udacity.locationreminders.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var fakeDataTest: FakeDataSource = FakeDataSource()

    private lateinit var viewModel: SaveReminderViewModel

    private val dataTestValid = ReminderDataItem(
        "Data Title", "Data description", "Data location", 106.0, 107.3
    )
    private val dataTestInValid = ReminderDataItem(
        null, "Data description", null, 106.0, 107.3
    )

    @Before
    fun initConfig() {
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), fakeDataTest
        )
        stopKoin()
    }

    @Test
    fun testSaveReminderInValid() = runBlockingTest {
        viewModel.validateAndSaveReminder(dataTestInValid)
        val actual = fakeDataTest.getReminder(dataTestInValid.id)
        Truth.assertThat(actual).isEqualTo(Result.Error("Not found reminder"))
    }

    @Test
    fun testSaveReminderSuccess() = runBlockingTest {
        fakeDataTest.deleteAllReminders()
        viewModel.validateAndSaveReminder(dataTestValid)
        val actual = fakeDataTest.getReminder(dataTestValid.id)
        val expected = Result.Success(
            ReminderDTO(
                title = dataTestValid.title,
                description = dataTestValid.description,
                location = dataTestValid.location,
                latitude = dataTestValid.latitude,
                longitude = dataTestValid.longitude,
                id = dataTestValid.id
            )
        )
        Truth.assertThat(actual).isEqualTo(expected)
    }
}