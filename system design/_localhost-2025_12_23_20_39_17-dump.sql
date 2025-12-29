-- MySQL dump 10.13  Distrib 9.5.0, for macos26.1 (arm64)
--
-- Host: 127.0.0.1    Database: bistro
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '234b051e-0c89-11f0-8cc9-084a5ffac902:1-99';

--
-- Table structure for table `Order`
--

DROP TABLE IF EXISTS `Order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Order` (
  `orderNumber` int NOT NULL AUTO_INCREMENT,
  `numberOfGuests` int NOT NULL,
  `conformationCode` char(36) NOT NULL,
  `orderDateTime` datetime NOT NULL,
  `placedOrderDateTime` datetime NOT NULL DEFAULT (now()),
  `orderCancelled` tinyint(1) NOT NULL DEFAULT '0',
  `orderCompleted` tinyint(1) NOT NULL DEFAULT '0',
  `orderPaid` int NOT NULL DEFAULT '0',
  `subscriberId` int DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phoneNumber` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`orderNumber`),
  KEY `Order_Subscriber_subscriberId_fk` (`subscriberId`),
  CONSTRAINT `Order_Subscriber_subscriberId_fk` FOREIGN KEY (`subscriberId`) REFERENCES `Subscriber` (`subscriberId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Order`
--

LOCK TABLES `Order` WRITE;
/*!40000 ALTER TABLE `Order` DISABLE KEYS */;
INSERT INTO `Order` (`orderNumber`, `numberOfGuests`, `conformationCode`, `orderDateTime`, `placedOrderDateTime`, `orderCancelled`, `orderCompleted`, `orderPaid`, `subscriberId`, `email`, `phoneNumber`) VALUES (1,2,'11111111-1111-1111-1111-111111111111','2025-12-20 17:00:00','2025-12-20 18:51:44',0,0,0,NULL,'a1@test.com','0500000001'),(2,4,'22222222-2222-2222-2222-222222222222','2025-12-20 17:30:00','2025-12-20 18:51:44',0,0,1,NULL,'a2@test.com','0500000002'),(3,6,'33333333-3333-3333-3333-333333333333','2025-12-20 18:00:00','2025-12-20 18:51:44',0,0,0,NULL,'a3@test.com','0500000003'),(4,2,'44444444-4444-4444-4444-444444444444','2025-12-20 18:30:00','2025-12-20 18:51:44',0,0,0,NULL,'a4@test.com','0500000004'),(5,8,'55555555-5555-5555-5555-555555555555','2025-12-20 16:00:00','2025-12-20 18:51:44',0,0,1,NULL,'a5@test.com','0500000005'),(6,4,'66666666-6666-6666-6666-666666666666','2025-12-20 19:00:00','2025-12-20 18:51:44',0,0,0,NULL,'a6@test.com','0500000006'),(7,4,'77777777-7777-7777-7777-777777777777','2025-12-20 17:00:00','2025-12-20 18:51:44',1,0,0,NULL,'x1@test.com','0500000011'),(8,2,'88888888-8888-8888-8888-888888888888','2025-12-20 18:00:00','2025-12-20 18:51:44',0,1,1,NULL,'x2@test.com','0500000012'),(9,2,'99999999-9999-9999-9999-999999999999','2025-12-21 18:00:00','2025-12-20 18:51:44',0,0,0,NULL,'n1@test.com','0500000099');
/*!40000 ALTER TABLE `Order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Subscriber`
--

DROP TABLE IF EXISTS `Subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Subscriber` (
  `subscriberId` int NOT NULL AUTO_INCREMENT,
  `email` varchar(30) NOT NULL,
  `name` varchar(30) NOT NULL,
  `phoneNumber` varchar(10) NOT NULL,
  PRIMARY KEY (`subscriberId`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phoneNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Subscriber`
--

LOCK TABLES `Subscriber` WRITE;
/*!40000 ALTER TABLE `Subscriber` DISABLE KEYS */;
INSERT INTO `Subscriber` (`subscriberId`, `email`, `name`, `phoneNumber`) VALUES (1,'noam.levi@gmail.com','Noam Levi','0501234567'),(2,'maya.cohen@gmail.com','Maya Cohen','0527654321'),(3,'itay.benari@gmail.com','Itay Ben Ari','0541122334');
/*!40000 ALTER TABLE `Subscriber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Table`
--

DROP TABLE IF EXISTS `Table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Table` (
  `tableId` int NOT NULL AUTO_INCREMENT,
  `capacity` int NOT NULL,
  `quantity` int DEFAULT NULL,
  PRIMARY KEY (`tableId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Table`
--

LOCK TABLES `Table` WRITE;
/*!40000 ALTER TABLE `Table` DISABLE KEYS */;
INSERT INTO `Table` (`tableId`, `capacity`, `quantity`) VALUES (1,2,4),(2,4,3),(3,6,2),(4,8,1);
/*!40000 ALTER TABLE `Table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Waiting`
--

DROP TABLE IF EXISTS `Waiting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Waiting` (
  `waitingNumber` int NOT NULL AUTO_INCREMENT,
  `waitingDateTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `isCurrentlyWaiting` tinyint(1) DEFAULT '1',
  `conformationCode` varchar(36) NOT NULL,
  `orderNumber` int DEFAULT NULL,
  `subscriberId` int DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phoneNumber` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`waitingNumber`),
  KEY `Waiting_Order_orderNumber_fk` (`orderNumber`),
  KEY `Waiting_Subscriber_subscriberId_fk` (`subscriberId`),
  CONSTRAINT `Waiting_Order_orderNumber_fk` FOREIGN KEY (`orderNumber`) REFERENCES `Order` (`orderNumber`),
  CONSTRAINT `Waiting_Subscriber_subscriberId_fk` FOREIGN KEY (`subscriberId`) REFERENCES `Subscriber` (`subscriberId`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Waiting`
--

LOCK TABLES `Waiting` WRITE;
/*!40000 ALTER TABLE `Waiting` DISABLE KEYS */;
INSERT INTO `Waiting` (`waitingNumber`, `waitingDateTime`, `isCurrentlyWaiting`, `conformationCode`, `orderNumber`, `subscriberId`, `email`, `phoneNumber`) VALUES (19,'2025-12-17 18:10:00',1,'9f4a1d2e-8b3c-44f1-8b9e-7a2c3d4e5f60',1,NULL,NULL,NULL),(20,'2025-12-18 19:05:00',1,'7a1c2d3e-4f50-4a60-8b70-9c80d1e2f3a4',2,NULL,NULL,NULL),(21,'2025-12-16 19:20:00',1,'11111111-2222-3333-4444-555555555555',NULL,1,NULL,NULL),(22,'2025-12-16 20:05:00',0,'66666666-7777-8888-9999-aaaaaaaaaaaa',NULL,2,NULL,NULL),(23,'2025-12-16 18:55:00',1,'b2c3d4e5-f607-489a-9b1c-2d3e4f506172',NULL,NULL,'or.giladi@gmail.com',NULL),(24,'2025-12-16 21:10:00',1,'c3d4e5f6-0718-49ab-8c2d-3e4f50617283',NULL,NULL,NULL,'0543344556');
/*!40000 ALTER TABLE `Waiting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Worker`
--

DROP TABLE IF EXISTS `Worker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Worker` (
  `workerName` varchar(30) NOT NULL,
  `workerPassword` varchar(70) NOT NULL,
  `workerEmail` varchar(30) NOT NULL,
  `isManager` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`workerName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Worker`
--

LOCK TABLES `Worker` WRITE;
/*!40000 ALTER TABLE `Worker` DISABLE KEYS */;
INSERT INTO `Worker` (`workerName`, `workerPassword`, `workerEmail`, `isManager`) VALUES ('andrei','85fb5700068fa9bc22e67342e9fddf6b85690454f7ace362b5efef03b763c593','andrei@gmail.com',1),('asaf','a0577c08d65cc092b00d75d93a23453a10e45fec813525d67b40d401a4b9fd57','asaf@gmail.com',0),('aviv ','48b9b4b7aa01d84ad4664b5604f87f314c4312283cfe6fa8113f77d0045f46cb','aviv@gmail.com',1),('shay','83e3dbdd01e0bb486cc4007c1e12a64116a410477fe640e2b4a66a93228a267f','shay@gmail.com',0);
/*!40000 ALTER TABLE `Worker` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-23 20:39:17
