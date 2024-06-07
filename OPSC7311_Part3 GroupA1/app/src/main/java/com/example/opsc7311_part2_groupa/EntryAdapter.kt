package com.example.opsc7311_part2_groupa

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class EntryAdapter(private val context: Context, private val entries: List<TimeSheetEntry>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = entries.size

    override fun getItem(position: Int): Any = entries[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.entry_list_item, parent, false)
        val entry = entries[position]

        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)

        timeTextView.text = entry.startTime
        categoryTextView.text = entry.category
        descriptionTextView.text = entry.description
        dateTextView.text = entry.date

        return view
    }
}
