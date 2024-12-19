package com.neerajsahu14.expencetracker.feature.statsscreen


import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.neerajsahu14.expencetracker.base.BaseViewModel
import com.neerajsahu14.expencetracker.base.UiEvent
import com.neerajsahu14.expencetracker.data.dao.ExpenseDao
import com.neerajsahu14.expencetracker.data.model.ExpenseSummary
import com.neerajsahu14.expencetracker.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {
    val entries = dao.getAllExpenseByDate()
    val topEntries = dao.getTopExpenses()
    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    override fun onEvent(event: UiEvent) {
    }
}
