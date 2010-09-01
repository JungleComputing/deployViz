package dataGenerator;

import helpers.VizUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InputDataGenerator {

	private static int vuNodes = 12;// 85;
	private static int leidenNodes = 12;// 32;
	private static int delftNodes = 13;// 68;
	private static int uvaNodes = 15;// 41;
	private static int multimedianNodes = 12;// 46;
	
	private static int getRandomWeight(int max){
		return (int)(Math.random()*100) % max;
	}

	public static void main(String[] args) throws IOException {
		generateInputFile();
	}
	
	public static void generateInputFile() throws IOException {
		FileWriter fw = new FileWriter("assets/das3.xml");
		BufferedWriter bw = new BufferedWriter(fw);
		int id = 0, dasid, vuid, leidenid, delftid, uvaid, multimedianid;
		int i, j, weight;

		bw
				.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
						+ "<!--  The DAS 3 supercomputer - GraphML file -->\n"
						+ "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n"
						+ "<graph edgedefault=\"undirected\">\n"
						+ "<!-- data schema -->\n"
						+ "<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"
						+ "<key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"int\"/>\n"
						+ "<key id=\"type\" for=\"node\" attr.name=\"type\" attr.type=\"string\"/>\n");

		dasid = id++;
		vuid = id++;
		leidenid = id++;
		delftid = id++;
		uvaid = id++;
		multimedianid = id++;

		// create a main node as the root of the network tree
		bw.write("<node id=\"" + dasid + "\">\n"
				+ "\t<data key=\"name\">DAS3</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.CLUSTER + "</data>\n"
				+ "</node>\n");

		// create the main nodes for the 5 DAS locations

		// VU
		weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
		bw.write("<node id=\"" + vuid + "\">\n"
				+ "\t<data key=\"name\">VU</data>\n" + "\t<data key=\"type\">"
				+ VizUtils.HEAD_NODE + "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + vuid
				+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");

		// Leiden
		weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
		bw.write("<node id=\"" + leidenid + "\">\n"
				+ "\t<data key=\"name\">Leiden</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE + "</data>\n"
				+ "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + leidenid
				+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");

		// Delft
		weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
		bw.write("<node id=\"" + delftid + "\">\n"
				+ "\t<data key=\"name\">Delft</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE + "</data>\n"
				+ "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + delftid
				+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");

		// UvA
		weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
		bw.write("<node id=\"" + uvaid + "\">\n"
				+ "\t<data key=\"name\">UvA</data>\n" + "\t<data key=\"type\">"
				+ VizUtils.HEAD_NODE + "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + uvaid
				+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");

		// MultimediaN
		weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
		bw.write("<node id=\"" + multimedianid + "\">\n"
				+ "\t<data key=\"name\">MultimediaN</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE + "</data>\n"
				+ "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + multimedianid
				+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");

		// VU nodes + edges to the main node
		for (i = 0; i < vuNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
			
			bw.write("<edge source=\"" + vuid + "\" target=\"" + id
					+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
			id++;
		}

		// Leiden nodes + edges to the main node

		for (i = 0; i < leidenNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
			
			bw.write("<edge source=\"" + leidenid + "\" target=\"" + id
					+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
			id++;
		}

		// Delft nodes + edges to the main node
		for (i = 0; i < delftNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");
			
			weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);

			bw.write("<edge source=\"" + delftid + "\" target=\"" + id
					+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
			id++;
		}

		// UvA nodes + edges to the main node
		for (i = 0; i < uvaNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");
			
			weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);

			bw.write("<edge source=\"" + uvaid + "\" target=\"" + id
					+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
			id++;
		}

		// Multimedian nodes + edges to the main node
		for (i = 0; i < multimedianNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");
			
			weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);

			bw.write("<edge source=\"" + multimedianid + "\" target=\"" + id
					+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
			id++;
		}

		double x;

		for (i = multimedianid + 1; i < id; i++) {
			for (j = i + 1; j < id; j++) {
				x = Math.random();
				if (x > 0.5) {
					
					weight = getRandomWeight(VizUtils.MAX_EDGE_WEIGHT);
					
					bw.write("<edge source=\"" + j + "\" target=\"" + i
							+ "\">" + "\t<data key=\"weight\">"+weight+"</data>\n" + "</edge>\n");
				}
			}
		}

		bw.write("</graph>\n</graphml>");
		bw.close();
		fw.close();
	}
}
