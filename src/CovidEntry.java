import java.text.DecimalFormat;

public class CovidEntry implements Comparable{

    private String state;
    private int month;
    private int day;
    private int dailyDeaths;
    private int dailyInfections;
    private int totalDeaths;
    private int totalInfections;

    public CovidEntry(String state, int month, int day, int dailyInfections,int dailyDeaths,int totalInfections, int totalDeaths ) {
        this.state = state;
        this.month = month;
        this.day = day;
        this.dailyInfections = dailyInfections;
        this.dailyDeaths = dailyDeaths;
        this.totalInfections = totalInfections;
        this.totalDeaths = totalDeaths;
    }
    public int compareTo(Object other){
        CovidEntry c = (CovidEntry) other;
        return c.dailyDeaths - dailyDeaths;
    }

    public String getState() {
        return state;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getDailyDeaths() {
        return dailyDeaths;
    }

    public int getDailyInfections() {
        return dailyInfections;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalInfections() {
        return totalInfections;
    }

    //prints out  daily infections/deaths and totals
    public String toString(){
        String pattern = "###,###";
        DecimalFormat numberFormat = new DecimalFormat(pattern);
        String s = this.state + " " + this.month + "/" + this.day + " " +
                numberFormat.format(this.dailyInfections) + " daily infections, " + numberFormat.format(this.dailyDeaths) + " daily deaths. Total infections: " +
                numberFormat.format(this.totalInfections) + " Total deaths: " + numberFormat.format(this.totalDeaths);
        return s;
    }

}

