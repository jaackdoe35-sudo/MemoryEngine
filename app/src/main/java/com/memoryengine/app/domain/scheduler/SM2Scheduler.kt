package com.memoryengine.app.domain.scheduler

import com.memoryengine.app.data.local.entity.RecallPrompt
import kotlin.math.max
import kotlin.math.roundToInt

enum class Rating {
    AGAIN,
    HARD,
    GOOD,
    EASY
}

data class SM2Result(
    val intervalDays: Int,
    val easeFactor: Float,
    val reviewCount: Int,
    val nextReviewAt: Long
)

class SM2Scheduler {

    companion object {
        const val DEFAULT_EASE_FACTOR = 2.5f
        const val MIN_EASE_FACTOR = 1.3f
        const val ONE_DAY_MILLIS = 86_400_000L
    }

    fun calculateNextReview(
        rating: Rating,
        currentIntervalDays: Int = 1,
        currentEaseFactor: Float = DEFAULT_EASE_FACTOR,
        currentReviewCount: Int = 0,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SM2Result {
        val grade = when (rating) {
            Rating.AGAIN -> 1
            Rating.HARD -> 3
            Rating.GOOD -> 4
            Rating.EASY -> 5
        }

        // New Ease Factor calculation: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEaseFactor = max(
            MIN_EASE_FACTOR,
            currentEaseFactor + (0.1f - (5 - grade) * (0.08f + (5 - grade) * 0.02f))
        )

        val newReviewCount: Int
        val newIntervalDays: Int

        if (grade < 3) {
            // Repetition failed (AGAIN)
            newReviewCount = 0
            newIntervalDays = 1
        } else {
            newReviewCount = currentReviewCount + 1
            newIntervalDays = when (currentReviewCount) {
                0 -> 1
                1 -> 6
                else -> max(
                    currentIntervalDays + 1,
                    (currentIntervalDays * newEaseFactor).roundToInt()
                )
            }
        }

        val nextReviewAt = currentTimeMillis + (newIntervalDays * ONE_DAY_MILLIS)

        return SM2Result(
            intervalDays = newIntervalDays,
            easeFactor = newEaseFactor,
            reviewCount = newReviewCount,
            nextReviewAt = nextReviewAt
        )
    }

    fun calculateNextReview(
        prompt: RecallPrompt,
        rating: Rating,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): RecallPrompt {
        val result = calculateNextReview(
            rating = rating,
            currentIntervalDays = prompt.intervalDays,
            currentEaseFactor = prompt.easeFactor,
            currentReviewCount = prompt.reviewCount,
            currentTimeMillis = currentTimeMillis
        )
        return prompt.copy(
            intervalDays = result.intervalDays,
            easeFactor = result.easeFactor,
            reviewCount = result.reviewCount,
            nextReviewAt = result.nextReviewAt
        )
    }
}
