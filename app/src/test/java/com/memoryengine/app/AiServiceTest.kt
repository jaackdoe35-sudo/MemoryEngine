package com.memoryengine.app

import com.memoryengine.app.data.remote.api.GeminiApiService
import com.memoryengine.app.data.remote.service.GeminiAiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var aiService: GeminiAiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(GeminiApiService::class.java)
        aiService = GeminiAiService(apiService = apiService, apiKey = "test_api_key")
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `generateAssociation sends prompt and returns AI response`() = runBlocking {
        val jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      { "text": "Synaptic plasticity connects short-term memory with long-term retention." }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = aiService.generateAssociation("Neuroscience notes on memory formation.")

        assertEquals("Synaptic plasticity connects short-term memory with long-term retention.", result)

        val recordedRequest = mockWebServer.takeRequest()
        assertNotNull(recordedRequest)
        assertEquals("/v1beta/models/gemini-1.5-flash:generateContent?key=test_api_key", recordedRequest.path)
    }

    @Test
    fun `generateRecallPrompt parses JSON response correctly`() = runBlocking {
        val jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      { "text": "{\"question\": \"What mechanism consolidates memories?\", \"answer\": \"Long-term potentiation (LTP).\"}" }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val prompt = aiService.generateRecallPrompt("Long-term potentiation is a persistent strengthening of synapses.")

        assertEquals("What mechanism consolidates memories?", prompt.question)
        assertEquals("Long-term potentiation (LTP).", prompt.answer)
    }

    @Test
    fun `generateAnswer returns AI text answer`() = runBlocking {
        val jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      { "text": "The hippocampus is crucial for spatial memory and navigation." }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val answer = aiService.generateAnswer("Explain the role of the hippocampus.")

        assertEquals("The hippocampus is crucial for spatial memory and navigation.", answer)
    }

    @Test
    fun `aiService fallback when API key is blank`() = runBlocking {
        val emptyKeyService = GeminiAiService(apiService = aiServiceLet(), apiKey = "")

        val prompt = emptyKeyService.generateRecallPrompt("Test text input")
        assertNotNull(prompt.question)
        assertNotNull(prompt.answer)

        val assoc = emptyKeyService.generateAssociation("Test text input")
        assertNotNull(assoc)
    }

    private fun aiServiceLet(): GeminiApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(GeminiApiService::class.java)
    }
}
