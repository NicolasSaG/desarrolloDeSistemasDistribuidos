// Carlos Pineda G. 2021

//#define INSERT
//#define SELECT
//#define HTML

using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

// agregados
using System.Collections.Generic;
using MySql.Data.MySqlClient;

namespace FunctionApp1
{
#if SELECT
    class Usuario
    {
        public int id_usuario;
        public string email;
        public string nombre;
        public string apellido_paterno;
        public string apellido_materno;
        public string fecha_nacimiento;
        public string telefono;
        public string genero;
    }
#endif
    public static class Function1
    {
        [FunctionName("Function1")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post")] HttpRequest req, ILogger log)
        {
            try
            {
                log.LogInformation("C# HTTP trigger function processed a request.");

                // obtiene los parámetros que pasan en la URL (si los hay)
                string email = req.Query["email"];
                string nombre = req.Query["nombre"];

                // obtiene los parámetros que pasan en el "body" (si lo hay)
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                dynamic data = JsonConvert.DeserializeObject(requestBody);
                email = email != null ? email : data != null ? data.email : "";
                nombre = nombre != null ? nombre : data != null ? data.nombre : "";

                if (email == "" || nombre == "")
                    return new BadRequestObjectResult("Se espera los parámetros email y nombre en la URL o en el body");  // regresa el código 400

                string Server = Environment.GetEnvironmentVariable("Server");
                string UserID = Environment.GetEnvironmentVariable("UserID");
                string Password = Environment.GetEnvironmentVariable("Password");
                string Database = Environment.GetEnvironmentVariable("Database");

                string sc = "Server=" + Server + ";UserID=" + UserID + ";Password=" + Password + ";" + "Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(sc);
                conexion.Open();
#if INSERT
                try
                {
                    var cmd = new MySqlCommand();
                    cmd.Connection = conexion;
                    cmd.CommandText = "insert into usuarios(id_usuario,email,nombre,apellido_paterno,apellido_materno," +
                                      "fecha_nacimiento,telefono,genero) values(0,'a@c','nombre','apellido paterno'," +
                                      "'apellido materno','2021-01-01','1234567890','M')";
                    cmd.ExecuteNonQuery();
                    return new OkObjectResult("OK");
                }
                finally
                {
                    conexion.Close();
                }
#endif
#if SELECT
                try
                {
                    var cmd = new MySqlCommand("select id_usuario,email,nombre,apellido_paterno,apellido_materno," +
                                           "fecha_nacimiento,telefono,genero from usuarios where email=@email", conexion);
                    cmd.Parameters.AddWithValue("@email", email);
                    MySqlDataReader r = cmd.ExecuteReader();
                    List<Usuario> lista = new List<Usuario>();
                    while (r.Read())
                    {
                        Usuario usuario = new Usuario();
                        usuario.id_usuario = r.GetInt32(0);
                        usuario.email = r.GetString(1);
                        usuario.nombre = r.GetString(2);
                        usuario.apellido_paterno = r.GetString(3);
                        usuario.apellido_materno = r.GetString(4);
                        usuario.fecha_nacimiento = r.GetString(5);
                        usuario.telefono = r.GetString(6);
                        usuario.genero = r.GetString(7);
                        lista.Add(usuario);
                    }
                    return new OkObjectResult(JsonConvert.SerializeObject(lista));
                }
                finally
                {
                    conexion.Close();
                }
#endif
#if HTML
                try
                {
                    string html = "";

                    html += "<!DOCTYPE html>";
                    html += "<html>";
                    html += "<body>";
                    html += "<button type='button' onclick='alert(\"OK\")' style='width:200px;height:30px'>Aceptar</button></br>";
                    html += "</body>";
                    html += "</html>";

                    return new ContentResult {Content = html,ContentType = "text/html"};
                }
                finally
                {
                    conexion.Close();
                }
#endif
            }
            catch (Exception e)
            {
                return new BadRequestObjectResult(e.Message);
            }
        }
    }
}