package com.example.opsc7311_part2_groupa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Define dummy data outside of the adapter class
val dummyEntriesList = listOf(
    TimeSheetEntry("09:00 AM", "Work", "Project A", "2024-04-28"),
    TimeSheetEntry("10:30 AM", "Meeting", "Team meeting", "2024-04-28"),
    TimeSheetEntry("01:00 PM", "Lunch", "Lunch break", "2024-04-28")
    // Add more dummy entries as needed
)

class EntryAdapter(private val entries: List<TimeSheetEntry>) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timesheet_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = if (entries.isEmpty()) dummyEntriesList[position] else entries[position]
        holder.bind(entry)
    }

    override fun getItemCount(): Int {
        return if (entries.isEmpty()) dummyEntriesList.size else entries.size
    }

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeTextView: TextView = itemView.findViewById(R.id.textStartTime)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.textEndTime)
        private val categoryTextView: TextView = itemView.findViewById(R.id.textCategory)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)

        fun bind(entry: TimeSheetEntry) {
            startTimeTextView.text = entry.startTime
            endTimeTextView.text = entry.endTime
            categoryTextView.text = entry.category
            descriptionTextView.text = entry.description
        }
    }


}




