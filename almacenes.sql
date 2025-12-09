-- ============================================
-- BASE DE DATOS OPTIMIZADA - INVENTORY SYSTEM
-- Sistema completo con control de calidad y ubicaciones
-- VERSIÓN CORREGIDA - MEJORADO SISTEMA DE UBICACIONES
-- ============================================

DROP DATABASE IF EXISTS inventory_system;
CREATE DATABASE inventory_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventory_system;

-- ============================================
-- TABLAS BÁSICAS (sin dependencias)
-- ============================================

-- TABLA: roles
CREATE TABLE roles(
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    nivel_acceso INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_codigo(codigo)
) ENGINE=InnoDB;

-- TABLA: categorias
CREATE TABLE categorias(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nombre(nombre)
) ENGINE=InnoDB;

-- TABLA: proveedores
CREATE TABLE proveedores(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    codigo VARCHAR(50) UNIQUE,
    ruc_nit VARCHAR(20) UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    contacto_principal VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nombre(nombre),
    INDEX idx_codigo(codigo)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: ubicaciones_almacen (DEBE CREARSE ANTES que productos)
-- ============================================

CREATE TABLE ubicaciones_almacen(
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('ZONA','ESTANTERIA','RACK','PASILLO') NOT NULL,
    capacidad_maxima INT,
    capacidad_actual INT DEFAULT 0,
    ubicacion_padre_id INT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(ubicacion_padre_id) REFERENCES ubicaciones_almacen(id) ON DELETE SET NULL,
    INDEX idx_codigo(codigo),
    INDEX idx_tipo(tipo),
    INDEX idx_ubicacion_padre(ubicacion_padre_id)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: productos (AHORA SÍ puede referenciar ubicaciones_almacen)
-- ============================================

CREATE TABLE productos(
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria_id INT,
    unidad_medida VARCHAR(20) NOT NULL,
    precio_compra DECIMAL(10,2),
    precio_venta DECIMAL(10,2),
    stock_actual INT DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    stock_maximo INT DEFAULT 0,
    requiere_vencimiento BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    ubicacion_predeterminada_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    FOREIGN KEY(ubicacion_predeterminada_id) REFERENCES ubicaciones_almacen(id) ON DELETE SET NULL,
    INDEX idx_codigo(codigo),
    INDEX idx_nombre(nombre),
    INDEX idx_categoria(categoria_id),
    INDEX idx_stock_actual(stock_actual),
    INDEX idx_ubicacion_predeterminada(ubicacion_predeterminada_id)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: usuarios (depende de roles)
-- ============================================

CREATE TABLE usuarios(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    rol_id INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso DATETIME,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(rol_id) REFERENCES roles(id) ON DELETE RESTRICT,
    INDEX idx_username(username),
    INDEX idx_email(email),
    INDEX idx_rol(rol_id)
) ENGINE=InnoDB;

-- ============================================
-- TABLA: lotes (depende de productos y proveedores)
-- ============================================

CREATE TABLE lotes(
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    numero_lote VARCHAR(50) NOT NULL,
    fecha_fabricacion DATE,
    fecha_vencimiento DATE,
    cantidad_inicial INT NOT NULL,
    cantidad_actual INT NOT NULL,
    proveedor_id INT,
    activo BOOLEAN DEFAULT TRUE,
    estado_calidad ENUM('PENDIENTE','EN_REVISION','LIBERADO','RECHAZADO','EN_CUARENTENA') DEFAULT 'PENDIENTE',
    ubicacion_id INT,
    fecha_liberacion DATETIME,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY(proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL,
    FOREIGN KEY(ubicacion_id) REFERENCES ubicaciones_almacen(id) ON DELETE SET NULL,
    INDEX idx_producto(producto_id),
    INDEX idx_numero_lote(numero_lote),
    INDEX idx_fecha_vencimiento(fecha_vencimiento),
    INDEX idx_estado_calidad(estado_calidad),
    INDEX idx_ubicacion(ubicacion_id),
    UNIQUE KEY unique_lote_producto(producto_id, numero_lote)
) ENGINE=InnoDB;

-- ============================================
-- TABLAS DE MOVIMIENTOS Y ALERTAS
-- ============================================

-- TABLA: tipos_movimiento
CREATE TABLE tipos_movimiento(
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    afecta_stock TINYINT NOT NULL COMMENT '1=Entrada, -1=Salida, 0=Ajuste',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- TABLA: movimientos
CREATE TABLE movimientos(
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento_id INT NOT NULL,
    producto_id INT NOT NULL,
    lote_id INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2),
    motivo VARCHAR(255),
    documento_referencia VARCHAR(100),
    usuario_id INT NOT NULL,
    proveedor_id INT,
    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(tipo_movimiento_id) REFERENCES tipos_movimiento(id) ON DELETE RESTRICT,
    FOREIGN KEY(producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    FOREIGN KEY(lote_id) REFERENCES lotes(id) ON DELETE SET NULL,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    FOREIGN KEY(proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL,
    INDEX idx_tipo_movimiento(tipo_movimiento_id),
    INDEX idx_producto(producto_id),
    INDEX idx_fecha_movimiento(fecha_movimiento),
    INDEX idx_usuario(usuario_id)
) ENGINE=InnoDB;

-- TABLA: tipos_alerta
CREATE TABLE tipos_alerta(
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    nivel_prioridad_default ENUM('baja','media','alta','critica') DEFAULT 'media',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- TABLA: alertas
CREATE TABLE alertas(
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_alerta_id INT NOT NULL,
    producto_id INT,
    lote_id INT,
    mensaje TEXT NOT NULL,
    nivel_prioridad ENUM('baja','media','alta','critica') DEFAULT 'media',
    leida BOOLEAN DEFAULT FALSE,
    fecha_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_lectura DATETIME,
    FOREIGN KEY(tipo_alerta_id) REFERENCES tipos_alerta(id) ON DELETE RESTRICT,
    FOREIGN KEY(producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY(lote_id) REFERENCES lotes(id) ON DELETE CASCADE,
    INDEX idx_tipo_alerta(tipo_alerta_id),
    INDEX idx_leida(leida),
    INDEX idx_fecha_alerta(fecha_alerta),
    INDEX idx_producto(producto_id)
) ENGINE=InnoDB;

-- ============================================
-- TABLAS DE CONTROL DE CALIDAD Y TRANSFERENCIAS
-- ============================================

-- TABLA: control_calidad
CREATE TABLE control_calidad(
    id INT AUTO_INCREMENT PRIMARY KEY,
    lote_id INT NOT NULL,
    estado_calidad ENUM('PENDIENTE','EN_REVISION','LIBERADO','RECHAZADO','EN_CUARENTENA') NOT NULL DEFAULT 'PENDIENTE',
    usuario_inspector_id INT,
    fecha_inspeccion DATETIME,
    fecha_liberacion DATETIME,
    observaciones TEXT,
    motivo_rechazo TEXT,
    cumple_especificaciones BOOLEAN,
    temperatura_recepcion DECIMAL(5,2),
    lote_proveedor VARCHAR(100),
    certificado_calidad VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(lote_id) REFERENCES lotes(id) ON DELETE CASCADE,
    FOREIGN KEY(usuario_inspector_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_lote(lote_id),
    INDEX idx_estado(estado_calidad),
    INDEX idx_fecha_inspeccion(fecha_inspeccion)
) ENGINE=InnoDB;

-- TABLA: transferencias_ubicacion
CREATE TABLE transferencias_ubicacion(
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    lote_id INT,
    ubicacion_origen_id INT,
    ubicacion_destino_id INT NOT NULL,
    cantidad INT NOT NULL,
    usuario_id INT NOT NULL,
    motivo VARCHAR(255),
    fecha_transferencia DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY(lote_id) REFERENCES lotes(id) ON DELETE SET NULL,
    FOREIGN KEY(ubicacion_origen_id) REFERENCES ubicaciones_almacen(id) ON DELETE SET NULL,
    FOREIGN KEY(ubicacion_destino_id) REFERENCES ubicaciones_almacen(id) ON DELETE CASCADE,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    INDEX idx_producto(producto_id),
    INDEX idx_ubicacion_origen(ubicacion_origen_id),
    INDEX idx_ubicacion_destino(ubicacion_destino_id),
    INDEX idx_fecha(fecha_transferencia)
) ENGINE=InnoDB;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER $$

-- Trigger para actualizar stock después de un movimiento
CREATE TRIGGER trg_actualizar_stock_movimiento
AFTER INSERT ON movimientos
FOR EACH ROW
BEGIN
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
END$$

-- Trigger para generar alertas de stock mínimo
CREATE TRIGGER trg_alerta_stock_minimo
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
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
END$$

-- Trigger para crear control de calidad al insertar lote
CREATE TRIGGER trg_crear_control_calidad
AFTER INSERT ON lotes
FOR EACH ROW
BEGIN
    INSERT INTO control_calidad(lote_id, estado_calidad, cumple_especificaciones)
    VALUES(NEW.id, 'PENDIENTE', NULL);
END$$

-- Trigger para actualizar estado de lote cuando cambia control de calidad
CREATE TRIGGER trg_actualizar_estado_lote
AFTER UPDATE ON control_calidad
FOR EACH ROW
BEGIN
    IF OLD.estado_calidad != NEW.estado_calidad THEN
        UPDATE lotes
        SET estado_calidad = NEW.estado_calidad,
        fecha_liberacion = IF(NEW.estado_calidad = 'LIBERADO', NOW(), NULL)
        WHERE id = NEW.lote_id;
    END IF;
END$$

-- Trigger para actualizar capacidad al transferir ubicación
CREATE TRIGGER trg_actualizar_capacidad_transferencia
AFTER INSERT ON transferencias_ubicacion
FOR EACH ROW
BEGIN
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
END$$

DELIMITER ;

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar roles
INSERT INTO roles(codigo, nombre, descripcion, nivel_acceso) VALUES 
('ADMIN', 'Administrador', 'Acceso completo al sistema', 4),
('GERENTE', 'Gerente', 'Puede gestionar inventario y reportes', 3),
('ALMACENERO', 'Almacenero', 'Puede registrar movimientos', 2),
('CONSULTOR', 'Consultor', 'Solo consultas', 1);

-- Insertar tipos de movimiento
INSERT INTO tipos_movimiento(codigo, nombre, descripcion, afecta_stock) VALUES 
('entrada', 'Entrada por Compra', 'Ingreso de productos al inventario por compra', 1),
('salida_venta', 'Salida por Venta', 'Salida de productos del inventario por venta', -1),
('ajuste_inv', 'Ajuste de Inventario', 'Ajuste de inventario', 0),
('devolucion', 'Devolución', 'Devolución de productos al inventario', 1),
('merma', 'Merma/Pérdida', 'Pérdida de productos por caducidad o daño', -1);

-- Insertar tipos de alerta
INSERT INTO tipos_alerta (codigo, nombre, descripcion, nivel_prioridad_default) VALUES
('stock_minimo', 'Stock Mínimo Alcanzado', 'El producto ha alcanzado el stock mínimo establecido', 'alta'),
('stock_critico', 'Stock Crítico', 'El producto está agotado o con stock muy bajo', 'critica'),
('vencimiento_proximo', 'Vencimiento Próximo', 'El producto vence en los próximos 30 días', 'media'),
('calidad_dudosa', 'Calidad Dudosa', 'El producto presenta características de calidad no conformes', 'alta');

-- ============================================
-- UBICACIONES DE ALMACÉN (CREAR PRIMERO)
-- ============================================

-- PRIMERO: Insertar Zonas Principales
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-A', 'Zona A - Medicamentos Controlados', 'ZONA', 1000, NULL),
('ZONA-B', 'Zona B - Medicamentos Generales', 'ZONA', 2000, NULL),
('ZONA-C', 'Zona C - Material de Curación', 'ZONA', 1500, NULL),
('ZONA-Q', 'Zona Q - Cuarentena', 'ZONA', 500, NULL);

-- SEGUNDO: Insertar Estanterías en Zona A
SET @zona_a_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A');
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-A-E1', 'Estantería A1', 'ESTANTERIA', 200, @zona_a_id),
('ZONA-A-E2', 'Estantería A2', 'ESTANTERIA', 200, @zona_a_id),
('ZONA-A-E3', 'Estantería A3', 'ESTANTERIA', 200, @zona_a_id);

-- TERCERO: Insertar Racks en Estantería A1
SET @zona_a_e1_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E1');
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-A-E1-R1', 'Rack A1-R1', 'RACK', 50, @zona_a_e1_id),
('ZONA-A-E1-R2', 'Rack A1-R2', 'RACK', 50, @zona_a_e1_id),
('ZONA-A-E1-R3', 'Rack A1-R3', 'RACK', 50, @zona_a_e1_id);

-- CUARTO: Insertar Estanterías en Zona B
SET @zona_b_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-B');
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-B-E1', 'Estantería B1', 'ESTANTERIA', 400, @zona_b_id),
('ZONA-B-E2', 'Estantería B2', 'ESTANTERIA', 400, @zona_b_id);

-- QUINTO: Insertar Estanterías en Zona C
SET @zona_c_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-C');
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-C-E1', 'Estantería C1 - Gasas y Vendas', 'ESTANTERIA', 300, @zona_c_id),
('ZONA-C-E2', 'Estantería C2 - Material Descartable', 'ESTANTERIA', 300, @zona_c_id);

-- SEXTO: Insertar Área de Cuarentena
SET @zona_q_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-Q');
INSERT INTO ubicaciones_almacen(codigo, nombre, tipo, capacidad_maxima, ubicacion_padre_id) VALUES 
('ZONA-Q-E1', 'Estantería Cuarentena Q1', 'ESTANTERIA', 200, @zona_q_id);

-- ============================================
-- 1. CATEGORÍAS
-- ============================================

INSERT INTO categorias (nombre, descripcion) VALUES
('Analgesicos', 'Medicamentos para el alivio del dolor'),
('Antibioticos', 'Medicamentos para tratar infecciones bacterianas'),
('Antiinflamatorios', 'Medicamentos para reducir la inflamación'),
('Vitaminas', 'Suplementos vitamínicos y nutricionales'),
('Dermatologicos', 'Productos para el cuidado de la piel'),
('Gastrointestinales', 'Medicamentos para problemas digestivos'),
('Antihistaminicos', 'Medicamentos para alergias estacionales'),
('Material de Curación', 'Insumos médicos para heridas y curaciones'),
('Higiene Personal', 'Productos de cuidado personal'),
('Equipos Médicos', 'Instrumentos y equipos para consulta médica');
-- ============================================
-- 2. PROVEEDORES
-- ============================================

INSERT INTO proveedores(nombre, codigo, ruc_nit, telefono, email, direccion, contacto_principal, activo) VALUES 
('Farmacéutica del Pacífico S.A.', 'FDP001', '20123456789', '01-234-5678', 'ventas@farmpacific.com', 'Av. Industrial 234, Lima', 'Juan Pérez', TRUE),
('Distribuidora Médica Nacional', 'DMN002', '20987654321', '01-876-5432', 'contacto@mednal.com', 'Jr. Comercio 567, Lima', 'María González', TRUE),
('Laboratorios Unidos S.R.L.', 'LAB003', '20456789123', '01-555-1234', 'pedidos@labunidos.com', 'Calle Industrial 890, Lima', 'Carlos Rodríguez', TRUE),
('Importadora Salud Total', 'IST004', '20321654987', '01-444-9876', 'ventas@saludtotal.com', 'Av. Principal 123, Lima', 'Ana Torres', TRUE),
('Droguería Importadora SA', 'DIS005', '20789456123', '01-333-2468', 'info@droimport.com', 'Jr. Farmacia 456, Lima', 'Luis Martínez', TRUE);

-- ============================================
-- 3. PRODUCTOS (50 productos realistas) - CÓDIGOS CORREGIDOS
-- ============================================

-- CORRECCIÓN: Mantener códigos de producto simples e inmutables
-- No incluir información de ubicación en los códigos de producto
-- La ubicación se manejará a través de la estructura jerárquica

SET @zona_a_e1_r1 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E1-R1');
SET @zona_a_e1_r2 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E1-R2');
SET @zona_a_e1_r3 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E1-R3');
SET @zona_a_e2 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E2');
SET @zona_a_e3 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-A-E3');
SET @zona_b_e1 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-B-E1');
SET @zona_b_e2 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-B-E2');
SET @zona_c_e1 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-C-E1');
SET @zona_c_e2 = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-C-E2');
SET @zona_q_id = (SELECT id FROM ubicaciones_almacen WHERE codigo = 'ZONA-Q');

INSERT INTO productos(codigo, nombre, descripcion, categoria_id, unidad_medida, precio_compra, precio_venta, stock_actual, stock_minimo, stock_maximo, requiere_vencimiento, activo, ubicacion_predeterminada_id) VALUES 
-- Analgésicos (Ubicación: ZONA-A-E1-R1)
(1001, 'Paracetamol 500mg', 'Tabletas de paracetamol 500mg, caja x 100', 1, 'caja', 15.00, 25.00, 50, 10, 200, TRUE, TRUE, @zona_a_e1_r1),
(1002, 'Ibuprofeno 400mg', 'Tabletas de ibuprofeno 400mg, caja x 50', 1, 'caja', 20.00, 35.00, 30, 10, 150, TRUE, TRUE, @zona_a_e1_r1),
(1003, 'Aspirina 100mg', 'Tabletas de ácido acetilsalicílico, caja x 100', 1, 'caja', 12.00, 22.00, 25, 10, 100, TRUE, TRUE, @zona_a_e1_r2),
(1004, 'Tramadol 50mg', 'Cápsulas de tramadol, caja x 20', 1, 'caja', 45.00, 75.00, 15, 5, 50, TRUE, TRUE, @zona_a_e1_r2),

-- Antibióticos (Ubicación: ZONA-A-E1-R3)
(1005, 'Amoxicilina 500mg', 'Cápsulas de amoxicilina, caja x 24', 2, 'caja', 18.00, 32.00, 40, 15, 150, TRUE, TRUE, @zona_a_e1_r3),
(1006, 'Azitromicina 500mg', 'Tabletas de azitromicina, caja x 6', 2, 'caja', 25.00, 45.00, 20, 8, 80, TRUE, TRUE, @zona_a_e1_r3),
(1007, 'Cefalexina 500mg', 'Cápsulas de cefalexina, caja x 20', 2, 'caja', 30.00, 50.00, 18, 10, 70, TRUE, TRUE, @zona_a_e2),
(1008, 'Ciprofloxacino 500mg', 'Tabletas de ciprofloxacino, caja x 10', 2, 'caja', 22.00, 40.00, 25, 10, 100, TRUE, TRUE, @zona_a_e2),

-- Antiinflamatorios (Ubicación: ZONA-A-E3)
(1009, 'Naproxeno 550mg', 'Tabletas de naproxeno, caja x 50', 3, 'caja', 18.00, 32.00, 35, 12, 120, TRUE, TRUE, @zona_a_e3),
(1010, 'Diclofenaco 75mg', 'Tabletas de diclofenaco, caja x 30', 3, 'caja', 16.00, 28.00, 28, 10, 100, TRUE, TRUE, @zona_a_e3),
(1011, 'Ketoprofeno 50mg', 'Cápsulas de ketoprofeno, caja x 24', 3, 'caja', 20.00, 35.00, 22, 8, 90, TRUE, TRUE, @zona_a_e3),

-- Vitaminas y Suplementos (Ubicación: ZONA-B-E1)
(1012, 'Vitamina C 1000mg', 'Tabletas efervescentes, caja x 30', 4, 'caja', 10.00, 18.00, 60, 20, 200, TRUE, TRUE, @zona_b_e1),
(1013, 'Vitamina D3 2000UI', 'Cápsulas blandas, frasco x 100', 4, 'frasco', 25.00, 45.00, 40, 15, 150, TRUE, TRUE, @zona_b_e1),
(1014, 'Multivitamínico Completo', 'Tabletas multivitamínicas, frasco x 60', 4, 'frasco', 30.00, 55.00, 35, 10, 120, TRUE, TRUE, @zona_b_e2),
(1015, 'Calcio+ Vitamina D', 'Tabletas de calcio 600mg, frasco x 100', 4, 'frasco', 22.00, 40.00, 28, 12, 100, TRUE, TRUE, @zona_b_e2),
(1016, 'Vitamina B12 500mcg', 'Tabletas sublinguales, frasco x 60', 4, 'frasco', 18.00, 32.00, 32, 10, 80, TRUE, TRUE, @zona_b_e1),

-- Dermatológicos (Ubicación: ZONA-B-E2)
(1017, 'Hidrocortisona 1% Crema', 'Crema dermatológica, tubo x 30g', 5, 'unidad', 8.00, 15.00, 45, 15, 150, TRUE, TRUE, @zona_b_e2),
(1018, 'Clotrimazol 1% Crema', 'Crema antifúngica, tubo x 20g', 5, 'unidad', 10.00, 18.00, 38, 12, 120, TRUE, TRUE, @zona_b_e2),
(1019, 'Mupirocina 2% Pomada', 'Pomada antibiótica, tubo x 15g', 5, 'unidad', 15.00, 28.00, 30, 10, 100, TRUE, TRUE, @zona_b_e1),
(1020, 'Bepanthen Crema', 'Crema regeneradora, tubo x 100g', 5, 'unidad', 25.00, 45.00, 25, 8, 80, TRUE, TRUE, @zona_b_e1),

-- Gastrointestinales (Ubicación: ZONA-A-E2)
(1021, 'Omeprazol 20mg', 'Cápsulas de omeprazol, caja x 28', 6, 'caja', 12.00, 22.00, 50, 15, 180, TRUE, TRUE, @zona_a_e2),
(1022, 'Ranitidina 150mg', 'Tabletas de ranitidina, caja x 30', 6, 'caja', 10.00, 18.00, 40, 12, 150, TRUE, TRUE, @zona_a_e2),
(1023, 'Metoclopramida 10mg', 'Tabletas antiemético, caja x 20', 6, 'caja', 8.00, 15.00, 35, 10, 120, TRUE, TRUE, @zona_a_e3),
(1024, 'Loperamida 2mg', 'Cápsulas antidiarreico, caja x 12', 6, 'caja', 6.00, 12.00, 45, 15, 150, TRUE, TRUE, @zona_a_e3),

-- Antihistamínicos (Ubicación: ZONA-A-E3)
(1025, 'Loratadina 10mg', 'Tabletas antihistamínico, caja x 30', 7, 'caja', 8.00, 15.00, 55, 18, 200, TRUE, TRUE, @zona_a_e3),
(1026, 'Cetirizina 10mg', 'Tabletas antihistamínico, caja x 20', 7, 'caja', 10.00, 18.00, 42, 15, 150, TRUE, TRUE, @zona_a_e3),
(1027, 'Dextrometorfano 15mg', 'Jarabe antitusivo, frasco x 120ml', 7, 'frasco', 12.00, 22.00, 30, 10, 100, TRUE, TRUE, @zona_a_e3),

-- Material de Curación (Ubicación: ZONA-C-E1 y ZONA-C-E2)
(1028, 'Gasa Estéril 10x10cm', 'Paquete de 10 gasas estériles', 8, 'unidad', 3.00, 6.00, 100, 30, 300, FALSE, TRUE, @zona_c_e1),
(1029, 'Venda Elástica 10cm x 5m', 'Venda elástica adhesiva', 8, 'unidad', 5.00, 10.00, 80, 25, 250, FALSE, TRUE, @zona_c_e1),
(1030, 'Algodón 500g', 'Algodón hidrófilo', 8, 'unidad', 8.00, 15.00, 60, 20, 200, FALSE, TRUE, @zona_c_e1),
(1031, 'Isopropílico 70% 1L', 'Alcohol isopropílico desinfectante', 8, 'unidad', 12.00, 22.00, 50, 15, 150, TRUE, TRUE, @zona_c_e2),
(1032, 'Jeringas 5ml Descartables', 'Caja x 100 unidades', 8, 'caja', 25.00, 45.00, 40, 12, 120, FALSE, TRUE, @zona_c_e2),
(1033, 'Guantes Látex M', 'Caja x 100 unidades talla M', 8, 'caja', 20.00, 35.00, 35, 10, 100, FALSE, TRUE, @zona_c_e2),
(1034, 'Curitas Adhesivas Variadas', 'Caja x 100 unidades', 8, 'caja', 8.00, 15.00, 70, 20, 200, FALSE, TRUE, @zona_c_e1),
(1035, 'Suero Fisiológico 1L', 'Solución salina 0.9%', 8, 'unidad', 6.00, 12.00, 55, 18, 180, TRUE, TRUE, @zona_c_e2),

-- Productos adicionales variados
(1036, 'Gel Antibacterial 500ml', 'Gel desinfectante con 70% alcohol', 8, 'unidad', 10.00, 18.00, 65, 20, 200, TRUE, TRUE, @zona_c_e2),
(1037, 'Termómetro Digital', 'Termómetro digital oral/axilar', 8, 'unidad', 15.00, 28.00, 20, 5, 50, FALSE, TRUE, @zona_c_e1),
(1038, 'Mascarillas KN95', 'Caja x 20 mascarillas', 8, 'caja', 35.00, 60.00, 30, 10, 100, FALSE, TRUE, @zona_c_e1),
(1039, 'Oxímetro de Pulso', 'Oxímetro digital portátil', 8, 'unidad', 45.00, 80.00, 15, 5, 40, FALSE, TRUE, @zona_c_e2),
(1040, 'Tensiómetro Digital', 'Tensiómetro de brazo automático', 8, 'unidad', 80.00, 140.00, 10, 3, 30, FALSE, TRUE, @zona_c_e2),

-- Productos con stock bajo o crítico (para pruebas de alertas)
(1041, 'Producto Stock Bajo', 'Producto de prueba para alerta de stock bajo', 1, 'unidad', 10.00, 20.00, 5, 10, 50, TRUE, TRUE, @zona_a_e1_r1),
(1042, 'Producto Stock Crítico', 'Producto de prueba para alerta crítica', 1, 'unidad', 10.00, 20.00, 0, 10, 50, TRUE, TRUE, @zona_a_e1_r1),
(1043, 'Producto Próximo a Vencer', 'Producto de prueba para alerta de vencimiento', 2, 'caja', 15.00, 28.00, 20, 5, 80, TRUE, TRUE, @zona_q_id);

-- ============================================
-- 4. USUARIOS
-- ============================================

INSERT INTO usuarios (username, email, password_hash, nombre_completo, rol_id, activo) VALUES
('admin', 'admin@inti.com', 'admin123', 'Administrador Sistema', 1, true),
('gerente_carlos', 'carlos@inti.com', 'carlos123', 'Carlos Quispe', 2, true),
('gerente_maría', 'maria@inti.com', 'maria123', 'María Sánchez', 2, true),
('almacenero_juan', 'juan.a@inti.com', 'juan123', 'Juan Pérez Almacén', 3, true),
('almacenero_ana', 'ana.a@inti.com', 'ana123', 'Ana Gómez Almacén', 3, true),
('almacenero_carlos', 'carlos.a@inti.com', 'carlosa123', 'Carlos Torres Almacén', 3, true),
('consultor_miguel', 'miguel.c@inti.com', 'miguel123', 'Miguel Fernández Consultor', 4, true);

-- ============================================
-- 5. LOTES (para productos con vencimiento)
-- ============================================

INSERT INTO lotes (producto_id, numero_lote, fecha_fabricacion, fecha_vencimiento, cantidad_inicial, cantidad_actual, proveedor_id) VALUES
(1, 'LT-PAR-2024-A01', '2024-01-15', '2026-01-15', 100, 75, 1),
(2, 'LT-IBU-2024-B02', '2024-02-10', '2026-02-10', 50, 42, 2),
(3, 'LT-AMI-2024-C03', '2024-03-05', '2025-12-31', 40, 38, 1),
(4, 'LT-DIC-2024-D04', '2024-01-20', '2025-11-30', 50, 45, 3),
(5, 'LT-VIT-2024-E05', '2024-02-15', '2025-08-15', 80, 60, 4),
(6, 'LT-HID-2024-F06', '2024-03-10', '2026-03-10', 50, 40, 2),
(7, 'LT-OME-2024-G07', '2024-01-25', '2025-10-25', 40, 35, 1),
(8, 'LT-LOR-2024-H08', '2024-02-20', '2026-02-20', 60, 55, 3),
(9, 'LT-GAZ-2024-I09', '2024-03-15', '2027-03-15', 150, 120, 5),
(10, 'LT-ALG-2024-J10', '2024-01-30', '2027-01-30', 80, 65, 2),
(11, 'LT-JAB-2024-K11', '2024-02-25', '2026-08-25', 100, 80, 4),
(12, 'LT-TER-2024-L12', '2024-03-20', '2028-03-20', 30, 25, 1),
(13, 'LT-PEN-2024-M13', '2024-01-10', '2025-07-10', 25, 20, 3),
(14, 'LT-DIE-2024-N14', '2024-02-05', '2025-05-05', 20, 15, 4),
(15, 'LT-CLA-2024-O15', '2024-03-01', '2026-03-01', 60, 48, 2),
(16, 'LT-LID-2024-P16', '2024-01-05', '2026-01-05', 40, 30, 1),
(17, 'LT-RAN-2024-Q17', '2024-02-01', '2025-06-01', 50, 38, 3),
(18, 'LT-MET-2024-R18', '2024-03-25', '2025-09-25', 60, 45, 2),
(19, 'LT-TEN-2024-S19', '2024-01-15', '2028-01-15', 15, 12, 5),
(20, 'LT-SUE-2024-T20', '2024-02-15', '2025-02-15', 120, 90, 4),
(21, 'LT-ATR-2024-U21', '2024-03-10', '2026-03-10', 30, 25, 3),
(22, 'LT-AZI-2024-V22', '2024-01-20', '2025-07-20', 24, 18, 1),
(23, 'LT-BEN-2024-W23', '2024-02-25', '2025-08-25', 40, 35, 4),
(24, 'LT-CAN-2024-X24', '2024-03-15', '2026-03-15', 35, 28, 2),
(25, 'LT-MET-2024-Y25', '2024-01-30', '2025-07-30', 60, 42, 1);


-- Datos para movimientos (30 ejemplos)
INSERT INTO movimientos (tipo_movimiento_id, producto_id, cantidad, precio_unitario, motivo, documento_referencia, usuario_id, proveedor_id) VALUES
(1, 1, 100, 15.50, 'Compra inicial de paracetamol', 'FC-001-2024', 1, 1),
(1, 2, 50, 22.30, 'Compra de ibuprofeno para nuevo stock', 'FC-002-2024', 1, 2),
(1, 3, 40, 18.75, 'Reposición de antibióticos', 'FC-003-2024', 2, 1),
(1, 4, 50, 16.25, 'Compra regular de antiinflamatorios', 'FC-004-2024', 2, 3),
(1, 5, 80, 12.80, 'Compra de vitaminas para temporada de frío', 'FC-005-2024', 3, 4),
(2, 1, 25, 25.00, 'Venta a farmacia local', 'V-001-2024', 6, NULL),
(2, 3, 2, 32.00, 'Venta a cliente particular', 'V-002-2024', 7, NULL),
(2, 5, 20, 22.50, 'Venta a clínica privada', 'V-003-2024', 6, NULL),
(1, 6, 50, 8.45, 'Compra de productos dermatológicos', 'FC-006-2024', 1, 2),
(1, 7, 40, 14.60, 'Compra de gastrointestinales', 'FC-007-2024', 2, 1),
(2, 7, 5, 25.00, 'Venta a hospital', 'V-004-2024', 7, NULL),
(1, 8, 60, 9.20, 'Compra de antihistamínicos para temporada de alergias', 'FC-008-2024', 3, 3),
(1, 9, 150, 4.25, 'Compra inicial de material de curación', 'FC-009-2024', 1, 5),
(1, 10, 80, 7.80, 'Reposición de algodón', 'FC-010-2024', 2, 2),
(2, 9, 30, 8.00, 'Venta a centro médico', 'V-005-2024', 6, NULL),
(3, 10, 70, 0.00, 'Ajuste por conteo físico - se encontró más stock', 'AJ-001-2024', 3, NULL),
(2, 10, 10, 14.50, 'Venta a particular', 'V-006-2024', 7, NULL),
(1, 11, 100, 6.50, 'Compra de productos de higiene', 'FC-011-2024', 1, 4),
(1, 12, 30, 18.75, 'Compra inicial de equipos médicos', 'FC-012-2024', 2, 1),
(2, 11, 20, 12.00, 'Venta a farmacia', 'V-007-2024', 6, NULL),
(4, 2, 5, 38.50, 'Devolución por producto defectuoso', 'DEV-001-2024', 3, 2),
(2, 12, 2, 35.00, 'Venta a consultorio médico', 'V-008-2024', 7, NULL),
(1, 13, 25, 25.30, 'Reposición de antibióticos especiales', 'FC-013-2024', 1, 3),
(1, 14, 20, 28.40, 'Compra de suplementos alimenticios', 'FC-014-2024', 2, 4),
(2, 13, 5, 45.00, 'Venta a hospital', 'V-009-2024', 6, NULL),
(5, 1, 2, 0.00, 'Merma por vencimiento próximo', 'MER-001-2024', 3, NULL),
(1, 15, 60, 11.25, 'Compra de antihistamínicos adicionales', 'FC-015-2024', 1, 2),
(2, 14, 5, 50.00, 'Venta a nutricionista', 'V-010-2024', 7, NULL),
(1, 16, 40, 15.60, 'Compra de cremas dermatológicas', 'FC-016-2024', 2, 1),
(2, 15, 10, 20.00, 'Venta a farmacia', 'V-011-2024', 6, NULL),
(1, 17, 50, 13.40, 'Reposición de gastrointestinales', 'FC-017-2024', 3, 3);


-- Datos para alertas (20 ejemplos)
INSERT INTO alertas (tipo_alerta_id, producto_id, mensaje, nivel_prioridad, leida) VALUES
(1, 17, 'El producto "Hidrocortisona 1% Crema" ha alcanzado el stock mínimo. Stock actual: 45. Stock mínimo: 15.', 'media', false),
(1, 25, 'El producto "Loratadina 10mg" ha alcanzado el stock mínimo. Stock actual: 55. Stock mínimo: 18.', 'media', false),
(2, 37, '¡URGENTE! El producto "Termómetro Digital" está por debajo del stock mínimo. Stock actual: 20. Stock mínimo: 5.', 'alta', false),
(3, 2, 'El lote "LT-IBU-2024-B02" del producto "Ibuprofeno 400mg" vence en 30 días (2026-02-10)', 'media', false),
(1, 40, 'El producto "Tensiómetro Digital" ha alcanzado el stock mínimo. Stock actual: 10. Stock mínimo: 3.', 'media', false),
(2, 35, '¡URGENTE! El producto "Suero Fisiológico" está por debajo del stock crítico. Stock actual: 55. Stock mínimo: 18.', 'alta', false),
(1, 23, 'El producto "Metoclopramida 10mg" ha alcanzado el stock mínimo. Stock actual: 35. Stock mínimo: 10.', 'media', false),
(3, 21, 'El lote "LT-OME-2024-G07" del producto "Omeprazol 20mg" vence en 25 días (2025-10-25)', 'media', false),
(1, 39, 'El producto "Oxímetro de Pulso" ha alcanzado el stock mínimo. Stock actual: 15. Stock mínimo: 5.', 'media', false),
(4, 24, 'El lote "LT-CAN-2024-X24" del producto "Loperamida 2mg" presenta cambios en color y textura. Requiere inspección de calidad.', 'alta', false),
(2, 40, '¡URGENTE! El producto "Tensiómetro Digital" está por debajo del stock mínimo. Stock actual: 10. Stock mínimo: 3.', 'alta', false),
(3, 5, 'El lote "LT-AMI-2024-C03" del producto "Amoxicilina 500mg" vence en 20 días (2025-12-31)', 'media', false),
(3, 1, 'El lote "LT-PAR-2024-A01" del producto "Paracetamol 500mg" vence en 45 días (2026-01-15)', 'baja', false),
(1, 31, 'El producto "Isopropílico 70% 1L" ha alcanzado el stock mínimo. Stock actual: 50. Stock mínimo: 15.', 'media', false),
(4, 17, 'El lote "LT-LID-2024-P16" del producto "Hidrocortisona 1% Crema" presenta olor anormal. Requiere inspección de calidad.', 'alta', false),
(3, 10, 'El lote "LT-DIC-2024-D04" del producto "Diclofenaco 75mg" vence en 25 días (2025-11-30)', 'media', false),
(1, 18, 'El producto "Clotrimazol 1% Crema" ha alcanzado el stock mínimo. Stock actual: 38. Stock mínimo: 12.', 'media', false);

-- Datos para transferencias de ubicación (25 ejemplos)
INSERT INTO transferencias_ubicacion (producto_id, lote_id, ubicacion_origen_id, ubicacion_destino_id, cantidad, usuario_id, motivo) VALUES
(1, 1, 3, 1, 20, 3, 'Reabastecimiento de zona de rápido acceso'),
(2, 2, 3, 2, 15, 3, 'Redistribución por alta demanda'),
(3, 3, 3, 2, 10, 3, 'Ajuste de inventario por venta mayorista'),
(4, 4, 3, 1, 8, 3, 'Preparación para venta especial'),
(5, 5, 3, 4, 12, 3, 'Traslado a área de exhibición'),
(6, 6, 3, 5, 5, 3, 'Reposición en mostrador de venta'),
(7, 7, 3, 1, 7, 3, 'Reubicación por vencimiento próximo'),
(8, 8, 3, 2, 18, 3, 'Preparación para campaña de alergias'),
(9, 9, 3, 6, 25, 3, 'Reabastecimiento área de curaciones'),
(10, 10, 3, 6, 15, 3, 'Traslado a zona de alto consumo'),
(11, 11, 3, 7, 20, 3, 'Acomodación en área de higiene personal'),
(12, 12, 3, 8, 3, 3, 'Ubicación en área de equipos médicos'),
(13, 13, 3, 2, 4, 3, 'Preparación para venta a hospital'),
(14, 14, 3, 4, 6, 3, 'Reubicación en área de nutrición'),
(15, 15, 3, 2, 12, 3, 'Ajuste por campaña de alergias estacionales'),
(16, 16, 3, 5, 8, 3, 'Reposición en mostrador de dermatología'),
(17, 17, 3, 1, 13, 3, 'Reabastecimiento zona de gastrointestinales'),
(18, 18, 3, 1, 10, 3, 'Ajuste por alta demanda en diabetes'),
(19, 19, 3, 8, 2, 3, 'Reubicación en área de equipos médicos'),
(20, 20, 3, 6, 18, 3, 'Reabastecimiento área de curaciones'),
(1, 1, 1, 9, 5, 3, 'Traslado a cuarentena por posible vencimiento'),
(22, 22, 3, 2, 6, 3, 'Reposición área de antibióticos'),
(23, 23, 3, 4, 8, 3, 'Ubicación en área de suplementos'),
(24, 24, 3, 5, 7, 3, 'Reabastecimiento mostrador dermatológico'),
(25, 25, 3, 1, 15, 3, 'Reubicación zona de gastrointestinales');


-- Datos para control de calidad (20 ejemplos)
INSERT INTO control_calidad (lote_id, estado_calidad, usuario_inspector_id, fecha_inspeccion, observaciones, cumple_especificaciones) VALUES
(1, 'LIBERADO', 1, NOW(), 'Producto en perfecto estado, sin anomalías visuales', true),
(2, 'LIBERADO', 1, NOW(), 'Cumple con todas las especificaciones de calidad', true),
(3, 'PENDIENTE', NULL, NULL, NULL, NULL),
(4, 'EN_REVISION', 2, NOW(), 'Se observa variación en el color de las tabletas, requiere análisis', NULL),
(5, 'LIBERADO', 1, NOW(), 'Vitamina C en excelente estado de conservación', true),
(6, 'EN_CUARENTENA', 2, NOW(), 'Crema presenta segregación de componentes, se envía a laboratorio', false),
(7, 'LIBERADO', 1, NOW(), 'Omeprazol cumple con especificaciones de calidad', true),
(8, 'LIBERADO', 1, NOW(), 'Antihistamínico sin anomalías detectadas', true),
(9, 'LIBERADO', 1, NOW(), 'Material de curación estéril y en buen estado', true),
(10, 'RECHAZADO', 2, NOW(), 'Algodón presenta signos de humedad y posible contaminación microbiana', false),
(11, 'LIBERADO', 1, NOW(), 'Producto de higiene cumple con especificaciones', true),
(12, 'PENDIENTE', NULL, NULL, NULL, NULL),
(13, 'LIBERADO', 1, NOW(), 'Antibiótico cumple con especificaciones de calidad', true),
(14, 'EN_REVISION', 2, NOW(), 'Se solicita análisis de contenido activo', NULL),
(15, 'LIBERADO', 1, NOW(), 'Antihistamínico sin anomalías', true),
(16, 'EN_CUARENTENA', 2, NOW(), 'Crema presenta textura diferente a la esperada, se envía a análisis', false),
(17, 'LIBERADO', 1, NOW(), 'Gastrointestinal cumple con especificaciones', true),
(18, 'PENDIENTE', NULL, NULL, NULL, NULL),
(19, 'RECHAZADO', 2, NOW(), 'Equipo presenta defectos en la calibración inicial', false),
(20, 'LIBERADO', 1, NOW(), 'Suero fisiológico cumple con especificaciones de esterilidad', true);

-- Actualizar estados de los lotes según el control de calidad
SET SQL_SAFE_UPDATES = 0;
UPDATE lotes l
JOIN control_calidad c ON l.id = c.lote_id
SET l.estado_calidad = c.estado_calidad,
l.fecha_liberacion = IF(c.estado_calidad = 'LIBERADO', c.fecha_inspeccion, NULL);
SET SQL_SAFE_UPDATES = 1;
-- ============================================
-- VISTA PARA PRODUCTOS CON UBICACIÓN COMPLETA
-- ============================================

CREATE VIEW vista_productos_ubicacion AS
SELECT 
    p.id as producto_id,
    p.codigo as codigo_producto,
    p.nombre as nombre_producto,
    p.descripcion,
    c.nombre as categoria,
    u.codigo as codigo_ubicacion,
    u.nombre as nombre_ubicacion,
    u.tipo as tipo_ubicacion,
    ubp.nombre as ubicacion_padre,
    CONCAT(u.codigo, '-', p.codigo) as codigo_ubicacion_producto,
    p.stock_actual,
    p.stock_minimo,
    p.stock_maximo,
    u.capacidad_actual,
    u.capacidad_maxima,
    p.requiere_vencimiento,
    p.precio_compra,
    p.precio_venta,
    p.unidad_medida,
    p.activo
FROM productos p
JOIN categorias c ON p.categoria_id = c.id
LEFT JOIN ubicaciones_almacen u ON p.ubicacion_predeterminada_id = u.id
LEFT JOIN ubicaciones_almacen ubp ON u.ubicacion_padre_id = ubp.id;

CREATE VIEW vista_lotes_ubicacion AS
SELECT 
    l.id,
    l.numero_lote,
    p.codigo as producto_codigo,
    p.nombre as producto_nombre,
    l.cantidad_actual,
    l.fecha_vencimiento,
    l.estado_calidad,
    u.codigo as ubicacion_codigo,
    u.nombre as ubicacion_nombre,
    u.tipo as tipo_ubicacion,
    ubp.nombre as ubicacion_padre
FROM lotes l
JOIN productos p ON l.producto_id = p.id
LEFT JOIN ubicaciones_almacen u ON l.ubicacion_id = u.id
LEFT JOIN ubicaciones_almacen ubp ON u.ubicacion_padre_id = ubp.id;

-- ============================================
-- ÍNDICES ADICIONALES
-- ============================================

CREATE INDEX idx_productos_busqueda ON productos(nombre, codigo) USING BTREE;
CREATE INDEX idx_movimientos_fecha_tipo ON movimientos(fecha_movimiento, tipo_movimiento_id) USING BTREE;
CREATE INDEX idx_lotes_vencimiento_activo ON lotes(fecha_vencimiento, activo) USING BTREE;
CREATE INDEX idx_ubicaciones_completo ON ubicaciones_almacen(codigo, tipo, ubicacion_padre_id) USING BTREE;
CREATE INDEX idx_transferencias_producto_ubicacion ON transferencias_ubicacion(producto_id, ubicacion_origen_id, ubicacion_destino_id) USING BTREE;

-- ============================================
-- VERIFICACIÓN FINAL
-- ============================================

SELECT '✅ BASE DE DATOS CREADA EXITOSAMENTE' AS mensaje;
SELECT '✅ Sistema de Ubicaciones Optimizado - Códigos mantenidos como identificadores únicos' AS mensaje;
SELECT '✅ Vistas creadas para manejar la información de ubicación' AS mensaje;

-- Verificar ubicaciones creadas
SELECT '📦 UBICACIONES CREADAS:' AS verificacion;
SELECT
    u.codigo, 
    u.nombre,
    u.tipo, 
    u.capacidad_maxima, 
    up.nombre AS ubicacion_padre
FROM ubicaciones_almacen u
LEFT JOIN ubicaciones_almacen up ON u.ubicacion_padre_id = up.id
ORDER BY u.codigo;

-- Verificar productos con ubicaciones asignadas
SELECT '📊 PRODUCTOS CON UBICACIONES ASIGNADAS:' AS verificacion;
SELECT
    p.codigo, 
    p.nombre, 
    c.nombre AS categoria, 
    u.codigo AS ubicacion_codigo, 
    u.nombre AS ubicacion_nombre
FROM productos p
LEFT JOIN categorias c ON p.categoria_id = c.id
LEFT JOIN ubicaciones_almacen u ON p.ubicacion_predeterminada_id = u.id
WHERE p.ubicacion_predeterminada_id IS NOT NULL
ORDER BY c.nombre, p.nombre;

-- Reporte de distribución final
SELECT '📈 RESUMEN FINAL POR ZONA:' AS reporte;
SELECT 
    SUBSTRING_INDEX(u.codigo, '-', 2) AS zona,
    COUNT(DISTINCT p.id) AS productos,
    COUNT(DISTINCT p.categoria_id) AS categorias,
    SUM(p.stock_actual) AS stock_total,
    ROUND(AVG(p.stock_actual), 2) AS stock_promedio
FROM productos p
INNER JOIN ubicaciones_almacen u ON p.ubicacion_predeterminada_id = u.id
GROUP BY SUBSTRING_INDEX(u.codigo, '-', 2)
ORDER BY zona;

SELECT '🎯 SISTEMA COMPLETO LISTO PARA USAR' AS resultado;

-- ============================================
-- TABLA DE BACKUPS
-- ============================================
CREATE TABLE IF NOT EXISTS backups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_archivo VARCHAR(255) NOT NULL UNIQUE,
    ruta_archivo VARCHAR(500) NOT NULL,
    tamano_bytes BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    usuario_id INT,
    tipo_backup ENUM('MANUAL', 'AUTOMATICO') NOT NULL,
    estado ENUM('EXITOSO', 'FALLIDO', 'EN_PROCESO') NOT NULL,
    descripcion VARCHAR(500),
    mensaje_error VARCHAR(1000),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_backups_fecha (fecha_creacion),
    INDEX idx_backups_estado (estado),
    INDEX idx_backups_tipo (tipo_backup)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT '✅ TABLA DE BACKUPS CREADA EXITOSAMENTE' AS mensaje;
