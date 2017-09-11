package watcher.logicaBusiness.gestori;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVWriter;

import watcher.logicaBusiness.entit‡.DatoRilevazione;
import watcher.logicaIntegrazione.DaoTrasferimento;

/**
 * Classe (Singleton) per la gestione del trasferimento delle rilevazioni 
 *  
 * @author Ivan Lamparelli, Graziano Accogli
 */
public class Trasferitore implements Runnable {

	/**L'intervallo di tempo per effettuare il trasferimento*/
	private final static long MILLISEC_INTERVALLO_TRASFERIMENTO = 30 * 60 * 1000; //30 minuti

	/**L'ISTANZA Singleton del trasferitore*/
	private final static Trasferitore ISTANZA = new Trasferitore();	
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	private final static Logger LOGGER = Logger.getLogger(Trasferitore.class.getName());
	
	/**Il dao a cui richiedere i dati del trasferimento*/
	private final DaoTrasferimento daoTA = new DaoTrasferimento();
	
	
	
	/**La lista in cui salvare le righe di dati da scrivere sul file da trasferire ad un cliente*/
	LinkedList<String[]> righeDatiDaTrasferire = null;
	
	
	
	/**
	 * Restituisce l'ISTANZA Singleton del Trasferitore
	 * @return L'ISTANZA Singleton
	 */
	public static Trasferitore getIstanza() {
		return ISTANZA;
	}
	
	
	/**
	 * Il costruttore privato (Per istanziare dall'esterno va usato il metodo getIstanza()
	 */
	private Trasferitore() {}
	
	
	/**
	 * Specifica le istruzioni da eseguire quando viene avviato un thread contenente questo oggetto Runnable
	 */

	@Override
	public void run() {
		
		/**La lista in cui salvare l'elenco di clienti con il trasferimento attivo*/
		LinkedList<String> clientiTrasferimento = null;
		
	
		try {
			while (true) {
	
					clientiTrasferimento = daoTA.mostraClientiConTrasferimentoAttivo();
				
				//Se ci sono clienti con il trasferimento attivo, effettuo il trasferimento
				if (clientiTrasferimento != null && clientiTrasferimento.size() > 0) {
					
					//Per ogni cliente, procedo col trasferimento
					for (String cliente : clientiTrasferimento) {	
							trasferisciDati(cliente);
					}
				}
					
					//Ripeto il trasferimento una volta trascorso l'intervallo di tempo definito
					Thread.sleep(MILLISEC_INTERVALLO_TRASFERIMENTO);
			}
		
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella lettura dei clienti con il trasferimento attivo");

		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella messa in pausa del thread di trasferimento file");
		}
		
	
	}
	
	private void trasferisciDati(String unCliente) {

		/**L'oggetto da utilizzare per inviare le email ai clienti*/
		GestoreEmail connettoreMail = new GestoreEmail();
		
		/**La lista in cui salvare i dati da trasferire ad un cliente*/
		LinkedList<DatoRilevazione> datiDaTrasferire = null;
		
		/**L'indirizzo a cui inviare i dati di un cliente*/
		String indirizzoInvio = null;
		
		if (daoTA.ËAttivoTrasferimento(unCliente)) {
			
			try {
				//Individuo i dati da trasferire per il cliente
				datiDaTrasferire = daoTA.mostraDatiDaTrasferire(unCliente);
				
				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nella visualizzazione dei dati da trasferire");
			}
			
			//Se il cliente ha dei dati da trasferire, effettuo il trasferimento
			if (datiDaTrasferire != null && datiDaTrasferire.size() > 0) {
				
				righeDatiDaTrasferire = new LinkedList<String[]>();
				
				//Converto i dati da trasferire in array di celle da inserire in un file excel
				for(DatoRilevazione rilevazione : datiDaTrasferire) {
					righeDatiDaTrasferire.add(convertiRilevazioneInRigaCelleCsv(rilevazione));
				}
				
				//Creo il file con le rilevazioni
				File rilevazioni = scriviDatiSuCsv(unCliente, righeDatiDaTrasferire);
				
				//Individuo l'indirizzo a cui inviare i dati
				if (daoTA.ËAttivaTerzaParte(unCliente)) {
					try {
						indirizzoInvio = daoTA.mostraTerzaParte(unCliente);
					} catch (SQLException e) {
						LOGGER.log(Level.SEVERE, "Problemi nel cercare la terza parte relativa al cliente inserito");
					}
				} else {
					indirizzoInvio = unCliente;
				}
				
				connettoreMail.inviaEmail(indirizzoInvio, scriviOggettoMailTrasferimento(), scriviCorpoMailTrasferimento(unCliente), rilevazioni, rilevazioni.getName());
		
			} 
		
		} 
		
 	}
		
