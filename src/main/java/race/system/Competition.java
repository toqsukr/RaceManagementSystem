package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "competition")
public class Competition {
    private Integer competitionID;
    private MyDate date;
    private Track track;

    @Id
    @Column(name = "competitionID")
    public Integer getCompetitionID() {
        return competitionID;
    }

    public void setCompetitionID(Integer id) {
        competitionID = id;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "dateID", referencedColumnName = "dateID")
    public MyDate getDate() {
        return date;
    };

    public void setDate(MyDate otherDate) {
        date = otherDate;
    };

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
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

    public Competition(MyDate _date, Track _track) {
        date = _date;
        track = _track;
    }
}
