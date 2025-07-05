# 🛒 Fawry E-Commerce Challenge

A simple object-oriented e-commerce system implemented in **Java**, submitted for the **Fawry Quantum Internship**.  
This project simulates product management, shopping cart functionality, checkout processing, and shipping calculation.

---

## ✅ Features

### 📦 Product Types
- Basic products with name, price, and quantity  
- Expirable products (e.g., Cheese, Biscuits)  
- Shippable products (e.g., TV, Cheese) with weight  
- Combined expirable + shippable products  

### 🛒 Cart System
- Add products to cart (within available quantity)  
- Prevents adding more than available stock  

### 💳 Checkout Process
- Subtotal calculation  
- Dynamic shipping fee (based on total weight)  
- Validates customer balance before payment  
- Prevents checkout if:
  - Cart is empty  
  - Product is expired  
  - Requested quantity exceeds available stock  
  - Customer balance is insufficient  
- Updates product stock and customer balance after payment  
- Displays formatted receipt and shipment notice in console  

### 🚚 Shipping Service
- Accepts shippable items  
- Displays list of items and total package weight in kg  

---


