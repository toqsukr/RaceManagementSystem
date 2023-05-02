package race.system;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "RaceManagementSystem.Tracks")
public class Track {
    private int trackId;
    private String trackName;
    private double length;
    private List<Score> scores;
    private List<Racer> winners;

    @Id
    @Column(name = "teamId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int id) {
        trackId = id;
    }

    @Column(name = "trackName")
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String inputString) {
        trackName = inputString;
    }

    @Column(name = "trackLength")
    public double getLength() {
        return length;
    };

    public void setLength(double value) {
        length = value;
    };

    public List<Score> getAllScores() {
        return scores;
    };

    public void addScore(Score score) {
    };

    public void deleteScore(Score score) {
    };

    public List<Racer> getWinners() {
        return winners;
    };
}
