package com.empresa.modelo;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConexionPool {

    private Connection conn;

    public ConexionPool() {
        conn = null;
    }

    public void conectar() {
        try {
            Context initCtx;
            initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup("jdbc/empresa");
            System.out.println("*********************");
            System.out.println("SE CONECTO");
            System.out.println("*********************");
            conn = ds.getConnection();
        } catch (NamingException | SQLException e) {
            System.out.println("*********************");
            System.out.println("db: " + e.getMessage());
            System.out.println("*********************");
        }
    }

    public Connection getConexion() {
        return conn;
    }

    public void desconectar() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
