package dev.sasikanth.pinnit.pages.historynotifs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import dev.sasikanth.pinnit.databinding.FragmentHistoryBinding
import dev.sasikanth.pinnit.di.activityViewModels
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.shared.ItemTouchHelperCallback
import dev.sasikanth.pinnit.shared.NotifAdapterListener
import dev.sasikanth.pinnit.shared.NotifDividerItemDecorator
import dev.sasikanth.pinnit.shared.NotifListAdapter
import dev.sasikanth.pinnit.shared.animators.CustomItemAnimator

class HistoryFragment : Fragment() {

    private val mainViewModel by activityViewModels { injector.mainViewModel }

    private lateinit var binding: FragmentHistoryBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val adapter = NotifListAdapter(NotifAdapterListener(
            onNotifClick = { notifItem ->
                try {
                    val packageManager = requireActivity().packageManager
                    packageManager?.let {
                        startActivity(
                            packageManager.getLaunchIntentForPackage(notifItem.packageName)
                        )
                    }
                } catch (e: Exception) {
                    // TODO: Handle exception
                }
            },
            pinNote = { notifId, isPinned ->
                mainViewModel.pinUnpinNotif(notifId, isPinned)
            }
        ))

        binding.notifHistoryList.setHasFixedSize(true)
        binding.notifHistoryList.itemAnimator =
            CustomItemAnimator()
        binding.notifHistoryList.adapter = adapter
        binding.notifHistoryList.addItemDecoration(NotifDividerItemDecorator(requireContext()))

        val itemTouchHelperCallback = ItemTouchHelperCallback(
            context = requireContext(),
            onItemSwiped = { notifItem ->
                notifItem?.let {
                    mainViewModel.deleteNotif(notifId = notifItem._id)
                }
            })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.notifHistoryList)

        // TODO: Verify item updates when app is opened
        mainViewModel.notifList.observe(viewLifecycleOwner, Observer {
            binding.notifErrorLayout.errorNotifView.isVisible = it.isNullOrEmpty()
            binding.notifHistoryList.isVisible = !it.isNullOrEmpty()

            adapter.submitList(it)
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}
