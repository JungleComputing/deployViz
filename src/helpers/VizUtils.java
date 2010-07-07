package helpers;

public class VizUtils {
	
	public static final String CLUSTER = "cluster";
	public static final String HEAD_NODE = "head node";
	public static final String COMPUTE_NODE = "compute node";
	
	private static final String[] colors = { "#FF0000", "#FF8000",
        "#80FF00", "#00FF00", "#00FF80", "#00FFFF", "#007FFF", "#0000FF",
        "#8000FF", "#FF0080", "#FF8080", "#FFBF80", "#FFFF80",
        "#BFFF80", "#80FF80", "#80FFBF", "#80FFFF", "#80BFFF", "#8080FF",
        "#BF80FF", "#FF80FF", "#FF80BF", "#800000", "#804000", "#808000",
        "#408000", "#008000", "#008040", "#008080", "#004080", "#000080",
        "#400080", "#800080", "#800040" };
	
	private static int colorIndex = 0;
	
	public static String getNextColor(){
		if(colorIndex == colors.length){
			colorIndex = 0;
		}
		
		return colors[colorIndex++];
	}
	
	public static String getRandomColor(){
		int idx = ((int)(Math.random() * 100)) % colors.length;
		return colors[idx];
	}

}
