package vision;

public class LookupTable {

	
	public static byte[][][] LUT = new byte[256][256][256];
	
	final public static byte TEAM_COLOUR = 00000001;
	final public static byte GREEN_COLOUR = 00000010;
	final public static byte BALL_COLOUR =  00000100;
	final public static byte GROUND_COLOUR = (byte) 00001000;

	final public static int TEAM_BIT_POS = 1;
	final public static int GREEN_BIT_POS = 2;
	final public static int BALL_BIT_POS = 3;
	final public static int GROUND_BIT_POS = 4;

	public static boolean[] bValid = new boolean[8];
	
	public static byte getLUTData(int h, int s, int v) {
		return LUT[h][s][v];
	}
	
	public static void setData(int p, int h, int s, int v, boolean state) {	
		
		byte mask = (byte) (1<<p);
		LUT[h][s][v] |= mask;
		
	}
	
	
}
