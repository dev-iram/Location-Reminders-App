package com.udacity.locationreminders.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.locationreminders.R
import com.udacity.locationreminders.databinding.ActivityReminderDescriptionBinding
import com.udacity.locationreminders.locationreminders.reminderslist.ReminderDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        // Receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = R.layout.activity_reminder_description
        binding = DataBindingUtil.setContentView(this, layoutId)
        val data = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.apply {
            val sp = String.format(getString(R.string.latitude_longitude), data.latitude,data.longitude)
            titleReminderText.text = data.title
            descriptionText.text = data.description
            locationText.text = data.location.plus(" ").plus(sp)

        }
    }
}