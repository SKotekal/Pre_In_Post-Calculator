import java.util.*;
import java.text.*;
import java.io.*;

public class Calculator
{
	public static class Node
	{
		public String n = "";
		public Node l;
		public Node r;
		
		public Node(String no)
		{
			n = no;
		}
		public Node(String no, Node le, Node ri)
		{
			n = no;
			l = le;
			r = ri;
		}
		public String toString()
		{
			String ret = "ROOT: " + n;
			if(l != null)
			{
				ret +=  " LEFT: (" + l + ")";
			}
			if(r != null)
			{
				ret += " RIGHT: (" + r + ")";
			}
			return ret;
		}
	}
	private static String[] prec = {" ^ ", " * / ", " + - "};
	private static boolean[] v;
	public static void main(String[] args) throws IOException
	{
		System.out.println("Subhodh Kotekal 2015\nInput Expression => Output: Infix, Prefix, Postfix Conversions & Evaluation\nEvery character must be del"
				+ "imited by a single space. Negative numbers must be denoted as 0 - n.\n\n");
		Scanner scan = new Scanner(System.in);
		String input = "";
		input = scan.nextLine();
		input.trim();
		while(!input.equals("DONE"))
		{
			//System.out.println(input.charAt(input.length()-1));
			//infix to prefix
			if((input.charAt(0) != '+' && input.charAt(0) != '/' && input.charAt(0) != '*' && input.charAt(0) != '-' && input.charAt(0) != '^') 
					&& (input.charAt(input.length()-1) != '+' && input.charAt(input.length()-1) != '/' && input.charAt(input.length()-1) != '*' && input.charAt(input.length()-1) != '-' && input.charAt(input.length()-1) != '^'))
			{
				input = inpre(input);
			}
			//System.out.println(input);
			v = new boolean[input.length()/2+1];
			Node tree = null;
			if(input.charAt(0) == '+' || input.charAt(0) == '/' || input.charAt(0) == '*' || input.charAt(0) == '-' || input.charAt(0) == '^')
			{
				tree = preassemble(input, 0);
			}
			else
			{
				tree = postassemble(input, v.length-1);
			}
			System.out.println(in(tree));
			System.out.println(pre(tree));
			System.out.println(post(tree));
			System.out.println(eval(tree));
			System.out.println();
			input = scan.nextLine();
			input.trim();
		}

	}
	public static String inpre(String exp)
	{
		//Get rid of parentheses as they mess up manipulation
		if(exp.charAt(0) == '(' && exp.charAt(exp.length()-1) == ')')
		{
			exp = exp.substring(2, exp.length()-2); //Remove padded spaces "(_ ... _)"
		}
		//If already in prefix notation, don't need to do anything
		if(exp.charAt(0) == '+' || exp.charAt(0) == '/' || exp.charAt(0) == '*' || exp.charAt(0) == '-' || exp.charAt(0) == '^')
		{
			return exp;
		}
		
		String ret = "";
		String[] parts = exp.split(" ");
		
		//Puts together parentheses into one "block" (workable unit)
		ArrayList<String> arr = new ArrayList<String>();
 		for(int i = 0; i < parts.length; i++)
		{
			if(parts[i].contains("("))
			{
				String a = parts[i];
				int counter = 1;
				for(int j = i+1; j < parts.length; j++)
				{
					for(int k = 0; k < parts[j].length(); k++)
					{
						if(parts[j].charAt(k) == '(')
						{
							counter++;
						}
						else if(parts[j].charAt(k) == ')')
						{
							counter--;
						}
					}
					a += " " + parts[j];
					parts[j] = "!";
					if(counter == 0)
					{
						break;
					}
				}
				arr.add(a);
			}
			else if(!parts[i].equals("!"))
			{
				arr.add(parts[i]);
			}
		}
		parts = new String[arr.size()];
		for(int i = 0; i < arr.size(); i++)
		{
			parts[i] = arr.get(i);
		}
		
		//Changes from infix to prefix
		boolean[] visited = new boolean[parts.length];
		for(int j = 0; j < prec.length; j++) //Ensures precedence of operators
		{
			boolean done = false;
			while(!done)
			{
				done = true;
				for(int i = 0; i < parts.length; i++) //Go through all terms
				{
					if(prec[j].contains(parts[i]))
					{
						//Switch order of elements
						String temp = parts[i] + " " + inpre(parts[i-1]) + " " + inpre(parts[i+1]);
						ret = temp;
						
						//Update term list and mark as visited
						parts[i] = temp;
						parts[i-1] = temp;
						parts[i+1] = temp;
						visited[i] = true;
						visited[i+1] = true;
						visited[i-1] = true;
						done = false;
					}
					else if(!visited[i]) //If this term hasn't been seem and is not an operator, then it is compounded operator
					{
						ret = parts[i];
					}
				}
			}
		}
		
		return ret;
	}
	public static Node preassemble(String exp, int pos)
	{
		String[] parts = exp.split(" ");
		
		//Add new node of current element
		Node ret = new Node(parts[pos]);
		v[pos] = true;
		
		//If an operator, then need to expand tree. 
		if(parts[pos].equals("+") || parts[pos].equals("-") || parts[pos].equals("*")|| parts[pos].equals("/") || parts[pos].equals("^"))
		{
			//Search for unused terms
			for(int i = 0; i < v.length; i++)
			{
				if(!v[i])
				{
					pos = i;
					break;
				}
			}
			ret.l = preassemble(exp, pos);
			
			//Search for unused terms
			for(int i = 0; i < v.length; i++)
			{
				if(!v[i])
				{
					pos = i;
					break;
				}
			}
			ret.r = preassemble(exp, pos);
		}
		
		//Return node
		return ret;
	}
	
