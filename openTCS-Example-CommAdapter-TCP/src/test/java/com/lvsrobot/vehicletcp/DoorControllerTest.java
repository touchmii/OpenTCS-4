package com.lvsrobot.vehicletcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

import com.lvsrobot.vehicletcp.DoorController;

import java.io.IOException;

class DoorControllerTest {

//    private String

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDoorStatus() throws IOException {
        DoorStatus rec = DoorController.doorAction(DoorController.JN2, DoorController.DOORACTION.STATUS);
        System.out.println(rec);
        rec = DoorController.doorAction(DoorController.JN2, DoorController.DOORACTION.OPEN);
        System.out.println(rec);
        try {

            Thread.sleep(6000);
        } catch (Exception e) {}
        rec = DoorController.doorAction(DoorController.JN2, DoorController.DOORACTION.CLOSE);
    }
    @Test
    void StatusJson() {
        DoorStatus doorStatus = DoorController.doorAction(DoorController.doorMap.get("JN1"), DoorController.DOORACTION.OPEN);
        System.out.println(doorStatus.getStatus());


    }
}