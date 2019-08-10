package dev.sasikanth.notif.pages.historypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import dev.sasikanth.notif.databinding.FragmentHistoryBinding
import dev.sasikanth.notif.di.activityViewModels
import dev.sasikanth.notif.di.injector
import dev.sasikanth.notif.shared.CustomItemAnimator
import dev.sasikanth.notif.shared.ItemTouchHelperCallback
import dev.sasikanth.notif.shared.NotifAdapterListener
import dev.sasikanth.notif.shared.NotifDividerItemDecorator
import dev.sasikanth.notif.shared.NotifListAdapter

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
                val packageManager = requireActivity().packageManager
                packageManager?.let {
                    startActivity(
                        packageManager.getLaunchIntentForPackage(notifItem.packageName)
                    )
                }
            },
            pinNote = { notifId, isPinned ->
                mainViewModel.pinUnpinNotif(notifId, isPinned)
            }
        ))

        binding.notifHistoryList.setHasFixedSize(true)
        binding.notifHistoryList.itemAnimator = CustomItemAnimator()
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

        mainViewModel.notifList.observe(viewLifecycleOwner, Observer {
            binding.notifErrorLayout.errorNotifView.isVisible = it.isNullOrEmpty()
            binding.notifHistoryList.isVisible = !it.isNullOrEmpty()

            adapter.submitList(it)
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}
