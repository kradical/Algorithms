/*  MWST.java
	Konrad Schultz
	00761540
	v0.1.0

    ***NOTE***
   		I am not the original author of this file, I would also like to reference
   		the Union-Find data structure I pulled from http://algs4.cs.princeton.edu/43mst/UF.java.html
   		I heavily followed its implementation of disjoint sets!
    ***NOTE***

   CSC 226 - Spring 2015
   Assignment 1 - Minimum Weight Spanning Tree Template
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java MWST
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java MWST file.txt
   where file.txt is replaced by the name of the text file.
   
   The input consists of a series of graphs in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry A[i][j] of the adjacency matrix gives the weight of the edge from 
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that A[i][j]
   is always equal to A[j][i].
	
   An input file can contain an unlimited number of graphs; each will be 
   processed separately.*/

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.util.PriorityQueue;

/*Do not change the name of the MWST class*/

public class MWST{

	/* mwst(G)
		Given an adjacency matrix for graph G, return the total weight
		of all edges in a minimum weight spanning tree.
		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/

	public static class Edge implements Comparable<Edge>{
		private int weight;
		private int vertex1;
		private int vertex2;
		public Edge(int initWeight, int initVertex1, int initVertex2){
			weight = initWeight;
			vertex1 = initVertex1;
			vertex2 = initVertex2;
		}
		public int getVertex1(){
			return vertex1;
		}
		public int getVertex2(){
			return vertex2;
		}
		public int getWeight(){
			return weight;
		}
		public void printEdge(){
			System.out.println("EDGE ("+this.vertex1+", "+this.vertex2+")WEIGHT: "+this.weight);
		}

		public int compareTo(Edge compareEdge){
			int compareQuantity = compareEdge.weight;
			return this.weight - compareQuantity;
		}
	}

	public static class Disjoint{
		private int[] id;     // id[i] = parent of i
		private byte[] rank;  // rank[i] = rank of subtree rooted at i (cannot be more than 31)
		private int count;    // number of components

		public Disjoint(int N) {
			if (N < 0) throw new IllegalArgumentException();
			count = N;
			id = new int[N];
			rank = new byte[N];
			for (int i = 0; i < N; i++) {
				id[i] = i;
				rank[i] = 0;
			}
		}

		public int find(int p) {
			if (p < 0 || p >= id.length) throw new IndexOutOfBoundsException();
			while (p != id[p]) {
				id[p] = id[id[p]];    // path compression by halving
				p = id[p];
			}
			return p;
		}

		public boolean connected(int p, int q) {
			return find(p) == find(q);
		}

		public void union(int p, int q) {
			int i = find(p);
			int j = find(q);
			if (i == j) return;

			// make root of smaller rank point to root of larger rank
			if      (rank[i] < rank[j]) id[i] = j;
			else if (rank[i] > rank[j]) id[j] = i;
			else {
				id[j] = i;
				rank[i]++;
			}
			count--;
		}
	}

	static int MWST(int[][] G){
		int numVerts = G.length;

		/* Find a minimum weight spanning tree by Kruskal's algorithm
		 (You may add extra functions if necessary)
		 ... Your code here ... */

		PriorityQueue<Edge> pq = new PriorityQueue<Edge>();

		for(int i = 0; i < numVerts; i++ ) {
			for (int j = i + 1; j < numVerts; j++) {
				if (G[i][j] > 0) {
					Edge tempEdge = new Edge(G[i][j], i, j);
					pq.add(tempEdge);
				}
			}
		}
		/* Add the weight of each edge in the minimum weight spanning tree
		   to totalWeight, which will store the total weight of the tree.
		*/

		int totalWeight = 0;

		/* ... Your code here ... */
		Disjoint tempSet = new Disjoint(numVerts);
		while(!pq.isEmpty()) {
			Edge tempEdge = pq.remove();
			if(!tempSet.connected(tempEdge.getVertex1(), tempEdge.getVertex2())){
				tempSet.union(tempEdge.getVertex1(), tempEdge.getVertex2());
				totalWeight += tempEdge.getWeight();
			}
		}
		return totalWeight;
		
	}

	/* main()
	   Contains code to test the MWST function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int graphNum = 0;
		double totalTimeSeconds = 0;
		
		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();
			
			int totalWeight = MWST(G);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			
			System.out.printf("Graph %d: Total weight is %d\n",graphNum,totalWeight);
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}