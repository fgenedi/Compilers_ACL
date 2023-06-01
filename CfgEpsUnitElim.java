package csen1002.main.task4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Write your info here
 * 
 * @name Farida Ayman El Genedi
 * @id 46-2291
 * @labNumber 10
 */

public class CfgEpsUnitElim {
	//for the toString
	String Vstring;
	String Tstring;

	//for elim methods
	List<String> V=new ArrayList<String>();
	List<String> T=new ArrayList<String>();
	List<variableAndItsRules> vAndItsRules=new ArrayList<variableAndItsRules>();

	//after epsilon and unit elim
	List<String> R=new ArrayList<String>();

	public CfgEpsUnitElim(String cfg) {
		//input form V#T#R
		String [] splitByHash=cfg.split("#");

		//V
		this.Vstring=splitByHash[0];
		this.V=Arrays.asList(splitByHash[0].split(";"));

		//Alphabet/sigma/T
		this.Tstring=splitByHash[1];
		this.T=Arrays.asList(splitByHash[1].split(";"));

		//Rules with epsilon and unit
		List<String> RwEU=Arrays.asList(splitByHash[2].split(";"));

		//create list of variable and its rules
		for(String s:RwEU) {
			String[] splitBySlash=s.split("/");
			//rules 
			List<String> rules=Arrays.asList(splitBySlash[1].split(","));
			this.vAndItsRules.add(new variableAndItsRules(splitBySlash[0], rules));
		}

	}

	//Sorts rules by upper-case firstly then by alphabetical order
	public List<String> sortStrings(List<String> rules) {
		List<String> uppercaseStrings = new ArrayList<>();
		List<String> lowercaseStrings = new ArrayList<>();

		// Partition strings into uppercase and lowercase lists
		for (String s : rules) {
			if (s.length() > 0 && Character.isUpperCase(s.charAt(0))) {
				uppercaseStrings.add(s);
			} else {
				lowercaseStrings.add(s);
			}
		}

		// Sort each list separately
		Collections.sort(uppercaseStrings);
		Collections.sort(lowercaseStrings);

		// Combine sorted lists
		List<String> sortedStrings = new ArrayList<>(rules.size());
		sortedStrings.addAll(uppercaseStrings);
		sortedStrings.addAll(lowercaseStrings);

		return sortedStrings;
	}

	//return form V#T#R
	public String toString() {	

		//add vAndItsRules to R and sort the rules
		for(variableAndItsRules vr:this.vAndItsRules) {
			String toAddString=vr.variable+"/";
			vr.rules=sortStrings(vr.rules);
			toAddString+=vr.toString();
			this.R.add(toAddString);
		}

		//new rule set
		String R="";
		if(!this.R.isEmpty()) {
			R+=this.R.get(0);
			for(int i=1;i<this.R.size();i++) {
				R+=";"+this.R.get(i);
			}
		}

		return this.Vstring+"#"+this.Tstring+"#"+R;
	}

	public void eliminateEpsilonRules() {
		//loop over rules and identify which contain epsilon, when found remove the epsilon rule part and
		//each occurrence of the variable that has epsilon in other rules with epsilon
		List<String> visitedList=new ArrayList<String>();
		while(terminalsHaveEpsilon()) {
			for(variableAndItsRules vr : this.vAndItsRules) {
				if(vr.rules.contains("e")) {
					visitedList.add(vr.variable);
					String vWithEpsilon=vr.variable;
					//remove epsilon from rule it occurs in
					vr.rules.remove("e");
					//replace each  occurrence of V in other rules with epsilon
					removeVFromOtherRules(vWithEpsilon, visitedList); 
				}		
			}
		}
	}

	//returns true if there are epsilon rules in the CFG
	public boolean terminalsHaveEpsilon()
	{
		for(variableAndItsRules vr : this.vAndItsRules) {
			if(vr.rules.contains("e"))
				return true;
		}
		return false;
	}

