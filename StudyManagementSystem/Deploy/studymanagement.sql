-- MySQL dump 10.13  Distrib 8.0.18, for macos10.14 (x86_64)
--
-- Host: localhost    Database: studymanagement
-- ------------------------------------------------------
-- Server version	5.6.50

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

CREATE database `studymanagement`;

USE `studymanagement`;

--
-- Table structure for table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conditions` (
  `condition_name` varchar(100) NOT NULL,
  `condition_key` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`condition_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `school`
--

DROP TABLE IF EXISTS `school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school` (
  `study_school_key` int(11) NOT NULL AUTO_INCREMENT,
  `study_key` int(11) DEFAULT NULL,
  `school_study_anon_prefix` varchar(100) DEFAULT NULL,
  `pretest` varchar(100) DEFAULT NULL,
  `intervention_from` varchar(100) DEFAULT NULL,
  `intervention_to` varchar(100) DEFAULT NULL,
  `posttest` varchar(100) DEFAULT NULL,
  `delayedtest` varchar(100) DEFAULT NULL,
  `windowslog_dir` varchar(100) DEFAULT NULL,
  `maclog_dir` varchar(100) DEFAULT NULL,
  `datasetname` varchar(100) DEFAULT NULL,
  `schoolname` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`study_school_key`),
  KEY `study_key` (`study_key`),
  CONSTRAINT `school_ibfk_1` FOREIGN KEY (`study_key`) REFERENCES `study` (`study_key`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shortid_table`
--

DROP TABLE IF EXISTS `shortid_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shortid_table` (
  `studentid` varchar(100) NOT NULL,
  `study_school_key` int(11) DEFAULT NULL,
  `teacher_key` int(11) DEFAULT NULL,
  `conditions` varchar(100) DEFAULT NULL,
  `pretest_version` varchar(100) DEFAULT NULL,
  `posttest_version` varchar(100) DEFAULT NULL,
  `delayedtest_version` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`studentid`),
  KEY `study_school_key` (`study_school_key`),
  KEY `teacher_key` (`teacher_key`),
  CONSTRAINT `shortid_table_ibfk_1` FOREIGN KEY (`study_school_key`) REFERENCES `school` (`study_school_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `shortid_table_ibfk_2` FOREIGN KEY (`teacher_key`) REFERENCES `teacher` (`teacher_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study` (
  `study_key` int(11) NOT NULL AUTO_INCREMENT,
  `creation_time` date NOT NULL,
  `study_name` varchar(100) NOT NULL,
  `level_of_assignment` varchar(100) NOT NULL,
  PRIMARY KEY (`study_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `study_condition_mapping`
--

DROP TABLE IF EXISTS `study_condition_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_condition_mapping` (
  `study_key` int(11) NOT NULL,
  `condition_key` int(11) NOT NULL,
  KEY `study_key` (`study_key`),
  KEY `condition_key` (`condition_key`),
  CONSTRAINT `study_condition_mapping_ibfk_1` FOREIGN KEY (`study_key`) REFERENCES `study` (`study_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `study_condition_mapping_ibfk_2` FOREIGN KEY (`condition_key`) REFERENCES `conditions` (`condition_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `study_school_key` int(11) DEFAULT NULL,
  `teacher_key` int(11) NOT NULL AUTO_INCREMENT,
  `teacher` varchar(100) DEFAULT NULL,
  `class_name` varchar(100) DEFAULT NULL,
  `no_of_students` int(11) DEFAULT NULL,
  PRIMARY KEY (`teacher_key`),
  KEY `study_school_key` (`study_school_key`),
  CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`study_school_key`) REFERENCES `school` (`study_school_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'studymanagement'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-12 11:09:16
