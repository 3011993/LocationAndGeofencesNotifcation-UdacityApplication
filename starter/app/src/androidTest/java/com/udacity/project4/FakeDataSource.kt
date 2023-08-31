package com.udacity.project4

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

open class FakeDataSource : ReminderDataSource {

    private val remindersList: MutableList<ReminderDTO> = mutableListOf()
    var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        try {
            if (shouldReturnError) {
                throw Exception("Error occurred while trying to retrieve reminders!")
            }
            Result.Success(remindersList)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        try {
            val firstOrNull = remindersList.firstOrNull { it.id == id }
            if (shouldReturnError) {
                throw Exception("Error occurred while trying to retrieve reminder with $id !")
            } else if (firstOrNull == null) {
                Result.Error("Reminder not found!")
            } else {
                Result.Success(firstOrNull)
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }


    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }
}


