package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "racers")
public class Racer {
    private Integer racerID;
    private String racerName;
    private Integer racerAge;
    private Integer racerPoints;
    private Team racerTeam;
    private Track winnerTrackID;

    @Id
    @Column(name = "racerID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getRacerID() {
        return racerID;
    }

    public void setRacerID(Integer id) {
        racerID = id;
    }

    @Column(name = "racerName")
    public String getRacerName() {
        return racerName;
    }

    public void setRacerName(String name) {
        racerName = name;
    }

    @Column(name = "racerAge")
    public Integer getRacerAge() {
        return racerAge;
    }

    public void setRacerAge(Integer age) {
        racerAge = age;
    }

    @Column(name = "racerPoints")
    public Integer getRacerPoints() {
        return racerPoints;
    }

    public void setRacerPoints(Integer points) {
        racerPoints = points;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "racerTeamID", referencedColumnName = "teamID")
    public Team getTeam() {
        return racerTeam;
    }

    public void setTeam(Team team) {
        racerTeam = team;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winnerTrackID", referencedColumnName = "trackID")
    public Track getWinnerTrack() {
        return winnerTrackID;
    }

    public void setWinnerTrack(Track track) {
        winnerTrackID = track;
    }

    public Racer() {
    };

    public Racer(String name, Integer age, Team team, Integer points) {
        racerID = 0;
        racerName = name;
        racerAge = age;
        racerTeam = team;
        racerPoints = points;
    };

    public void showRacerInfo() {
        System.out.println(racerName + ", " + racerAge + " y.o from team " + racerTeam.getTeamName() + " have "
                + racerPoints + " points");
    }
}
