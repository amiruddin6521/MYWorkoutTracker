-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 03, 2019 at 11:28 AM
-- Server version: 10.1.39-MariaDB
-- PHP Version: 7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `id9626094_mwtdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `body_building`
--

CREATE TABLE `body_building` (
  `_id` int(5) NOT NULL,
  `bDate` date NOT NULL,
  `bTime` time NOT NULL,
  `sets` int(5) NOT NULL,
  `reps` int(5) NOT NULL,
  `weight` double(10,1) NOT NULL,
  `machine_id` int(5) NOT NULL,
  `user_id` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `body_building`
--

INSERT INTO `body_building` (`_id`, `bDate`, `bTime`, `sets`, `reps`, `weight`, `machine_id`, `user_id`) VALUES
(4, '2019-05-14', '08:45:30', 2, 5, 20.5, 1, 1),
(5, '2019-05-15', '09:05:40', 3, 3, 6.0, 1, 1),
(6, '2019-05-15', '15:18:47', 4, 4, 7.0, 1, 1),
(7, '2019-05-15', '15:29:05', 2, 2, 7.0, 1, 1),
(8, '2019-05-15', '00:56:17', 1, 3, 10.0, 1, 1),
(9, '2019-05-15', '01:00:01', 2, 3, 15.0, 1, 1),
(11, '2019-05-15', '01:06:21', 3, 3, 5.0, 1, 1),
(12, '2019-05-15', '01:06:35', 3, 3, 5.0, 1, 1),
(13, '2019-05-15', '01:16:41', 1, 1, 5.0, 1, 1),
(14, '2019-05-15', '01:33:01', 2, 1, 5.0, 1, 1),
(15, '2019-05-16', '02:28:38', 1, 3, 5.0, 1, 1),
(16, '2019-05-16', '02:32:48', 1, 1, 5.0, 1, 1),
(17, '2019-05-16', '02:34:14', 5, 6, 7.0, 1, 1),
(18, '2019-05-16', '02:34:22', 5, 6, 9.0, 1, 1),
(19, '2019-05-16', '02:34:33', 5, 6, 12.0, 1, 1),
(20, '2019-05-16', '05:41:56', 1, 1, 5.0, 1, 1),
(21, '2019-05-16', '06:41:03', 2, 2, 2.0, 1, 1),
(22, '2019-05-16', '22:21:03', 1, 1, 5.0, 1, 1),
(23, '2019-05-16', '22:33:19', 2, 3, 5.0, 1, 1),
(26, '2019-05-19', '17:11:18', 1, 1, 5.0, 1, 1),
(30, '2019-05-21', '12:18:15', 1, 3, 5.0, 1, 1),
(32, '2019-05-26', '01:31:55', 1, 3, 10.0, 1, 1),
(33, '2019-08-11', '23:44:49', 1, 3, 5.0, 4, 2),
(34, '2019-08-12', '23:45:07', 1, 3, 7.0, 4, 2),
(35, '2019-08-13', '23:45:34', 1, 5, 5.0, 4, 2),
(36, '2019-08-14', '23:45:44', 1, 5, 6.0, 4, 2),
(37, '2019-08-15', '23:45:58', 1, 4, 7.0, 4, 2),
(38, '2019-08-16', '23:47:05', 1, 5, 8.0, 4, 2),
(39, '2019-08-17', '23:47:50', 1, 5, 6.5, 4, 2),
(45, '2019-08-19', '09:26:10', 1, 15, 4.5, 4, 2);

-- --------------------------------------------------------

--
-- Table structure for table `body_track`
--

CREATE TABLE `body_track` (
  `_id` int(25) NOT NULL,
  `btDate` date NOT NULL,
  `bodypart` int(25) NOT NULL,
  `bodymeasure` double(10,1) NOT NULL,
  `user_id` int(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `body_track`
--

INSERT INTO `body_track` (`_id`, `btDate`, `bodypart`, `bodymeasure`, `user_id`) VALUES
(1, '2019-05-25', 0, 50.0, 1),
(2, '2019-05-25', 1, 35.0, 1),
(3, '2019-05-24', 0, 25.0, 1),
(7, '2019-05-25', 2, 10.0, 1),
(8, '2019-05-26', 0, 75.0, 1),
(9, '2019-08-13', 0, 10.0, 2),
(10, '2019-08-14', 0, 11.0, 2),
(11, '2019-08-15', 0, 13.0, 2),
(12, '2019-08-13', 1, 10.0, 2),
(13, '2019-08-14', 1, 13.0, 2),
(14, '2019-08-15', 1, 15.0, 2);

-- --------------------------------------------------------

--
-- Table structure for table `cardio`
--

CREATE TABLE `cardio` (
  `_id` int(5) NOT NULL,
  `cDate` date NOT NULL,
  `cTime` time NOT NULL,
  `distance` double(10,1) NOT NULL,
  `duration` varchar(255) NOT NULL,
  `machine_id` int(5) NOT NULL,
  `user_id` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cardio`
--

