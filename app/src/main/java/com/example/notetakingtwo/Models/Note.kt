package com.example.notetakingtwo.Models
import kotlinx.serialization.*

@Serializable
data class Note( var description: String, var title: String, val _id: String = "", var createdAt: String = "", var updatedAt: String = "")
