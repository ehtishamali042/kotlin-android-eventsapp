package com.example.eventspoc

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var createEventBtn: Button
    private lateinit var adapter: EventsListingAdapter
    private lateinit var spinner: Spinner
    private lateinit var calenderButton: LinearLayout
    private lateinit var calenderText: TextView
    private lateinit var overlayView: View
    private var calendar = Calendar.getInstance()

    var spinnerSelectedItem = "All"
    val eventTypeItems = listOf("All", "Event", "OOO", "Task")
    var dateSelected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createEventBtn = findViewById(R.id.createEventBtn)
        calenderButton = findViewById(R.id.calenderButton)
        calenderText = findViewById(R.id.calenderText)
        spinner = findViewById<Spinner>(R.id.maSpinnerEventType)
        overlayView = findViewById<View>(R.id.overlayView)

        // Spinner configuration
        val spinner_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_adapter.addAll(eventTypeItems)
        spinner.adapter = spinner_adapter

        // action listeners
        createEventBtn.setOnClickListener {
            val intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
        }
        calenderButton.setOnClickListener {
            showDatePickerDialog()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerSelectedItem = parent?.getItemAtPosition(position).toString()
                updateEventsListOnActivity()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun showDatePickerDialog() {
        if (dateSelected.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            calendar.time = dateFormat.parse(dateSelected)
        } else calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Handle the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the selected date and set it to the button text
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                dateSelected = formattedDate
                calenderText.text = formattedDate
                updateEventsListOnActivity()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Show the DatePickerDialog
        datePicker.show()
    }

    private fun editEvent(position: Int) {
        val clickedEvent = EventsDS.events[position]
        val intent = Intent(this, CreateEvent::class.java)
        intent.putExtra("isEditMode", true)
        intent.putExtra("eventId", clickedEvent.eventId)
        startActivity(intent)
    }

    private fun deleteEvent(eventId: String) {
        EventsDS.events.removeIf { it.eventId == eventId }
        updateEventsListOnActivity()
    }

    // Function to filter and update the RecyclerView
    private fun updateEventsListOnActivity() {
        var filteredEvents: ArrayList<EventInfo> =
            if (spinnerSelectedItem === "All") EventsDS.events
            else EventsDS.events.filter { it.type == spinnerSelectedItem } as ArrayList<EventInfo>
        var eventsArray =
            if (dateSelected.isNotEmpty()) filteredEvents.filter { it.startDate == dateSelected }
            else filteredEvents
        adapter.updateData(eventsArray)
    }

    override fun onResume() {
        super.onResume()
        val recyclerView: RecyclerView = findViewById(R.id.events_listing)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter =
            EventsListingAdapter(this, EventsDS.events, object : OnEventsListingItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click here
                    val clickedEvent = EventsDS.events[position]
                    System.out.println(adapter.getCurrentData())
                    System.out.println(EventsDS.events)
                    Toast.makeText(applicationContext, clickedEvent.title, Toast.LENGTH_SHORT)
                        .show()
                    showCustomDialog(clickedEvent)
                }
            },
                { position -> editEvent(position) }, // Replace with your edit logic
                { position -> deleteEvent(position) } // Replace with your delete logic
            )
        recyclerView.adapter = adapter
        updateEventsListOnActivity()
    }

    private fun showCustomDialog(event: EventInfo) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.event_detail_modal, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        overlayView.visibility = View.VISIBLE
        var dialogTitle = dialogView.findViewById<TextView>(R.id.dialogEventTitle)
        var dialogDesc = dialogView.findViewById<TextView>(R.id.dialogDesc)
        var dialogEventType = dialogView.findViewById<Chip>(R.id.dialogEventChip)
        var dialogDate = dialogView.findViewById<Chip>(R.id.dialogDate)
        dialogTitle.text = event.title
        dialogDesc.text = event.description
        dialogEventType.text = event.type
        dialogDate.text = event.startDate
        // Handle button click inside the dialog
        dialogView.findViewById<ImageButton>(R.id.dialogueImageButton).setOnClickListener {
            alertDialog.dismiss()
            overlayView.visibility = View.GONE
        }
    }
}