package com.memoryengine.app

import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.domain.scheduler.Rating
import com.memoryengine.app.domain.scheduler.SM2Scheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SM2SchedulerTest {

    private lateinit var scheduler: SM2Scheduler
    private val baseTime = 1700000000000L // arbitrary fixed epoch timestamp

    @Before
    fun setUp() {
        scheduler = SM2Scheduler()
    }

    @Test
    fun `calculateNextReview with AGAIN rating resets review count and sets 1 day interval`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.AGAIN,
            currentIntervalDays = 10,
            currentEaseFactor = 2.5f,
            currentReviewCount = 3,
            currentTimeMillis = baseTime
        )

        assertEquals(0, result.reviewCount)
        assertEquals(1, result.intervalDays)
        assertEquals(baseTime + SM2Scheduler.ONE_DAY_MILLIS, result.nextReviewAt)
        // Ease factor for AGAIN (grade 1) should decrease
        assertTrue(result.easeFactor < 2.5f)
    }

    @Test
    fun `calculateNextReview first repetition with GOOD rating yields 1 day interval`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.GOOD,
            currentIntervalDays = 1,
            currentEaseFactor = 2.5f,
            currentReviewCount = 0,
            currentTimeMillis = baseTime
        )

        assertEquals(1, result.reviewCount)
        assertEquals(1, result.intervalDays)
        assertEquals(baseTime + SM2Scheduler.ONE_DAY_MILLIS, result.nextReviewAt)
        assertEquals(2.5f, result.easeFactor, 0.01f)
    }

    @Test
    fun `calculateNextReview second repetition with GOOD rating yields 6 days interval`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.GOOD,
            currentIntervalDays = 1,
            currentEaseFactor = 2.5f,
            currentReviewCount = 1,
            currentTimeMillis = baseTime
        )

        assertEquals(2, result.reviewCount)
        assertEquals(6, result.intervalDays)
        assertEquals(baseTime + (6 * SM2Scheduler.ONE_DAY_MILLIS), result.nextReviewAt)
        assertEquals(2.5f, result.easeFactor, 0.01f)
    }

    @Test
    fun `calculateNextReview subsequent repetition multiplies interval by ease factor`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.GOOD,
            currentIntervalDays = 6,
            currentEaseFactor = 2.5f,
            currentReviewCount = 2,
            currentTimeMillis = baseTime
        )

        assertEquals(3, result.reviewCount)
        assertEquals(15, result.intervalDays) // 6 * 2.5 = 15
        assertEquals(baseTime + (15 * SM2Scheduler.ONE_DAY_MILLIS), result.nextReviewAt)
    }

    @Test
    fun `calculateNextReview with EASY rating increases ease factor`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.EASY,
            currentIntervalDays = 6,
            currentEaseFactor = 2.5f,
            currentReviewCount = 2,
            currentTimeMillis = baseTime
        )

        assertTrue(result.easeFactor > 2.5f)
        assertEquals(baseTime + (result.intervalDays * SM2Scheduler.ONE_DAY_MILLIS), result.nextReviewAt)
    }

    @Test
    fun `calculateNextReview with HARD rating decreases ease factor`() {
        val result = scheduler.calculateNextReview(
            rating = Rating.HARD,
            currentIntervalDays = 6,
            currentEaseFactor = 2.5f,
            currentReviewCount = 2,
            currentTimeMillis = baseTime
        )

        assertTrue(result.easeFactor < 2.5f)
        assertTrue(result.intervalDays > 6)
        assertEquals(baseTime + (result.intervalDays * SM2Scheduler.ONE_DAY_MILLIS), result.nextReviewAt)
    }

    @Test
    fun `calculateNextReview for RecallPrompt entity updates entity fields correctly`() {
        val prompt = RecallPrompt(
            id = "prompt-1",
            chunkId = "chunk-1",
            question = "Sample Q?",
            answer = "Sample A.",
            intervalDays = 6,
            easeFactor = 2.5f,
            nextReviewAt = baseTime,
            reviewCount = 2
        )

        val updatedPrompt = scheduler.calculateNextReview(prompt, Rating.GOOD, baseTime)

        assertEquals("prompt-1", updatedPrompt.id)
        assertEquals(3, updatedPrompt.reviewCount)
        assertEquals(15, updatedPrompt.intervalDays)
        assertEquals(baseTime + (15 * SM2Scheduler.ONE_DAY_MILLIS), updatedPrompt.nextReviewAt)
    }
}
