package com.loderunner.project.database;

import java.sql.*;

/**
 * Gestion des opérations liées aux joueurs dans la base de données.
 * Permet l'ajout d'un joueur et la récupération de son identifiant.
 */

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init(){
        try(Connection conn = connect() ;
            Statement stmt = conn.createStatement()) {
                String sql1 = """
                        CREATE TABLE IF NOT EXISTS player (
                            id  INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT UNIQUE
                        );
                    """;
                String sql2 = """
                        CREATE TABLE IF NOT EXISTS game(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            mode TEXT,
                            score INTEGER DEFAULT 0
                        );
                    """;
                String sql3 = """
                        CREATE TABLE IF NOT EXISTS game_player(
                            game_id INTEGER,
                            player_id INTEGER,
                            PRIMARY KEY(game_id, player_id),
                            FOREIGN KEY(game_id) REFERENCES game(id),
                            FOREIGN KEY(player_id) REFERENCES player(id)
                        );
                    """;
                    stmt.execute(sql1);
                    stmt.execute(sql2);
                    stmt.execute(sql3);
                    System.out.println("Base prete");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}

