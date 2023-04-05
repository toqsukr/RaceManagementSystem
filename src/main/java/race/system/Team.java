package race.system;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "RaceManagementSystem.Teams")
public class Team {
    private int teamId;
    private String name;
    private int racerNumber;
    private List<Racer> structure;
    private int teamPoints;

    @Id
    @Column(name = "teamId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getTeamId() { return teamId; }
    public void setTeamId(int id) { teamId = id; }

    @Column(name = "teamName")
    public String getTeamName(){ return name; };
    public void setTeamName(String inputString){ name = inputString; };

    public int getTeamPoints(){ return teamPoints; };
    public int getRacerNumber(){ return structure.size(); };

    public List<Racer> getTeamStructure(){ return structure; };
    public void addRacer(Racer racer){ structure.add(racer); };
    public void deleteRacer(Racer racer){ structure.remove(racer); };
}
