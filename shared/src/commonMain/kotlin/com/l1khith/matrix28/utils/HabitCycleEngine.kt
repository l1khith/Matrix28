package com.l1khith.matrix28.utils

data class HabitCyclePosition(
    val cycleIndex: Long,
    val dayInCycle: Int
) {
    init {
        require(dayInCycle in 1..28) {
            "dayInCycle must be between 1 and 28 inclusive, was $dayInCycle"
        }
    }
}

object HabitCycleEngine {

    const val DAYS_IN_CYCLE = 28

    /**
     * Dynamically computes cycleIndex and dayInCycle (1..28) based on epochDay and an optional anchor epochDay.
     */
    fun computePosition(epochDay: Long, anchorEpochDay: Long = 0L): HabitCyclePosition {
        val delta = epochDay - anchorEpochDay
        val cycleIndex: Long
        val dayInCycle: Int

        if (delta >= 0) {
            cycleIndex = delta / DAYS_IN_CYCLE
            dayInCycle = ((delta % DAYS_IN_CYCLE) + 1).toInt()
        } else {
            val adjustedDelta = delta + 1
            cycleIndex = (adjustedDelta / DAYS_IN_CYCLE) - 1
            val rem = delta % DAYS_IN_CYCLE
            dayInCycle = (if (rem == 0L) 1L else rem + DAYS_IN_CYCLE + 1).toInt()
        }

        return HabitCyclePosition(cycleIndex, dayInCycle)
    }

    fun getCycleIndex(epochDay: Long, anchorEpochDay: Long = 0L): Long {
        return computePosition(epochDay, anchorEpochDay).cycleIndex
    }

    fun getDayInCycle(epochDay: Long, anchorEpochDay: Long = 0L): Int {
        return computePosition(epochDay, anchorEpochDay).dayInCycle
    }

    /**
     * Converts a cycleIndex and dayInCycle (1..28) back to the target epochDay.
     */
    fun getEpochDay(cycleIndex: Long, dayInCycle: Int, anchorEpochDay: Long = 0L): Long {
        require(dayInCycle in 1..28) { "dayInCycle must be in 1..28" }
        return anchorEpochDay + (cycleIndex * DAYS_IN_CYCLE) + (dayInCycle - 1)
    }
}
