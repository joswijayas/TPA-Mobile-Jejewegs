package edu.bluejack22_1.Jejewegs

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.bluejack22_1.Jejewegs.Model.*
import edu.bluejack22_1.Jejewegs.Model.Notification
import edu.bluejack22_1.Jejewegs.databinding.ActivityCreateNotificationBinding
import java.text.DateFormat
import java.util.*

class CreateNotificationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        binding.submitButton.setOnClickListener{
            scheduleNotification()
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = getString(R.string.reminder_for_create_review)
        val message = binding.etInputMessage.text.toString()
        if(message.isEmpty()){
            binding.etInputMessage.error = getString(R.string.must_be_filled)
            binding.etInputMessage.requestFocus()
        }else{
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val time = getTime()
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
            showAlert(time, title, message)
        }


    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reminder_for_create_review))
            .setMessage(
                getString(R.string.title_) + title +
                        "\n"+getString(R.string.message_) + message +
                        "\n"+getString(R.string.at_) + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Oke"){_,_->}
            .show()

    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }


    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}