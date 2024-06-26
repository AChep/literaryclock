package com.artemchep.literaryclock.ui.fragments

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.data.DatabaseState
import com.artemchep.literaryclock.databinding.FragmentMainBinding
import com.artemchep.literaryclock.logic.viewmodels.MainViewModel
import com.artemchep.literaryclock.models.MomentItem
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.models.Time
import com.artemchep.literaryclock.ui.adapters.QuoteAdapter
import com.artemchep.literaryclock.ui.drawables.AnalogClockDrawable
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener
import com.artemchep.literaryclock.ui.setProgressBarShown
import com.artemchep.literaryclock.ui.showTimePickerDialog
import com.artemchep.literaryclock.utils.*
import com.artemchep.literaryclock.utils.ext.launchInCustomTabs
import com.artemchep.literaryclock.utils.ext.setOnApplyWindowInsetsListener
import com.artemchep.literaryclock.utils.ext.startActivityIfExists
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin


/**
 * @author Artem Chepurnoy
 */
class MainFragment : BaseFragment<FragmentMainBinding>(),
    View.OnClickListener,
    OnItemClickListener<QuoteItem> {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    companion object {
        const val ANALOG_CLOCK_ANIM_DURATION = 1200L
    }

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var adapter: QuoteAdapter

    private val analogClockDrawable by lazy {
        AnalogClockDrawable().apply {
            val typedValue = TypedValue()
            val theme = requireContext().theme
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnSecondaryContainer,
                typedValue,
                true
            )
            color = typedValue.data
        }
    }

    private var analogClockAnimator: ValueAnimator? = null

    /** The format of a digital clock used system-wide */
    private val timeFormat by lazy { createTimeFormat(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = super.onCreateView(inflater, container, savedInstanceState)
        .let {
            wrapInStatusBarView(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnApplyWindowInsetsListener { insets ->
            fun Float.asPixelsPlusInsetBottom() =
                insets.systemWindowInsetBottom +
                        (this * resources.displayMetrics.density).roundToInt()

            viewBinding.recyclerView.updatePadding(
                bottom = 72.0f.asPixelsPlusInsetBottom(),
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            viewBinding.moreBtnContainer.updatePadding(
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            viewBinding.btnContainer.updatePadding(
                bottom = insets.systemWindowInsetBottom,
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )

            view.findViewById<View>(R.id.statusBarBg).apply {
                layoutParams.height = insets.systemWindowInsetTop
                requestLayout()
            }

            insets.consumeSystemWindowInsets()
        }

        viewBinding.moreBtn.setOnClickListener(this)
        viewBinding.btn.setOnClickListener(this)

        viewBinding.analogClock.foreground = analogClockDrawable

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.recyclerView.adapter = QuoteAdapter()
            .also(::adapter::set)
            .apply {
                // Listen to the on click events
                onItemClickListener = this@MainFragment
            }

        mainViewModel.setup()
    }

    private fun MainViewModel.setup() {
        openUrlEvent.observe(viewLifecycleOwner, Observer(::showUrl))
        shareQuoteEvent.observe(viewLifecycleOwner) { quote ->
            val subject = getString(R.string.app_name)
            val text = quote.quote(requireContext())
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "description/plain"
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivityIfExists(Intent.createChooser(i, getString(R.string.quote_share)))
        }
        editTimeEvent.observe(viewLifecycleOwner) { time ->
            parentFragmentManager.showTimePickerDialog(time, mainViewModel::postTime)
        }

        timeLiveData.observe(viewLifecycleOwner, Observer(::showTime))
        momentLiveData.observe(viewLifecycleOwner, Observer(::showMoment))
        databaseIsUpdatingLiveData.observe(viewLifecycleOwner, Observer(::showDatabaseState))
    }

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(requireActivity())

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
        viewBinding.digitalClock.text = formatTime(time, timeFormat)
    }

    private fun showAnalogTime(time: Time) {
        // Cancel previous animation if it
        // existed.
        analogClockAnimator?.cancel()

        val hourHandRotationNew = calculateHourHandRotation(time.time)
        val minuteHandRotationNew = calculateMinuteHandRotation(time.time)

        if (viewBinding.analogClock.isLaidOut) {
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

    private fun showDatabaseState(state: DatabaseState) =
        viewBinding.progressBar.setProgressBarShown(state.isWorking)

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
        val popup = PopupMenu(requireContext(), view)
        val items = arrayOf(
            getString(R.string.donate_iap) to {
                navigate(MainFragmentDirections.actionMainFragmentToDonateFragment())
            },
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
