package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "scores")
public class Score {
    private Integer scoreID;
    private Racer racerInfo;
    private Track trackInfo;
    private Integer finishTime;

    @Id
    @Column(name = "ScoreID")
    public Integer getScoreID() {
        return scoreID;
    }

    public void setScoreID(Integer id) {
        scoreID = id;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "racerID", referencedColumnName = "racerID")
    public Racer getRacerInfo() {
        return racerInfo;
    };

    public void setRacerInfo(Racer racer) {
        racerInfo = racer;
    };

    @Column(name = "finishTime")
    public Integer getFinishTime() {
        return finishTime;
    };

    public void setFinishTime(Integer time) {
        finishTime = time;
    };

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "trackID", referencedColumnName = "trackID")
    public Track getTrackInfo() {
        return trackInfo;
    };

    public void setTrackInfo(Track track) {
        trackInfo = track;
    };

    public Score() {
        racerInfo = null;
        trackInfo = null;
        finishTime = 0;
    }

    public Score(Racer racer, Track track, Integer time) {
        racerInfo = racer;
        trackInfo = track;
        finishTime = time;
    }
}
