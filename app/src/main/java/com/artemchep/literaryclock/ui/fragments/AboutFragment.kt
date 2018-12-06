package com.artemchep.literaryclock.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.BuildConfig
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.logic.viewmodels.AboutViewModel
import com.artemchep.literaryclock.models.DependencyItem
import com.artemchep.literaryclock.ui.adapters.DependencyAdapter
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener
import com.artemchep.literaryclock.utils.ext.launchInCustomTabs
import kotlinx.android.synthetic.main.fragment_about.*

/**
 * @author Artem Chepurnoy
 */
class AboutFragment : Fragment(), View.OnClickListener, OnItemClickListener<DependencyItem> {

    private lateinit var aboutViewModel: AboutViewModel

    private lateinit var adapter: DependencyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navUpBtn.setOnClickListener(this)
        appGithubBtn.setOnClickListener(this)
        googlePlusBtn.setOnClickListener(this)
        linkedInBtn.setOnClickListener(this)
        instagramBtn.setOnClickListener(this)
        twitterBtn.setOnClickListener(this)

        depsRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        depsRecyclerView.adapter = DependencyAdapter()
            .also(::adapter::set)
            .apply {
                // Listen to the on click events
                onItemClickListener = this@AboutFragment
            }

        aboutViewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)
        aboutViewModel.setup()
    }

    private fun AboutViewModel.setup() {
        openUrlEvent.observe(viewLifecycleOwner, Observer(::showUrl))
        depsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.show(it)
        })
        versionLiveData.observe(viewLifecycleOwner, Observer {
            val appVersion = getString(R.string.about_app_version__template, it.versionName)
            appVersionTextView.text = appVersion
        })
    }

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(activity!!)

    override fun onClick(view: View) {
        when (view.id) {
            R.id.navUpBtn -> findNavController().navigateUp()
            R.id.appGithubBtn -> aboutViewModel.openGitHub()
            R.id.appShareBtn -> {
                val subject = getString(R.string.app_name)
                val text = "Check out Literary Clock!"
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
            R.id.googlePlusBtn -> aboutViewModel.openGooglePlus()
        }
    }

    override fun onItemClick(view: View, data: DependencyItem, position: Int) {
    }

}
