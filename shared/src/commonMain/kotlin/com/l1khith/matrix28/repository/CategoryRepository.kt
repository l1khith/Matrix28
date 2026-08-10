package com.l1khith.matrix28.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CategoryRepository {
    val defaultCategories = listOf("Personal", "Work", "Health", "Finance", "Social", "Education")

    private val _customCategories = MutableStateFlow<List<String>>(emptyList())
    val customCategories: StateFlow<List<String>> = _customCategories.asStateFlow()

    fun getAllCategories(): List<String> {
        return defaultCategories + _customCategories.value
    }

    fun addCustomCategory(name: String): Pair<Boolean, String?> {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return Pair(false, "Category name cannot be empty")
        }

        val allCategories = getAllCategories()
        if (allCategories.any { it.equals(trimmed, ignoreCase = true) }) {
            return Pair(false, "Category '$trimmed' already exists")
        }

        _customCategories.value = _customCategories.value + trimmed
        return Pair(true, null)
    }

    fun removeCustomCategory(name: String) {
        _customCategories.value = _customCategories.value.filter { !it.equals(name, ignoreCase = true) }
    }
}
