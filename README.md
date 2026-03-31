# Padrón Electoral — Proyecto Programación III

## Integrantes

Damian Herrera

| Nombre | Parte |
|--------|-------|
|        | Dominio / Entidades |
|        | Capa de Datos |
|        | Lógica de Negocio |
|        | Servidor TCP |
|        | Servidor HTTP |

---

## Estructura del Proyecto

```
Proyecto_Progra_Padron/
├── build.xml
├── lib/
├── src/
│   └── padron/
│       ├── Main.java
│       ├── dto/
│       ├── entidades/
│       ├── datos/
│       ├── logica/
│       ├── presentacion/
│       │   ├── tcp/
│       │   └── http/
│       └── util/
└── README.md
```

---

## Compilación con Ant

### Requisitos
- Java 21
- Apache Ant

### Archivos de datos (descarga manual)

`PADRON.txt` no está incluido en el repositorio por su tamaño (≈430 MB).
Antes de ejecutar el proyecto, descárgalo del TSE y colócalo así:

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

# Compilar y ejecutar
ant run
```

> La compilación usa `--enable-preview` para habilitar características de vista previa de Java 21.

---

## Protocolo TCP

### Formato de petición

```
GET|cedula|JSON
```

- `GET` — operación solicitada
- `cedula` — número de cédula a consultar
- `JSON` — formato de respuesta esperado

### Ejemplos

**Petición:**
```
GET|123456789|JSON
```

**Respuesta exitosa:**
```json
{
  "cedula": "123456789",
  "nombre": "Juan Pérez",
  "provincia": "San José",
  "canton": "Central"
}
```

**Respuesta — no encontrado:**
```json
{
  "error": "Persona no encontrada",
  "cedula": "123456789"
}
```

---

## Endpoints HTTP

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/padron/{cedula}` | Consultar persona por cédula |

### Ejemplo de petición

```
GET /padron/123456789 HTTP/1.1
Host: localhost:8080
```

### Ejemplo de respuesta

```json
{
  "cedula": "123456789",
  "nombre": "Juan Pérez",
  "provincia": "San José",
  "canton": "Central"
}
```

---

## Códigos HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| `200` | OK | Consulta exitosa, persona encontrada |
| `400` | Bad Request | Cédula con formato inválido |
| `404` | Not Found | Cédula no existe en el padrón |
| `405` | Method Not Allowed | Se usó un método HTTP distinto a GET |
| `500` | Internal Server Error | Error inesperado en el servidor |
