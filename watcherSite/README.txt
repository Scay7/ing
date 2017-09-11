Requisiti per eseguire il progetto:
-Scaricare e installare xampp
-Dopo l'installazione avviare i server apache e mysql
-Cliccare su Admin di mysql e importare il db presente nella cartella db della root del progetto

-Nella root del progetto aprire la cartella db
-Copiare la cartella watcher in C:/


Configurare il server:
-Avviare eclipse
-Selezionare window -> show view -> servers
-Selezionare new server -> apache/tomcat 7.0
-Installarlo nella cartella C:/Xampp/tomcat
-Aggiungere il progetto al server
-Nella cartella dei servers, selezionare tomcat/web.xml e 
	copiare il seguente comando all'interno del tag <web-app>:
	<listener>
       <listener-class>watcher.logicaBusiness.gestori.ServletContextListener</listener-class>
    </listener>

NB: Se eclipse viene chiuso, con conseguente arresto di Tomcat, il progetto non si avvierà

Avviare il progetto:
-Avviare il server tomcat su eclipse
-Aprire il browser e digitare il seguente link http://localhost:8080/watcherSite/index.jsp