package dataGenerator;

import helpers.VizUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InputDataGenerator {

	private static int vuNodes = 12;//85;
	private static int leidenNodes = 12;//32;
	private static int delftNodes = 13;//68;
	private static int uvaNodes = 15;//41;
	private static int multimedianNodes = 12;//46;

	public static void main(String[] args) throws IOException {
		FileWriter fw = new FileWriter("assets/das3.xml");
		BufferedWriter bw = new BufferedWriter(fw);
		int id = 0, dasid, vuid, leidenid, delftid, uvaid, multimedianid;
		int i, j;

		bw
				.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
						+ "<!--  The DAS 3 supercomputer -->\n"
						+ "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n"
						+ "<graph edgedefault=\"undirected\">\n"
						+ "<!-- data schema -->\n"
						+ "<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"
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
		bw.write("<node id=\"" + vuid + "\">\n"
				+ "\t<data key=\"name\">VU</data>\n" + "\t<data key=\"type\">"
				+ VizUtils.HEAD_NODE + "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + vuid
				+ "\"></edge>\n");

		// Leiden
		bw.write("<node id=\"" + leidenid + "\">\n"
				+ "\t<data key=\"name\">Leiden</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE
				+ "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + leidenid
				+ "\"></edge>\n");

		// Delft
		bw.write("<node id=\"" + delftid + "\">\n"
				+ "\t<data key=\"name\">Delft</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE
				+ "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + delftid
				+ "\"></edge>\n");

		// UvA
		bw.write("<node id=\"" + uvaid + "\">\n"
				+ "\t<data key=\"name\">UvA</data>\n" + "\t<data key=\"type\">"
				+ VizUtils.HEAD_NODE + "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + uvaid
				+ "\"></edge>\n");

		// MultimediaN
		bw.write("<node id=\"" + multimedianid + "\">\n"
				+ "\t<data key=\"name\">MultimediaN</data>\n"
				+ "\t<data key=\"type\">" + VizUtils.HEAD_NODE
				+ "</data>\n" + "</node>\n");
		bw.write("<edge source=\"" + dasid + "\" target=\"" + multimedianid
				+ "\"></edge>\n");

		// VU nodes + edges to the main node
		for (i = 0; i < vuNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			bw.write("<edge source=\"" + vuid + "\" target=\"" + id
					+ "\"></edge>\n");
			id++;
		}

		// Leiden nodes + edges to the main node

		for (i = 0; i < leidenNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			bw.write("<edge source=\"" + leidenid + "\" target=\"" + id
					+ "\"></edge>\n");
			id++;
		}

		// Delft nodes + edges to the main node
		for (i = 0; i < delftNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			bw.write("<edge source=\"" + delftid + "\" target=\"" + id
					+ "\"></edge>\n");
			id++;
		}

		// UvA nodes + edges to the main node
		for (i = 0; i < uvaNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			bw.write("<edge source=\"" + uvaid + "\" target=\"" + id
					+ "\"></edge>\n");
			id++;
		}

		// Multimedian nodes + edges to the main node
		for (i = 0; i < multimedianNodes; i++) {
			bw.write("<node id=\"" + id + "\">\n" + "\t<data key=\"name\">n"
					+ id + "</data>\n" + "\t<data key=\"type\">"
					+ VizUtils.COMPUTE_NODE + "</data>\n" + "</node>\n");

			bw.write("<edge source=\"" + multimedianid + "\" target=\"" + id
					+ "\"></edge>\n");
			id++;
		}

		double x;

		for (i = multimedianid+1; i < id; i++) {
			for (j = i + 1; j < id; j++) {
				x = Math.random();
				if (x > 0.5) {
					bw.write("<edge source=\"" + j + "\" target=\"" + i
							+ "\"></edge>\n");
				}
			}
		}

		bw.write("</graph>\n</graphml>");
		bw.close();
		fw.close();
	}
}
