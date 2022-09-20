package com.example.notetakingtwo


import android.app.Activity
import android.content.Intent
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.databinding.ActivityMainBinding
import com.example.notetakingtwo.viewmodels.NotesViewModel
import com.example.notetakingtwo.viewmodels.NotesViewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private var notesAdapter: NotesAdapter? = null
    private val ACTIVITY_CODE: Int = 1
    private lateinit var viewModel: NotesViewModel
    private var spinnerValue: Int = -1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val handler = CoroutineExceptionHandler { _, _ ->
            Toast.makeText(applicationContext, "The server may be down :(", Toast.LENGTH_SHORT).show()
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerForContextMenu(binding.layoutTwo)

        lifecycleScope.launch {
            viewModel = ViewModelProviders.of(this@MainActivity, NotesViewModelFactory(application)).get(
                NotesViewModel::class.java
            )

            if(!viewModel.isNotesListControllerInit()) {
                println("CONTROLLER LATEINIT INITALISED")
                delay(100L)
            }
            println("LIFECYCLE SCOPE CREATION")
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
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    println(spinnerValue)
                    println(p2)
                    if (spinnerValue != p2) {
                        spinnerValue = p2
                        viewModel.changeSpinnerValueState(
                            spinnerAdapter.getItem(spinnerValue).toString()
                        )
                    }

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

                val intent = Intent(this@MainActivity, Update::class.java)
                intent.putExtra("note", Json.encodeToString((binding.layoutTwo.adapter.getItem((item.menuInfo as AdapterView.AdapterContextMenuInfo).position) as Note)))
                intent.putExtra("position", (item.menuInfo as AdapterView.AdapterContextMenuInfo).position)
                startActivityForResult(intent, ACTIVITY_CODE)

                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode == ACTIVITY_CODE) and (resultCode == Activity.RESULT_OK)) {
           val note: Note =  Json.decodeFromString(data?.getStringExtra("note")?:"")

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

    private fun observeNotesDataSet() {
        viewModel.allNotesLiveData.observe(this@MainActivity) {
            println("OBSERVED")
            notesAdapter!!.notifyDataSetChanged()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeSortedLastChange() {
        viewModel.spinnerValuesLiveData.observe(this@MainActivity) {
            viewModel.sortNote()
            println("CHANGED")
        }
    }

}