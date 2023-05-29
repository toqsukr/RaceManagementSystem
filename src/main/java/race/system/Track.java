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

    public void setTrackID(Integer id) {
        trackID = id;
    }

    @Column(name = "trackName")
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String name) {
        trackName = name;
    }

    @Column(name = "trackLength")
    public Integer getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(Integer length) {
        trackLength = length;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "winnerRacerID", referencedColumnName = "racerID")
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
