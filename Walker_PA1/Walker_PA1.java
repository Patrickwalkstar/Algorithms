//A program that implements the Gale-Shapley algorithm for Stable Marriages.
//Author: Patrick Walker
//Date: 1 March 2019


import java.util.*;
import org.json.*;
import org.json.simple.*;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.google.gson.*;



public class Walker_PA1{
    public int length;
    public int engagedCount;
    public String[] men;
    public String[] women;
    private String[][] menPref;
    private String[][] womenPref;
    public String[] menPairs;
    public String[] womenPairs;
    public boolean[] menEngaged;
    public boolean[] womenEngaged;

    /** Constructor **/

    /** This constructor is meant to create a galeshapley object, containing the necessary
     * elements, including parameters which are the arrays that store the names of the men,
     * the names of the women,the arrays of men's preferences and the arrays of women's preferences. **/
    public Walker_PA1(String[] men, String[] women, String[][] menPref, String[][] womenPref){

        this.length = menPref.length;
        this.engagedCount = 0;
        this.men = men;
        this.women = women;
        this.menPref = menPref;
        this.womenPref = womenPref;
        this.womenEngaged = new boolean[this.length];
        this.womenPairs = new String[this.length];
        this.menEngaged = new boolean[this.length];
        this.menPairs = new String[this.length];

    }



