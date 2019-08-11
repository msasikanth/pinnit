package dev.sasikanth.notif.pages.apps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.sasikanth.notif.databinding.FragmentAppsBinding
import dev.sasikanth.notif.di.injector
import dev.sasikanth.notif.di.viewModels
import dev.sasikanth.notif.shared.AppsCustomItemAnimator

class AppsFragment : Fragment() {

    private val appsViewModel by viewModels { injector.appsViewModel }

    private lateinit var binding: FragmentAppsBinding
    private lateinit var appsAdapter: AppsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppsBinding.inflate(layoutInflater)

        appsAdapter = AppsAdapter(AppItemListener { appItem ->
            appsViewModel.setAllowState(appItem)
        })

        appsViewModel.installedApps.observe(viewLifecycleOwner, Observer { apps ->
            binding.appsLoading.isVisible = false
            binding.appsList.isVisible = true
            appsAdapter.submitList(apps)
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.appsList.itemAnimator = AppsCustomItemAnimator()
        binding.appsList.setHasFixedSize(true)
        binding.appsList.adapter = appsAdapter
    }
}
