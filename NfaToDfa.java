package csen1002.main.task2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

/**
 * Write your info here
 * 
 * @name Farida Ayman El Genedi
 * @id 46-2291
 * @labNumber 10
 */

public class NfaToDfa {

	String alphabet;
	State initialState;
	List<String> givenAcceptStates=new ArrayList<String>();
	List<String> dfaStates=new ArrayList<String>();
	List<String> transitions=new ArrayList<String>();

	public NfaToDfa(String input) {
		//Q#A#T#I#F.
		String[] inputParsed = input.split("#");
		//Q
		String[] givenStates = inputParsed[0].split(";");
		//A
		String givenAlphabet = inputParsed[1];
		alphabet = givenAlphabet;
		//T
		String[] givenTransitions = inputParsed[2].split(";");
		//I
		String givenInitialState = inputParsed[3];
		//F
		String[] givenAcceptStates = inputParsed[4].split(";");
		this.givenAcceptStates=Arrays.asList(givenAcceptStates);

		//Our list of NFA states
		List<State> nfastates=new ArrayList<State>();
		//find the direct epsilon closure of each state
		for(String s: givenStates) {
			State state=new State(Integer.parseInt(s), new LinkedHashSet<Integer>());
			state.epsilonClosure.addAll(state.findDirectEpsilonClosure(state.stateNumber, givenTransitions));
			nfastates.add(state);	

		}
		Collections.sort(nfastates);

		//sort the EC of each state
		for(State s: nfastates) {
			List<Integer> array = new ArrayList<Integer>();		  
			array.addAll(s.epsilonClosure);
			Collections.sort(array);
			s.epsilonClosure.clear();
			s.epsilonClosure.addAll(array);
			//set the initial state of DFA to EC of the initial state in NFA and add to dfaStates
			if(((s.stateNumber)+"").equals(givenInitialState)) {
				initialState=s;
				dfaStates.add(s.toString());
			}
		}

		TransitionHandler(nfastates, givenTransitions);
	}

	public void TransitionHandler(List<State> nfaStates, String[] transitions) {
		//available literals; we want each state to have a transition for each literal
		String[] alphabetStrings=alphabet.split(";");
		//loop over states
		int stateSize=dfaStates.size();
		int j=0;
		while(j<stateSize) {
			//split into statecomponents like 0/1 becomes 0 and 1
			List<String> stateComponents=Arrays.asList(this.dfaStates.get(j).split("/"));
			//loop over the alphabet to have a transition for each literal
			for(int i=0;i<alphabetStrings.length;i++) {
				String currentAlphabet = alphabetStrings[i];
				//stores transitions we find for the current alphabet for the current state 
				LinkedHashSet<Integer> alphabetTrans=new LinkedHashSet<Integer>();	
				//find transitions for literal from each state component and store in alphabetTrans 
				for(String stateComponent: stateComponents) {
					//loop over transitions to see if we already have (if not we handle dead state ta7t)
					for(String transCurrent: transitions) {
						String[] transitionComponents = transCurrent.split(",");
						if(transitionComponents[0].equals(stateComponent) && transitionComponents[1].equals(currentAlphabet)) {
							if(!alphabetTrans.contains(Integer.parseInt(transitionComponents[2])))
								alphabetTrans.add(Integer.parseInt(transitionComponents[2]));
						}
					}
				}
				//dead state
				if(alphabetTrans.isEmpty()) {
					alphabetTrans.add(-1);
					if(!this.dfaStates.contains("-1")) {
						this.dfaStates.add("-1");
						stateSize++;
					}
					this.transitions.add(this.dfaStates.get(j)+","+currentAlphabet+","+"-1");
				}
				else {
					//sort the transitions
					List<Integer> array = new ArrayList<Integer>();		  
					array.addAll(alphabetTrans);
					Collections.sort(array);
					alphabetTrans.clear();
					alphabetTrans.addAll(array);

					//add transitions with the current state, alphabet, alphabetTrans[] ec
					//add states 
					LinkedHashSet<Integer> ec=new LinkedHashSet<Integer>();
					for(Integer alphabetTransComponent: alphabetTrans) {
						//get ec of state hamro7laha from our DFA state with the current alphabet
						for(State stateTany: nfaStates) {
							if(stateTany.stateNumber==alphabetTransComponent) {
								ec.addAll(stateTany.epsilonClosure);
							}
						}
					}
					//sort the ec
					List<Integer> array2 = new ArrayList<Integer>();		  
					array2.addAll(ec);
					Collections.sort(array2);
					ec.clear();
					ec.addAll(array2);
					//add the transition
					State tempState=new State(0, ec);
					this.transitions.add(this.dfaStates.get(j)+","+currentAlphabet+","+tempState.toString());
					//add state if doesn't exist
					if(!this.dfaStates.contains(tempState.toString())) {
						dfaStates.add(tempState.toString());
						stateSize++;
					}
				}
			}
			j++;
		}
	}

