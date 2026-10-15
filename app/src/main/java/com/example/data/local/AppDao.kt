package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BudgetConfig
import com.example.data.model.PlannedAction
import com.example.data.model.PrevisionNote
import com.example.data.model.RealAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Planned Actions
    @Query("SELECT * FROM planned_actions ORDER BY groupName ASC, sortOrder ASC, id ASC")
    fun getAllPlannedActions(): Flow<List<PlannedAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedAction(action: PlannedAction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlannedActions(actions: List<PlannedAction>)

    @Update
    suspend fun updatePlannedAction(action: PlannedAction)

    @Delete
    suspend fun deletePlannedAction(action: PlannedAction)

    @Query("DELETE FROM planned_actions WHERE groupName = :groupName")
    suspend fun deleteGroupByGroupName(groupName: String)

    @Query("UPDATE planned_actions SET groupName = :newName WHERE groupName = :oldName")
    suspend fun renameGroup(oldName: String, newName: String)

    @Query("DELETE FROM planned_actions")
    suspend fun clearPlannedActions()

    // Real Accounts
    @Query("SELECT * FROM real_accounts ORDER BY sortOrder ASC, id ASC")
    fun getAllRealAccounts(): Flow<List<RealAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRealAccount(account: RealAccount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRealAccounts(accounts: List<RealAccount>)

    @Update
    suspend fun updateRealAccount(account: RealAccount)

    @Delete
    suspend fun deleteRealAccount(account: RealAccount)

    @Query("DELETE FROM real_accounts")
    suspend fun clearRealAccounts()

    // Prevision Notes
    @Query("SELECT * FROM prevision_notes ORDER BY timestamp DESC, id DESC")
    fun getAllPrevisionNotes(): Flow<List<PrevisionNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrevisionNote(note: PrevisionNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPrevisionNotes(notes: List<PrevisionNote>)

    @Update
    suspend fun updatePrevisionNote(note: PrevisionNote)

    @Delete
    suspend fun deletePrevisionNote(note: PrevisionNote)

    @Query("DELETE FROM prevision_notes")
    suspend fun clearPrevisionNotes()

    // Budget Config
    @Query("SELECT * FROM budget_config WHERE id = 1 LIMIT 1")
    fun getBudgetConfig(): Flow<BudgetConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBudgetConfig(config: BudgetConfig)
}
