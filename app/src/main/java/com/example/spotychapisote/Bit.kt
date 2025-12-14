package com.example.spotychapisote

class Bit(private val size: Int) {

    private val bit = IntArray(size + 1)

    // update(pos, valor)
    fun update(pos: Int, value: Int) {
        var i = pos
        while (i <= size) {
            bit[i] += value
            i += i and -i
        }
    }

    // query(pos) -> suma [1..pos]
    fun query(pos: Int): Int {
        var i = pos
        var sum = 0
        while (i > 0) {
            sum += bit[i]
            i -= i and -i
        }
        return sum
    }

    // query(l, r)
    fun rangeQuery(l: Int, r: Int): Int {
        return query(r) - query(l - 1)
    }

    // valor exacto en una posición
    fun valueAt(pos: Int): Int {
        return rangeQuery(pos, pos)
    }

    fun reset() {
        for (i in bit.indices) {
            bit[i] = 0
        }
    }
}
