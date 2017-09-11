package watcher.logicaBusiness.gestori;

import javax.servlet.ServletContextEvent;

/**
 * La classe gestisce l'avvio dei thread all'avvio del server
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class ServletContextListener implements javax.servlet.ServletContextListener{
	//TODO (a fine progetto): inserire in web.xml il package della classe se dovesse essere cambiato
	
	/**
	 *	Metodo invocato automaticamente dal server al suo avvio: Vengono avviati i thread dichiarati all'interno di questo metodo 
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Server avviato");
	}
	
	/**
	 * Medoto invocato automaticamente dal server al momento del suo spegnimento: Vengono distrutti i thread dichiarati all'interno di questo metodo
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server disattivato");
	}
}
