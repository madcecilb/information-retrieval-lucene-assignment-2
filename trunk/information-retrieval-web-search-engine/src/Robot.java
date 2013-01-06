import java.util.ArrayList;


public class Robot {
	
	private Boolean considerable;
	private ArrayList<String> disallowList;
	
	public Robot(String robotString){
		String[] strArray = robotString.split(" Disallow: ");
		disallowList = new ArrayList<>();
		//"gent: *" is part of User-Agent: *, what means that this rule is for all agents
		//here we use gent instead of agent, because it could be agent or Agent
		if(strArray[0].contains("gent: *")){
			considerable = true;
			
			for(int i = 1; i < strArray.length; i++){
				disallowList.add(strArray[i].split(" ")[0]);
			}
		}
		else{
			considerable = false;
		}
	}
	
	public String toString(){
		String str = considerable.toString();
		for (String disallow : disallowList) {
			str = str + " " + disallow;
		}
		return str;
	}
	
	public Boolean getIsForUs() {
		return considerable;
	}

	public void setIsForUs(Boolean consid) {
		considerable = consid;
	}

	public ArrayList<String> getDisallowList() {
		return disallowList;
	}
	public void setDisallowList(ArrayList<String> disallows) {
		disallowList = disallows;
	}
}
