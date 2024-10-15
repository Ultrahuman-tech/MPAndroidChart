package com.github.mikephil.charting.data

import java.util.Arrays
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow


/**
 * This is a Segment Tree implementation over a list of [Entry].
 * Points to consider
 * - the list [arr] should be sorted by [Entry.x]
 * - [queryMinInRange] & [queryMaxInRange] may use [Arrays.binarySearch] so its limitations also apply
 */
class SegmentTree(private val arr: List<Entry>) {

    private val treeForMin: Array<Entry?>
    private val lazyForMin: FloatArray
    private val treeForMax: Array<Entry?>
    private val lazyForMax: FloatArray
    private val entryXToIndexMap = mutableMapOf<Float, Int>()
    private val listOfEntryX = FloatArray(arr.size)

    init {
        val n = arr.size
        //Height of segment tree
        val x = ceil(ln(n.toDouble()) / ln(2.0)).toInt()
        //Maximum size of segment tree
        val treeSize = 2 * (2.0).pow(x.toDouble()).toInt() - 1

        arr.forEachIndexed { index, entry -> entryXToIndexMap[entry.x] = index }
        arr.map { it.x }.let {
            listOfEntryX.indices.forEach { index -> listOfEntryX[index] = it[index] }
        }

        treeForMin = arrayOfNulls(4 * n) // Allocate space for the tree
        lazyForMin = FloatArray(4 * n) // Allocate space for lazy propagation
        if (n > 0) {
            buildTreeForMin(1, 0, n - 1)
        }
        treeForMax = arrayOfNulls(4 * n)
        lazyForMax = FloatArray(4 * n)
        if (n > 0) {
            buildTreeForMax(1, 0, n - 1)
        }
    }

    private fun buildTreeForMin(node: Int, start: Int, end: Int) {
        if (start == end) {
            treeForMin[node] = arr[start]
        } else {
            val mid = getMid(start, end)
            buildTreeForMin(2 * node, start, mid)
            buildTreeForMin(2 * node + 1, mid + 1, end)
            treeForMin[node] = minEntry(treeForMin[2 * node], treeForMin[2 * node + 1])
        }
    }

    private fun minEntry(entry1: Entry?, entry2: Entry?): Entry? {
        if (entry1 == null) return entry2
        if (entry2 == null) return entry1
        return if (entry1.y < entry2.y) entry1 else entry2
    }

    private fun buildTreeForMax(node: Int, start: Int, end: Int) {
        if (start == end) {
            treeForMax[node] = arr[start]
        } else {
            val mid = getMid(start, end)
            buildTreeForMax(2 * node, start, mid)
            buildTreeForMax(2 * node + 1, mid + 1, end)
            treeForMax[node] = maxEntry(treeForMax[2 * node], treeForMax[2 * node + 1])
        }
    }

    private fun maxEntry(entry1: Entry?, entry2: Entry?): Entry? {
        if (entry1 == null) return entry2
        if (entry2 == null) return entry1
        return if (entry1.y > entry2.y) entry1 else entry2
    }

    fun queryMinInRange(fromEntryX: Float, toEntryX: Float): Entry? {
        return this.queryMinInRange(
            node = 1,
            start = 0,
            end = arr.size - 1,
            left = entryXToIndexMap[fromEntryX] ?: entryXToIndexMap[usingBinarySearch(fromEntryX)] ?: 0,
            right = entryXToIndexMap[toEntryX] ?: entryXToIndexMap[usingBinarySearch(toEntryX)] ?: (arr.size - 1),
        )
    }

    private fun queryMinInRange(node: Int, start: Int, end: Int, left: Int, right: Int): Entry? {
        if (lazyForMin[node] != 0f) {
            treeForMin[node]?.let {
                treeForMin[node] = Entry(it.x, it.y + lazyForMin[node])
            }
            if (start != end) {
                lazyForMin[2 * node] += lazyForMin[node]
                lazyForMin[2 * node + 1] += lazyForMin[node]
            }
            lazyForMin[node] = 0f
        }

        if (start > right || end < left) {
            return null
        }

        if(start >= left && end <= right) {
            return treeForMin[node]
        }

        val mid = getMid(start, end)
        val leftChild = queryMinInRange(node = 2 * node, start = start, end = mid, left = left, right = right)
        val rightChild = queryMinInRange(node = 2 * node + 1, start = mid + 1, end = end, left = left, right = right)
        return minEntry(leftChild, rightChild)
    }

