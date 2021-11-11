import React from "react";
// import ReactDOM from "react-dom";
import "./index.css";
import App from "./App";
import { render } from "react-dom";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import FormArticulo from "./components/FormArticulo";
import Compras from "./components/Compras";

const rootElement = document.getElementById("root");
render(
  <BrowserRouter>
    <Routes>
      <Route path='/' element={<App />} />
      <Route path='captura' element={<FormArticulo />} />
      <Route path='compras' element={<Compras />} />
    </Routes>
  </BrowserRouter>,
  rootElement
);
