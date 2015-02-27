package vision;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import strategy.Play;
import strategy.Role;
import ui.ColourPanel;
import ui.SamplingPanel;
import config.ConfigFile;
import controllers.VisionController;
import controllers.WebcamController;
import data.Situation;

public class VisionSettingFile {

	private WebcamController webcamController;
	private ColourPanel colourPanel;
	private VisionController visionController;

	public VisionSettingFile (WebcamController wc, ColourPanel colourPanel, VisionController vc) {
		webcamController = wc;
		this.colourPanel = colourPanel;
		visionController = vc;

	}


	public void saveVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last save directory
		if ((path = ConfigFile.getInstance().getLastSaveDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		fileChooser.showSaveDialog(null);

		if (fileChooser.getSelectedFile() == null) {
			return;
		}

		String fileName = fileChooser.getSelectedFile().getAbsolutePath();
		String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
		ConfigFile.getInstance().setLastSaveDirectory(folderPath);

		try {
			XMLConfiguration saveSetting = new XMLConfiguration();
			saveSetting.setFile(new File(fileName));
			saveSetting.save();

			saveSetting.addProperty("topRightX", visionController.getTopRight().getX());
			saveSetting.addProperty("topRightY", visionController.getTopRight().getY());
			saveSetting.addProperty("topLeftX", visionController.getTopLeft().getX());
			saveSetting.addProperty("topLeftY", visionController.getTopLeft().getY());
			saveSetting.addProperty("bottomRightX", visionController.getBottomRight().getX());
			saveSetting.addProperty("bottomRightY", visionController.getBottomRight().getY());
			saveSetting.addProperty("bottomLeftX", visionController.getBottomLeft().getX());
			saveSetting.addProperty("bottomLeftY", visionController.getBottomLeft().getY());

			SamplingPanel ballSP = colourPanel.ballSamplingPanel;

			saveSetting.addProperty("ballYUpper", ballSP.getUpperBoundForY());
			saveSetting.addProperty("ballYLower", ballSP.getLowerBoundForY());

			saveSetting.addProperty("ballUUpper", ballSP.getUpperBoundForU());
			saveSetting.addProperty("ballULower", ballSP.getLowerBoundForU());

			saveSetting.addProperty("ballVUpper", ballSP.getUpperBoundForV());
			saveSetting.addProperty("ballVLower", ballSP.getLowerBoundForV());


			SamplingPanel teamSp = colourPanel.teamSamplingPanel;

			saveSetting.addProperty("teamYUpper", teamSp.getUpperBoundForY());
			saveSetting.addProperty("teamYLower", teamSp.getLowerBoundForY());

			saveSetting.addProperty("teamUUpper", teamSp.getUpperBoundForU());
			saveSetting.addProperty("teamULower", teamSp.getLowerBoundForU());

			saveSetting.addProperty("teamVUpper", teamSp.getUpperBoundForV());
			saveSetting.addProperty("teamVLower", teamSp.getLowerBoundForV());

			SamplingPanel greenSp = colourPanel.greenSamplingPanel;

			saveSetting.addProperty("greenYUpper", greenSp.getUpperBoundForY());
			saveSetting.addProperty("greenYLower", greenSp.getLowerBoundForY());

			saveSetting.addProperty("greenUUpper", greenSp.getUpperBoundForU());
			saveSetting.addProperty("greenULower", greenSp.getLowerBoundForU());

			saveSetting.addProperty("greenVUpper", greenSp.getUpperBoundForV());
			saveSetting.addProperty("greenVLower", greenSp.getLowerBoundForV());

			SamplingPanel groundSp = colourPanel.groundSamplingPanel;

			saveSetting.addProperty("groundYUpper", groundSp.getUpperBoundForY());
			saveSetting.addProperty("groundYLower", groundSp.getLowerBoundForY());

			saveSetting.addProperty("groundUUpper", groundSp.getUpperBoundForU());
			saveSetting.addProperty("groundULower", groundSp.getLowerBoundForU());

			saveSetting.addProperty("groundVUpper", groundSp.getUpperBoundForV());
			saveSetting.addProperty("groundVLower", groundSp.getLowerBoundForV());

			SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

			saveSetting.addProperty("opponentYUpper", opponentSp.getUpperBoundForY());
			saveSetting.addProperty("opponentYLower", opponentSp.getLowerBoundForY());

			saveSetting.addProperty("opponentUUpper", opponentSp.getUpperBoundForU());
			saveSetting.addProperty("opponentULower", opponentSp.getLowerBoundForU());

			saveSetting.addProperty("opponentVUpper", opponentSp.getUpperBoundForV());
			saveSetting.addProperty("opponentVLower", opponentSp.getLowerBoundForV());
			saveSetting.save();


		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void openVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last open directory
		if ((path = ConfigFile.getInstance().getLastOpenDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		fileChooser.showOpenDialog(null);

		if (fileChooser.getSelectedFile() == null) {
			return;
		}

		String fileName = fileChooser.getSelectedFile().getAbsolutePath();

		//creating the folder name and write into configuration
		String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
		ConfigFile.getInstance().setLastOpenDirectory(folderPath);

		try {
			XMLConfiguration openSetting = new XMLConfiguration(fileName);

			visionController.setTopLeft(new Point2D.Double(
					openSetting.getDouble("topLeftX"), openSetting.getDouble("topLeftY")));
			visionController.setTopRight(new Point2D.Double(
					openSetting.getDouble("topRightX"), openSetting.getDouble("topRightY")));
			visionController.setBottomLeft(new Point2D.Double(
					openSetting.getDouble("bottomLeftX"), openSetting.getDouble("bottomLeftY")));
			visionController.setBottomRight(new Point2D.Double(
					openSetting.getDouble("bottomRightX"), openSetting.getDouble("bottomRightY")));

			SamplingPanel ballSP = colourPanel.ballSamplingPanel;

			ballSP.setUpperBoundForY(openSetting.getInt("ballYUpper"));
			ballSP.setUpperBoundForU(openSetting.getInt("ballUUpper"));
			ballSP.setUpperBoundForV(openSetting.getInt("ballVUpper"));

			ballSP.setLowerBoundForY(openSetting.getInt("ballYLower"));
			ballSP.setLowerBoundForU(openSetting.getInt("ballULower"));
			ballSP.setLowerBoundForV(openSetting.getInt("ballVLower"));

			SamplingPanel teamSp = colourPanel.teamSamplingPanel;

			teamSp.setUpperBoundForY(openSetting.getInt("teamYUpper"));
			teamSp.setUpperBoundForU(openSetting.getInt("teamUUpper"));
			teamSp.setUpperBoundForV(openSetting.getInt("teamVUpper"));

			teamSp.setLowerBoundForY(openSetting.getInt("teamYLower"));
			teamSp.setLowerBoundForU(openSetting.getInt("teamULower"));
			teamSp.setLowerBoundForV(openSetting.getInt("teamVLower"));

			SamplingPanel greenSp = colourPanel.greenSamplingPanel;

			greenSp.setUpperBoundForY(openSetting.getInt("greenYUpper"));
			greenSp.setUpperBoundForU(openSetting.getInt("greenUUpper"));
			greenSp.setUpperBoundForV(openSetting.getInt("greenVUpper"));

			greenSp.setLowerBoundForY(openSetting.getInt("greenYLower"));
			greenSp.setLowerBoundForU(openSetting.getInt("greenULower"));
			greenSp.setLowerBoundForV(openSetting.getInt("greenVLower"));

			SamplingPanel groundSp = colourPanel.groundSamplingPanel;

			groundSp.setUpperBoundForY(openSetting.getInt("groundYUpper"));
			groundSp.setUpperBoundForU(openSetting.getInt("groundUUpper"));
			groundSp.setUpperBoundForV(openSetting.getInt("groundVUpper"));

			groundSp.setLowerBoundForY(openSetting.getInt("groundYLower"));
			groundSp.setLowerBoundForU(openSetting.getInt("groundULower"));
			groundSp.setLowerBoundForV(openSetting.getInt("groundVLower"));

			SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

			opponentSp.setUpperBoundForY(openSetting.getInt("opponentYUpper"));
			opponentSp.setUpperBoundForU(openSetting.getInt("opponentUUpper"));
			opponentSp.setUpperBoundForV(openSetting.getInt("opponentVUpper"));

			opponentSp.setLowerBoundForY(openSetting.getInt("opponentYLower"));
			opponentSp.setLowerBoundForU(openSetting.getInt("opponentULower"));
			opponentSp.setLowerBoundForV(openSetting.getInt("opponentVLower"));

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
