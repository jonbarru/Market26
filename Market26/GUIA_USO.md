# Guía de Uso - Market26 Nuevas Funcionalidades

## 🎯 Acceso a las Nuevas Funcionalidades

Desde **MainGUI**, se agregaron dos nuevos botones en la pantalla principal:

```
┌─────────────────────────────────────────┐
│         MARKET26 - Pantalla Principal   │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  Seleccionar opción              │  │
│  └──────────────────────────────────┘  │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │    Registrarse                  │   │
│  ├─────────────────────────────────┤   │
│  │    Iniciar Sesión               │   │
│  ├─────────────────────────────────┤   │
│  │ ✨ Ver Ranking de Vendedores    │   │
│  ├─────────────────────────────────┤   │
│  │ ✨ Enviar Emails de Contraofertas│  │
│  ├─────────────────────────────────┤   │
│  │  [ English | Euskara | Español] │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

---

## 1️⃣ Ver Ranking de Vendedores

### Acceso
Click en botón: **"Ver Ranking de Vendedores"**

### Interfaz
```
┌────────────────────────────────────────────────┐
│   Ranking de Vendedores          [Actualizar]  │
├────────────────────────────────────────────────┤
│  #  │  Nombre              │  Email           │
├─────┼──────────────────────┼──────────────────┤
│  1. │  Aitor Fernandez     │  seller1@g...   │  ⭐ 4.8
│  2. │  Ane Gaztañaga       │  seller22@g...  │  ⭐ 4.2
│  3. │  Test Seller         │  seller3@g...   │  ⭐ 3.5
└────────────────────────────────────────────────┘
           [Actualizar]  [Cerrar]
```

### Características
- ✅ Tabla JTable con 3 columnas: Nombre, Email, Valoración
- ✅ Vendedores ordenados de mayor a menor valoración
- ✅ Botón "Actualizar" para refrescar datos
- ✅ Multiidioma (español, inglés, euskera)
- ✅ Manejo de errores con mensajes claros

### Flujo de Datos
```
SellerRankingGUI
    ↓
MainGUI.getBusinessLogic()
    ↓
BLFacade.getSellerRanking()
    ↓
BLFacadeImplementation.getSellerRanking()
    ↓
DataAccess.getSellerRanking()
    ↓
ObjectDB: SELECT s FROM Seller s ORDER BY s.rating DESC
    ↓
Resultado: List<Seller>
    ↓
Mostrar en JTable
```

---

## 2️⃣ Enviar Emails de Contraofertas

### Acceso
Click en botón: **"Enviar Emails de Contraofertas"**

### Interfaz
```
┌─────────────────────────────────────────────┐
│  Enviar Emails de Contraofertas             │
├─────────────────────────────────────────────┤
│                                             │
│  Este proceso enviará un email a todos      │
│  los vendedores con sus contraofertas       │
│  pendientes. Asegúrate de que el servicio   │
│  de email está configurado correctamente.   │
│                                             │
├─────────────────────────────────────────────┤
│  Iniciando envío de emails...               │
│  ================================           │
│                                             │
│  ✅ Proceso completado exitosamente.        │
│                                             │
│  Los vendedores han sido notificados        │
│  sobre sus contraofertas pendientes.        │
│                                             │
└─────────────────────────────────────────────┘
         [Enviar Emails]  [Cerrar]
```

### Características
- ✅ Diálogo modal (no permite interacción con otras ventanas)
- ✅ Área de texto para ver progreso/resultados
- ✅ Agrupación automática de contraofertas por vendedor
- ✅ Envío SMTP con configuración Gmail
- ✅ Mensajes informativos: ✅ (éxito), ❌ (error)
- ✅ Manejo de excepciones

### Email Enviado
```
Asunto: Contraofertas pendientes - Market26

Estimado vendedor,

Tiene las siguientes contraofertas pendientes:

- Producto: Futbol Baloia
  Precio original: 10.0€
  Precio ofertado: 8.5€
  Comprador: buyer1@gmail.com

- Producto: Samsung 42" Telebista
  Precio original: 175.0€
  Precio ofertado: 150.0€
  Comprador: buyer2@gmail.com

Por favor, responda a través de la aplicación.

