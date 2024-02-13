package com.udacity.locationreminders.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.google.common.truth.Truth
import com.udacity.locationreminders.locationreminders.data.FakeDataSource
import com.udacity.locationreminders.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.Q])
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private var fakeDataTest: FakeDataSource = FakeDataSource()

    private lateinit var viewModel: RemindersListViewModel

    private val dataTest = ReminderDTO(
        "Data Title", "Data description", "Data location", 106.0, 107.3
    )

    @Before
    fun initConfig() {
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), fakeDataTest
        )
        stopKoin()
    }


    @Test
    fun testInternalError() = runBlockingTest {
        fakeDataTest.isInternalError = true
        fakeDataTest.deleteAllReminders()
        viewModel.loadReminders()
        assertEquals("Internal errors while getting reminders", viewModel.showSnackBar.value)
    }

    @Test
    fun testLoadingData() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        fakeDataTest.isInternalError = false
        fakeDataTest.saveReminder(dataTest)
        viewModel.loadReminders()
        Truth.assertThat(viewModel.showLoading.value).isTrue()
        mainCoroutineRule.resumeDispatcher()
        Truth.assertThat(viewModel.showLoading.value).isFalse()
    }

}