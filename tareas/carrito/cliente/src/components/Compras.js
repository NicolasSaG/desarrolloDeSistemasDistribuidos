import React from "react";
import { useState, useEffect } from "react";
import axios from "axios";
import Menu from "./Menu";

export default function Compras() {
  const [state, setState] = useState([]);
  const [search, setSearch] = useState("");
  useEffect(() => {
    axios
      .post("/Servicio/rest/ws/articulos")
      .then((response) => {
        setState(response.data);
        console.log(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  });

  return (
    <div>
      <Menu />
      <h1>Buscador de artículos</h1>
      <input type='text' placeholder='buscar...' />
      <input type='submit' value='Buscar artículos' />
      <h1>Artículos disponibles</h1>

      {state.map((item) => (
        <div key={item.id}>
          <p>{item.descripcion}</p>
          <p>{item.precio}</p>
          <p>{item.cantidad}</p>
          <p>Imagen</p>
          <img id='articulo_imagen' alt={item.id} width='200px'></img>
          <button>Agregar al carrito</button>
        </div>
      ))}
    </div>
  );
}
