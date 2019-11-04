package dev.sasikanth.pinnit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.Result
import dev.sasikanth.pinnit.data.source.PinnitRepository
import dev.sasikanth.pinnit.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.launch

class MainViewModel
@Inject constructor(
    private val pinnitRepository: PinnitRepository
) : ViewModel() {

    val pinnitList: LiveData<List<PinnitItem>> = pinnitRepository.getNotifs()

    private val _notifAction = MutableLiveData<Event<Unit>>()
    val notifAction: LiveData<Event<Unit>>
        get() = _notifAction

    private val _showOptionsMenu = MutableLiveData<Event<Unit>>()
    val showOptionsMenu: LiveData<Event<Unit>>
        get() = _showOptionsMenu

    private val _notifDeleted = MutableLiveData<Event<PinnitItem>>()
    val pinnitDeleted: LiveData<Event<PinnitItem>>
        get() = _notifDeleted

    fun showOptionsMenu() {
        _showOptionsMenu.postValue(Event(Unit))
    }

    fun notifAction() {
        _notifAction.postValue(Event(Unit))
    }

    fun pinUnpinNotif(notifId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            if (isPinned) {
                pinnitRepository.pinNotif(notifId)
            } else {
                pinnitRepository.unPinNotif(notifId)
            }
        }
    }

    fun deleteUnPinnedNotifs() {
        if (!pinnitList.value.isNullOrEmpty()) {
            viewModelScope.launch {
                pinnitRepository.deleteUnPinnedNotifs()
            }
        }
    }

    fun deleteNotif(notifId: Long) {
        viewModelScope.launch {
            val notifItem = pinnitRepository.getNotif(notifId)
            if (notifItem is Result.Success) {
                val deletedRow = pinnitRepository.deleteNotif(notifId)
                if (deletedRow == 1) {
                    _notifDeleted.postValue(Event(notifItem.data))
                }
            }
        }
    }

    fun saveNotif(pinnitItem: PinnitItem) {
        viewModelScope.launch {
            pinnitRepository.saveNotif(pinnitItem)
        }
    }
}