	@Override
	public String toString() {
		//Q#A#T#I#F

		//Q: a set of states separated by ; and in each state the states separates by /
		String Q="";
		dfaStates=SortStates(dfaStates);
		if(!dfaStates.isEmpty())
			Q+=dfaStates.get(0);
		for(int i=1;i<dfaStates.size();i++) {
			Q+=";"+dfaStates.get(i);
		}

		//A
		String A= alphabet;

		//T: transitions in form of i, a,j separated by ; and sorted
		String T="";
		SortTransitions();
		if(!transitions.isEmpty()) {
			T+=transitions.remove(0);
		}
		for(String s: transitions) {
			T+=";"+s;
		}

		//I
		String I="";
		LinkedHashSet<Integer> initialStateEC=new LinkedHashSet<Integer>();		
		initialStateEC.addAll(initialState.epsilonClosure);
		for(Integer i: initialStateEC) {
			if(I.equals(""))
				I=i+"";
			else 
				I+="/"+i;							
		}

		//F : accept states separated by ; (ay state feha given accept state(s))
		String F="";
		givenAcceptStates=SortStates(givenAcceptStates);
		List<String> dfaAcceptStateStrings=new ArrayList<String>();
		for(String s: givenAcceptStates) {
			for(String state: dfaStates) {
				List<String> stateCompList=Arrays.asList(state.split("/"));
				if(stateCompList.contains(s) && !dfaAcceptStateStrings.contains(state)) {
					dfaAcceptStateStrings.add(state);
				}
			}
		}
		dfaAcceptStateStrings=SortStates(dfaAcceptStateStrings);
		if(!dfaAcceptStateStrings.isEmpty()) {
			F+=dfaAcceptStateStrings.remove(0);
			for(String s: dfaAcceptStateStrings) {
				F+=";"+s;
			}
		}

		return Q+"#"+A+"#"+T+"#"+I+"#"+F;
	}

