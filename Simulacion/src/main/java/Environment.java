import javafx.geometry.Pos;

import java.util.List;

public class Environment {

    private List<Particle> particles;
    private double width, height, grooveLength;
    private double[][] collisionTimes;

    private enum walls {UPPER, LOWER, LEFT, RIGHT, UPPER_GROOVE, LOWER_GROOVE};

    public Environment(List<Particle> particles, double width, double height, double grooveLength) {
        this.particles = particles;
        this.width = width;
        this.height = height;
        this.grooveLength = grooveLength;
        this.collisionTimes = new double[particles.size()+walls.values().length][particles.size()+walls.values().length];

    }

    public List<Particle> getState() {
        return this.particles;
    }

    private double timeToParticlesCollision(Particle particle1, Particle particle2) {
        double deltaVdeltaR, deltaVdeltaV, deltaRdeltaR, sigma, d;
        deltaVdeltaR = (particle2.getVx() - particle1.getVx())*(particle2.getX() - particle1.getX()) + (particle2.getVy() - particle1.getVy())*(particle2.getY() - particle1.getY());
        if(deltaVdeltaR >= 0)
            return Double.POSITIVE_INFINITY;
        deltaRdeltaR = Math.pow(particle2.getX() - particle1.getX(),2) + Math.pow(particle2.getY() - particle1.getY(),2);
        deltaVdeltaV = Math.pow(particle2.getVx() - particle1.getVx(),2) + Math.pow(particle2.getVy() - particle1.getVy(),2);
        sigma = particle1.getRadius() + particle2.getRadius();
        d = Math.pow(deltaVdeltaR,2) - deltaVdeltaV*(deltaRdeltaR-sigma*sigma);
        if (d < 0)
            return Double.POSITIVE_INFINITY;
        return -1*(deltaVdeltaR + Math.sqrt(d))/(deltaVdeltaV);

    }

    public void setupEvolution() {

        Particle particle1, particle2;

        for(int i = 0; i < this.collisionTimes.length - walls.values().length; i++) {
            for (int j = i+1; j < this.collisionTimes[0].length - walls.values().length; j++) {
                if(i != j) {
                    particle1 = this.particles.get(i);
                    particle2 = this.particles.get(j);
                    this.collisionTimes[i][j] = this.timeToParticlesCollision(particle1, particle2);
                    this.collisionTimes[j][i] = this.timeToParticlesCollision(particle1, particle2);
                }
            }
        }
    }


    public void evolve() {
    }

    private boolean aux = false;
    public boolean stopCriteria() {
        if(aux)
            return true;
        else
        {
            aux = true;
            return false;
        }
    }
}
