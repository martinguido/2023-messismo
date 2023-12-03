import React from "react";
import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Welcome from "./screens/Welcome";
import Home from "./screens/Home";
import Products from "./screens/Products";
import Orders from "./screens/Orders";
import Resources from "./screens/Resources";
import Categories from "./screens/Categories";
import Login from "./screens/Login";
import Register from "./screens/Register";
import Dashboard from "./screens/Dashboard";
import Goals from "./screens/Goals";
import BarConfiguration from "./screens/BarConfiguration";
import Reservations from "./screens/Reservations";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Welcome />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/homepage" element={<Home />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/goals" element={<Goals />} />
          <Route path="/products" element={<Products />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/resources" element={<Resources />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/barConfiguration" element={<BarConfiguration />} />
          <Route path="/reservations" element={<Reservations />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
