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

    public static void printGames(){
        String sql = """
                SELECT g.mode, g.score, p.name
                FROM game g
                LEFT JOIN game_player gp ON g.id = gp.game_id
                LEFT JOIN player p ON gp.player_id = p.id
                ORDER BY g.id
                LIMIT 10
        """;

        try(Connection c = DatabaseManager.connect();
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()){
                    System.out.print("Mode" + rs.getString("mode") + "| Score : " + rs.getString("score") + "| Joueurs: ");
                    if(rs.getString("name") != null){
                        System.out.print(rs.getString("name"));
                    }
                    System.out.println();
                }
                System.out.println();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
