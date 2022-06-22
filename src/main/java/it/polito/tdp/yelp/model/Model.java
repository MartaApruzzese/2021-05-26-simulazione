package it.polito.tdp.yelp.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<String, Business> idMap;
	private List<Business> best;
	private double x;
	private Business localeMigliore;
	
	public Model() {
		this.dao= new YelpDao();
	}
	
	public void creaGrafo(int anno, String citta) {
		this.grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap= new HashMap<String, Business>();
		this.vertici= new ArrayList<>();
		
		//Popolo idMap
		for(Business b: dao.getAllBusiness()) {
			idMap.put(b.getBusinessId(), b);
		}
		
		//Aggiungo i vertici
		for(String id: dao.getVertici(anno, citta)) {
			vertici.add(idMap.get(id));
		}
		Graphs.addAllVertices(this.grafo, vertici);
		
		//Aggiungo gli archi.
				//ho una mappa con id e media. Calcolo per ogni coppia quale è più grande
		Map<String, Double> medie= dao.getBusinessConMedia(anno, citta);
		for(Business b1: this.vertici) {
			for(Business b2: this.vertici) {
				if(b1.getBusinessId().compareTo(b2.getBusinessId())!=0) {
					double media1= medie.get(b1.getBusinessId());
					double media2=medie.get(b2.getBusinessId());
					if(media1>media2){
						//media di b1 maggiore di b2, arco da b2 a b1
						double diff= media1-media2;
						Graphs.addEdgeWithVertices(this.grafo, b2, b1, diff);
					}else if(media1<media2) {
						//media b1 minore di b2, arco da b1 a b2
						double diff=media2-media1;
						Graphs.addEdgeWithVertices(this.grafo, b1, b2, diff);
					}		
				}
			}
		}
		
		System.out.println("Grafo creato.");
		System.out.println("#VERTICI: "+grafo.vertexSet().size());
		System.out.println("#ARCHI: "+grafo.edgeSet().size());
		
	}
	
	public Business getLocaleMigliore() {
		
		 localeMigliore=null;
		 double pesoMigliore=0;
		 for(Business b: this.vertici) {
			 double entranti=0;
			 double uscenti=0;
			 //Differenza tra entranti meno uscenti max
			 for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b)) {
				 entranti=entranti + this.grafo.getEdgeWeight(e);
			 }
			 for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b)) {
				 uscenti=uscenti+this.grafo.getEdgeWeight(e);
			 }
			if(entranti-uscenti>pesoMigliore) {
				pesoMigliore=entranti-uscenti;
				localeMigliore=b;
			}
			
		 }
	
		return localeMigliore;
	}
	
	
	/**
	 * RICORSIONE: parto da localeDiPartenza, voglio arrivare al locale migliore, con
				il percorso minore, ma gli archi devono essere maggiori, uguali di x.
	 */
	
	public List<Business> calcolaPercorso(Business b, double x){
		this.best= new ArrayList<Business>();
		this.x= x;
		List<Business> parziale= new ArrayList<>();
		
		
		parziale.add(b);
		
		cerca(parziale,1);
		return best;
	}
	
	
	private void cerca(List<Business> parziale, int livello) {
		//Condizioni di uscita
		if(parziale.get(parziale.size()-1).equals(localeMigliore)) {
			//è il best?
			if(best.isEmpty()) {
				best=new ArrayList<>(parziale);
			}
			if(parziale.size()<best.size()) {
				best= new ArrayList<>(parziale);
			}
			return;
		}
		
		//condizioni di ricorsione
		/*for(Business b: Graphs.neighborListOf(this.grafo,parziale.get(livello-1) )) {
			if(!parziale.contains(b)) {
				if(this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(livello-1), b))>=x) {
					parziale.add(b);
					cerca(parziale, livello+1);
					parziale.remove(b);
				}
			}
		}*/
		
		//Ricordati che sono orientati, ho bisogno solo della lista di quelli uscenti, non entranti.
		Business source= parziale.get(parziale.size()-1);
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(source)) {
			Business target = this.grafo.getEdgeTarget(e);
			if(this.grafo.getEdgeWeight(e)>=x && !parziale.contains(target)) {
				parziale.add(target);
				cerca(parziale, livello+1);
				parziale.remove(target);
			}
		}
		
		
		
	}

	public List<Business> getVertici(){
		return this.vertici;
	}	
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	public List<String> getAllCitta(){
		return dao.getAllCitta();
	}
	
}
