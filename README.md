# TechMart Online - E-Commerce Platform Modernization

This repository contains a comprehensive enterprise modernization strategy and high-performance prototype for **TechMart
Online**. The monolithic legacy system was re-architected into a scalable, decoupled **Jakarta EE 10** platform capable
of handling **10,000+ concurrent users** with sub-second response times.

---

## 🏗️ Enterprise Multi-Module Architecture

The project is structured using a clean **Maven Multi-Module Architecture** to maintain a strict separation of
concerns (SoC):

* **`techmart-core` (JAR):** Contains decoupled Data Transfer Objects (DTOs), shared utilities, and custom enterprise
  exceptions.
* **`techmart-ejb` (JAR):** The heavy business logic layer hosting Container-Managed Transactions (JTA),
  Stateful/Stateless/Singleton Session Beans, and JMS Message-Driven Beans (MDB).
* **`techmart-web` (WAR):** Exposes high-performance Jakarta RESTful Web Services (JAX-RS) and a lightweight
  HTML5/JavaScript dashboard.
* **`techmart-ear` (EAR):** Bundles all components into a single enterprise archive unit for robust application server
  deployment.

---

## 🛠️ Technology Stack & Optimizations

* **Java Version:** JDK 21
* **Enterprise Platform:** Jakarta EE 10 (EJB 4.0, JPA 3.0, JAX-RS 3.1)
* **Application Server:** Payara Server 6
* **Database:** MySQL 8+ with Optimized JNDI Connection Pooling (`jdbc/TechMartDS`)
* **Build Tool:** Maven

### Key Architectural Patterns Implemented:

* **Singleton Session Bean:** Manages global real-time inventory caching with strict transaction write-locks to
  completely eliminate overselling issues.
* **Stateless Session Bean Pool:** Drives high-concurrency order placement processing.
* **Stateful Session Bean:** Guarantees isolated, server-side session persistence for shopping carts.
* **JMS & Message-Driven Beans (MDB):** Implements point-to-point asynchronous messaging for background order processing
  and customer notifications.

---
