package csen1002.main.task6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Write your info here
 * 
 * @name Farida El Genedi
 * @id 46-2291
 * @labNumber 10
 */

public class CfgFirstFollow {
	//V
	List<String> nonTerminalsList=new ArrayList<String>();
	//T
	List<String> terminalsList=new ArrayList<String>();
	//list of the rules in the form of a list of VRs
	List<VR> vrList=new ArrayList<VR>();

	public CfgFirstFollow(String cfg) {
		//parse string given in the form V#T#R
		String[] cfgHashSeperated=cfg.split("#");
		//the list of Vs
		nonTerminalsList.addAll(Arrays.asList(cfgHashSeperated[0].split(";")));
		//the list of Ts
		terminalsList.addAll(Arrays.asList(cfgHashSeperated[1].split(";")));
		//parse R and initiate VR list
		List<String> seperatedBySemicolon=Arrays.asList(cfgHashSeperated[2].split(";"));
		for(String s:seperatedBySemicolon) {
			String[] seperatedBySlash=s.split("/");
			Set<String> rulesSet=new LinkedHashSet<String>();
			rulesSet.addAll(Arrays.asList(seperatedBySlash[1].split(",")));
			VR toAdd=new VR(seperatedBySlash[0],rulesSet,  new LinkedHashSet<String>(),  new LinkedHashSet<String>());
			this.vrList.add(toAdd);
		}
	}

	public String first() {

		boolean change=true;

		//loop till no more first rules added
		while(change) {
			change=false;
			//loop over all variables/non-terminals
			for(VR vr: vrList) {
				//loop over all variable's rules (MAIN LOOP)
				for(String rule: vr.rList) {
					//check if all rule components contain epsilon, if yes add e to the first of this variable
					if(allRuleHasEpsilon(rule)) {
						if(!getFirstOf(vr.vString).contains("e")) {
							//add first to list of vr
							vr.firstList.add("e");
							change=true;
						}
					}

					//loop over the variable's current rule from the main loop
					int k=rule.length();
					for(int i=0;i<k;i++) {
						if(i==0) {
							Set<String> toAddList= new LinkedHashSet<String>();
							toAddList.addAll(getFirstOf(rule.substring(0,1)));
							toAddList.remove("e");
							for(String toAdd:toAddList) {
								if(!getFirstOf(vr.vString).contains(toAdd)) {
									//add first to list of vr
									vr.firstList.add(toAdd);
									change=true;
								}
							}

						}
						else {
							if(allRuleHasEpsilon(rule.substring(0, i))){
								Set<String> toAddList= new LinkedHashSet<String>();
								toAddList.addAll(getFirstOf(rule.substring(i, i+1)));
								toAddList.remove("e");
								for(String toAdd:toAddList) {
									if(!getFirstOf(vr.vString).contains(toAdd)) {
										//add first to list of vr
										vr.firstList.add(toAdd);
										change=true;
									}
								}
							}
						}
					}
				}
			}
		}
		//sort 
		for(VR vr:vrList) {
			List <String> toSortList=new ArrayList<String>();
			toSortList.addAll(vr.firstList);
			Collections.sort(toSortList);
			vr.firstList.clear();
			vr.firstList.addAll(toSortList);
		}

		//making el string
		String allVariablesString="";
		for(VR vr: vrList) {
			//append the first of the variable to the return string
			if(allVariablesString.equals("")) {
				allVariablesString+=vr.vString+"/";

			}
			else {
				allVariablesString+=";"+vr.vString+"/";
			}
			for(String currentFirst:vr.firstList) {
				allVariablesString+=currentFirst;
			}
		}

		return allVariablesString;
	}

	public Set<String> getFirstOf(String v){
		if(terminalsList.contains(v)) {
			return new LinkedHashSet<>(Collections.singleton(v));
		}
		for(VR vr:vrList) {
			if(vr.vString.equals(v)) {
				return vr.firstList;
			}
		}
		return new LinkedHashSet<String>();
	}

	public Set<String> getFollowOf(String v){
		for(VR vr:vrList) {
			if(vr.vString.equals(v)) {
				return vr.followList;
			}
		}
		return new LinkedHashSet<String>();
	}