    /** The main function is meant to process the general workings of the program.
     * Read the input arguments and return the appropriate output. **/
    public static void main(String args[]){
        String filename = args[1];
        String line, strJson = "";
        BufferedReader reader;

        //Make sure there is a a file to read from and read the lines from the input file.
        try{
            reader = new BufferedReader(new FileReader(filename));
            while((line = reader.readLine())!= null){
                strJson += line;
            }
            reader.close();

        }catch(IOException e){
            System.out.println("Input failure: -w,-m InputFile.json -o OutputFile.json");
            System.exit(0);
        }

        System.out.println("Gale-Shapley Marriage Algorithm\n");

        //Creating a map for the Json elemetns is the best way to store the data.

        Set<Map.Entry<String,JsonElement>> menMap  = getJsonMap("man", strJson);
        Set<Map.Entry<String,JsonElement>> womenMap  = getJsonMap("woman", strJson);

        int bigness = womenMap.size();

        String[] men = new String[bigness];
        String[][] menPref = new String[bigness][bigness];

        String[] women = new String [bigness];
        String[][] womenPref = new String [bigness][bigness];

        getArrays(men, menPref, menMap);
        getArrays(women, womenPref, womenMap);
        String matchedJSONObject = "";

        Walker_PA1 galeshapleyObject = new Walker_PA1(men, women, menPref, womenPref);

        //Process  the Gale-SHapley algorithm for the appropriate arguments from the command line.
        if (args[0].equals("-m") && args[2].equals("-o")) {
            matchedJSONObject = galeshapleyObject.menMatching();
        }
        else if (args[0].equals("-w") && args[2].equals("-o")) {
            matchedJSONObject = galeshapleyObject.womenMatching();
        }
        else {
            System.out.println("Input failure: -w,-m InputFile.json -o OutputFile.json");
            System.exit(0);
        }
        //Write to a new output file.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[3]));
            writer.write(matchedJSONObject);
            writer.close();

        } catch (IOException e) {
            System.out.println("Output failure: -w,m InputFile.json -o OutputFile.json");
            System.exit(0);
        }
    }

    //Process the Gale-Shapley algorithm for men. The idea is to iterate through all free men
    //while there is any free man available. Every free man goes to all women in his preference
    //list according to the order. For every woman he goes to, he checks if the woman is free,
    //if yes, then both become engaged. If the woman is not free, then the woman chooses to either
    //say no to the proposing man  or dump her current engagement according to her preference list.
    // So an engagement done once can be broken if a women gets a better option.
    private String menMatching() {
         int freeMan;
        //while there is a free man who has not yet proposed to a woman
        while (this.engagedCount < this.length) {
           
            for (freeMan = 0; freeMan < this.length; freeMan++) {
                if (!menEngaged[freeMan]) {
                    break;
                }
            }
            //have the man propose to the first woman on her preference list that has not yet been proposed to
            for (int i = 0; i < this.length && !menEngaged[freeMan]; i++) {
                int index = womanLocation(menPref[freeMan][i]);
                if (womenPairs[index] == null) {
                    womenPairs[index] = men[freeMan];
                    menEngaged[freeMan] = true;
                    this.engagedCount++;
                }
                else {
                    String currentPair = womenPairs[index];
                    if (checkPreferences(index, currentPair, men[freeMan])) {
                        womenPairs[index] = men[freeMan];
                        menEngaged[freeMan] = true;
                        menEngaged[manLocation(currentPair)] = false;
                    }
                }
            }
        }
        return mentoJSON();
    }

    //Process the Gale-Shapley algorithm for women. Read details above in the menMatching method.
    // The process is essentially the same for both men and women, except their arrays are swapped.
    //Creating a separate method for women makes output much easier.
    private String womenMatching() {
        int freeWoman;
        //While there is a free woman who has not yet proposed to a man
        while (this.engagedCount < this.length) {
            for (freeWoman = 0; freeWoman < this.length; freeWoman++) {
                if (!womenEngaged[freeWoman]) {
                    break;
                }
            }
            //have the man propose to the first man on her preference list that has not yet been proposed to
            for (int i = 0; i < this.length && !womenEngaged[freeWoman]; i++) {
                int index = manLocation(womenPref[freeWoman][i]);
                if (menPairs[index] == null) {
                    menPairs[index] = women[freeWoman];
                    womenEngaged[freeWoman] = true;
                    this.engagedCount++;
                }
                else {
                    String currentPair = menPairs[index];
                    if (checkPreferences(index, currentPair, women[freeWoman])) {
                        menPairs[index] = men[freeWoman];
                        womenEngaged[freeWoman] = true;
                        womenEngaged[womanLocation(currentPair)] = false;
                    }
                }
            }
        }
        return womentoJSON();
    }

    //Checks the index of the woman.
    private int womanLocation(String w) {
        for (int i = 0; i < this.length; i++)
            if (women[i].equals(w))
                return i;
        return -1;
    }

    //Checks the index of the man.
    private int manLocation(String m) {
        for (int i = 0; i < this.length; i++)
            if (men[i].equals(m))
                return i;
        return -1;
    }

    //Checks to see if the man or woman has a higher preference, using and comparing the two possible pairs.
    private boolean checkPreferences(int index, String currentPair, String newPair) {
        for (int i = 0; i < this.length; i++) {
            if (womenPref[index][i].equals(newPair))
                return true;
            if (womenPref[index][i].equals(currentPair))
                return false;
        }
        return false;
    }

    //Fills a map from a LinkedHashMap. Puts the womenPairs value and woman value in the currentMap.
    public String mentoJSON() {
        Map<String, String> currentMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < this.length; i++) {
            currentMap.put(womenPairs[i], women[i]);
        }

        Gson gson = new Gson();
        return gson.toJson(currentMap);
    }

    //Fills a map from a LinkedHashMap. Puts the menPairs value and man value in the currentMap.
    public String womentoJSON() {
        Map<String, String> currentMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < this.length; i++) {
            currentMap.put(menPairs[i], men[i]);
        }
        Gson gson = new Gson();
        return gson.toJson(currentMap);
    }

    //Creates a returns a set, recognizing the appropriate keys from the input file. The
    //elements from the file are appropriately parsed.
    private static Set<Map.Entry<String,JsonElement>> getJsonMap(String key, String json) {
        if (key.equals("man")) {
            JsonElement jsonElement = new JsonParser().parse(json);
            JsonObject  menJson = jsonElement.getAsJsonObject();
            menJson = menJson.getAsJsonObject("man");
            return menJson.entrySet();
        }
        else if (key.equals("woman")) {
            JsonElement jsonElement = new JsonParser().parse(json);
            JsonObject  womenJson = jsonElement.getAsJsonObject();
            womenJson = womenJson.getAsJsonObject("woman");
            return womenJson.entrySet();
        }
        return null;
    }

    //The arrays of preferences are appropriately parsed from the Json file. Extracting and tokenizing
    // the arrays from their respective keys. Iterate through the keys to retrieve each key's array of preferences.
    private static void getArrays(String[] list, String[][] preferences, Set<Map.Entry<String,JsonElement>> map) {
        String tempVals = "";
        StringTokenizer stringTokenizer;

        Iterator<Map.Entry<String,JsonElement>> iterator = map.iterator();

        if (iterator != null ) {
            for (int i = 0; iterator.hasNext(); i++) {
                Map.Entry<String,JsonElement> entry = iterator.next();
                list[i] = entry.getKey();
                tempVals = entry.getValue().toString();
                stringTokenizer = new StringTokenizer(tempVals, "[,]\"");

                for(int j = 0; stringTokenizer.hasMoreTokens(); j++) {
                    preferences[i][j] = stringTokenizer.nextToken();
                }
            }
        }
    }
}
