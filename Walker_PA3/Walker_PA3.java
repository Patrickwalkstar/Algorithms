import java.util.*;
import java.io.*;

/*

For this project we were to write a program that: reads and parses a provided roads.txt file
and is able to decide the shortest route between the two cities and calculate the total distance (by miles).
Walker_PA2 provides an implementation of Dijkstra's Algorithm that parses the input roads.txt file and finds
the shortest path between two user-input cities.

Walker_PA3 provides an extension of Walker_PA2 by not only providing the distance between cities, but also the speed
between cities and the time it will take to get from city to city. This program finds the fastest route
between two cities and calculate the total distance (miles) and time spent(hours) as well as speed (miles/hour)
on each road section.

Name: Patrick Walker
Last updated: 3/7/2019
Class: COMP 480 - Algorithms

*/

public class Walker_PA3 {

    //Global variables that can be accessed by all methods.
    public static String[] cities;
    public static int[][] mileage;
    public static boolean[] visited;
    public static int[] prev;
    public static int[] dist;
    public static int size;
    public static double[][] speeds;
    public static double basespeed;
    public static double throttledspeed;
    public static List<LinkedList<String>> paths;
    public static int totaldistances;
    public static double averagespeed;
    public static double totaltime;


    public static void main(String[] args) throws IOException {

        System.out.println("Walker's Shortest Road Finder:");

        File filename = new File("roads.txt");
        Scanner fin = new Scanner(filename);
        Scanner in = new Scanner(System.in);

        /*Read the roads.txt file to write a file of cities called cityFile.txt
        Although this method writes the cityFile, it also returns the number of
        cities so that the arrays below can be initialized.*/
        size = writeCityFile(filename);
        File filename2 = new File("cityFile.txt");
        Scanner cin = new Scanner(filename2);
        cities = new String[size];
        mileage = new int[size][size];
        visited = new boolean[size];
        prev = new int[size];
        dist = new int[size];
        speeds = new double[size][size];


        //Read all the cities in the cityFile and input them into an array (cities)
        for (int i = 0; i < cities.length && cin.hasNextLine(); i++) {
            cities[i] = cin.nextLine().toLowerCase();
        }

        String line = "";
        String city1;
        String city2;
        int distance;



        /*Populate the 2D mileage array with the maximum value.
        Individual mileages between cities in this 2D array will change later*/
        /*Then, populate the 2D speeds array with the maximum value.
        Individual speeds between cities in this 2D array will change later*/
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                mileage[i][j] = Integer.MAX_VALUE;
                speeds[i][j] = Integer.MAX_VALUE;
            }
        }

        //Initialize paths ArrayList to store the paths between cities (as linked lists).
        paths = new ArrayList<>();

        /*Parse the roads.txt file, assigning the distance between
          two cities to the their index in the mileage 2D array. While parsing, create a
          temporary LinkedList for the two cities and store it in paths.*/
        while (fin.hasNextLine()) {
            line = fin.nextLine();
            String[] splitline2 = line.split(" ");
            city1 = splitline2[0].toLowerCase();
            city2 = splitline2[1].toLowerCase();
            distance = Integer.parseInt(splitline2[2]);
            LinkedList<String> individualpath = new LinkedList<>();
            individualpath.addFirst(city1);
            individualpath.addLast(city2);
            paths.add(individualpath);

            int a = index(city1);
            int b = index(city2);

            /* Assign the distances between the two cities to the
            index of both cities in the mileage 2D array. A 2D array of
            mileages (the cost array) is a key component to Dijkstra's */
            mileage[a][b] = distance;
            mileage[b][a] = distance;


        }

        while (true) {

            /*Read the starting city, destination city and normal speed from user input.
            Trim and make both cities lowercase, handling any typing errors from the user.
            Cast and initialize the base speed (for distances from city A to city B) and the
            throttled speed (for distances from city B to city A). */
            System.out.print("Enter the starting city: ");
            city1 = in.nextLine().trim();
            city1 = city1.toLowerCase();
            System.out.print("Enter the destination city: ");
            city2 = in.nextLine().trim();
            city2 = city2.toLowerCase();
            System.out.print("Enter the normal speed: ");

            try {
                basespeed = new Integer(in.nextLine().trim());
            }catch(NumberFormatException n){
                System.out.println("Please try again and enter a speed!");
                basespeed = 0;
                continue;

            }
            throttledspeed = 0.7 * basespeed;



            /*In this while loop, the user inputs the base speed (and therefore the throttled speed).
            Before this while loop, mileages between cities were stored in a the mileage 2D array using the
            index of two cities.

           The paths ArrayList allows access to all of the paths from the roads.txt file within this while loop.
                                               [Solves the issue I had with scope].
            Here, if the path is from city A to city B the base speed is assigned to a specific point in the speeds
            2D array; if the path is from city B to city A the throttled speed is assigned to a specific point in
            the speeds 2D array.*/
            for(LinkedList<String> thislist: paths){
                speeds[index(thislist.getFirst())][index(thislist.getLast())] = basespeed;
                speeds[index(thislist.getLast())][index(thislist.getFirst())] = throttledspeed;
            }

            //Check if the user inputs a base speed that is impossible or 0.
            if(basespeed == 0){
                System.out.println("You will never reach your location because you are not moving.");
            }

            if(basespeed < 0){
                System.out.println("You must have a positive speed value. Try moving forward!");
            }

            //Handle the case where the user inputs the same starting city and destination city.
            if (city1.equals(city2)) {
                System.out.println("You are already in the city, so the distance is 0.");
            }

            /*Handle the case where, if either of the cities is not found, print out the name of
            the city or cities that are not found*/
            int startInd = index(city1);
            int stopInd = index(city2);

            if (startInd == -1 || stopInd == -1) {
                if (startInd == -1) {
                    System.out.println("The Starting City is Not Found");
                }
                if (stopInd == -1) {
                    System.out.println("The Destination City is Not Found");
                }
            }else{

                //Run Dijkstra's algorithm with the first city
                dijkstra(city1);

                try{

                    //After running Dijkstra's algorithm print the path of cities (with distances in between) and return the total distance.
                    totaldistances = printdistances(city1, city2);

                    //In the case where either the starting city or destination city can not be reached, ask the user if they want to input a new path.
                } catch (IndexOutOfBoundsException j) {
                    System.out.println("There is no road to or from either the starting city or the destination city. Look at the possible roads and submit a new route!");
                }


                //Handle the case where the user inputs the same starting and destination city.
                try{

                    //Then, print the path of cities (with the distances in between) and retrun the average speed.
                    averagespeed = printspeeds(city1, city2);

                    //Finally, print the path of cities (with the times in between) and return the total time for the trip.
                    totaltime = printtimes(city1, city2);

                    //Update the average speed to a more accurate value
                    averagespeed = properNumber(totaldistances/totaltime);

                    //Print out the final statistics, including the total distance, total time and the average speed.
                    System.out.println("Total of " + totaldistances + " miles; " + totaltime + " hours;");
                    System.out.println("The average speed is: " + averagespeed + "m/h");

                    //ArrayIndexOutofBoundsException occurs because there are no cities in the intCities* list used in the methods above,
                    //meaning there is no path, especially from a city to itself.
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("(Know that there is no average speed or time taken for this trip because you are already in the city.)");
                }



            }

            //Allows the user to exit the program.
            System.out.print("Please enter 'Q'/'q' to quit, any other key to continue: ");
            line = in.nextLine();
            if (line.equalsIgnoreCase("Q")) {
                break;
            }
        }
    }

    //Gets the index of the city in the cities array. If the city is not in the array of cities return -1.
    public static int index(String name) {
        for (int i = 0; i < cities.length; ++i) {
            if (name.equalsIgnoreCase(cities[i])) {
                return i;
            }
        }

        return -1;
    }

    /*Method "init" initializes the distance for the starting array. This method
     serves as a basis for the implementation of Dijkstra's Algorithm. */
    public static void init(String start) {
        int startInd = index(start);
        for (int i = 0; i < size; ++i) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
            visited[i] = false;
        }

        dist[startInd] = 0;
    }

    //Mehtod "minIndex" is used to get the minimum index of of the last city to be visited.
    public static int minIndex() {
        int minInd = -1, minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < size; ++i) {
            if (!visited[i] && dist[i] < minDistance) {
                minInd = i;
                minDistance = dist[i];
            }
        }
        return minInd;
    }

    /* Method "done" that returns false if there is still a city that hasn't been visited and returns
     * true if all the cities have been visited. */
    public static boolean done() {
        for (int i = 0; i < size; ++i) {
            if (!visited[i]) {
                return false;
            }
        }
        return true;
    }

    /*Prints the path of cities from the start city to the stop city,
    including the cities in between and the distances between each city.*/
    public static int printdistances(String start, String stop) {
        int startInd = index(start);
        int stopInd = index(stop);
        int prevInd = prev[stopInd];
        List <String> intCities = new ArrayList<>();

        //Add the cities in between the starting city and destination city to the intCities List.
        //This uses the prev array to get the cities in reverse order.
        while (prevInd != startInd) {
            intCities.add(cities[prevInd]);
            prevInd = prev[prevInd];
        }

        //Handle the case where there are cities in between the starting and destination cities.
        if (intCities.size() > 0 ) {
            //Print out the start city and the mileage to the second city in the path
            System.out.print("Distance(m): [" + toPropNoun(start) + " ==" + mileage[startInd][index(intCities.get(intCities.size() - 1))] + "=> ");

                 /*Print out the second city followed by the distance to the third city,
                 then print out each city thereafter followed by the distance to the next city in the List.
                 This loop will continue to the second to last city.*/
            for (int i = intCities.size() - 1; i > 0; --i) {
                System.out.print(toPropNoun(intCities.get(i)) + " ==" + mileage[index(intCities.get(i))][index(intCities.get(i - 1))] + "=> ");
            }

            //Print out the second to last city, the distance to the last city, then the last city.
            System.out.println(toPropNoun(intCities.get(0)) + " ==" + mileage[index(intCities.get(0))][stopInd] + "=> " + toPropNoun(stop) + "]");

            //Handle the case where there are no cities in between the starting and destination cities. The route is direct.
        }else{
            System.out.println("Distance(m): [" + toPropNoun(start) + " ==" + mileage[startInd][stopInd] + "=> " + toPropNoun(stop) + "]");
        }

        //Print and return the total(shortest)distance between the start city and the stop city.
        int totaldistance = dist[stopInd];
        return totaldistance;
    }

    /*Prints the path of cities from the start city to the stop city,
    including the cities in between and the times between each city.
    Time calculation is the mileage index divided by the speed index,
    rounded to two decimal places. */
    public static double printtimes(String start, String stop){
        int startInd = index(start);
        int stopInd = index(stop);
        int prevInd = prev[stopInd];
        List <String> intCities2 = new ArrayList <>();

        //initialize the total time
        double totaltimes = 0;

        //Add the cities in between the starting city and destination city to the intCities2 array List.
        //This uses the prev array to get the cities in reverse order.
        while (prevInd != startInd) {
            intCities2.add(cities[prevInd]);
            prevInd = prev[prevInd];
        }

        //Handle the case where there are cities in between the starting and destination cities.
        if (intCities2.size() > 0 ){

            //Print out the start city and the time to the second city in the path
            System.out.print("Time(h): [" + toPropNoun(start) + " ==" + properNumber(mileage[startInd][index(intCities2.get(intCities2.size() - 1))]/speeds[startInd][index(intCities2.get(intCities2.size() - 1))]) + "=> ");

            //update the total time
            totaltimes = totaltimes + properNumber(mileage[startInd][index(intCities2.get(intCities2.size() - 1))]/speeds[startInd][index(intCities2.get(intCities2.size() - 1))]);

            /*Print out the second city followed by the time to the third city,
            then print out each city thereafter followed by the time to the next city in the List.
            This loop will continue to the second to last city.*/
            for (int i = intCities2.size() - 1; i > 0; --i) {
                System.out.print(toPropNoun(intCities2.get(i)) + " ==" + properNumber(mileage[index(intCities2.get(i))][index(intCities2.get(i - 1))]/speeds[index(intCities2.get(i))][index(intCities2.get(i - 1))]) + "=> ");

                //update the total time
                totaltimes = totaltimes + properNumber(mileage[index(intCities2.get(i))][index(intCities2.get(i - 1))]/speeds[index(intCities2.get(i))][index(intCities2.get(i - 1))]);
            }

            //Print out the second to last city, the time to the last city, then the last city.
            System.out.println(toPropNoun(intCities2.get(0)) + " ==" + properNumber(mileage[index(intCities2.get(0))][stopInd]/speeds[index(intCities2.get(0))][stopInd]) + "=> " + toPropNoun(stop) + "]");

            //update the total time
            totaltimes = totaltimes + properNumber(mileage[index(intCities2.get(0))][stopInd]/speeds[index(intCities2.get(0))][stopInd]);
            //Handle the case where there are no cities in between the starting and destination cities. The route is direct.
        } else {
            System.out.println("Time(h): [" + toPropNoun(start) + " ==" + properNumber(mileage[startInd][stopInd]/speeds[startInd][stopInd]) + "=> " + toPropNoun(stop) + "]");
            totaltimes = totaltimes + properNumber(mileage[startInd][stopInd]/speeds[startInd][stopInd]);
        }
        return properNumber(totaltimes);
    }

    /*Prints the path of cities from the start city to the stop city,
    including the cities in between and the speeds between each city. */
    public static double printspeeds(String start, String stop){
        int startInd = index(start);
        int stopInd = index(stop);
        int prevInd = prev[stopInd];
        List <String> intCities2 = new ArrayList <>();

        //initialize the average speed variable
        double averagespeed = 0.0;

        //Add the cities in between the starting city and destination city to the intCities2 ArrayList.
        //This uses the prev array to get the cities in reverse order.
        while (prevInd != startInd) {
            intCities2.add(cities[prevInd]);
            prevInd = prev[prevInd];
        }

        //Handle the case where there are cities in between the starting and destination cities.
        if (intCities2.size() > 0 ){

            //Print out the start city and the speed to the second city in the path
            System.out.print("Speed(m/h): [" + toPropNoun(start) + " ==" + speeds[startInd][index(intCities2.get(intCities2.size() - 1))] + "=> ");

            //update the speed value
            averagespeed = averagespeed + speeds[startInd][index(intCities2.get(intCities2.size() - 1))];

            /*Print out the second city followed by the speed to the third city,
            then print out each city thereafter followed by the speed to the next city in the List.
            This loop will continue to the second to last city.*/
            for (int i = intCities2.size() - 1; i > 0; --i) {
                System.out.print(toPropNoun(intCities2.get(i)) + " ==" + speeds[index(intCities2.get(i))][index(intCities2.get(i - 1))] + "=> ");

                //update the speed value
                averagespeed = averagespeed + speeds[index(intCities2.get(i))][index(intCities2.get(i - 1))];
            }

            //Print out the second to last city, the speed to the last city, then the last city.
            System.out.println(toPropNoun(intCities2.get(0)) + " ==" + speeds[index(intCities2.get(0))][stopInd] + "=> " + toPropNoun(stop) + "]");

            //update the speed value
            averagespeed = averagespeed + speeds[index(intCities2.get(0))][stopInd];

            //Handle the case where there are no cities in between the starting and destination cities. The route is direct.
        }else{
            System.out.println("Speed(m/h): [" + toPropNoun(start) + " ==" + speeds[startInd][stopInd] + "=> " + toPropNoun(stop) + "]");

            //update the speed value
            averagespeed = averagespeed + speeds[startInd][stopInd];
        }

        //Finds and returns the average speed, which is the sum of all speeds divided by the number of cities in between the start and end cities + 1
        //Cities in between + 1 = number of paths (number of speeds in between all cities in the path
        return properNumber(averagespeed/(intCities2.size() + 1));
    }

    //Implementation of Dijkstra's Algorithm.
    public static void dijkstra(String start) {
        try {
            init(start);

            //While there is still a city to be visited.
            while (!done()) {

                //Get the minimum index
                int minimumIndex = minIndex();

                //The city has now been visited
                visited[minimumIndex] = true;

                //Increment though the rest of the cities in the visited array
                for (int endIndex = 0; endIndex < size; ++endIndex) {

                    /*Look at the rest of the values in the city array. If the end of the visited array has not been visited
                     * and the mileage between the two cities is not the max value, add the mileage between the two
                     * cities to the total distance between the two cities. */
                    if (!visited[endIndex]) {
                        if (mileage[minimumIndex][endIndex] != Integer.MAX_VALUE) {
                            int dy = dist[minimumIndex] + mileage[minimumIndex][endIndex];

                            //Check to see if the individual distance is less than the total distance so far.
                            if (dy < dist[endIndex]) {

                        /* Assign the updated total distance to the distance array at the endIndex.
                        At the end of dijkstra's algorithm the value at dist[endIndex] will be the
                        shortest distance between the starting city and the destination city. */
                                dist[endIndex] = dy;

                                //Assign the minimumIndex to the endIndex in prev array to keep track of the least indices.
                                prev[endIndex] = minimumIndex;
                            }
                        }
                    }
                }
            }
        }catch(IndexOutOfBoundsException t) {
            System.out.print("");
        }
    }

    /* Writes the cityFile.txt from the roadsFile.txt*/
    public static int writeCityFile(File filename) {
        ArrayList<String> c = new ArrayList<>();
        try {
            Scanner newscanner = new Scanner(filename);
            PrintWriter pwriter = new PrintWriter("cityFile.txt");

            //Parse the roadsFile for the city names.
            while (newscanner.hasNextLine()) {
                String line = newscanner.nextLine();
                String[] splitline = line.split(" ");
                String city1 = splitline[0].toLowerCase();
                String city2 = splitline[1].toLowerCase();

                /*Since the size of the ArrayList of cities starts at 0,
                 add the first city read from the roads.txt file to the cityFile.
                 If ArrayList does not contain the starting city, add
                 the city to the ArrayList and write the city to the
                 the cityFile. If ArrayList does not contain the destination
                 city add the destination city to the ArrayList and write the
                 city to the cityFile*/

                if (c.size() <= 0) {
                    c.add(city1);
                    pwriter.println(city1);
                } else {
                    if (!c.contains(city1)) {
                        c.add(city1);
                        pwriter.println(city1);
                    }
                    if (!c.contains(city2)) {
                        c.add(city2);
                        pwriter.println(city2);
                    }
                }


            }
            //close the scanner and the writer.
            newscanner.close();
            pwriter.close();

            //Handle the case where the roads file was not found.
        } catch (FileNotFoundException e) {
            System.out.println("The roads file was not found.");
        }
        return c.size();
    }

    //Returns the city name to its original, proper-noun form
    public static String toPropNoun(String name) {
        char[] chararray = name.toCharArray();
        //Capitalize the first character
        chararray[0] = Character.toUpperCase(chararray[0]);
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) == '_') {
                //Capitalize the character after any underscore
                chararray[i + 1] = Character.toUpperCase(chararray[i + 1]);
            }
        }
        String text = String.copyValueOf(chararray);
        return text;
    }

    //Rounds a double to two decimal places
    public static double properNumber(double number){
        number = (double) Math.round(number * 100d) / 100d;
        return number;
    }

}