package site.liangbai.realhomehunt.storage.impl;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;
import site.liangbai.realhomehunt.util.JdbcUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class SqliteStorage implements IStorage {
    private static final String INIT_TABLE_SQL = "create table if not exists residences (residence blob not null, owner text not null)";

    private static final String QUERY_RESIDENCES_SQL = "select * from residences";

    private static final String INSERT_RESIDENCE_SQL = "insert into residences (residence, owner) values (?, ?)";

    private static final String UPDATE_RESIDENCE_SQL = "update residences set residence = ? where owner = ?";

    private static final String DELETE_RESIDENCE_SQL = "delete from residences where owner = ?";

    private final String path;

    private final Plugin plugin;

    private final List<String> owners = new ArrayList<>();

    private int count;

    public SqliteStorage(Plugin plugin, String path) {
        JdbcUtil.init();

        this.plugin = plugin;
        this.path = path;

        initTable();
    }

    private void handleConnection(Consumer<Connection> connectionConsumer) {
        Connection connection = null;

        try {
            connection = JdbcUtil.getConnection(plugin, path);

            connectionConsumer.accept(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePrepared(String sql, Consumer<PreparedStatement> connectionConsumer) {
        handleConnection(it -> {
            PreparedStatement statement = null;

            try {
                statement = it.prepareStatement(sql);

                connectionConsumer.accept(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initTable() {
        handlePrepared(INIT_TABLE_SQL, it -> {
            try {
                it.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void save(Residence residence) {
        if (!owners.contains(residence.getOwner())) {
            handlePrepared(INSERT_RESIDENCE_SQL, it -> doUpdateResidence(it, residence, false));

            return;
        }

        handlePrepared(UPDATE_RESIDENCE_SQL, it -> doUpdateResidence(it, residence, true));
    }

    private void doUpdateResidence(PreparedStatement statement, Residence residence, boolean update) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(residence);

            statement.setBytes(1, byteArrayOutputStream.toByteArray());

            statement.setString(2, residence.getOwner());

            statement.executeUpdate();

            if (!update) owners.add(residence.getOwner());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Residence residence) {
        handlePrepared(DELETE_RESIDENCE_SQL, it -> {
            try {
                it.setString(1, residence.getOwner());

                it.executeUpdate();

                owners.remove(residence.getOwner());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<Residence> loadAll() {
        List<Residence> list = new LinkedList<>();

        handlePrepared(QUERY_RESIDENCES_SQL, it -> {
            try {
                ResultSet resultSet = it.executeQuery();

                while (resultSet.next()) {
                    byte[] residenceBytes = resultSet.getBytes("residence");

                    String owner = resultSet.getString("owner");

                    owners.add(owner);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(residenceBytes);

                    BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                    Object resObj = objectInputStream.readObject();

                    count++;

                    if (!(resObj instanceof Residence)) continue;

                    Residence residence = ((Residence) resObj);

                    list.add(residence);
                }
            } catch (SQLException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return list;
    }

    @Override
    public int count() {
        return count;
    }
}
