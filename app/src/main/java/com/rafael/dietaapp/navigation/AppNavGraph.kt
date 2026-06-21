package com.rafael.dietaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.screens.*
import com.rafael.dietaapp.util.FechaUtils

@Composable
fun AppNavGraph(repository: DietaRepository) {
    val navController = rememberNavController()
    val fechaInicial = FechaUtils.hoy()

    NavHost(navController = navController, startDestination = Rutas.dia(fechaInicial)) {

        composable(
            route = Rutas.DIA,
            arguments = listOf(navArgument("fecha") { type = NavType.StringType })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: fechaInicial
            DiaScreen(
                fecha = fecha,
                repository = repository,
                onIrAComida = { comidaId -> navController.navigate(Rutas.comidaDetalle(comidaId)) },
                onIrAAlimentos = { navController.navigate(Rutas.ALIMENTOS) },
                onIrARecetas = { navController.navigate(Rutas.RECETAS) },
                onIrAExtra = { extraId -> navController.navigate(Rutas.extraForm(fecha, extraId)) },
                onCambiarFecha = { nuevaFecha ->
                    navController.navigate(Rutas.dia(nuevaFecha)) {
                        popUpTo(Rutas.DIA) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.ALIMENTOS) {
            AlimentosScreen(
                repository = repository,
                onVolver = { navController.popBackStack() },
                onEditarAlimento = { id -> navController.navigate(Rutas.alimentoForm(id)) },
                onNuevoAlimento = { navController.navigate(Rutas.alimentoForm()) }
            )
        }

        composable(
            route = Rutas.ALIMENTO_FORM,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val idStr = backStackEntry.arguments?.getString("id")
            AlimentoFormScreen(
                alimentoId = idStr?.toLongOrNull(),
                repository = repository,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.RECETAS) {
            RecetasScreen(
                repository = repository,
                onVolver = { navController.popBackStack() },
                onAbrirReceta = { id -> navController.navigate(Rutas.recetaDetalle(id)) },
                onNuevaReceta = { navController.navigate(Rutas.recetaForm()) }
            )
        }

        composable(
            route = Rutas.RECETA_DETALLE,
            arguments = listOf(navArgument("recetaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recetaId = backStackEntry.arguments?.getLong("recetaId") ?: 0L
            RecetaDetalleScreen(
                recetaId = recetaId,
                repository = repository,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.RECETA_FORM) {
            RecetaFormScreen(
                repository = repository,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.COMIDA_DETALLE,
            arguments = listOf(navArgument("comidaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val comidaId = backStackEntry.arguments?.getLong("comidaId") ?: 0L
            ComidaDetalleScreen(
                comidaId = comidaId,
                repository = repository,
                onVolver = { navController.popBackStack() },
                onGuardarComoReceta = { id -> navController.navigate(Rutas.guardarReceta(id)) }
            )
        }

        composable(
            route = Rutas.EXTRA_FORM,
            arguments = listOf(
                navArgument("fecha") { type = NavType.StringType },
                navArgument("extraId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: fechaInicial
            val extraIdStr = backStackEntry.arguments?.getString("extraId")
            ExtraFormScreen(
                fecha = fecha,
                extraId = extraIdStr?.toLongOrNull(),
                repository = repository,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.GUARDAR_RECETA,
            arguments = listOf(navArgument("comidaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val comidaId = backStackEntry.arguments?.getLong("comidaId") ?: 0L
            GuardarRecetaScreen(
                comidaId = comidaId,
                repository = repository,
                onVolver = { navController.popBackStack() },
                onRecetaGuardada = { navController.popBackStack() }
            )
        }
    }
}
