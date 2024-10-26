package io.github.eon08.plotMan.db;

import io.github.eon08.plotMan.PlotMan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.sql.*;

public class DB {

    private final String DB_FOLDER;
    private final String DB_PATH;
    private final String JDBC_URL;

    public DB(String DB_FOLDER, String DB_FILE_NAME) {
        this.DB_FOLDER = System.getProperty("user.dir") + "\\plugins\\" + PlotMan.plugin.getName() + "\\" + DB_FOLDER;
        this.DB_PATH = this.DB_FOLDER + File.separator + DB_FILE_NAME;
        this.JDBC_URL = "jdbc:sqlite:" + DB_PATH;
        create();
    }

    public DB(String DB_FILE_NAME) {
        this.DB_FOLDER = System.getProperty("user.dir") + "\\plugins\\" + PlotMan.plugin.getName();
        this.DB_PATH = DB_FOLDER + File.separator + DB_FILE_NAME;
        this.JDBC_URL = "jdbc:sqlite:" + DB_PATH;
        create();
    }

    private void create() {
        File pluginDir = new File(DB_FOLDER);
        if (!pluginDir.exists()) {
            boolean dirCreated = pluginDir.mkdirs();
            if (!dirCreated) return;
        }
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try {
                if (dbFile.createNewFile()) {
                    try (Connection connection = connect();
                         Statement statement = connection.createStatement())
                    {
                        String createTableSQL = "CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT);";
                        statement.execute(createTableSQL);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private @Nullable Connection connect() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T get(String key, @NotNull Class<T> clazz) {
        String selectSQL = "SELECT value FROM settings WHERE key = ?";
        byte[] blob = null;
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                blob = resultSet.getBytes("value");
                System.out.println("Setting loaded: " + key + " = " + blob);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (blob != null) {
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(blob);
                 ObjectInputStream objStream = new ObjectInputStream(byteStream)) {
                return clazz.cast(objStream.readObject());  // 객체로 역직렬화
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void set(String key, Serializable object) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {
            objStream.writeObject(object);
            byte[] blob = byteStream.toByteArray();
            String sql = "INSERT INTO settings (key, value) VALUES (?, ?) " +
                    "ON CONFLICT(key) DO UPDATE SET value = excluded.value;";
            try (Connection connection = connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, key);
                if (blob instanceof byte[]) {
                    preparedStatement.setBytes(2, blob);
                } else {
                    preparedStatement.setString(2, blob.toString());
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}