	private File scriviDatiSuCsv(String unCliente, LinkedList<String[]> unArrayDiRighe) {
	 	
		File rilevazioniCsv = new File("rilevazioni csv" + File.separator + "rilevazioni " + mostraDataOdierna() + ".csv");
						
		CSVWriter scrittoreCsv = null;
		
		try {
			scrittoreCsv = new CSVWriter(new FileWriter(rilevazioniCsv), ';');
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problema con il costruttore del Csv");
		}
		
		//Scrivo i nomi delle colonne
		String[] nomiColonne = {"Ambiente", "Tipo sensore", "Codice sensore", "Valore", "Errore", "Unit‡ di misura", "Data", "Messaggio"};
		scrittoreCsv.writeNext(nomiColonne);
		
		//Popolo il file
		for (String[] riga : unArrayDiRighe) {
			scrittoreCsv.writeNext(riga);
		}
		
     	try {
			scrittoreCsv.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problema con la chiusura del Csv");
		}
     	
     	return rilevazioniCsv;
	}
	
	private String[] convertiRilevazioneInRigaCelleCsv(DatoRilevazione unaRilevazione) {
		String[] rigaCelleCsv = new String[8];
		
		//cella 1, nome ambiente
		rigaCelleCsv[0] = unaRilevazione.getNomeAmbiente();
		
		//cella 2, tipo sensore
		rigaCelleCsv[1] = unaRilevazione.getTipoSensore();
		
		//cella 3, codice sensore
		rigaCelleCsv[2] = unaRilevazione.getCodiceSensore();
		
		//cella 4, valore rilevazione
		rigaCelleCsv[3] = Integer.toString(unaRilevazione.getValore());
		
		//cella 5, flag errore
		rigaCelleCsv[4] = Boolean.toString(unaRilevazione.ËErrore());
		
		//cella 6, unit‡ misura rilevazione
		String unit‡DiMisura = unaRilevazione.getUnit‡DiMisura();
		if (unit‡DiMisura == null) {
			unit‡DiMisura = "";
		}
		rigaCelleCsv[5] = unit‡DiMisura;
		
		//cella 7, data rilevazione
		rigaCelleCsv[6] = unaRilevazione.getData().toString().substring(0, 16);
		
		//cella 8, messaggio rilevazione
		rigaCelleCsv[7] = unaRilevazione.getMessaggio();
						
		return rigaCelleCsv;
	}
	
	
	private String scriviOggettoMailTrasferimento() {
		String oggetto = "Rilevazioni " + mostraDataOdierna();
		
		return oggetto;
	}
	
	
	private String scriviCorpoMailTrasferimento(String unCliente) {
		StringBuilder corpo = new StringBuilder();		
		LinkedList<String> ambientiTrasf = null;
		
		try {
			ambientiTrasf = daoTA.mostraAmbientiFiltrati(unCliente);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare i dati dell'ambiente inserito");
		}
		
		corpo.append("In allegato sono presenti le rilevazioni fino alla data " + mostraDataOdierna() + " degli ambienti:");
		
		for (String ambiente : ambientiTrasf) {
			corpo.append("<br>- " + ambiente);
		}
		
		return corpo.toString();
	}
	
	
	private String mostraDataOdierna() {
		String oggiInStringa;
		
		long oggiInMillisec = Calendar.getInstance().getTimeInMillis();
		Timestamp oggiInTimestamp = new Timestamp(oggiInMillisec);

		//la data in formato GG-MM-AAAA
		oggiInStringa = oggiInTimestamp.toString().substring(0, 10); 
		
		return oggiInStringa;
	}
	
	
	public static void main(String[] args) {
		Trasferitore trasf = Trasferitore.getIstanza();
		Thread t = new Thread(trasf);
		
		t.start();
		System.out.println("exec");	
	}
	
	
}