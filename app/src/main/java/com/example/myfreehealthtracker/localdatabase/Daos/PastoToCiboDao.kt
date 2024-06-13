package com.example.myfreehealthtracker.localdatabase.Daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import com.example.myfreehealthtracker.localdatabase.Entities.PastoToCibo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PastoToCiboDao {


    @Upsert
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo)

    @Query("SELECT * FROM PastoToCibo ")
    fun allPastoToCibo(): Flow<List<PastoToCibo>>


    @Query("SELECT * FROM Alimento WHERE Alimento.id IN (SELECT idAlimentoId FROM PastoToCibo WHERE idUserID =:userId AND idDate=:date)")
    fun getAlimentiByPasto(userId: String, date: Date): Flow<List<Alimento>>

    @Query("SELECT quantity FROM PastoToCibo WHERE idUserID =:userId AND idDate=:date AND idAlimentoId =:idAlimento")
    fun getQuantitaByPasto(userId: String, date: Date, idAlimento: String): Flow<Float>

    @Query("SELECT idAlimentoId, SUM(quantity) as Qsum FROM PastoToCibo GROUP BY idAlimentoId")
    fun getAllQuantities(): Flow<List<AlimentoQuantity>>

    class AlimentoQuantity {
        @ColumnInfo(name = "idAlimentoId")
        var id: String = ""

        @ColumnInfo(name = "Qsum")
        var quantity: Float = 0f
    }


}