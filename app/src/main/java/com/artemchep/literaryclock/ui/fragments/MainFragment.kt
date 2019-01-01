package com.artemchep.literaryclock.ui.fragments

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.logic.viewmodels.MainViewModel
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.ui.adapters.QuoteAdapter
import com.artemchep.literaryclock.ui.drawables.AnalogClockDrawable
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener
import com.artemchep.literaryclock.ui.showTimePickerDialog
import com.artemchep.literaryclock.utils.calculateHourHandRotation
import com.artemchep.literaryclock.utils.calculateMinuteHandRotation
import com.artemchep.literaryclock.utils.createTimeFormat
import com.artemchep.literaryclock.utils.ext.launchInCustomTabs
import com.artemchep.literaryclock.utils.formatTime
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.math.PI
import kotlin.math.sin

/**
 * @author Artem Chepurnoy
 */
class MainFragment : Fragment(),
    View.OnClickListener,
    OnItemClickListener<QuoteItem> {

    companion object {
        const val ANALOG_CLOCK_ANIM_DURATION = 1200L
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var adapter: QuoteAdapter

    private val analogClockDrawable by lazy {
        AnalogClockDrawable().apply {
            color = Color.WHITE
        }
    }

    private var analogClockAnimator: ValueAnimator? = null

    /** The format of a digital clock used system-wide */
    private val timeFormat by lazy { createTimeFormat(context!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreBtn.setOnClickListener(this)
        btn.setOnClickListener(this)
        clear.setOnClickListener(this)

        analogClock.foreground = analogClockDrawable

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = QuoteAdapter()
            .also(::adapter::set)
            .apply {
                // Listen to the on click events
                onItemClickListener = this@MainFragment
            }

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.setup()
    }

    private fun MainViewModel.setup() {
        openUrlEvent.observe(viewLifecycleOwner, Observer(::showUrl))
        shareQuoteEvent.observe(viewLifecycleOwner, Observer { quote ->
            val subject = getString(R.string.app_name)
            val text = quote.quote
            try {
                val i = Intent(Intent.ACTION_SEND).apply {
                    type = "description/plain"
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivity(Intent.createChooser(i, getString(R.string.quote_share)))
            } catch (e: ActivityNotFoundException) {
            }
        })
        editTimeEvent.observe(viewLifecycleOwner, Observer { time ->
            context!!.showTimePickerDialog(time, mainViewModel::postTime)
        })

        timeLiveData.observe(viewLifecycleOwner, Observer(::showTime))
        momentLiveData.observe(viewLifecycleOwner, Observer(::showMoment))
        databaseIsUpdatingLiveData.observe(viewLifecycleOwner, Observer(::showDatabaseState))
    }

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(activity!!)

    private fun showMoment(moment: MomentItem) {
        adapter.apply {
            models.apply {
                clear()
                addAll(moment.quotes)
            }

            notifyDataSetChanged()
        }
    }

    private fun showTime(time: Time) {
        showDigitalTime(time)
        showAnalogTime(time)
    }

    private fun showDigitalTime(time: Time) {
        digitalClock.text = formatTime(time, timeFormat)
    }

    private fun showAnalogTime(time: Time) {
        // Cancel previous animation if it
        // existed.
        analogClockAnimator?.cancel()

        val hourHandRotationNew = calculateHourHandRotation(time.time)
        val minuteHandRotationNew = calculateMinuteHandRotation(time.time)

        if (analogClock.isLaidOut) {
            fun rotationDelta(new: Float, old: Float) =
                (new - old).let { dt ->
                    if (dt < 360f - dt) {
                        // Rotate forwards
                        dt
                    } else {
                        // Rotate backwards
                        dt - 360f
                    }
                }

            val hourHandRotationOld = analogClockDrawable.hourHandRotation % 360f
            val minuteHandRotationOld = analogClockDrawable.minuteHandRotation % 360f
            val hourHandRotationDelta =
                rotationDelta(hourHandRotationNew, hourHandRotationOld)
            val minuteHandRotationDelta =
                rotationDelta(minuteHandRotationNew, minuteHandRotationOld)

            // Animate analog clock changing
            // time.
            analogClockAnimator = ValueAnimator
                .ofFloat(0f, 1f)
                .apply {
                    addUpdateListener {
                        val r = it.animatedValue as Float
                        analogClockDrawable.apply {
                            hourHandRotation = hourHandRotationOld + hourHandRotationDelta * r
                            minuteHandRotation = minuteHandRotationOld + minuteHandRotationDelta * r
                        }
                    }

                    interpolator = TimeInterpolator { sin(it * PI * 0.5f).toFloat() }
                    duration = ANALOG_CLOCK_ANIM_DURATION
                    start()
                }
        } else {
            analogClockDrawable.apply {
                hourHandRotation = hourHandRotationNew
                minuteHandRotation = minuteHandRotationNew
            }
        }
    }

    private fun showDatabaseState(state: DatabaseState) {
        progressBar.isVisible = true
        progressBar.animate().cancel() // cancel previous animation
        progressBar
            .animate()
            .apply {
                when (state) {
                    DatabaseState.UPDATING -> scaleY(1f).alpha(1f)
                    DatabaseState.IDLE -> scaleY(0f)
                        .alpha(0f)
                        .withEndAction {
                            progressBar.isVisible = false
                        }
                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn -> mainViewModel.editTime()
            R.id.moreBtn -> showMorePopUp(view)
        }
    }

    override fun onItemClick(view: View, data: QuoteItem, position: Int) {
        when (view.id) {
            R.id.shareBtn -> mainViewModel.shareQuote(data)
            else -> mainViewModel.openQuote(data)
        }
    }

    private fun showMorePopUp(view: View) {
        val popup = PopupMenu(context!!, view)
        val items = arrayOf(
            getString(R.string.donate) to {
                navigate(MainFragmentDirections.actionMainFragmentToDonateQuoteFragment())
            },
            getString(R.string.settings) to {
                navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
            },
            getString(R.string.about) to {
                navigate(MainFragmentDirections.actionMainFragmentToAboutFragment())
            }
        )

        items.forEachIndexed { index, (title) ->
            popup.menu.add(0, index, 0, title)
        }

        popup.setOnMenuItemClickListener { item ->
            items[item.itemId].second.invoke()
            return@setOnMenuItemClickListener false
        }
        popup.show()
    }

    private fun navigate(direction: NavDirections) = view?.findNavController()?.navigate(direction)

}
