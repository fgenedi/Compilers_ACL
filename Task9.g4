/**
 * Write your info here
 *
 * @name Farida El Genedi
 * @id 46-2291
 * @labNumber 10
 */

grammar Task9;

@members {
	/**
	 * Compares two integer numbers
	 *
	 * @param x the first number to compare
	 * @param y the second number to compare
	 * @return 1 if x is equal to y, and 0 otherwise
	 */
	public static int equals(int x, int y) {
	    return x == y ? 1 : 0;
	}
}

s returns [int check]:

a c[0,1,$a.n2,$a.n3] b {$check= $c.slf * $c.suf * equals($a.n,$b.nY);};

a returns [int n, int n2, int n3]: 'a' a1=a {$n = $a1.n + 1; $n2 = $a1.n2 * 2; $n3 = $a1.n3 * 3;}
| {$n = 0; $n2 = 1; $n3 = 1;};

b returns [int nY]: 'b' b1=b {$nY = $b1.nY + 1;}
| {$nY = 0;};

c [int ilf,int iuf,int l, int u] returns [int m, int slf, int suf]: 'c' c1=c[$ilf,$iuf,$l,$u] {$m = $c1.m + 1;$slf = $c1.slf + equals($l,$m);$suf = $c1.suf - equals($u,$c1.m);}
| {$m = 0;$slf = $ilf;$suf = $iuf;};

WS: [ \r\t\n]+ -> skip;