package com.lvsrobot.vehicletcp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class TCPServerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void Server() {
        TCPServer tcpServer = new TCPServer();
    }

    @Test
    public void NettyServer() throws InterruptedException {

        NettyServer nettyServer = new NettyServer(44444, mock(TCPCommAdapter.class));
//        nettyServer.setDaemon(true);
        nettyServer.start();
        nettyServer.join();

    }
}