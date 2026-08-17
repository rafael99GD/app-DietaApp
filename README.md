# 🥗 Mi Dieta — App Android de seguimiento nutricional

Aplicación Android nativa para registrar y controlar tu alimentación diaria. Permite gestionar una biblioteca personalizada de alimentos con su tabla nutricional completa, organizar comidas por día, guardar recetas reutilizables y anotar comidas fuera de casa como estimaciones rápidas.

---

## 📱 Capturas de pantalla

> *(Añade aquí capturas cuando quieras: arrástralas al editor de GitHub o usa la sintaxis `![descripción](ruta/imagen.png)`)*

---

## ✨ Funcionalidades

### 🗓️ Diario diario
- Navega entre días con flechas o abriendo el **calendario mensual** (toca la fecha)
- Los días con datos aparecen **marcados con un punto** en el calendario
- Botón **"Hoy"** en la barra superior para volver al día actual con un toque (solo visible cuando estás en otro día)
- Resumen nutricional total del día siempre visible en la parte superior (kcal, grasas, hidratos, proteínas, sal...)
- Puedes **editar días anteriores** en cualquier momento

### 🍽️ Comidas
- Añade tantas comidas al día como necesites ("Desayuno", "Almuerzo", "Cena", o el nombre que quieras)
- Dentro de cada comida, añade **alimentos de tu biblioteca** especificando los gramos usados
- Añade también **recetas completas** directamente, con opción de ajustar la cantidad (media ración, doble, etc.)
- Los totales nutricionales de cada comida se calculan automáticamente a partir de los gramos introducidos
- Menú de opciones por comida: renombrar, eliminar, o **guardar como receta nueva**

### 🧺 Biblioteca de alimentos
- Guarda todos los alimentos que compres con su **tabla nutricional completa** por cada 100g:
  - Calorías (kcal), Grasas, Grasas saturadas, Hidratos de carbono, Azúcares, Proteínas, Sal
- Asigna a cada alimento un **emoticono** (de una lista amplia de comida) o una **foto de tu galería**
- Buscador instantáneo por nombre
- Edición y eliminación de alimentos existentes

### 📖 Recetas
- Crea recetas manualmente añadiendo ingredientes desde tu biblioteca
- O guárdalas directamente desde una comida ya preparada con **"Guardar como receta"** (las recetas anidadas se aplanan automáticamente)
- Asigna nombre, foto y emoticono a cada receta
- Visualiza los ingredientes y los **totales nutricionales completos** de cada receta
- Añade recetas a cualquier comida del día (con ajuste de cantidad)

### 🍦 Extras (comidas fuera)
- Para lo que comes fuera y no puedes medir con exactitud: introduce una **estimación manual** de los valores nutricionales totales
- Se suman al resumen del día igual que el resto de comidas
- Emoticono personalizable para identificarlos rápidamente

---

## 🏗️ Arquitectura y tecnologías

```
com.rafael.dietaapp/
├── data/
│   ├── entities/        # Entidades Room (Alimento, Comida, Receta, Extra...)
│   ├── dao/             # Data Access Objects con queries SQL
│   ├── model/           # Modelos compuestos (ComidaDetallada, DiaDetallado...)
│   ├── repository/      # DietaRepository — única fuente de verdad
│   └── AppDatabase.kt   # Base de datos Room
├── navigation/
│   ├── AppNavGraph.kt   # Grafo de navegación con todas las rutas
│   └── Rutas.kt         # Constantes y constructores de rutas
├── ui/
│   ├── components/      # Componentes reutilizables (ResumenNutrientes, SelectorEmoji, Calendario...)
│   ├── screens/         # Una pantalla por fichero
│   └── theme/           # Tema Material 3 (colores, tipografía)
├── util/
│   └── FechaUtils.kt    # Utilidades de fechas
├── DietaApplication.kt  # Application class (inicializa DB y repositorio)
└── MainActivity.kt      # Punto de entrada
```

### Stack técnico

| Componente | Tecnología |
|---|---|
| Lenguaje | **Kotlin** |
| UI | **Jetpack Compose** + Material 3 |
| Base de datos local | **Room** (SQLite) |
| Generación de código DB | **KSP** (Kotlin Symbol Processing) |
| Navegación | **Navigation Compose** |
| Carga de imágenes | **Coil** |
| Concurrencia | **Coroutines** + **Flow** |
| Arquitectura | Repository pattern + Estado reactivo con Flow |

### Esquema de base de datos

```
alimentos          → biblioteca de alimentos del usuario
dias               → días con al menos un registro
comidas            → comidas dentro de un día
comida_alimentos   → ingredientes sueltos de una comida (N:M con gramos)
comida_recetas     → recetas añadidas a una comida (N:M con factor de ración)
extras             → estimaciones manuales de comidas fuera
recetas            → recetas guardadas
receta_alimentos   → ingredientes de una receta (N:M con gramos)
```

---

## 🚀 Instalación y uso

### Requisitos
- **Android Studio** (versión Hedgehog o superior recomendada)
- **Android SDK** mínimo API 26 (Android 8.0 Oreo)
- **JDK 17**

### Clonar y abrir
```bash
git clone https://github.com/TU_USUARIO/TU_REPO.git
```
1. Abre **Android Studio**
2. `File → Open` → selecciona la carpeta clonada
3. Espera a que termine el **Gradle Sync** (descarga dependencias automáticamente)
4. Conecta un dispositivo Android o crea un emulador (AVD Manager)
5. Dale a **Run ▶️**

### Compilar en modo release (APK)
```
Build → Generate Signed App Bundle / APK → APK → ...
```

---

## 📐 Decisiones de diseño

- **Todo local, sin backend**: todos los datos se guardan únicamente en el dispositivo con Room/SQLite. No hay cuentas, no hay servidor, no hay internet requerido.
- **Flujo reactivo**: el repositorio expone `Flow<T>` en todas las consultas. Las pantallas se suscriben con `collectAsState()` y se actualizan automáticamente cuando cambia la base de datos, sin polling ni callbacks manuales.
- **Cálculo nutricional siempre derivado**: los totales de comidas y días nunca se guardan en la base de datos, siempre se calculan en tiempo real a partir de los gramos y los valores por 100g. Esto evita inconsistencias si se edita un alimento.
- **Recetas autocontenidas**: al guardar una comida como receta, las recetas anidadas que tuviera se "aplanan" (sus ingredientes se incorporan directamente con los gramos escalados), para que cada receta sea independiente y no dependa de que otras recetas existan.

---

## 🗺️ Roadmap / ideas futuras

- [ ] Gráficas de evolución semanal/mensual
- [ ] Objetivos diarios personalizables (kcal objetivo, macros...)
- [ ] Exportación/importación de datos (backup)
- [ ] Búsqueda de alimentos por código de barras (cámara)
- [ ] Modo oscuro manual (independiente del sistema)
- [ ] Notificaciones recordatorio

---

## 👤 Autor

**Rafael** — Graduado de Ingeniería en Videojuegos, U-tad Madrid  
GitHub: [@rafael99GD](https://github.com/rafael99GD)  
Portfolio: [rafael99gd.github.io/game-Portfolio](https://rafael99gd.github.io/game-Portfolio)

---

## 📄 Licencia

Este proyecto es de uso personal. Si quieres reutilizar partes del código, menciona la fuente.
