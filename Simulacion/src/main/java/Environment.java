

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Environment {

    private List<Particle> particles;
    private double width, height, grooveLength;
    private double[][] collisionTimes;
    private double timeForFirstCollision;
    CollitedObject collitedObject1, collitedObject2;
    Particle grooveParticleTop,grooveParticleBottom;



    public Environment(List<Particle> particles, double width, double height, double grooveLength) {
        this.particles = particles;
        this.width = width;
        this.height = height;
        this.grooveLength = grooveLength;
        this.collisionTimes = new double[particles.size()+WALL_DIRECTION.values().length+2][particles.size()+WALL_DIRECTION.values().length+2];
        this.timeForFirstCollision = Double.POSITIVE_INFINITY;
        this.collitedObject1 = null;
        this.collitedObject2 = null;
        this.grooveParticleBottom=new Particle(width/2,(height-grooveLength)/2,0,0,0,0,true);
        this.grooveParticleTop=new Particle(width/2,height-(height-grooveLength)/2,0,0,0,0,true);

    }

    public List<Particle> getState() {
        return this.particles;
    }

    public List<Particle> getParticlesToRecalculate() {
        if(this.collitedObject1 == null && this.collitedObject2 == null)
            return this.getState();  //With all the particles, 1st time
        else
            return Arrays.stream(new Object[]{collitedObject1.getObject(), collitedObject2.getObject()}).map(o -> (Particle)o).collect(Collectors.toList());
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
    private double timeToWallCollision(Particle particle, Wall wall) {
        double time,finalY;
        switch (wall.getDirection()){
            case UPPER:
                if (particle.getVy()>0)
                    return (height-particle.getRadius()-particle.getY())/particle.getVy();
                return Double.POSITIVE_INFINITY;
            case LOWER:
                if (particle.getVy()<0)
                    return (particle.getY()-particle.getRadius())/particle.getVy();
                return Double.POSITIVE_INFINITY;
            case RIGHT:
                if (particle.getVx()>0)
                    return (width-particle.getRadius()-particle.getX())/particle.getVx();
                return Double.POSITIVE_INFINITY;
            case LEFT:
                if (particle.getVx()<0)
                    return (particle.getX()-particle.getRadius())/particle.getVx();
                return Double.POSITIVE_INFINITY;
            case UPPER_GROOVE:
                if (particle.getX()>width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2&&particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                time=(width/2 - particle.getX())/particle.getVx();
                finalY=particle.getY()+particle.getVy()*time;
                if (finalY<height&&finalY>height-(height-grooveLength)/2)
                    return time;
                return Double.POSITIVE_INFINITY;
            case LOWER_GROOVE:
                if (particle.getX()>width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2&&particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                time=(width/2 - particle.getX())/particle.getVx();
                finalY=particle.getY()+particle.getVy()*time;
                if (finalY>0&&finalY<(height-grooveLength)/2)
                    return time;
                return Double.POSITIVE_INFINITY;
            default:
                throw new RuntimeException("invalid direction");
        }
    }



    public void recalculateCollisions(List<Particle> particlesToRecalculate) {
        try {
            int idxAux;
            double time;
            Particle particle1, particle2;
            for (int i = 0; i < particlesToRecalculate.size(); i++) {
                //choques entre particulas
                for (int j = 0; j < this.collisionTimes.length - WALL_DIRECTION.values().length-2; j++) {
                    if(!particlesToRecalculate.get(i).equals(this.particles.get(j))) {
                        particle1 = particlesToRecalculate.get(i);
                        idxAux = this.particles.indexOf(particle1);
                        particle2 = this.particles.get(j);
                        time = this.timeToParticlesCollision(particle1, particle2);
                        this.collisionTimes[idxAux][j] = time;
                        this.collisionTimes[j][idxAux] = time;

                    }
                }
                //considerar las particulas de radio 0 del groove
                particle1 = particlesToRecalculate.get(i);
                idxAux = this.particles.indexOf(particle1);
                time = this.timeToParticlesCollision(particle1, grooveParticleBottom);
                this.collisionTimes[idxAux][WALL_DIRECTION.values().length+1] = time;
                this.collisionTimes[WALL_DIRECTION.values().length+1][idxAux] = time;
                time = this.timeToParticlesCollision(particle1, grooveParticleTop);
                this.collisionTimes[idxAux][WALL_DIRECTION.values().length+2] = time;
                this.collisionTimes[WALL_DIRECTION.values().length+2][idxAux] = time;

                //choques con las paredes
                for (int j = 0; j < WALL_DIRECTION.values().length; j++) {
                    particle1 = particlesToRecalculate.get(i);
                    idxAux = this.particles.indexOf(particle1);
                    time = timeToWallCollision(particle1,new Wall(WALL_DIRECTION.values()[j]));
                    this.collisionTimes[idxAux][j] = time;
                    this.collisionTimes[j][idxAux] = time;
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public double timeToNextCollision(){
        double min=Double.POSITIVE_INFINITY;
        for (int i = 0; i < collisionTimes.length; i++) {
            for (int j = i+1; j < collisionTimes.length; j++) {
                if (collisionTimes[i][j]<min)
                    min=collisionTimes[i][j];
            }
        }
        this.timeForFirstCollision=min;
        return min;
    }


    public void evolve(double tc) {
        //Calculamos nuevas posiciones de acuerdo a ecuaciones de MRU hasta tc
        Particle particle;
        for(int i = 0; i < this.particles.size(); i++) {
            particle = this.particles.get(i);
            particle.setX(particle.getX() + particle.getVx()*tc);
            particle.setY(particle.getY() + particle.getVy()*tc);
            this.particles.set(i, particle);
        }
    }

    public void calculateNewVelocities() {
        if(collitedObject1.getObjectType() == collitedObject2.getObjectType()){
            Particle particle1 = (Particle) collitedObject1.getObject();
            Particle particle2 = (Particle) collitedObject2.getObject();
            if(particle2.isFixed()) {
                CollisionOperators.particleToFixedParticle(this.particles, particle1, particle2);
            } else if(particle1.isFixed()) {
                CollisionOperators.particleToFixedParticle(this.particles, particle2, particle1);

            } else {
                CollisionOperators.particleToParticle(this.particles, particle1, particle2);
            }

        } else {
            //Collisions with walls
            Particle particle;
            Wall wall;
            if (collitedObject1.getObjectType()==Wall.class){
                wall=(Wall) collitedObject1.getObject();
                particle= (Particle) collitedObject2.getObject();
            }else {
                wall=(Wall) collitedObject2.getObject();
                particle= (Particle) collitedObject1.getObject();
            }
            CollisionOperators.particleToWall(this.particles,particle,wall);
        }

    }


    private int counter = 0;
    public boolean stopCriteria() {
        return counter++ == 5;
    }


}
