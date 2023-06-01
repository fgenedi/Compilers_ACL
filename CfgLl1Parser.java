package csen1002.main.task7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Write your info here
 * 
 * @name Farida El Genedi
 * @id 46-2291
 * @labNumber 10
 */

public class CfgLl1Parser {
	//V
	List<String> nonTerminalsList=new ArrayList<String>();
	//T
	List<String> terminalsList=new ArrayList<String>();
	//list of variables and their rules
	List<VR> vrList=new ArrayList<VR>();
	//parsing table represented as row with a variable and a hashmap for terminals and rules
	List<ParsingTable> parsingTableRows= new ArrayList<ParsingTable>();

	//V#T#R#I#O.
	public CfgLl1Parser(String input) {
		//parse string given in the form V#T#R#I#O
		String[] cfgHashSeperated=input.split("#");
		//the list of Vs
		nonTerminalsList.addAll(Arrays.asList(cfgHashSeperated[0].split(";")));
		//the list of Ts
		terminalsList.addAll(Arrays.asList(cfgHashSeperated[1].split(";")));
		//parse R and initiate VR list and parse I and O to populate list of RFirstFollow in the vrList for each VR
		List<String> seperatedBySemicolonR=Arrays.asList(cfgHashSeperated[2].split(";"));
		List<String> seperatedBySemicolonI=Arrays.asList(cfgHashSeperated[3].split(";"));
		List<String> seperatedBySemicolonO=Arrays.asList(cfgHashSeperated[4].split(";"));
		for(String s:seperatedBySemicolonR) {
			String[] seperatedBySlash=s.split("/");
			List<String> rulesSet=new ArrayList<String>();
			rulesSet.addAll(Arrays.asList(seperatedBySlash[1].split(",")));
			Set<String> followSet=new LinkedHashSet<String>();
			Set<RFirstFollow> rFirstFollow=new LinkedHashSet<RFirstFollow>();
			for(int i=0;i<rulesSet.size();i++) {

				Set<String> firstSet=new LinkedHashSet<String>();
				//get rules first
				for(String first: seperatedBySemicolonI) {
					String[] seperatedBySlashI=first.split("/");
					//if same variable
					if(seperatedBySlashI[0].equals(seperatedBySlash[0])) {
						firstSet.addAll(Arrays.asList((seperatedBySlashI[1].split(","))[i].split("")));
					}
				}
				//get rules follow
				for(String follow: seperatedBySemicolonO) {
					String[] seperatedBySlashO=follow.split("/");
					if(seperatedBySlashO[0].equals(seperatedBySlash[0])) {
						followSet.addAll(Arrays.asList(seperatedBySlashO[1].split("")));

					}
				}
				RFirstFollow toAddFirstFollow=new RFirstFollow(rulesSet.get(i),firstSet);

				rFirstFollow.add(toAddFirstFollow);

			}
			VR toAdd=new VR(seperatedBySlash[0],rFirstFollow,followSet);
			this.vrList.add(toAdd);
		}

		//create parsing table
		//loop over variables to create row per variable
		for(VR vr:vrList) {
			ParsingTable toAddParsingTable=new ParsingTable(vr.vString, new HashMap<String, String>());
			//loop over terminals to create column per terminal with the addition of $
			List<String> columnList= new ArrayList<String>();
			columnList.addAll(terminalsList);
			columnList.add("$");
			for(String term:columnList) {
				boolean foundMatch=false;
				//loop over rules to find matching entry to put in table
				for(RFirstFollow rFirstFollow : vr.rulesSet) {
					if(rFirstFollow.firstSet.contains(term)) {
						toAddParsingTable.terminalRuleHashMap.put(term, rFirstFollow.rule);
						foundMatch=true;
					}
					else if(rFirstFollow.firstSet.contains("e")){
						if(termInFollowOfVariable(vr.followSet,term)) {
							toAddParsingTable.terminalRuleHashMap.put(term, rFirstFollow.rule);
							foundMatch=true;
						}
					}
				}
				if(!foundMatch) {
					toAddParsingTable.terminalRuleHashMap.put(term, "");
				}
			}
			parsingTableRows.add(toAddParsingTable);
		}
	}

	public boolean termInFollowOfVariable(Set<String> followSet, String term){

		if(followSet.contains(term))
			return true;

		return false;
	}

	public boolean isVariable(String str) {
		//if not lowercase return true
		if (!Character.isLowerCase(str.toCharArray()[0])) {
			return true;
		}
		return false;
	}
	public boolean isTerminal(String str) {
		//if lowercase return true
		if (!Character.isLowerCase(str.toCharArray()[0])) {
			return false;
		}
		return true;
	}

