package com.ravat.hanzalah.securechat.net.server;

public final class BlockingConnection{
    boolean requiresBlocking;
    boolean isBlocking;
    Connection connection;
    BlockingConnection(Connection connection){
        this.connection = connection;
        requiresBlocking = true;
    }

    public interface BlockableConnection{
        public void performBlockTask();

        public boolean blockingStillRequired();
    }
}
