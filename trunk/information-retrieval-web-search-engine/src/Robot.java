import java.util.ArrayList;


public class Robot {
	
	private Boolean IsForUs;
	private ArrayList<String> DisallowList;
	
	public Robot(String robotString){
		String[] strArray = robotString.split(" Disallow: ");
		DisallowList = new ArrayList<>();
		//"gent: *" is part of User-Agent: *, what means that this rule is for all agents
		//here we use gent instead of agent, because ic could be agent or Agent
		if(strArray[0].contains("gent: *")){
			IsForUs = true;
			
			for(int i = 1; i < strArray.length; i++){
				DisallowList.add(strArray[i].split(" ")[0]);
			}
		}
		else{
			IsForUs = false;
		}
	}
	
	public String toString(){
		String str = IsForUs.toString();
		for (String disallow : DisallowList) {
			str = str + " " + disallow;
		}
		return str;
	}
	
	public Boolean getIsForUs() {
		return IsForUs;
	}

	public void setIsForUs(Boolean isForUs) {
		IsForUs = isForUs;
	}

	public ArrayList<String> getDisallowList() {
		return DisallowList;
	}
	public void setDisallowList(ArrayList<String> disallowList) {
		DisallowList = disallowList;
	}
}
