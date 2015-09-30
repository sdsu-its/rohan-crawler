package edu.sdsu.its.rohan_crawler;

import edu.sdsu.its.rohan_crawler.models.File;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.Properties;

/**
 * Communicate with a PostgreSQL Database.
 * Only 1 table is used, named file.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */

public class DB {
    private static DB instance;
    private static Connection connection;

    /**
     * Create DB Instance.
     * Will create the table if it does not exist.
     * The DB stores the name of the file, its size, its path, and the last time it was indexed.
     */
    private DB() {
        try {
            connection = getConnection();

            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("SELECT files");
            } catch (PSQLException e) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE files (" +
                        "  File_Name   TEXT," +
                        "  File_Size   TEXT," +
                        "  File_Path   TEXT," +
                        "  Last_seen   TIMESTAMP);");
            }
        } catch (SQLException e) {
            Logger.getLogger(getClass()).fatal("Problem connecting to DB.", e);
        }
    }

    /**
     * Connect to DB.
     *
     * @return {@link Connection} The Connection to the Database
     * @throws SQLException {@link SQLException} If the DB cannot be reached, or if there is a problem communicating with the DB.
     */
    private static Connection getConnection() throws SQLException {
        String username = new Config().getDb_user();
        String password = new Config().getDb_password();
        String dbUrl = "jdbc:postgresql://" + new Config().getDb_host() + "/" + new Config().getDb_name();
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("ssl", "true");
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");

        return DriverManager.getConnection(dbUrl, props);
    }

    public static void main(final String[] args) throws Exception {
        File test_file = new File();
        test_file.setFile_name("Test.mp4");
        test_file.setFile_size("1 GB");
        test_file.setFile_path("~/ondemand/nas/t_paulus/Test.mp4");

        instance.addFile(test_file);
    }

    /**
     * We only want one instance of the DB to insure that there are no conflicts.
     *
     * @return {@link DB} DB instance
     */
    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }

        return instance;
    }

    /**
     * Add a file to the DB
     *
     * @param file {@link File} The file to add to the DB.
     */
    public void addFile(final File file) {
        Logger.getLogger(getClass()).debug(String.format("Saving File with attributes\n\tName: %s\n\tSize: %s\n\tPath: %s", file.getFile_name(), file.getFile_size(), file.getFile_path()));

        boolean file_in_DB = false;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM files WHERE file_name='%s' AND file_path='%s';", file.getFile_name(), file.getFile_path()));
            resultSet.next();
            file_in_DB = !resultSet.getString("file_name").isEmpty();
        } catch (PSQLException e) {
            Logger.getLogger(getClass()).debug(String.format("File with name %s not found in DB", file.getFile_name()));
        } catch (SQLException e) {
            Logger.getLogger(getClass()).error("Problem reading from DB", e);
        }

        if (file_in_DB) {  // File already exists in Database. We need to update it.
            Logger.getLogger(getClass()).info(String.format("Updating %s in DB", file.getFile_name()));
            try {
                Statement statement = connection.createStatement();
                final String sql = String.format("UPDATE files SET file_size = '%s', last_seen = now() WHERE file_name = '%s' AND file_path = '%s';", file.getFile_size(), file.getFile_name(), file.getFile_path());

                Logger.getLogger(getClass()).debug(String.format("Executing SQL Command: %s", sql));
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {  // We need to add the file entry into the Database
            Logger.getLogger(getClass()).info(String.format("Adding %s to DB", file.getFile_name()));
            try {
                Statement statement = connection.createStatement();
                final String sql = String.format("INSERT INTO Files (File_Name, File_Size, File_Path, Last_seen)" +
                        " VALUES ('%s', '%s', '%s',  now());", file.getFile_name(), file.getFile_size(), file.getFile_path());

                Logger.getLogger(getClass()).debug(String.format("Executing SQL Command: %s", sql));
                statement.executeUpdate(sql);

            } catch (SQLException e) {
                Logger.getLogger(getClass()).error("Problem writing new file entry to DB", e);
            }
        }
    }

    /**
     * Remove old entries from the DB.
     * Old entries may result from files that are moved or deleted.
     */
    public void clean() {
        try {
            Logger.getLogger(getClass()).info("Removing Old Entries from DB");
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM files \n" +
                    "WHERE last_seen < date_trunc('day', NOW() - INTERVAL '" + new Config().getDb_decay_age() + "');");
        } catch (SQLException e) {
            Logger.getLogger(getClass()).warn("Problem Cleaning Old Entries from DB.", e);
        }
    }

}

