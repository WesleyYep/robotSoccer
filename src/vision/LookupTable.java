package vision;

public class LookupTable {

	public static byte[]YTable = new byte[256];
	public static byte[]UTable = new byte[256];
	public static byte[]VTable = new byte[256];


	final public static byte TEAM_COLOUR = 00000001;
	final public static byte GREEN_COLOUR = 00000010;
	final public static byte BALL_COLOUR =  00000100;

	final public static int TEAM_BIT_POS = 1;
	final public static int GREEN_BIT_POS = 2;
	final public static int BALL_BIT_POS = 3;



	public static void setYTable(int max, int min, byte colour) {
		for (int i = 0; i <256; i++) {
			if (i<= max && i >= min) {
				YTable[i] |= colour;
			} else {
				YTable[i] &= ~(colour);
			}
		}
	}

	public static void setUTable(int max, int min, byte colour) {
		for (int i = 0; i <256; i++) {
			if (i<= max && i >= min) {
				UTable[i] |= colour;
			} else {
				UTable[i] &= ~(colour);
			}
		}

	}

	public static void setVTable(int max, int min, byte colour) {
		for (int i = 0; i <256; i++) {
			if (i<= max && i >= min) {
				VTable[i] |= colour;
			} else {
				VTable[i] &= ~(colour);
			}
		}
	}
}
