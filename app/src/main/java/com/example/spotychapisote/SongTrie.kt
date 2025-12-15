package com.example.spotychapisote

class SongTrie {

    private class Node {
        var currentCharacter: Char = '\u0000'
        var isWord: Boolean = false
        // Usamos un HashMap para soportar CUALQUIER carácter
        val children: MutableMap<Char, Node> = mutableMapOf()
        val songIds: MutableSet<String> = linkedSetOf()
    }

    private var trie: Node = Node()

    fun init() {
        trie = Node()
    }

    fun insertWord(word: String, songId: String) {
        var currentNode = trie
        for (i in word.indices) {
            val char = word[i].lowercaseChar()
            if (!currentNode.children.containsKey(char)) {
                currentNode.children[char] = Node()
            }

            currentNode = currentNode.children[char]!!
            currentNode.currentCharacter = char
            currentNode.songIds.add(songId)
        }
        currentNode.isWord = true
    }

    fun searchWord(word: String): Boolean {
        var currentNode = trie
        for (i in word.indices) {
            val char = word[i].lowercaseChar()

            val next = currentNode.children[char] ?: return false
            currentNode = next
        }
        return currentNode.isWord
    }

    fun searchPrefix(prefix: String): List<String> {
        var currentNode = trie
        for (i in prefix.indices) {
            val char = prefix[i].lowercaseChar()

            val next = currentNode.children[char] ?: return emptyList()
            currentNode = next
        }
        return currentNode.songIds.toList()
    }
}
