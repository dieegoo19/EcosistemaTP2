# Simulador de ecosistema en Java

**Simulación de un ecosistema de lobos y ovejas mediante programación orientada a objetos.**

Proyecto académico desarrollado en pareja. La aplicación representa un entorno dividido en regiones en el que los animales se desplazan, buscan alimento, reaccionan a otros animales, se reproducen y mueren según su estado y las condiciones de la simulación.

El simulador se configura mediante archivos **JSON**, puede ejecutarse desde la terminal y dispone de un **visor gráfico sencillo opcional** para observar la evolución de los animales.

## Tecnologías y conceptos

- **Java:** lógica de simulación y diseño orientado a objetos.
- **JSON:** configuración de escenarios y exportación del estado de la simulación.
- **Apache Commons CLI:** procesamiento de los argumentos de ejecución.
- **Visor gráfico:** biblioteca `viewer.jar` incluida en el proyecto.
- **Eclipse:** entorno de desarrollo utilizado.
- **Diseño:** herencia, clases abstractas, interfaces, factorías (*Factory/Builder*) y patrón *Strategy*.

## Funcionalidades

- **Dos especies:** ovejas (herbívoras) y lobos (carnívoros), con comportamientos diferenciados.
- **Comportamiento por estados:** normal, búsqueda de pareja, hambre, peligro y muerte, según corresponda a cada especie.
- **Interacción entre animales:** los lobos pueden perseguir y cazar presas; las ovejas detectan depredadores e intentan huir.
- **Reproducción y ciclo de vida:** gestión de edad, energía, apareamiento, gestación y nacimiento de nuevos animales.
- **Mapa dividido en regiones:** los animales pueden cambiar de región al desplazarse.
- **Disponibilidad de alimento:** regiones estándar y regiones con suministro dinámico de comida.
- **Estrategias configurables de selección:** elegir el primer animal, el más cercano o el más joven para determinadas interacciones.
- **Escenarios configurables:** número de animales, dimensiones del mapa, regiones y parámetros de creación definidos en JSON.
- **Salida JSON:** exportación de los estados inicial y final; visualización opcional durante la ejecución.

## Estructura del proyecto

```text
EcosistemaTP2/
├── src/
│   └── simulator/
│       ├── launcher/     # Inicio y argumentos de ejecución
│       ├── control/      # Carga de escenarios y control de simulación
│       ├── model/        # Animales, estados, regiones y simulador
│       ├── factories/    # Construcción de objetos desde JSON
│       └── misc/         # Utilidades y operaciones con vectores
├── resources/
│   └── examples/         # Escenarios de ejemplo (ex1.json a ex5.json)
├── lib/
│   └── lib/              # Dependencias JAR
└── README.md
```

### Organización de la simulación

1. `Main` procesa los argumentos, carga el escenario JSON e inicializa las factorías.
2. `Controller` incorpora las regiones y los animales al modelo y ejecuta la simulación.
3. `Simulator` avanza el tiempo, actualiza animales y regiones y gestiona los nacimientos.
4. `Animal`, `Sheep` y `Wolf` implementan las reglas y los comportamientos de cada especie.
5. `RegionManager` controla la distribución espacial de los animales y su acceso a recursos.

## Requisitos

- **JDK de Java** instalado (comprobado con OpenJDK 21).
- **Eclipse IDE**, si se desea trabajar con el proyecto desde el entorno gráfico.
- Las dependencias incluidas en `lib/lib/`:
  - `commons-cli-1.4.jar`
  - `json.jar`
  - `viewer.jar`

No utiliza Maven ni Gradle; las bibliotecas se añaden al *classpath*.

## Cómo ejecutar el proyecto

### Opción A: Eclipse

1. Clona o descarga este repositorio.
2. En Eclipse, selecciona **File → Import → General → Existing Projects into Workspace**.
3. Selecciona la carpeta raíz del proyecto.
4. Abre **Project → Properties → Java Build Path → Libraries** y comprueba que están añadidos los tres archivos `.jar` de `lib/lib/`.
   - **Importante:** la configuración `.classpath` de la versión original contiene rutas absolutas del equipo donde se creó el proyecto. Si Eclipse señala bibliotecas no encontradas, elimina esas referencias y vuelve a añadir los JAR desde la carpeta local.
5. Ejecuta `simulator.launcher.Main` con los siguientes **Program arguments**:

   ```text
   -i resources/examples/ex1.json -o resultado.json -t 10 -dt 0.03 -sv
   ```

6. Ejecuta la clase. Se abrirá el visor sencillo (si tu equipo dispone de entorno gráfico) y se generará `resultado.json`.

### Opción B: terminal (Linux o macOS)

Desde la carpeta raíz del proyecto:

```bash
# Compilar
mkdir -p build
javac -cp "lib/lib/*" -d build $(find src -name '*.java')

# Ejecutar una simulación y guardar el resultado
java -cp "build:lib/lib/*" simulator.launcher.Main \
  -i resources/examples/ex1.json \
  -o resultado.json \
  -t 10 \
  -dt 0.03
```

Para ver el **visor gráfico sencillo** durante la ejecución, añade `-sv` al final del comando. El visor requiere un entorno de escritorio.

**En Windows:** el separador de entradas del *classpath* de Java es `;` en lugar de `:`. Por ejemplo, para ejecutar las clases ya compiladas en `build`:

```powershell
java -cp "build;lib/lib/*" simulator.launcher.Main -i resources/examples/ex1.json -o resultado.json -t 10 -dt 0.03 -sv
```

> La aplicación se ejecuta en modo de simulación por lotes (*batch*). El visor `-sv` permite observar su evolución, pero el modo de interfaz gráfica independiente no está implementado en esta versión.

### Argumentos disponibles

| Parámetro | Descripción | Valor por defecto |
| --- | --- | --- |
| `-i`, `--input` | Archivo JSON de configuración (**obligatorio**) | — |
| `-o`, `--output` | Archivo JSON de salida (**obligatorio**) | — |
| `-t`, `--time` | Duración total de la simulación, en segundos | `10.0` |
| `-dt`, `--delta-time` | Incremento de tiempo por paso, en segundos | `0.03` |
| `-sv`, `--simple-viewer` | Activa el visor gráfico sencillo | Desactivado |
| `-h`, `--help` | Muestra la ayuda de ejecución | — |

## Configuración de un escenario

Los archivos de ejemplo se encuentran en [`resources/examples/`](resources/examples/). Por ejemplo, `ex1.json` define un mapa de 800 × 600, dividido en 20 columnas y 15 filas, con 200 ovejas y 30 lobos:

```json
{
  "width": 800,
  "height": 600,
  "cols": 20,
  "rows": 15,
  "animals": [
    { "amount": 200, "spec": { "type": "sheep", "data": {} } },
    { "amount": 30, "spec": { "type": "wolf",  "data": {} } }
  ]
}
```

Otros ejemplos incorporan regiones con alimento dinámico y posiciones iniciales delimitadas. El programa genera un JSON con los campos `in` (estado inicial) y `out` (estado final), incluyendo el tiempo y el contenido de las regiones.

## Autoría

Proyecto académico **desarrollado en pareja**, con participación de [Carlos Belmonte Arias](https://github.com/carlbelm) y su compañero de equipo. Se reconoce la autoría compartida y se recomienda mantener la referencia al repositorio original y su historial de contribuciones.
