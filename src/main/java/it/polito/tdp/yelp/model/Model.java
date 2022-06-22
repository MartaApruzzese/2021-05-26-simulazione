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
