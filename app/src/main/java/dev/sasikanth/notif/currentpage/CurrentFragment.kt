package dev.sasikanth.notif.currentpage

import androidx.fragment.app.Fragment
import dev.sasikanth.notif.services.NotifListenerService

class CurrentFragment : Fragment() {

    private var notifListenerService: NotifListenerService? = null

    override fun onStart() {
        super.onStart()
        notifListenerService = NotifListenerService.getInstanceIfConnected()
    }
}
