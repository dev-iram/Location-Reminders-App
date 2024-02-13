package com.udacity.locationreminders

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoMatchingRootException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.locationreminders.locationreminders.RemindersActivity
import com.udacity.locationreminders.locationreminders.data.ReminderDataSource
import com.udacity.locationreminders.locationreminders.data.local.LocalDB
import com.udacity.locationreminders.locationreminders.data.local.RemindersLocalRepository
import com.udacity.locationreminders.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.locationreminders.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.locationreminders.util.DataBindingIdlingResource
import com.udacity.locationreminders.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) }
            single { LocalDB.createRemindersDao(appContext) }
        }
        startKoin {
            modules(listOf(myModule))
        }
        repository = get()
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun showSnackBarLocationNotInputTest() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.add_new_reminder)).perform(click())
        insertTextIntoInput(R.id.reminderTitle, "my title")
        insertTextIntoInput(R.id.reminderDescription, "my description")
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(appContext.getString(R.string.error_enter_location)))
            .check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun saveLocationToastShowSuccessTest() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.add_new_reminder)).perform(click())
        insertTextIntoInput(R.id.reminderTitle, "my title")
        insertTextIntoInput(R.id.reminderDescription, "my description")
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_button)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        var exceptionCaptured = false
        try {
            onView(withText(R.string.save_reminder))
                .inRoot(
                    withDecorView(
                        not(`is`(activity.window.decorView))
                    )
                )
                .check(doesNotExist())
        } catch (e: NoMatchingRootException) {
            exceptionCaptured = true
        }
        assertTrue(exceptionCaptured)
        activityScenario.close()
    }

    private fun insertTextIntoInput(inputId: Int?, text: String?) {
        onView(withId(inputId!!)).perform(typeText(text), closeSoftKeyboard())
    }

}
