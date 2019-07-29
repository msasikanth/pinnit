/**
 *
 * Copyright © 2019-2020 Sasikanth Miriyampalli <sasikanthmiriyampalli@gmail.com>.
 * All Rights Reserved
 *
 */

package dev.sasikanth.notif.utils

import androidx.lifecycle.Observer

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been consumed.
 *
 * [onEventUnconsumedContent] is *only* called if the [Event]'s contents has not been consumed.
 */
class EventObserver<T>(private val onEventUnconsumedContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.consume()?.run(onEventUnconsumedContent)
    }
}
