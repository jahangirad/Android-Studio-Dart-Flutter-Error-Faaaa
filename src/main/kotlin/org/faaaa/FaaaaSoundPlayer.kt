package org.faaaa

import com.intellij.openapi.diagnostic.Logger
import java.awt.Toolkit
import java.util.concurrent.Executors

object FaaaaSoundPlayer {
    private val log = Logger.getInstance(FaaaaSoundPlayer::class.java)
    private var lastPlayed: Long = 0
    private const val COOLDOWN_MS = 5000L

    private val audioExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "faaaa-audio-player").apply { isDaemon = true }
    }

    fun playWithCooldown() {
        val now = System.currentTimeMillis()
        if (now - lastPlayed < COOLDOWN_MS) return
        lastPlayed = now
        play()
    }

    private fun play() {
        audioExecutor.execute {
            try {
                val inputStream = FaaaaSoundPlayer::class.java.getResourceAsStream("/sound.mp3")
                    ?: FaaaaSoundPlayer::class.java.getResourceAsStream("/resources/sound.mp3")

                if (inputStream == null) {
                    log.warn("Sound resource not found: /sound.mp3")
                    Toolkit.getDefaultToolkit().beep()
                    return@execute
                }

                inputStream.use { stream ->
                    javazoom.jl.player.Player(stream).play()
                }
            } catch (e: Exception) {
                log.warn("Failed to play sound.mp3; using fallback beep", e)
                Toolkit.getDefaultToolkit().beep()
            }
        }
    }
}

