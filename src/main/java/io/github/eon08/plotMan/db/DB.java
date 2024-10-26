package io.github.eon08.plotMan.db;

import io.github.eon08.plotMan.PlotMan;
import org.bukkit.Bukkit;
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

    private DB create() {
        File pluginDir = new File(DB_FOLDER);
        if (!pluginDir.exists()) {
            boolean dirCreated = pluginDir.mkdirs();
            if (dirCreated) {
                System.out.println("Plugin directory created: " + DB_FOLDER);
            } else {
                System.out.println("Failed to create plugin directory.");
                return this;
            }
        }
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try {
                if (dbFile.createNewFile()) {
                    System.out.println("Database file created: " + DB_PATH);
                    try (Connection connection = connect();
                         Statement statement = connection.createStatement())
                    {
                        String createTableSQL = "CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT);";
                        statement.execute(createTableSQL);
                        System.out.println("Settings table created.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to create database file.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Database file already exists: " + DB_PATH);
        }
        return this;
    }

    private @Nullable Connection connect() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public <T> T get(String key, @NotNull Class<T> clazz) {
        String selectSQL = "SELECT value FROM settings WHERE key = ?";
        byte[] blob = null;  // byte[]로 선언
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                blob = resultSet.getBytes("value"); // BLOB 타입으로 불러오기
                System.out.println("Setting loaded: " + key + " = " + blob);
            } else {
                System.out.println("Setting not found for key: " + key);
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
        } else {
            System.out.println("Blob is null for key: " + key);
        }
        return null;
    }

    public void set(String key, Serializable object) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {
            objStream.writeObject(object);  // 객체를 직렬화
            byte[] blob = byteStream.toByteArray();  // 바이트 배열로 변환
            String sql = "INSERT INTO settings (key, value) VALUES (?, ?) " +
                    "ON CONFLICT(key) DO UPDATE SET value = excluded.value;";
            try (Connection connection = connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, key);
                if (blob instanceof byte[]) {
                    preparedStatement.setBytes(2, (byte[]) blob);
                } else {
                    preparedStatement.setString(2, blob.toString());
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }  // BLOB 저장
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}