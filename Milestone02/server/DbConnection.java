/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Implements an IEDCS Server - IEDCS Database connection
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 * @see Server
 * @see Connection
 * @version 1.0
 */
public class DbConnection {

    Connection conn = null;

    /**
     * Constructs a database connection between IEDCS Server and MySQL database
     */
    public DbConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/seguranca2015";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, "user", "trabalhoseg2015");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public DbConnection(String dados) {
        String[] check = dados.split(":");

        try {
            String url = "jdbc:mysql://" + dados + "/seguranca2015";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, "user", "trabalhoseg2015");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes connection to database
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                // ignore errors
            }
        }
    }

    /**
     * Authenticates user in database
     *
     * @param username Username to authenticate
     * @param passwd User password
     * @return True if user is authorized. False otherwise
     */
    public boolean authorizeUser(String username, byte[] passwd) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call validateUser(?, ?, ?)}");

            cStmt.setString(1, username.toLowerCase());
            cStmt.setBytes(2, passwd);
            cStmt.setString(3, "@var");
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Register an user on database
     *
     * @param username Username
     * @param password Password hash
     * @param email Valid e-mail
     * @param userKey Generated User Key
     * @return True if successful registration. False otherwise
     */
    public boolean registerUser(String username, byte[] password, String email, byte[] userKey) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call registerUser(?, ?, ?, ?)}");
            //byte[] hash = check.hashPassword(password);

            cStmt.setString(1, username.toLowerCase());
            cStmt.setBytes(2, password);
            cStmt.setString(3, email.toLowerCase());
            cStmt.setBytes(4, userKey);
            cStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean registerUserCc(String username, String email, byte[] ccId, byte[] userKey) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call registerUserCc(?, ?, ?, ?)}");

            cStmt.setString(1, username.toLowerCase());
            cStmt.setString(2, email.toLowerCase());
            cStmt.setBytes(3, userKey);
            cStmt.setBytes(4, ccId);
            cStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Check catalog availability
     *
     * @return ArrayList containing all current titles on catalog
     */
    public ArrayList getFullCatalog() {
        ResultSet queryResult = null;
        ArrayList<String> resultados = new ArrayList<>();

        try {
            CallableStatement cStmt = conn.prepareCall("{call getCatalog()}");
            queryResult = cStmt.executeQuery();

            resultados.add(new String(new char[104]).replace("\0", "-"));
            resultados.add(String.format("|%-4s|%-40s|%-40s|%-15s|", "ID", "Titulo", "Autor", "Data Pub."));
            resultados.add(new String(new char[104]).replace("\0", "-"));

            while (queryResult.next()) {
                resultados.add(String.format("|%-4d|%-40s|%-40s|%-15s|",
                        queryResult.getInt("id_produto"), queryResult.getString("titulo"),
                        queryResult.getString("autor"), new SimpleDateFormat("dd/MM/yyyy").format(queryResult.getDate("data_pub"))));
            }
        } catch (SQLException ex) {
        }

        return resultados;
    }

    /**
     * Searches catalog for a specific keyword
     *
     * @param keyword Keyword to search
     * @return ArrayList containing entries matching the keyword
     */
    public ArrayList searchCatalog(String keyword) {
        ResultSet queryResult = null;
        ArrayList<String> resultados = new ArrayList<>();

        try {
            CallableStatement cStmt = conn.prepareCall("{call searchCatalog(?)}");
            cStmt.setString(1, keyword);
            queryResult = cStmt.executeQuery();

            resultados.add(new String(new char[104]).replace("\0", "-"));
            resultados.add(String.format("|%-4s|%-40s|%-40s|%-15s|", "ID", "Titulo", "Autor", "Data Pub."));
            resultados.add(new String(new char[104]).replace("\0", "-"));

            while (queryResult.next()) {
                resultados.add(String.format("|%-4d|%-40s|%-40s|%-15s|",
                        queryResult.getInt("id_produto"), queryResult.getString("titulo"),
                        queryResult.getString("autor"), new SimpleDateFormat("dd/MM/yyyy").format(queryResult.getDate("data_pub"))));
            }
        } catch (SQLException ex) {
        }

        return resultados;
    }

    /**
     * Searches Database for items bought by a specific client
     *
     * @param username Client username
     * @return ArrayList containing all titles available for client
     */
    public ArrayList searchUserCatalog(String username) {
        ResultSet queryResult = null;
        ArrayList<String> resultados = new ArrayList<>();

        try {
            CallableStatement cStmt = conn.prepareCall("{call searchUserCatalog(?)}");
            cStmt.setString(1, username);
            queryResult = cStmt.executeQuery();

            resultados.add(new String(new char[104]).replace("\0", "-"));
            resultados.add(String.format("|%-4s|%-40s|%-40s|%-15s|", "ID", "Titulo", "Autor", "Data Pub."));
            resultados.add(new String(new char[104]).replace("\0", "-"));

            while (queryResult.next()) {
                resultados.add(String.format("|%-4d|%-40s|%-40s|%-15s|",
                        queryResult.getInt("id_produto"), queryResult.getString("titulo"),
                        queryResult.getString("autor"), new SimpleDateFormat("dd/MM/yyyy").format(queryResult.getDate("data_pub"))));
            }
        } catch (SQLException ex) {
        }

        return resultados;
    }

    /**
     * Checks if a specific username has bought a specific item
     *
     * @param username Client username
     * @param item_id Item ID to verify
     * @return True if client has bought the item. False otherwise
     */
    public boolean checkOnUserCart(String username, int item_id) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call checkOnUserCart(?, ?, ?)}");

            cStmt.setString(1, username.toLowerCase());
            cStmt.setInt(2, item_id);
            cStmt.setString(3, "@var");
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Returns the path of a item on server file system
     *
     * @param item Item ID
     * @param username User username that requested file
     * @return Path for file
     */
    public String getPath(String item, String username) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call retrievePath(?,?)}");

            cStmt.setInt(1, Integer.parseInt(item));
            cStmt.setString(2, username);
            cStmt.execute();

            ResultSet rs = cStmt.executeQuery();
            String tmp = null;
            if (rs.next()) {
                tmp = rs.getString("path");
            }
            //rs.first();
            return tmp;
        } catch (SQLException ex) {
        }
        return "";
    }

    /**
     * Completes a purchase on database
     *
     * @param item_id Item ID to buy
     * @param username Username of client
     * @return False if purchase successfully completed. False otherwise
     */
    public boolean purchaseItem(String item_id, String username) {
        int id = 0;
        try {
            id = Integer.parseInt(item_id);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            CallableStatement cStmt = conn.prepareCall("{call purchaseItem(?, ?, ?)}");

            cStmt.setInt(1, id);
            cStmt.setString(2, username);
            cStmt.setString(3, "@var");
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            boolean cenas = rs.getBoolean("b");
            return cenas;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Changes e-mail on database
     *
     * @param username Username of client
     * @param prev Current e-mail address
     * @param fut Desired e-mail address
     * @return True if successfully changed. False otherwise.
     */
    public boolean changeEmail(String username, String prev, String fut) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call changeEmail(?, ?, ?)}");

            cStmt.setString(1, username.toLowerCase());
            cStmt.setString(2, prev);
            cStmt.setString(3, fut);
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Changes password on database
     *
     * @param username Username of client
     * @param prev Current password address
     * @param fut Desired password address
     * @return True if successfully changed. False otherwise.
     */
    public boolean changePw(String username, byte[] prev, byte[] fut) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call changePw(?, ?, ?)}");

            cStmt.setString(1, username.toLowerCase());
            cStmt.setBytes(2, prev);
            cStmt.setBytes(3, fut);
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Returns user key of a given user that is stored on database
     *
     * @param username Username of client
     * @return User Key of the specified client
     */
    public byte[] getUserKey(String username) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call getUserKey(?)}");

            cStmt.setString(1, username);
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBytes("user_key");
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Inserts a player on database. Only authorized players can reproduce
     * files.
     *
     * @param playerKey Player Key
     */
    public void registerPlayer(byte[] playerKey) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call registerPlayer(?)}");

            cStmt.setBytes(1, playerKey);
            cStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Register a new device on server
     *
     * @param deviceKey Device key
     * @param location Device location
     * @param os Device Operating System
     * @param playerKey Player key of player used
     */
    public void registerDevice(byte[] deviceKey, String location, String os, byte[] playerKey) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call registerDevice(?, ?, ?, ?)}");

            cStmt.setBytes(1, deviceKey);
            cStmt.setString(2, location);
            cStmt.setString(3, os);
            cStmt.setBytes(4, playerKey);
            cStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Recharges number of times a item can be retrieved
     *
     * @param id_prod Product ID
     * @param username User username
     */
    public void rebuyItem(int id_prod, String username) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call rebuyItem(?, ?}");

            cStmt.setInt(1, id_prod);
            cStmt.setString(2, username);
            cStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if player with this player key exists on database
     *
     * @param playerKey Player player key
     * @return True if player exists. False otherwise.
     */
    public boolean checkPlayerExistance(byte[] playerKey) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call checkPlayerExistance(?,?)}");

            cStmt.setBytes(1, playerKey);
            cStmt.setString(2, "@var");
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean authorizeUserCc(byte[] ccId) {
        try {
            CallableStatement cStmt = conn.prepareCall("{call validateUserCc(?, ?)}");

            cStmt.setBytes(1, ccId);
            cStmt.setString(2, "@var");
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getBoolean("b");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public String getUsernameCc(byte[] ccId){
        try {
            CallableStatement cStmt = conn.prepareCall("{call getUserCc(?)}");

            cStmt.setBytes(1, ccId);
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();
            rs.first();
            return rs.getString("nome");
        } catch (SQLException ex) {
        }
        return null;
    }
}
