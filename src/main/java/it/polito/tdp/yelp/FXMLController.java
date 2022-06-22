/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	Business source= cmbLocale.getValue();
    	double x;
    	txtResult.clear();
    	if(source.equals(null)) {
    		txtResult.setText("Selezionare un locale.");
    		return;
    	}
    	
    	try {
    		x= Double.parseDouble(txtX.getText());
    		if(x<0 && x>1) {
    			txtResult.setText("Inserire un valore numerico compreso tra 0 e 1.");
        		return;
    		}
    	}catch(NumberFormatException e) {
    		txtResult.setText("Inserire un valore numerico compreso tra 0 e 1.");
    		return;
    	}
    		
    	List<Business> percorsoMigliore= new ArrayList<>(model.calcolaPercorso(source, x));
    	
    	txtResult.setText("Il percorso migliore è: \n");
    	for(Business b: percorsoMigliore) {
    		txtResult.appendText(b.getBusinessName()+ "\n");
    	}
    	
   }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String citta= cmbCitta.getValue();
    	int anno= cmbAnno.getValue();
    	if(citta==null || anno==0) {
    		txtResult.setText("Selezionare una citta e un anno.");
    		return;
    	}
    	model.creaGrafo(anno, citta);
    	txtResult.setText("GRAFO CREATO!\n");
    	txtResult.appendText("Numero di vertici: "+model.getVertici().size());
    	txtResult.appendText("\nNumero di archi: "+model.getNumeroArchi());
    	
    	cmbLocale.getItems().clear();
    	for(Business b: model.getVertici()) {
    		cmbLocale.getItems().add(b);
    	}
    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	txtResult.clear();
    	String citta= cmbCitta.getValue();
    	int anno= cmbAnno.getValue();
    	if(citta==null || anno==0) {
    		txtResult.setText("Selezionare una citta e un anno.");
    		return;
    	}
    	model.creaGrafo(anno, citta);
    	if(!model.getLocaleMigliore().equals(null))
    			txtResult.setText("Il locale migliore risulta essere: "+model.getLocaleMigliore().getBusinessName());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	cmbAnno.getItems().clear();
    	for(int i=2005;i<=2013; i++) {
    		cmbAnno.getItems().add(i);
    	}
    	
    	cmbCitta.getItems().clear();
    	for(String s: this.model.getAllCitta()) {
    		cmbCitta.getItems().add(s);
    	}
    }
}
