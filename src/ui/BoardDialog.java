package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controllers.VisionController;

public class BoardDialog extends JDialog {

	
//	private JPanel glassPanel;
	private BufferedImage boardImage;
	
	private JLabel picLabel;
	private JPanel panel;
	
	private BoardAreaGlassPanel glassPanel;
	
	public BoardDialog(JFrame jFrame, VisionController vc) {
		super(jFrame,false);

		picLabel = new JLabel();
		glassPanel = new BoardAreaGlassPanel(vc);
		this.setResizable(false);

		
		
		panel = new JPanel();
		//this.getContentPane().setLayout(new BorderLayout());
		//this.getContentPane().add(picLabel, BorderLayout.CENTER);
		
		panel.setLayout(null);
		
		panel.add(picLabel);
		panel.add(glassPanel);
		glassPanel.setVisible(true);
		
		panel.setComponentZOrder(glassPanel,0);
		
		this.add(panel);
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	
	public void setBoardImage(BufferedImage image) {
		
		boardImage = image;
		picLabel.setIcon(new ImageIcon(image));
		
		picLabel.setSize(image.getWidth(),image.getHeight());
		picLabel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
		panel.setSize(image.getWidth(),image.getHeight());
		panel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
		
		glassPanel.setSize(image.getWidth(),image.getHeight());
		glassPanel.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
		glassPanel.setOpaque(false);
		
		glassPanel.setBounds(0,0,image.getWidth(),image.getHeight());
		glassPanel.repaint();
		this.setSize(image.getWidth()+50,image.getHeight()+50);
		this.pack();
		this.validate();
		this.repaint();
	}
	
	
	
	
	
	
}
