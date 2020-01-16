package com.jkj.frame;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) throws Exception{
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        BeautyEyeLNFHelper.launchBeautyEyeLNF();
        Font font = new Font("宋体",Font.PLAIN,15);
        UIManager.put("TextArea.font", font);
        new MainFrame();
    }
}
