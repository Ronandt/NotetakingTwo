package com.example.notetakingtwo

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Retrofit.NoteRestApiService

import com.example.notetakingtwo.databinding.ActivityUpdateBinding
import com.example.notetakingtwo.repositories.NotesRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Update : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private val notesRepository: NotesRepository = NotesRepository(NoteRestApiService())
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val api =  NoteRestApiService()
        binding.apply {





                intent.getStringExtra("note")?.let { noteString ->
                    Json.decodeFromString<Note>(noteString).let { noteObject ->
                   titleName.setText(noteObject.title)
                   description.setText(noteObject.description)

                    updateNote.setOnClickListener {
                        val note = Note(description.text.toString() ,titleName.text.toString(),noteObject._id, noteObject.createdAt, ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ))


                        val returnIntent: Intent = Intent()
                        returnIntent.putExtra("note", Json.encodeToString(note))
                        returnIntent.putExtra("position", intent.getIntExtra("position", -1))
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()




                    }

                }

                } ?: run {
                    Toast.makeText(applicationContext, "No such note to update!", Toast.LENGTH_SHORT).show()

        }
    }




}}