	public void SortTransitions() {
		//swap based on i,a,j
		boolean swapped;
		boolean swapByI;
		boolean swapByA;
		boolean swapByJ;
		for (int i = 0; i < transitions.size() - 1; i++) {
			swapped = false;
			for (int j = 0; j < transitions.size() - 1 - i; j++) {
				swapByI=false;
				swapByA=false;
				swapByJ=false;
				String [] aStrings=(transitions.get(j)).split(",");
				String [] bStrings=(transitions.get(j+1)).split(",");
				//decide which part to sort with
				if(aStrings[0].equals(bStrings[0])) {
					if(aStrings[1].equals(bStrings[1]))
						swapByJ=true;
					else if(aStrings[2].equals(bStrings[2])) 
						swapByA=true;											
				}
				else 
					swapByI=true;
				//swap using i
				if(swapByI) {
					String [] x =(aStrings[0]).split("/");
					String [] y =(bStrings[0]).split("/");
					int xFirst = Integer.parseInt(x[0]);
					int yFirst = Integer.parseInt(y[0]);
					if (xFirst != yFirst) {
						if(xFirst>yFirst) {
							String temp = transitions.get(j);
							transitions.set(j, transitions.get(j + 1));
							transitions.set(j + 1, temp);
							swapped = true;
						}
					}
					else {
						int lenX=x.length;
						int lenY=y.length;
						int shorter = Math.min(lenX, lenY);
						boolean equalTillShortest=true;
						for (int k = 1; k < shorter; k++) {
							int xcomp = Integer.parseInt(x[k]);
							int ycomp = Integer.parseInt(y[k]);
							if(xcomp!=ycomp) {
								equalTillShortest=false;
								if(xcomp>ycomp) {
									String temp = transitions.get(j);
									transitions.set(j, transitions.get(j + 1));
									transitions.set(j + 1, temp);
									swapped = true;
								}
								break;
							}
						}
						if(equalTillShortest && lenX>lenY ) {
							String temp = transitions.get(j);
							transitions.set(j, transitions.get(j + 1));
							transitions.set(j + 1, temp);
							swapped = true;
						}
					}
				}
				//swap using the literal
				else if(swapByA) {
					String x =aStrings[1];
					String y =bStrings[1];
					if(x.compareTo(y)>0) {
						String temp = transitions.get(j);
						transitions.set(j, transitions.get(j + 1));
						transitions.set(j + 1, temp);
						swapped = true;
					}
				}
				//swap by j
				else if(swapByJ) {
					String [] x =(aStrings[2]).split("/");
					String [] y =(bStrings[2]).split("/");
					int xFirst = Integer.parseInt(x[0]);
					int yFirst = Integer.parseInt(y[0]);
					if (xFirst != yFirst) {
						if(xFirst>yFirst) {
							String temp = transitions.get(j);
							transitions.set(j, transitions.get(j + 1));
							transitions.set(j + 1, temp);
							swapped = true;
						}
					}
					else {
						int lenX=x.length;
						int lenY=y.length;
						int shorter = Math.min(lenX, lenY);
						boolean equalTillShortest=true;
						for (int k = 1; k < shorter; k++) {
							int xcomp = Integer.parseInt(x[k]);
							int ycomp = Integer.parseInt(y[k]);
							if(xcomp!=ycomp) {
								equalTillShortest=false;
								if(xcomp>ycomp) {
									String temp = transitions.get(j);
									transitions.set(j, transitions.get(j + 1));
									transitions.set(j + 1, temp);
									swapped = true;
								}
								break;
							}
						}
						if(equalTillShortest && lenX>lenY ) {
							String temp = transitions.get(j);
							transitions.set(j, transitions.get(j + 1));
							transitions.set(j + 1, temp);
							swapped = true;
						}
					}
				}
			}
			if (!swapped) {
				break;
			}

		}
	}

	public List<String> SortStates(List<String> givenStatesList){
		boolean swapped;
		for (int i = 0; i < givenStatesList.size() - 1; i++) {
			swapped = false;
			for (int j = 0; j < givenStatesList.size() - 1 - i; j++) {
				String [] x =(givenStatesList.get(j)).split("/");
				String [] y =(givenStatesList.get(j+1)).split("/");
				int xFirst = Integer.parseInt(x[0]);
				int yFirst = Integer.parseInt(y[0]);
				if (xFirst != yFirst) {
					if(xFirst>yFirst) {
						String temp = givenStatesList.get(j);
						givenStatesList.set(j, givenStatesList.get(j + 1));
						givenStatesList.set(j + 1, temp);
						swapped = true;
					}
				}
				else {
					int lenX=x.length;
					int lenY=y.length;
					int shorter = Math.min(lenX, lenY);
					boolean equalTillShortest=true;
					for (int k = 1; k < shorter; k++) {
						int xcomp = Integer.parseInt(x[k]);
						int ycomp = Integer.parseInt(y[k]);
						if(xcomp!=ycomp) {
							equalTillShortest=false;
							if(xcomp>ycomp) {
								String temp = givenStatesList.get(j);
								givenStatesList.set(j, givenStatesList.get(j + 1));
								givenStatesList.set(j + 1, temp);
								swapped = true;
							}
							break;
						}
					}
					if(equalTillShortest && lenX>lenY && !swapped) {
						String temp = givenStatesList.get(j);
						givenStatesList.set(j, givenStatesList.get(j + 1));
						givenStatesList.set(j + 1, temp);
						swapped = true;
					}
				}
			}		
			if (!swapped) 
				break;	
		}
		return givenStatesList;
	}

