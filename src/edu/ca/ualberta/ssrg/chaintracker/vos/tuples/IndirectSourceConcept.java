package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public class IndirectSourceConcept extends ModelConcept{
	
	public static final String MODEL_ELEMENT_ATTRIBUTE = "MEA";
	public static final String MODEL_ELEMENT_REFERENCE = "MER";
	
	private String type; // from above MEA/MER
	
	private String relationName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	
	public String toStringReference(){
		return getModelName()+":"+getElementID()+":"+relationName;
	}
	
	@Override
	public String getUniqueID(){
		if(getType().equals(MODEL_ELEMENT_ATTRIBUTE)){
			return  sanitazeID(getModelName()+";"+getElementID()+";"+getAttributeID());
		}
		else{//MODEL_ELEMENT_REFERENCE)
			return  sanitazeID(getModelName()+";"+getElementID()+";"+getRelationName());
		}
		
	}
	
	private String sanitazeID(String id){
		String sID = id.replace("/", "");
		return sID;
	}
	

}
