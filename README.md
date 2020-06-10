## __Algorithms__

> Advanced implementation and theory of algorithms. Topics may include: algorithm analysis; algorithm design techniques; and computational complexity.
---
### __Walker_PA1 - _Gale-Shapley Matching Algorithm___

A program that implements the _Gale-Shapley algorithm_ for Stable Marriages. The naming scheme of man and woman is for simplification purposes only. First, the program reads the command line (shown below) to determine which version of the Gale-Shapley algorithm to run. The -w and -m in the command (choose one or the other) stand for women and men respectively, where the algorithm is ran based on either the preferences of the men or the women. The program then parses an input JSON file, which contains a list of males and females and their respective preference lists. The matching algorithm is then ran and the matches are written to an output JSON file.

#### The Gale-Shapley Algorithm works as follows:

Processing the Gale-Shapley algorithm for men, the idea is to iterate through all free men while there is any free man available. Every free man goes to all women in his preference list according to the order. For every woman he goes to, he checks if the woman is free, if yes, then both become engaged. If the woman is not free, then the woman chooses to either say no to the proposing man  or dump her current engagement according to her preference list. So an engagement done once can be broken if a women gets a better option. Processing the Gale-Shapley algorithm for women is similar to the process for men, except the preference arrays are swapped. The algorithm inherently carries a preference for proposing gender (group) in the matching process.

##### Run with the command: java -jar Walker_PA1.jar -w,-m InputFile.json -o OutputFile.json - while in the Walker_PA1 folder.
---
### __Walker_PA2 - _Dijkstra's Shortest Path (Road) Algorithm (Distance)___

A program that implements _Dijkstra's shortest path algorithm_ that parses an input roads.txt file and finds the shortest path (and calculates the total distance) between two user-input cities.

##### Run with the command: java -jar Walker_PA2.jar ./roads.txt - while in the Walker_PA2 folder.
---
Walker_PA3 - Dijkstra's Shortest Path (Road) Algorithm (Speed & Time)
A program that extends Walker_PA2 (the implementation of Dijkstra's shortest path algorithm) that finds the fastest route between two user-input cities and calculates the total distance (miles), time spent(hours) as well as speed (miles/hour) on each road section.

##### Run with the command: java -jar Walker_PA3.jar ./roads.txt - while in the Walker_PA3 folder.
---
### __Walker_PA4 - _Pattern Matching Program (Brute Force, Rabin-Karp Algorithm___
A program that implements a brute force method and the Rabin-Karp algorithm to find a string pattern, either in a simple string or an input file. The program first asks the user if they would like to search a simple string or a file. If the user wants to search a simple string (phrase) [indicated by typing the letter s], they would simply write the input string and the pattern they want to find. If the user wants to search a text file [indicated by typing the letter f], they would simply write the name of the input text file (that is in the Walker_PA4 folder) and the pattern they want to find. In both instances, the user is presented with the number of matches through the Brute Force method and the Rabin-Karp algorithm (the same value), as well as the time each method/algorithm took too complete the search. The user is finally prompted to search again or quit [with the letter q].

___The Naïve String Matching Algorithm (Brute Force method)___: this algorithm looks at each substring that is of equal length to the pattern we want to find, "sliding" the pattern's length until the pattern is/is not found in the phrase or document.

___The Rabin-Karp Algorithm___: this algorithm also slides the pattern one by one, but unlike the naïve algorithm, the Rabin-Karp algorithm matches the hash-value of the pattern with the hash value of the current substring of text, and if the hash values match then, and only then does it start matching individual characters thereafter.

Included is a README pdf document that provides a complexity analysis of both the Brute Force (Naïve Pattern Searching) and Rabin-Karp Algorithm. The document also includes a graph comparison of time required to find certain words in simple phrases, as well as in lengthy text files.

##### Run with the command: java -jar Walker_PA4.jar - while in the Walker_PA4 folder.
---
## __Walker_Rivera_PA5 - _Traveling Salesman Problem (Draw the Path!)___

A program that implements the _Nearest Neighbor Algorithm_ to solve the Traveling Salesman Problem. Given a set of cities (indicated by a set of coordinates), this program calculates the distance between each pair of cities, and finds the shortest possible route that visits every city exactly once and returns back to the starting city.

This program first asks the user for the pathname of the input .csv file that will contain a number of cities (indicated by an x and y coordinate). Then, the user is asked to enter the pathname of the .svg file to output the results of the algorithm. The input file is parsed, the distances between cities are calculated and the algorithm is run. The user is then tasked with entering the starting city (number) out of the total number of cities where they would like to start. The output svg file is then written inside the desired folder, drawing the shortest path from the input start city, to all the other cities (exactly once), and back to the starting city. The user is finally asked if they would like to find another path (with another input file and starting city).

##### Run with the command: java -jar Rivera_Walker_PA5.jar - while in the Walker_PA5/dist folder
