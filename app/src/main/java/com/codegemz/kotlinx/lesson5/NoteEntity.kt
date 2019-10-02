package com.codegemz.kotlinx.lesson5

import kotlinx.serialization.Serializable

@Serializable
data class NoteEntity(var text: String, var isStarred: Boolean = false)
