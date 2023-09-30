package com.example.eventspoc

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateEvent : AppCompatActivity() {
    private lateinit var submitBtn: Button
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var calenderButton: LinearLayout
    private lateinit var calenderText: TextView
    private var isEditMode: Boolean = false
    private lateinit var selectedDate: Calendar
    val eventTypeItems = listOf("Event", "OOO", "Task")
    var spinnerSelectedItem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val eventId = intent.getStringExtra("eventId")


        var actionBar = getSupportActionBar()
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = if (eventId != null) "Edit Event" else "Create Event"
        }

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        submitBtn = findViewById(R.id.submitBtn)
        calenderButton = findViewById(R.id.calenderButton)
        calenderText = findViewById(R.id.calenderText)
        val spinner = findViewById<Spinner>(R.id.spinnerEventType)

        selectedDate = Calendar.getInstance()

        // Create an empty ArrayAdapter
        val spinner_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Populate the adapter with dynamically generated items
        spinner_adapter.addAll(eventTypeItems)
        spinner.adapter = spinner_adapter

        if (eventId != null) {
            isEditMode = true
            val filteredEvent = EventsDS.events.find { it.eventId == eventId }
            if (filteredEvent !== null) {
                etTitle.setText(filteredEvent.title)
                etDescription.setText(filteredEvent.description)
                calenderText.text = filteredEvent.startDate
                spinnerSelectedItem = filteredEvent.type
                spinner.setSelection(eventTypeItems.indexOf(filteredEvent.type))
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                selectedDate.time = dateFormat.parse(filteredEvent.startDate)
            }
        }

        // action listeners
        calenderButton.setOnClickListener {
            showDatePickerDialog()
        }
        submitBtn.setOnClickListener {
            var event: EventInfo = EventInfo(
                eventId ?: etTitle.text.toString(),
                etTitle.text.toString(),
                etDescription.text.toString(),
                androidx.appcompat.R.drawable.abc_cab_background_internal_bg,
                calenderText.text.toString(),
                spinnerSelectedItem
            )
            if (eventId !== null) {
                val indexToReplace = EventsDS.events.indexOfFirst { it.eventId == eventId }
                EventsDS.events[indexToReplace] = event
            } else EventsDS.events.add(event)
            finish()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerSelectedItem = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button click here
                finish() // Close the current activity
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                calenderText.text = formattedDate
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }
}