/*
 * Created by JFormDesigner on Tue Aug 17 16:40:15 CST 2021
 */

package com.lvsrobot.guing.plugins.panels.video;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.opentcs.components.plantoverview.*;
import org.opentcs.util.event.EventHandler;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
//import uk.co.caprica.vlcj.runtime.x.LibXUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

/**
 * @author 蒋滨鸿
 */
public class VideoViewPanel extends PluggablePanel implements EventHandler {
    private MediaPlayer mediaPlayer;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent;
    public VideoViewPanel() {
        initComponents();
    }

    @Override
    public void initialize() {
//        File vlcInstallPath = new File("C:\\Users\\cisco\\Downloads\\vlc-3.0.8");
//        NativeLibrary.addSearchPath(
//                RuntimeUtil.getLibVlcLibraryName(), vlcInstallPath.getAbsolutePath());
//        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
//        LibXUtil.initialise();
        mediaPlayerFactory = new MediaPlayerFactory();
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        embeddedMediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.vlcview.add(embeddedMediaPlayerComponent, BorderLayout.CENTER);
        embeddedMediaPlayerComponent.mediaPlayer().media().play("rtsp://cisco:Cisco@192.168.0.99:554/StreamingSetting?version=1.0&action=getRTSPStream&ChannelID=1&ChannelName=Channel1");
        embeddedMediaPlayerComponent.mediaPlayer().controls().play();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void terminate() {

    }

    @Override
    public void onEvent(Object event) {

    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - 蒋滨鸿
        panel3 = new JPanel();
        comboBox1 = new JComboBox();
        vlcview = new JPanel();
        panel4 = new JPanel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        button1 = new JButton();
        button2 = new JButton();

        //======== this ========
        setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax.
        swing. border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border
        . TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog"
        ,java .awt .Font .BOLD ,12 ), java. awt. Color. red) , getBorder
        ( )) );  addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java
        .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException
        ( ); }} );

        //======== panel3 ========
        {

            //======== vlcview ========
            {
                vlcview.setBorder(new TitledBorder("video"));
                vlcview.setLayout(new BorderLayout());
            }

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(vlcview, GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(381, Short.MAX_VALUE))
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                        .addComponent(vlcview, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
            );
        }

        //======== panel4 ========
        {

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(list1);
            }

            //---- button1 ----
            button1.setText("text");

            //---- button2 ----
            button2.setText("text");

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                panel4Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel4Layout.createParallelGroup()
                            .addComponent(button1)
                            .addComponent(button2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
            panel4Layout.setVerticalGroup(
                panel4Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(panel4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(button1)
                        .addGap(18, 18, 18)
                        .addComponent(button2)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(panel4, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
                    .addGap(22, 22, 22))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - 蒋滨鸿
    private JPanel panel3;
    private JComboBox comboBox1;
    private JPanel vlcview;
    private JPanel panel4;
    private JScrollPane scrollPane1;
    private JList list1;
    private JButton button1;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
