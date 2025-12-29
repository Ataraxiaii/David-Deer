package com.daviddeer.daviddeer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import com.daviddeer.daviddeer.R
import com.daviddeer.daviddeer.util.StepDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class StatsFragment : Fragment() {
    private lateinit var dbHelper: StepDbHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = StepDbHelper(requireContext())

        val calendarView = view.findViewById<CalendarView>(R.id.statsCalendarView)
        val selectedStepsTv = view.findViewById<TextView>(R.id.selectedDateStepsTv)

        // 1. Set listener for date selection on the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Note: month index is 0-based (January is 0), so we add 1 for formatting
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            val steps = dbHelper.getSteps(formattedDate)

            selectedStepsTv.text = "$formattedDate: $steps Steps"
        }

        // 2. Display today's data by default upon initialization
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        selectedStepsTv.text = "Today: ${dbHelper.getSteps(today)} Steps"

        // 3. Load the historical step data for the last 7 days into the list below
        loadRecentHistory(view)
    }

    /**
     * Dynamically populates the history container with records from the past week
     */
    private fun loadRecentHistory(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.historyContainer)
        // Clear existing views to prevent duplication during fragment recreation
        container.removeAllViews()

        // Retrieve the most recent 7 days of data from the database
        val historyList = dbHelper.getRecentHistory(7)
        val inflater = LayoutInflater.from(requireContext())

        for (record in historyList) {
            // Inflate individual history item layouts
            val itemView = inflater.inflate(R.layout.history_item, container, false)

            val dateTv = itemView.findViewById<TextView>(R.id.dateTextView)
            val stepsTv = itemView.findViewById<TextView>(R.id.stepsTextView)

            // Map data to the view components
            dateTv.text = record.first
            stepsTv.text = "${record.second} steps"

            // Add the populated item view to the vertical scroll container
            container.addView(itemView)
        }
    }
}