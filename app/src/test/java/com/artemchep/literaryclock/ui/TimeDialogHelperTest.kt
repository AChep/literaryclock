package com.artemchep.literaryclock.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.artemchep.literaryclock.models.Time
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimeDialogHelperTest {
    class TimePickerHostFragment : Fragment() {
        companion object {
            var pickedTime: Time? = null
        }

        override fun onCreateView(
            inflater: android.view.LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View = View(requireContext())

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            parentFragmentManager.restoreTimePickerDialog("time_picker") { pickedTime = it }
        }
    }

    @Test
    fun restoredPickerDeliversSelectedTime() {
        val controller = Robolectric.buildActivity(FragmentActivity::class.java).setup()
        try {
            TimePickerHostFragment.pickedTime = null
            controller.get().supportFragmentManager.beginTransaction()
                .add(android.R.id.content, TimePickerHostFragment(), "host")
                .commitNow()
            controller.get().supportFragmentManager.showTimePickerDialog("time_picker", Time(8 * 60 + 15)) {}
            controller.get().supportFragmentManager.executePendingTransactions()

            controller.recreate()
            val picker = controller.get().supportFragmentManager.fragments
                .filterIsInstance<MaterialTimePicker>()
                .single()
            picker.requireView().findViewById<android.view.View>(
                com.google.android.material.R.id.material_timepicker_ok_button
            ).performClick()

            assertThat(TimePickerHostFragment.pickedTime).isEqualTo(Time(8 * 60 + 15))
        } finally {
            controller.pause().stop().destroy()
        }
    }
}
