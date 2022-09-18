package com.example.notetakingtwo

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.Retrofit.NoteRestApiService

import com.example.notetakingtwo.databinding.ActivityMainBinding
import com.example.notetakingtwo.repositories.NotesRepository
import com.example.notetakingtwo.viewmodels.LoginViewModel
import com.example.notetakingtwo.viewmodels.LoginViewModelFactory
import com.example.notetakingtwo.viewmodels.NotesViewModel
import com.example.notetakingtwo.viewmodels.NotesViewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



import kotlinx.serialization.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var notesAdapter: NotesAdapter? = null
    private val ACTIVITY_CODE: Int = 1
    private lateinit var notesInfo: MutableList<Note>
    private val noteRepository: NotesRepository = NotesRepository(NoteRestApiService())
    private lateinit var viewModel: NotesViewModel
    private var spinnerValue: Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Toast.makeText(applicationContext, "The server may be down :(", Toast.LENGTH_SHORT).show()
        }


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)





        setContentView(binding.root)

        registerForContextMenu(binding.layoutTwo)


            viewModel = ViewModelProviders.of(this, NotesViewModelFactory(application)).get(
                NotesViewModel::class.java
            )

            Log.d("IHFIOSOFHSFS", "HIHIHIHII")

            //add the stuff
        lifecycleScope.launch {
            delay(50L)
            notesAdapter =
                NotesAdapter(this@MainActivity, R.layout.list_item, viewModel.notesListController)
            binding.layoutTwo.adapter = notesAdapter




            binding.apply {

                createNote.setOnClickListener {
                    viewModel.createNote(description.text.toString(), titleName.text.toString())
                    description.text.clear()
                    titleName.text.clear()

                }


            }


            val spinner: Spinner = findViewById(R.id.sort_spinner)
            val spinnerAdapter = ArrayAdapter.createFromResource(
                this@MainActivity,
                R.array.sort_array,
                android.R.layout.simple_spinner_item
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            if(savedInstanceState != null) {
                spinner.setSelection(savedInstanceState.getInt("spinnerValue", 0))
                print(savedInstanceState.getInt("spinnerValue", 0))
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    spinnerValue = p2
                    viewModel.changeSpinnerValueState(spinnerAdapter.getItem(spinnerValue).toString())
                    println(spinnerAdapter.getItem(spinnerValue).toString())
                    println(
                        notesAdapter
                    )
                    viewModel.sortNote()
                    /* val savedNotesInfo: MutableList<Note> =  notesAdapter?.objects?.toMutableList()!!
                Log.d("T", savedNotesInfo.toString())
                notesAdapter?.clear()
                if(adapter.getItem(p2).toString() == "Last Edited") {
                    println("FJSIOFJIOFIWEOFJIOJFIOJFIOWEJFWIOFJIOFJWEIOFWJEIOWJIOFWJIOJIOEJIEO")
                    notesAdapter?.addAll(savedNotesInfo.sortedBy { Instant.parse(it.updatedAt).epochSecond}.reversed())


                } else if (adapter.getItem(p2).toString() == "Last Added"){
                    notesAdapter?.addAll(savedNotesInfo.sortedBy { Instant.parse(it.createdAt).epochSecond}.reversed())
                }
                else if(adapter.getItem(p2).toString() == "Alphabetically") {
                    notesAdapter?.addAll(savedNotesInfo.sortedBy { it.title })
                }
                Log.d("T", notesAdapter!!.objects.toString())
                adapter.notifyDataSetChanged()*/

                }
            }
            observeSortedLastChange()
            observeNotesDataSet()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                finishAffinity()
                (application as NoteTakingApplication).loggedUser = null
                startActivity(Intent(this@MainActivity, Login::class.java))
                true
            }
           else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context, menu)
        println("hihiihihi")


    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.delete -> {
                val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
                viewModel.deleteNote((binding.layoutTwo.adapter.getItem(info.position) as Note))
                true
            }
            R.id.update -> {

                val intent: Intent = Intent(this@MainActivity, Update::class.java)
                intent.putExtra("note", Json.encodeToString((binding.layoutTwo.adapter.getItem((item.menuInfo as AdapterView.AdapterContextMenuInfo).position) as Note)))
                intent.putExtra("position", (item.menuInfo as AdapterView.AdapterContextMenuInfo).position)
                startActivityForResult(intent, ACTIVITY_CODE)

                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode == ACTIVITY_CODE) and (resultCode == Activity.RESULT_OK)) {
           val note: Note =  Json.decodeFromString<Note>(data?.getStringExtra("note")?:"")

            data?.getIntExtra("position", -1)?.let { viewModel.updateNote(note, it)
               }

            //notesAdapter.set(1, "ff")

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("spinnerValue", spinnerValue)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        spinnerValue = savedInstanceState.getInt("spinnerValue")
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun observeNotesDataSet() {
        viewModel.allNotesLiveData.observe(this@MainActivity) {
            println("OBSERVED")
            notesAdapter!!.notifyDataSetChanged()
        }
    }

    fun observeSortedLastChange() {
        viewModel.spinnerValuesLiveData.observe(this@MainActivity) {
            println("CHANGED")
        }
    }

}