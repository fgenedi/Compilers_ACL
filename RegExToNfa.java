package csen1002.main.task1;

import java.util.Stack;
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

public class RegExToNfa {

	int startState=0;
	ArrayList<Integer> acceptStates=new ArrayList<Integer>();
	int currentState=0;
	Stack<NFA> stack=new Stack<NFA>();
	List<String> states=new ArrayList<String>();
	String alph="";
	NFA finalNFA;

	public RegExToNfa(String input) {
		//parsing the input to alphabet and regular expression
		String[] seperateAlphRegEx=input.split("#");
		alph=seperateAlphRegEx[0];
		List<String> regEx=Arrays.asList(seperateAlphRegEx[1].split(""));    
		//List<String> alphabet=Arrays.asList(seperateAlphRegEx[0].split(";"));

		//construct nfa(s) of regEx
		for(String s : regEx) {
			//add literals to stack as nfa and updating states and acceptStates
			if(!(s.equals("|") || s.equals(".") || s.equals("*"))) {
				NFAcomponent nfaComp=new NFAcomponent(currentState, currentState+1, s);
				states.add(currentState+"");
				states.add((currentState+1)+"");
				acceptStates.add(currentState+1);
				NFA nfa= new NFA(currentState, currentState+1);
				nfa.addComponent(nfaComp);
				stack.push(nfa);	
				currentState+=2;
			}
			else {
				nfaConstructor(s);
			}
		}

		finalNFA=stack.pop();
	}

	public void nfaConstructor(String operator) {

		switch(operator) {
		case ("*"):
			nfaStar();
		break;
		case("|"):
			nfaOr();
		break;
		case("."):
			nfaConcat();
		break;
		default: break;

		}
	}

	public void nfaStar() {
		//get current nfa
		NFA nfa=stack.pop();

		//get current start and accept states
		int currentStartState=nfa.startState;
		int currentAcceptState=nfa.acceptState;

		//add 4 new transitions to the current nfa
		nfa.NFAcomponents.add(new NFAcomponent(currentAcceptState, currentStartState, "e"));
		nfa.NFAcomponents.add(new NFAcomponent(currentAcceptState, currentState+1, "e"));
		nfa.NFAcomponents.add(new NFAcomponent(currentState, currentStartState, "e"));
		nfa.NFAcomponents.add(new NFAcomponent(currentState, currentState+1, "e"));

		//update states with the 2 new added states
		states.add(currentState+"");
		states.add(currentState+1+"");

		//update accept states with removing old accept state and adding new
		acceptStates.remove(Integer.valueOf(currentAcceptState));
		acceptStates.add(currentState+1);

		//update start state
		startState=currentState;

		//update nfa start and accept states
		nfa.startState=currentState;
		nfa.acceptState=currentState+1;

		//update current state
		currentState+=2;

		//push the new nfa in the stack after * operator has been applied
		stack.push(nfa);
	}

	public void nfaOr() {
		//get two NFAs to apply |
		NFA nfa2=stack.pop();
		NFA nfa1=stack.pop();

		//new NFA to be pushed
		NFA nfa=new NFA();

		//start and accept states of poped NFAs
		int start1=nfa1.startState;
		int start2=nfa2.startState;
		int accept1=nfa1.acceptState;
		int accept2=nfa2.acceptState;

		//start and accept states for new NFA after or
		nfa.startState=currentState;
		nfa.acceptState=currentState+1;

		//overall start and accept states adjustment
		startState=currentState;
		acceptStates.remove(Integer.valueOf(accept1));
		acceptStates.remove(Integer.valueOf(accept2));
		acceptStates.add(currentState+1);

		//add new states to state list
		states.add(currentState+"");
		states.add(currentState+1+"");

		//add NFA components in poped NFAs to new NFA as nothing is removed
		nfa.NFAcomponents.addAll(nfa1.NFAcomponents);
		nfa.NFAcomponents.addAll(nfa2.NFAcomponents);

		//add new transitions as NFA components to the new NFA
		nfa.addComponent(new NFAcomponent(currentState, start1, "e"));
		nfa.addComponent(new NFAcomponent(currentState, start2, "e"));
		nfa.addComponent(new NFAcomponent(accept1, currentState+1, "e"));
		nfa.addComponent(new NFAcomponent(accept2, currentState+1, "e"));

		//update current state
		currentState+=2;

		//push the new nfa in the stack after | operator has been applied
		stack.push(nfa);
	}

