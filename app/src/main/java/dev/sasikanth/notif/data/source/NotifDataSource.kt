package dev.sasikanth.notif.data.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class NotifDataSource(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