	//return true if all rule components first have epsilon, if empty return false
	public boolean allRuleHasEpsilon(String rule) {
		//if the rule is epsilon
		if(rule.equals("e"))
			return true;
		//if the rule is all terminals
		if(isAllLowerCase(rule))
			return false;
		//else there are variables in the rule
		String[] ruleSeperated=rule.split("");
		for(String c:ruleSeperated) {
			//if the rule contains a terminal that isn't an epsilon
			if(Character.isLowerCase(c.toCharArray()[0]))
				return false;
			//if nonterminal, get first and check if it has epsilon
			Set<String> ruleFirst = new LinkedHashSet<String>();
			//get its First from vrList			
			ruleFirst.addAll(getFirstOf(c));
			if(!ruleFirst.contains("e") || ruleFirst.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllLowerCase(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	public String follow() {
		this.first();
		//add $ to follow of S
		for(VR vr: vrList) {		
			if(vr.vString.equals("S")) {
				vr.followList.add("$");
			}
		}

		boolean change=true;
		while(change) {
			change=false;
			//loop over all rules
			for(VR vr : vrList) {
				for(String rule : vr.rList) {					
					//pass over rule searching for a variable
					for(int i=0;i<rule.length();i++) {
						String current=rule.substring(i, i+1);
						if(isVariable(current)){
							//beta is anything after rule
							String betaString=rule.substring(i+1, rule.length());
							//handle if variable last in rule case so beta is empty
							if(betaString.equals("")) {
								//add follow of variable el gai menha el rule to follow of current variable
								Set<String> vFollow=new LinkedHashSet<String>();
								vFollow.addAll(vr.followList);
								change=change || addSetToFollow(vFollow,current);
							}
							else {
								//loop on betaString
								List<String> betaStringList=Arrays.asList(betaString.split(""));
								for(String betaComponent: betaStringList) {
									//get first of beta component 
									Set<String> betaComponentFirst=new LinkedHashSet<String>();
									betaComponentFirst.addAll(getFirstOf(betaComponent));
									//if has epsilon continue else add to follow and break
									if(betaComponentFirst.contains("e")) {
										betaComponentFirst.remove("e");
										change = change || addSetToFollow(betaComponentFirst, current);
										//if last beta component and has epsilon
										if(i==rule.length()-1) {
											//add follow of variable el gai menha el rule to follow of current variable
											Set<String> vFollow=new LinkedHashSet<String>();
											vFollow.addAll(vr.followList);
											change = change || addSetToFollow(vFollow,current);
										}

									}
									else {
										change = change || addSetToFollow(betaComponentFirst, current);
										break;
									}				
								}
							}
						}
					}
				}
			}
		}

		//sort 
		for(VR vr:vrList) {
			boolean hasDollarSign=false;
			List <String> toSortList=new ArrayList<String>();
			toSortList.addAll(vr.followList);
			if(toSortList.contains("$")) {
				hasDollarSign=true;
				toSortList.remove("$");
			}
			Collections.sort(toSortList);
			vr.followList.clear();
			if(hasDollarSign)
				vr.followList.add("$");
			vr.followList.addAll(toSortList);
		}

		//making el string
		String allVariablesString="";
		for(VR vr: vrList) {
			//append the first of the variable to the return string
			if(allVariablesString.equals("")) {
				allVariablesString+=vr.vString+"/";

			}
			else {
				allVariablesString+=";"+vr.vString+"/";
			}
			for(String currentFollow:vr.followList) {
				allVariablesString+=currentFollow;
			}
		}
		return allVariablesString;
	}

	public boolean addSetToFollow(Set<String> toAddSet,String v) {
		boolean change=false;
		for(VR vr:vrList) {
			if(vr.vString.equals(v)) {
				for(String toAdd: toAddSet) {
					if(!getFollowOf(v).contains(toAdd) && !toAdd.equals("e")) {
						vr.followList.add(toAdd);
						change=true;
					}
				}
			}
		}
		return change;
	}


	public boolean isVariable(String str) {
		//if not lowercase return true
		if (!Character.isLowerCase(str.toCharArray()[0])) {
			return true;
		}
		return false;
	}

	public static void main(String [] args) {
		CfgFirstFollow testCfgFirstFollow=new CfgFirstFollow("S;T;L#a;b;c;d;i#S/ScT,T;T/aSb,iaLb,e;L/SdL,S");
		testCfgFirstFollow.follow();
	}

	//-------------------------------------------------Classes----------------------------------------
	//represents nonterminal with its rules and first and follow
	class VR{

		String vString;
		Set<String> rList=new LinkedHashSet<String>();
		Set<String> firstList = new LinkedHashSet<String>();
		Set<String> followList = new LinkedHashSet<String>();

		//Default constructor
		public VR() {

		}

		//constructor
		public VR(String vString, Set<String> rList, Set<String> firstList, Set<String> followList) {
			this.vString=vString;
			this.rList.addAll(rList);
			this.firstList.addAll(firstList);
			this.followList.addAll(followList);
		}
	}
}
