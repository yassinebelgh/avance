package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ResetDataDialog
import com.example.ui.screens.ComptesEtPrevisionsScreen
import com.example.ui.screens.MonthlyBudgetScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

sealed class NavItem(val title: String, val icon: ImageVector, val tag: String) {
    object Incomes : NavItem("Incomes & Budget", Icons.Default.TableChart, "tab_incomes")
    object ComptesPrevisions : NavItem("Comptes & À prévoir", Icons.Default.AccountBalanceWallet, "tab_comptes_prev")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val uiState by budgetViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        NavItem.Incomes,
        NavItem.ComptesPrevisions
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Avance Budget",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("app_reset_data_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Réinitialiser modèle",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = {
                            Text(
                                item.title,
                                maxLines = 1,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            // Layout 1 (Écran Principal) : Premier tableau des Incomes & dépenses prévues
            0 -> MonthlyBudgetScreen(
                uiState = uiState,
                onAddAction = { name, amount, group, isSavings ->
                    budgetViewModel.addOrUpdatePlannedAction(0, name, amount, group, isSavings)
                    scope.launch { snackbarHostState.showSnackbar("« $name » ajouté dans « $group »") }
                },
                onUpdateAction = { id, name, amount, group, isSavings ->
                    budgetViewModel.addOrUpdatePlannedAction(id, name, amount, group, isSavings)
                    scope.launch { snackbarHostState.showSnackbar("« $name » mis à jour") }
                },
                onDeleteAction = { action ->
                    budgetViewModel.deletePlannedAction(action)
                    scope.launch { snackbarHostState.showSnackbar("« ${action.name} » supprimé") }
                },
                onUpdateIncome = { newIncome ->
                    budgetViewModel.updateMonthlyIncome(newIncome)
                    scope.launch { snackbarHostState.showSnackbar("Revenu mensuel mis à jour") }
                },
                onDeleteGroup = { groupName ->
                    budgetViewModel.deleteGroup(groupName)
                    scope.launch { snackbarHostState.showSnackbar("Catégorie « $groupName » supprimée") }
                },
                onRenameGroup = { oldName, newName ->
                    budgetViewModel.renameGroup(oldName, newName)
                    scope.launch { snackbarHostState.showSnackbar("Catégorie renommée en « $newName »") }
                },
                modifier = Modifier.padding(innerPadding)
            )

            // Layout 2 : Solde Total Réel & Remarques et Prévisions
            1 -> ComptesEtPrevisionsScreen(
                uiState = uiState,
                onAddAccount = { name, balance, colorTag ->
                    budgetViewModel.addOrUpdateRealAccount(0, name, balance, colorTag)
                    scope.launch { snackbarHostState.showSnackbar("Compte « $name » ajouté") }
                },
                onUpdateAccount = { id, name, balance, colorTag ->
                    budgetViewModel.addOrUpdateRealAccount(id, name, balance, colorTag)
                    scope.launch { snackbarHostState.showSnackbar("Compte « $name » mis à jour") }
                },
                onDeleteAccount = { account ->
                    budgetViewModel.deleteRealAccount(account)
                    scope.launch { snackbarHostState.showSnackbar("Compte « ${account.name} » supprimé") }
                },
                onAddNote = { amount, desc ->
                    budgetViewModel.addOrUpdatePrevisionNote(0, amount, desc)
                    scope.launch { snackbarHostState.showSnackbar("Prévision « $desc » ajoutée") }
                },
                onUpdateNote = { id, amount, desc ->
                    budgetViewModel.addOrUpdatePrevisionNote(id, amount, desc)
                    scope.launch { snackbarHostState.showSnackbar("Prévision mise à jour") }
                },
                onToggleSettled = { note ->
                    budgetViewModel.togglePrevisionSettled(note)
                },
                onDeleteNote = { note ->
                    budgetViewModel.deletePrevisionNote(note)
                    scope.launch { snackbarHostState.showSnackbar("Prévision supprimée") }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showResetDialog) {
        ResetDataDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                budgetViewModel.resetToSpreadsheetDefault()
                showResetDialog = false
                scope.launch { snackbarHostState.showSnackbar("Données réinitialisées avec succès") }
            }
        )
    }
}
