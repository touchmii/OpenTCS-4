/*
 * Created by JFormDesigner on Fri Jun 26 11:20:47 CST 2020
 */

package com.lvsrobot.vehicle.exchange;

import javax.swing.border.*;
import com.google.inject.assistedinject.Assisted;
//import org.opentcs.commadapter.vehicle.ExampleProcessModel;
import com.lvsrobot.vehicle.ExampleProcessModel;
//import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.OrderRequest;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import com.lvsrobot.vehicle.ExampleProcessModelTO;
import org.opentcs.util.CallWrapper;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.GroupLayout;

import static java.util.Objects.requireNonNull;

/**
 * @author Benoni Jiang
 */
public class myPanel2 extends VehicleCommAdapterPanel {
//    private final DefaultListModel<OrderRequest> lastOrderListModel = new DefaultListModel<>();
    private final VehicleService vehicleService;
    private final CallWrapper callWrapper;
    private ExampleProcessModelTO processModel;

    @Inject
    public myPanel2(@Assisted ExampleProcessModelTO processModel,
                    @Assisted VehicleService vehicleService,
                    @ServiceCallWrapper CallWrapper callWrapper) {
        this.processModel = requireNonNull(processModel, "processModel");
        this.vehicleService = requireNonNull(vehicleService, "vehicleService");
        this.callWrapper = requireNonNull(callWrapper, "callWrapper");
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        button8 = new JButton();
        button9 = new JButton();
        button1 = new JButton();
        button3 = new JButton();
        button7 = new JButton();
        button4 = new JButton();
        button6 = new JButton();
        button2 = new JButton();
        button5 = new JButton();
        slider1 = new JSlider();
        label2 = new JLabel();
        label3 = new JLabel();
        button10 = new JButton();
        button11 = new JButton();
        panel2 = new JPanel();
        label1 = new JLabel();
        panel3 = new JPanel();

        //======== this ========
        setBorder(null);

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("control"));

            //---- button8 ----
            button8.setText("UP");

            //---- button9 ----
            button9.setText("DOWN");

            //---- button1 ----
            button1.setText("\u2b07\ufe0f");

            //---- button3 ----
            button3.setText("\u2b05\ufe0f");

            //---- button7 ----
            button7.setText("STOP");

            //---- button4 ----
            button4.setText("\u27a1\ufe0f");

            //---- button6 ----
            button6.setText("\u21a9\ufe0f");

            //---- button2 ----
            button2.setText("\u2b06\ufe0f");

            //---- button5 ----
            button5.setText("\u21aa\ufe0f");

            //---- label2 ----
            label2.setText("Speed :");

            //---- label3 ----
            label3.setText("Manual Control");

            //---- button10 ----
            button10.setText("OUT");

            //---- button11 ----
            button11.setText("IN");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap(30, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(label2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(slider1, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
                                        .addGap(14, 14, 14))
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(button5, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(button2, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button6, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                        .addGap(25, 25, 25)))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(button8)
                                            .addGap(24, 24, 24)
                                            .addComponent(button9))
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(button3, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel1Layout.createParallelGroup()
                                                .addGroup(panel1Layout.createSequentialGroup()
                                                    .addComponent(button7, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(button4, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(button10)
                                            .addGap(24, 24, 24)
                                            .addComponent(button11)))
                                    .addContainerGap()))
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addComponent(label3)
                                .addGap(59, 59, 59))))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label3)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(slider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button6)
                            .addComponent(button5)
                            .addComponent(button2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button3)
                            .addComponent(button7)
                            .addComponent(button4))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(button1)
                        .addGap(12, 12, 12)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button9)
                            .addComponent(button8))
                        .addGap(12, 12, 12)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button11)
                            .addComponent(button10))
                        .addContainerGap(61, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(LineBorder.createBlackLineBorder());

            //---- label1 ----
            label1.setText("Camera");

            //======== panel3 ========
            {

                GroupLayout panel3Layout = new GroupLayout(panel3);
                panel3.setLayout(panel3Layout);
                panel3Layout.setHorizontalGroup(
                    panel3Layout.createParallelGroup()
                        .addGap(0, 341, Short.MAX_VALUE)
                );
                panel3Layout.setVerticalGroup(
                    panel3Layout.createParallelGroup()
                        .addGap(0, 139, Short.MAX_VALUE)
                );
            }

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(287, Short.MAX_VALUE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(28, Short.MAX_VALUE))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(18, 18, 18))
                .addGroup(layout.createSequentialGroup()
                    .addGap(58, 58, 58)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(96, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        getAccessibleContext().setAccessibleName("myPanel2");
    }

    @Override
    public void processModelChange(String attributeChange, VehicleProcessModelTO newProcessModel) {

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton button8;
    private JButton button9;
    private JButton button1;
    private JButton button3;
    private JButton button7;
    private JButton button4;
    private JButton button6;
    private JButton button2;
    private JButton button5;
    private JSlider slider1;
    private JLabel label2;
    private JLabel label3;
    private JButton button10;
    private JButton button11;
    private JPanel panel2;
    private JLabel label1;
    private JPanel panel3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
