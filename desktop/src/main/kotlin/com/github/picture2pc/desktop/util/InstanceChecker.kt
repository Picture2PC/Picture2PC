package com.github.picture2pc.desktop.util

import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.OverlappingFileLockException
import java.nio.file.StandardOpenOption

object InstanceChecker {
    private const val LOCK_FILE = "app.lock"
    private var lockChannel: FileChannel? = null

    fun isAppAlreadyRunning(): Boolean {
        return try {
            val lockFile = File(System.getProperty("java.io.tmpdir"), LOCK_FILE)
            lockChannel = FileChannel.open(
                lockFile.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            )
            val lock = lockChannel?.tryLock()
            lock == null
        } catch (e: OverlappingFileLockException) {
            true
        } catch (e: Exception) {
            false
        }
    }
}