	public static Node postassemble(String exp, int pos)
	{
		String[] parts = exp.split(" ");
		
		//Add new node of current element
		Node ret = new Node(parts[pos]);
		v[pos] = true;
		
		//If an operator, then need to expand tree. 
		if(parts[pos].equals("+") || parts[pos].equals("-") || parts[pos].equals("*")|| parts[pos].equals("/") || parts[pos].equals("^"))
		{
			//Search for unused terms
			for(int i = v.length-1; i >= 0; i--)
			{
				if(!v[i])
				{
					pos = i;
					break;
				}
			}
			ret.r = postassemble(exp, pos);
			
			//Search for unused terms
			for(int i = v.length-1; i >= 0; i--)
			{
				if(!v[i])
				{
					pos = i;
					break;
				}
			}
			ret.l = postassemble(exp, pos);
		}
		
		//Return node
		return ret;
	}
	public static String in(Node nod)
	{
		if(nod.l == null && nod.r == null)
		{
			return nod.n;
		}
		else if(nod.l == null && nod.r != null)
		{
			return "(" + nod.n + " " + in(nod.r) + ")";
		}
		else if(nod.l != null && nod.r == null)
		{
			return "(" + in(nod.l) +  " " + nod.n + " )";
		}
		return "(" + in(nod.l) + " " + nod.n + " " + in(nod.r) + ")";
	}
	public static String pre(Node nod)
	{
		if(nod.l == null && nod.r == null)
		{
			return nod.n;
		}
		else if(nod.l == null && nod.r != null)
		{
			return nod.n + " " + pre(nod.r);
		}
		else if(nod.l != null && nod.r == null)
		{
			return nod.n + " " + pre(nod.l);
		}
		return nod.n + " " + pre(nod.l) + " " + pre(nod.r);
	}
	public static String post(Node nod)
	{
		if(nod.l == null && nod.r == null)
		{
			return nod.n;
		}
		else if(nod.l == null && nod.r != null)
		{
			return post(nod.r) + " " + nod.n;
		}
		else if(nod.l != null && nod.r == null)
		{
			return post(nod.l) + " " + nod.n;
		}
		return post(nod.l) + " " + post(nod.r) + " " + nod.n;
	}
	public static double eval(Node nod)
	{
		if(nod.l == null && nod.r == null)
		{
			return Double.parseDouble(nod.n);
		}
		if(nod.n.equals("+"))
		{
			return eval(nod.l) + eval(nod.r);
		}
		if(nod.n.equals("-"))
		{
			return eval(nod.l) - eval(nod.r);
		}
		if(nod.n.equals("*"))
		{
			return eval(nod.l) * eval(nod.r);
		}
		if(nod.n.equals("/"))
		{
			return eval(nod.l) / eval(nod.r);
		}
		return Math.pow(eval(nod.l), eval(nod.r));
	}
}
