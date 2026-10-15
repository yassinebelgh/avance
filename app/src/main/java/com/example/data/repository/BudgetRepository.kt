package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.AppDatabase
import com.example.data.model.BudgetConfig
import com.example.data.model.PlannedAction
import com.example.data.model.PrevisionNote
import com.example.data.model.RealAccount
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: AppDao) {

    val plannedActions: Flow<List<PlannedAction>> = dao.getAllPlannedActions()
    val realAccounts: Flow<List<RealAccount>> = dao.getAllRealAccounts()
    val previsionNotes: Flow<List<PrevisionNote>> = dao.getAllPrevisionNotes()
    val budgetConfig: Flow<BudgetConfig?> = dao.getBudgetConfig()

    suspend fun insertPlannedAction(action: PlannedAction) = dao.insertPlannedAction(action)
    suspend fun updatePlannedAction(action: PlannedAction) = dao.updatePlannedAction(action)
    suspend fun deletePlannedAction(action: PlannedAction) = dao.deletePlannedAction(action)
    suspend fun deleteGroupByGroupName(groupName: String) = dao.deleteGroupByGroupName(groupName)
    suspend fun renameGroup(oldName: String, newName: String) = dao.renameGroup(oldName, newName)

    suspend fun insertRealAccount(account: RealAccount) = dao.insertRealAccount(account)
    suspend fun updateRealAccount(account: RealAccount) = dao.updateRealAccount(account)
    suspend fun deleteRealAccount(account: RealAccount) = dao.deleteRealAccount(account)

    suspend fun insertPrevisionNote(note: PrevisionNote) = dao.insertPrevisionNote(note)
    suspend fun updatePrevisionNote(note: PrevisionNote) = dao.updatePrevisionNote(note)
    suspend fun deletePrevisionNote(note: PrevisionNote) = dao.deletePrevisionNote(note)

    suspend fun updateIncome(amount: Double) {
        dao.setBudgetConfig(
            BudgetConfig(
                id = 1,
                monthlyIncome = amount,
                currencySymbol = "€",
                title = "Budget Mensuel"
            )
        )
    }

    suspend fun updateConfig(config: BudgetConfig) = dao.setBudgetConfig(config)

    suspend fun resetToDefaultSpreadsheet() {
        dao.clearPlannedActions()
        dao.clearRealAccounts()
        dao.clearPrevisionNotes()
        AppDatabase.populateInitialData(dao)
    }
}
