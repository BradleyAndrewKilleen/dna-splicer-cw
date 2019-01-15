package dnasplicing;

import static junit.framework.Assert.*;
import static org.apache.commons.lang3.StringUtils.*;
import static sbcc.Core.*;

import java.util.*;

//import javafx.scene.transform.*;

public class LinkedDnaStrand implements DnaStrand {

	private DnaSequenceNode firstNode;
	private DnaSequenceNode lastNode;
	private int nodeCount = 0;
	private int appendCount = 0;


	public LinkedDnaStrand(String dna) {
		DnaSequenceNode myDnaStrand = new DnaSequenceNode(dna);
		firstNode = myDnaStrand;
		lastNode = myDnaStrand;
		nodeCount++;
	}


	public LinkedDnaStrand() {
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		DnaSequenceNode cursor = firstNode;

		for (int ndx = 0; ndx < nodeCount; ndx++) {
			builder.append(cursor.dnaSequence);
			cursor = cursor.next;
		}
		return builder.toString();
	}


	@Override
	public long getNucleotideCount() {
		int nucleotideCount = 0;
		DnaSequenceNode cursor = firstNode;

		for (int ndx = 0; ndx < nodeCount; ndx++) {
			int nodeLength = cursor.dnaSequence.length();
			cursor = cursor.next;
			nucleotideCount += nodeLength;
		}
		return nucleotideCount;
	}


	@Override
	public void append(String dnaSequence) {
		if (nodeCount >= 1) {
			DnaSequenceNode appendedDnaStrand = new DnaSequenceNode(dnaSequence);
			lastNode.next = appendedDnaStrand;
			appendedDnaStrand.previous = lastNode;
			lastNode = appendedDnaStrand;
			nodeCount++;
			appendCount++;
		} else {
			DnaSequenceNode myDnaStrand = new DnaSequenceNode(dnaSequence);
			firstNode = myDnaStrand;
			lastNode = myDnaStrand;
			nodeCount++;
		}
	}


	@Override
	public DnaStrand cutSplice(String enzyme, String splicee) {
		LinkedDnaStrand returnStrand = new LinkedDnaStrand();

		if (nodeCount == 1) {

			// This is complex so I'll break it down. Using stringbuilder to manipulate
			// original sequence.
			StringBuilder builder = new StringBuilder(firstNode.dnaSequence);

			// Exit condition is builder is empty
			while (builder.length() > 0) {

				// Determines if there are enzymes left to splice
				if (builder.indexOf(enzyme) != -1) {

					// If so, make a substring from the beginning of builder to the beginning of the
					// enzyme
					String mySequence = builder.substring(0, builder.indexOf(enzyme));

					// This substring will be empty if the first part of the sequence is the enzyme.
					// If it is, we append the splicee. If not, we append the sequence before the
					// enzyme and then append splicee
					if (mySequence.isEmpty() == false) {
						returnStrand.append(mySequence);
						returnStrand.append(splicee);
					} else {
						returnStrand.append(splicee);
					}

					// Delete what has been added to the strand from builder for next looping
					builder.delete(0, builder.indexOf(enzyme) + enzyme.length());
				}

				// This is tricky. Once all enzymes have been handled, there may still be dna
				// left in builder. This appends the last bit and then deletes it so we can exit
				// the loop
				else {
					returnStrand.append(builder.toString());
					builder.delete(0, builder.length());
				}
			}
		}
		return returnStrand;
	}


	@Override
	public DnaStrand createReversedDnaStrand() {
		StringBuilder builder = new StringBuilder();
		DnaSequenceNode cursor = firstNode;

		for (int ndx = 0; ndx < nodeCount; ndx++) {
			builder.append(cursor.dnaSequence);
			cursor = cursor.next;
		}

		String mySequence = builder.toString();
		String reversed = reverse(mySequence);
		DnaStrand myStrand = new LinkedDnaStrand(reversed);
		return myStrand;
	}


	@Override
	public int getAppendCount() {
		return appendCount;
	}


	@Override
	public DnaSequenceNode getFirstNode() {
		return firstNode;
	}


	@Override
	public int getNodeCount() {
		return nodeCount;
	}

}
