package com.loderunner.project.database;

import java.sql.*;

public class DatabaseGame {
    
    public static int createGame(String mode){
        String sql = "INSERT INTO game(mode) VALUES(?)";
        try(Connection c = DatabaseManager.connect();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, mode);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    return rs.getInt(1);
                }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void addPlayerToGame(int gameId, String playerName){
        DatabasePlayer.addPlayer(playerName);
        int playerId = DatabasePlayer.getPlayerId(playerName);
        String sql = "INSERT INTO game_player(game_id, player_id) VALUES(?, ?)";
        try(Connection c = DatabaseManager.connect();
            PreparedStatement ps = c.prepareStatement(sql)){
                ps.setInt(1, gameId);
                ps.setInt(2, playerId);
                ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void refreshScore(int id, int score){
        String sql = "UPDATE game SET score = ? WHERE id = ?";
        try(Connection c = DatabaseManager.connect();
            PreparedStatement ps = c.prepareStatement(sql)){
                ps.setInt(1, score);
                ps.setInt(2, id);
                ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static String printGames(String mode){
        String sql = """
                SELECT g.id, g.mode, g.score, p.name
                FROM game g
                LEFT JOIN game_player gp ON g.id = gp.game_id
                LEFT JOIN player p ON gp.player_id = p.id
                WHERE g.mode = ?
                ORDER BY g.score DESC
                LIMIT 10
        """;

        try(Connection c = DatabaseManager.connect();
            PreparedStatement stmt = c.prepareStatement(sql)) {
                stmt.setString(1, mode);
                ResultSet rs = stmt.executeQuery();
                int lastId = -1;
                String response = "";
                while(rs.next()){
                    int idGame = rs.getInt("id");
                    if(idGame != lastId){
                        response += " \n";
                        response += "Mode" + rs.getString("mode") + "| Score : " + rs.getString("score") + "| Joueurs: ";
                        lastId = idGame;
                        if(rs.getString("name") != null){
                            response += rs.getString("name");
                        }
                    }else{
                        if(rs.getString("name") != null){
                            response += ", " + rs.getString("name");
                        }
                    }
                }
                return response;
        }catch(SQLException e){
            e.printStackTrace();
            return "Erreur";
        }
    }
}
