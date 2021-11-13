import React from "react";
import axios from "axios";
import { useRef } from "react";
import { useState } from "react";
import Menu from "./Menu";
export default function FormArticulo() {
  const formRef = useRef();
  const [state, setState] = useState(null);

  const onSubmit = (event) => {
    event.preventDefault();
    const formdata = new FormData(formRef.current);

    //creacion de json
    const data = {
      descripcion: formdata.get("art_descripcion"),
      cantidad: parseInt(formdata.get("art_cantidad")),
      precio: parseFloat(formdata.get("art_precio")),
    };

    //base 64 de la imagen
    const reader = new FileReader();
    reader.readAsDataURL(formdata.get("art_imagen"));

    reader.onload = function (e) {
      // console.log(reader.result);
      data.img = reader.result.split(",")[1];
      console.log(data);
      axios
        .post("/Servicio/rest/ws/alta_articulo", data, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          if (response.data.status === 200) {
            alert("Artículo agregado exitosamente");
          }
        })
        .catch((error) => {
          alert("Error al agregar el artículo");
        });
    };
  };

  const onChange = (event) => {
    setState(URL.createObjectURL(event.target.files[0]));
  };

  return (
    <div>
      <Menu />
      <h1>Añadir artículo</h1>
      <form method='POST' onSubmit={onSubmit} ref={formRef}>
        <label>
          Descripción del artículo:
          <input type='text' name='art_descripcion' />
        </label>
        <label>
          Cantidad en almacén:
          <input type='text' name='art_cantidad' />
        </label>
        <label>
          Precio:
          <input type='text' name='art_precio' />
        </label>
        <label>
          Fotografía del artículo:
          <img
            src={state}
            id='articulo_imagen'
            alt=' '
            name='img_prev'
            width='200px'
          ></img>
          <input type='file' name='art_imagen' onChange={onChange} />
        </label>
        <input type='submit' value='Agregar artículo' />
      </form>
    </div>
  );
}