Saludos,
Equipo Market26
```

### Flujo de Datos
```
SendCounterOffersEmailGUI
    ↓
click en [Enviar Emails]
    ↓
BLFacade.sendCounterOffersEmailToSellers()
    ↓
BLFacadeImplementation.sendCounterOffersEmailToSellers()
    ↓
DataAccess.getCounterOffersSummary()
    ↓
ObjectDB: SELECT c FROM CounterOffer c WHERE c.status = 'Pendiente'
    ↓
Agrupar por vendedor
    ↓
Para cada vendedor:
    └─→ EmailService.sendEmail(vendedor_email, asunto, cuerpo)
    └─→ SMTP (Gmail u otro proveedor)
    └─→ Vendor recibe email
```

### Configuración de Email

Editar `src/main/java/businessLogic/EmailService.java`:

```java
// Líneas 14-17:
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SMTP_PORT = "587";
private static final String SENDER_EMAIL = "tu_email@gmail.com";  // ← CAMBIAR
private static final String SENDER_PASSWORD = "contraseña_app";   // ← CAMBIAR
```

**Para Gmail:**
1. Acceder a: https://myaccount.google.com/apppasswords
2. Crear contraseña de aplicación para "Mail"
3. Copiar contraseña generada a SENDER_PASSWORD

**Modo Fallback:**
Si SENDER_PASSWORD está vacío, el sistema loguea el email en consola sin enviarlo realmente.

---

## 📊 Automatización Futura

Para ejecutar el envío de emails **automáticamente cada día**:

### Opción 1: Quartz Scheduler

```java
// En pom.xml:
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.3.2</version>
</dependency>

// En BusinessLogicServer:
Scheduler scheduler = new StdSchedulerFactory().getScheduler();
scheduler.start();
JobDetail job = JobBuilder.newJob(SendEmailsJob.class)
    .withIdentity("sendEmails", "group1")
    .build();
Trigger trigger = TriggerBuilder.newTrigger()
    .withIdentity("trigger1", "group1")
    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(9, 0))
    .build();
scheduler.scheduleJob(job, trigger);
```

### Opción 2: ScheduledExecutorService

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(
    () -> businessLogic.sendCounterOffersEmailToSellers(),
    0,           // Delay inicial
    1,           // Período
    TimeUnit.DAYS  // Unidad de tiempo
);
```

---

## 🔧 Troubleshooting

### "Connection refused"
- ✓ Verificar que BusinessLogicServer está ejecutándose
- ✓ Verificar puerto 1099 en config.xml

### Email no se envía
- ✓ Configurar SENDER_PASSWORD en EmailService.java
- ✓ Usar contraseña de aplicación (no contraseña normal)
- ✓ Verificar conexión a internet

### No aparecen vendedores en ranking
- ✓ Verificar que hay vendedores registrados en BD
- ✓ Verificar conexión a ObjectDB
- ✓ Revisar logs de la aplicación

### No aparecen contraofertas para enviar
- ✓ Verificar que existen contraofertas con estado "Pendiente"
- ✓ Click derecho en contraoferta → "Ver Contraofertas"

---

## 📝 Campos Agregados/Modificados

### Seller.java
```java
// ANTES:
private String email;
private String name;
private List<Sale> sales;
private String password;

// DESPUÉS:
private String email;
private String name;
private List<Sale> sales;
private String password;
private double rating = 0.0;  // ← NUEVO
```

### BLFacade.java
```java
// NUEVOS MÉTODOS:
public List<Seller> getSellerRanking();
public boolean sendCounterOffersEmailToSellers();
```

### DataAccess.java
```java
// NUEVOS MÉTODOS:
public List<Seller> getSellerRanking();
public List<CounterOffer> getCounterOffersSummary();
public void updateSellerRating(String sellerEmail, double newRating);
```

---

## 📚 Documentación Relacionada

- `ARQUITECTURA_3_NIVELES.md` - Guía completa de arquitectura
- `CAMBIOS.md` - Resumen detallado de cambios
- Código fuente: Bien comentado con Javadoc

---

**Última actualización**: 6 de mayo de 2026
**Estado**: ✅ Funcional y probado
