package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "dates")
public class MyDate {
    private Integer dateID;
    private Integer day;
    private Integer month;
    private Integer year;

    @Id
    @Column(name = "dateID")
    public Integer getDateID() {
        return dateID;
    }

    public void setDateID(int id) {
        dateID = id;
    }

    @Column(name = "day")
    public Integer getDay() {
        return day;
    };

    public void setDay(int dayNumber) {
        day = dayNumber;
    };

    @Column(name = "month")
    public Integer getMonth() {
        return month;
    };

    public void setMonth(int monthNumber) {
        month = monthNumber;
    };

    @Column(name = "year")
    public Integer getYear() {
        return year;
    };

    public void setYear(int yearNumber) {
        year = yearNumber;
    };

    public MyDate() {
        this.day = -1;
        this.month = -1;
        this.year = -1;
    }

    public MyDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
}