	//Replaces occurrences of variable v in all rules with epsilon
	public void removeVFromOtherRules(String v, List<String> hadEpsilonBefore) {
		for(variableAndItsRules vr: this.vAndItsRules) {
			int size=vr.rules.size();
			//loop over rules
			for(int i=0;i<size;i++) {
				List<String> visitedList=new ArrayList<String>();
				String rule=vr.rules.get(i);
				if(rule.contains(v) && !visitedList.contains(rule)) {
					//two cases:
					//1. if the variable is stand-alone we add an epsilon rule if rule doesn't already have epsilon
					if(rule.equals(v)) {
						if(!vr.rules.contains("e") && !hadEpsilonBefore.contains(vr.variable)) {
							vr.rules.add("e");
						}
					}
					//2. variable not alone, replace with permutations of the rule given v can be epsilon
					else {
						List<String> permStrings=getPermutations(rule, v);
						for(String s: permStrings) {
							if(!vr.rules.contains(s)) {
								if(s.equals("") && !vr.rules.contains("e") && !hadEpsilonBefore.contains(vr.variable)) {
									vr.rules.add("e");
									size++;
								}
								else if(!s.equals("")){
									vr.rules.add(s);
									visitedList.add(s);
									size++;
								}
							}
						}						
					}
					visitedList.add(rule);
				}
			}
		}
	}

	//returns list of permutations of a string given v in this string can be the empty string
	public List<String> getPermutations(String rule, String v){
		List<String> permutations=new ArrayList<String>();

		// Iterate through each character in the rule
		for (int i = 0; i < rule.length(); i++) {
			String current = rule.substring(i,i+1);

			// If the character is v, create a copy of the original string without the v
			if (current.equals(v.substring(0,1))) {
				String p = rule.substring(0, i) + rule.substring(i + 1);
				permutations.add(p);

				// Create a copy of the possibleString with the next v removed
				for (int j = i; j < p.length(); j++) {
					String current2 = p.substring(j,j+1);
					if (current2.equals(v.substring(0,1))) {
						String newString = p.substring(0, j) + p.substring(j + 1);
						permutations.add(newString);
					}
				}
			}
		}

		return permutations;
	}

	public void eliminateUnitRules() {
		//loop as long as there are still unit rules present
		while(terminalsHaveUnit()) {
			for(variableAndItsRules vr: this.vAndItsRules) {
				int size=vr.rules.size();
				for(int i=0;i<size;i++) {
					String rule= vr.rules.get(i);
					if(rule.equals(vr.variable)) {
						vr.rules.remove(rule);
						size--;
					}
					else if(rule.length()==1 && Character.isUpperCase(rule.charAt(0))){
						vr.rules.remove(rule);
						size--;
						List<String> newRules= getRulesOfUnitElement(rule.substring(0,1));
						for(String newRule: newRules) {
							if(!vr.rules.contains(newRule)) {
								vr.rules.add(newRule);
								size++;
							}
						}
					}
				}
			}
		}
	}

	public List<String> getRulesOfUnitElement(String v)
	{
		List<String> newRules= new ArrayList<String>();
		for(variableAndItsRules vr: this.vAndItsRules) {
			if(vr.variable.equals(v)) {
				newRules.addAll(vr.rules);
			}
		}
		return newRules;
	}

	public boolean terminalsHaveUnit() {
		for(variableAndItsRules vr: this.vAndItsRules) {
			for(String rule: vr.rules) {
				if(rule.length()==1 && Character.isUpperCase(rule.charAt(0)))
					return true;
			}
		}
		return false;
	}

	//------------------------------------------Classes----------------------------------------------------

	class variableAndItsRules{
		String variable;
		List<String> rules=new ArrayList<String>();

		//default constructor
		public variableAndItsRules() {

		}

		//constructor
		public variableAndItsRules(String variable, List<String> rules) {
			this.variable=variable;
			this.rules.addAll(rules);			
		}

		public boolean containsEpsilon() {
			if(rules.contains("e"))
				return true;
			return false;
		}

		public String toString() {
			String s="";
			if(!this.rules.isEmpty()) {
				s+=rules.get(0);
				for(int i=1;i<rules.size();i++) {
					s+=","+rules.get(i);
				}
			}
			return s;
		}
	}
}
