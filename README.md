# Padrón Electoral — Proyecto Programación III

## Integrantes

| Nombre | Parte |
|--------|-------|
| Damian Herrera | Dominio / Entidades / DTOs / Integración |
| Josué Hernández Ayala | Capa de Datos / Repositorios |
| Adrián Leitón | Servidor TCP |
| Daniel Carillo | Servidor HTTP |
| Fabián Pérez | Lógica de Negocio / Serialización |

---

## Estructura del Proyecto

```
Proyecto_Progra_Padron/
├── build.xml
├── datos/
│   ├── distelec.txt        ← incluido en el repo
│   └── PADRON.txt          ← descargar del TSE manualmente
├── lib/
├── src/
│   └── padron/
│       ├── Main.java
│       ├── entidades/
│       │   ├── Persona.java
│       │   └── Direccion.java
│       ├── dto/
│       │   ├── FormatoSalida.java
│       │   ├── SolicitudPadron.java
│       │   └── RespuestaPadron.java
│       ├── datos/
│       │   ├── RepositorioDistelec.java
│       │   └── RepositorioPadron.java
│       ├── logica/
│       │   └── ServicioPadron.java
│       ├── presentacion/
│       │   ├── tcp/
│       │   │   ├── ServidorTCP.java
│       │   │   └── ManejadorCliente.java
│       │   └── http/
│       │       ├── ServidorHTTP.java
│       │       └── ManejadorHTTP.java
│       └── util/
│           └── Serializador.java
└── README.md
```

---

## Compilación y ejecución con Ant

### Requisitos
- Java 21
- Apache Ant

### Archivos de datos (descarga manual)

`PADRON.txt` no está incluido en el repositorio por su tamaño (≈430 MB).
Descárgalo del TSE y colócalo en la carpeta `datos/`:

```
datos/PADRON.txt      ← descargar del TSE manualmente
datos/distelec.txt    ← ya incluido en el repo
```

### Comandos

```bash
# Limpiar archivos compilados
ant clean

# Solo compilar
ant compile

# Generar JAR
ant jar

# Compilar y ejecutar (inicia ambos servidores)
ant run
```

> Nota: en Windows con PowerShell, si `ant` no está en el PATH, usar la ruta completa:
> ```powershell
> & "C:\Users\<usuario>\.vscode\extensions\oracle.oracle-java-25.0.1\nbcode\extide\ant\bin\ant.bat" run
> ```

Una vez iniciado, el sistema queda escuchando en:
- **TCP:** `localhost:5000`
- **HTTP:** `localhost:8080`

---

## Protocolo TCP

### Formato de petición

```
GET|cedula|formato
```

| Campo | Valores válidos | Descripción |
|-------|----------------|-------------|
| `GET` | `GET` | Operación de consulta |
| `cedula` | 9-10 dígitos | Cédula costarricense (acepta guiones/espacios) |
| `formato` | `JSON` / `XML` | Formato de respuesta |

Para cerrar la sesión:
```
BYE
```

### Ejemplos TCP

**Consulta exitosa — JSON:**
```
GET|101053316|JSON
```
```json
{
  "cedula": "101053316",
  "nombre": "LUCILA",
  "primerApellido": "PORRAS",
  "segundoApellido": "AGUERO",
  "provincia": "SAN JOSE",
  "canton": "PURISCAL",
  "distrito": "GRIFO ALTO"
}
```

**Consulta exitosa — XML:**
```
GET|101053316|XML
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persona>
  <cedula>101053316</cedula>
  <nombre>LUCILA</nombre>
  <primerApellido>PORRAS</primerApellido>
  <segundoApellido>AGUERO</segundoApellido>
  <provincia>SAN JOSE</provincia>
  <canton>PURISCAL</canton>
  <distrito>GRIFO ALTO</distrito>
</persona>
```

**Cédula no encontrada:**
```
GET|000000000|JSON
```
```json
{
  "error": "Persona no encontrada para la cédula: 000000000",
  "codigo": 404
}
```

**Formato inválido:**
```
GET|101053316|CSV
```
```json
{
  "error": "Formato desconocido: 'CSV'. Use JSON o XML.",
  "codigo": 400
}
```

**Cerrar sesión:**
```
BYE
→ ADIOS
```

---

## Endpoints HTTP

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/padron/{cedula}?format=json\|xml` | Consulta por cédula en path |
| `GET` | `/padron?cedula={cedula}&format=json\|xml` | Consulta por query param |

### Ejemplos HTTP

**JSON por path variable:**
```
GET http://localhost:8080/padron/101053316?format=json
```
```json
{
  "cedula": "101053316",
  "nombre": "LUCILA",
  "primerApellido": "PORRAS",
  "segundoApellido": "AGUERO",
  "provincia": "SAN JOSE",
  "canton": "PURISCAL",
  "distrito": "GRIFO ALTO"
}
```

**XML por path variable:**
```
GET http://localhost:8080/padron/101053316?format=xml
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persona>
  <cedula>101053316</cedula>
  <nombre>LUCILA</nombre>
  <primerApellido>PORRAS</primerApellido>
  <segundoApellido>AGUERO</segundoApellido>
  <provincia>SAN JOSE</provincia>
  <canton>PURISCAL</canton>
  <distrito>GRIFO ALTO</distrito>
</persona>
```

**JSON por query param:**
```
GET http://localhost:8080/padron?cedula=101053316&format=json
```
*(respuesta idéntica al ejemplo anterior)*

**Cédula con guiones (normalización automática):**
```
GET http://localhost:8080/padron/1-0105-3316?format=json
```
*(retorna el mismo resultado — los guiones se ignoran)*

---

## Códigos HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| `200` | OK | Consulta exitosa, persona encontrada |
| `400` | Bad Request | Cédula vacía o formato inválido |
| `404` | Not Found | Cédula no existe en el padrón |
| `405` | Method Not Allowed | Se usó un método distinto a GET |
| `500` | Internal Server Error | Error inesperado en el servidor |

### Ejemplos de error HTTP

**400 — sin cédula:**
```
GET http://localhost:8080/padron?format=json
```
```json
{ "error": "Cedula vacia.", "codigo": 400 }
```

**404 — no encontrada:**
```
GET http://localhost:8080/padron/000000000?format=json
```
```json
{ "error": "Persona no encontrada para la cédula: 000000000", "codigo": 404 }
```

**405 — método no permitido:**
```
POST http://localhost:8080/padron/101053316
```
```json
{ "error": "Método no permitido. Solo se admite GET.", "codigo": 405 }
```
