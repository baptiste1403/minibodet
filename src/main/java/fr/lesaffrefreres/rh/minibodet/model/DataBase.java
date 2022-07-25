package fr.lesaffrefreres.rh.minibodet.model;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class represents the Singleton managing the connection to the database.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class DataBase {

    private static DataBase instance;

    private Connection connection;

    /**
     * Return the instance of the DataBase. containing the connection to the database.
     * @return the instance of the DataBase.
     */
    public static DataBase getInstance() {
        if(instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    /**
     * Return the connection to the database.
     * Don't close the connection. It's done by the DataBase. If the connection is closed, Database operations will fail.
     * @return the connection to the database.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * protected constructor.
     * Create the connection to the database, create the database if it doesn't exist.
     * If the database already exists, it will be opened.
     */
    protected DataBase() {
        AppDirs appDirs = AppDirsFactory.getInstance(); // Get the AppDirs instance. to get the path to the user's application data.
        String path = appDirs.getUserDataDir("minibodet", "v0.1", "lesaffrefreres"); // create the path to the database.
        try {
            // connection (and creation if not exists) to database
            Class.forName ("org.h2.Driver");
            connection = DriverManager.getConnection ("jdbc:h2:file:" + path + "/database", "",""); // create the connection to the database.
            System.out.println("jdbc:h2:file:" + path + "/database");
            connection.setAutoCommit(true);

            // execute SQL script to create if not exist all the tables

            ScriptRunner runner = new ScriptRunner(connection); // create the ScriptRunner to execute the SQL script.
            runner.setAutoCommit(true);

            Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/sql/script.sql")));

            runner.runScript(reader); // execute the SQL script.

        } catch(ClassNotFoundException cnfe) {
            System.err.println("driver for H2 database not found");
            cnfe.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
