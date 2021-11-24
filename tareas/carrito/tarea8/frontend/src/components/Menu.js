import React from "react";
import { Link } from "react-router-dom";

export default function Menu() {
  return (
    <div>
      <h1>Menu</h1>
      <nav>
        <Link to='/captura'>Captura de artículos</Link>
        <Link to='/compras'>Compra de artículos</Link>
      </nav>
    </div>
  );
}
