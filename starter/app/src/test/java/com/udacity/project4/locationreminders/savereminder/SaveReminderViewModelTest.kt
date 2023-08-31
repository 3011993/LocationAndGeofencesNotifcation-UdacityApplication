package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.ReminderAppTest
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest : ReminderAppTest(){


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()



    @Test
    fun saveReminder_andShowLoadingValue() {

        //Given Reminder to save it
        val reminder = ReminderDataItem("title","description","location",40.99,50.00,"id")

        //when saveviewModel save reminder
        saveReminderViewModel.validateAndSaveReminder(reminder)
        //than assert show loading not null

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),
            notNullValue()
        )

    }



}