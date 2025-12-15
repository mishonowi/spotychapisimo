package com.example.spotychapisote

class SongTrie {

    private class Node {
        var currentCharacter: Char = '\u0000'
        var isWord: Boolean = false
        // 26 letras + 10 dígitos + 1 para espacios/otros = 37 posiciones
        val children: Array<Node?> = Array(37) { null }
        val songIds: MutableSet<String> = linkedSetOf()
    }

    private var trie: Node = Node()

    fun init() {
        trie = Node()
    }

    private fun charToIndex(c: Char): Int {
        return when {
            c.lowercaseChar() in 'a'..'z' -> c.lowercaseChar() - 'a'  // 0-25
            c in '0'..'9' -> 26 + (c - '0')  // 26-35
            else -> 36  // espacios u otros caracteres
        }
    }

    fun insertWord(word: String, songId: String) {
        var currentNode = trie
        for (i in word.indices) {
            val characterIndex = charToIndex(word[i])

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

            if (currentNode.children[characterIndex] == null) return false
            currentNode = currentNode.children[characterIndex]!!
        }
        return currentNode.isWord
    }

    fun searchPrefix(prefix: String): List<String> {
        var currentNode = trie
        for (i in prefix.indices) {
            val characterIndex = charToIndex(prefix[i])

            val next = currentNode.children[characterIndex] ?: return emptyList()
            currentNode = next
        }
        return currentNode.songIds.toList()
    }
}