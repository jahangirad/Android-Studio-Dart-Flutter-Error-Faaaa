package org.faaaa

import com.intellij.openapi.diagnostic.Logger
import java.awt.Toolkit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object FaaaaSoundPlayer {
    private val log = Logger.getInstance(FaaaaSoundPlayer::class.java)
    private var lastPlayed: Long = 0
    private const val COOLDOWN_MS = 5000L
    private const val MIN_PERSISTENCE_MS = 2500L
    private const val MIN_HITS_BEFORE_PLAY = 2
    private const val SAME_SIGNAL_SUPPRESS_MS = 15000L
    private const val SIGNAL_RESET_GAP_MS = 2000L
    private const val SIGNAL_TTL_MS = 120000L

    private val audioExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "faaaa-audio-player").apply { isDaemon = true }
    }
    private val signalState = ConcurrentHashMap<String, SignalState>()

    private data class SignalState(
        var firstSeenAt: Long,
        var lastSeenAt: Long,
        var hits: Int = 1,
        var lastPlayedAt: Long = 0
    )

    fun playWithCooldown(signalId: String) {
        val now = System.currentTimeMillis()

        val state = signalState.compute(signalId) { _, existing ->
            val current = existing ?: SignalState(now, now)
            if (now - current.lastSeenAt > SIGNAL_RESET_GAP_MS) {
                current.firstSeenAt = now
                current.hits = 0
            }
            current.lastSeenAt = now
            current.hits += 1
            current
        } ?: return

        if (now - state.firstSeenAt < MIN_PERSISTENCE_MS) return
        if (state.hits < MIN_HITS_BEFORE_PLAY) return
        if (now - state.lastPlayedAt < SAME_SIGNAL_SUPPRESS_MS) return
        if (now - lastPlayed < COOLDOWN_MS) return

        lastPlayed = now
        state.lastPlayedAt = now
        cleanupStaleSignals(now)
        play()
    }

    private fun cleanupStaleSignals(now: Long) {
        signalState.entries.removeIf { now - it.value.lastSeenAt > SIGNAL_TTL_MS }
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
