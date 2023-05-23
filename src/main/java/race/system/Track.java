package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "tracks")
public class Track {
    private Integer trackID;
    private String trackName;
    private Integer trackLength;
    private Racer trackWinner;

    @Id
    @Column(name = "trackID")
    public Integer getTrackID() {
        return trackID;
    }

    public void setTrackID(Integer trackID) {
        this.trackID = trackID;
    }

    @Column(name = "trackName")
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Column(name = "trackLength")
    public Integer getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(Integer trackLength) {
        this.trackLength = trackLength;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "winnerRacerID", referencedColumnName = "RacerID")
    public Racer getWinner() {
        return trackWinner;
    }

    public void setWinner(Racer racer) {
        trackWinner = racer;
    }

    public Track() {
        trackID = 0;
        trackName = null;
        trackLength = 0;
    }

    public Track(String name, int length) {
        trackID = 0;
        trackName = name;
        trackLength = length;
    }
}
