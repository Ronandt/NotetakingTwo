package com.example.notetakingtwo.viewmodels

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.NoteTakingApplication
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.repositories.NotesRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NotesViewModel(application: Application): AndroidViewModel(application) {

    private val notesRepository: NotesRepository = NotesRepository(NoteRestApiService())
    lateinit var notesListController: MutableList<Note>
    private val _allNotesLiveData: MutableLiveData<MutableList<Note>> = MutableLiveData()

    private val _spinnerValuesLiveData: MutableLiveData<String> = MutableLiveData()
    val spinnerValuesLiveData: MutableLiveData<String> = _spinnerValuesLiveData

    init {
        viewModelScope.launch {
            println("VIEWMODEL INIT")
            notesListController = (getApplication() as NoteTakingApplication).loggedUser?.let {
                notesRepository.getNotes(
                    it._id
                ).toMutableList()
            }!!
            _allNotesLiveData.value = notesListController
        }
    }
    val allNotesLiveData = _allNotesLiveData




    @RequiresApi(Build.VERSION_CODES.O)
    fun createNote(description: String, title: String) {
        viewModelScope.launch {
            (getApplication() as NoteTakingApplication).loggedUser?._id?.let {
                Note(description, title,
                    it, createdAt= ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ), updatedAt= ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT )
                )
            }?.let { notesRepository.addNote(it)
                _allNotesLiveData.value?.set(0, it)
                _allNotesLiveData.value = _allNotesLiveData.value
                Toast.makeText(getApplication(), "Note has been added!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun updateNote(note: Note, it: Int) {
        viewModelScope.launch {

            _allNotesLiveData.value?.set(it, note)
            _allNotesLiveData.value = _allNotesLiveData.value
        }
        viewModelScope.launch {
            notesRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            _allNotesLiveData.value?.remove(note)
            _allNotesLiveData.value = _allNotesLiveData.value
            Toast.makeText(getApplication(),"Note has been removed!", Toast.LENGTH_SHORT).show()
        }
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun sortNote() {
        val savedNotesInfo: MutableList<Note> =  _allNotesLiveData.value?.toMutableList()!!
        _allNotesLiveData.value?.clear()
        when(_spinnerValuesLiveData.value) {
            "Last Edited" -> {
                savedNotesInfo.sortedBy { Instant.parse(it.updatedAt).epochSecond }.reversed().toMutableList()
                    .let { _allNotesLiveData.value!!.addAll(it) }
            }
            "Last Added" -> {
                savedNotesInfo.sortedBy { Instant.parse(it.createdAt).epochSecond }.reversed().toMutableList()
                    .let { _allNotesLiveData.value!!.addAll(it) }

            }
            "Alphabetically" -> {
                savedNotesInfo.sortedBy { it.title }.toMutableList()
                    .let { _allNotesLiveData.value!!.addAll(it) }
            }

        }
        _allNotesLiveData.value = _allNotesLiveData.value
    }

    fun changeSpinnerValueState(spinnerState: String) {
        if(spinnerState != _spinnerValuesLiveData.value) {
            _spinnerValuesLiveData.value = spinnerState
        }

    }

    fun isNotesListControllerInit(): Boolean {
        return this::notesListController.isInitialized
    }







}

class NotesViewModelFactory(private var application: Application): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(application) as T
    }
}