package dev.sasikanth.notif.historypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import dev.sasikanth.notif.MainViewModel
import dev.sasikanth.notif.databinding.FragmentHistoryBinding
import dev.sasikanth.notif.shared.CustomItemAnimator
import dev.sasikanth.notif.shared.ItemTouchHelperCallback
import dev.sasikanth.notif.shared.NotifAdapterListener
import dev.sasikanth.notif.shared.NotifListAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentHistoryBinding

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

        val itemTouchHelperCallback = ItemTouchHelperCallback(onItemSwiped = { notifItem ->
            notifItem?.let {
                mainViewModel.deleteNotif(notifId = notifItem._id)
            }
        })
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.notifHistoryList)

        mainViewModel.notifList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.lifecycleOwner = this
        return binding.root
    }
}
