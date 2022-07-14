package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenze;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(double x, Map<Integer, Player> idMap) {
		
		String sql = "SELECT p.PlayerID AS id, p.Name AS nome "
				+ "FROM actions a, matches m, players p  \n"
				+ "WHERE a.MatchID=m.MatchID AND p.PlayerID=a.PlayerID "
				+ "GROUP BY a.PlayerID "
				+ "HAVING avg(a.Goals)>?";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {
		
			Player player = new Player(res.getInt("id"), res.getString("nome"));
			idMap.put(player.getPlayerID(), player);
	}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	
	public List<Adiacenze> getArchi (double x, Map<Integer, Player> idMap) {
		
		String sql = "SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, a1.TimePlayed, a2.TimePlayed, (a1.TimePlayed-a2.TimePlayed) AS peso "
				+ "FROM actions a1, actions a2, matches m "
				+ "WHERE a1.MatchID=a2.MatchID AND a1.TeamID!=a2.TeamID AND a1.PlayerID!=a2.PlayerID "
				+ "AND a1.`Starts`=1 AND a2.`Starts`=1 AND a1.MatchID=m.MatchID "
				+ "GROUP BY a1.PlayerID, a2.PlayerID, a1.TimePlayed, a2.TimePlayed "
				+ "HAVING  AVG(a1.Goals)>=? AND AVG(a2.Goals)>=? AND a1.TimePlayed > a2.TimePlayed ";
		
		List<Adiacenze> result = new ArrayList<Adiacenze>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			st.setDouble(2, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {
		
				result.add(new Adiacenze(idMap.get(res.getInt("id1")), idMap.get(res.getInt("id2")), 
											res.getDouble("peso")));
			}
			
		conn.close();
		return result;
					
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
					
		}
	}
	
	
	
	
	
}
