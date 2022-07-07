package fr.lesaffrefreres.rh.minibodet.model;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private static DataBase instance;

    private Connection connection;

    public static DataBase getInstance() {
        if(instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    protected DataBase() {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String path = appDirs.getUserDataDir("minibodet", "v0.1", "lesaffrefreres");
        try {
            // connection (and creation if not exists) to database
            Class.forName ("org.h2.Driver");
            connection = DriverManager.getConnection ("jdbc:h2:file:" + path + "/database", "","");
            System.out.println("jdbc:h2:file:" + path + "/database");
            connection.setAutoCommit(true);

            // execute SQL script to create if not exist all the tables

            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);

            Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/sql/script.sql")));

            runner.runScript(reader);

        } catch(ClassNotFoundException cnfe) {
            System.err.println("driver for H2 database not found");
            cnfe.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
