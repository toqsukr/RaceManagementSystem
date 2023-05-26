package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "competition")
public class Competition {
    private int competitionID;
    private MyDate date;
    private Track track;

    @Id
    @Column(name = "competitionID")
    public int getCompetitionID() {
        return competitionID;
    }

    public void setCompetitionID(int id) {
        competitionID = id;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dateID", referencedColumnName = "dateID")
    public MyDate getDate() {
        return date;
    };

    public void setDate(MyDate otherDate) {
        date = otherDate;
    };

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "trackID", referencedColumnName = "trackID")
    public Track getTrack() {
        return track;
    };

    public void setTrack(Track otherTrack) {
        track = otherTrack;
    };

    public Competition() {
        this.date = null;
        this.track = null;
    }

    public Competition(MyDate date, Track track) {
        this.date = date;
        this.track = track;
    }
}
