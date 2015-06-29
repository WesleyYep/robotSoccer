package vision;

import config.ConfigFile;
import config.ConfigPreviousFile;
import controllers.VisionController;
import controllers.WebcamController;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import ui.ColourPanel;
import ui.SamplingPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.NoSuchElementException;

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

		save(fileName);

	}

	public void save(String fileName) {
		/*
		 * Check the output filename and adds the correct extension type. If the output filename already has the extension added, it
		 * doesn't append extension again.
		 */
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

            saveSetting.addProperty("leftGoalTopLeftX", visionController.getLeftGoalTopLeft().getX());
            saveSetting.addProperty("leftGoalTopLeftY", visionController.getLeftGoalTopLeft().getY());
            saveSetting.addProperty("leftGoalTopRightX", visionController.getLeftGoalTopRight().getX());
            saveSetting.addProperty("leftGoalTopRightY", visionController.getLeftGoalTopRight().getY());
            saveSetting.addProperty("leftGoalBottomLeftX", visionController.getLeftGoalBottomLeft().getX());
            saveSetting.addProperty("leftGoalBottomLeftY", visionController.getLeftGoalBottomLeft().getY());
            saveSetting.addProperty("leftGoalBottomRightX", visionController.getLeftGoalBottomRight().getX());
            saveSetting.addProperty("leftGoalBottomRightY", visionController.getLeftGoalBottomRight().getY());

            saveSetting.addProperty("rightGoalTopLeftX", visionController.getRightGoalTopLeft().getX());
            saveSetting.addProperty("rightGoalTopLeftY", visionController.getRightGoalTopLeft().getY());
            saveSetting.addProperty("rightGoalTopRightX", visionController.getRightGoalTopRight().getX());
            saveSetting.addProperty("rightGoalTopRightY", visionController.getRightGoalTopRight().getY());
            saveSetting.addProperty("rightGoalBottomLeftX", visionController.getRightGoalBottomLeft().getX());
            saveSetting.addProperty("rightGoalBottomLeftY", visionController.getRightGoalBottomLeft().getY());
            saveSetting.addProperty("rightGoalBottomRightX", visionController.getRightGoalBottomRight().getX());
            saveSetting.addProperty("rightGoalBottomRightY", visionController.getRightGoalBottomRight().getY());

			SamplingPanel ballSP = colourPanel.ballSamplingPanel;

			saveSetting.addProperty("ballHUpper", ballSP.getUpperBoundForH());
			saveSetting.addProperty("ballHLower", ballSP.getLowerBoundForH());

			saveSetting.addProperty("ballSUpper", ballSP.getUpperBoundForS());
			saveSetting.addProperty("ballSLower", ballSP.getLowerBoundForS());

			saveSetting.addProperty("ballVUpper", ballSP.getUpperBoundForV());
			saveSetting.addProperty("ballVLower", ballSP.getLowerBoundForV());


			SamplingPanel teamSp = colourPanel.teamSamplingPanel;

			saveSetting.addProperty("teamHUpper", teamSp.getUpperBoundForH());
			saveSetting.addProperty("teamHLower", teamSp.getLowerBoundForH());

			saveSetting.addProperty("teamSUpper", teamSp.getUpperBoundForS());
			saveSetting.addProperty("teamSLower", teamSp.getLowerBoundForS());

			saveSetting.addProperty("teamVUpper", teamSp.getUpperBoundForV());
			saveSetting.addProperty("teamVLower", teamSp.getLowerBoundForV());

			SamplingPanel greenSp = colourPanel.greenSamplingPanel;

			saveSetting.addProperty("greenHUpper", greenSp.getUpperBoundForH());
			saveSetting.addProperty("greenHLower", greenSp.getLowerBoundForH());

			saveSetting.addProperty("greenSUpper", greenSp.getUpperBoundForS());
			saveSetting.addProperty("greenSLower", greenSp.getLowerBoundForS());

			saveSetting.addProperty("greenVUpper", greenSp.getUpperBoundForV());
			saveSetting.addProperty("greenVLower", greenSp.getLowerBoundForV());

			SamplingPanel groundSp = colourPanel.groundSamplingPanel;

			saveSetting.addProperty("groundHUpper", groundSp.getUpperBoundForH());
			saveSetting.addProperty("groundHLower", groundSp.getLowerBoundForH());

			saveSetting.addProperty("groundSUpper", groundSp.getUpperBoundForS());
			saveSetting.addProperty("groundSLower", groundSp.getLowerBoundForS());

			saveSetting.addProperty("groundVUpper", groundSp.getUpperBoundForV());
			saveSetting.addProperty("groundVLower", groundSp.getLowerBoundForV());

			SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

			saveSetting.addProperty("opponentHUpper", opponentSp.getUpperBoundForH());
			saveSetting.addProperty("opponentHLower", opponentSp.getLowerBoundForH());

			saveSetting.addProperty("opponentSUpper", opponentSp.getUpperBoundForS());
			saveSetting.addProperty("opponentSLower", opponentSp.getLowerBoundForS());

			saveSetting.addProperty("opponentVUpper", opponentSp.getUpperBoundForV());
			saveSetting.addProperty("opponentVLower", opponentSp.getLowerBoundForV());

			saveSetting.addProperty("robotMinSize", colourPanel.getRobotSizeMinimum());
			saveSetting.addProperty("greenMinSize", colourPanel.getGreenSizeMinimum());
			saveSetting.addProperty("ballMinSize", colourPanel.getBallSizeMinimum());

			saveSetting.addProperty("robotMaxSize", colourPanel.getRobotSizeMaximum());
			saveSetting.addProperty("greenMaxSize", colourPanel.getGreenSizeMaximum());
			saveSetting.addProperty("ballMaxSize", colourPanel.getBallSizeMaximum());

			saveSetting.save();

			//save last read file
			ConfigPreviousFile.getInstance().setPreviousVisionFile(fileName);

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
        open(fileName);
        //save last read file
        ConfigPreviousFile.getInstance().setPreviousVisionFile(fileName);
	}

    public void open(String fileName) {
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

            try {
                visionController.setLeftGoalTopLeft(new Point2D.Double(
                        openSetting.getDouble("leftGoalTopLeftX"), openSetting.getDouble("leftGoalTopLeftY")));
                visionController.setLeftGoalTopRight(new Point2D.Double(
                        openSetting.getDouble("leftGoalTopRightX"), openSetting.getDouble("leftGoalTopRightY")));
                visionController.setLeftGoalBottomLeft(new Point2D.Double(
                        openSetting.getDouble("leftGoalBottomLeftX"), openSetting.getDouble("leftGoalBottomLeftY")));
                visionController.setLeftGoalBottomRight(new Point2D.Double(
                        openSetting.getDouble("leftGoalBottomRightX"), openSetting.getDouble("leftGoalBottomRightY")));

                visionController.setRightGoalTopLeft(new Point2D.Double(
                        openSetting.getDouble("rightGoalTopLeftX"), openSetting.getDouble("rightGoalTopLeftY")));
                visionController.setRightGoalTopRight(new Point2D.Double(
                        openSetting.getDouble("rightGoalTopRightX"), openSetting.getDouble("rightGoalTopRightY")));
                visionController.setRightGoalBottomLeft(new Point2D.Double(
                        openSetting.getDouble("rightGoalBottomLeftX"), openSetting.getDouble("rightGoalBottomLeftY")));
                visionController.setRightGoalBottomRight(new Point2D.Double(
                        openSetting.getDouble("rightGoalBottomRightX"), openSetting.getDouble("rightGoalBottomRightY")));
            } catch (NoSuchElementException ex) {
                System.out.println("outdated vision setting file, please update board area and save it again");
            }

            SamplingPanel ballSP = colourPanel.ballSamplingPanel;

            ballSP.setLowerBoundForH(openSetting.getInt("ballHLower"));
            ballSP.setLowerBoundForS(openSetting.getInt("ballSLower"));
            ballSP.setLowerBoundForV(openSetting.getInt("ballVLower"));

            ballSP.setUpperBoundForH(openSetting.getInt("ballHUpper"));
            ballSP.setUpperBoundForS(openSetting.getInt("ballSUpper"));
            ballSP.setUpperBoundForV(openSetting.getInt("ballVUpper"));

            SamplingPanel teamSp = colourPanel.teamSamplingPanel;

            teamSp.setLowerBoundForH(openSetting.getInt("teamHLower"));
            teamSp.setLowerBoundForS(openSetting.getInt("teamSLower"));
            teamSp.setLowerBoundForV(openSetting.getInt("teamVLower"));

            teamSp.setUpperBoundForH(openSetting.getInt("teamHUpper"));
            teamSp.setUpperBoundForS(openSetting.getInt("teamSUpper"));
            teamSp.setUpperBoundForV(openSetting.getInt("teamVUpper"));

            SamplingPanel greenSp = colourPanel.greenSamplingPanel;

            greenSp.setLowerBoundForH(openSetting.getInt("greenHLower"));
            greenSp.setLowerBoundForS(openSetting.getInt("greenSLower"));
            greenSp.setLowerBoundForV(openSetting.getInt("greenVLower"));

            greenSp.setUpperBoundForH(openSetting.getInt("greenHUpper"));
            greenSp.setUpperBoundForS(openSetting.getInt("greenSUpper"));
            greenSp.setUpperBoundForV(openSetting.getInt("greenVUpper"));

            SamplingPanel groundSp = colourPanel.groundSamplingPanel;

            groundSp.setLowerBoundForH(openSetting.getInt("groundHLower"));
            groundSp.setLowerBoundForS(openSetting.getInt("groundSLower"));
            groundSp.setLowerBoundForV(openSetting.getInt("groundVLower"));

            groundSp.setUpperBoundForH(openSetting.getInt("groundHUpper"));
            groundSp.setUpperBoundForS(openSetting.getInt("groundSUpper"));
            groundSp.setUpperBoundForV(openSetting.getInt("groundVUpper"));

            SamplingPanel opponentSp = colourPanel.opponentSamplingPanel;

            opponentSp.setLowerBoundForH(openSetting.getInt("opponentHLower"));
            opponentSp.setLowerBoundForS(openSetting.getInt("opponentSLower"));
            opponentSp.setLowerBoundForV(openSetting.getInt("opponentVLower"));

            opponentSp.setUpperBoundForH(openSetting.getInt("opponentHUpper"));
            opponentSp.setUpperBoundForS(openSetting.getInt("opponentSUpper"));
            opponentSp.setUpperBoundForV(openSetting.getInt("opponentVUpper"));

            colourPanel.setRobotSizeMinimum(openSetting.getInt("robotMinSize",0));
            colourPanel.setGreenSizeMinimum(openSetting.getInt("greenMinSize",0));
            colourPanel.setBallSizeMinimum(openSetting.getInt("ballMinSize",0));

            colourPanel.setRobotSizeMaximum(openSetting.getInt("robotMaxSize",0));
            colourPanel.setGreenSizeMaximum(openSetting.getInt("greenMaxSize",0));
            colourPanel.setBallSizeMaximum(openSetting.getInt("ballMaxSize",0));

        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
