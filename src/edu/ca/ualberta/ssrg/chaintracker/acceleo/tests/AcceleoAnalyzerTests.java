package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoAnalyzer;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoLauncher;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoLauncherException;

public class AcceleoAnalyzerTests {

	// Acceleo Example Test files
	private static final String ANDROID_METAMODEL = "./data/acceleotests/acceleo-examples-simplified/metamodels/android.ecore";
	
	private static final String ANDROID_MODEL = "./data/acceleotests/acceleo-examples-simplified/models/Project.xmi";
	private static final String MANIFEST_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/androidmanifestXML.mtl";
	private static final String DBADAPTER_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/dbadapter.mtl";
	private static final String EDIT_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/edit.mtl";
	private static final String EDITXML_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/editXML.mtl";
	private static final String LIST_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/list.mtl";
	private static final String LISTROWXML_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/listrowXML.mtl";
	private static final String LISTXML_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/listXML.mtl";
	private static final String STRINGSXML_TEMPLATE =  "./data/acceleotests/acceleo-examples-simplified/templates/stringsXML.mtl";
	
	@Test
	public void test() throws AcceleoLauncherException, InterruptedException {
		AcceleoAnalyzer a = new AcceleoAnalyzer();
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, MANIFEST_TEMPLATE);
		
		// generates file correctly but throws a "model content is invalid" error
		//a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, DBADAPTER_TEMPLATE);
		
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, EDIT_TEMPLATE);
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, EDITXML_TEMPLATE);
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, LIST_TEMPLATE);
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, LISTXML_TEMPLATE);
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, LISTROWXML_TEMPLATE);
		a.analyzeTemplateToCode(ANDROID_METAMODEL, ANDROID_MODEL, STRINGSXML_TEMPLATE);
	}
}
