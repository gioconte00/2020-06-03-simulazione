package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Map<Integer, Player> idMap;  //id giocatori, giocatori
	private Graph<Player, DefaultWeightedEdge> grafo;
	

	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	
	public String creaGrafo(double x) {
		
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		idMap = new HashMap<>();
		this.dao.getVertici(x, idMap);
		
		//i vertici saranno i giocatori presenti nella idMap
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		for(Adiacenze a : this.dao.getArchi(x, idMap)) {
			if(!this.grafo.containsEdge(a.getP1(), a.getP2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
			}
		}
		
		return "Grafo creato con "+this.grafo.vertexSet().size()+" archi e "+this.grafo.edgeSet().size()+
					" archi.";
	}
	
	
	
	public Player giocatoreVincente() {
		
		Player vincente = null;
		
		for(Player p:this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p)>this.grafo.outDegreeOf(vincente))
				p=vincente;
		}
		
		return vincente;
		
	}
	
	
	
	public Set<DefaultWeightedEdge> giocatoriBattuti() {
		
		return this.grafo.outgoingEdgesOf(this.giocatoreVincente());
		
	}
	
}
