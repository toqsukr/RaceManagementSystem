package race.system;

import javax.persistence.*;

@Entity
@Table(name = "RaceManagementSystem.Racers")
public class Racer {
    private Integer racerId;
    private String name;
    private Integer age;
    private Integer points;

    private String team;

    public Racer(String name, Integer age, String team, Integer points) {
        this.name = name;
        this.age = age;
        this.team = team;
        this.points = points;
    }

    @Id
    @Column(name = "racerId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getRacerId() {
        return racerId;
    }

    public void setRacerId(int id) {
        racerId = id;
    }

    @Column(name = "racerName")
    public String getName() {
        return name;
    }

    public void setName(String inputString) {
        name = inputString;
    }

    public String getTeam() {
        return team;
    }

    @Column(name = "racerAge")
    public Integer getAge() {
        return age;
    }

    public void setAge(int value) {
        age = value;
    }

    @Column(name = "racerPoints")
    public Integer getPoints() {
        return points;
    }

    public void setPoints(int value) {
        points = value;
    }
}
