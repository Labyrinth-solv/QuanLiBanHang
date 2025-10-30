CREATE DATABASE  IF NOT EXISTS `mydata` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mydata`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: mydata
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chitiethoadon`
--

DROP TABLE IF EXISTS `chitiethoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chitiethoadon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mahd` int DEFAULT NULL,
  `tensp` varchar(100) DEFAULT NULL,
  `soluong` int DEFAULT NULL,
  `dongia` double DEFAULT NULL,
  `thanhtien` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mahd` (`mahd`),
  CONSTRAINT `chitiethoadon_ibfk_1` FOREIGN KEY (`mahd`) REFERENCES `hoadon` (`mahd`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chitiethoadon`
--

LOCK TABLES `chitiethoadon` WRITE;
/*!40000 ALTER TABLE `chitiethoadon` DISABLE KEYS */;
INSERT INTO `chitiethoadon` VALUES (17,9,'OOP',3,1000,3000),(18,10,'Conan',1,2000,2000),(19,11,'Conan',1,2000,2000),(20,12,'Nơi tận cùng con đường',1,10000,10000),(21,13,'Conan',1,2000,-1333.3333333333335),(22,13,'3 chàng lính ngự lâm',5,15000,71666.66666666667),(23,13,'3 chàng lính ngự lâm',5,15000,71666.66666666667),(24,14,'3 chàng lính ngự lâm',1,15000,15000),(25,14,'3 chàng lính ngự lâm',1,15000,15000),(26,14,'Đại số ',1,1000,1000),(27,15,'3 chàng lính ngự lâm',1,15000,15000),(28,15,'3 chàng lính ngự lâm',1,15000,15000),(29,15,'Đại số ',1,1000,1000),(30,15,'Đại số ',4,1000,4000),(31,16,'3 chàng lính ngự lâm',6,15000,80000),(32,17,'Nơi tận cùng con đường',7,10000,70000),(33,18,'Nơi tận cùng con đường',7,10000,70000),(34,19,'Nơi tận cùng con đường',7,10000,60000),(35,20,'Nơi tận cùng con đường',3,10000,30000),(36,20,'Nơi tận cùng con đường',5,10000,50000),(37,21,'3 chàng lính ngự lâm',1,15000,15000),(38,22,'Nơi tận cùng con đường',6,10000,60000),(39,23,'Nơi tận cùng con đường',6,10000,50000),(40,24,'Conan',1,2000,2000),(41,25,'Nơi tận cùng con đường',5,10000,50000),(42,26,'Nơi tận cùng con đường',5,10000,40000);
/*!40000 ALTER TABLE `chitiethoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `birth` date NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `shift` varchar(20) DEFAULT NULL,
  `salary` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'Nguyễn Văn Trường','2025-07-15','Nam','Ca sáng',5000),(2,'NGuyên Văn A','2025-10-01','Nam','Ca chiều',200);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoadon`
--

DROP TABLE IF EXISTS `hoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoadon` (
  `mahd` int NOT NULL AUTO_INCREMENT,
  `ngaylap` datetime DEFAULT NULL,
  `tongtien` double DEFAULT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `giamgia` double DEFAULT '0',
  PRIMARY KEY (`mahd`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoadon`
--

LOCK TABLES `hoadon` WRITE;
/*!40000 ALTER TABLE `hoadon` DISABLE KEYS */;
INSERT INTO `hoadon` VALUES (9,'2025-10-28 23:02:40',3000,NULL,0),(10,'2025-10-28 23:09:18',2000,NULL,0),(11,'2025-10-30 17:05:44',2000,'01231231',0),(12,'2025-10-30 17:06:05',10000,'0123456789',0),(13,'2025-10-30 17:46:10',152000,'01231231',10000),(14,'2025-10-30 23:00:33',31000,'0123456789',0),(15,'2025-10-30 23:00:43',35000,'0123456789',0),(16,'2025-10-30 23:00:51',90000,'0123456789',10000),(17,'2025-10-30 23:01:09',70000,'0123456789',0),(18,'2025-10-30 23:01:14',70000,'0123456789',0),(19,'2025-10-30 23:01:19',70000,'0123456789',10000),(20,'2025-10-30 23:27:24',80000,NULL,0),(21,'2025-10-30 23:29:47',15000,'0123456789',0),(22,'2025-10-30 23:30:02',60000,'0123456789',0),(23,'2025-10-30 23:30:23',50000,'0123456789',10000),(24,'2025-10-30 23:31:40',2000,NULL,0),(25,'2025-10-30 23:31:50',50000,'01231231',0),(26,'2025-10-30 23:31:56',40000,'01231231',10000);
/*!40000 ALTER TABLE `hoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khachhang`
--

DROP TABLE IF EXISTS `khachhang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khachhang` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(100) NOT NULL,
  `sdt` varchar(15) DEFAULT NULL,
  `diem` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sdt` (`sdt`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khachhang`
--

LOCK TABLES `khachhang` WRITE;
/*!40000 ALTER TABLE `khachhang` DISABLE KEYS */;
INSERT INTO `khachhang` VALUES (1,'Đức','0123456789',10),(2,'Nguyễn Văn Trường','01231231',0);
/*!40000 ALTER TABLE `khachhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sanpham`
--

DROP TABLE IF EXISTS `sanpham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanpham` (
  `id` varchar(50) NOT NULL,
  `categoryId` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sanpham`
--

LOCK TABLES `sanpham` WRITE;
/*!40000 ALTER TABLE `sanpham` DISABLE KEYS */;
INSERT INTO `sanpham` VALUES ('','','',NULL,NULL),('1','Sách bài tập','OOP',1000,96),('3','Tuyện tranh','Conan',2000,296),('4','Tiểu thuyết','Nơi tận cùng con đường',10000,90),('5','Truyện ngụ ngôn','3 chàng lính ngự lâm',15000,176),('8','Sách giáo khoa','Đại số ',1000,494);
/*!40000 ALTER TABLE `sanpham` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `account` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','admin');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-30 23:37:08
