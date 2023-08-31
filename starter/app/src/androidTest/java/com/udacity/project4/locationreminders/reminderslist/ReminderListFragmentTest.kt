package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.FakeDataSource
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()






    @Before
    fun init() {
        dataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(
                getApplicationContext(),
                dataSource
            )
        stopKoin()
        val myModule = module {
            single {
                viewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }




    @After
    fun stopDown() {
        stopKoin()
    }

        @Test
        fun reminders_DisplayUi() = runBlockingTest{

            //Given reminder and save it to repository
            val reminder = ReminderDTO("title","description","location",40.99,50.00,"id")
                dataSource.saveReminder(reminder)

            //when launch reminder list fragment
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

            //than assert reminder and match it
            onView(withId(R.id.noDataTextView)).check(
                ViewAssertions.matches(
                    CoreMatchers.not(
                        ViewMatchers.isDisplayed()
                    )
                )
            )
            onView(ViewMatchers.withText(reminder.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            onView(ViewMatchers.withText(reminder.description)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
            onView(ViewMatchers.withText(reminder.location)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )

        }

        @Test
        fun noReminders_shows_noData() = runBlockingTest{

            //when user navigate reminder list fragment
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

            //assert the view data is displayed or not
            onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

        @Test
        fun fabButtonCLicked_navigatesTo_saveReminderFragment() = runBlockingTest {
            // given fragment scenario and  nav controller
            val scenario =
                launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
            val navController = mock(NavController::class.java)

            //when user navigate to another fragment
            scenario.onFragment {
                Navigation.setViewNavController(it.view!!, navController)
            }


            //than assert the button and nav controller
            onView(withId(R.id.addReminderFAB)).perform(click())
            verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
        }




    }











