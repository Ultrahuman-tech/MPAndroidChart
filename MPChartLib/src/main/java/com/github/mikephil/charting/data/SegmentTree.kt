package com.github.mikephil.charting.data

class SegmentTree(private val arr: List<Entry>) {

    private val treeForMin: Array<Entry?>
    private val lazyForMin: FloatArray
    private val treeForMax: Array<Entry?>
    private val lazyForMax: FloatArray

    init {
        val n = arr.size
        treeForMin = arrayOfNulls(4 * n) // Allocate space for the tree
        lazyForMin = FloatArray(4 * n) // Allocate space for lazy propagation
        buildTreeForMin(1, 0, n - 1)
        treeForMax = arrayOfNulls(4 * n)
        lazyForMax = FloatArray(4 * n)
        buildTreeForMax(1, 0, n - 1)
    }

    private fun buildTreeForMin(node: Int, start: Int, end: Int) {
        if (start == end) {
            treeForMin[node] = arr[start]
        } else {
            val mid = (start + end) / 2
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
            val mid = (start + end) / 2
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
        return queryMinInRange(1, 0, arr.size - 1, fromEntryX, toEntryX)
    }

    private fun queryMinInRange(node: Int, start: Int, end: Int, fromEntryX: Float, toEntryX: Float): Entry? {
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

        if (start > toEntryX || end < fromEntryX) {
            return null
        }

        if(start >= fromEntryX && end <= toEntryX) {
            return treeForMin[node]
        }

        val mid = (start + end) / 2
        val leftChild = queryMinInRange(2 * node, start, mid, fromEntryX, toEntryX)
        val rightChild = queryMinInRange(2 * node + 1, mid + 1, end, fromEntryX, toEntryX)
        return minEntry(leftChild, rightChild)
    }

    fun queryMaxInRange(fromEntryX: Float, toEntryX: Float): Entry? {
        return queryForMax(1, 0, arr.size - 1, fromEntryX, toEntryX)
    }

    private fun queryForMax(node: Int, start: Int, end: Int, fromEntryX: Float, toEntryX: Float): Entry? {
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

        if (start > toEntryX || end < fromEntryX) {
            return null // Change here
        }

        if (start >= fromEntryX && end <= toEntryX) {
            return treeForMax[node]
        }

        val mid = (start + end) / 2
        val leftChild = queryForMax(2 * node, start, mid, fromEntryX, toEntryX)
        val rightChild = queryForMax(2 * node + 1, mid + 1, end, fromEntryX, toEntryX)
        return maxEntry(leftChild, rightChild) // Change here
    }

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

        val mid = (start + end) / 2
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

        val mid = (start + end) / 2
        updateForMax(2 * node, start, mid, left, right, value)
        updateForMax(2 * node + 1, mid + 1, end, left, right, value)
        treeForMax[node] = maxEntry(treeForMax[2 * node], treeForMax[2 * node + 1]) // Change here
    }
}
