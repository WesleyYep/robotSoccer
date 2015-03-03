package vision;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	/**
	 * <p>Saves the vision setting into a xml file</p>
	 */
	
	public void saveVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last save directory
		if ((path = ConfigFile.getInstance().getLastSaveDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		
		// Removes the accept all filter.
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// Adds the save filter.
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text/xml", "xml"));
		
		fileChooser.showSaveDialog(null);

		if (fileChooser.getSelectedFile() == null) {
			return;
		}

		String extensionType = fileChooser.getFileFilter().getDescription();
		String fileName = fileChooser.getSelectedFile().getAbsolutePath();
		
		/*
		 * Check the output filename and adds the correct extension type. If the output filename already has the extension added, it
		 * doesn't append extension again.
		 */
		if (extensionType.contains("text/xml") && !fileName.contains("xml")) {
			fileName = fileName + "." + "xml";
		}
		
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

            saveSetting.addProperty("robotMinSize", colourPanel.getRobotSizeMinimum());
            saveSetting.addProperty("greenMinSize", colourPanel.getGreenSizeMinimum());
            saveSetting.addProperty("ballMinSize", colourPanel.getBallSizeMinimum());

			saveSetting.save();


		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * <p>Opens the vision setting xml file and loads it into the program</p>
	 */
	
	public void openVisionSetting() {
		JFileChooser fileChooser;
		String path;
		//read in the last open directory
		if ((path = ConfigFile.getInstance().getLastOpenDirectory()) == null) {
			fileChooser = new JFileChooser();
		} else {
			fileChooser = new JFileChooser(path);
		}
		
		// Removes the accept all filter.
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// Adds the open filter.
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text/xml", "xml"));
		
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

			ballSP.setLowerBoundForY(openSetting.getInt("ballYLower"));
			ballSP.setLowerBoundForU(openSetting.getInt("ballULower"));
			ballSP.setLowerBoundForV(openSetting.getInt("ballVLower"));
			
			ballSP.setUpperBoundForY(openSetting.getInt("ballYUpper"));
			ballSP.setUpperBoundForU(openSetting.getInt("ballUUpper"));
			ballSP.setUpperBoundForV(openSetting.getInt("ballVUpper"));

			SamplingPanel teamSp = colourPanel.teamSamplingPanel;

			teamSp.setLowerBoundForY(openSetting.getInt("teamYLower"));
			teamSp.setLowerBoundForU(openSetting.getInt("teamULower"));
			teamSp.setLowerBoundForV(openSetting.getInt("teamVLower"));
			
			teamSp.setUpperBoundForY(openSetting.getInt("teamYUpper"));
			teamSp.setUpperBoundForU(openSetting.getInt("teamUUpper"));
			teamSp.setUpperBoundForV(openSetting.getInt("teamVUpper"));

			SamplingPanel greenSp = colourPanel.greenSamplingPanel;

			greenSp.setLowerBoundForY(openSetting.getInt("greenYLower"));
			greenSp.setLowerBoundForU(openSetting.getInt("greenULower"));
			greenSp.setLowerBoundForV(openSetting.getInt("greenVLower"));
			
			greenSp.setUpperBoundForY(openSetting.getInt("greenYUpper"));
			greenSp.setUpperBoundForU(openSetting.getInt("greenUUpper"));
			greenSp.setUpperBoundForV(openSetting.getInt("greenVUpper"));

			SamplingPanel groundSp = colourPanel.groundSamplingPanel;

			groundSp.setLowerBoundForY(openSetting.getInt("groundYLower"));
			groundSp.setLowerBoundForU(openSetting.getInt("groundULower"));
			groundSp.setLowerBoundForV(openSetting.getInt("groundVLower"));
			
			groundSp.setUpperBoundForY(openSetting.getInt("groundYUpper"));
			groundSp.setUpperBoundForU(openSetting.getInt("groundUUpper"));
			groundSp.setUpperBoundForV(openSetting.getInt("groundVUpper"));

			SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

			opponentSp.setLowerBoundForY(openSetting.getInt("opponentYLower"));
			opponentSp.setLowerBoundForU(openSetting.getInt("opponentULower"));
			opponentSp.setLowerBoundForV(openSetting.getInt("opponentVLower"));
			
			opponentSp.setUpperBoundForY(openSetting.getInt("opponentYUpper"));
			opponentSp.setUpperBoundForU(openSetting.getInt("opponentUUpper"));
			opponentSp.setUpperBoundForV(openSetting.getInt("opponentVUpper"));

            colourPanel.setRobotSizeMinimum(openSetting.getInt("robotMinSize"));
            colourPanel.setGreenSizeMinimum(openSetting.getInt("greenMinSize"));
            colourPanel.setBallSizeMinimum(openSetting.getInt("ballMinSize"));

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
