package com.samuel.mazetowers.etc;

public class ColouredLightHelper {

	// The following code is a public API taken from here:
	// https://github.com/CptSpaceToaster/CptsModdingLight/blob/1.7.2/src/main/java/coloredlightscore/src/api/CLApi.java

	public static int r[] = new int[] { 0, 15, 0, 8, 0, 10,
		0, 10, 5, 15, 8, 15, 0, 15, 15, 15 };
	public static int g[] = new int[] { 0, 0, 15, 3, 0, 0,
		15, 10, 5, 10, 15, 15, 8, 0, 12, 15 };
	public static int b[] = new int[] { 0, 0, 0, 0, 15, 15,
		15, 10, 5, 13, 0, 0, 15, 15, 10, 15 };

	public static int makeRGBLightValue(float r, float g,
		float b, float currentLightValue) {
		// Clamp color channels
		if (r < 0.0f)
			r = 0.0f;
		else if (r > 1.0f)
			r = 1.0f;

		if (g < 0.0f)
			g = 0.0f;
		else if (g > 1.0f)
			g = 1.0f;

		if (b < 0.0f)
			b = 0.0f;
		else if (b > 1.0f)
			b = 1.0f;

		int brightness = (int) (currentLightValue * 15.0f);
		brightness &= 0xf;

		return brightness | ((int) (15.0F * b) << 15)
			+ ((int) (15.0F * g) << 10)
			+ ((int) (15.0F * r) << 5);
	}

	// End API Copypasta

	private static int packedColors[][] = null;

	public static int getPackedColor(int meta, int light) {
		if (packedColors == null)
			initPackedColors();

		return packedColors[meta][light];
	}

	private static void initPackedColors() {
		packedColors = new int[16][16];
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++)
				packedColors[i][j] = makeRGBLightValue(
					r[15 - i], g[15 - i], b[15 - i],
					j / 16F);
	}
}
