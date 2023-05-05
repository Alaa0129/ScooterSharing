package dk.itu.moapd.scootersharing.alia

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.itu.moapd.scootersharing.alia.fragments.MainFragment

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MainFragmentInstrumentedTest {
    @Test
    fun testOpenRentalHistoryFragment() {
        val mainFragment = launchFragmentInContainer<MainFragment>()

        onView(ViewMatchers.withId(R.id.list_rides_button)).perform(click())

        onView(ViewMatchers.withId(R.id.listRidesFragment)).check(matches(isDisplayed()))
    }
}