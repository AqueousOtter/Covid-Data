import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class CovidDatabase {

    private ArrayList<CovidEntry> CovidEntryList;
    private static final int SAFE = 5;

    public CovidDatabase() {

        this.CovidEntryList = new ArrayList<CovidEntry>();
    }

    public void transferCovidData(String filename){
        int batchSize =20;
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:covid.db");
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists entry");
            statement.executeUpdate("create table entry (state String,month int,day int,daily_infections int,daily_deaths int,total_infections int,total_deaths int)");

            String sql = "INSERT INTO entry (state,month,day,daily_infections,daily_deaths,total_infections,total_deaths)VALUES(?,?,?,?,?,?,?)";

            PreparedStatement PS = connection.prepareStatement(sql);
            BufferedReader lineReader = new BufferedReader(new FileReader(filename));
            lineReader.readLine();//skips first line
            String lineText;
            int count = 0;

            while((lineText = lineReader.readLine()) != null){
                String[] data = lineText.split(",");
                String state = data[0];
                String month = data[1];
                String day = data[2];
                String dailyInfections = data[3];
                String dailyDeaths = data[4];
                String totalInfections = data[5];
                String totalDeaths = data[6];

                PS.setString(1, state);
                PS.setInt(2, Integer.parseInt(month));
                PS.setInt(3, Integer.parseInt(day));
                PS.setInt(4, Integer.parseInt(dailyInfections));
                PS.setInt(5, Integer.parseInt(dailyDeaths));
                PS.setInt(6, Integer.parseInt(totalInfections));
                PS.setInt(7, Integer.parseInt(totalDeaths));

                PS.addBatch();
                if(count % batchSize == 0){
                    PS.executeBatch();
                }
            }
            lineReader.close();

            PS.executeBatch();
            connection.commit();
            connection.close();

        }
        catch (IOException ex){
            System.err.println(ex);
        }catch (SQLException e){
            e.printStackTrace();
            try {
                connection.rollback();
            }catch (SQLException exx){
                exx.printStackTrace();
            }
        }
    }

   //reads data from database file
    public void readCovidData(){
        Connection connection = null;
        try {
            //transfers data from csv

            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:covid.db");
            String sql = "SELECT * FROM entry";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            //loops through all entries
            while(rs.next()){
                CovidEntry newEntry = new CovidEntry(rs.getString("state"), rs.getInt("month"), rs.getInt("day"), rs.getInt("daily_Infections"), rs.getInt("daily_Deaths"), rs.getInt("total_Infections"), rs.getInt("total_Deaths"));
                CovidEntryList.add(newEntry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //method to read data from csv file
/*/   public void readCovidData(String filename){
        FileInputStream fileByteStream = null;
        Scanner scnr = null;
        try {
            //open
            fileByteStream = new FileInputStream(filename);
            scnr = new Scanner(fileByteStream);
            scnr.useDelimiter("[,\r\n]+");
            scnr.nextLine();//skips first line
            while (scnr.hasNext()){    //read all data, creating new entries with every line...
                String state = scnr.next();
                int month = scnr.nextInt();
                int day = scnr.nextInt();
                int dailyInfections = scnr.nextInt();
                int dailyDeaths = scnr.nextInt();
                int totalInfections = scnr.nextInt();
                int totalDeaths = scnr.nextInt();
                CovidEntry newEntry = new CovidEntry(state, month, day, dailyInfections, dailyDeaths, totalInfections, totalDeaths);
                CovidEntryList.add(newEntry);
            }
            fileByteStream.close();


        }catch (IOException error1){
            System.out.println("Error with file "+ filename);
        }
    }*/

    public int countRecords(){
        return this.CovidEntryList.size();
    }
    public int getTotalDeaths(){
        int totalDeaths =0;
        for (CovidEntry covidEntry : CovidEntryList) {
            totalDeaths += covidEntry.getDailyDeaths();
        }
        return totalDeaths;
    }
    public int getTotalInfections(){
        int totalInfections = 0;
        for(CovidEntry covidEntry : CovidEntryList){
            totalInfections += covidEntry.getDailyInfections();
        }
        return totalInfections;
    }
    public CovidEntry peakDailyDeaths(String st){
        int peak = 0;
        CovidEntry peakDeath = null;
        for(CovidEntry covidEntry: CovidEntryList){
            if(covidEntry.getState().equalsIgnoreCase(st)){
                if(covidEntry.getDailyDeaths() > peak){
                    peak = covidEntry.getDailyDeaths();
                    peakDeath = covidEntry;
                }
            }
        }
        return peakDeath;
    }
    public ArrayList<CovidEntry> getDailyDeaths(int m, int d){
        ArrayList<CovidEntry> matchedDates = new ArrayList<>();
        for(CovidEntry covidEntry : CovidEntryList){
            if(covidEntry.getMonth() == m && covidEntry.getDay() == d){
                matchedDates.add(covidEntry);
            }
        }
        return matchedDates;
    }

    //return the CovidEntry object with the highest daily death for the requested date.
    public CovidEntry peakDailyDeaths(int m, int d){
        int peak = 0;
        CovidEntry peakDaily = null;
        for(CovidEntry covidEntry: CovidEntryList){
            if(covidEntry.getMonth() == m && covidEntry.getDay() == d){
                if(covidEntry.getDailyDeaths() > peak){
                    peak = covidEntry.getDailyDeaths();
                    peakDaily = covidEntry;
                }
            }
        }
        return peakDaily;
    }

    //return the CovidEntry object with the highest total deaths.
    public CovidEntry mostTotalDeaths(){
        int peak = 0;
        CovidEntry peakDeaths = null;
        for (CovidEntry covidEntry : CovidEntryList){
            if(covidEntry.getTotalDeaths() > peak){
                peak = covidEntry.getTotalDeaths();
                peakDeaths = covidEntry;
            }
        }
        return peakDeaths;
    }
    //return a new ArrayList containing all records (CovidEntry objects) that match the requested date and have a minimum requested daily infection
    public ArrayList<CovidEntry> listMinimumDailyInfections(int m, int d, int min){
        ArrayList<CovidEntry> minInfections = new ArrayList<>();
        for(CovidEntry covidEntry : CovidEntryList){
            if(covidEntry.getMonth() == m && covidEntry.getDay() == d && covidEntry.getDailyInfections() >= min){
                minInfections.add(covidEntry);
            }
        }
        return minInfections;
    }
    public ArrayList<CovidEntry> safeToOpen(String st){
        ArrayList<CovidEntry> safeOpen = new ArrayList<>();
        CovidEntry previousDay = null;
        int safeDays = 0;
        for(CovidEntry covidEntry : CovidEntryList){
            if(covidEntry.getState().equalsIgnoreCase(st) && safeDays < SAFE){
                if(safeDays == 0){
                    previousDay = covidEntry;
                    safeOpen.add(previousDay);
                    safeDays++;
                }
                else{
                    //checks previous day's infection and compares current day's infection. resetting everything if not consecutive days
                    if(previousDay.getDailyInfections() > covidEntry.getDailyInfections() && covidEntry.getDay() == previousDay.getDay()+1){
                        previousDay = covidEntry;
                        safeOpen.add(covidEntry);
                        safeDays++;
                    }
                    else{
                        safeOpen.clear();
                        safeOpen.add(covidEntry);
                        previousDay = covidEntry;
                        safeDays = 1;
                    }
                }
            }
        }
        if(safeDays == SAFE){
            return safeOpen;
        }
        else{
            return null;
        }
    }

    //comparable
    public ArrayList<CovidEntry> topTenDeaths(int m, int d){
        ArrayList<CovidEntry> dailyDeathsList = getDailyDeaths(m, d);
        Collections.sort(dailyDeathsList);
        if(dailyDeathsList.size() > 0 ){
            ArrayList<CovidEntry> topTen = new ArrayList<>(dailyDeathsList.subList(0,10));
            return  topTen;
        }
        else{
            return dailyDeathsList;
        }

    }




}

