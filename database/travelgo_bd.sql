-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-09-2025 a las 20:29:51
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `travelgo_bd`
--
CREATE DATABASE IF NOT EXISTS `travelgo_bd` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `travelgo_bd`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `administradores`
--

CREATE TABLE IF NOT EXISTS `administradores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `credencial_id` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `cargo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `credencial_id` (`credencial_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alojamientos`
--

CREATE TABLE IF NOT EXISTS `alojamientos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `ciudad` varchar(100) DEFAULT NULL,
  `tipo` enum('Hotel','Hostal','Airbnb','Otro') DEFAULT NULL,
  `capacidad` int(11) DEFAULT NULL,
  `precio_noche` decimal(10,2) DEFAULT NULL,
  `viaje_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `viaje_id` (`viaje_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `credenciales`
--

CREATE TABLE IF NOT EXISTS `credenciales` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `correo` varchar(100) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `tipo_usuario` enum('usuario','admin') NOT NULL,
  `esta_activo` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `credenciales`
--

INSERT INTO `credenciales` (`id`, `correo`, `contrasena`, `tipo_usuario`, `esta_activo`) VALUES
(1, 'laura@example.com', 'clave123', 'usuario', 1),
(2, 'correo@gmail.com', 'admin123', 'admin', 1),
(3, 'correoejemplo@example.com', 'example12345', 'usuario', 1),
(4, 'ejemplo@example.com', 'example12345', 'usuario', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `documentos`
--

CREATE TABLE IF NOT EXISTS `documentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `tipo_documento` enum('VISA','PASSPORT','IDENTITY_CARD') NOT NULL,
  `numero` varchar(50) NOT NULL,
  `fecha_emision` date DEFAULT NULL,
  `fecha_expiracion` date DEFAULT NULL,
  `pais_emision` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pagos`
--

CREATE TABLE IF NOT EXISTS `pagos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reserva_id` int(11) NOT NULL,
  `metodo_pago` enum('Tarjeta','Nequi','PSE','Efectivo') DEFAULT NULL,
  `monto` decimal(10,2) DEFAULT NULL,
  `estado` enum('pendiente','pagado','fallido') DEFAULT 'pendiente',
  `fecha_pago` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reserva_id` (`reserva_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE IF NOT EXISTS `reservas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `viaje_id` int(11) NOT NULL,
  `alojamiento_id` int(11) DEFAULT NULL,
  `transporte_id` int(11) DEFAULT NULL,
  `fecha_reserva` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` enum('pendiente','confirmada','cancelada') DEFAULT 'pendiente',
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `viaje_id` (`viaje_id`),
  KEY `alojamiento_id` (`alojamiento_id`),
  KEY `transporte_id` (`transporte_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `transporte`
--

CREATE TABLE IF NOT EXISTS `transporte` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` enum('Avión','Bus','Tren','Barco') DEFAULT NULL,
  `proveedor` varchar(100) DEFAULT NULL,
  `numero_transporte` varchar(50) DEFAULT NULL,
  `salida` datetime DEFAULT NULL,
  `llegada` datetime DEFAULT NULL,
  `origen` varchar(100) DEFAULT NULL,
  `destino` varchar(100) DEFAULT NULL,
  `viaje_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `viaje_id` (`viaje_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `credencial_id` int(11) NOT NULL,
  `primerNombre` varchar(100) DEFAULT NULL,
  `primerApellido` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `nacionalidad` enum('Colombia','Mexico','Argentina','Ecuador','Peru','Bolivia','Chile','Paraguay','Uruguay','Panama','Costa_rica','Nicaragua','Honduras','Guatemala') DEFAULT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `genero` enum('MALE','FEMALE','UNSPECIFIED') DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `primer_apellido` varchar(100) DEFAULT NULL,
  `primer_nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `credencial_id` (`credencial_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `credencial_id`, `primerNombre`, `primerApellido`, `telefono`, `nacionalidad`, `fechaNacimiento`, `genero`, `fecha_nacimiento`, `primer_apellido`, `primer_nombre`) VALUES
(10, 1, 'Laura', 'Torres', '3216549870', '', '1992-07-15', '', NULL, NULL, NULL),
(11, 2, 'Alejandra', 'Paz', '3123456879', '', '2025-07-07', '', NULL, NULL, NULL),
(12, 4, 'Fernando', 'Perez', '3217897456', '', '1992-07-15', '', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `viajes`
--

CREATE TABLE IF NOT EXISTS `viajes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) DEFAULT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `destino` varchar(20) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  `cupos_disponibles` int(11) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `administradores`
--
ALTER TABLE `administradores`
  ADD CONSTRAINT `administradores_ibfk_1` FOREIGN KEY (`credencial_id`) REFERENCES `credenciales` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `alojamientos`
--
ALTER TABLE `alojamientos`
  ADD CONSTRAINT `alojamientos_ibfk_1` FOREIGN KEY (`viaje_id`) REFERENCES `viajes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `documentos`
--
ALTER TABLE `documentos`
  ADD CONSTRAINT `documentos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `pagos`
--
ALTER TABLE `pagos`
  ADD CONSTRAINT `pagos_ibfk_1` FOREIGN KEY (`reserva_id`) REFERENCES `reservas` (`id`);

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`viaje_id`) REFERENCES `viajes` (`id`),
  ADD CONSTRAINT `reservas_ibfk_3` FOREIGN KEY (`alojamiento_id`) REFERENCES `alojamientos` (`id`),
  ADD CONSTRAINT `reservas_ibfk_4` FOREIGN KEY (`transporte_id`) REFERENCES `transporte` (`id`);

--
-- Filtros para la tabla `transporte`
--
ALTER TABLE `transporte`
  ADD CONSTRAINT `transporte_ibfk_1` FOREIGN KEY (`viaje_id`) REFERENCES `viajes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`credencial_id`) REFERENCES `credenciales` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
