package communication;

import java.util.List;

public interface ReceiverListener {
	
	//public void actions(List<Integer> chunks);

	public void action(List<String> chunks);
	
}
