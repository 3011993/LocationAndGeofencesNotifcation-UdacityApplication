package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database : RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb(){
        database = Room.
        inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RemindersDatabase::class.java).
                allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_andGetResult() = runBlocking {

        //GiVEN reminder and save it
        val reminder = ReminderDTO("title","description","location",40.99,50.00,"id")
        repository.saveReminder(reminder)

        //when get the result from repository
        val result = repository.getReminder(reminder.id)

        //than assert the reminder
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data.id, `is`(reminder.id))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun deleteAllReminders() = runBlocking {
        //Given reminder to add it and delete all reminders
        val reminder = ReminderDTO("title","description","location",40.99,50.00,"id")
        repository.saveReminder(reminder)

        //when user delete all reminders
        repository.deleteAllReminders()
        val result = repository.getReminder(reminder.id)

        //assert the result is empty and return error
        assertThat(result is Result.Error, `is` (true))
        result as Result.Error
        assertThat(result.message,`is`( "Reminder not found!"))


    }





}