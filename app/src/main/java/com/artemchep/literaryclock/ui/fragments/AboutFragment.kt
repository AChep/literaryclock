package com.artemchep.literaryclock.ui.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.artemchep.literaryclock.utils.ext.startActivityIfExists
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewBinding.appGithubBtn.setOnClickListener(this)
        viewBinding.linkedInBtn.setOnClickListener(this)
        viewBinding.instagramBtn.setOnClickListener(this)
        viewBinding.twitterBtn.setOnClickListener(this)
        viewBinding.appShareBtn.setOnClickListener(this)
        @SuppressLint("SetTextI18n")
        viewBinding.authorFlag.text = "\uD83C\uDDFA\uD83C\uDDE6"

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

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(requireActivity())

    override fun onClick(view: View) {
        when (view.id) {
            R.id.appGithubBtn -> aboutViewModel.openGitHub()
            R.id.appShareBtn -> {
                val subject = getString(R.string.app_name)
                val text = "Literary Clock represents time in a form of literature quotes. " +
                        "Check it out: https://play.google.com/store/apps/details?id=com.artemchep.literaryclock"
                val i = Intent(Intent.ACTION_SEND).apply {
                    type = "description/plain"
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivityIfExists(Intent.createChooser(i, getString(R.string.app_share)))
            }
            R.id.twitterBtn -> aboutViewModel.openTwitter()
            R.id.instagramBtn -> aboutViewModel.openInstagram()
            R.id.linkedInBtn -> aboutViewModel.openLinkedIn()
        }
    }

    override fun onItemClick(view: View, data: DependencyItem, position: Int) {
        val i = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, data.name)
        }
        startActivityIfExists(i)
    }

}
