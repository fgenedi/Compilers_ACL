package csen1002.main.task5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Write your info here
 * 
 * @name Farida El Genedi
 * @id 46-2291
 * @labNumber 10
 */

public class CfgLeftRecElim {

	//list of Vs
	List<String> vList=new ArrayList<String>();
	//list of terminals/alphabets/Ts
	List<String> tList=new ArrayList<String>();
	//list of the rules in the form of list of VRs
	List<VR> vrList=new ArrayList<VR>();

	//Initializes attributes of CfgLeftRecElim
	public CfgLeftRecElim(String cfg) {
		//input form is V#T#R.
		//Parsing
		String[] seperatedByHash= cfg.split("#");
		//the list of Vs
		vList.addAll(Arrays.asList(seperatedByHash[0].split(";")));
		//the list of Ts
		tList.addAll(Arrays.asList(seperatedByHash[1].split(";")));
		//parse rules
		List<String> seperatedBySemicolon=Arrays.asList(seperatedByHash[2].split(";"));
		for(String s:seperatedBySemicolon) {
			String[] seperatedBySlash=s.split("/");
			VR toAdd=new VR(seperatedBySlash[0],Arrays.asList(seperatedBySlash[1].split(",")));
			this.vrList.add(toAdd);
		}
	}

	public String toString() {
		String vString="";
		if(!vList.isEmpty()) {
			vString+=vList.get(0);
			for(int i=1;i<vList.size();i++) {
				vString+=";"+vList.get(i);
			}
		}

		String tString="";
		if(!tList.isEmpty()) {
			tString+=tList.get(0);
			for(int i=1;i<tList.size();i++) {
				tString+=";"+tList.get(i);
			}
		}

		String rString="";
		if(!vrList.isEmpty()) {
			rString+=vrList.get(0).vString+"/"+vrList.get(0).toString();
			for(int i=1;i<vrList.size();i++) {
				rString+=";"+vrList.get(i).vString+"/"+vrList.get(i).toString();
			}
		}

		return vString+"#"+tString+"#"+rString;
	}

	public void eliminateLeftRecursion() {
		int size=this.vrList.size();
		for(int i=0;i<size;i++) {
			VR vr=this.vrList.get(i);
			//if the rules contain left recursion
			if(hasLeftRec(vr)) {
				//get alphas
				List<String> alphaStrings=getAlphas(vr);
				//get betas
				List<String> betaStrings=getBetas(vr);
				//if V' doesn't already exist create it and adjust rules
				if(!this.vList.contains(vr.vString+"'")) {
					//add V' to list of variables
					this.vList.add(vr.vString+"'");
					//remove vr's rules 
					vr.rList.clear();
					//add betas to vr's rules followed by V'
					for(String betaString:betaStrings) {
						vr.rList.add(betaString+vr.vString+"'");
					}
					//create new VR with V' 
					VR vrApostrophe=new VR(vr.vString+"'",new ArrayList<String>());	
					//add rules alpha followed by V' and epsilon
					for(String alphaString:alphaStrings) {
						vrApostrophe.rList.add(alphaString+vr.vString+"'");
					}
					vrApostrophe.rList.add("e");
					this.vrList.add(vrApostrophe);
					size++;
					//replace each occurrence of V in LHS of other rules with its new rules
					replaceLHSOccurrences(vr.vString, vr.rList, i);
				}
			}
			else {
				replaceLHSOccurrences(vr.vString, vr.rList, i);
			}
		}
	}

	//returns true if variable has immediate left recursion
	public boolean hasLeftRec(VR vr) {
		for(String s:vr.rList) {
			if(s.substring(0,1).equals(vr.vString))
				return true;
		}
		return false;
	}

	//returns alphas of rules (where rule is in form Valpha)
	public List<String> getAlphas(VR vr){
		List<String> alphaStrings=new ArrayList<String>();
		for(String s:vr.rList) {
			if(s.substring(0, 1).equals(vr.vString))
				alphaStrings.add(s.substring(1,s.length()));
		}
		return alphaStrings;
	}

	//returns betas of rules (where betas don't start with the V)
	public List<String> getBetas(VR vr){
		List<String> betaStrings=new ArrayList<String>();
		for(String s:vr.rList) {
			if(!s.substring(0, 1).equals(vr.vString))
				betaStrings.add(s);
		}
		return betaStrings;
	}

	//replaces occurences of V in LHS of any rule of any Variable after V
	public void replaceLHSOccurrences(String vString, List<String> rList, int vIndex) {
		for(int j=0;j<this.vrList.size();j++) {
			VR vr=this.vrList.get(j);
			//if variable not the vString or its apostrophe then replace accordingly
			if(!vr.vString.equals(vString) && !vr.vString.equals(vString+"'") && j>vIndex && vr.vString.length()!=2) {
				int size=vr.rList.size();
				//new rList for this vr
				List<String> newRList=new ArrayList<String>();
				//loop over rules
				for(int i=0;i<size;i++) {
					String currentRuleString= vr.rList.get(i);
					//if rule starts with vString then replace rule with permutations of replacing vString with rList in the rule
					if(currentRuleString.substring(0, 1).equals(vString)) {
						for(String toConact: rList) {
							if(!newRList.contains(toConact+ currentRuleString.subSequence(1, currentRuleString.length())))
								newRList.add(toConact+ currentRuleString.subSequence(1, currentRuleString.length()));
						}
					}
					//if doesn't start with vString add to newRList normally
					else {
						if(!newRList.contains(currentRuleString))
							newRList.add(currentRuleString);
					}
				}
				vr.rList.clear();
				vr.rList.addAll(newRList);
			}
		}
	}

	//-------------------------------------------Classes------------------------------------------------

	class VR{

		String vString;
		List<String> rList=new ArrayList<String>();

		//Default constructor
		public VR() {

		}

		//constructor
		public VR(String vString, List<String> rList) {
			this.vString=vString;
			this.rList.addAll(rList);
		}

		public String toString() {
			String rulesString="";
			for(int i=0;i<rList.size();i++) {
				if(i==0)
					rulesString+=rList.get(0);
				else 
					rulesString+=","+rList.get(i);
			}
			return rulesString;
		}
	}
}
