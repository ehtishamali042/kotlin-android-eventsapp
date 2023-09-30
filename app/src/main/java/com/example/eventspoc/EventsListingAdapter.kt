package com.example.eventspoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnEventsListingItemClickListener {
    fun onItemClick(position: Int)
}

class EventsListingAdapter(
    private val context: MainActivity,
    private var items: List<EventInfo>,
    private val clickListener: OnEventsListingItemClickListener,
    private val editClickListener: (position: Int) -> Unit,
    private val deleteClickListener: (eventId: String) -> Unit
) : RecyclerView.Adapter<EventsListingAdapter.ViewHolder>() {

    val emptyText: TextView = context.findViewById(R.id.emptyText)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textView: TextView = itemView.findViewById(R.id.rvTitle)
        val descView: TextView = itemView.findViewById(R.id.rvDescription)
        val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delIcon)

        init {
            itemView.setOnClickListener(this)
            editIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    editClickListener(position)
                }
            }

            deleteIcon.setOnClickListener {
                val position = adapterPosition
                val event = EventsDS.events[position]
                if (position != RecyclerView.NO_POSITION) {
                    deleteClickListener(event.eventId)
                }
            }
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.events_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.title
        holder.descView.text = item.description
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(filteredEvents: List<EventInfo>) {
        items = filteredEvents
        if (items.isEmpty()) {
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
        }
        notifyDataSetChanged()
    }

    fun getCurrentData(): List<EventInfo> {
        return items
    }
}
