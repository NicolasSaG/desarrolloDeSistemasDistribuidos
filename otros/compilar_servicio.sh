#!/bin/bash

#agregar en /usr/bin/
#chmod +x /usr/bin/archivo.sh
#alias compilar='/usr/bin/compilar_servicio.sh'

#Compilacion
echo "Compilando clase Servicio..."
# export CATALINA_HOME=

javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. negocio/Servicio.java

rm WEB-INF/classes/negocio/*

cp negocio/*.class WEB-INF/classes/negocio/.
echo "Creando archivo war..."
jar cvf Servicio.war WEB-INF META-INF


#apagar servidor
echo "apagando tomcat..."
sh $CATALINA_HOME/bin/catalina.sh stop

echo "eliminando anterior app..."
#limpiar lo del servidor
rm $CATALINA_HOME/webapps/Servicio.war
rm -R $CATALINA_HOME/webapps/Servicio

#copia a tomcat
echo "copiando nuevo war..."
cp Servicio.war $CATALINA_HOME/webapps/

#iniciar servidor
echo "iniciando servidor..."
sh $CATALINA_HOME/bin/catalina.sh start

echo "Hemos terminado."
