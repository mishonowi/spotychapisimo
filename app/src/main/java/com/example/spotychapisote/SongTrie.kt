package com.example.spotychapisote

class SongTrie {

    private class Node {
        var currentCharacter: Char = '\u0000'
        var isWord: Boolean = false
        val children: Array<Node?> = Array(27) { null }
        val songIds: MutableSet<String> = linkedSetOf()

    }

    private var trie: Node = Node()

    fun init() {
        trie = Node()
    }

    private fun charToIndex(c: Char): Int {
        val ch = c.lowercaseChar()
        return if (ch in 'a'..'z') (ch - 'a') else -1
    }

    fun insertWord(word: String, songId: String) {
        var currentNode = trie
        for (i in word.indices) {
            val characterIndex = charToIndex(word[i])
            if (characterIndex == -1) continue

            if (currentNode.children[characterIndex] == null) {
                currentNode.children[characterIndex] = Node()
            }
            currentNode = currentNode.children[characterIndex]!!
            currentNode.currentCharacter = word[i]
            currentNode.songIds.add(songId)
        }
        currentNode.isWord = true
    }

    fun searchWord(word: String): Boolean {
        var currentNode = trie
        for (i in word.indices) {
            val characterIndex = charToIndex(word[i])
            if (characterIndex == -1) continue

            if (currentNode.children[characterIndex] == null) return false
            currentNode = currentNode.children[characterIndex]!!
        }
        return currentNode.isWord
    }

    fun searchPrefix(prefix: String): List<String> {
        var currentNode = trie
        for (i in prefix.indices) {
            val characterIndex = charToIndex(prefix[i])
            if (characterIndex == -1) continue

            val next = currentNode.children[characterIndex] ?: return emptyList()
            currentNode = next
        }
        return currentNode.songIds.toList()

    }
}