	public void nfaConcat() {
		//get two NFAs to apply .
		NFA nfa2=stack.pop();
		NFA nfa1=stack.pop();

		//new NFA to be pushed
		NFA nfa=new NFA();

		//start and accept states of poped NFAs
		int start1=nfa1.startState;
		int start2=nfa2.startState;
		int accept1=nfa1.acceptState;
		int accept2=nfa2.acceptState;

		//new nfa start and accept states
		nfa.startState=start1;
		nfa.acceptState=accept2;


		//add old NFAcomponents to new nfa without removed ones 
		nfa.NFAcomponents.addAll(nfa1.NFAcomponents);

		//add new transitions from nfa1's accept to accept of nfa2's transitions from its start state as it is removed
		int j=0;
		for(int i=0; i<nfa2.NFAcomponents.size();i++) {
			NFAcomponent n=nfa2.NFAcomponents.get(j);
			if(n.startState==start2) {
				nfa.addComponent(new NFAcomponent(accept1,n.acceptState,n.literal));
			}
			else
				nfa.NFAcomponents.add(n);
			j++;
		}

		//update global variables
		acceptStates.remove(Integer.valueOf(accept1));
		startState=start1;
		states.remove(start2+"");

		//push the new nfa in the stack after . operator has been applied
		stack.push(nfa);
	}

	public String toString() {

		//get string of states
		String Q="";
		if(!states.isEmpty()) {
			Q+=states.remove(0);
			for(String s : states) {
				Q+=";"+s;
			}
		}

		//alphabet
		String A=alph;

		//transition functions
		String T="";
		Collections.sort(finalNFA.NFAcomponents);
		for(NFAcomponent n : finalNFA.NFAcomponents) {
			if(!T.equals(""))
				T+=";"+n.toString();
			else
				T+=n.toString();
		}
		//Initial State
		String I=startState+"";

		//Accept States
		String F="";
		if(!acceptStates.isEmpty()) {
			F+=acceptStates.remove(0);
			for(Integer s : acceptStates) {
				F+=";"+s;
			}
		}

		//Q#A#T#I#F.
		return Q+"#"+A+"#"+T+"#"+I+"#"+F;
	}

	public static void main(String[]args) {
		RegExToNfa r=new RegExToNfa("a;o;z#za|*o.");
		//String outputS=r.toString();
		//String e="0;1;2;3;4;5;6;7;9#a;o;z#0,z,1;1,e,5;2,a,3;3,e,5;4,e,0;4,e,2;5,e,4;5,e,7;6,e,4;6,e,7;7,o,9#6#9";
		//System.out.print(r);	
	}
	class NFA{

		int startState;
		int acceptState;
		List<NFAcomponent> NFAcomponents=new ArrayList<NFAcomponent>();

		public NFA() {

		}

		public NFA(int startState, int acceptState) {
			this.startState=startState;
			this.acceptState=acceptState;
		}


		public void addComponent(NFAcomponent newComponent) {
			NFAcomponents.add(newComponent);	
		}

		public void removeComponent(NFAcomponent newComponent) {
			NFAcomponents.remove(newComponent);	
		}

	}
	class NFAcomponent implements Comparable<Object>{

		int startState;
		int acceptState;
		String literal;

		public NFAcomponent(int startState, int acceptState, String literal) {
			this.startState=startState;
			this.acceptState=acceptState;
			this.literal=literal;
		}

		public NFAcomponent() {

		}

		public String toString() {
			return this.startState+","+this.literal+","+this.acceptState;
		}

		@Override
		//return -ve if invoked on smaller than given
		public int compareTo(Object o) {
			NFAcomponent nfa=(NFAcomponent)o;

			if(this.startState<nfa.startState)
				return -1;
			if(this.startState>nfa.startState)
				return 1;

			int c=this.literal.compareTo(nfa.literal);
			if(c!=0)
				return c;

			if(this.acceptState<nfa.acceptState)
				return -1;
			if(this.acceptState>nfa.acceptState)
				return 1;
			//equal --not possible el mafrood
			return 0;
		}

	}

}
