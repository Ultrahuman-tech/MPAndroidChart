package com.github.mikephil.charting.test.data

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.SegmentTree
import org.junit.jupiter.api.Assertions.*

/**
 * Empty array: Test with an empty list of entries.
 * Single element: Test with a list containing only one entry.
 * Querying the entire range: Query for the minimum in the entire range of x values.
 * Querying a single element: Query for the minimum with left and right being the same x value. Range Queries
 * Overlapping ranges: Perform multiple queries with overlapping ranges of x values.
 * Non-overlapping ranges: Perform queries with non-overlapping ranges.
 * Out-of-bounds ranges: Test with query ranges that are completely outside the range of x values in the entries.
 * Partially out-of-bounds ranges: Test with query ranges that partially overlap with the range of x values. Updates
 * Updating a single element: Update the y value of a single entry.
 * Updating a range of elements: Update the y values of a range of entries.
 * Overlapping updates: Perform multiple updates with overlapping ranges.
 * Updates followed by queries: Perform updates and then query for minimum values within different ranges. Edge Cases
 * Duplicate x values: Test with entries having the same x value but different y values.
 * Negative x and y values: Include entries with negative values.
 * Large input size: Test with a large number of entries to assess performance. Example Test Cases in Kotlin
 */
class SegmentTreeTest {

    @org.junit.jupiter.api.Test
    fun queryMinInRange() {
        val entries = listOf(
            Entry(1f, 5f),
            Entry(2f, 3f),
            Entry(3f, 8f),
            Entry(4f, 1f),
            Entry(5f, 6f)
        )
        val segmentTree = SegmentTree(entries)
        // Test case 1: Query entire range
        val q1 = segmentTree.queryMinInRange(fromEntryX = 1f, toEntryX = 5f)
        assertEquals(true, Entry(4f, 1f).equalTo(q1))

        // Test case 2: Query single element
        val q2 = segmentTree.queryMinInRange(fromEntryX = 3f, toEntryX = 3f)
        assertNotNull(q2)
        println(q2)
        assertEquals(true, Entry(3f, 8f).equalTo(q2))

        // Test case 3: Overlapping ranges
        val q3 = segmentTree.queryMinInRange(fromEntryX = 1f, toEntryX = 3f)
        assertNotNull(q3)
        println(q2)
        assertEquals(true, Entry(2f, 3f).equalTo(q3))
        val q4 = segmentTree.queryMinInRange(fromEntryX = 3f, toEntryX = 5f)
        assertNotNull(q4)
        assertEquals(true, Entry(4f, 1f).equalTo(q4))

        // Test case 4: Update and query
        segmentTree.update(2f, 4f, -2f)
        assertEquals(Entry(4f, -1f).equalTo(segmentTree.queryMinInRange(fromEntryX = 1f, toEntryX = 5f)), true)
    }

    @org.junit.jupiter.api.Test
    fun queryMaxInRange() {
    }

    @org.junit.jupiter.api.Test
    fun update() {
    }
}
