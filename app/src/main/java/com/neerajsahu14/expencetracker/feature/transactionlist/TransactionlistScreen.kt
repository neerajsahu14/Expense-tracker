package com.neerajsahu14.expencetracker.feature.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neerajsahu14.expencetracker.feature.add_expense.ExpenseDropDown
import com.neerajsahu14.expencetracker.feature.home.HomeViewModel
import com.neerajsahu14.expencetracker.feature.home.TransactionItem
import com.neerajsahu14.expencetracker.util.Utils
import com.neerajsahu14.expencetracker.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state = viewModel.expenses.collectAsState(initial = emptyList())
    var filterType by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("All Time") }
    var menuExpanded by remember { mutableStateOf(false) }

    val filteredTransactions = when (filterType) {
        "Expense" -> state.value.filter { it.type == "Expense" }
        "Income" -> state.value.filter { it.type == "Income" }
        else -> state.value
    }

    val filteredByDateRange = filteredTransactions.filter { transaction ->
        // Apply date range filter logic here
        true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.rotate(90f),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit = slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            ExpenseDropDown(
                                listOfItems = listOf("All", "Expense", "Income"),
                                onItemSelected = { selected ->
                                    filterType = selected
                                    menuExpanded = false
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExpenseDropDown(
                                listOfItems = listOf("Yesterday", "Today", "Last 30 Days", "Last 90 Days", "Last Year"),
                                onItemSelected = { selected ->
                                    dateRange = selected
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
                items(filteredByDateRange) { item ->
                    val icon = Utils.getItemIcon(item)
                    TransactionItem(
                        title = item.title,
                        amount = item.amount.toString(),
                        icon = icon!!,
                        date = item.date,
                        color = if (item.type == "Income") Color.Green else Color.Red,
                        Modifier.animateItemPlacement(tween(100))
                    )
                }
            }
        }
    }
}