package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "tracks")
public class Track {
    private Integer trackID;
    private String trackName;
    private Integer trackLength;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getTrackID() {
        return trackID;
    }

    public void setTrackID(Integer trackID) {
        this.trackID = trackID;
    }

    // @Column(name = "trackName")
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    // @Column(name = "trackLength")
    public Integer getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(Integer trackLength) {
        this.trackLength = trackLength;
    }
}