    fun queryMaxInRange(fromEntryX: Float, toEntryX: Float): Entry? {
        return queryForMax(
            node = 1,
            start = 0,
            end = arr.size - 1,
            left = entryXToIndexMap[fromEntryX] ?: entryXToIndexMap[usingBinarySearch(fromEntryX)] ?: 0,
            right = entryXToIndexMap[toEntryX] ?: entryXToIndexMap[usingBinarySearch(toEntryX)] ?: (arr.size - 1),
        )
    }

    private fun queryForMax(node: Int, start: Int, end: Int, left: Int, right: Int): Entry? {
        if (lazyForMax[node] != 0f) {
            treeForMax[node]?.let {
                treeForMax[node] = Entry(it.x, it.y + lazyForMax[node])
            }
            if (start != end) {
                lazyForMax[2 * node] += lazyForMax[node]
                lazyForMax[2 * node + 1] += lazyForMax[node]
            }
            lazyForMax[node] = 0f
        }

        if (start > right || end < left) {
            return null // Change here
        }

        if (start >= left && end <= right) {
            return treeForMax[node]
        }

        val mid = getMid(start, end)
        val leftChild = queryForMax(2 * node, start, mid, left, right)
        val rightChild = queryForMax(2 * node + 1, mid + 1, end, left, right)
        return maxEntry(leftChild, rightChild) // Change here
    }

    /**
     * Not supported. Not tested. Don't use.
     */
    fun update(left: Float, right: Float, value: Float) {
        updateForMin(1, 0, arr.size - 1, left, right, value)
        updateForMax(1, 0, arr.size - 1, left, right, value)
    }

    private fun updateForMin(node: Int, start: Int, end: Int, left: Float, right: Float, value: Float) {
        if (lazyForMin[node] != 0f) {
            treeForMin[node]?.let {
                treeForMin[node] = Entry(it.x, it.y + lazyForMin[node])
            }
            if (start != end) {
                lazyForMin[2 * node] += lazyForMin[node]
                lazyForMin[2 * node + 1] += lazyForMin[node]
            }
            lazyForMin[node] = 0f
        }

        if (start > right || end < left) {
            return
        }

        if (start >= left && end <= right) {
            treeForMin[node]?.let {
                treeForMin[node] = Entry(it.x, it.y + value)
            }
            if (start != end) {
                lazyForMin[2 * node] += value
                lazyForMin[2 * node + 1] += value
            }
            return
        }

        val mid = getMid(start, end)
        updateForMin(2 * node, start, mid, left, right, value)
        updateForMin(2 * node + 1, mid + 1, end, left, right, value)
        treeForMin[node] = minEntry(treeForMin[2 * node], treeForMin[2 * node + 1])
    }

    private fun updateForMax(node: Int, start: Int, end: Int, left: Float, right: Float, value: Float) {
        if (lazyForMax[node] != 0f) {
            treeForMax[node]?.let {
                treeForMax[node] = Entry(it.x, it.y + lazyForMax[node])
            }
            if (start != end) {
                lazyForMax[2 * node] += lazyForMax[node]
                lazyForMax[2 * node + 1] += lazyForMax[node]
            }
            lazyForMax[node] = 0f
        }

        if (start > right || end < left) {
            return
        }

        if (start >= left && end <= right) {
            treeForMax[node]?.let {
                treeForMax[node] = Entry(it.x, it.y + value)
            }
            if (start != end) {
                lazyForMax[2 * node] += value
                lazyForMax[2 * node + 1] += value
            }
            return
        }

        val mid = getMid(start, end)
        updateForMax(2 * node, start, mid, left, right, value)
        updateForMax(2 * node + 1, mid + 1, end, left, right, value)
        treeForMax[node] = maxEntry(treeForMax[2 * node], treeForMax[2 * node + 1])
    }

    private fun getMid(start: Int, end: Int): Int {
        return start + (end - start) / 2
    }

    private fun usingBinarySearch(value: Float): Float {
        if (value <= listOfEntryX[0]) {
            return listOfEntryX[0]
        }
        if (value >= listOfEntryX[listOfEntryX.size - 1]) {
            return listOfEntryX[listOfEntryX.size - 1]
        }

        val result = Arrays.binarySearch(listOfEntryX, value)
        if (result >= 0) {
            return listOfEntryX[result]
        }

        val insertionPoint = -result - 1
        return if (insertionPoint == 0) {
            listOfEntryX[insertionPoint]
        } else if (insertionPoint == listOfEntryX.size) {
            listOfEntryX[insertionPoint - 1]
        } else {
            if ((listOfEntryX[insertionPoint] - value) < (value - listOfEntryX[insertionPoint - 1]))
                listOfEntryX[insertionPoint]
            else
                listOfEntryX[insertionPoint - 1]
        }
    }
}
