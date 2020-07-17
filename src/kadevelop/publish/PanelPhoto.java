package kadevelop.publish;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PanelPhoto extends JPanel {

	private BufferedImage bufferedImage;
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(bufferedImage, 0,0, this.getWidth(), this.getHeight(), null);
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}


	public void setBufferedImage(BufferedImage bi) {
		this.bufferedImage = bi;
		
	}
	
	
}
