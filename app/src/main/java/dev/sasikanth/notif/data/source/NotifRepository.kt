package dev.sasikanth.notif.data.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class NotifRepository(
    private val notifDataSource: NotifDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
