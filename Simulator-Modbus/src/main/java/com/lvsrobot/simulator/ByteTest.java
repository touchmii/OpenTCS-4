package com.lvsrobot.simulator;

public class ByteTest {

    public static void main(String[] args) {
        byte a=2;
//        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);

        System.out.println(a);
        System.out.println(0xFF);
        System.out.println((a&0xFF));
        System.out.println((a&0xFF) << 8);
        System.out.println((a&0xFF) << 16);
        System.out.println((a&0xFF) << 24);
    }
}
