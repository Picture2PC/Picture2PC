package com.github.picture2pc.common.ui

import kotlinx.coroutines.flow.StateFlow

interface NotificationHandler {
    val showNotification: StateFlow<Boolean>

    fun displayNotification(title: String, message: String, popup: Boolean = false)
    fun hideNotification()
}