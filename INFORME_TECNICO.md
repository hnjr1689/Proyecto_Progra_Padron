# Informe Técnico — Padrón Electoral

## 1. Arquitectura del sistema

El sistema sigue una **arquitectura de capas estricta** en la que cada capa solo depende de la inmediatamente inferior:

```
Presentación (TCP / HTTP)
        │
        ▼
  Lógica de negocio
        │
        ▼
  Acceso a datos
        │
        ▼
  Entidades / DTOs / Utilidades
```

### Capas y responsabilidades

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Entidades | `padron.entidades` | POJOs puros (`Persona`, `Direccion`) sin lógica de negocio |
| DTOs | `padron.dto` | Objetos de transferencia: `SolicitudPadron`, `RespuestaPadron`, `FormatoSalida` |
| Datos | `padron.datos` | Lectura de archivos (`PADRON.txt`, `distelec.txt`), búsquedas |
| Lógica | `padron.logica` | Validación y orquestación de la consulta (`ServicioPadron`) |
| Presentación TCP | `padron.presentacion.tcp` | Servidor TCP concurrente, parser del protocolo `GET|cedula|formato` |
| Presentación HTTP | `padron.presentacion.http` | Servidor HTTP con endpoints REST, manejo de códigos de estado |
| Utilidades | `padron.util` | Serialización JSON/XML sin dependencias externas |

---

## 2. Decisiones de diseño

### 2.1 Sin dependencias externas
El proyecto usa **únicamente el JDK 21** (Swing, `com.sun.net.httpserver`, `java.nio`). Esto facilita la compilación en cualquier ambiente sin gestores de dependencias (Maven, Gradle). La serialización JSON/XML se implementó manualmente en `Serializador.java`.

### 2.2 Protocolo TCP — una respuesta por línea
Cada petición TCP produce exactamente **una línea de respuesta**. Las respuestas JSON/XML se compactan (sin saltos de línea internos) para que clientes programáticos puedan leer la respuesta con un único `readLine()`.

**Formato:**
```
← Solicitud:  GET|101053316|JSON
→ Respuesta:  { "cedula": "101053316", "nombre": "LUCILA", ... }
```

### 2.3 Separación de errores de protocolo vs. errores de contenido
En `ManejadorCliente` se realiza una **validación estructural previa** al parseo:
1. Si el mensaje no tiene exactamente 3 campos separados por `|` → error de protocolo (400).
2. Si el primer campo no es `GET` → error de protocolo (400).
3. Si el formato pasa la estructura pero el contenido es inválido (cédula vacía, formato desconocido) → error de contenido delegado a `SolicitudPadron.parsear()`.

### 2.4 Distelec cargado en memoria
`RepositorioDistelec` carga **todos los registros de `distelec.txt` en un `HashMap`** al iniciar. Esto permite búsquedas O(1) por código electoral durante el procesamiento de cada solicitud, evitando una lectura secuencial del archivo por cada consulta al padrón.

### 2.5 Concurrencia
Ambos servidores usan un pool de hilos (`ExecutorService.newFixedThreadPool(10)`) para manejar hasta 10 clientes concurrentes. El servidor HTTP corre en un hilo daemon para no bloquear la JVM; el TCP corre en el hilo principal.

### 2.6 Normalización de cédula
La cédula se normaliza en dos puntos:
- En `ServicioPadron.atender()` se eliminan todos los caracteres no numéricos antes de validar longitud.
- En `RepositorioPadron.buscarPorCedula()` se normaliza tanto la cédula buscada como la del archivo para garantizar coincidencia independientemente del formato de entrada.

### 2.7 Escape de caracteres
`Serializador` escapa caracteres especiales en JSON (`"`, `\`, `\n`, `\r`) y XML (`&`, `<`, `>`, `"`, `'`) para garantizar que las respuestas sean documentos válidos incluso si los datos contienen caracteres problemáticos.

---

## 3. Problemas encontrados y soluciones

### 3.1 Tamaño de PADRON.txt
El archivo `PADRON.txt` del TSE pesa aproximadamente 430 MB y no puede incluirse en el repositorio. Se documenta en el `README.md` cómo descargarlo y ubicarlo en `datos/PADRON.txt`.

### 3.2 Concurrencia en lectura de PADRON.txt
Cada solicitud abre y cierra su propio `BufferedReader` sobre `PADRON.txt`. No se usa caché en memoria del padrón porque el archivo es demasiado grande. La lectura secuencial es la única opción viable sin estructuras de índice externas.

### 3.3 Registros malformados en PADRON.txt
El archivo real del TSE contiene algunas líneas con menos de 8 campos. `RepositorioPadron` ignora silenciosamente estas líneas (condición `partes.length < 8`) para no interrumpir la búsqueda.

### 3.4 Respuestas TCP multilínea
Inicialmente las respuestas JSON/XML eran multilinea (formato legible), lo que causaba que clientes programáticos leyendo con `readLine()` solo recibieran la primera línea. Se corrigió compactando todas las respuestas TCP a **una sola línea** en `ManejadorCliente.compactar()`.

---

## 4. Pruebas automatizadas

Las pruebas se ejecutan con `ant test` desde la raíz del proyecto. Cubren:

| Clase de prueba | Escenarios |
|----------------|------------|
| `SolicitudPadronTest` | Parseo válido JSON/XML, líneas con 2 y 4 campos, null, vacío, comando no-GET, formato CSV, cédula vacía |
| `SerializadorTest` | JSON éxito/error, XML éxito/error, escape de comillas y ampersand, delegación desde `serializar()` |
| `ServicioPadronTest` | Solicitud nula, cédula vacía/letras/corta/larga, cédula no encontrada, cédula válida, cédula con guiones |
| `RepositorioDistelecTest` | Carga de archivo real, búsqueda por código existente/inexistente, null, vacío, `totalRegistros()` |

---

## 5. Limitaciones conocidas

- El padrón se lee secuencialmente; en el archivo real (~3 millones de registros) cada consulta puede tardar varios segundos.
- No hay autenticación ni cifrado en ninguno de los dos protocolos.
- El servidor HTTP usa `com.sun.net.httpserver`, una API interna del JDK no documentada oficialmente para producción.
- No se persiste ningún log de consultas.
