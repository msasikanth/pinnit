package dev.sasikanth.notif.currentpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sasikanth.notif.MainViewModel
import dev.sasikanth.notif.R
import dev.sasikanth.notif.databinding.FragmentCurrentBinding
import dev.sasikanth.notif.services.NotifListenerService
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CurrentFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentCurrentBinding
    private var notifListenerService: NotifListenerService? = null

    override fun onStart() {
        super.onStart()
        notifListenerService = NotifListenerService.getInstanceIfConnected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentBinding.inflate(layoutInflater, container, false)
        binding.mainViewModel = mainViewModel
        binding.notifErrorLayout.errorNotifAction.setOnClickListener {
            findNavController().navigate(R.id.actionHistoryFragment)
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}
