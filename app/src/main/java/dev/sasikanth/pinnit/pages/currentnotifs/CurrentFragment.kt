package dev.sasikanth.pinnit.pages.currentnotifs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.databinding.FragmentCurrentBinding
import dev.sasikanth.pinnit.di.activityViewModels
import dev.sasikanth.pinnit.di.injector

class CurrentFragment : Fragment() {

    private val mainViewModel by activityViewModels { injector.mainViewModel }

    private lateinit var binding: FragmentCurrentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentBinding.inflate(layoutInflater, container, false)
        binding.mainViewModel = mainViewModel
        binding.notifErrorLayout.errorNotifAction.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }

        binding.lifecycleOwner = this
        return binding.root
    }
}
