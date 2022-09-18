package com.example.notetakingtwo

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.notetakingtwo.Models.Note
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotesAdapter(
    context: Context,
    resource: Int,

     var objects: MutableList<Note> = mutableListOf()
) : ArrayAdapter<Note>(context, resource, objects) {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var p: Note = getItem(position) as Note
        val v: View = mInflater.inflate(R.layout.list_item, parent, false )
        val title = v.findViewById<TextView>(R.id.title)
        val description = v.findViewById<TextView>(R.id.description)
        val createdAt = v.findViewById<TextView>(R.id.created_at)
        val updatedAt =  v.findViewById<TextView>(R.id.updated_at)
        val metadata =  v.findViewById<TextView>(R.id.metadata)


        createdAt.text = "Created At: ${p.createdAt.split("T")[0]}"

        //metadata.text = "${Instant.parse(p.createdAt).epochSecond}|${Instant.parse(p.updatedAt).epochSecond}"

        updatedAt.text = "Updated At ${p.updatedAt.split("T")[0]}"
        title.text = "Title: " + p.title
        description.text ="Description: " + p.description


        return v
    }

    fun updatePosition(content: Note, position: Int) {
        objects[position] = content
        notifyDataSetChanged()
    }

    fun getList() {
        println(objects)
    }




} //for classes private var (initalising it yoruself is different than base implementation variable. However, for arrays it doesn't matter as they hold the same pointer to the array
