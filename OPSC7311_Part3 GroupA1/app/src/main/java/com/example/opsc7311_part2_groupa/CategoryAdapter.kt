package com.example.opsc7311_part2_groupa

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CategoryAdapter(context: Context, private val dataSource: List<String>) : ArrayAdapter<String>(context, R.layout.category_list_item, dataSource) {

    private class ViewHolder {
        lateinit var displayTextView: TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val rowView: View

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = inflater.inflate(R.layout.category_list_item, parent, false)

            viewHolder = ViewHolder()
            viewHolder.displayTextView = rowView.findViewById(R.id.display_text)

            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        val displayText = getItem(position)
        viewHolder.displayTextView.text = displayText

        return rowView
    }
}
