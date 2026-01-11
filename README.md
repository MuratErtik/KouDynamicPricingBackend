# ✈️ KOU Airlines - Dynamic Pricing Backend

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue) ![Fuzzy Logic](https://img.shields.io/badge/AI-Fuzzy%20Logic-purple)

**KOU Airlines Backend** is a reservation system for an airline company. It is built with **Spring Boot** and **PostgreSQL**.

The most important feature of this project is the **Dynamic Pricing Engine**. It uses **Fuzzy Logic** (Artificial Intelligence) to calculate ticket prices automatically based on real-time data.

---

## 🚀 Key Features

### 🧠 1. Smart Dynamic Pricing
Standard systems use simple rules (if-else). This project uses **Fuzzy Logic (.fcl files)** to make smarter decisions, like a human manager.
* **It analyzes:** How many days are left, how full the plane is, and the time of the flight.
* **It decides:**
    * If the plane is full or it is a busy day (like Monday morning), the price goes **UP**.
    * If it is a night flight (03:00 AM) and empty, the price goes **DOWN**.

### 🛠 2. Flight & Booking Management
* **Management:** You can create and edit flights, airports, and routes.
* **Soft Delete:** When we cancel a flight or ticket, we do not delete it from the database permanently. We mark it as "passive" to keep the data safe for reports.
* **Seat Safety:** The system prevents two people from buying the same seat at the same time.

### 📅 3. Special Days & Seasons
* The system knows holidays, festivals, and special days.
* It automatically increases prices for specific dates or countries during these times.

### 🔔 4. Notifications & Security
* **Email:** When a user buys or cancels a ticket, the system sends an automatic email with flight details.
* **Security:** Sensitive data (like ID numbers) is hashed and saved securely in the database.

---

## 🏗 Tech Stack

| Technology | Usage |
| :--- | :--- |
| **Java 17+** | Main Programming Language |
| **Spring Boot 4** | Backend Framework (Web, Data JPA) |
| **PostgreSQL** | Database |
| **jFuzzyLogic** | Library for Fuzzy Logic calculations |
| **Hibernate / JPA** | Database connection and management |
| **Lombok** | To write less code (cleaner code) |

---

## 🧩 How the Pricing Logic Works

The system uses a logic file (`DynamicPricing.fcl`) to calculate the price. Here is the process:

1.  **Inputs (Fuzzification):**
    * `daysToDeparture`: Is the flight soon? (Short term vs Long term)
    * `occupancyRate`: Is the plane empty or full?
    * `dayScore`: Is it a popular day? (Friday is popular, Tuesday is not)
    * `timeScore`: Is it a popular hour? (09:00 AM is popular, 03:00 AM is not)
    * `specialDays`:  The system knows holidays, festivals, and special days.

      
  


2.  **Rules (Inference):**
    * *Example:* IF the flight is **very soon** AND the plane is **full**, THEN increase the price significantly.
    * *Example:* IF it is **late night** AND the plane is **empty**, THEN give a discount.

3.  **Result (Defuzzification):**
    * The system calculates a **Multiplier** (e.g., 1.2 or 0.8).
    * **Final Price** = Base Price * Multiplier.

---




## 📞 Contact

**Developer:** Murat Ertik

**Email:** dmuratertik1@gmail.com

**LinkedIn:** www.linkedin.com/in/murat-ertik

> *This project was developed as a Senior Design Project at Kocaeli University (KOÜ).*
