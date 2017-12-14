package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelElement;

/**
 * Converts M2M, M2T and T2C Tuples to a StandardTuple
 * to be used by TuplePrinter. 
 *
 * @see TuplePrinter
 * 
 */
public class StandardTupleConvertor {
	
	private Hashtable<String, String> absoluteFilePaths;
	
	public StandardTupleConvertor() {
		absoluteFilePaths = new Hashtable<String, String>();
	}
	
	/**
	 * Convert M2MTuples to Standard Tuples
	 * 
	 * @param M2MTuples
	 * @return
	 */
	public ArrayList<StandardTuple> getStandardTuplesForM2M(List<M2MTransformationTuple> M2MTuples) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();
		
		for (M2MTransformationTuple M2MTuple : M2MTuples) {
			tuples.addAll(getStandardTuple(M2MTuple));
		}
		
		return tuples;
	}
	
	/**
	 * Convert M2TTuples to Standard Tuples
	 * 
	 * @param M2TTuples
	 * @return
	 */
	public ArrayList<StandardTuple> getStandardTuplesForM2T(List<M2TTransformationTuple> M2TTuples) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();
		
		for (M2TTransformationTuple M2TTuple : M2TTuples) {
			tuples.addAll(getStandardTuple(M2TTuple));
		}
		
		return tuples;
	}
	
	/**
	 * Convert T2CTuples to Standard Tuples
	 * 
	 * @param T2CTuples
	 * @return
	 */
	public ArrayList<StandardTuple> getStandardTuplesForT2C(List<T2CTuple> T2CTuples) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();
		
		for (T2CTuple T2CTuple : T2CTuples) {
			tuples.addAll(getStandardTuple(T2CTuple));
		}
		
		return tuples;
	}
	
	//********************************************************************
	// StandardTuple convertors
	//********************************************************************
	
	/**
	 * Convert M2MTuple to Standard Tuple.
	 * 
	 * Creates a single tuple if direct source,
	 * else creates a tuple for each indirect source.
	 * 
	 * @param M2MTuple
	 * @return
	 */
	private ArrayList<StandardTuple> getStandardTuple(M2MTransformationTuple M2MTuple) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();

		String targetID = M2MTuple.getTarget().getUniqueID();
		String targetName = M2MTuple.getTarget().getElementID();
		String file = M2MTuple.getTransformationFile();
		String name = M2MTuple.getRuleID();

		ArrayList<IndirectSourceConcept> isc = M2MTuple.getIndirectSourceConcept();
		// In the case of no indirect sources only the main source is used to create standard tuple
		if(isc.size()==0){

			StandardTuple tuple = new StandardTuple();
			tuple.sourceID = M2MTuple.getSource().getUniqueID();
			tuple.sourceName = M2MTuple.getSource().getElementID();
			tuple.sourceTypeCode = M2MTuple.getSource().getTypeCode();
			tuple.targetID = targetID;
			tuple.targetName = targetName;
			tuple.targetTypeCode = M2MTuple.getTarget().getTypeCode();
			tuple.explicitReference = true;
			tuple.implicitReferenceName = M2MTuple.getImplicitReferenceName();
			if (tuple.implicitReferenceName != null && tuple.implicitReferenceName.length() > 0) {
				// function bindings are implicit
				tuple.explicitReference = false;
			}
			tuple.file =  file;
			tuple.name = name;
			
			tuples.add(tuple);

		} else {
			//Otherwise create standard tuple for all the indirect sources 
			for(int j=0; j< isc.size();j++){
				IndirectSourceConcept tempind = isc.get(j);

				StandardTuple tuple = new StandardTuple();
				tuple.sourceID = tempind.getUniqueID();
				tuple.sourceName = M2MTuple.getSource().getElementID();
				tuple.sourceTypeCode = M2MTuple.getSource().getTypeCode();
				tuple.targetID = targetID;
				tuple.targetName = targetName;
				tuple.targetTypeCode = M2MTuple.getTarget().getTypeCode();
				tuple.explicitReference = j == 0; // naive implementation - first source is explicit; following are not
				tuple.implicitReferenceName = M2MTuple.getImplicitReferenceName();
				if (tuple.implicitReferenceName != null && tuple.implicitReferenceName.length() > 0) {
					// function bindings are implicit
					tuple.explicitReference = false;
				}
				tuple.file = file;
				tuple.name = name;
				
				tuples.add(tuple);
			}
		}

		return formatFileNames(tuples);
	}

	
	/**
	 * Convert M2TTuple to Standard Tuple.
	 * 
	 * Creates a single tuple if direct source,
	 * else creates a tuple for each indirect source.
	 * 
	 * @param M2TTuple
	 * @return
	 */
	private ArrayList<StandardTuple> getStandardTuple(M2TTransformationTuple M2TTuple) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();
		
		String targetID = M2TTuple.getTarget().getUniqueID();
		String targetName = targetID;
		String file = M2TTuple.getTarget().getFilePath();
		String name = M2TTuple.getTarget().getTemplateName();

		ArrayList<IndirectSourceConcept> isc = M2TTuple.getIndirectSourceConcept();
		// In the case of no indirect sources only the main source is used to create standard tuple
		if(isc.size()==0){

			StandardTuple tuple = new StandardTuple();
			tuple.sourceID = M2TTuple.getSource().getUniqueID();
			tuple.sourceName = tuple.sourceID;
			tuple.sourceTypeCode = M2TTuple.getSource().getTypeCode();
			tuple.targetID = targetID;
			tuple.targetName = targetName;
			tuple.targetTypeCode = M2TTuple.getTarget().getTypeCode();
			tuple.explicitReference = true;
			tuple.file =  file;
			tuple.name = name;
			
			tuples.add(tuple);

		} else {
			//Otherwise create standard tuple for all the indirect sources 
			for(int j=0; j< isc.size();j++){
				IndirectSourceConcept tempind = isc.get(j);

				StandardTuple tuple = new StandardTuple();
				tuple.sourceID = tempind.getUniqueID();
				tuple.sourceName =  M2TTuple.getSource().getUniqueID();
				tuple.sourceTypeCode = M2TTuple.getSource().getTypeCode();
				tuple.targetID = targetID;
				tuple.targetName = targetName;
				tuple.targetTypeCode = M2TTuple.getTarget().getTypeCode();
				tuple.explicitReference = j == 0; // naive implementation - first source is explicit; following are not
				tuple.file = file;
				tuple.name = name;
				
				tuples.add(tuple);
			}
		}
		
		return formatFileNames(tuples);
	}
	
	/**
	 * Convert T2CTuple to Standard Tuple.
	 * 
	 * @param T2CTuple
	 * @return
	 */
	private ArrayList<StandardTuple> getStandardTuple(T2CTuple T2CTuple) {
		ArrayList<StandardTuple> tuples = new ArrayList<StandardTuple>();

		if (T2CTuple.getTarget().isEmptyLine()) return tuples;
		
		StandardTuple tuple = new StandardTuple();
		tuple.sourceID = T2CTuple.getSource().getUniqueID();
		tuple.sourceName = tuple.sourceID;
		tuple.sourceTypeCode = T2CTuple.getSource().getTypeCode();
		tuple.targetID = T2CTuple.getTarget().getUniqueID();
		tuple.targetName = tuple.targetID;
		tuple.targetTypeCode = T2CTuple.getTarget().getTypeCode();
		tuple.explicitReference = true;
		tuple.file = T2CTuple.getTarget().getFilePath();
		tuple.name = stripFilePath(T2CTuple.getTarget().getFilePath());
		
		tuples.add(tuple);
		
		return formatFileNames(tuples);
	}
	
	//********************************************************************
	// File name format helpers
	//********************************************************************
	
	private ArrayList<StandardTuple> formatFileNames(ArrayList<StandardTuple> tuples) {
		for (StandardTuple tuple : tuples) {
			tuple.targetID = stripFilePath(tuple.targetID);
			tuple.sourceID = stripFilePath(tuple.sourceID);
			tuple.file = getFileAbsolutePath(tuple.file);
		}
		
		return tuples;
	}
	
	private String stripFilePath(String fileName) {
		File f = new File(fileName);
		String name = f.getName();
		
		return name;
	}
	
	private String getFileAbsolutePath(String fileName) {
		if (fileName == null) return fileName;
		if (absoluteFilePaths.containsKey(fileName)) {
			return absoluteFilePaths.get(fileName);
		}
		
		File f = new File(fileName);
		String absPath = f.getAbsolutePath();
		absoluteFilePaths.put(fileName, absPath);
		
		return absPath;
	}
}
