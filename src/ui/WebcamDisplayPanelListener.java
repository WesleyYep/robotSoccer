package ui;

import java.awt.image.BufferedImage;

import ui.WebcamDisplayPanel.ViewState;

public interface WebcamDisplayPanelListener {
	public void viewStateChanged(ViewState currentViewState);
	public void imageUpdated(BufferedImage image);
}
