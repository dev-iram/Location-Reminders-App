package com.udacity.locationreminders

import android.app.Application
import com.udacity.locationreminders.locationreminders.data.ReminderDataSource
import com.udacity.locationreminders.locationreminders.data.local.LocalDB
import com.udacity.locationreminders.locationreminders.data.local.RemindersLocalRepository
import com.udacity.locationreminders.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.locationreminders.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

	override fun onCreate() {
		super.onCreate()
		val myModule = module {
			viewModel {
				RemindersListViewModel(
					get(),
					get() as ReminderDataSource
				)
			}
			single {
				SaveReminderViewModel(
					get(),
					get() as ReminderDataSource
				)
			}
			single { RemindersLocalRepository(get()) as ReminderDataSource }
			single { LocalDB.createRemindersDao(this@MyApp) }
		}
		startKoin {
			androidContext(this@MyApp)
			modules(listOf(myModule))
		}
	}
}