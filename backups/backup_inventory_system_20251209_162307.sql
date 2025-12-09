-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: inventory_system
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `inventory_system`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `inventory_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `inventory_system`;

--
-- Table structure for table `alertas`
--

DROP TABLE IF EXISTS `alertas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alertas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_alerta_id` int NOT NULL,
  `producto_id` int DEFAULT NULL,
  `lote_id` int DEFAULT NULL,
  `mensaje` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `nivel_prioridad` enum('baja','media','alta','critica') COLLATE utf8mb4_unicode_ci DEFAULT 'media',
  `leida` tinyint(1) DEFAULT '0',
  `fecha_alerta` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_lectura` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  KEY `idx_tipo_alerta` (`tipo_alerta_id`),
  KEY `idx_leida` (`leida`),
  KEY `idx_fecha_alerta` (`fecha_alerta`),
  KEY `idx_producto` (`producto_id`),
  CONSTRAINT `alertas_ibfk_1` FOREIGN KEY (`tipo_alerta_id`) REFERENCES `tipos_alerta` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `alertas_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `alertas_ibfk_3` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alertas`
--

LOCK TABLES `alertas` WRITE;
/*!40000 ALTER TABLE `alertas` DISABLE KEYS */;
INSERT INTO `alertas` VALUES (1,1,17,NULL,'El producto \"Hidrocortisona 1% Crema\" ha alcanzado el stock mínimo. Stock actual: 45. Stock mínimo: 15.','media',0,'2025-12-09 19:58:41',NULL),(2,1,25,NULL,'El producto \"Loratadina 10mg\" ha alcanzado el stock mínimo. Stock actual: 55. Stock mínimo: 18.','media',0,'2025-12-09 19:58:41',NULL),(3,2,37,NULL,'¡URGENTE! El producto \"Termómetro Digital\" está por debajo del stock mínimo. Stock actual: 20. Stock mínimo: 5.','alta',0,'2025-12-09 19:58:41',NULL),(4,3,2,NULL,'El lote \"LT-IBU-2024-B02\" del producto \"Ibuprofeno 400mg\" vence en 30 días (2026-02-10)','media',0,'2025-12-09 19:58:41',NULL),(5,1,40,NULL,'El producto \"Tensiómetro Digital\" ha alcanzado el stock mínimo. Stock actual: 10. Stock mínimo: 3.','media',0,'2025-12-09 19:58:41',NULL),(6,2,35,NULL,'¡URGENTE! El producto \"Suero Fisiológico\" está por debajo del stock crítico. Stock actual: 55. Stock mínimo: 18.','alta',0,'2025-12-09 19:58:41',NULL),(7,1,23,NULL,'El producto \"Metoclopramida 10mg\" ha alcanzado el stock mínimo. Stock actual: 35. Stock mínimo: 10.','media',0,'2025-12-09 19:58:41',NULL),(8,3,21,NULL,'El lote \"LT-OME-2024-G07\" del producto \"Omeprazol 20mg\" vence en 25 días (2025-10-25)','media',0,'2025-12-09 19:58:41',NULL),(9,1,39,NULL,'El producto \"Oxímetro de Pulso\" ha alcanzado el stock mínimo. Stock actual: 15. Stock mínimo: 5.','media',0,'2025-12-09 19:58:41',NULL),(10,4,24,NULL,'El lote \"LT-CAN-2024-X24\" del producto \"Loperamida 2mg\" presenta cambios en color y textura. Requiere inspección de calidad.','alta',0,'2025-12-09 19:58:41',NULL),(11,2,40,NULL,'¡URGENTE! El producto \"Tensiómetro Digital\" está por debajo del stock mínimo. Stock actual: 10. Stock mínimo: 3.','alta',0,'2025-12-09 19:58:41',NULL),(12,3,5,NULL,'El lote \"LT-AMI-2024-C03\" del producto \"Amoxicilina 500mg\" vence en 20 días (2025-12-31)','media',0,'2025-12-09 19:58:41',NULL),(13,3,1,NULL,'El lote \"LT-PAR-2024-A01\" del producto \"Paracetamol 500mg\" vence en 45 días (2026-01-15)','baja',0,'2025-12-09 19:58:41',NULL),(14,1,31,NULL,'El producto \"Isopropílico 70% 1L\" ha alcanzado el stock mínimo. Stock actual: 50. Stock mínimo: 15.','media',0,'2025-12-09 19:58:41',NULL),(15,4,17,NULL,'El lote \"LT-LID-2024-P16\" del producto \"Hidrocortisona 1% Crema\" presenta olor anormal. Requiere inspección de calidad.','alta',0,'2025-12-09 19:58:41',NULL),(16,3,10,NULL,'El lote \"LT-DIC-2024-D04\" del producto \"Diclofenaco 75mg\" vence en 25 días (2025-11-30)','media',0,'2025-12-09 19:58:41',NULL),(17,1,18,NULL,'El producto \"Clotrimazol 1% Crema\" ha alcanzado el stock mínimo. Stock actual: 38. Stock mínimo: 12.','media',0,'2025-12-09 19:58:41',NULL);
/*!40000 ALTER TABLE `alertas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `datos_anteriores` json DEFAULT NULL,
  `datos_nuevos` json DEFAULT NULL,
  `fecha_operacion` datetime(6) DEFAULT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operacion` enum('DELETE','INSERT','UPDATE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `registro_id` int DEFAULT NULL,
  `tabla_afectada` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj0mvp43e8kl8ec92f9u0q46g9` (`usuario_id`),
  CONSTRAINT `FKj0mvp43e8kl8ec92f9u0q46g9` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
INSERT INTO `auditoria` VALUES (3,NULL,'{\"id\": 1, \"estado\": \"FALLIDO\", \"usuario\": {\"id\": 1, \"rol\": {\"id\": 1, \"activo\": true, \"codigo\": \"ADMIN\", \"nombre\": \"Administrador\", \"descripcion\": \"Acceso completo al sistema\", \"nivelAcceso\": 4, \"fechaCreacion\": \"2025-12-09T11:58:40\", \"fechaActualizacion\": \"2025-12-09T11:58:40\"}, \"email\": \"admin@inti.com\", \"activo\": true, \"username\": \"admin\", \"passwordHash\": \"admin123\", \"fechaCreacion\": \"2025-12-09T11:58:41\", \"nombreCompleto\": \"Administrador Sistema\", \"fechaActualizacion\": \"2025-12-09T11:58:41\"}, \"tipoBackup\": \"MANUAL\", \"descripcion\": \"Backup manual de la base de datos inventory_system\", \"rutaArchivo\": \"backups/backup_inventory_system_20251209_161825.sql\", \"tamanoBytes\": 0, \"mensajeError\": \"Error al ejecutar mysqldump\", \"fechaCreacion\": \"2025-12-09T16:18:25.2149073\", \"nombreArchivo\": \"backup_inventory_system_20251209_161825.sql\"}','2025-12-09 20:18:25.631769','0:0:0:0:0:0:0:1','INSERT',1,'backups',1);
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backups`
--

DROP TABLE IF EXISTS `backups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_archivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ruta_archivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tamano_bytes` bigint NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `usuario_id` int DEFAULT NULL,
  `tipo_backup` enum('MANUAL','AUTOMATICO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('EXITOSO','FALLIDO','EN_PROCESO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mensaje_error` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_archivo` (`nombre_archivo`),
  KEY `usuario_id` (`usuario_id`),
  KEY `idx_backups_fecha` (`fecha_creacion`),
  KEY `idx_backups_estado` (`estado`),
  KEY `idx_backups_tipo` (`tipo_backup`),
  CONSTRAINT `backups_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backups`
--

LOCK TABLES `backups` WRITE;
/*!40000 ALTER TABLE `backups` DISABLE KEYS */;
INSERT INTO `backups` VALUES (1,'backup_inventory_system_20251209_161825.sql','backups/backup_inventory_system_20251209_161825.sql',0,'2025-12-09 20:18:25',1,'MANUAL','FALLIDO','Backup manual de la base de datos inventory_system','Error al ejecutar mysqldump');
/*!40000 ALTER TABLE `backups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `activa` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'Analgesicos','Medicamentos para el alivio del dolor',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,'Antibioticos','Medicamentos para tratar infecciones bacterianas',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,'Antiinflamatorios','Medicamentos para reducir la inflamación',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,'Vitaminas','Suplementos vitamínicos y nutricionales',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,'Dermatologicos','Productos para el cuidado de la piel',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(6,'Gastrointestinales','Medicamentos para problemas digestivos',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(7,'Antihistaminicos','Medicamentos para alergias estacionales',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(8,'Material de Curación','Insumos médicos para heridas y curaciones',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(9,'Higiene Personal','Productos de cuidado personal',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(10,'Equipos Médicos','Instrumentos y equipos para consulta médica',1,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion_sistema`
--

DROP TABLE IF EXISTS `configuracion_sistema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_sistema` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `fecha_actualizacion` datetime(6) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `tipo_dato` enum('BOOLEAN','INTEGER','JSON','STRING') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKn2k8b6hwyumsdfmax8vg0320m` (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion_sistema`
--

LOCK TABLES `configuracion_sistema` WRITE;
/*!40000 ALTER TABLE `configuracion_sistema` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuracion_sistema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `control_calidad`
--

DROP TABLE IF EXISTS `control_calidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `control_calidad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lote_id` int NOT NULL,
  `estado_calidad` enum('PENDIENTE','EN_REVISION','LIBERADO','RECHAZADO','EN_CUARENTENA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `usuario_inspector_id` int DEFAULT NULL,
  `fecha_inspeccion` datetime DEFAULT NULL,
  `fecha_liberacion` datetime DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `motivo_rechazo` text COLLATE utf8mb4_unicode_ci,
  `cumple_especificaciones` tinyint(1) DEFAULT NULL,
  `temperatura_recepcion` decimal(5,2) DEFAULT NULL,
  `lote_proveedor` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `certificado_calidad` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuario_inspector_id` (`usuario_inspector_id`),
  KEY `idx_lote` (`lote_id`),
  KEY `idx_estado` (`estado_calidad`),
  KEY `idx_fecha_inspeccion` (`fecha_inspeccion`),
  CONSTRAINT `control_calidad_ibfk_1` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `control_calidad_ibfk_2` FOREIGN KEY (`usuario_inspector_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `control_calidad`
--

LOCK TABLES `control_calidad` WRITE;
/*!40000 ALTER TABLE `control_calidad` DISABLE KEYS */;
INSERT INTO `control_calidad` VALUES (1,1,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,2,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,3,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,4,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,5,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(6,6,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(7,7,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(8,8,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(9,9,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(10,10,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(11,11,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(12,12,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(13,13,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(14,14,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(15,15,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(16,16,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(17,17,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(18,18,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(19,19,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(20,20,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(21,21,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(22,22,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(23,23,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(24,24,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(25,25,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(26,1,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Producto en perfecto estado, sin anomalías visuales',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(27,2,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Cumple con todas las especificaciones de calidad',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(28,3,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(29,4,'EN_REVISION',2,'2025-12-09 15:58:41',NULL,'Se observa variación en el color de las tabletas, requiere análisis',NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(30,5,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Vitamina C en excelente estado de conservación',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(31,6,'EN_CUARENTENA',2,'2025-12-09 15:58:41',NULL,'Crema presenta segregación de componentes, se envía a laboratorio',NULL,0,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(32,7,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Omeprazol cumple con especificaciones de calidad',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(33,8,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Antihistamínico sin anomalías detectadas',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(34,9,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Material de curación estéril y en buen estado',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(35,10,'RECHAZADO',2,'2025-12-09 15:58:41',NULL,'Algodón presenta signos de humedad y posible contaminación microbiana',NULL,0,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(36,11,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Producto de higiene cumple con especificaciones',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(37,12,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(38,13,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Antibiótico cumple con especificaciones de calidad',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(39,14,'EN_REVISION',2,'2025-12-09 15:58:41',NULL,'Se solicita análisis de contenido activo',NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(40,15,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Antihistamínico sin anomalías',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(41,16,'EN_CUARENTENA',2,'2025-12-09 15:58:41',NULL,'Crema presenta textura diferente a la esperada, se envía a análisis',NULL,0,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(42,17,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Gastrointestinal cumple con especificaciones',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(43,18,'PENDIENTE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(44,19,'RECHAZADO',2,'2025-12-09 15:58:41',NULL,'Equipo presenta defectos en la calibración inicial',NULL,0,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(45,20,'LIBERADO',1,'2025-12-09 15:58:41',NULL,'Suero fisiológico cumple con especificaciones de esterilidad',NULL,1,NULL,NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `control_calidad` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_actualizar_estado_lote` AFTER UPDATE ON `control_calidad` FOR EACH ROW BEGIN
    IF OLD.estado_calidad != NEW.estado_calidad THEN
        UPDATE lotes
        SET estado_calidad = NEW.estado_calidad,
        fecha_liberacion = IF(NEW.estado_calidad = 'LIBERADO', NOW(), NULL)
        WHERE id = NEW.lote_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `numero_lote` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_fabricacion` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `cantidad_inicial` int NOT NULL,
  `cantidad_actual` int NOT NULL,
  `proveedor_id` int DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `estado_calidad` enum('PENDIENTE','EN_REVISION','LIBERADO','RECHAZADO','EN_CUARENTENA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `ubicacion_id` int DEFAULT NULL,
  `fecha_liberacion` datetime DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_lote_producto` (`producto_id`,`numero_lote`),
  KEY `proveedor_id` (`proveedor_id`),
  KEY `idx_producto` (`producto_id`),
  KEY `idx_numero_lote` (`numero_lote`),
  KEY `idx_fecha_vencimiento` (`fecha_vencimiento`),
  KEY `idx_estado_calidad` (`estado_calidad`),
  KEY `idx_ubicacion` (`ubicacion_id`),
  KEY `idx_lotes_vencimiento_activo` (`fecha_vencimiento`,`activo`) USING BTREE,
  CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `lotes_ibfk_2` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE SET NULL,
  CONSTRAINT `lotes_ibfk_3` FOREIGN KEY (`ubicacion_id`) REFERENCES `ubicaciones_almacen` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes`
--

LOCK TABLES `lotes` WRITE;
/*!40000 ALTER TABLE `lotes` DISABLE KEYS */;
INSERT INTO `lotes` VALUES (1,1,'LT-PAR-2024-A01','2024-01-15','2026-01-15',100,75,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,2,'LT-IBU-2024-B02','2024-02-10','2026-02-10',50,42,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,3,'LT-AMI-2024-C03','2024-03-05','2025-12-31',40,38,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,4,'LT-DIC-2024-D04','2024-01-20','2025-11-30',50,45,3,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,5,'LT-VIT-2024-E05','2024-02-15','2025-08-15',80,60,4,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(6,6,'LT-HID-2024-F06','2024-03-10','2026-03-10',50,40,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(7,7,'LT-OME-2024-G07','2024-01-25','2025-10-25',40,35,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(8,8,'LT-LOR-2024-H08','2024-02-20','2026-02-20',60,55,3,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(9,9,'LT-GAZ-2024-I09','2024-03-15','2027-03-15',150,120,5,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(10,10,'LT-ALG-2024-J10','2024-01-30','2027-01-30',80,65,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(11,11,'LT-JAB-2024-K11','2024-02-25','2026-08-25',100,80,4,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(12,12,'LT-TER-2024-L12','2024-03-20','2028-03-20',30,25,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(13,13,'LT-PEN-2024-M13','2024-01-10','2025-07-10',25,20,3,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(14,14,'LT-DIE-2024-N14','2024-02-05','2025-05-05',20,15,4,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(15,15,'LT-CLA-2024-O15','2024-03-01','2026-03-01',60,48,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(16,16,'LT-LID-2024-P16','2024-01-05','2026-01-05',40,30,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(17,17,'LT-RAN-2024-Q17','2024-02-01','2025-06-01',50,38,3,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(18,18,'LT-MET-2024-R18','2024-03-25','2025-09-25',60,45,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(19,19,'LT-TEN-2024-S19','2024-01-15','2028-01-15',15,12,5,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(20,20,'LT-SUE-2024-T20','2024-02-15','2025-02-15',120,90,4,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(21,21,'LT-ATR-2024-U21','2024-03-10','2026-03-10',30,25,3,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(22,22,'LT-AZI-2024-V22','2024-01-20','2025-07-20',24,18,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(23,23,'LT-BEN-2024-W23','2024-02-25','2025-08-25',40,35,4,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(24,24,'LT-CAN-2024-X24','2024-03-15','2026-03-15',35,28,2,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(25,25,'LT-MET-2024-Y25','2024-01-30','2025-07-30',60,42,1,1,'PENDIENTE',NULL,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `lotes` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_crear_control_calidad` AFTER INSERT ON `lotes` FOR EACH ROW BEGIN
    INSERT INTO control_calidad(lote_id, estado_calidad, cumple_especificaciones)
    VALUES(NEW.id, 'PENDIENTE', NULL);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `movimientos`
--

DROP TABLE IF EXISTS `movimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_movimiento_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `lote_id` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documento_referencia` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_id` int NOT NULL,
  `proveedor_id` int DEFAULT NULL,
  `fecha_movimiento` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  KEY `proveedor_id` (`proveedor_id`),
  KEY `idx_tipo_movimiento` (`tipo_movimiento_id`),
  KEY `idx_producto` (`producto_id`),
  KEY `idx_fecha_movimiento` (`fecha_movimiento`),
  KEY `idx_usuario` (`usuario_id`),
  KEY `idx_movimientos_fecha_tipo` (`fecha_movimiento`,`tipo_movimiento_id`) USING BTREE,
  CONSTRAINT `movimientos_ibfk_1` FOREIGN KEY (`tipo_movimiento_id`) REFERENCES `tipos_movimiento` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `movimientos_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `movimientos_ibfk_3` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`) ON DELETE SET NULL,
  CONSTRAINT `movimientos_ibfk_4` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `movimientos_ibfk_5` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos`
--

LOCK TABLES `movimientos` WRITE;
/*!40000 ALTER TABLE `movimientos` DISABLE KEYS */;
INSERT INTO `movimientos` VALUES (1,1,1,NULL,100,15.50,'Compra inicial de paracetamol','FC-001-2024',1,1,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(2,1,2,NULL,50,22.30,'Compra de ibuprofeno para nuevo stock','FC-002-2024',1,2,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(3,1,3,NULL,40,18.75,'Reposición de antibióticos','FC-003-2024',2,1,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(4,1,4,NULL,50,16.25,'Compra regular de antiinflamatorios','FC-004-2024',2,3,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(5,1,5,NULL,80,12.80,'Compra de vitaminas para temporada de frío','FC-005-2024',3,4,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(6,2,1,NULL,25,25.00,'Venta a farmacia local','V-001-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(7,2,3,NULL,2,32.00,'Venta a cliente particular','V-002-2024',7,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(8,2,5,NULL,20,22.50,'Venta a clínica privada','V-003-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(9,1,6,NULL,50,8.45,'Compra de productos dermatológicos','FC-006-2024',1,2,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(10,1,7,NULL,40,14.60,'Compra de gastrointestinales','FC-007-2024',2,1,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(11,2,7,NULL,5,25.00,'Venta a hospital','V-004-2024',7,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(12,1,8,NULL,60,9.20,'Compra de antihistamínicos para temporada de alergias','FC-008-2024',3,3,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(13,1,9,NULL,150,4.25,'Compra inicial de material de curación','FC-009-2024',1,5,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(14,1,10,NULL,80,7.80,'Reposición de algodón','FC-010-2024',2,2,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(15,2,9,NULL,30,8.00,'Venta a centro médico','V-005-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(16,3,10,NULL,70,0.00,'Ajuste por conteo físico - se encontró más stock','AJ-001-2024',3,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(17,2,10,NULL,10,14.50,'Venta a particular','V-006-2024',7,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(18,1,11,NULL,100,6.50,'Compra de productos de higiene','FC-011-2024',1,4,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(19,1,12,NULL,30,18.75,'Compra inicial de equipos médicos','FC-012-2024',2,1,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(20,2,11,NULL,20,12.00,'Venta a farmacia','V-007-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(21,4,2,NULL,5,38.50,'Devolución por producto defectuoso','DEV-001-2024',3,2,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(22,2,12,NULL,2,35.00,'Venta a consultorio médico','V-008-2024',7,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(23,1,13,NULL,25,25.30,'Reposición de antibióticos especiales','FC-013-2024',1,3,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(24,1,14,NULL,20,28.40,'Compra de suplementos alimenticios','FC-014-2024',2,4,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(25,2,13,NULL,5,45.00,'Venta a hospital','V-009-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(26,5,1,NULL,2,0.00,'Merma por vencimiento próximo','MER-001-2024',3,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(27,1,15,NULL,60,11.25,'Compra de antihistamínicos adicionales','FC-015-2024',1,2,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(28,2,14,NULL,5,50.00,'Venta a nutricionista','V-010-2024',7,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(29,1,16,NULL,40,15.60,'Compra de cremas dermatológicas','FC-016-2024',2,1,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(30,2,15,NULL,10,20.00,'Venta a farmacia','V-011-2024',6,NULL,'2025-12-09 15:58:41','2025-12-09 19:58:41'),(31,1,17,NULL,50,13.40,'Reposición de gastrointestinales','FC-017-2024',3,3,'2025-12-09 15:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `movimientos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_actualizar_stock_movimiento` AFTER INSERT ON `movimientos` FOR EACH ROW BEGIN
    DECLARE v_afecta_stock TINYINT;
    
    SELECT afecta_stock INTO v_afecta_stock
    FROM tipos_movimiento WHERE id = NEW.tipo_movimiento_id;
    
    IF v_afecta_stock = 1 THEN
        -- Entrada
        UPDATE productos SET stock_actual = stock_actual + NEW.cantidad,
        fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id = NEW.producto_id;
        
        IF NEW.lote_id IS NOT NULL THEN
            UPDATE lotes
            SET cantidad_actual = cantidad_actual + NEW.cantidad,
            fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id = NEW.lote_id;
        END IF;
    ELSEIF v_afecta_stock = -1 THEN
        -- Salida
        UPDATE productos
        SET stock_actual = stock_actual - NEW.cantidad,
        fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id = NEW.producto_id;
        
        IF NEW.lote_id IS NOT NULL THEN
            UPDATE lotes
            SET cantidad_actual = cantidad_actual - NEW.cantidad,
            fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id = NEW.lote_id;
        END IF;
    ELSEIF v_afecta_stock = 0 THEN
        -- Ajuste
        UPDATE productos
        SET stock_actual = NEW.cantidad,
        fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id = NEW.producto_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `categoria_id` int DEFAULT NULL,
  `unidad_medida` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `precio_venta` decimal(10,2) DEFAULT NULL,
  `stock_actual` int DEFAULT '0',
  `stock_minimo` int DEFAULT '0',
  `stock_maximo` int DEFAULT '0',
  `requiere_vencimiento` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  `ubicacion_predeterminada_id` int DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_categoria` (`categoria_id`),
  KEY `idx_stock_actual` (`stock_actual`),
  KEY `idx_ubicacion_predeterminada` (`ubicacion_predeterminada_id`),
  KEY `idx_productos_busqueda` (`nombre`,`codigo`) USING BTREE,
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE SET NULL,
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`ubicacion_predeterminada_id`) REFERENCES `ubicaciones_almacen` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'1001','Paracetamol 500mg','Tabletas de paracetamol 500mg, caja x 100',1,'caja',15.00,25.00,123,10,200,1,1,8,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,'1002','Ibuprofeno 400mg','Tabletas de ibuprofeno 400mg, caja x 50',1,'caja',20.00,35.00,85,10,150,1,1,8,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,'1003','Aspirina 100mg','Tabletas de ácido acetilsalicílico, caja x 100',1,'caja',12.00,22.00,63,10,100,1,1,9,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,'1004','Tramadol 50mg','Cápsulas de tramadol, caja x 20',1,'caja',45.00,75.00,65,5,50,1,1,9,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,'1005','Amoxicilina 500mg','Cápsulas de amoxicilina, caja x 24',2,'caja',18.00,32.00,100,15,150,1,1,10,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(6,'1006','Azitromicina 500mg','Tabletas de azitromicina, caja x 6',2,'caja',25.00,45.00,70,8,80,1,1,10,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(7,'1007','Cefalexina 500mg','Cápsulas de cefalexina, caja x 20',2,'caja',30.00,50.00,53,10,70,1,1,6,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(8,'1008','Ciprofloxacino 500mg','Tabletas de ciprofloxacino, caja x 10',2,'caja',22.00,40.00,85,10,100,1,1,6,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(9,'1009','Naproxeno 550mg','Tabletas de naproxeno, caja x 50',3,'caja',18.00,32.00,155,12,120,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(10,'1010','Diclofenaco 75mg','Tabletas de diclofenaco, caja x 30',3,'caja',16.00,28.00,60,10,100,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(11,'1011','Ketoprofeno 50mg','Cápsulas de ketoprofeno, caja x 24',3,'caja',20.00,35.00,102,8,90,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(12,'1012','Vitamina C 1000mg','Tabletas efervescentes, caja x 30',4,'caja',10.00,18.00,88,20,200,1,1,11,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(13,'1013','Vitamina D3 2000UI','Cápsulas blandas, frasco x 100',4,'frasco',25.00,45.00,60,15,150,1,1,11,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(14,'1014','Multivitamínico Completo','Tabletas multivitamínicas, frasco x 60',4,'frasco',30.00,55.00,50,10,120,1,1,12,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(15,'1015','Calcio+ Vitamina D','Tabletas de calcio 600mg, frasco x 100',4,'frasco',22.00,40.00,78,12,100,1,1,12,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(16,'1016','Vitamina B12 500mcg','Tabletas sublinguales, frasco x 60',4,'frasco',18.00,32.00,72,10,80,1,1,11,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(17,'1017','Hidrocortisona 1% Crema','Crema dermatológica, tubo x 30g',5,'unidad',8.00,15.00,95,15,150,1,1,12,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(18,'1018','Clotrimazol 1% Crema','Crema antifúngica, tubo x 20g',5,'unidad',10.00,18.00,38,12,120,1,1,12,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(19,'1019','Mupirocina 2% Pomada','Pomada antibiótica, tubo x 15g',5,'unidad',15.00,28.00,30,10,100,1,1,11,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(20,'1020','Bepanthen Crema','Crema regeneradora, tubo x 100g',5,'unidad',25.00,45.00,25,8,80,1,1,11,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(21,'1021','Omeprazol 20mg','Cápsulas de omeprazol, caja x 28',6,'caja',12.00,22.00,50,15,180,1,1,6,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(22,'1022','Ranitidina 150mg','Tabletas de ranitidina, caja x 30',6,'caja',10.00,18.00,40,12,150,1,1,6,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(23,'1023','Metoclopramida 10mg','Tabletas antiemético, caja x 20',6,'caja',8.00,15.00,35,10,120,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(24,'1024','Loperamida 2mg','Cápsulas antidiarreico, caja x 12',6,'caja',6.00,12.00,45,15,150,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(25,'1025','Loratadina 10mg','Tabletas antihistamínico, caja x 30',7,'caja',8.00,15.00,55,18,200,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(26,'1026','Cetirizina 10mg','Tabletas antihistamínico, caja x 20',7,'caja',10.00,18.00,42,15,150,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(27,'1027','Dextrometorfano 15mg','Jarabe antitusivo, frasco x 120ml',7,'frasco',12.00,22.00,30,10,100,1,1,7,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(28,'1028','Gasa Estéril 10x10cm','Paquete de 10 gasas estériles',8,'unidad',3.00,6.00,100,30,300,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(29,'1029','Venda Elástica 10cm x 5m','Venda elástica adhesiva',8,'unidad',5.00,10.00,80,25,250,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(30,'1030','Algodón 500g','Algodón hidrófilo',8,'unidad',8.00,15.00,60,20,200,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(31,'1031','Isopropílico 70% 1L','Alcohol isopropílico desinfectante',8,'unidad',12.00,22.00,50,15,150,1,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(32,'1032','Jeringas 5ml Descartables','Caja x 100 unidades',8,'caja',25.00,45.00,40,12,120,0,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(33,'1033','Guantes Látex M','Caja x 100 unidades talla M',8,'caja',20.00,35.00,35,10,100,0,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(34,'1034','Curitas Adhesivas Variadas','Caja x 100 unidades',8,'caja',8.00,15.00,70,20,200,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(35,'1035','Suero Fisiológico 1L','Solución salina 0.9%',8,'unidad',6.00,12.00,55,18,180,1,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(36,'1036','Gel Antibacterial 500ml','Gel desinfectante con 70% alcohol',8,'unidad',10.00,18.00,65,20,200,1,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(37,'1037','Termómetro Digital','Termómetro digital oral/axilar',8,'unidad',15.00,28.00,20,5,50,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(38,'1038','Mascarillas KN95','Caja x 20 mascarillas',8,'caja',35.00,60.00,30,10,100,0,1,13,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(39,'1039','Oxímetro de Pulso','Oxímetro digital portátil',8,'unidad',45.00,80.00,15,5,40,0,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(40,'1040','Tensiómetro Digital','Tensiómetro de brazo automático',8,'unidad',80.00,140.00,10,3,30,0,1,14,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(41,'1041','Producto Stock Bajo','Producto de prueba para alerta de stock bajo',1,'unidad',10.00,20.00,5,10,50,1,1,8,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(42,'1042','Producto Stock Crítico','Producto de prueba para alerta crítica',1,'unidad',10.00,20.00,0,10,50,1,1,8,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(43,'1043','Producto Próximo a Vencer','Producto de prueba para alerta de vencimiento',2,'caja',15.00,28.00,20,5,80,1,1,4,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_alerta_stock_minimo` AFTER UPDATE ON `productos` FOR EACH ROW BEGIN
    DECLARE v_tipo_alerta_id INT;
    
    IF OLD.stock_actual != NEW.stock_actual THEN
        IF NEW.stock_actual <= NEW.stock_minimo AND NEW.stock_actual > 0 THEN
            SELECT id INTO v_tipo_alerta_id FROM tipos_alerta WHERE codigo = 'stock_minimo';
            
            IF NOT EXISTS(
                SELECT 1 FROM alertas
                WHERE producto_id = NEW.id AND tipo_alerta_id = v_tipo_alerta_id AND leida = FALSE
                AND fecha_alerta >= DATE_SUB(NOW(), INTERVAL 1 DAY)
            ) THEN
                INSERT INTO alertas(tipo_alerta_id, producto_id, mensaje, nivel_prioridad)
                VALUES(
                    v_tipo_alerta_id,
                    NEW.id,
                    CONCAT('El producto "', NEW.nombre, '" ha alcanzado el stock mínimo. Stock actual: ', NEW.stock_actual, '. Stock mínimo: ', NEW.stock_minimo),
                    'alta'
                );
            END IF;
        ELSEIF NEW.stock_actual = 0 AND OLD.stock_actual > 0 THEN
            SELECT id INTO v_tipo_alerta_id FROM tipos_alerta WHERE codigo = 'stock_critico';
            
            IF NOT EXISTS(
                SELECT 1 FROM alertas
                WHERE producto_id = NEW.id AND tipo_alerta_id = v_tipo_alerta_id AND leida = FALSE
                AND fecha_alerta >= DATE_SUB(NOW(), INTERVAL 1 DAY)
            ) THEN
                INSERT INTO alertas(tipo_alerta_id, producto_id, mensaje, nivel_prioridad)
                VALUES(
                    v_tipo_alerta_id,
                    NEW.id,
                    CONCAT('¡URGENTE! El producto "', NEW.nombre, '" está agotado.'),
                    'critica'
                );
            END IF;
        ELSEIF NEW.stock_actual > NEW.stock_minimo AND OLD.stock_actual <= OLD.stock_minimo THEN
            UPDATE alertas
            SET leida = TRUE,
            fecha_lectura = NOW()
            WHERE producto_id = NEW.id AND tipo_alerta_id IN(
                SELECT id FROM tipos_alerta WHERE codigo IN('stock_minimo','stock_critico')
            )
            AND leida = FALSE;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ruc_nit` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` text COLLATE utf8mb4_unicode_ci,
  `contacto_principal` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  UNIQUE KEY `ruc_nit` (`ruc_nit`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (1,'Farmacéutica del Pacífico S.A.','FDP001','20123456789','01-234-5678','ventas@farmpacific.com','Av. Industrial 234, Lima','Juan Pérez',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,'Distribuidora Médica Nacional','DMN002','20987654321','01-876-5432','contacto@mednal.com','Jr. Comercio 567, Lima','María González',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,'Laboratorios Unidos S.R.L.','LAB003','20456789123','01-555-1234','pedidos@labunidos.com','Calle Industrial 890, Lima','Carlos Rodríguez',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,'Importadora Salud Total','IST004','20321654987','01-444-9876','ventas@saludtotal.com','Av. Principal 123, Lima','Ana Torres',1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,'Droguería Importadora SA','DIS005','20789456123','01-333-2468','info@droimport.com','Jr. Farmacia 456, Lima','Luis Martínez',1,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reportes_generados`
--

DROP TABLE IF EXISTS `reportes_generados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reportes_generados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha_generacion` datetime(6) DEFAULT NULL,
  `formato` enum('csv','excel','pdf') COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_archivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parametros` json DEFAULT NULL,
  `ruta_archivo` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_reporte` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6yv406y2q6ffy2m52l2752rko` (`usuario_id`),
  CONSTRAINT `FK6yv406y2q6ffy2m52l2752rko` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reportes_generados`
--

LOCK TABLES `reportes_generados` WRITE;
/*!40000 ALTER TABLE `reportes_generados` DISABLE KEYS */;
/*!40000 ALTER TABLE `reportes_generados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `nivel_acceso` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN','Administrador','Acceso completo al sistema',4,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(2,'GERENTE','Gerente','Puede gestionar inventario y reportes',3,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(3,'ALMACENERO','Almacenero','Puede registrar movimientos',2,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(4,'CONSULTOR','Consultor','Solo consultas',1,1,'2025-12-09 19:58:40','2025-12-09 19:58:40');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_alerta`
--

DROP TABLE IF EXISTS `tipos_alerta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_alerta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `nivel_prioridad_default` enum('baja','media','alta','critica') COLLATE utf8mb4_unicode_ci DEFAULT 'media',
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_alerta`
--

LOCK TABLES `tipos_alerta` WRITE;
/*!40000 ALTER TABLE `tipos_alerta` DISABLE KEYS */;
INSERT INTO `tipos_alerta` VALUES (1,'stock_minimo','Stock Mínimo Alcanzado','El producto ha alcanzado el stock mínimo establecido','alta',1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(2,'stock_critico','Stock Crítico','El producto está agotado o con stock muy bajo','critica',1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(3,'vencimiento_proximo','Vencimiento Próximo','El producto vence en los próximos 30 días','media',1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(4,'calidad_dudosa','Calidad Dudosa','El producto presenta características de calidad no conformes','alta',1,'2025-12-09 19:58:40','2025-12-09 19:58:40');
/*!40000 ALTER TABLE `tipos_alerta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_movimiento`
--

DROP TABLE IF EXISTS `tipos_movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_movimiento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `afecta_stock` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_movimiento`
--

LOCK TABLES `tipos_movimiento` WRITE;
/*!40000 ALTER TABLE `tipos_movimiento` DISABLE KEYS */;
INSERT INTO `tipos_movimiento` VALUES (1,'entrada','Entrada por Compra','Ingreso de productos al inventario por compra',1,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(2,'salida_venta','Salida por Venta','Salida de productos del inventario por venta',-1,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(3,'ajuste_inv','Ajuste de Inventario','Ajuste de inventario',0,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(4,'devolucion','Devolución','Devolución de productos al inventario',1,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(5,'merma','Merma/Pérdida','Pérdida de productos por caducidad o daño',-1,1,'2025-12-09 19:58:40','2025-12-09 19:58:40');
/*!40000 ALTER TABLE `tipos_movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transferencias_ubicacion`
--

DROP TABLE IF EXISTS `transferencias_ubicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transferencias_ubicacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `lote_id` int DEFAULT NULL,
  `ubicacion_origen_id` int DEFAULT NULL,
  `ubicacion_destino_id` int NOT NULL,
  `cantidad` int NOT NULL,
  `usuario_id` int NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_transferencia` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `documento_referencia` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lote_id` (`lote_id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `idx_producto` (`producto_id`),
  KEY `idx_ubicacion_origen` (`ubicacion_origen_id`),
  KEY `idx_ubicacion_destino` (`ubicacion_destino_id`),
  KEY `idx_fecha` (`fecha_transferencia`),
  KEY `idx_transferencias_producto_ubicacion` (`producto_id`,`ubicacion_origen_id`,`ubicacion_destino_id`) USING BTREE,
  CONSTRAINT `transferencias_ubicacion_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transferencias_ubicacion_ibfk_2` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`) ON DELETE SET NULL,
  CONSTRAINT `transferencias_ubicacion_ibfk_3` FOREIGN KEY (`ubicacion_origen_id`) REFERENCES `ubicaciones_almacen` (`id`) ON DELETE SET NULL,
  CONSTRAINT `transferencias_ubicacion_ibfk_4` FOREIGN KEY (`ubicacion_destino_id`) REFERENCES `ubicaciones_almacen` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transferencias_ubicacion_ibfk_5` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transferencias_ubicacion`
--

LOCK TABLES `transferencias_ubicacion` WRITE;
/*!40000 ALTER TABLE `transferencias_ubicacion` DISABLE KEYS */;
INSERT INTO `transferencias_ubicacion` VALUES (1,1,1,3,1,20,3,'Reabastecimiento de zona de rápido acceso','2025-12-09 15:58:41',NULL),(2,2,2,3,2,15,3,'Redistribución por alta demanda','2025-12-09 15:58:41',NULL),(3,3,3,3,2,10,3,'Ajuste de inventario por venta mayorista','2025-12-09 15:58:41',NULL),(4,4,4,3,1,8,3,'Preparación para venta especial','2025-12-09 15:58:41',NULL),(5,5,5,3,4,12,3,'Traslado a área de exhibición','2025-12-09 15:58:41',NULL),(6,6,6,3,5,5,3,'Reposición en mostrador de venta','2025-12-09 15:58:41',NULL),(7,7,7,3,1,7,3,'Reubicación por vencimiento próximo','2025-12-09 15:58:41',NULL),(8,8,8,3,2,18,3,'Preparación para campaña de alergias','2025-12-09 15:58:41',NULL),(9,9,9,3,6,25,3,'Reabastecimiento área de curaciones','2025-12-09 15:58:41',NULL),(10,10,10,3,6,15,3,'Traslado a zona de alto consumo','2025-12-09 15:58:41',NULL),(11,11,11,3,7,20,3,'Acomodación en área de higiene personal','2025-12-09 15:58:41',NULL),(12,12,12,3,8,3,3,'Ubicación en área de equipos médicos','2025-12-09 15:58:41',NULL),(13,13,13,3,2,4,3,'Preparación para venta a hospital','2025-12-09 15:58:41',NULL),(14,14,14,3,4,6,3,'Reubicación en área de nutrición','2025-12-09 15:58:41',NULL),(15,15,15,3,2,12,3,'Ajuste por campaña de alergias estacionales','2025-12-09 15:58:41',NULL),(16,16,16,3,5,8,3,'Reposición en mostrador de dermatología','2025-12-09 15:58:41',NULL),(17,17,17,3,1,13,3,'Reabastecimiento zona de gastrointestinales','2025-12-09 15:58:41',NULL),(18,18,18,3,1,10,3,'Ajuste por alta demanda en diabetes','2025-12-09 15:58:41',NULL),(19,19,19,3,8,2,3,'Reubicación en área de equipos médicos','2025-12-09 15:58:41',NULL),(20,20,20,3,6,18,3,'Reabastecimiento área de curaciones','2025-12-09 15:58:41',NULL),(21,1,1,1,9,5,3,'Traslado a cuarentena por posible vencimiento','2025-12-09 15:58:41',NULL),(22,22,22,3,2,6,3,'Reposición área de antibióticos','2025-12-09 15:58:41',NULL),(23,23,23,3,4,8,3,'Ubicación en área de suplementos','2025-12-09 15:58:41',NULL),(24,24,24,3,5,7,3,'Reabastecimiento mostrador dermatológico','2025-12-09 15:58:41',NULL),(25,25,25,3,1,15,3,'Reubicación zona de gastrointestinales','2025-12-09 15:58:41',NULL);
/*!40000 ALTER TABLE `transferencias_ubicacion` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_actualizar_capacidad_transferencia` AFTER INSERT ON `transferencias_ubicacion` FOR EACH ROW BEGIN
    -- Reducir capacidad origen
    IF NEW.ubicacion_origen_id IS NOT NULL THEN
        UPDATE ubicaciones_almacen
        SET capacidad_actual = capacidad_actual - NEW.cantidad
        WHERE id = NEW.ubicacion_origen_id;
    END IF;
    
    -- Aumentar capacidad destino
    UPDATE ubicaciones_almacen
    SET capacidad_actual = capacidad_actual + NEW.cantidad
    WHERE id = NEW.ubicacion_destino_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `ubicaciones_almacen`
--

DROP TABLE IF EXISTS `ubicaciones_almacen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ubicaciones_almacen` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` enum('ZONA','ESTANTERIA','RACK','PASILLO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacidad_maxima` int DEFAULT NULL,
  `capacidad_actual` int DEFAULT '0',
  `ubicacion_padre_id` int DEFAULT NULL,
  `activa` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`),
  KEY `idx_tipo` (`tipo`),
  KEY `idx_ubicacion_padre` (`ubicacion_padre_id`),
  KEY `idx_ubicaciones_completo` (`codigo`,`tipo`,`ubicacion_padre_id`) USING BTREE,
  CONSTRAINT `ubicaciones_almacen_ibfk_1` FOREIGN KEY (`ubicacion_padre_id`) REFERENCES `ubicaciones_almacen` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ubicaciones_almacen`
--

LOCK TABLES `ubicaciones_almacen` WRITE;
/*!40000 ALTER TABLE `ubicaciones_almacen` DISABLE KEYS */;
INSERT INTO `ubicaciones_almacen` VALUES (1,'ZONA-A','Zona A - Medicamentos Controlados','ZONA',1000,68,NULL,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(2,'ZONA-B','Zona B - Medicamentos Generales','ZONA',2000,65,NULL,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(3,'ZONA-C','Zona C - Material de Curación','ZONA',1500,-267,NULL,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(4,'ZONA-Q','Zona Q - Cuarentena','ZONA',500,26,NULL,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(5,'ZONA-A-E1','Estantería A1','ESTANTERIA',200,20,1,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(6,'ZONA-A-E2','Estantería A2','ESTANTERIA',200,58,1,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(7,'ZONA-A-E3','Estantería A3','ESTANTERIA',200,20,1,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(8,'ZONA-A-E1-R1','Rack A1-R1','RACK',50,5,5,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(9,'ZONA-A-E1-R2','Rack A1-R2','RACK',50,5,5,1,'2025-12-09 19:58:40','2025-12-09 19:58:41'),(10,'ZONA-A-E1-R3','Rack A1-R3','RACK',50,0,5,1,'2025-12-09 19:58:40','2025-12-09 19:58:40'),(11,'ZONA-B-E1','Estantería B1','ESTANTERIA',400,0,2,1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(12,'ZONA-B-E2','Estantería B2','ESTANTERIA',400,0,2,1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(13,'ZONA-C-E1','Estantería C1 - Gasas y Vendas','ESTANTERIA',300,0,3,1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(14,'ZONA-C-E2','Estantería C2 - Material Descartable','ESTANTERIA',300,0,3,1,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(15,'ZONA-Q-E1','Estantería Cuarentena Q1','ESTANTERIA',200,0,4,1,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `ubicaciones_almacen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol_id` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `ultimo_acceso` datetime DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_rol` (`rol_id`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin','admin@inti.com','admin123','Administrador Sistema',1,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(2,'gerente_carlos','carlos@inti.com','carlos123','Carlos Quispe',2,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(3,'gerente_maría','maria@inti.com','maria123','María Sánchez',2,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(4,'almacenero_juan','juan.a@inti.com','juan123','Juan Pérez Almacén',3,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(5,'almacenero_ana','ana.a@inti.com','ana123','Ana Gómez Almacén',3,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(6,'almacenero_carlos','carlos.a@inti.com','carlosa123','Carlos Torres Almacén',3,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41'),(7,'consultor_miguel','miguel.c@inti.com','miguel123','Miguel Fernández Consultor',4,1,NULL,'2025-12-09 19:58:41','2025-12-09 19:58:41');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vista_lotes_ubicacion`
--

DROP TABLE IF EXISTS `vista_lotes_ubicacion`;
/*!50001 DROP VIEW IF EXISTS `vista_lotes_ubicacion`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vista_lotes_ubicacion` AS SELECT 
 1 AS `id`,
 1 AS `numero_lote`,
 1 AS `producto_codigo`,
 1 AS `producto_nombre`,
 1 AS `cantidad_actual`,
 1 AS `fecha_vencimiento`,
 1 AS `estado_calidad`,
 1 AS `ubicacion_codigo`,
 1 AS `ubicacion_nombre`,
 1 AS `tipo_ubicacion`,
 1 AS `ubicacion_padre`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vista_productos_ubicacion`
--

DROP TABLE IF EXISTS `vista_productos_ubicacion`;
/*!50001 DROP VIEW IF EXISTS `vista_productos_ubicacion`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vista_productos_ubicacion` AS SELECT 
 1 AS `producto_id`,
 1 AS `codigo_producto`,
 1 AS `nombre_producto`,
 1 AS `descripcion`,
 1 AS `categoria`,
 1 AS `codigo_ubicacion`,
 1 AS `nombre_ubicacion`,
 1 AS `tipo_ubicacion`,
 1 AS `ubicacion_padre`,
 1 AS `codigo_ubicacion_producto`,
 1 AS `stock_actual`,
 1 AS `stock_minimo`,
 1 AS `stock_maximo`,
 1 AS `capacidad_actual`,
 1 AS `capacidad_maxima`,
 1 AS `requiere_vencimiento`,
 1 AS `precio_compra`,
 1 AS `precio_venta`,
 1 AS `unidad_medida`,
 1 AS `activo`*/;
SET character_set_client = @saved_cs_client;

--
-- Current Database: `inventory_system`
--

USE `inventory_system`;

--
-- Final view structure for view `vista_lotes_ubicacion`
--

/*!50001 DROP VIEW IF EXISTS `vista_lotes_ubicacion`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_lotes_ubicacion` AS select `l`.`id` AS `id`,`l`.`numero_lote` AS `numero_lote`,`p`.`codigo` AS `producto_codigo`,`p`.`nombre` AS `producto_nombre`,`l`.`cantidad_actual` AS `cantidad_actual`,`l`.`fecha_vencimiento` AS `fecha_vencimiento`,`l`.`estado_calidad` AS `estado_calidad`,`u`.`codigo` AS `ubicacion_codigo`,`u`.`nombre` AS `ubicacion_nombre`,`u`.`tipo` AS `tipo_ubicacion`,`ubp`.`nombre` AS `ubicacion_padre` from (((`lotes` `l` join `productos` `p` on((`l`.`producto_id` = `p`.`id`))) left join `ubicaciones_almacen` `u` on((`l`.`ubicacion_id` = `u`.`id`))) left join `ubicaciones_almacen` `ubp` on((`u`.`ubicacion_padre_id` = `ubp`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vista_productos_ubicacion`
--

/*!50001 DROP VIEW IF EXISTS `vista_productos_ubicacion`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_productos_ubicacion` AS select `p`.`id` AS `producto_id`,`p`.`codigo` AS `codigo_producto`,`p`.`nombre` AS `nombre_producto`,`p`.`descripcion` AS `descripcion`,`c`.`nombre` AS `categoria`,`u`.`codigo` AS `codigo_ubicacion`,`u`.`nombre` AS `nombre_ubicacion`,`u`.`tipo` AS `tipo_ubicacion`,`ubp`.`nombre` AS `ubicacion_padre`,concat(`u`.`codigo`,'-',`p`.`codigo`) AS `codigo_ubicacion_producto`,`p`.`stock_actual` AS `stock_actual`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`stock_maximo` AS `stock_maximo`,`u`.`capacidad_actual` AS `capacidad_actual`,`u`.`capacidad_maxima` AS `capacidad_maxima`,`p`.`requiere_vencimiento` AS `requiere_vencimiento`,`p`.`precio_compra` AS `precio_compra`,`p`.`precio_venta` AS `precio_venta`,`p`.`unidad_medida` AS `unidad_medida`,`p`.`activo` AS `activo` from (((`productos` `p` join `categorias` `c` on((`p`.`categoria_id` = `c`.`id`))) left join `ubicaciones_almacen` `u` on((`p`.`ubicacion_predeterminada_id` = `u`.`id`))) left join `ubicaciones_almacen` `ubp` on((`u`.`ubicacion_padre_id` = `ubp`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-09 16:23:08
