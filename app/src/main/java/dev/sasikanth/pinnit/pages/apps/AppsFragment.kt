package dev.sasikanth.pinnit.pages.apps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.di.viewModels
import kotlinx.android.synthetic.main.fragment_apps.*

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
    return inflater.inflate(R.layout.fragment_apps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val appsAdapter = AppsAdapter { appItem ->
      appsViewModel.toggleAppSelection(appItem)
    }

    with(appsRecyclerView) {
      setHasFixedSize(true)
      (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
      adapter = appsAdapter
    }

    appsViewModel.installedApps.observe(viewLifecycleOwner, Observer { appsList ->
      appsLoaded()
      appsAdapter.submitList(appsList)
    })
  }

  override fun onDestroyView() {
    appsRecyclerView.adapter = null
    super.onDestroyView()
  }

  private fun appsLoaded() {
    appsProgressBar.isVisible = false
    appsRecyclerView.isVisible = true
  }
}
