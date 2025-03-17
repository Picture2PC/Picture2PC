package com.github.picture2pc.common.ui.notification

import kotlinx.coroutines.flow.MutableStateFlow

interface NotificationHandler {
    val showNotification: MutableStateFlow<Boolean>
    fun displayNotification(title: String, message: String, popup: Boolean = false)
}