	public String parse(String input) {
		//string to append steps popped from stack 
		String stepsString="S";
		//append $ to input to know when stop
		input+="$";
		//start stack of steps with S
		Stack<String> stepsStack=new Stack<String>();
		stepsStack.push("$");
		stepsStack.push("S");
		//loop over string
		List<String> inputList=Arrays.asList(input.split(""));
		int i=0;
		while(true) {
			String lookahead=inputList.get(i);
			String stackTop=stepsStack.peek();
			//error if one finished and other didn't
			if(!lookahead.equals("$") && stackTop.equals("$")) {
				stepsString+=";ERROR";
				break;
			}
			//else if neither are $
			else if(isTerminal(stackTop)) {
				//two cases
				//if both terminals match then remove and move forward
				if(stackTop.equals(lookahead)) {
					i++;
					
					stepsStack.pop();
				}
				//Error if terminals aren't equal
				else {
					stepsString+=";ERROR";
					break;
				}
			}
			//top is variable and have input then find replacement from parsing table
			else {
				String replacement= findEntry(stackTop, lookahead);
				//if entry is empty in table then error 
				if(replacement.equals("")) {
					stepsString+=";ERROR";
					break;
				}
				//append stepsString as will substitute with the replacement string
				String replacementString=getStepString(stackTop, replacement,stepsString);
				if(replacementString.equals("NOT FOUND GET STRING")) {
					break;
				}
				if(!replacementString.equals(""))
					stepsString=stepsString+";"+replacementString;
				//pop rule to be replaced
				stepsStack.pop();
				//push replacement rule bel 3aks
				List<String> replacementRuleList=Arrays.asList(replacement.split(""));
				Collections.reverse(replacementRuleList);
				for(String replacementComponent:replacementRuleList) {
					if(!replacementComponent.equals("e"))
						stepsStack.push(replacementComponent);
				}	
				if(lookahead.equals("$"))
					break;
			}
		}
		String xString=stepsString.split(";")[stepsString.split(";").length-1]+"$";
		if(!xString.equals(input) && !xString.equals("ERROR$")) {
			stepsString+=";ERROR";
		}
		return stepsString;
	}

	public String getStepString(String stackTop, String replacement,String stepsString) {
		//replace leftmost rule match with replacement
		if(stepsString.length()==1)
			return replacement;
		List<String> steps=Arrays.asList(stepsString.split(";"));
		String previousStep=steps.get(steps.size()-1);
		if(replacement.equals("e"))
			replacement="";
		for(int i=0;i<previousStep.length();i++) {
			if(previousStep.charAt(i)==stackTop.charAt(0)) {
				return previousStep.substring(0,i)+replacement+previousStep.subSequence(i+1, previousStep.length());
			}
		}
		return "NOT FOUND GET STRING";
	}

	public String findEntry(String stackTop,String lookahead) {
		for(ParsingTable row:parsingTableRows) {
			//find row
			if(row.variable.equals(stackTop)) {
				//find column
				return row.terminalRuleHashMap.get(lookahead);
			}
		}
		return "ENTRY NOT FOUND";
	}

	//-------------------------------------------Classes---------------------------------------
	//class of variable and its set of rules each with their first and follow
	class VR{

		String vString;
		Set<RFirstFollow> rulesSet=new LinkedHashSet<RFirstFollow>();
		Set<String> followSet=new LinkedHashSet<String>();
		//Default constructor
		public VR() {

		}

		//constructor
		public VR(String vString, Set<RFirstFollow> rulesSet, Set<String> followSet) {
			this.vString=vString;
			this.rulesSet.addAll(rulesSet);
			this.followSet.addAll(followSet);

		}
	}

	//class of rule and its first and follow
	class RFirstFollow{
		String rule;
		Set<String> firstSet=new LinkedHashSet<String>();

		//default constructor
		public RFirstFollow() {

		}

		//constructor
		public RFirstFollow(String rule, Set<String> firstSet){
			this.rule=rule;
			this.firstSet.addAll(firstSet);
		}
	}

	//class of parsing table data structure
	class ParsingTable{
		String variable;
		HashMap<String, String> terminalRuleHashMap=new HashMap<String, String>();

		//default constructor
		public ParsingTable() {

		}

		public ParsingTable(String variable, HashMap<String, String> terminalRuleHashMap) {
			this.variable=variable;
			this.terminalRuleHashMap.putAll(terminalRuleHashMap);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");

			for (String key : this.terminalRuleHashMap.keySet()) {
				sb.append(key).append("=").append(this.terminalRuleHashMap.get(key)).append(", ");
			}

			if (!this.terminalRuleHashMap.isEmpty()) {
				// Remove the trailing comma and space
				sb.setLength(sb.length() - 2);
			}

			sb.append("}");
			return sb.toString();
		}
	}
}