	public static void main(String[] args) {
		NfaToDfa nfaToDfa= new NfaToDfa("0;1;2;3;4;5;6;7;8;9#o;p;q#0,q,1;1,e,6;1,e,8;2,p,3;3,e,7;4,e,5;5,e,7;6,e,2;6,e,4;7,e,6;7,e,8;8,o,9#0#9");
		System.out.println(nfaToDfa);
	}
	
	//--------------------------------------------Classes-----------------------------------------------

	class State implements Comparable<Object>{

		int stateNumber;
		LinkedHashSet<Integer> epsilonClosure; //including state itself

		public State() {

		}

		public State(int state, LinkedHashSet<Integer> epsilonClosure) {

			this.stateNumber=state;
			this.epsilonClosure=epsilonClosure;
		}

		//finds the epsilon closure of state n given the transitions
		LinkedHashSet<Integer> findDirectEpsilonClosure(int state, String [] transitionsNFA) {
			LinkedHashSet<Integer> epsilonClosure=new LinkedHashSet<Integer>(); 
			List<Integer> visited=new ArrayList<Integer>();
			Stack<Integer> unprocessedStates=new Stack<>();
			unprocessedStates.add(state);
			while(!unprocessedStates.isEmpty()) {
				int currentState = (int) unprocessedStates.pop();
				epsilonClosure.add(currentState);
				//find reachable states from current state
				for (int i=0;i<transitionsNFA.length;i++) {
					String s=transitionsNFA[i];
					String[] transitionComponents = s.split(",");
					//check if this transition starts from our desired state
					if(transitionComponents[0].equals(""+currentState)) {
						//get literal in transition, if e add to epsilon closure of the state
						if(transitionComponents[1].equals("e") && !visited.contains(Integer.parseInt(transitionComponents[2]))) {
							unprocessedStates.push(Integer.parseInt(transitionComponents[2]));
							visited.add(Integer.parseInt(transitionComponents[2]));
						}
					}
				}		        
			}
			return epsilonClosure;
		}

		public int compareTo(Object o) {
			State s = (State) o;
			if(this.stateNumber<s.stateNumber)
				return -1;
			else if(this.stateNumber>s.stateNumber)
				return 1;
			return 0;
		}

		//returns ec formatted as i/j/k 
		public String toString() {
			String s="";
			Iterator<Integer> itr = this.epsilonClosure.iterator();
			if(itr.hasNext()) {
				s+=itr.next();
			}
			while (itr.hasNext())
				s+="/"+itr.next();

			return s;
		}
	}
	class Transition implements Comparable<Object>{
		int i;
		String a;
		int j;

		public Transition() {

		}

		public Transition (int i, String a, int j) {
			this.i=i;
			this.a=a;
			this.j=j;
		}

		@Override
		public int compareTo(Object o) {
			Transition transition=(Transition)o;
			if(this.i<transition.i)
				return -1;
			if(this.i>transition.i)
				return 1;
			int c=this.a.compareTo(transition.a);
			if(c!=0)
				return c;
			if(this.j<transition.j)
				return -1;
			if(this.j>transition.j)
				return 1;
			return 0;
		}
	}
}
