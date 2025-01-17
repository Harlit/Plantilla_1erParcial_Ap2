package com.ucne.proj_1erparcial_ap2.ui.theme.Prestamos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.proj_1erparcial_ap2.data.local.Entity.PrestamosEntity
import com.ucne.proj_1erparcial_ap2.data.repository.RepositorioPrestamos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PrestamoUiState(
    val PrestamoLista: List<PrestamosEntity> = emptyList()
)

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val Repositorioprestamos: RepositorioPrestamos
) : ViewModel() {
    var hasDeudorError: Boolean by mutableStateOf(false)
    var hasConceptoError: Boolean by mutableStateOf(false)
    var hasMontoError: Boolean by mutableStateOf(false)
    var deudor by mutableStateOf("")
    var concepto by mutableStateOf("")
    var monto by mutableStateOf("")
    var uiState = MutableStateFlow(PrestamoUiState())
        private set

    init {
        GetLista()
    }
    fun GetLista() {
        viewModelScope.launch(Dispatchers.IO) {
            Repositorioprestamos.getList().collect { lista ->
                uiState.update {
                    it.copy(PrestamoLista = lista)
                }
            }
        }
    }

    fun validateField(value: String?, fieldName: String): Boolean {
        if (value.isNullOrEmpty()) {
            throw IllegalArgumentException("El campo $fieldName es requerido")
        } else {
            return false
        }
    }

    fun validateInputs() {
        hasDeudorError = validateField(deudor, "Deudor")
        hasConceptoError = validateField(concepto, "Concepto")
        hasMontoError = validateField(monto, "Monto")
    }

    fun insertar() {
        try {
            validateInputs()
            val prestamos = PrestamosEntity(
                deudor = deudor!!,
                concepto = concepto!!,
                monto = monto!!.toDoubleOrNull() ?: 0.0
            )
            viewModelScope.launch(Dispatchers.IO) {
                Repositorioprestamos.insert(prestamos)
                Limpiar()
            }
        } catch (ex: IllegalArgumentException) {
            hasDeudorError = true
            hasConceptoError = true
            hasMontoError = true
        }
    }

    private fun Limpiar(){
        deudor = ""
        concepto = ""
        monto = ""
    }

}