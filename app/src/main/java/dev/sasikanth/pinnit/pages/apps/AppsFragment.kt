package dev.sasikanth.pinnit.pages.apps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.sasikanth.pinnit.databinding.FragmentAppsBinding
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.di.viewModels
import dev.sasikanth.pinnit.shared.animators.AppsCustomItemAnimator

class AppsFragment : Fragment() {

    private val appsViewModel by viewModels { injector.appsViewModel }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAppsBinding = FragmentAppsBinding.inflate(layoutInflater)

        val appsController = AppsEpoxyController(AppItemListener { appItem ->
            appsViewModel.setAllowState(appItem)
        })
        binding.appsController = appsController
        appsController.setFilterDuplicates(true)
        binding.appsList.itemAnimator = AppsCustomItemAnimator()

        appsViewModel.installedApps.observe(viewLifecycleOwner, Observer { appsList ->
            binding.appsLoading.isVisible = false
            binding.appsList.isVisible = true
            appsController.appsList = appsList
        })

        return binding.root
    }
}
