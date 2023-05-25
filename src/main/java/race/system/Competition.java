package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "competition")
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "dateID", referencedColumnName = "dateID")
    public MyDate getDate() {
        return date;
    };

    public void setDate(MyDate otherDate) {
        date = otherDate;
    };

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "trackID", referencedColumnName = "trackID")
    public Track getTrack() {
        return track;
    };

    public void setTrack(Track otherTrack) {
        track = otherTrack;
    };
}
