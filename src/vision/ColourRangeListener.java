package vision;

import ui.SamplingPanel;

public interface ColourRangeListener {

	public void yRangeChanged(int max, int min, SamplingPanel panel);
	public void uRangeChanged(int max, int min, SamplingPanel panel);
	public void vRangeChanged(int max, int min, SamplingPanel panel);
}
