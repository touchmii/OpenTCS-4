package org.opentcs.guing.application.action.draw;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.SharedKernelServicePortal;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.access.rmi.ClientID;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.guing.components.drawing.figures.LabeledPointFigure;
import org.opentcs.guing.util.ImageDirectory;
import org.opentcs.guing.util.ResourceBundleUtil;

import static java.util.Objects.requireNonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.util.Objects.requireNonNull;
import static org.opentcs.guing.util.I18nPlantOverview.MODELVIEW_PATH;

public class ChangePointAction extends AbstractSelectedAction {

    public final static String ID = "x";

    private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MODELVIEW_PATH);

    private final SharedKernelServicePortalProvider portalProvider;

//    @Inject
    public ChangePointAction(DrawingEditor editor, SharedKernelServicePortalProvider portalProvider) {
       super(editor);
        requireNonNull(portalProvider, "portalProvider");
        this.portalProvider = portalProvider;
        putValue(NAME, BUNDLE.getString("changePointAction.name"));
        putValue(SMALL_ICON, ImageDirectory.getImageIcon("/menu/edit-copy-3.png"));
        updateEnabledState();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        changePoint();
    }

    public void changePoint() {
        for (Figure selected : getView().getSelectedFigures()) {
            if (selected.getClass().getName().equals("org.opentcs.guing.components.drawing.figures.LabeledPointFigure")) {
//                StandardContentDialog fDialog = new StandardContentDialog();
                String pointName = ((LabeledPointFigure)selected).getLabel().getText();
                Point point;
                try {
                    point = getOrderService().fetchObject(Point.class, pointName);
                    JTextField x = new JTextField();
                    x.setText(String.valueOf(point.getPosition().getX()));
                    JTextField y = new JTextField();
                    y.setText(String.valueOf(point.getPosition().getY()));
                    Object[] message = {
                            "X:", x,
                            "Y:", y
                    };

                    int option = JOptionPane.showConfirmDialog(null, message, "new position: "+pointName, JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if (x.getText().matches("-?\\d+") && y.getText().matches("-?\\d+")) {
                            ClientID clientID = new ClientID("x");


                            point.setPosition(new Triple( Integer.parseInt(x.getText()), Integer.parseInt(y.getText()), 0));
                            getOrderService().updateObjectValue(point);

                            System.out.println("change successful");
                        } else {
                            System.out.println("input int");
                        }
                    } else {
                        System.out.println("change canceled");
                    }
                } catch (KernelRuntimeException e) {
                    e.getMessage();
                }
            }

        }
    }

     TransportOrderService getOrderService() {

        try (SharedKernelServicePortal sharedPortal = portalProvider.register()) {
            TransportOrderService transportOrderService = sharedPortal.getPortal().getTransportOrderService();
//            TCSObjectService = transportOrderService.
            return transportOrderService;
        } catch (KernelRuntimeException e) {
            e.getMessage();
        }
        return null;
    }

    private static void createUI(final JFrame frame){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        JButton button = new JButton("OK!");
        final JLabel label = new JLabel();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = (String)JOptionPane.showInputDialog(
                        frame,
                        "Select one of the color",
                        "Swing Tester",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "Red"
                );
                if(result != null && result.length() > 0){
                    label.setText("You selected:" + result);
                }else {
                    label.setText("None selected");
                }
            }
        });

        panel.add(button);
        panel.add(label);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

}
