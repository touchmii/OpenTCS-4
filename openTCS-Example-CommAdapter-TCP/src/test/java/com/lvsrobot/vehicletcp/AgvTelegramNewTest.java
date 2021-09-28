package com.lvsrobot.vehicletcp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgvTelegramNewTest {
    private static AgvTelegramNew agv;

    @BeforeAll
    public static void  initClient() {
        agv = new AgvTelegramNew("192.168.10.235", 10001);
        agv.getAgvInfo();
    }

    @AfterAll
    public static void unintClient() {
        agv.disConnect();
    }

    @Test
    void getAgvInfo() {
    }

    @Test
    void charge() {
    }

    @Test
    void discharge() {
    }

    @Test
    void liftAction() {
        agv.liftAction("208", AgvTelegramNew.LIFTACTION.UP);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}
        agv.liftAction("208", AgvTelegramNew.LIFTACTION.DOWN);
    }
}