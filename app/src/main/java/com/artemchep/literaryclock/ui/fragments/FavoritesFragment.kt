package com.artemchep.literaryclock.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.FragmentFavoritesBinding
import com.artemchep.literaryclock.logic.viewmodels.FavoritesViewModel
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.ui.adapters.QuoteAdapter
import com.artemchep.literaryclock.ui.interfaces.OnItemClickListener
import com.artemchep.literaryclock.utils.ext.launchInCustomTabs
import com.artemchep.literaryclock.utils.ext.startActivityIfExists

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(),
    OnItemClickListener<QuoteItem> {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavoritesBinding
        get() = FragmentFavoritesBinding::inflate

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private lateinit var adapter: QuoteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.recyclerView.adapter = QuoteAdapter()
            .also(::adapter::set)
            .apply {
                onItemClickListener = this@FavoritesFragment
            }

        favoritesViewModel.setup()
        favoritesViewModel.onFavoritesScreenOpened()
    }

    private fun FavoritesViewModel.setup() {
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
        favoritesLiveData.observe(viewLifecycleOwner, Observer(::showFavorites))
    }

    private fun showUrl(url: String) = url.toUri().launchInCustomTabs(requireActivity())

    private fun showFavorites(quotes: List<QuoteItem>) {
        adapter.show(quotes)
        viewBinding.recyclerView.isVisible = quotes.isNotEmpty()
        viewBinding.emptyStateContainer.isVisible = quotes.isEmpty()
    }

    override fun onItemClick(view: View, data: QuoteItem, position: Int) {
        when (view.id) {
            R.id.shareBtn -> favoritesViewModel.shareQuote(data)
            R.id.bookmarkBtn -> favoritesViewModel.toggleFavorite(data)
            else -> favoritesViewModel.openQuote(data)
        }
    }
}
