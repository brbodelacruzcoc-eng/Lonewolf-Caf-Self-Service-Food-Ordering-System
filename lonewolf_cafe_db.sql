-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 28, 2026 at 01:37 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lonewolf_cafe_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_archived_orders`
--

CREATE TABLE `tbl_archived_orders` (
  `archive_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `order_date` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `order_type` varchar(20) DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `archived_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_archived_orders`
--

INSERT INTO `tbl_archived_orders` (`archive_id`, `order_id`, `user_id`, `total_amount`, `order_date`, `status`, `order_type`, `payment_method`, `archived_date`) VALUES
(9, 1, NULL, 690.00, '2026-09-30 17:00:58', 'Completed', 'Take Out', 'Cash', '2026-10-08 00:20:10'),
(10, 2, NULL, 440.00, '2026-09-30 17:24:54', 'Completed', 'Take Out', 'Cash', '2026-10-08 00:20:10'),
(14, 3, NULL, 640.00, '2026-09-30 17:26:20', 'Completed', 'Take Out', 'Cash', '2026-10-08 10:56:17'),
(15, 4, NULL, 290.00, '2026-10-02 20:21:56', 'Completed', 'Dine In', 'Card', '2026-10-08 10:56:17'),
(16, 6, NULL, 790.00, '2026-10-05 16:31:26', 'Pending', 'Dine In', 'Cash', '2026-10-08 11:55:55'),
(17, 7, NULL, 360.00, '2026-10-07 13:42:50', 'Pending', 'Take Out', 'Card', '2026-10-08 11:55:55'),
(18, 5, NULL, 400.00, '2026-10-02 20:45:07', 'Completed', 'Take Out', 'Card', '2026-10-08 21:23:14'),
(19, 8, 1, 360.00, '2026-10-07 19:06:54', 'Completed', 'Take Out', 'Cash', '2026-10-08 21:28:42'),
(20, 9, 1, 200.00, '2026-10-07 20:07:17', 'Completed', 'Dine In', 'Cash', '2026-10-09 11:01:44'),
(21, 10, 1, 150.00, '2026-10-07 20:15:55', 'Completed', 'Take Out', 'Card', '2026-10-09 11:01:44'),
(22, 11, 1, 200.00, '2026-10-07 21:36:01', 'Completed', 'Dine In', 'Cash', '2026-10-11 15:38:49'),
(23, 12, 1, 200.00, '2026-10-07 21:36:14', 'Completed', 'Take Out', 'Cash', '2026-10-11 15:38:49'),
(24, 13, 1, 200.00, '2026-10-07 22:23:35', 'Completed', 'Dine In', 'Cashless', '2026-10-11 15:38:49'),
(25, 14, 1, 700.00, '2026-10-08 21:25:27', 'Completed', 'Take Out', 'Cash', '2026-10-11 15:38:49'),
(26, 15, 1, 450.00, '2026-10-08 21:29:29', 'Completed', 'Take Out', 'Cash', '2026-10-12 14:09:50'),
(27, 16, 1, 290.00, '2026-10-09 10:19:34', 'Ready for Pickup', 'Dine In', 'Cash', '2026-10-12 14:09:50'),
(28, 17, 3, 260.00, '2026-10-09 10:27:14', 'Completed', 'Take Out', 'Cashless', '2026-10-12 14:09:50'),
(29, 18, 3, 180.00, '2026-10-09 10:31:25', 'Completed', 'Dine In', 'Cashless', '2026-10-12 21:57:09'),
(30, 19, 3, 180.00, '2026-10-09 10:31:45', 'Completed', 'Dine In', 'Cash', '2026-10-12 21:57:09'),
(31, 20, 1, 620.00, '2026-10-09 10:34:25', 'Completed', 'Dine In', 'Cashless', '2026-10-12 21:57:09'),
(32, 21, 1, 1000.00, '2026-10-09 10:34:40', 'Completed', 'Take Out', 'Cashless', '2026-10-12 21:57:09'),
(33, 50, 2, 4010.00, '2026-10-13 09:23:14', 'Completed', 'Dine In', 'Cash', '2026-10-13 09:31:41');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_menu_items`
--

CREATE TABLE `tbl_menu_items` (
  `item_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `availability` varchar(15) NOT NULL DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_menu_items`
--

INSERT INTO `tbl_menu_items` (`item_id`, `item_name`, `price`, `category`, `description`, `availability`) VALUES
(1, 'Espresso Coffe', 90.00, 'Signatures', 'Bold single shot', 'Available'),
(2, 'Americano', 100.00, 'Coffee', 'Smooth black coffee', 'Available'),
(3, 'Cortado', 100.00, 'Coffee', 'Espresso + warm milk', 'Available'),
(4, 'Flat White', 110.00, 'Coffee', 'Velvety and strong', 'Available'),
(5, 'Cappuccino', 110.00, 'Coffee', 'Espresso, milk, foam', 'Available'),
(6, 'Cafe Latte', 120.00, 'Coffee', 'Creamy and smooth', 'Available'),
(7, 'Spanish Latte', 140.00, 'Coffee', 'Sweet condensed milk', 'Available'),
(8, 'Rose Latte', 150.00, 'Coffee', 'Floral and creamy', 'Available'),
(9, 'Cafe Mocha', 150.00, 'Coffee', 'Chocolate + espresso', 'Available'),
(10, 'Caramel Macchiato', 150.00, 'Coffee', 'Caramel vanilla latte', 'Available'),
(11, 'Salted Caramel', 150.00, 'Coffee', 'Sweet & salty caramel', 'Available'),
(12, 'Dirty Matcha', 200.00, 'Coffee', 'Matcha + espresso shot', 'Available'),
(13, 'Black Eye', 200.00, 'Coffee', 'Extra strong brew', 'Available'),
(14, 'Glacial Alpha', 170.00, 'Signatures', 'Icy creamy blend', 'Available'),
(15, 'Arctic Siberian', 170.00, 'Signatures', 'Cool vanilla frost', 'Available'),
(16, 'Frozen Alaskan', 170.00, 'Signatures', 'Sweet creamy ice blend', 'Available'),
(17, 'Chamomile', 90.00, 'Tea', 'Calming, caffeine-free', 'Available'),
(18, 'Jasmine', 90.00, 'Tea', 'Floral and soothing', 'Available'),
(19, 'Pure Green Tea', 90.00, 'Tea', 'Light and fresh', 'Available'),
(20, 'Earl Grey', 90.00, 'Tea', 'Bergamot black tea', 'Available'),
(21, 'Sencha', 90.00, 'Tea', 'Earthy Japanese green tea', 'Available'),
(22, 'Earl Grey Black Tea', 90.00, 'Tea', 'Bold Earl Grey', 'Available'),
(23, 'Premium Black Tea', 90.00, 'Tea', 'Rich and full-bodied', 'Available'),
(24, 'Oolong Tea', 90.00, 'Tea', 'Floral, lightly toasty', 'Available'),
(25, 'Red Iberian Berry', 150.00, 'Soda Based', 'Sweet mixed berries fizz', 'Available'),
(26, 'Blue Iberian Berry', 150.00, 'Soda Based', 'Blueberry berry fizz', 'Available'),
(27, 'Tropical Berry', 150.00, 'Soda Based', 'Tropical berry sparkle', 'Available'),
(28, 'Tundra Peach', 150.00, 'Soda Based', 'Peach soda fizz', 'Available'),
(29, 'Hudson Bay Passion', 150.00, 'Soda Based', 'Tangy passionfruit', 'Available'),
(30, 'Green Apple', 150.00, 'Soda Based', 'Crisp apple fizz', 'Available'),
(31, 'Kiwi Wolf', 150.00, 'Soda Based', 'Zesty kiwi punch', 'Available'),
(32, 'Lychee Hound', 150.00, 'Soda Based', 'Sweet lychee soda', 'Available'),
(33, 'Classic Iced Tea', 150.00, 'Iced Tea', 'Classic chilled tea', 'Available'),
(34, 'Kiwi Mango Rose', 160.00, 'Iced Tea', 'Fruity with rose', 'Available'),
(35, 'Lemongrass Peach', 160.00, 'Iced Tea', 'Citrusy peach tea', 'Available'),
(36, 'Lemongrass Berry Rose', 160.00, 'Iced Tea', 'Berry rose citrus', 'Available'),
(37, 'Strawberry Mint', 160.00, 'Iced Tea', 'Strawberry + mint', 'Available'),
(38, 'Strawberry Iced Tea', 160.00, 'Iced Tea', 'Strawberry infused', 'Available'),
(39, 'Classic Lemonade', 160.00, 'Lemonade', 'Fresh sweet-sour lemon', 'Available'),
(40, 'Kiwi Mango Rose', 160.00, 'Lemonade', 'Tropical rose lemonade', 'Available'),
(41, 'Lemongrass Peach Apple', 160.00, 'Lemonade', 'Fruity citrus lemonade', 'Available'),
(42, 'Lemongrass Berry Rose', 160.00, 'Lemonade', 'Berry rose lemonade', 'Available'),
(43, 'Strawberry Mint', 160.00, 'Lemonade', 'Strawberry mint lemonade', 'Available'),
(44, 'Strawberry Iced Tea', 160.00, 'Lemonade', 'Sweet strawberry lemon', 'Available'),
(45, 'Rocky Mountain', 200.00, 'Frappes', 'Choco, nuts, marshmallow', 'Available'),
(46, 'Wild Matcha', 200.00, 'Frappes', 'Creamy matcha blend', 'Available'),
(47, 'Black Forest', 200.00, 'Frappes', 'Choco cherry blend', 'Available'),
(48, 'Choco Mint', 200.00, 'Frappes', 'Mint choco blend', 'Available'),
(49, 'Turkey Burger', 250.00, 'Food & Snacks', 'Juicy turkey patty burger', 'Available'),
(50, 'Nachos', 250.00, 'Food & Snacks', 'Crispy chips with toppings', 'Available'),
(51, 'Brownies', 55.00, 'Food & Snacks', 'Rich fudgy chocolate square', 'Available'),
(52, 'White Almond Croissant', 135.00, 'Food & Snacks', 'Flaky, buttery and nutty', 'Available'),
(53, 'Ramen', 140.00, 'Food & Snacks', 'Warm savory noodle bowl', 'Available'),
(54, 'Carbonara', 240.00, 'Food & Snacks', 'Creamy bacon pasta', 'Available'),
(55, 'Honey Garlic', 299.00, 'Food & Snacks', 'Sweet and savory honey garlic', 'Available'),
(56, 'Creamy Parmesan', 299.00, 'Food & Snacks', 'Rich parmesan cream sauce', 'Available'),
(59, 'kiwi juice', 120.00, 'Iced Tea', 'fruit juice', 'Available');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_orders`
--

