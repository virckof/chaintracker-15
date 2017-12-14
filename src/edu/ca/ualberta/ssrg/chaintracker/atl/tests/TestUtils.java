package edu.ca.ualberta.ssrg.chaintracker.atl.tests;

import java.util.ArrayList;

import edu.ca.ualberta.ssrg.chaintracker.vos.M2MTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetAttribute;

public class TestUtils {
	public static M2MTransformation GetTransformationByName(ArrayList<M2MTransformation> transformations, String name) {
		for (M2MTransformation transformation : transformations) {
			if (transformation.getName().equals(name)) {
				return transformation;
			}
		}
		
		return null;
 	}
	
	public static TargetAttribute GetAttributeByName(ArrayList<TargetAttribute> attributes, String name) {
		for (TargetAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		
		return null;
 	}
}