INSERT INTO `cardio` (`_id`, `cDate`, `cTime`, `distance`, `duration`, `machine_id`, `user_id`) VALUES
(2, '2019-05-14', '00:00:00', 2.0, '00:20', 2, 1),
(3, '2019-05-15', '00:41:31', 5.0, '00:10', 2, 1),
(4, '2019-05-15', '00:41:33', 5.0, '00:10', 2, 1),
(5, '2019-05-15', '00:41:36', 5.2, '00:10', 2, 1),
(6, '2019-05-15', '00:41:43', 5.5, '00:25', 2, 1),
(7, '2019-05-15', '01:01:04', 10.0, '00:30', 2, 1),
(8, '2019-05-15', '01:06:57', 10.0, '00:15', 2, 1),
(9, '2019-05-15', '01:16:49', 5.0, '00:10', 2, 1),
(25, '2019-05-26', '01:53:32', 1.0, '00:30', 2, 1),
(26, '2019-08-11', '23:51:24', 1.0, '00:15', 5, 2),
(27, '2019-08-12', '23:51:39', 1.5, '00:20', 5, 2),
(28, '2019-08-13', '23:51:53', 2.0, '00:30', 5, 2),
(29, '2019-08-14', '23:52:05', 1.5, '00:25', 5, 2),
(30, '2019-08-15', '23:52:27', 1.7, '00:20', 5, 2),
(31, '2019-08-16', '23:52:39', 2.3, '00:35', 5, 2),
(33, '2019-08-17', '23:54:41', 1.3, '00:23', 5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `machine`
--

CREATE TABLE `machine` (
  `_id` int(5) NOT NULL,
  `mDate` date NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `user_id` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `machine`
--

INSERT INTO `machine` (`_id`, `mDate`, `name`, `description`, `type`, `picture`, `user_id`) VALUES
(1, '2019-05-01', 'Dumbbell', 'This is dumbbell.', 'Body Building', 'images/1558259991329.jpg', 1),
(2, '2019-05-02', 'Treadmill', 'This is treadmill.', 'Cardio', 'images/1558460074054.jpg', 1),
(4, '2019-08-14', 'Dumbbell', 'This is dumbbell.', 'Body Building', 'images/1565797184961.jpg', 2),
(5, '2019-08-14', 'Treadmill', 'This is treadmill.', 'Cardio', 'images/1565797288953.jpg', 2);

-- --------------------------------------------------------

--
-- Table structure for table `user_data`
--

CREATE TABLE `user_data` (
  `_id` int(5) NOT NULL,
  `date_create` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_date` date NOT NULL,
  `name` varchar(255) NOT NULL,
  `bdate` date NOT NULL,
  `gender` varchar(255) NOT NULL,
  `height` double(10,1) NOT NULL,
  `p_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_data`
--

INSERT INTO `user_data` (`_id`, `date_create`, `email`, `password`, `password_date`, `name`, `bdate`, `gender`, `height`, `p_path`) VALUES
(1, '2019-05-03', 'amiruddin6521@gmail.com', 'amir6521', '2019-05-03', 'Amiruddin Bazli', '1994-09-24', 'Male', 187.0, 'images/1564644741033.jpg'),
(2, '2019-08-13', 'amir@gmail.com', 'amir1234', '2019-08-13', 'Amir', '1994-09-24', 'Male', 180.0, 'images/1565796867422.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `weight_track`
--

CREATE TABLE `weight_track` (
  `_id` int(5) NOT NULL,
  `cDate` date NOT NULL,
  `weight` double(10,1) NOT NULL,
  `user_id` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `weight_track`
--

INSERT INTO `weight_track` (`_id`, `cDate`, `weight`, `user_id`) VALUES
(28, '2019-05-22', 80.9, 1),
(35, '2019-05-23', 85.0, 1),
(36, '2019-05-24', 70.5, 1),
(37, '2019-08-13', 80.0, 2),
(40, '2019-08-14', 77.0, 2),
(42, '2019-08-15', 73.0, 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `body_building`
--
ALTER TABLE `body_building`
  ADD PRIMARY KEY (`_id`),
  ADD KEY `machine_id` (`machine_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `body_track`
--
ALTER TABLE `body_track`
  ADD PRIMARY KEY (`_id`),
  ADD KEY `body_track_ibfk_1` (`user_id`);

--
-- Indexes for table `cardio`
--
ALTER TABLE `cardio`
  ADD PRIMARY KEY (`_id`),
  ADD KEY `machine_id` (`machine_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `machine`
--
ALTER TABLE `machine`
  ADD PRIMARY KEY (`_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user_data`
--
ALTER TABLE `user_data`
  ADD PRIMARY KEY (`_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `weight_track`
--
ALTER TABLE `weight_track`
  ADD PRIMARY KEY (`_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `body_building`
--
ALTER TABLE `body_building`
  MODIFY `_id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT for table `body_track`
--
ALTER TABLE `body_track`
  MODIFY `_id` int(25) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `cardio`
--
ALTER TABLE `cardio`
  MODIFY `_id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `machine`
--
ALTER TABLE `machine`
  MODIFY `_id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `user_data`
--
ALTER TABLE `user_data`
  MODIFY `_id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `weight_track`
--
ALTER TABLE `weight_track`
  MODIFY `_id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `body_building`
--
ALTER TABLE `body_building`
  ADD CONSTRAINT `body_building_ibfk_1` FOREIGN KEY (`machine_id`) REFERENCES `machine` (`_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `body_building_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user_data` (`_id`) ON DELETE CASCADE;

--
-- Constraints for table `body_track`
--
ALTER TABLE `body_track`
  ADD CONSTRAINT `body_track_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_data` (`_id`) ON DELETE CASCADE;

--
-- Constraints for table `cardio`
--
ALTER TABLE `cardio`
  ADD CONSTRAINT `cardio_ibfk_1` FOREIGN KEY (`machine_id`) REFERENCES `machine` (`_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cardio_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user_data` (`_id`) ON DELETE CASCADE;

--
-- Constraints for table `machine`
--
ALTER TABLE `machine`
  ADD CONSTRAINT `machine_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_data` (`_id`) ON DELETE CASCADE;

--
-- Constraints for table `weight_track`
--
ALTER TABLE `weight_track`
  ADD CONSTRAINT `weight_track_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_data` (`_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
