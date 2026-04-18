package com.loderunner.project.database;

import java.sql.*;

/**
 * Gestion des joueurs dans la base de données.
 * Permet d'ajouter un joueur et de récupérer son identifiant.
 */

public class DatabasePlayer {

    public static void addPlayer(String name){
        String sql = "INSERT OR IGNORE INTO player(name) VALUES(?)";
        try(Connection c = DatabaseManager.connect();
            PreparedStatement ps = c.prepareStatement(sql)){
                ps.setString(1, name);
                ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static int getPlayerId(String name){
        String sql = "SELECT id FROM player WHERE name = ?";
        try(Connection c = DatabaseManager.connect();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt("id");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return -1;
    }
}
