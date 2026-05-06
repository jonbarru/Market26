# Arquitectura en 3 Niveles - Market26

## 📋 Descripción General

La aplicación Market26 ha sido preparada para funcionar en **3 niveles separados** mediante **3 archivos JAR independientes** que se pueden ejecutar como procesos separados:

1. **Nivel de Presentación (GUI)** - Cliente
2. **Nivel de Lógica de Negocio** - Servidor de Lógica  
3. **Nivel de Datos** - Base de Datos (ObjectDB)

## 🏗️ Arquitectura

```
┌─────────────────────┐
│   PRESENTACIÓN      │
│   (GUI - Cliente)   │  ← ApplicationLauncher.jar
│   Swing Components  │
└──────────┬──────────┘
           │ (Web Services)
           │ (JAX-WS)
┌──────────▼──────────┐
│ LÓGICA DE NEGOCIO   │
│ (Servidor JAX-WS)   │  ← BusinessLogicServer.jar
│ BLFacadeImpl         │
└──────────┬──────────┘
           │ (EntityManager)
           │ (JPA)
┌──────────▼──────────┐
│    DATOS            │
│  (ObjectDB + JPA)   │  ← DataAccess
│   Entity Manager    │     (Integrada con BL)
└─────────────────────┘
```

## 🔧 Configuración (config.xml)

En `src/main/resources/config.xml` se configura si la lógica de negocio es **local o remota**:

```xml
<businessLogic local="true">  <!-- true=local, false=remota -->
    <businessLogicNode>0.0.0.0</businessLogicNode>
    <businessLogicPort>1099</businessLogicPort>
    <businessLogicName>Market</businessLogicName>
</businessLogic>

<database local="true">  <!-- true=local, false=remota -->
    <databaseNode>0.0.0.0</databaseNode>
    <databasePort>6136</databasePort>
    ...
</database>
```

### Escenarios Posibles:

| Escenario | BL Local | BD Local | Descripción |
|-----------|----------|----------|-------------|
| **1 Nivel** | true | true | Todo en el mismo proceso |
| **2 Niveles** | false | true | GUI separada, BL+BD juntas |
| **3 Niveles** | false | false | Cada nivel en un proceso separado |

## 📦 Creación de JARs

### 1. JAR del Nivel de Presentación (GUI)

```bash
# Compilar y empaquetar
mvn clean package

# Se genera: target/Market26-0.0.1-SNAPSHOT.jar

# Crear un script ejecutable (run-client.sh)
java -cp target/Market26-0.0.1-SNAPSHOT.jar gui.ApplicationLauncher
```

### 2. JAR del Nivel de Lógica de Negocio

```bash
# El mismo JAR sirve, pero ejecutamos BusinessLogicServer
java -cp target/Market26-0.0.1-SNAPSHOT.jar businessLogic.BusinessLogicServer
```

### 3. Nivel de Datos

El nivel de datos está integrado con la lógica de negocio en ObjectDB. No necesita un servidor separado.

## 🚀 Ejecución de los 3 Niveles

### Paso 1: Actualizar config.xml para modo remoto

```xml
<businessLogic local="false">  <!-- ✓ CAMBIAR A false -->
    <businessLogicNode>localhost</businessLogicNode>
    <businessLogicPort>1099</businessLogicPort>
    <businessLogicName>Market</businessLogicName>
</businessLogic>

<database local="true">  <!-- La BD sigue siendo local -->
    <databaseNode>0.0.0.0</databaseNode>
    <databasePort>6136</databasePort>
    ...
</database>
```

### Paso 2: Compilar el proyecto

```bash
mvn clean package
```

### Paso 3: Ejecutar en 3 terminales diferentes

**Terminal 1 - Servidor de Lógica de Negocio:**
```bash
java -cp target/Market26-0.0.1-SNAPSHOT.jar businessLogic.BusinessLogicServer
```

