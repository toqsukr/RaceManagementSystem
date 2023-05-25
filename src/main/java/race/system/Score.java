package race.system;

import jakarta.persistence.*;

@Entity
@Table(name = "scores")
public class Score {
    private Integer ScoreID;
    private Racer racerInfo;
    private Track trackInfo;
    private Double finishTime;

    @Id
    @Column(name = "ScoreID")
    public Integer getScoreID() {
        return ScoreID;
    }

    public void setScoreID(int id) {
        ScoreID = id;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "racerID", referencedColumnName = "racerID")
    public Racer getRacerInfo() {
        return racerInfo;
    };

    public void setRacerInfo(Racer racer) {
        racerInfo = racer;
    };

    @Column(name = "finishTime")
    public Double getFinishTime() {
        return finishTime;
    };

    public void setFinishTime(double time) {
        finishTime = time;
    };

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "trackID", referencedColumnName = "trackID")
    public Track getTrackInfo() {
        return trackInfo;
    };

    public void setTrackInfo(Track track) {
        trackInfo = track;
    };
}
