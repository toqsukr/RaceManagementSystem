package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    private Integer teamID;
    private String teamName;
    private Integer racerNumber;

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

    public void updateRacerNumber() {
        racerNumber++;
    }

    public Team() {
        teamID = 0;
        teamName = null;
        racerNumber = 0;
    };

    public Team(String name) {
        teamID = 0;
        teamName = name;
        racerNumber = 1;
    };
}