package com.github.picture2pc.common.ui

interface NotificationHandler {
    fun displayNotification(title: String, message: String, popup: Boolean = false)
}