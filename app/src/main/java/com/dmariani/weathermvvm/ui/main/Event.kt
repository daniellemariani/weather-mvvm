package com.dmariani.weathermvvm.ui.main

/**
 * Wraps a value that should only be consumed once, e.g. a one-time Snackbar
 * message. getContentIfNotHandled() returns the content on first call and null
 * on any subsequent call, preventing re-delivery on LiveData re-observation
 * (e.g. after a configuration change).
 */
class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}