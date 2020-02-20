package dev.sasikanth.pinnit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.Result
import dev.sasikanth.pinnit.data.source.PinnitRepository
import dev.sasikanth.pinnit.utils.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    private val pinnitRepository: PinnitRepository
) : ViewModel() {

  val pinnitList: LiveData<List<PinnitItem>> = pinnitRepository
      .notifications()
      .asLiveData()

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

  fun togglePinStatus(key: Long, isPinned: Boolean) {
    viewModelScope.launch {
      pinnitRepository.pinStatus(key, !isPinned)
    }
  }

  fun deleteUnPinned() {
    viewModelScope.launch {
      if (!pinnitList.value.isNullOrEmpty()) pinnitRepository.deleteUnPinned()
    }
  }

  // TODO (SM): Delete all duplicate notifications?
  fun deleteNotification(key: Long) {
    viewModelScope.launch {
      val pinnitItem = pinnitRepository.notification(key)
      if (pinnitItem is Result.Success) {
        val deletedRow = pinnitRepository.delete(key)
        if (deletedRow == 1) {
          _notifDeleted.postValue(Event(pinnitItem.data))
        }
      }
    }
  }

  fun insertNotification(pinnitItem: PinnitItem) {
    viewModelScope.launch {
      pinnitRepository.insert(pinnitItem)
    }
  }
}
