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

    public void setDateID(Integer id) {
        dateID = id;
    }

    @Column(name = "day")
    public Integer getDay() {
        return day;
    };

    public void setDay(Integer dayNumber) {
        day = dayNumber;
    };

    @Column(name = "month")
    public Integer getMonth() {
        return month;
    };

    public void setMonth(Integer monthNumber) {
        month = monthNumber;
    };

    @Column(name = "year")
    public Integer getYear() {
        return year;
    };

    public void setYear(Integer yearNumber) {
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
