package race.system;

import javax.persistence.*;

@Entity
@Table(name = "RaceManagementSystem.Dates")
public class MyDate {
    private int dateId;
    private int day;
    private int month;
    private int year;

    @Id
    @Column(name = "dateId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getDateId() { return dateId; }
    public void setDateId(int id) { dateId = id; }

    @Column(name = "day")
    public int getDay(){ return day; };
    public void setDay(int dayNumber){ day = dayNumber; };

    @Column(name = "month")
    public int getMonth(){ return month; };
    public void setMonth(int monthNumber){ month = monthNumber; };

    @Column(name = "year")
    public int getYear(){ return year; };
    public void setYear(int yearNumber){ year = yearNumber; };
}
