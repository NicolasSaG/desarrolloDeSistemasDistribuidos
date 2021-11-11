import React from "react";
import axios from "axios";
import { useRef } from "react";
import { useState } from "react";

export default function FormArticulo() {
  const formRef = useRef();
  const [state, setState] = useState(null);

  const onSubmit = (event) => {
    event.preventDefault();
    const formdata = new FormData(formRef.current);

    const reader = new FileReader();
    console.log(file1);
    console.log(formdata.get("art_imagen"));
    reader.readAsDataURL(formdata.get("art_imagen"));
    reader.onload = function (e) {
      console.log(reader.result);
    };
    const data = {
      descripcion: formdata.get,
    };

    // axios
    //   .post("ruta", data, {
    //     headers: {
    //       "Content-Type": "multipart/form-data",
    //     },
    //   })
    //   .then((response) => {
    //     if (response.data.status === 200) {
    //       alert("Artículo agregado exitosamente");
    //     } else {
    //       alert("No se pudo crear el artículo");
    //     }
    //   });
    //console.log(data.get("art_descripcion"));
  };

  const onChange = (event) => {
    setState(URL.createObjectURL(event.target.files[0]));
  };

  return (
    <div>
      <h1>Añadir artículo</h1>
      <form method='POST' onSubmit={onSubmit} ref={formRef}>
        <label>
          Descripción:
          <input type='text' name='art_descripcion' />
        </label>
        <label>
          Cantidad:
          <input type='text' name='art_cantidad' />
        </label>
        <label>
          Precio
          <input type='text' name='art_precio' />
        </label>
        <label>
          Imagen:
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
