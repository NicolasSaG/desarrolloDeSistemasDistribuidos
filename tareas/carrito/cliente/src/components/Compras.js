import React from "react";
import { useState } from "react";
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

    let body = "";
    let name;
    let pairs = [];
    try {
      for (name in data) {
        let value = data[name];
        if (typeof value !== "string") value = JSON.stringify(value);
        pairs.push(
          name +
            "=" +
            encodeURI(value)
              .replace(/=/g, "%3D")
              .replace(/&/g, "%26")
              .replace(/%20/g, "+")
        );
      }
    } catch (error) {
      alert("Error: " + error.message);
    }
    body = pairs.join("&");
    console.log(body);
    axios
      .post("/Servicio/rest/ws/consulta_articulos", body, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setState(response.data);
          console.log(state);
        } else {
          alert("No se encontraron articulos que coincidan");
        }
      })
      .catch((error) => {
        alert("hubo un error");
        console.long(error);
      });
  };

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

      {state.map((item) => (
        <div>
          <p>{item.descripcion}</p>
          <p>{item.precio}</p>
          <img
            id='articulo_imagen'
            alt={item.descripcion}
            width='200px'
            src={`data:image/jpeg;base64,${item.imagen}`}
          ></img>
          <button>Agregar al carrito</button>
          <br />
          <br />
        </div>
      ))}
    </div>
  );
}
