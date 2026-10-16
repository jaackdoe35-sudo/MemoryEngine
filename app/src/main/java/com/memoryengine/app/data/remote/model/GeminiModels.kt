package com.memoryengine.app.data.remote.model

data class GeminiRequest(
    val contents: List<Content>
) {
    data class Content(
        val parts: List<Part>
    )
    data class Part(
        val text: String
    )

    companion object {
        fun create(promptText: String): GeminiRequest {
            return GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = promptText))
                    )
                )
            )
        }
    }
}

data class GeminiResponse(
    val candidates: List<Candidate>? = null
) {
    data class Candidate(
        val content: Content? = null
    )
    data class Content(
        val parts: List<Part>? = null
    )
    data class Part(
        val text: String? = null
    )

    val firstText: String?
        get() = candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}
