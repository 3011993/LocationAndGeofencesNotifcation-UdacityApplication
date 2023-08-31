package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.locationreminders.ReminderAppTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest : ReminderAppTest() {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Test
    fun loadRemindersWhenRemindersAreUnavailable() {

        // Given fake data resource with set Return error true
        fakeDataSource.setReturnError(true)

        //when view model load reminders
        remindersListViewModel.loadReminders()

        //than assert the result
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("Error occurred while trying to retrieve reminders!")
        )

    }

    @Test
    fun noData() = mainCoroutineRule.runBlockingTest {
        // given fake data delete all reminder
        fakeDataSource.deleteAllReminders()
        //when view model load reminders
        remindersListViewModel.loadReminders()
        //than assert that
        assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            `is`(true)
        )
    }

    @Test
    fun showLoading_withData() = mainCoroutineRule.runBlockingTest {
        //Given fresh fake data resource and add new reminder
        fakeDataSource.deleteAllReminders()
        val reminder = ReminderDTO("title", "description", "location", 40.99, 50.00, "id")
        fakeDataSource.saveReminder(reminder)
        //when view model load reminders
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        //than assert show loading live data
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(false))


    }

}