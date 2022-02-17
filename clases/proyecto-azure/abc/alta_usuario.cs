using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System.Collections.Generic;
using MySql.Data.MySqlClient;

namespace abc
{
    public static class alta_usuario
    {
        [FunctionName("alta_usuario")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                dynamic data = JsonConvert.DeserializeObject(requestBody);


                //validacion de datos
                //variables de entorno
                string Server = Environment.GetEnvironmentVariable("Server");
                string UserID = Environment.GetEnvironmentVariable("UserID");
                string Password = Environment.GetEnvironmentVariable("Password");
                string Database = Environment.GetEnvironmentVariable("Database");

                string sc = "Server=" + Server + ";UserID=" + ";Password=" + Password + ";Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(sc);
                conexion.Open();

                //iniciar transaccion
                //insert 
                MySqlTransaction t = conexion.BeginTransaction();
                try
                {
                    var cmd_2 = new MySqlCommand();
                    cmd_2
                        .Connection = conexion;
                    cmd_2.CommandText = "insert into usuarios(id_usuario,email,nombre,apellido_paterno,apellido_materno," +
                                      "fecha_nacimiento,telefono,genero) values(0,'"+ data.email+ "','" + data.nombre + "','" + data.apellido_paterno + "','" + data.apellido_materno + "'," +
                                      "'" + data.fecha_nacimiento.ToString("yyyy-MM--DD HH:mm:ss") + "','" + data.telefono + "','" + data.genero + ")";
                    cmd_2.ExecuteNonQuery();
                
                    if(data.foto != null)
                    {
                        var cmd_3 = new MySqlCommand();
                        cmd_3.Connection = conexion;
                        cmd_3.CommandText = "insert into fotos_usuario values(0, @foto, (select id_usuario from usaurios where email=@email))";
                        cmd_3.Parameters.AddWithValue("@foto", Convert.FromBase64String((string)data.foto));
                        cmd_3.Parameters.AddWithValue("@email", data.email);
                        cmd_3.ExecuteNonQuery();
                        return new OkObjectResult("ok");
                    }
                }
                catch (Exception e)
                {
                    return new BadRequestObjectResult(e.Message);
                }
            }
            catch (Exception e)
            {

                return new BadRequestObjectResult(e.Message);
            }
            
            return new OkObjectResult("ok");
        }
    }
}
