package com.artemchep.literaryclock.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.SearchManager
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.FragmentAboutBinding
import com.artemchep.literaryclock.logic.viewmodels.AboutViewModel
import com.artemchep.literaryclock.models.DependencyItem
import com.artemchep.literaryclock.ui.adapters.DependencyAdapter
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener
import com.artemchep.literaryclock.utils.ext.launchInCustomTabs
import com.artemchep.literaryclock.utils.ext.setOnApplyWindowInsetsListener
import com.artemchep.literaryclock.utils.wrapInStatusBarView

/**
 * @author Artem Chepurnoy
 */
class AboutFragment : BaseFragment<FragmentAboutBinding>(), View.OnClickListener,
    OnItemClickListener<DependencyItem> {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutBinding
        get() = FragmentAboutBinding::inflate

    private val aboutViewModel: AboutViewModel by viewModels()

    private lateinit var adapter: DependencyAdapter

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
            viewBinding.scrollView.updatePadding(
                bottom = insets.systemWindowInsetBottom,
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            viewBinding.navUpBtnContainer.updatePadding(
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )

            view.findViewById<View>(R.id.statusBarBg).apply {
                layoutParams.height = insets.systemWindowInsetTop
                requestLayout()
            }

            insets.consumeSystemWindowInsets()
        }

        viewBinding.navUpBtn.setOnClickListener(this)
        viewBinding.appGithubBtn.setOnClickListener(this)
        viewBinding.linkedInBtn.setOnClickListener(this)
        viewBinding.instagramBtn.setOnClickListener(this)
        viewBinding.twitterBtn.setOnClickListener(this)
        viewBinding.appShareBtn.setOnClickListener(this)

        viewBinding.depsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.depsRecyclerView.adapter = DependencyAdapter()
            .also(::adapter::set)
            .apply {
                // Listen to the on click events
                onItemClickListener = this@AboutFragment
            }

        aboutViewModel.setup()
    }

    private fun AboutViewModel.setup() {
        openUrlEvent.observe(viewLifecycleOwner, Observer(::showUrl))
        depsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.show(it)
        })
        versionLiveData.observe(viewLifecycleOwner, Observer {
            val appVersion = getString(R.string.about_app_version__template, it.versionName)
            viewBinding.appVersionTextView.text = appVersion
        })
    }

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(activity!!)

    override fun onClick(view: View) {
        when (view.id) {
            R.id.navUpBtn -> findNavController().navigateUp()
            R.id.appGithubBtn -> aboutViewModel.openGitHub()
            R.id.appShareBtn -> {
                val subject = getString(R.string.app_name)
                val text = "Literary Clock represents time in a form of literature qoutes. " +
                        "Check it out: https://play.google.com/store/apps/details?id=com.artemchep.literaryclock"
                try {
                    val i = Intent(Intent.ACTION_SEND).apply {
                        type = "description/plain"
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    startActivity(Intent.createChooser(i, getString(R.string.app_share)))
                } catch (e: ActivityNotFoundException) {
                }
            }
            R.id.twitterBtn -> aboutViewModel.openTwitter()
            R.id.instagramBtn -> aboutViewModel.openInstagram()
            R.id.linkedInBtn -> aboutViewModel.openLinkedIn()
        }
    }

    override fun onItemClick(view: View, data: DependencyItem, position: Int) {
        try {
            val i = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, data.name)
            }
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
        }
    }

}
