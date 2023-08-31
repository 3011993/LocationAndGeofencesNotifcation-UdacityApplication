package com.udacity.project4.locationreminders

import com.udacity.project4.authentication.LoginViewModel


import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mockito.Mockito

open class ReminderAppTest {
    // init coroutines dispatcher for testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // handle each task synchronously.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    var application = Mockito.mock(Application::class.java)
    lateinit var fakeDataSource: FakeDataSource
    lateinit var remindersListViewModel: RemindersListViewModel
    lateinit var saveReminderViewModel: SaveReminderViewModel


    @Before
    fun before() {
        val applicationContext: Application = application
        fakeDataSource= Mockito.spy(FakeDataSource())
        remindersListViewModel = RemindersListViewModel(application, fakeDataSource)
        saveReminderViewModel = SaveReminderViewModel(application, fakeDataSource)
    }

    @Before
    fun initKoin() {
        //stop last module
        stopKoin()
        val applicationContext: Application =
            application
        val myTestModule = module {
            viewModel {
                RemindersListViewModel(
                    applicationContext,
                    get() as ReminderDataSource
                )
            }
            viewModel {
                LoginViewModel()
            }
            single {
                SaveReminderViewModel(
                    applicationContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single(named("isTesting")) { true }
        }
        // init new module
        startKoin {
            modules(listOf(myTestModule))
        }
    }
}