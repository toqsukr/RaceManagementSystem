package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "Teams")
public class Team {
    private Integer teamID;
    private String teamName;

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

    public Team() {
    };

    public Team(String name) {
        teamName = name;
    };
}