CREATE TABLE `tbl_orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `order_date` datetime DEFAULT current_timestamp(),
  `status` varchar(30) NOT NULL DEFAULT 'Pending',
  `order_type` varchar(20) DEFAULT 'Dine In',
  `payment_method` varchar(20) DEFAULT 'Cash',
  `is_paid` tinyint(1) NOT NULL DEFAULT 0,
  `amount_paid` decimal(10,2) DEFAULT NULL,
  `change_due` decimal(10,2) DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_orders`
--

INSERT INTO `tbl_orders` (`order_id`, `user_id`, `total_amount`, `order_date`, `status`, `order_type`, `payment_method`, `is_paid`, `amount_paid`, `change_due`, `paid_at`) VALUES
(22, 1, 680.00, '2026-10-09 10:34:55', 'Completed', 'Dine In', 'Cash', 1, NULL, NULL, NULL),
(23, 1, 200.00, '2026-10-09 10:51:49', 'Completed', 'Dine In', 'Cash', 1, NULL, NULL, NULL),
(24, 1, 280.00, '2026-10-10 17:07:49', 'Completed', 'Take Out', 'Cash', 1, NULL, NULL, NULL),
(25, 1, 500.00, '2026-10-11 02:09:41', 'Completed', 'Take Out', 'Cash', 1, NULL, NULL, NULL),
(26, 1, 180.00, '2026-10-11 13:00:04', 'Completed', 'Dine In', 'Cash', 1, NULL, NULL, NULL),
(27, 3, 400.00, '2026-10-11 14:22:13', 'Completed', 'Take Out', 'Cash', 1, NULL, NULL, NULL),
(28, 2, 620.00, '2026-10-11 15:04:22', 'Completed', 'Take Out', 'Cash', 1, NULL, NULL, NULL),
(29, 2, 580.00, '2026-10-11 15:28:05', 'Completed', 'Take Out', 'Cash', 1, NULL, NULL, NULL),
(30, NULL, 1000.00, '2026-10-11 15:33:44', 'Preparing', 'Dine In', 'Cash', 1, NULL, NULL, NULL),
(31, NULL, 280.00, '2026-10-11 16:19:16', 'Preparing', 'Take Out', 'Cashless', 1, NULL, NULL, NULL),
(32, 2, 280.00, '2026-10-11 16:23:19', 'Completed', 'Dine In', 'Cash', 1, NULL, NULL, NULL),
(33, NULL, 620.00, '2026-10-11 16:26:02', 'Preparing', 'Dine In', 'Cashless', 1, NULL, NULL, NULL),
(34, 6, 220.00, '2026-10-11 16:26:08', 'Pending', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(35, 3, 620.00, '2026-10-11 23:10:05', 'Completed', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(36, 3, 500.00, '2026-10-11 23:10:11', 'Completed', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(37, 3, 430.00, '2026-10-11 23:10:27', 'Completed', 'Take Out', 'Cashless', 0, NULL, NULL, NULL),
(38, 6, 280.00, '2026-10-12 00:49:10', 'Completed', 'Take Out', 'Cash', 0, NULL, NULL, NULL),
(39, 2, 210.00, '2026-10-12 11:09:08', 'Completed', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(40, NULL, 230.00, '2026-10-12 11:13:58', 'Pending', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(41, 6, 515.00, '2026-10-12 11:46:51', 'Pending', 'Take Out', 'Cashless', 0, NULL, NULL, NULL),
(42, 6, 280.00, '2026-10-12 11:46:59', 'Completed', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(43, 2, 840.00, '2026-10-12 13:44:22', 'Completed', 'Take Out', 'Cashless', 0, NULL, NULL, NULL),
(44, NULL, 830.00, '2026-10-12 13:53:27', 'Pending', 'Dine In', 'Cashless', 0, NULL, NULL, NULL),
(45, 2, 870.00, '2026-10-12 13:56:07', 'Completed', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(46, 2, 1070.00, '2026-10-12 21:52:26', 'Completed', 'Dine In', 'Cashless', 0, NULL, NULL, NULL),
(47, NULL, 200.00, '2026-10-12 22:07:15', 'Pending', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(48, NULL, 620.00, '2026-10-13 00:57:16', 'Pending', 'Dine In', 'Cashless', 0, NULL, NULL, NULL),
(49, 2, 1790.00, '2026-10-13 07:39:21', 'Pending', 'Dine In', 'Cash', 0, NULL, NULL, NULL),
(51, NULL, 200.00, '2026-10-13 09:24:03', 'Pending', 'Dine In', 'Cashless', 0, NULL, NULL, NULL),
(52, NULL, 200.00, '2026-10-13 09:26:45', 'Pending', 'Dine In', 'Cashless', 0, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_order_items`
--

CREATE TABLE `tbl_order_items` (
  `order_item_id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_order_items`
--

INSERT INTO `tbl_order_items` (`order_item_id`, `order_id`, `item_id`, `quantity`, `subtotal`) VALUES
(55, 22, 2, 1, 100.00),
(56, 22, 5, 1, 110.00),
(57, 22, 6, 1, 120.00),
(58, 22, 4, 1, 110.00),
(59, 22, 1, 1, 80.00),
(60, 22, 34, 1, 160.00),
(61, 23, 3, 1, 100.00),
(62, 23, 2, 1, 100.00),
(63, 24, 3, 1, 100.00),
(64, 24, 2, 1, 100.00),
(65, 24, 1, 1, 80.00),
(66, 25, 2, 1, 100.00),
(67, 25, 1, 1, 80.00),
(68, 25, 3, 1, 100.00),
(69, 25, 5, 1, 110.00),
(70, 25, 4, 1, 110.00),
(71, 26, 2, 1, 100.00),
(72, 26, 1, 1, 80.00),
(73, 27, 1, 1, 80.00),
(74, 27, 2, 1, 100.00),
(75, 27, 3, 1, 100.00),
(76, 27, 6, 1, 120.00),
(77, 28, 1, 1, 80.00),
(78, 28, 2, 1, 100.00),
(79, 28, 5, 1, 110.00),
(80, 28, 4, 1, 110.00),
(81, 28, 3, 1, 100.00),
(82, 28, 6, 1, 120.00),
(83, 29, 1, 1, 80.00),
(84, 29, 2, 1, 100.00),
(85, 29, 45, 1, 200.00),
(86, 29, 46, 1, 200.00),
(87, 30, 1, 1, 80.00),
(88, 30, 14, 1, 170.00),
(89, 30, 17, 1, 90.00),
(90, 30, 25, 1, 150.00),
(91, 30, 33, 1, 150.00),
(92, 30, 39, 1, 160.00),
(93, 30, 45, 1, 200.00),
(94, 31, 1, 1, 80.00),
(95, 31, 2, 1, 100.00),
(96, 31, 3, 1, 100.00),
(97, 32, 3, 1, 100.00),
(98, 32, 2, 1, 100.00),
(99, 32, 1, 1, 80.00),
(100, 33, 2, 1, 100.00),
(101, 33, 1, 1, 80.00),
(102, 33, 4, 1, 110.00),
(103, 33, 5, 1, 110.00),
(104, 33, 6, 1, 120.00),
(105, 33, 3, 1, 100.00),
(106, 34, 3, 1, 100.00),
(107, 34, 6, 1, 120.00),
(108, 35, 2, 1, 100.00),
(109, 35, 1, 1, 80.00),
(110, 35, 4, 1, 110.00),
(111, 35, 5, 1, 110.00),
(112, 35, 3, 1, 100.00),
(113, 35, 6, 1, 120.00),
(114, 36, 1, 1, 80.00),
(115, 36, 2, 1, 100.00),
(116, 36, 5, 1, 110.00),
(117, 36, 4, 1, 110.00),
(118, 36, 3, 1, 100.00),
(119, 37, 6, 1, 120.00),
(120, 37, 3, 1, 100.00),
(121, 37, 5, 1, 110.00),
(122, 37, 2, 1, 100.00),
(123, 38, 3, 1, 100.00),
(124, 38, 2, 1, 100.00),
(125, 38, 1, 1, 80.00),
(126, 39, 2, 1, 100.00),
(127, 39, 5, 1, 110.00),
(128, 40, 1, 1, 80.00),
(129, 40, 26, 1, 150.00),
(130, 41, 52, 1, 135.00),
(131, 41, 53, 1, 140.00),
(132, 41, 54, 1, 240.00),
(133, 42, 1, 1, 80.00),
(134, 42, 2, 1, 100.00),
(135, 42, 3, 1, 100.00),
(136, 43, 3, 4, 400.00),
(137, 43, 2, 2, 200.00),
(138, 43, 1, 3, 240.00),
(139, 44, 5, 1, 110.00),
(140, 44, 9, 2, 300.00),
(141, 44, 8, 2, 300.00),
(142, 44, 6, 1, 120.00),
(143, 45, 3, 1, 100.00),
(144, 45, 2, 2, 200.00),
(145, 45, 1, 3, 240.00),
(146, 45, 4, 2, 220.00),
(147, 45, 5, 1, 110.00),
(148, 46, 2, 1, 100.00),
(149, 46, 3, 7, 700.00),
(150, 46, 6, 1, 120.00),
(151, 46, 9, 1, 150.00),
(152, 47, 3, 1, 100.00),
(153, 47, 2, 1, 100.00),
(154, 48, 3, 1, 100.00),
(155, 48, 2, 1, 100.00),
(156, 48, 1, 1, 80.00),
(157, 48, 4, 1, 110.00),
(158, 48, 5, 1, 110.00),
(159, 48, 6, 1, 120.00),
(160, 49, 1, 5, 400.00),
(161, 49, 2, 4, 400.00),
(162, 49, 3, 3, 300.00),
(163, 49, 5, 3, 330.00),
(164, 49, 6, 3, 360.00),
(170, 51, 3, 1, 100.00),
(171, 51, 2, 1, 100.00),
(172, 52, 3, 1, 100.00),
(173, 52, 2, 1, 100.00);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE `tbl_users` (
  `id` int(11) NOT NULL,
  `uname` varchar(50) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'cashier'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_users`
--

INSERT INTO `tbl_users` (`id`, `uname`, `full_name`, `password`, `role`) VALUES
(1, 'Admin1', 'David Brown', 'Admin123', 'Owner'),
(2, 'Cashier', 'Jessica Davis', 'Cashier123', 'Cashier'),
(3, 'Cashierjane', 'Jane Smith', 'Jane123', 'Cashier'),
(6, 'BaristaJean', 'Jean Jacques', 'Barista123', 'Barista'),
(7, 'Barista4', 'AnnaSmith', 'Barista2006', 'Barista');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_archived_orders`
--
ALTER TABLE `tbl_archived_orders`
  ADD PRIMARY KEY (`archive_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `tbl_menu_items`
--
ALTER TABLE `tbl_menu_items`
  ADD PRIMARY KEY (`item_id`);

--
-- Indexes for table `tbl_orders`
--
ALTER TABLE `tbl_orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `fk_orders_user` (`user_id`);

--
-- Indexes for table `tbl_order_items`
--
ALTER TABLE `tbl_order_items`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uname` (`uname`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_archived_orders`
--
ALTER TABLE `tbl_archived_orders`
  MODIFY `archive_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `tbl_menu_items`
--
ALTER TABLE `tbl_menu_items`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT for table `tbl_orders`
--
ALTER TABLE `tbl_orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `tbl_order_items`
--
ALTER TABLE `tbl_order_items`
  MODIFY `order_item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=174;

--
-- AUTO_INCREMENT for table `tbl_users`
--
ALTER TABLE `tbl_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_archived_orders`
--
ALTER TABLE `tbl_archived_orders`
  ADD CONSTRAINT `tbl_archived_orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`);

--
-- Constraints for table `tbl_orders`
--
ALTER TABLE `tbl_orders`
  ADD CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`id`);

--
-- Constraints for table `tbl_order_items`
--
ALTER TABLE `tbl_order_items`
  ADD CONSTRAINT `tbl_order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `tbl_orders` (`order_id`),
  ADD CONSTRAINT `tbl_order_items_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `tbl_menu_items` (`item_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
