package com.example.notetakingtwo

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.Retrofit.NoteRestApiService

import com.example.notetakingtwo.databinding.ActivityMainBinding
import com.example.notetakingtwo.repositories.NotesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch



import kotlinx.serialization.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    private var notesAdapter: NotesAdapter? = null
    private val ACTIVITY_CODE: Int = 1
    private lateinit var notesInfo: MutableList<Note>
    private val noteRepository: NotesRepository = NotesRepository(NoteRestApiService())
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Toast.makeText(applicationContext, "The server may be down :(", Toast.LENGTH_SHORT).show()
        }


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)





        setContentView(binding.root)

        registerForContextMenu(binding.layoutTwo)


        Log.d("IHFIOSOFHSFS", "HIHIHIHII")
        lifecycleScope.launch(handler){

                val result = (application as NoteTakingApplication).loggedUser?.let {
                 println("IIJIJOOPPJOFPJOPFJFOPEJOPFWJFOPJFWOPJ")
                    noteRepository.getNotes(
                        it._id)

                }
                if (result != null) {
                    notesInfo = result.toMutableList()

                    println(notesInfo)



                    notesAdapter = NotesAdapter(this@MainActivity, R.layout.list_item, notesInfo)
                }
                binding.layoutTwo.adapter =notesAdapter


        }
        binding.apply {

                createNote.setOnClickListener {
                    val note = (application as NoteTakingApplication).loggedUser?.let { it1 -> Note( description.text.toString(), titleName.text.toString(), it1._id, createdAt= ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ), updatedAt=ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT )) }
                    println(note)
                    if (note != null) {
                        lifecycleScope.launch {
                            noteRepository.addNote(note)
                        }
                    }
                    description.text.clear()
                    titleName.text.clear()

                    notesAdapter?.let {
                        notesAdapter!!.insert(note, 0)



                        notesAdapter!!.notifyDataSetChanged()
                        Toast.makeText(applicationContext, "Note has been added!", Toast.LENGTH_SHORT).show()
                    } ?: let {

                        Toast.makeText(applicationContext, "We have some trouble connecting to the server!", Toast.LENGTH_SHORT).show()
                    }

                }




        }


        val spinner: Spinner = findViewById(R.id.sort_spinner)
        val adapter = ArrayAdapter.createFromResource(this@MainActivity, R.array.sort_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {


                println(adapter.getItem(p2).toString())
                val savedNotesInfo: MutableList<Note> =  notesAdapter?.objects?.toMutableList()!!
                notesAdapter?.clear()
                if(adapter.getItem(p2).toString() == "Last Edited") {
                    println("FJSIOFJIOFIWEOFJIOJFIOJFIOWEJFWIOFJIOFJWEIOFWJEIOWJIOFWJIOJIOEJIEO")
                    notesAdapter?.addAll(savedNotesInfo)


                } else if (adapter.getItem(p2).toString() == "Last Added"){
                    notesAdapter?.addAll(savedNotesInfo)
                }
                else if(adapter.getItem(p2).toString() == "Alphabetically") {
                    notesAdapter?.addAll(savedNotesInfo.sortedBy { it.title }.toMutableList())
                }
                adapter.notifyDataSetChanged()

            }
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
                             lifecycleScope.launch {
                    noteRepository.deleteNote((binding.layoutTwo.adapter.getItem(info.position) as Note))
                }

                lifecycleScope.launch {

                    notesAdapter?.remove(binding.layoutTwo.adapter.getItem(info.position) as Note)

                    notesAdapter?.notifyDataSetChanged()

                    Toast.makeText(applicationContext,"Note has been removed!", Toast.LENGTH_SHORT).show()

                }

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

            data?.getIntExtra("position", -1)?.let { notesAdapter?.updatePosition(note, it)
               }

            //notesAdapter.set(1, "ff")

        }
    }

}