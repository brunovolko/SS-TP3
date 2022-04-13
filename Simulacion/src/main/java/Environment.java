import javafx.geometry.Pos;

import java.util.List;

public class Environment {

    private List<Particle> particles;
    private double width, height, grooveLength;
    private double[][] collisionTimes;
    private double timeForFirstCollision;
    CollitedObject collitedObject1, collitedObject2;

    private enum walls {UPPER, LOWER, LEFT, RIGHT, UPPER_GROOVE, LOWER_GROOVE};

    public Environment(List<Particle> particles, double width, double height, double grooveLength) {
        this.particles = particles;
        this.width = width;
        this.height = height;
        this.grooveLength = grooveLength;
        this.collisionTimes = new double[particles.size()+walls.values().length][particles.size()+walls.values().length];
        this.timeForFirstCollision = Double.POSITIVE_INFINITY;
    }

    public List<Particle> getState() {
        return this.particles;
    }

    private void updateTimeForFirstCollision(double time, CollitedObject collitedObject1, CollitedObject collitedObject2) throws Exception {
        this.timeForFirstCollision = time;
        this.collitedObject1 = collitedObject1;
        this.collitedObject2 = collitedObject2;
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

    public void recalculateCollisions(List<Particle> particlesToRecalculate) {
        try {
            //TODO considerar las 6 paredes
            //TODO onsiderarlas partiuclas de radio 0 del groove
            int idxAux;
            double time;
            Particle particle1, particle2;
            for (int i = 0; i < particlesToRecalculate.size(); i++) {
                for (int j = i + 1; j < this.collisionTimes.length - walls.values().length; j++) {
                    particle1 = particlesToRecalculate.get(i);
                    idxAux = this.particles.indexOf(particle1);
                    particle2 = this.particles.get(j);
                    time = this.timeToParticlesCollision(particle1, particle2);
                    this.collisionTimes[idxAux][j] = time;
                    this.collisionTimes[j][idxAux] = time;
                    if (time < timeForFirstCollision)
                        this.updateTimeForFirstCollision(time, new CollitedObject(particle1), new CollitedObject(particle2)); //For particle-particle collision
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    public void evolve() {
        //Calculamos nuevas posiciones de acuerdo a ecuaciones de MRU hasta tc
        Particle particle;
        for(int i = 0; i < this.particles.size(); i++) {
            particle = this.particles.get(i);
            particle.setX(particle.getX() + particle.getVx()*this.timeForFirstCollision);
            particle.setY(particle.getY() + particle.getVy()*this.timeForFirstCollision);
            this.particles.set(i, particle);
        }
    }

    public void calculateNewVelocities() {
        if(collitedObject1.getObjectType() == collitedObject2.getObjectType()){
            Particle particle1 = (Particle) collitedObject1.getObject();
            Particle particle2 = (Particle) collitedObject2.getObject();
            if(particle2.isFixed()) {
                //TODO particle-grooveparticle
            } else if(particle1.isFixed()) {
                //TODO particle-grooveparticle

            } else {
                CollisionOperators.particleToParticle(this.particles, particle1, particle2);
            }
        } else {
            //TODO particle-wall
        }

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
