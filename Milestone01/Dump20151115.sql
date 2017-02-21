-- MySQL dump 10.13  Distrib 5.6.27, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: seguranca2015
-- ------------------------------------------------------
-- Server version	5.6.27-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `catalogo`
--

DROP TABLE IF EXISTS `catalogo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catalogo` (
  `id_produto` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(60) NOT NULL,
  `autor` varchar(60) NOT NULL,
  `data_pub` date NOT NULL,
  `path` varchar(90) NOT NULL DEFAULT 'c:ebooks	est.txt',
  PRIMARY KEY (`id_produto`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogo`
--

LOCK TABLES `catalogo` WRITE;
/*!40000 ALTER TABLE `catalogo` DISABLE KEYS */;
INSERT INTO `catalogo` VALUES (1,'Titulo de Teste','Autor de Teste','2015-11-14','ebooks\\\\test.txt'),(2,'A Christmas Carol','Charles Dickens','2015-11-14','ebooks\\\\A-Christmas-Carol_Charles-Dickens.txt'),(3,'A Tale of Two Cities','Charles Dickens','2015-11-14','ebooks\\\\A-Tale-of-Two-Cities_Charles-Dickens.txt'),(4,'Agulha em Palheiro','Camilo Castelo Branco','2015-11-14','ebooks\\\\Agulha-em-Palheiro_Camilo-Castelo-Branco.txt'),(5,'Alices Adventures in Wonderland','Lewis Carroll','2015-11-14','ebooks\\\\Alices-Adventures-in-Wonderland_Lewis Carroll.txt'),(6,'Amor de Perdicao','Camilo Castelo Branco','2015-11-14','ebooks\\\\Amor-de-Perdicao_Camilo-Castelo-Branco.txt'),(7,'Amor de Salvacao','Camilo Castelo Branco','2015-11-14','ebooks\\\\Amor-de-Salvacao_Camilo-Castelo-Branco.txt'),(8,'Annos de Prosa','Camilo Castelo Branco','2015-11-14','ebooks\\\\Annos-de-Prosa_Camilo-Castelo-Branco.txt'),(9,'Beowulf','Lesslie Hall','2015-11-14','ebooks\\\\Beowulf_Lesslie-Hall.txt'),(10,'Carlota Angela','Camilo Castelo Branco','2015-11-14','ebooks\\\\Carlota-Angela_Camilo-Castelo-Branco.txt'),(11,'Contos dAldeia','Alberto Braga','2015-11-14','ebooks\\\\Contos-dAldeia_Alberto-Braga.txt'),(12,'Contos para a infancia','Guerra Junqueiro','2015-11-14','ebooks\\\\Contos-para-a-infancia_Guerra-Junqueiro.txt'),(13,'Desperate Choices','Jeanette Cooper','2015-11-14','ebooks\\\\Desperate-Choices_Jeanette-Cooper.txt'),(14,'Estrellas Funestas','Camilo Castelo Branco','2015-11-14','ebooks\\\\Estrellas-Funestas_Camilo-Castelo-Branco.txt'),(15,'Estrellas Propicias','Camilo Castelo Branco','2015-11-14','ebooks\\\\Estrellas-Propicias_Camilo-Castelo-Branco.txt'),(16,'Frankenstein or The Modern Prometheus','Mary Wollstonecraft','2015-11-14','ebooks\\\\Frankenstein-or-The-Modern-Prometheus_Mary-Wollstonecraft.txt'),(17,'Great Expectations','Charles Dickens','2015-11-14','ebooks\\\\Great-Expectations_Charles-Dickens.txt'),(18,'Moby Dick','Herman Melville','2015-11-14','ebooks\\\\Moby-Dick_Herman-Melville.txt'),(19,'Pride and Prejudice','Jane Austen','2015-11-14','ebooks\\\\Pride-and-Prejudice_Jane-Austen.txt'),(20,'Redemption s Warrior','Jennifer Morse and William Mortimer','2015-11-14','ebooks\\\\Redemption-s-Warrior_Jennifer-Morse-and-William-Mortimer.txt'),(21,'The Adventures of Sherlock Holmes','Arthur Conan Doyle','2015-11-14','ebooks\\\\The-Adventures-of-Sherlock-Holmes_Arthur-Conan-Doyle.txt'),(22,'The Adventures of Tom Sawyer','Mark Twain','2015-11-14','ebooks\\\\The-Adventures-of-Tom-Sawyer_Mark-Twain.txt'),(23,'The Diaries of Bunty Danvers','Patricia Ainger','2015-11-14','ebooks\\\\The-Diaries-of-Bunty-Danvers_Patricia-Ainger.txt'),(24,'The Identity Check','Ken Merrell','2015-11-14','ebooks\\\\The-Identity-Check_Ken-Merrell.txt'),(25,'The Importance of Being Earnest','Oscar Wilde','2015-11-14','ebooks\\\\The-Importance-of-Being-Earnest_Oscar-Wilde.txt'),(26,'The Picture of Dorian Gray','Oscar Wilde','2015-11-14','ebooks\\\\The-Picture-of-Dorian-Gray_Oscar-Wilde.txt'),(27,'The Yellow Wallpaper','Charlotte Perkins Gilman','2015-11-14','ebooks\\\\The-Yellow-Wallpaper_Charlotte-Perkins-Gilman.txt'),(28,'Ulysses','James Joyce','2015-11-14','ebooks\\\\Ulysses_James-Joyce.txt');
/*!40000 ALTER TABLE `catalogo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compras`
--

DROP TABLE IF EXISTS `compras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compras` (
  `id_compra` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) DEFAULT NULL,
  `id_produto` int(11) DEFAULT NULL,
  `datacompra` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4),
  `n_transferencias` int(11) NOT NULL DEFAULT '0',
  `regiao` varchar(20) NOT NULL,
  `horario_permitido_init` time NOT NULL,
  `horario_permitido_end` time NOT NULL,
  PRIMARY KEY (`id_compra`),
  KEY `id_user` (`id_user`),
  KEY `id_produto` (`id_produto`),
  CONSTRAINT `compras_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`),
  CONSTRAINT `compras_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `catalogo` (`id_produto`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compras`
--

LOCK TABLES `compras` WRITE;
/*!40000 ALTER TABLE `compras` DISABLE KEYS */;
INSERT INTO `compras` VALUES (3,27,1,'2015-11-14 19:52:12.5839',100,'Europe','00:00:00','23:59:59'),(4,27,2,'2015-11-14 22:24:20.1622',94,'Europe','00:00:00','23:59:59'),(5,28,1,'2015-11-15 22:21:39.9504',6,'Europe','00:00:00','23:59:59'),(6,28,2,'2015-11-15 22:21:48.2169',8,'Europe','00:00:00','23:59:59'),(7,28,18,'2015-11-14 20:58:28.6870',10,'Europe','00:00:00','23:59:59');
/*!40000 ALTER TABLE `compras` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER date_trigger_compras BEFORE INSERT ON compras FOR EACH ROW
    SET NEW.datacompra = current_timestamp() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `dispositivos`
--

DROP TABLE IF EXISTS `dispositivos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispositivos` (
  `id_dispositivo` int(11) NOT NULL AUTO_INCREMENT,
  `device_key` binary(32) NOT NULL,
  `location` varchar(50) NOT NULL,
  `operating_system` varchar(20) NOT NULL,
  PRIMARY KEY (`id_dispositivo`),
  UNIQUE KEY `device_key` (`device_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispositivos`
--

LOCK TABLES `dispositivos` WRITE;
/*!40000 ALTER TABLE `dispositivos` DISABLE KEYS */;
INSERT INTO `dispositivos` VALUES (2,'(õsÉöÿfH	±PkµAÂmi¬[uE¬“ÝEãgý','Greenwich Mean Time','Windows 8.1');
/*!40000 ALTER TABLE `dispositivos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispositivos_players`
--

DROP TABLE IF EXISTS `dispositivos_players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispositivos_players` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_dispositivo` int(11) NOT NULL,
  `id_player` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_dispositivo` (`id_dispositivo`),
  KEY `id_player` (`id_player`),
  CONSTRAINT `dispositivos_players_ibfk_1` FOREIGN KEY (`id_dispositivo`) REFERENCES `dispositivos` (`id_dispositivo`),
  CONSTRAINT `dispositivos_players_ibfk_2` FOREIGN KEY (`id_player`) REFERENCES `players` (`id_player`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispositivos_players`
--

LOCK TABLES `dispositivos_players` WRITE;
/*!40000 ALTER TABLE `dispositivos_players` DISABLE KEYS */;
INSERT INTO `dispositivos_players` VALUES (1,2,1);
/*!40000 ALTER TABLE `dispositivos_players` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `players` (
  `id_player` int(11) NOT NULL AUTO_INCREMENT,
  `player_key` binary(32) NOT NULL,
  `dw_date` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4),
  PRIMARY KEY (`id_player`),
  UNIQUE KEY `player_key` (`player_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `players`
--

LOCK TABLES `players` WRITE;
/*!40000 ALTER TABLE `players` DISABLE KEYS */;
INSERT INTO `players` VALUES (1,'UHÐ\0ÝçH{™ô\rÁ]`u\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2015-11-14 19:24:47.0000');
/*!40000 ALTER TABLE `players` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER date_trigger_players BEFORE INSERT ON players FOR EACH ROW
    SET NEW.dw_date = current_timestamp() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `passwd` binary(32) NOT NULL,
  `email` varchar(50) NOT NULL,
  `user_key` binary(32) NOT NULL,
  `reg_date` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4),
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `nome` (`nome`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `user_key` (`user_key`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (27,'domingo','x\0c=ïÍ8-HrÉOòžˆ ©R0ú‰õB«­F>Ù','domingo@sadas.pt',':\Zû:ûâÂ²Rà]Ò™¶\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2015-11-14 18:53:20.0000'),(28,'rita','ÅBCxk öÍ`¤ƒ±(éÒ@ …,^Ss%ôð','rita@ua.pt','bü}@×÷’O_	ÌâäþB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2015-11-14 20:57:15.0000'),(29,'testedorelatorio','ýOáŸo°†Ä_}brŠ¤ß›`´$OxÍ ˜í‚&','relatorio@seguranca.pt','øN??ST[UÎKŽÞô.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2015-11-15 22:24:41.0000');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER date_trigger_users BEFORE INSERT ON users FOR EACH ROW
    SET NEW.reg_date = current_timestamp() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping events for database 'seguranca2015'
--

--
-- Dumping routines for database 'seguranca2015'
--
/*!50003 DROP PROCEDURE IF EXISTS `changeEmail` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `changeEmail`(in _user varchar(50), in previous varchar(50), in future varchar(50))
BEGIN
	declare b boolean;
	declare id_username int;
    DECLARE counter INT;
    
    SELECT COUNT(nome) INTO counter FROM users WHERE nome=_user AND email=previous;

	if counter < 1  then
		set b = false;
	else
		UPDATE `seguranca2015`.`users` SET `email`=future WHERE `nome`=_user;
		set b = true;
	end if;
    
    select b;	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `changePw` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `changePw`(in _user varchar(50), in previous binary(32), in future binary(32))
BEGIN
	declare b boolean;
	declare id_username int;
    DECLARE counter INT;
    
    SELECT COUNT(nome) INTO counter FROM users WHERE nome=_user AND passwd=previous;

	if counter < 1  then
		set b = false;
	else
		UPDATE `seguranca2015`.`users` SET `passwd`=future WHERE `nome`=_user;
		set b = true;
	end if;
    
    select b;	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `checkOnUserCart` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkOnUserCart`(in username varchar(30), in item_id int, out b boolean)
BEGIN
	DECLARE counter INT;
    declare id_username int;
    set counter = 0;
	set id_username = (select id_user from users where nome = username);
    
    
    SELECT COUNT(compras.id_user) INTO counter FROM compras WHERE compras.id_user=id_username AND compras.id_produto=item_id;

	if counter > 0 then
		set b = true;
	else
		set b = false;
	end if;
    
    select b;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `checkPlayerExistance` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkPlayerExistance`(in pkey binary(32), out b boolean)
BEGIN
    DECLARE counter INT;
    set counter = 0;
    SELECT COUNT(players.id_player) INTO counter FROM players WHERE player_key=pkey;

	if counter > 0 then
		set b = true;
	else
		set b = false;
	end if;
    
    select b;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCatalog` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getCatalog`()
BEGIN
	SELECT * FROM catalogo;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getPath` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getPath`(in item_id int)
BEGIN
	select catalogo.path from catalogo where id_produto=item_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getUserKey` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserKey`(in username varchar(50))
BEGIN
	select user_key from users where users.nome=username;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `purchaseItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `purchaseItem`(in item_id int, in username varchar(50), out b boolean)
BEGIN
	declare id_username int;
    DECLARE counter INT;
	set id_username = (select id_user from users where nome = username);
    
    SELECT COUNT(compras.id_user) INTO counter FROM compras WHERE compras.id_user=id_username AND compras.id_produto=item_id;

	if counter < 1  then
		insert into compras (id_compra, id_user, id_produto, datacompra, n_transferencias, regiao, horario_permitido_init, horario_permitido_end)
		values (0, id_username, item_id, now(),10,'Europe','00:00:00','23:59:59');
    
		SELECT COUNT(compras.id_user) INTO counter FROM compras WHERE compras.id_user=id_username AND compras.id_produto=item_id;

		if counter > 0 then
			set b = true;
		else
			set b = false;
		end if;
	else
		set b = false;
	end if;
    
    select b;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `rebuyItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `rebuyItem`(in id_prod int, in username varchar(50))
BEGIN
	declare _id_user int;
    select id_user into _id_user from users where nome = username;
	update compras set n_transferencias = 10 where id_produto = id_prod and id_user = _id_user; 
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registerDevice` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `registerDevice`(in _device_key binary(32), in _location varchar(50), in _os varchar(20), in _player_key binary(32))
BEGIN
	declare counter int;
    declare player_id int;
    declare device_id int;
    set counter = 0;
    
    select count(id_dispositivo) into counter from dispositivos where device_key = _device_key;
    
    if counter < 1 then
		select id_player into player_id from players where players.player_key = _player_key;
		insert into dispositivos (id_dispositivo, device_key, location, operating_system) values (0, _device_key, _location, _os);
        select id_dispositivo into device_id from dispositivos where dispositivos.device_key = _device_key;
        insert into dispositivos_players (id, id_dispositivo, id_player) values (0, device_id, player_id);
    end if;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registerPlayer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `registerPlayer`(in _player_key binary(32))
BEGIN
	declare counter int;
    set counter = 0;
    
    select count(id_player) into counter from players where player_key = _player_key;
    
    if counter < 1 then
		insert into players (id_player, player_key, dw_date) values (0, _player_key, current_timestamp());
    end if;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registerProduct` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `registerProduct`(in _titulo varchar(50), in _autor varchar(30), in _data date)
BEGIN
	insert into catalogo (id_produto, titulo, autor, data_pub) values (0, _titulo, _autor, _data);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registerUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `registerUser`(IN _nome varchar(50),IN _passwd binary(32), IN _email varchar(50),IN _user_key binary(32))
insert into users (id_user, nome, passwd, email, user_key, reg_date) values (0, _nome, _passwd, _email, _user_key, current_timestamp()) ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `retrievePath` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `retrievePath`(in item_id int, in username varchar(50))
BEGIN
	declare userid int;
    declare ndw int;
    
    select id_user into userid from users where nome = username;
    
	select catalogo.path from catalogo where id_produto=item_id;
    
    update compras set n_transferencias = n_transferencias-1 where id_user=userid and id_produto = item_id;
    
    select n_transferencias into ndw from compras where id_user=userid and id_produto = item_id;
    
    if ndw <= 0 then
		call rebuyItem(item_id, username);
	end if;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `searchCatalog` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `searchCatalog`(IN keyword varchar(50))
BEGIN
	SELECT * FROM catalogo where (autor like concat(concat('%',keyword),'%') or titulo like concat(concat('%',keyword),'%')  or id_produto like concat(concat('%',keyword),'%'));
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `searchUserCatalog` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `searchUserCatalog`(in username varchar(50))
BEGIN
	declare id_username int;
	set id_username = (select id_user from users where nome = username);
    
    CREATE TEMPORARY TABLE aux (id_compra int, id_user int, id_produto int); 
    insert into aux select id_compra, id_user, id_produto from compras where id_user=id_username;
    
	select catalogo.id_produto, catalogo.titulo, catalogo.autor, catalogo.data_pub from (aux inner join catalogo on aux.id_produto=catalogo.id_produto);
    drop temporary table if exists aux;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `validateUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `validateUser`(IN _nome varchar(50), IN _passwd binary(32), out b boolean)
BEGIN
    DECLARE counter INT;
    set counter = 0;
    SELECT COUNT(users.id_user) INTO counter FROM users WHERE users.nome=_nome AND users.passwd=_passwd;

	if counter > 0 then
		set b = true;
	else
		set b = false;
	end if;
    
    select b;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-11-15 22:48:05
