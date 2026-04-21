# Cliente Padrón Electoral — Proyecto 2

Aplicación cliente con **interfaz gráfica Swing** que consume el servidor del Proyecto 1 (Padrón Electoral) mediante TCP o HTTP, y muestra la información en formato JSON o XML.

## Integrantes
| Nombre | Parte |
|--------|-------|
| Damian Herrera | Dominio / Entidades / DTOs / Integración |
| Josué Hernández Ayala | Capa de Datos / Repositorios |
| Adrián Leitón | Servidor TCP |
| Daniel Carillo | Servidor HTTP |
| Fabián Pérez | Lógica de Negocio / Serialización |

---

## Estructura del proyecto

```
ClientePadron/
├── run.bat                              ← compilar y ejecutar (sin Ant)
├── build.xml                            ← compilar con Ant
└── src/cliente/
    ├── Main.java                        ← punto de entrada
    ├── tcp/
    │   └── ClienteTCP.java              ← conexión Socket al servidor TCP
    ├── http/
    │   └── ClienteHTTP.java             ← consumo HTTP con HttpURLConnection
    ├── dto/
    │   └── RespuestaPadron.java         ← DTO con datos parseados
    ├── util/
    │   └── ParserRespuesta.java         ← parser JSON/XML sin librerías externas
    └── presentacion/
        └── VentanaPrincipal.java        ← interfaz Swing completa
```

---

## Requisitos

- Java 23 (JDK)
- El **servidor del Proyecto 1** debe estar en ejecución antes de lanzar el cliente

---

## Cómo ejecutar

### Paso 1 — Iniciar el servidor (Proyecto 1)

Abrir una terminal CMD en la carpeta `Proyecto_Progra_Padron` y ejecutar:

```cmd
run.bat
```

El servidor queda escuchando en:
- **TCP:** `localhost:5000`
- **HTTP:** `localhost:8080`

### Paso 2 — Iniciar el cliente

Abrir **otra** terminal CMD en la carpeta `ClientePadron` y ejecutar:

```cmd
run.bat
```

Se abre la ventana gráfica. También se puede compilar con Ant:

```bash
ant run
```

---

## Cómo conectar al servidor

### Conexión TCP

El cliente abre un `Socket` a `localhost:5000` y envía:

```
GET|<cedula>|JSON
GET|<cedula>|XML
```

Recibe la respuesta en **una sola línea** (JSON o XML compacto).

### Conexión HTTP

El cliente realiza un `GET` a `localhost:8080` usando `HttpURLConnection`:

```
GET http://localhost:8080/padron?cedula=<cedula>&format=json
GET http://localhost:8080/padron?cedula=<cedula>&format=xml
```

---

## Ejemplos de uso

### Consulta por TCP — JSON

**Solicitud enviada:**
```
GET|101053316|JSON
```

**Respuesta recibida:**
```json
{ "cedula": "101053316", "nombre": "LUCILA", "primerApellido": "PORRAS",
  "segundoApellido": "AGUERO", "provincia": "SAN JOSE",
  "canton": "PURISCAL", "distrito": "GRIFO ALTO" }
```

---

### Consulta por TCP — XML

**Solicitud enviada:**
```
GET|101053316|XML
```

**Respuesta recibida:**
```xml
<?xml version="1.0" encoding="UTF-8"?> <persona> <cedula>101053316</cedula>
<nombre>LUCILA</nombre> <primerApellido>PORRAS</primerApellido>
<segundoApellido>AGUERO</segundoApellido> <provincia>SAN JOSE</provincia>
<canton>PURISCAL</canton> <distrito>GRIFO ALTO</distrito> </persona>
```

---

### Consulta por HTTP — JSON

**URL:**
```
GET http://localhost:8080/padron?cedula=101053316&format=json
```

**Respuesta:**
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

---

### Manejo de errores

| Error | Respuesta del servidor |
|-------|------------------------|
| Cédula vacía | `{ "error": "Cedula vacia.", "codigo": 400 }` |
| Cédula no encontrada | `{ "error": "Persona no encontrada...", "codigo": 404 }` |
| Formato TCP inválido | `{ "error": "Formato TCP invalido...", "codigo": 400 }` |
| Sin conexión | Excepción capturada → mensaje en barra de estado |

---

## Evidencia de pruebas

### TCP — Consulta JSON exitosa
![TCP JSON](evidencia/tcp_json.png)

### TCP — Consulta XML exitosa
![TCP XML](evidencia/tcp_xml.png)

### HTTP — Consulta JSON exitosa
![HTTP JSON](evidencia/http_json.png)

### HTTP — Consulta XML exitosa
![HTTP XML](evidencia/http_xml.png)

### Manejo de error — cédula no encontrada
![Error 404](evidencia/error_404.png)

### Manejo de error — sin conexión al servidor
![Error conexión](evidencia/error_conexion.png)
