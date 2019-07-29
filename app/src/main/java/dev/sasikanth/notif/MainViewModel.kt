package dev.sasikanth.notif

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.source.NotifRepository
import dev.sasikanth.notif.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val notifRepository: NotifRepository) : ViewModel() {

    val notifList: LiveData<List<NotifItem>> = notifRepository.getNotifs()

    private val _notifAction = MutableLiveData<Event<Unit>>()
    val notifAction: LiveData<Event<Unit>>
        get() = _notifAction

    private val _showOptionsMenu = MutableLiveData<Event<Unit>>()
    val showOptionsMenu: LiveData<Event<Unit>>
        get() = _showOptionsMenu

    fun showOptionsMenu() {
        _showOptionsMenu.postValue(Event(Unit))
    }

    fun notifAction() {
        _notifAction.postValue(Event(Unit))
    }

    fun pinUnpinNotif(notifId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            if (isPinned) {
                notifRepository.pinNotif(notifId)
            } else {
                notifRepository.unPinNotif(notifId)
            }
        }
    }

    fun deleteUnPinnedNotifs() {
        if (!notifList.value.isNullOrEmpty()) {
            viewModelScope.launch {
                notifRepository.deleteUnPinnedNotifs()
            }
        }
    }

    fun deleteNotif(notifId: Long) {
        viewModelScope.launch {
            notifRepository.deleteNotif(notifId)
        }
    }
}
