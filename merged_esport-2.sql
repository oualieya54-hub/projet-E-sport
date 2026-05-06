-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 05, 2026 at 11:23 PM
-- Server version: 8.4.7
-- PHP Version: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `merged_esport`
--

-- --------------------------------------------------------

--
-- Table structure for table `bets`
--

DROP TABLE IF EXISTS `bets`;
CREATE TABLE IF NOT EXISTS `bets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `match_id` int NOT NULL,
  `bet_on_team_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `odds` decimal(6,2) NOT NULL DEFAULT '1.00',
  `potential_win` decimal(10,2) GENERATED ALWAYS AS ((`amount` * `odds`)) STORED,
  `status` enum('pending','won','lost','cancelled','refunded') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `placed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `settled_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bet_match` (`match_id`),
  KEY `idx_bet_user` (`user_id`),
  KEY `idx_bet_status` (`status`),
  KEY `fk_bet_team` (`bet_on_team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bet_transactions`
--

DROP TABLE IF EXISTS `bet_transactions`;
CREATE TABLE IF NOT EXISTS `bet_transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `bet_id` int DEFAULT NULL,
  `type` enum('deposit','withdrawal','bet_placed','bet_won','bet_refund') COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `balance_after` decimal(10,2) NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tx_user` (`user_id`),
  KEY `idx_tx_bet` (`bet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bet_wallet`
--

DROP TABLE IF EXISTS `bet_wallet`;
CREATE TABLE IF NOT EXISTS `bet_wallet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total_deposited` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total_won` decimal(10,2) NOT NULL DEFAULT '0.00',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_wallet_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
CREATE TABLE IF NOT EXISTS `booking` (
  `id_booking` int NOT NULL AUTO_INCREMENT,
  `id_session` int NOT NULL,
  `id_eleve` int NOT NULL,
  `statut_paiement` enum('en_attente','confirmé','annulé') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'en_attente',
  `date_reservation` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mode_paiement` enum('carte','virement','espèces','gratuit') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'carte',
  PRIMARY KEY (`id_booking`),
  UNIQUE KEY `uq_booking` (`id_session`,`id_eleve`),
  KEY `fk_booking_eleve` (`id_eleve`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `captains`
--

DROP TABLE IF EXISTS `captains`;
CREATE TABLE IF NOT EXISTS `captains` (
  `id` int NOT NULL,
  `jeu` enum('FIFA','LEAGUE OF LEGENDS','VALORANT','GRAN TURISMO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rang` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Bronze',
  `badges` text COLLATE utf8mb4_unicode_ci,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categorie`
--

DROP TABLE IF EXISTS `categorie`;
CREATE TABLE IF NOT EXISTS `categorie` (
  `id_categorie` int NOT NULL AUTO_INCREMENT,
  `nom_categorie` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_categorie`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categorie`
--

INSERT INTO `categorie` (`id_categorie`, `nom_categorie`, `description`) VALUES
(1, 'FPS', 'First Person Shooter'),
(2, 'MOBA', 'Multiplayer Online Battle Arena'),
(3, 'Battle Royale', 'Dernier survivant');

-- --------------------------------------------------------

--
-- Table structure for table `certification`
--

DROP TABLE IF EXISTS `certification`;
CREATE TABLE IF NOT EXISTS `certification` (
  `id_certification` int NOT NULL AUTO_INCREMENT,
  `id_eleve` int NOT NULL,
  `id_formation` int NOT NULL,
  `niveau_obtenu` enum('Bronze','Silver','Gold','Pro') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Bronze',
  `score_final` float NOT NULL DEFAULT '0',
  `date_obtention` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`id_certification`),
  UNIQUE KEY `uq_certif` (`id_eleve`,`id_formation`),
  KEY `fk_certif_formation` (`id_formation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `coaches`
--

DROP TABLE IF EXISTS `coaches`;
CREATE TABLE IF NOT EXISTS `coaches` (
  `id` int NOT NULL,
  `specialite` enum('FIFA','LEAGUE OF LEGENDS','VALORANT','GRAN TURISMO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commande`
--

DROP TABLE IF EXISTS `commande`;
CREATE TABLE IF NOT EXISTS `commande` (
  `id_commande` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `montant_total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `points_utilises` int NOT NULL DEFAULT '0',
  `points_gagnes` int NOT NULL DEFAULT '0',
  `statut` enum('en_attente','confirmée','expédiée','livrée','annulée') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'en_attente',
  `date_commande` datetime DEFAULT CURRENT_TIMESTAMP,
  `adresse_livraison` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `methode_paiement` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_commande`),
  KEY `idx_commande_user` (`id_user`),
  KEY `idx_commande_statut` (`statut`),
  KEY `idx_commande_date` (`date_commande`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Triggers `commande`
--
DROP TRIGGER IF EXISTS `after_commande_insert`;
DELIMITER $$
CREATE TRIGGER `after_commande_insert` AFTER INSERT ON `commande` FOR EACH ROW BEGIN
  UPDATE `users`
  SET    `points` = `points` + NEW.`points_gagnes` - NEW.`points_utilises`
  WHERE  `id`     = NEW.`id_user`;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `evaluation`
--

DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE IF NOT EXISTS `evaluation` (
  `id_evaluation` int NOT NULL AUTO_INCREMENT,
  `id_booking` int NOT NULL,
  `note` int NOT NULL,
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `date_eval` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_evaluation`),
  UNIQUE KEY `uq_eval_booking` (`id_booking`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
CREATE TABLE IF NOT EXISTS `events` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('LAN','Online','Meetup','Bootcamp','Watch Party','Other') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Online',
  `game` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_online` tinyint(1) NOT NULL DEFAULT '1',
  `platform_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `ticket_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('draft','published','cancelled','completed') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft',
  `thumbnail_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `organizer_id` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_event_status` (`status`),
  KEY `idx_event_type` (`type`),
  KEY `fk_event_organizer` (`organizer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `title`, `type`, `game`, `location`, `is_online`, `platform_link`, `start_date`, `end_date`, `capacity`, `ticket_price`, `status`, `thumbnail_url`, `description`, `organizer_id`, `created_at`, `updated_at`) VALUES
(1, 'EsportFest LAN 2025', 'LAN', 'Multiple', 'Palais des Congrès, Tunis', 0, NULL, '2025-08-15 09:00:00', '2025-08-17 22:00:00', 500, 25.00, 'published', NULL, 'The biggest LAN event in North Africa.', NULL, '2026-05-05 23:18:46', '2026-05-05 23:18:46'),
(2, 'Valorant Watch Party', 'Watch Party', 'Valorant', 'Online', 1, NULL, '2025-06-15 18:00:00', '2025-06-15 23:00:00', NULL, 0.00, 'published', NULL, 'Live watch party for VCT Championship.', NULL, '2026-05-05 23:18:46', '2026-05-05 23:18:46'),
(3, 'Pro Bootcamp Weekend', 'Bootcamp', 'Valorant', 'Gaming Hub Tunis', 0, NULL, '2025-07-10 08:00:00', '2025-07-12 18:00:00', 30, 60.00, 'draft', NULL, 'Intensive weekend bootcamp with pro coaches.', NULL, '2026-05-05 23:18:46', '2026-05-05 23:18:46'),
(7, 'Wahch L Gaming Event', 'LAN', 'Valorant', NULL, 1, NULL, '2026-05-09 00:00:00', '2026-05-10 00:00:00', NULL, 450.00, 'draft', NULL, NULL, NULL, '2026-05-05 23:18:46', '2026-05-05 23:18:46');

-- --------------------------------------------------------

--
-- Table structure for table `event_registrations`
--

DROP TABLE IF EXISTS `event_registrations`;
CREATE TABLE IF NOT EXISTS `event_registrations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `user_id` int NOT NULL,
  `ticket_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `attended` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_event_user` (`event_id`,`user_id`),
  UNIQUE KEY `uq_ticket` (`ticket_code`),
  KEY `idx_ereg_event` (`event_id`),
  KEY `idx_ereg_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `formation`
--

DROP TABLE IF EXISTS `formation`;
CREATE TABLE IF NOT EXISTS `formation` (
  `id_formation` int NOT NULL AUTO_INCREMENT,
  `titre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `jeu` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `niveau` enum('débutant','intermédiaire','avancé') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'débutant',
  `duree_semaines` int NOT NULL DEFAULT '4',
  `prix` float NOT NULL DEFAULT '0',
  `id_coach` int NOT NULL,
  `date_debut` date DEFAULT NULL,
  `statut` enum('active','archivée','brouillon') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'brouillon',
  `nombre_sessions` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_formation`),
  KEY `fk_formation_coach` (`id_coach`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `forums`
--

DROP TABLE IF EXISTS `forums`;
CREATE TABLE IF NOT EXISTS `forums` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `display_order` int DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `forums`
--

INSERT INTO `forums` (`id`, `name`, `description`, `icon`, `display_order`, `created_at`) VALUES
(1, 'General', 'General eSports discussion', 'general_icon', 1, '2026-05-05 21:34:36'),
(2, 'FIFA', 'FIFA tournaments and strategies', 'fifa_icon', 2, '2026-05-05 21:34:36'),
(3, 'Valorant', 'Valorant agents and tactics', 'valorant_icon', 3, '2026-05-05 21:34:36');

-- --------------------------------------------------------

--
-- Table structure for table `game`
--

DROP TABLE IF EXISTS `game`;
CREATE TABLE IF NOT EXISTS `game` (
  `id_game` int NOT NULL AUTO_INCREMENT,
  `nom_game` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `plateforme` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nb_joueurs_max` int NOT NULL,
  `id_categorie` int NOT NULL,
  PRIMARY KEY (`id_game`),
  KEY `fk_game_categorie` (`id_categorie`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `game`
--

INSERT INTO `game` (`id_game`, `nom_game`, `description`, `plateforme`, `nb_joueurs_max`, `id_categorie`) VALUES
(1, 'Valorant', 'FPS tactique', 'PC', 10, 1),
(2, 'CS:GO', 'FPS compétitif', 'PC', 10, 1),
(3, 'League of Legends', 'MOBA stratégique', 'PC', 10, 2),
(4, 'Fortnite', 'Battle Royale', 'PC/Console', 100, 3);

-- --------------------------------------------------------

--
-- Table structure for table `guests`
--

DROP TABLE IF EXISTS `guests`;
CREATE TABLE IF NOT EXISTS `guests` (
  `id` int NOT NULL,
  `coins` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
CREATE TABLE IF NOT EXISTS `likes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `target_type` enum('post','reply') COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_id` int NOT NULL,
  `vote` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_like` (`user_id`,`target_type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `membership`
--

DROP TABLE IF EXISTS `membership`;
CREATE TABLE IF NOT EXISTS `membership` (
  `id_membership` int NOT NULL AUTO_INCREMENT,
  `id_team` int NOT NULL,
  `id_user` int NOT NULL,
  `role_dans_equipe` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_membership`),
  KEY `fk_membership_team` (`id_team`),
  KEY `fk_membership_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
CREATE TABLE IF NOT EXISTS `players` (
  `id` int NOT NULL,
  `jeu` enum('FIFA','LEAGUE OF LEGENDS','VALORANT','GRAN TURISMO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rang` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Bronze',
  `badges` text COLLATE utf8mb4_unicode_ci,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  `forum_id` int NOT NULL,
  `views` int DEFAULT '0',
  `is_pinned` tinyint(1) DEFAULT '0',
  `is_locked` tinyint(1) DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_forum` (`forum_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produit`
--

DROP TABLE IF EXISTS `produit`;
CREATE TABLE IF NOT EXISTS `produit` (
  `id_produit` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `prix` decimal(10,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `statut` enum('disponible','rupture','archivé') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'disponible',
  `type_produit` enum('equipement','jersey','cle_jeu','coaching','avatar_item') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'equipement',
  `categorie` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prix_promo` decimal(10,2) DEFAULT NULL,
  `promo_debut` datetime DEFAULT NULL,
  `promo_fin` datetime DEFAULT NULL,
  `code_promo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reduction_pct` decimal(5,2) DEFAULT NULL,
  `points_gagnes` int NOT NULL DEFAULT '0',
  `date_ajout` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_produit`),
  KEY `idx_produit_statut` (`statut`),
  KEY `idx_produit_type` (`type_produit`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `produit`
--

INSERT INTO `produit` (`id_produit`, `nom`, `description`, `prix`, `stock`, `statut`, `type_produit`, `categorie`, `prix_promo`, `promo_debut`, `promo_fin`, `code_promo`, `reduction_pct`, `points_gagnes`, `date_ajout`) VALUES
(1, 'Souris Gamer ProX 25K', NULL, 79.99, 50, 'disponible', 'equipement', 'Périphériques', 59.99, '2026-05-05 09:56:33', '2026-05-06 09:56:33', NULL, NULL, 80, '2026-05-05 09:56:33'),
(2, 'Casque eSport 7.1', NULL, 129.99, 30, 'disponible', 'equipement', 'Audio', NULL, NULL, NULL, NULL, NULL, 130, '2026-05-05 09:56:33'),
(3, 'Jersey Team Viper', NULL, 49.99, 100, 'disponible', 'jersey', 'Vêtements', NULL, NULL, NULL, NULL, NULL, 50, '2026-05-05 09:56:33'),
(4, 'Clé Steam FPS Bundle', NULL, 24.99, 999, 'disponible', 'cle_jeu', 'Jeux', NULL, NULL, NULL, NULL, NULL, 25, '2026-05-05 09:56:33'),
(5, 'Session Coaching 1h', NULL, 39.99, 20, 'disponible', 'coaching', 'Coaching', NULL, NULL, NULL, NULL, NULL, 40, '2026-05-05 09:56:33'),
(6, 'Souris Gamer PRO', NULL, 49.99, 10, 'disponible', 'equipement', 'Peripherique', NULL, NULL, NULL, NULL, NULL, 50, '2026-05-05 21:09:18');

-- --------------------------------------------------------

--
-- Table structure for table `replies`
--

DROP TABLE IF EXISTS `replies`;
CREATE TABLE IF NOT EXISTS `replies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `is_approved` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`),
  KEY `idx_user_reply` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
CREATE TABLE IF NOT EXISTS `session` (
  `id_session` int NOT NULL AUTO_INCREMENT,
  `date_heure` datetime NOT NULL,
  `jeu` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prix` float NOT NULL DEFAULT '0',
  `id_coach` int NOT NULL,
  `duree_minutes` int NOT NULL DEFAULT '60',
  `capacite_max` int NOT NULL DEFAULT '10',
  `type_session` enum('individuel','groupe','atelier') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'groupe',
  `statut` enum('ouverte','complète','annulée') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ouverte',
  `id_formation` int DEFAULT NULL,
  PRIMARY KEY (`id_session`),
  KEY `fk_session_coach` (`id_coach`),
  KEY `fk_session_formation` (`id_formation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
CREATE TABLE IF NOT EXISTS `team` (
  `id_team` int NOT NULL AUTO_INCREMENT,
  `nom_equipe` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `logo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_creation` date NOT NULL,
  `id_capitaine` int NOT NULL,
  PRIMARY KEY (`id_team`),
  KEY `fk_team_capitaine` (`id_capitaine`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tournaments`
--

DROP TABLE IF EXISTS `tournaments`;
CREATE TABLE IF NOT EXISTS `tournaments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `game` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `format` enum('Single Elimination','Double Elimination','Round Robin','Swiss') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Single Elimination',
  `status` enum('upcoming','ongoing','completed','cancelled') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'upcoming',
  `max_teams` int NOT NULL DEFAULT '16',
  `prize_pool` decimal(10,2) NOT NULL DEFAULT '0.00',
  `entry_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `registration_deadline` datetime DEFAULT NULL,
  `banner_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_by` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_tournament_creator` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tournaments`
--

INSERT INTO `tournaments` (`id`, `name`, `game`, `format`, `status`, `max_teams`, `prize_pool`, `entry_fee`, `start_date`, `end_date`, `registration_deadline`, `banner_url`, `description`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 'Valorant Spring Cup 2025', 'Valorant', 'Single Elimination', 'upcoming', 16, 5000.00, 20.00, '2025-06-01 10:00:00', '2025-06-03 20:00:00', '2025-05-25 23:59:59', NULL, 'Premier Valorant tournament for amateur teams.', NULL, '2026-05-05 23:18:03', '2026-05-05 23:18:03'),
(2, 'League of Legends Pro Series', 'League of Legends', 'Double Elimination', 'completed', 8, 10000.00, 50.00, '2025-05-10 09:00:00', '2025-05-20 20:00:00', '2025-05-05 23:59:59', NULL, 'Competitive LoL series with top teams.', NULL, '2026-05-05 23:18:03', '2026-05-05 23:18:03'),
(3, 'CS2 Open Championship', 'CS2', 'Swiss', 'upcoming', 32, 2000.00, 10.00, '2025-07-01 12:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-05 23:18:03', '2026-05-05 23:18:03'),
(4, 'FIFA 26 Cup', 'FIFA26', 'Single Elimination', 'upcoming', 16, 2500.00, 22.00, '2026-04-25 09:00:00', NULL, '2026-04-23 23:00:00', NULL, NULL, NULL, '2026-05-05 23:18:03', '2026-05-05 23:18:03'),
(8, 'WAHCH L GAMING', 'League of Legends', 'Single Elimination', 'upcoming', 16, 50000.00, 10.00, '2026-05-18 00:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-05 23:18:03', '2026-05-05 23:18:03');

-- --------------------------------------------------------

--
-- Table structure for table `tournament_matches`
--

DROP TABLE IF EXISTS `tournament_matches`;
CREATE TABLE IF NOT EXISTS `tournament_matches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `round` int NOT NULL DEFAULT '1',
  `match_number` int NOT NULL,
  `team1_id` int DEFAULT NULL,
  `team2_id` int DEFAULT NULL,
  `score_team1` int NOT NULL DEFAULT '0',
  `score_team2` int NOT NULL DEFAULT '0',
  `winner_id` int DEFAULT NULL,
  `scheduled_at` datetime DEFAULT NULL,
  `played_at` datetime DEFAULT NULL,
  `status` enum('scheduled','live','completed','cancelled') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'scheduled',
  `stream_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tournament_id` (`tournament_id`),
  KEY `idx_status` (`status`),
  KEY `fk_match_team1` (`team1_id`),
  KEY `fk_match_team2` (`team2_id`),
  KEY `fk_match_winner` (`winner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tournament_registrations`
--

DROP TABLE IF EXISTS `tournament_registrations`;
CREATE TABLE IF NOT EXISTS `tournament_registrations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `team_id` int NOT NULL,
  `registered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_reg` (`tournament_id`,`team_id`),
  KEY `idx_reg_tournament` (`tournament_id`),
  KEY `idx_reg_team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE IF NOT EXISTS `transaction` (
  `id_transaction` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `id_produit` int NOT NULL,
  `id_commande` int DEFAULT NULL,
  `type` enum('panier','wishlist','achat','avis','cosmetique') COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantite` int DEFAULT '1',
  `prix_unitaire` decimal(10,2) DEFAULT NULL,
  `note` tinyint DEFAULT NULL,
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `data_json` text COLLATE utf8mb4_unicode_ci,
  `date_action` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_transaction`),
  UNIQUE KEY `uq_panier` (`id_user`,`id_produit`,`type`),
  KEY `idx_trans_user` (`id_user`),
  KEY `idx_trans_produit` (`id_produit`),
  KEY `idx_trans_commande` (`id_commande`),
  KEY `idx_trans_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pseudo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('player','captain','coach','guest','moderator','admin') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'player',
  `points` int NOT NULL DEFAULT '0',
  `last_active` datetime DEFAULT NULL,
  `is_banned` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pseudo` (`pseudo`),
  UNIQUE KEY `uq_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `posts`
--
ALTER TABLE `posts` ADD FULLTEXT KEY `idx_search` (`title`,`content`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bets`
--
ALTER TABLE `bets`
  ADD CONSTRAINT `fk_bet_match` FOREIGN KEY (`match_id`) REFERENCES `tournament_matches` (`id`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_bet_team` FOREIGN KEY (`bet_on_team_id`) REFERENCES `team` (`id_team`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_bet_wallet` FOREIGN KEY (`user_id`) REFERENCES `bet_wallet` (`user_id`) ON DELETE RESTRICT;

--
-- Constraints for table `bet_transactions`
--
ALTER TABLE `bet_transactions`
  ADD CONSTRAINT `fk_tx_bet` FOREIGN KEY (`bet_id`) REFERENCES `bets` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_tx_wallet` FOREIGN KEY (`user_id`) REFERENCES `bet_wallet` (`user_id`) ON DELETE RESTRICT;

--
-- Constraints for table `bet_wallet`
--
ALTER TABLE `bet_wallet`
  ADD CONSTRAINT `fk_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `fk_booking_eleve` FOREIGN KEY (`id_eleve`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_booking_session` FOREIGN KEY (`id_session`) REFERENCES `session` (`id_session`) ON DELETE CASCADE;

--
-- Constraints for table `captains`
--
ALTER TABLE `captains`
  ADD CONSTRAINT `fk_captains_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `certification`
--
ALTER TABLE `certification`
  ADD CONSTRAINT `fk_certif_eleve` FOREIGN KEY (`id_eleve`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_certif_formation` FOREIGN KEY (`id_formation`) REFERENCES `formation` (`id_formation`) ON DELETE CASCADE;

--
-- Constraints for table `coaches`
--
ALTER TABLE `coaches`
  ADD CONSTRAINT `fk_coaches_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `commande`
--
ALTER TABLE `commande`
  ADD CONSTRAINT `fk_commande_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluation`
--
ALTER TABLE `evaluation`
  ADD CONSTRAINT `fk_eval_booking` FOREIGN KEY (`id_booking`) REFERENCES `booking` (`id_booking`) ON DELETE CASCADE;

--
-- Constraints for table `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `fk_event_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `event_registrations`
--
ALTER TABLE `event_registrations`
  ADD CONSTRAINT `fk_ereg_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_ereg_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `formation`
--
ALTER TABLE `formation`
  ADD CONSTRAINT `fk_formation_coach` FOREIGN KEY (`id_coach`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `game`
--
ALTER TABLE `game`
  ADD CONSTRAINT `fk_game_categorie` FOREIGN KEY (`id_categorie`) REFERENCES `categorie` (`id_categorie`) ON DELETE RESTRICT;

--
-- Constraints for table `guests`
--
ALTER TABLE `guests`
  ADD CONSTRAINT `fk_guests_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `likes`
--
ALTER TABLE `likes`
  ADD CONSTRAINT `fk_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `membership`
--
ALTER TABLE `membership`
  ADD CONSTRAINT `fk_membership_team` FOREIGN KEY (`id_team`) REFERENCES `team` (`id_team`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_membership_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `players`
--
ALTER TABLE `players`
  ADD CONSTRAINT `fk_players_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `fk_posts_forum` FOREIGN KEY (`forum_id`) REFERENCES `forums` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `replies`
--
ALTER TABLE `replies`
  ADD CONSTRAINT `fk_replies_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_replies_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `session`
--
ALTER TABLE `session`
  ADD CONSTRAINT `fk_session_coach` FOREIGN KEY (`id_coach`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_session_formation` FOREIGN KEY (`id_formation`) REFERENCES `formation` (`id_formation`) ON DELETE SET NULL;

--
-- Constraints for table `team`
--
ALTER TABLE `team`
  ADD CONSTRAINT `fk_team_capitaine` FOREIGN KEY (`id_capitaine`) REFERENCES `users` (`id`) ON DELETE RESTRICT;

--
-- Constraints for table `tournaments`
--
ALTER TABLE `tournaments`
  ADD CONSTRAINT `fk_tournament_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `tournament_matches`
--
ALTER TABLE `tournament_matches`
  ADD CONSTRAINT `fk_match_team1` FOREIGN KEY (`team1_id`) REFERENCES `team` (`id_team`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_match_team2` FOREIGN KEY (`team2_id`) REFERENCES `team` (`id_team`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_match_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_match_winner` FOREIGN KEY (`winner_id`) REFERENCES `team` (`id_team`) ON DELETE SET NULL;

--
-- Constraints for table `tournament_registrations`
--
ALTER TABLE `tournament_registrations`
  ADD CONSTRAINT `fk_reg_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id_team`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reg_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `fk_trans_commande` FOREIGN KEY (`id_commande`) REFERENCES `commande` (`id_commande`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_trans_produit` FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_trans_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
