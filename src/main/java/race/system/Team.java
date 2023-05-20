package race.system;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
@Immutable
public class Team {
    private Integer teamID;
    private String teamName;
    private Integer racerNumber;
    private Integer totalPoints;

    @Id
    @Column(name = "teamID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer id) {
        teamID = id;
    }

    @Column(name = "teamName")
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String name) {
        teamName = name;
    }

    @Column(name = "racerNumber")
    @Access(AccessType.FIELD)
    public Integer getRacerNumber() {
        return racerNumber;
    }

    public void expandRacerNumber() {
        racerNumber++;
    }

    public void reduceRacerNumber() {
        racerNumber--;
    }

    @Column(name = "totalPoints")
    @Access(AccessType.FIELD)
    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void addPoints(int value) {
        totalPoints += value;
    }

    public void deductPoints(int value) {
        totalPoints -= value;
    }

    public Team() {
        teamID = 0;
        teamName = null;
        racerNumber = 0;
        totalPoints = 0;
    };

    public Team(String name) {
        teamID = 0;
        teamName = name;
        racerNumber = 1;
        totalPoints = 0;
    };
}