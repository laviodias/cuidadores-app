package com.example.cuidadores.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cuidadores.data.dao.ClienteDao
import com.example.cuidadores.data.dao.MedicamentoDao
import com.example.cuidadores.data.dao.AplicacaoReceitaDao
import com.example.cuidadores.data.dao.RegistroAplicacaoDao
import com.example.cuidadores.data.dao.HorarioAtendimentoDao
import com.example.cuidadores.data.dao.UsuarioDao
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.data.model.AplicacaoReceita
import com.example.cuidadores.data.model.RegistroAplicacao
import com.example.cuidadores.data.model.HorarioAtendimento
import com.example.cuidadores.data.model.Usuario
import com.example.cuidadores.data.converter.DateConverter

@Database(
    entities = [
        Cliente::class,
        Medicamento::class,
        AplicacaoReceita::class,
        RegistroAplicacao::class,
        HorarioAtendimento::class,
        Usuario::class
    ], 
    version = 5,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun aplicacaoReceitaDao(): AplicacaoReceitaDao
    abstract fun registroAplicacaoDao(): RegistroAplicacaoDao
    abstract fun horarioAtendimentoDao(): HorarioAtendimentoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuidadores_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 