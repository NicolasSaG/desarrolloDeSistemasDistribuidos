import React from "react";
import { useState } from "react";
import axios from "axios";
import Menu from "./Menu";
import { Link } from "react-router-dom";
import { useRef } from "react";

export default function Compras() {
  const formRef = useRef();
  const [state, setState] = useState([]);

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
      .post("/Servicio/rest/ws/consulta_articulos", body)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          setState(response.data);
          console.log(state);
        } else if (response.status === 202) {
          alert("No se encontraron articulos que coincidan");
          setState([]);
        }
      })
      .catch((error) => {
        alert("hubo un error al obtener el carrito");
        console.log(error);
      });
  };

  const addArticulo = (event) => {
    event.preventDefault();
    const cantidad = event.target.form[0].value;
    const descripcion = event.target.form[1].value;

    const data = {
      descripcion: descripcion,
      cantidad: parseInt(cantidad),
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
      .post("/Servicio/rest/ws/alta_carrito", body, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          alert("Se agrego el artículo al carrito");
        } else if (response.status === 201) {
          alert(`Solo hay ${response.data} artículos disponibles`);
        } else if (response.data === 202) {
          alert(response.data);
        }
      })
      .catch((error) => {
        alert("hubo un error al agregar al carrito");
        console.log(error);
      });
    //regreso al valor default
    //event.target.form[0].value = 1;
  };

  return (
    <div>
      <Menu />
      <Link to='/carrito'>Carrito</Link>
      <h1>Buscador de artículos</h1>
      <form method='POST' onSubmit={onSubmit} ref={formRef}>
        <input type='text' name='busqueda' placeholder='buscar...' />
        <input type='submit' value='Buscar artículos' />
      </form>
      <h1>Artículos disponibles</h1>

      {state.map((item) => (
        <div>
          <label>
            <p>Descripción: {item.descripcion}</p>
          </label>
          <label>
            <p>Precio: {item.precio}</p>
          </label>
          <img
            id='articulo_imagen'
            alt={item.descripcion}
            width='200px'
            src={`data:image/jpeg;base64,${item.imagen}`}
          ></img>
          <form>
            <label>
              Cantidad:
              <input
                type='text'
                name={`art_cantidad_${item.descripcion}`}
                defaultValue='1'
              />
            </label>
            <input
              type='hidden'
              name='art_descripcion'
              value={item.descripcion}
            />
            <input type='button' onClick={addArticulo} value='Compra' />
          </form>
          <br />
          <br />
        </div>
      ))}
    </div>
  );
}
