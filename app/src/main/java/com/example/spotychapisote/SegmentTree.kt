package com.example.spotychapisote

import kotlin.math.max
data class Node(
    var max: Int = 0
)


class SegmentTree(private val a: IntArray) {

    private val n = a.size
    private val tree = Array(4 * n) { Node() }

    private fun max(l: Node, r: Node): Node {
        return Node(
            max = max(l.max, r.max),
        )
    }

    fun init(inicio: Int, fin: Int, nodo: Int) {
        if (inicio == fin) {
            tree[nodo].max = a[inicio]
        } else {
            val mid = (inicio + fin) / 2
            val izq = 2 * nodo + 1
            val der = 2 * nodo + 2

            init(inicio, mid, izq)
            init(mid + 1, fin, der)

            tree[nodo] = max(tree[izq], tree[der])
        }
    }

    fun query(
        inicio: Int,
        fin: Int,
        nodo: Int,
        l: Int,
        r: Int
    ): Node {
        if (inicio >= l && fin <= r) {
            return tree[nodo]
        }

        val mid = (inicio + fin) / 2
        val izq = 2 * nodo + 1
        val der = 2 * nodo + 2

        return when {
            r <= mid -> query(inicio, mid, izq, l, r)
            l > mid -> query(mid + 1, fin, der, l, r)
            else -> {
                val q1 = query(inicio, mid, izq, l, r)
                val q2 = query(mid + 1, fin, der, l, r)
                max(q1, q2)
            }
        }
    }

    fun update(
        inicio: Int,
        fin: Int,
        nodo: Int,
        pos: Int,
        valor: Int
    ) {
        if (pos < inicio || pos > fin) return

        if (inicio == fin) {
            a[pos] = valor
            tree[nodo].max = valor
        } else {
            val mid = (inicio + fin) / 2
            val izq = 2 * nodo + 1
            val der = 2 * nodo + 2

            update(inicio, mid, izq, pos, valor)
            update(mid + 1, fin, der, pos, valor)

            tree[nodo] = max(tree[izq], tree[der])
        }
    }
}
