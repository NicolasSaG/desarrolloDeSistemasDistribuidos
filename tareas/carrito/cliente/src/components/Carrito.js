import React from "react";
import { useState } from "react";
import axios from "axios";
import Menu from "./Menu";
import { Link } from "react-router-dom";
import { useRef, useEffect } from "react";

export default function Carrito() {
  const [state, setState] = useState([]);

  useEffect(() => {
    axios
      .post("/Servicio/rest/ws/consulta_carrito")
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setState(response.data);
          console.log(state);
        } else {
          alert("No hay articulos en el carrito");
          setState([]);
        }
      })
      .catch((error) => {
        console.log(error);
        alert("error al obtener los articulos del carrito");
      });
  });

  return (
    <div>
      <Link to='/compras'>Compra de artículos</Link>
      <h1>Carrito de compras</h1>

      <table>
        <tr>
          <th>Imagen</th>
          <th>Descripción</th>
          <th>Cantidad</th>
          <th>Precio total</th>
        </tr>
        {state.map((item) => (
          <tr>
            <td>
              <img
                id='articulo_imagen'
                alt={item.descripcion}
                width='200px'
                src={`data:image/jpeg;base64,${item.imagen}`}
              ></img>
            </td>
            <td>{item.descripcion}</td>
            <td>{item.cantidad}</td>
            <td>{item.cantidad * item.precio}</td>
          </tr>
        ))}
      </table>
    </div>
  );
}
