package race.system;

import javax.persistence.*;

@Entity
@Table(name = "RaceManagementSystem.Scores")
public class Score {
    private int scoreId;
    private Racer racerInfo;
    private double finishTime;

    @Id
    @Column(name = "scoreId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getScoreId() { return scoreId; }
    public void setScoreId(int id) { scoreId = id; }

    public Racer getRacerInfo(){ return racerInfo; };
    public void setRacerInfo(Racer racer){ racerInfo = racer; };

    @Column(name = "finishTime")
    public double getFinishTime(){ return finishTime; };
    public void setFinishTime(double time){ finishTime = time; };
}
