package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    private Integer teamID;
    private String teamName;
    private Integer racerNumber;
    private Integer totalPoints;

    @Id
    @Column(name = "teamID")
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
    public Integer getRacerNumber() {
        return racerNumber;
    }

    public void setRacerNumber(Integer value) {
        racerNumber = value;
    }

    public void expandRacerNumber() {
        racerNumber++;
    }

    public void reduceRacerNumber() {
        racerNumber--;
    }

    @Column(name = "totalPoints")
    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int points) {
        totalPoints = points;
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
        racerNumber = 1;
        totalPoints = 0;
    };

    public Team(String name) {
        teamID = 0;
        teamName = name;
        racerNumber = 1;
        totalPoints = 0;
    };
}