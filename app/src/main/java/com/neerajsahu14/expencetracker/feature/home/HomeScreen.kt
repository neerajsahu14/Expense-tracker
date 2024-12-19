package com.neerajsahu14.expencetracker.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neerajsahu14.expencetracker.R
import com.neerajsahu14.expencetracker.base.HomeNavigationEvent
import com.neerajsahu14.expencetracker.base.NavigationEvent
import com.neerajsahu14.expencetracker.data.model.ExpenseEntity
import com.neerajsahu14.expencetracker.feature.home.UserNameDialog
import com.neerajsahu14.expencetracker.util.PreferencesHelper
import com.neerajsahu14.expencetracker.util.Utils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf(PreferencesHelper.getUserName(context)) }
    var showDialog by remember { mutableStateOf(userName == null) }
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    if (showDialog) {
        UserNameDialog(
            onConfirm = { name ->
                PreferencesHelper.setUserName(context, name)
                userName = name
                showDialog = false
            }
        )
    } else {
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    NavigationEvent.NavigateBack -> navController.popBackStack()
                    HomeNavigationEvent.NavigateToSeeAll -> navController.navigate("/all_transactions")
                    HomeNavigationEvent.NavigateToAddIncome -> navController.navigate("/add_income")
                    HomeNavigationEvent.NavigateToAddExpense -> navController.navigate("/add_exp")
                    else -> {}
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = userName ?: "User",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle notification */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            val state = viewModel.expenses.collectAsState(initial = emptyList())
            val expense = viewModel.getTotalExpense(state.value)
            val income = viewModel.getTotalIncome(state.value)
            val balance = viewModel.getBalance(state.value)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column {
                    CardItem(
                        balance = balance,
                        income = income,
                        expense = expense,
                        modifier = Modifier.padding(16.dp)
                    )
                    TransactionList(
                        list = state.value,
                        onSeeAllClicked = { viewModel.onEvent(HomeUiEvent.OnSeeAllClicked) },
                        modifier = Modifier.weight(1f)
                    )
                }
                MultiFloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onAddExpenseClicked = { viewModel.onEvent(HomeUiEvent.OnAddExpenseClicked) },
                    onAddIncomeClicked = { viewModel.onEvent(HomeUiEvent.OnAddIncomeClicked) }
                )
            }
        }
    }
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    balance: String,
    income: String,
    expense: String
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = balance,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = { /* Handle menu */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardRowItem(
                    title = "Income",
                    amount = income,
                    icon = R.drawable.ic_income
                )
                CardRowItem(
                    title = "Expense",
                    amount = expense,
                    icon = R.drawable.ic_expense
                )
            }
        }
    }
}

@Composable
fun CardRowItem(
    title: String,
    amount: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    list: List<ExpenseEntity>,
    title: String = "Recent Transactions",
    onSeeAllClicked: () -> Unit
) {
    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
            trailingContent = {
                if (title == "Recent Transactions") {
                    TextButton(onClick = onSeeAllClicked) {
                        Text("See all")
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = list,
                key = { item -> item.id ?: 0 }
            ) { item ->
                val icon = Utils.getItemIcon(item)
                val amount = if (item.type == "Income") item.amount else item.amount * -1

                TransactionItem(
                    title = item.title,
                    amount = Utils.formatCurrency(amount),
                    icon = icon,
                    date = Utils.formatStringDateToMonthDayYear(item.date),
                    color = if (item.type == "Income") Green else Color.Red,
                    modifier = Modifier,
                )
                if (list.last() != item) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    title: String,
    amount: String,
    icon: Int,
    date: String,
    color: Color,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(51.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = date, fontSize = 13.sp, color = Color.LightGray)
            }
        }
        Text(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color
        )
    }
}

@Composable
fun MultiFloatingActionButton(
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(visible = expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        onAddIncomeClicked()
                        expanded = false
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_income),
                        contentDescription = "Add Income"
                    )
                }
                SmallFloatingActionButton(
                    onClick = {
                        onAddExpenseClicked()
                        expanded = false
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expense),
                        contentDescription = "Add Expense"
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Transaction"
            )
        }
    }
}


@Composable
fun UserNameDialog(onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Enter Your Name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text("OK")
            }
        }
    )
}