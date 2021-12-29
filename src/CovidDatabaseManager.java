public class CovidDatabaseManager {


    public static void main(String[] args) {
        //CovidEntry covidEntry = new CovidEntry("NY",4,20,4726, 478,91332,6457); test of covid entry
        //test results on main
        CovidDatabase covidDatabase = new CovidDatabase();
        //covidDatabase.readCovidData("covid_data.csv");
        covidDatabase.transferCovidData("covid_data.csv");
        covidDatabase.readCovidData();

        System.out.println("Test of Count: " + covidDatabase.countRecords());
        System.out.println();

        System.out.println("Test getting total Deaths: " + covidDatabase.getTotalDeaths());
        System.out.println();

        System.out.println("Test getting infections: " + covidDatabase.getTotalInfections());
        System.out.println();

        System.out.println("Test getting most Deaths: " + covidDatabase.mostTotalDeaths());
        System.out.println();

        System.out.println("Test peak daily deaths of \"MI\""  + covidDatabase.peakDailyDeaths("MI"));
        System.out.println();

        System.out.println("test of peak daily deaths \"5/5\" " + covidDatabase.peakDailyDeaths(5, 5));
        System.out.println();

        System.out.println("Test of top ten deaths \"5, 5\" " + covidDatabase.topTenDeaths(5, 5));
        System.out.println();

        System.out.println("Test safe to open \"MI\": "+ covidDatabase.safeToOpen("MI"));
        System.out.println("");

        System.out.println("Test minimum daily infections \"6/12/1000\": " + covidDatabase.listMinimumDailyInfections(6,12,1000));

    }
}
