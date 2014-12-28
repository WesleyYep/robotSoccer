package communication;

import java.util.List;

public interface ReceiverListener {
	
	public void action(List<Integer> chunks);
	
}
