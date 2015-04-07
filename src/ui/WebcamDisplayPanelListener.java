package ui;

import ui.WebcamDisplayPanel.ViewState;

import java.awt.image.BufferedImage;

public interface WebcamDisplayPanelListener {
	public void viewStateChanged(ViewState currentViewState);
	public void imageUpdated(BufferedImage image);
}
