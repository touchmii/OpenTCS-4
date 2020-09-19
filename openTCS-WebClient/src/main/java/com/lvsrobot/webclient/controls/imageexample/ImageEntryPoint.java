package com.lvsrobot.webclient.controls.imageexample;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ImageEntryPoint extends AbstractEntryPoint {

	protected Shell createShell(Display display) {
		Shell shell = new Shell(display, SWT.RESIZE);
		return shell;
	}

	@Override
	protected void createContents(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Image image = new Image(parent.getDisplay(), ImageEntryPoint.class.getResourceAsStream("image.png"));
		ImageData scaledTo = image.getImageData().scaledTo(50, 100);
		Image scaledImage = new Image(parent.getDisplay(), scaledTo);
		Button b2 = new Button(parent, SWT.PUSH);
		b2.setImage(scaledImage);
		new Label(parent, SWT.NONE).setText("Label");
		Label label = new Label(parent, SWT.NONE);
		label.setImage(image);
	}

}
