package com.rafael.dietaapp.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object FechaUtils {
    private val formatoBD = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** La fecha de hoy en formato "yyyy-MM-dd", usada como clave en la base de datos. */
    fun hoy(): String = LocalDate.now().format(formatoBD)

    fun aTexto(fecha: LocalDate): String = fecha.format(formatoBD)

    fun deTexto(fecha: String): LocalDate = LocalDate.parse(fecha, formatoBD)

    /** Texto legible tipo "viernes, 20 de junio de 2026". */
    fun formatoLegible(fecha: String): String {
        val date = deTexto(fecha)
        val diaSemana = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        val mes = date.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        return "$diaSemana, ${date.dayOfMonth} de $mes de ${date.year}"
    }

    /** Texto corto tipo "20 jun" para mostrar en listas. */
    fun formatoCorto(fecha: String): String {
        val date = deTexto(fecha)
        val mes = date.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
        return "${date.dayOfMonth} $mes"
    }

    fun esHoy(fecha: String): Boolean = fecha == hoy()

    fun diaAnterior(fecha: String): String = aTexto(deTexto(fecha).minusDays(1))

    fun diaSiguiente(fecha: String): String = aTexto(deTexto(fecha).plusDays(1))

    /** No se puede avanzar a días futuros respecto a hoy. */
    fun puedeAvanzar(fecha: String): Boolean = deTexto(fecha).isBefore(LocalDate.now())
}
