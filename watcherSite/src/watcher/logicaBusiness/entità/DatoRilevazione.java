package watcher.logicaBusiness.entitā;

import java.sql.Timestamp;

/**
 * Classe per raccogliere le informazioni su un dato di rilevazione
 * @author Ivan Lamparelli
 *
 */
public class DatoRilevazione {
	private String nomeAmbiente;	
	private String tipoSensore;
	private String codiceSensore;
	private int valore;
	private boolean errore;
	private String unitāDiMisura;
	private Timestamp data;
	private String messaggio;
	
	public DatoRilevazione(String unNomeAmbiente, String unTipoSensore, String unCodiceSensore, 
			int unValore, boolean flagErrore, String unaUnitāDiMisura, Timestamp unaData, String unMessaggio) {
		this.nomeAmbiente = unNomeAmbiente;
		this.tipoSensore = unTipoSensore;
		this.codiceSensore = unCodiceSensore;
		this.valore = unValore; 
		this.errore = flagErrore;
		this.unitāDiMisura = unaUnitāDiMisura;
		this.data = unaData;
		this.messaggio = unMessaggio;
	}
	
	public String getNomeAmbiente() {
		return nomeAmbiente;
	}
	
	public String getTipoSensore() {
		return tipoSensore;
	}
	
	public String getCodiceSensore() {
		return codiceSensore;
	}
	
	public int getValore() {
		return valore;
	}
	
	public boolean čErrore() {
		return errore;
	}
	
	public String getUnitāDiMisura() throws NullPointerException {
		return unitāDiMisura;
	}
		
	public Timestamp getData() {
		return data;
	}
	
	public String getMessaggio() {
		return messaggio;
	}
	
}