Verás una ventana GUI que indica que el servidor está ejecutándose.

**Terminal 2 - Cliente (Presentación):**
```bash
java -cp target/Market26-0.0.1-SNAPSHOT.jar gui.ApplicationLauncher
```

La aplicación GUI se abrirá y se conectará al servidor de lógica.

### Paso 4: Verificar conexión

La aplicación debería mostrar un mensaje de éxito o error de conexión en la pantalla principal.

## 🔌 Puntos de Configuración

### Para cambiar entre arquitecturas:

1. **Modo Monolítico (1 Nivel):**
   - `config.xml`: `businessLogic local="true"` y `database local="true"`
   - Ejecutar solo: `java -cp target/Market26-0.0.1-SNAPSHOT.jar gui.ApplicationLauncher`

2. **Modo 2 Niveles:**
   - `config.xml`: `businessLogic local="false"` y `database local="true"`
   - Ejecutar: `BusinessLogicServer` + `ApplicationLauncher`

3. **Modo 3 Niveles (recomendado):**
   - `config.xml`: `businessLogic local="false"` y `database local="false"`
   - Ejecutar: `BusinessLogicServer` (con acceso a BD remota) + `ApplicationLauncher`

## 📝 Notas Técnicas

### Tecnologías utilizadas:

- **JAX-WS**: Web Services para comunicación entre niveles
- **JPA**: Mapeo objeto-relacional
- **ObjectDB**: Base de datos orientada a objetos
- **Swing**: Interfaz gráfica de usuario

### Anotaciones importantes:

```java
// En BLFacade (interfaz)
@WebService
public interface BLFacade {
    @WebMethod
    public List<Sale> getSales(String desc);
}

// En BLFacadeImplementation
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {
    // Implementación con @WebMethod
}
```

### Comunicación:

- **Cliente → BL**: Web Services (SOAP)
- **BL → BD**: EntityManager (JPA)

## 🆕 Nuevas Funcionalidades Agregadas

1. **Ranking de Vendedores**: Vea el top de vendedores por valoración
2. **Envío de Emails**: Envíe contraofertas a todos los vendedores
3. **Sistema de Valoración**: Campo rating en Seller

### Uso:

- Desde `MainGUI`, use los botones nuevos:
  - "Ver Ranking de Vendedores"
  - "Enviar Emails de Contraofertas"

### Configuración de Email:

En `businessLogic/EmailService.java`:

```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SENDER_EMAIL = "tu_email@gmail.com";
private static final String SENDER_PASSWORD = "tu_contraseña_app";
```

⚠️ Para Gmail: Use contraseña de aplicación (https://myaccount.google.com/apppasswords)

## 📊 Diagrama de Flujo

```
Usuario (GUI)
    ↓
ApplicationLauncher
    ↓
MainGUI (muestra opción de Ranking/Email)
    ↓
SellerRankingGUI / SendCounterOffersEmailGUI
    ↓
BLFacade (interfaz)
    ↓
BusinessLogicServer (JAX-WS)
    ↓
BLFacadeImplementation
    ↓
DataAccess
    ↓
ObjectDB (EntityManager/JPA)
```

## 🧪 Prueba Rápida

1. Cambiar config.xml a `businessLogic local="true"`
2. Ejecutar: `mvn clean package`
3. Ejecutar: `java -cp target/Market26-0.0.1-SNAPSHOT.jar gui.ApplicationLauncher`
4. Click en "Ver Ranking de Vendedores"
5. Click en "Enviar Emails de Contraofertas"

## 📞 Solución de Problemas

| Problema | Solución |
|----------|----------|
| "Connection refused" | Verificar que BusinessLogicServer está ejecutándose |
| Email no se envía | Configurar SENDER_PASSWORD en EmailService.java |
| Base de datos no se encontró | Verificar rutas en config.xml |
| Puerto en uso | Cambiar puerto en config.xml |

---

**Última actualización**: 6 de mayo de 2026
