package race.system;

import javax.persistence.*;

@Entity
@Table(name = "RaceManagementSystem.Competition")
public class Competition {
    private int competitionId;
    private MyDate date;
    private Track track;

    @Id
    @Column(name = "competitionId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int id) {
        competitionId = id;
    }

    @Column(name = "dateId")
    public MyDate getDate() {
        return date;
    };

    public void setDate(MyDate otherDate) {
        date = otherDate;
    };

    @Column(name = "trackId")
    public Track getTrack() {
        return track;
    };

    public void setTrack(Track otherTrack) {
        track = otherTrack;
    };
}
