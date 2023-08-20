package com.mjc.stage2.impl;


import com.mjc.stage2.Connection;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.IntStream;

public class ConnectionPool {
    private static final int POOL_SIZE = 10;
    private static final String URL = "url";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static ConnectionPool instance;
    private Queue<ProxyConnection> freeConnections;
    private Queue<ProxyConnection> usedConnections;

    private ConnectionPool() {
        freeConnections = new ArrayDeque<>(POOL_SIZE);
        usedConnections = new ArrayDeque<>();
        IntStream.range(0, POOL_SIZE)
                .mapToObj(i -> new ProxyConnection(this, new RealConnection(URL, LOGIN, PASSWORD)))
                .forEach(freeConnections::offer);
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    public Connection getConnection() {
        ProxyConnection connection = freeConnections.poll();
        usedConnections.offer(connection);
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (connection.getClass() == ProxyConnection.class) {
            usedConnections.remove(connection);
            freeConnections.offer((ProxyConnection) connection);
        } else {
            throw new RuntimeException(String.format("Wrong connection is detected: %s, should be ProxyConnection.class ", connection.getClass()));
        }
    }

    public void destroyPool() {
        freeConnections.forEach(ProxyConnection::reallyClose);
    }

    public int getFreeConnectionsCount() {
        return freeConnections.size();
    }

    public int getUsedConnectionsCount() {
        return usedConnections.size();
    }



    /* Testing */
    public static void main(String[] args) {
        ConnectionPool pool = ConnectionPool.getInstance();
        System.out.format("Free connections: %s\n", pool.getFreeConnectionsCount());

        pool.getConnection();
        pool.getConnection().close();
        pool.releaseConnection(pool.getConnection());
        pool.destroyPool();

        System.out.format("Free connections: %s\n", pool.getFreeConnectionsCount());
        System.out.format("Used connections: %s\n", pool.getUsedConnectionsCount());
    }
}
