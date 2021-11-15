import React from "react";
import { useState, useEffect } from "react";
import axios from "axios";
import Menu from "./Menu";

import { useRef } from "react";

export default function Compras() {
  const formRef = useRef();
  const [state, setState] = useState([]);
  const [search, setSearch] = useState("");

  const onSubmit = (event) => {
    event.preventDefault();
    const formdata = new FormData(formRef.current);
    const data = {
      busqueda: formdata.get("busqueda"),
    };

    axios
      .post("/Servicio/rest/ws/articulos")
      .then((response) => {
        setState(response.data);
        console.log("datos recibidos:");
        console.log(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  // useEffect(() => {
  //   axios
  //     .post("/Servicio/rest/ws/articulos")
  //     .then((response) => {
  //       setState(response.data);
  //       console.log(response.data);
  //     })
  //     .catch((error) => {
  //       console.log(error);
  //     });
  // });

  return (
    <div>
      <Menu />
      <h1>Buscador de artículos</h1>
      <form method='POST' onSubmit={onSubmit} ref={formRef}>
        <input
          type='text'
          name='busqueda'
          onChange={setSearch}
          placeholder='buscar...'
        />
        <input type='submit' value='Buscar artículos' />
      </form>
      <h1>Artículos disponibles</h1>

      {/* {state.map((item) => (
        <div key={item.id}>
          <p>{item.descripcion}</p>
          <p>{item.precio}</p>
          <p>{item.cantidad}</p>
          <p>Imagen</p>
          <img id='articulo_imagen' alt={item.id} width='200px'></img>
          <button>Agregar al carrito</button>
        </div>
      ))} */}
    </div>
  );
}
