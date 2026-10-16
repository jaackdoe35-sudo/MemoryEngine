package com.memoryengine.app.worker

object TextChunker {
    fun chunkText(text: String, maxWordsPerChunk: Int = 200): List<String> {
        if (text.isBlank()) return emptyList()
        val paragraphs = text.split("\n\n").map { it.trim() }.filter { it.isNotBlank() }
        if (paragraphs.isEmpty()) return listOf(text.trim())

        val result = mutableListOf<String>()
        var currentChunk = StringBuilder()
        var currentWordCount = 0

        for (paragraph in paragraphs) {
            val words = paragraph.split("\\s+".toRegex()).filter { it.isNotBlank() }
            val wordCount = words.size

            if (currentWordCount + wordCount > maxWordsPerChunk && currentChunk.isNotEmpty()) {
                result.add(currentChunk.toString().trim())
                currentChunk = StringBuilder()
                currentWordCount = 0
            }

            if (currentChunk.isNotEmpty()) {
                currentChunk.append("\n\n")
            }
            currentChunk.append(paragraph)
            currentWordCount += wordCount
        }

        if (currentChunk.isNotEmpty()) {
            result.add(currentChunk.toString().trim())
        }

        return result
    }
}
