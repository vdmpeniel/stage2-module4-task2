package com.mjc.stage2.impl;


import com.mjc.stage2.Connection;

public class ProxyConnection implements Connection {
    private final RealConnection realConnection;
    private ConnectionPool connectionPool;

    public ProxyConnection(ConnectionPool connectionPool, RealConnection realConnection) {
        this.connectionPool = connectionPool;
        this.realConnection = realConnection;
    }

    public void reallyClose() {
        realConnection.close();
    }

    // Implement methods here!
    @Override
    public void close() {
        connectionPool.releaseConnection(this);
    }

    @Override
    public boolean isClosed() {
        return realConnection.isClosed();
    }

}
