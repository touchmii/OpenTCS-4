package com.lvsrobot.webclient.plantview;


import com.lvsrobot.webclient.controls.ControlsDemo;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.widgets.Composite;


public class BasicEntryPoint extends AbstractEntryPoint {

    @Override
    protected void createContents(Composite parent) {
    	new ControlsDemo(parent);
    }
    
}
