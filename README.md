# Minimum Vertex Cover
Branch &amp; bound, local search and approximation algorithms to solve minimum vertex cover problem. This is the solution for final project of **CSE 6140 Algorithm** at Gatech.

## Introduction

The Minimum Vertex cover (MVC) problem is a well- known NP-complete problem with numerous applications in computational biology, operations research, the routing and management of resources. In this project, we will treat the problem using different algorithms, evaluating their theoretical and experimental complexities on real world datasets.

## Problem Defination

![](https://github.com/xwave7/markDownPhotos/blob/master/mvc-def.png?raw=true =600x)

## Code Structure

```
folder/
	|---- code/
		|---- README-group_21.txt
		|---- main: RunAll.java
		|---- BnB: branchBound.java
		|---- Approx: Solution.java Graph.java
		|---- LS1: FactVC.java, Edge.java
		|---- LS2: SA.java, Edge.java
	|---- output/
		|---- Approx/
			|---- 11 solution files
			|---- 11 trace files
		|---- BnB/ (7/11 were able to finish)
			|---- 7 solution files
			|---- 7 trace files
		|---- LS1/
			for each graph:
			|---- 10 solution files for different seeds
			|---- 10 trace files for different seeds
		|---- LS2/
			|---- 10 solution files for different seeds
			|---- 10 trace files for different seeds
```

## Compile
- unzip our submitted file
- direct into the submitted folder/code
- javac *.java
- Please put Data folder in the folder


##Execution

We follow the format provided in the handouts, several examples for runs on power.graph:
NOTE: For BnB, we provided “-seed 500” for the formality, but in the algorithm, we didn’t use this parameter

```
java RunAll -inst power.graph -alg BnB -time 600 -seed 500
java RunAll -inst power.graph -alg Approx -time 600 -seed 500
java RunAll -inst power.graph -alg LS1 -time 600 -seed 500
java RunAll -inst power.graph -alg LS2 -time 600 -seed 500
```
