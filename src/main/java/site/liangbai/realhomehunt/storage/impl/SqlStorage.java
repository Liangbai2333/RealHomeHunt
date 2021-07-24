/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.storage.impl;

import org.bukkit.util.Consumer;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;
import site.liangbai.realhomehunt.util.Serialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class SqlStorage implements IStorage {
    private static final String INIT_TABLE_SQL = "create table if not exists %s (residence blob not null, owner text not null)";

    private static final String QUERY_RESIDENCES_SQL = "select * from %s";

    private static final String INSERT_RESIDENCE_SQL = "insert into %s (residence, owner) values (?, ?)";

    private static final String UPDATE_RESIDENCE_SQL = "update %s set residence = ? where owner = ?";

    private static final String DELETE_RESIDENCE_SQL = "delete from %s where owner = ?";

    private final List<String> owners = new ArrayList<>();

    private int count;

    public abstract Connection getConnection() throws SQLException;

    private void handleConnection(Consumer<Connection> connectionConsumer) {
        Connection connection = null;

        try {
            connection = getConnection();

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

    public void initTable() {
        handlePrepared(String.format(INIT_TABLE_SQL, Config.storage.tableSetting.residenceTable),it -> {
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
            handlePrepared(String.format(INSERT_RESIDENCE_SQL, Config.storage.tableSetting.residenceTable), it -> doUpdateResidence(it, residence, false));

            return;
        }

        handlePrepared(String.format(UPDATE_RESIDENCE_SQL, Config.storage.tableSetting.residenceTable), it -> doUpdateResidence(it, residence, true));
    }

    private void doUpdateResidence(PreparedStatement statement, Residence residence, boolean update) {
        try {
            statement.setBytes(1, Serialization.toByteArray(residence));

            statement.setString(2, residence.getOwner());

            statement.executeUpdate();

            if (!update) owners.add(residence.getOwner());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Residence residence) {
        handlePrepared(String.format(DELETE_RESIDENCE_SQL, Config.storage.tableSetting.residenceTable), it -> {
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

        handlePrepared(String.format(QUERY_RESIDENCES_SQL, Config.storage.tableSetting.residenceTable), it -> {
            try {
                ResultSet resultSet = it.executeQuery();
                while (resultSet.next()) {
                    byte[] residenceBytes = resultSet.getBytes("residence");

                    String owner = resultSet.getString("owner");

                    owners.add(owner);

                    Residence residence = Serialization.fromByteArray(residenceBytes);

                    count++;

                    list.add(residence);
                }
            } catch (SQLException e) {
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
