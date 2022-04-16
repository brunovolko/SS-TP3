

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

        for(int i = this.particles.size(); i < this.collisionTimes.length; i++)
            for(int j = this.particles.size(); j < this.collisionTimes.length; j++)
                this.collisionTimes[i][j] = Double.POSITIVE_INFINITY;
    }

    public List<Particle> getState() {
        return this.particles;
    }

    public List<Particle> getParticlesToRecalculate() {
        List<Particle> particlesToRecalculate = new ArrayList<>();
        if(this.collitedObject1 != null && this.collitedObject1.getObjectType().equals(Particle.class)) {
            Particle auxPart1 = (Particle) this.collitedObject1.getObject();
            if (!auxPart1.isFixed())
                particlesToRecalculate.add(auxPart1);
        }
        if(this.collitedObject2 != null && this.collitedObject2.getObjectType().equals(Particle.class)) {
            Particle auxPart2 = (Particle) this.collitedObject2.getObject();
            if (!auxPart2.isFixed())
                particlesToRecalculate.add(auxPart2);
        }
        if(particlesToRecalculate.size() == 0)
            return this.getState();
        else
            return particlesToRecalculate;
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
        double ret = -1*(deltaVdeltaR + Math.sqrt(d))/(deltaVdeltaV);
    //    if (ret<=0.0)
     //       return Double.POSITIVE_INFINITY;
        return -1*(deltaVdeltaR + Math.sqrt(d))/(deltaVdeltaV);

    }
    private double timeToWallCollision(Particle particle, Wall wall) {
        double time,finalY;
        switch (wall.getDirection()){
            case UPPER:
                if (particle.getVy()>0)
                    return Math.abs((height-particle.getRadius()-particle.getY())/particle.getVy());
                return Double.POSITIVE_INFINITY;
            case LOWER:
                if (particle.getVy()<0)
                    return Math.abs((particle.getY()-particle.getRadius())/particle.getVy());
                return Double.POSITIVE_INFINITY;
            case RIGHT:
                if (particle.getVx()>0)
                    return Math.abs((width-particle.getRadius()-particle.getX())/particle.getVx());
                return Double.POSITIVE_INFINITY;
            case LEFT:
                if (particle.getVx()<0)
                    return Math.abs((particle.getX()-particle.getRadius())/particle.getVx());
                return Double.POSITIVE_INFINITY;
            case UPPER_GROOVE:
                if (particle.getX()>width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2&&particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                time=Math.abs((width/2 - particle.getX())/particle.getVx());
                finalY=particle.getY()+particle.getVy()*time;
                if (finalY<height&&finalY>height-(height-grooveLength)/2)
                    return time;
                return Double.POSITIVE_INFINITY;
            case LOWER_GROOVE:
                if (particle.getX()>width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2&&particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                time=Math.abs((width/2 - particle.getX())/particle.getVx());
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
                this.collisionTimes[idxAux][this.particles.size() + WALL_DIRECTION.values().length] = time;
                this.collisionTimes[this.particles.size() + WALL_DIRECTION.values().length][idxAux] = time;
                time = this.timeToParticlesCollision(particle1, grooveParticleTop);
                this.collisionTimes[idxAux][this.particles.size() + WALL_DIRECTION.values().length+1] = time;
                this.collisionTimes[this.particles.size() + WALL_DIRECTION.values().length+1][idxAux] = time;

                //choques con las paredes
                for (int j = 0; j < WALL_DIRECTION.values().length; j++) {
                    time = timeToWallCollision(particle1,new Wall(WALL_DIRECTION.values()[j]));
                    this.collisionTimes[idxAux][this.particles.size() + j] = time;
                    this.collisionTimes[this.particles.size() + j][idxAux] = time;
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public double timeToNextCollision() throws Exception{
        double min=Double.POSITIVE_INFINITY;
        int imin = 0, jmin = 0;
        for (int i = 0; i < collisionTimes.length; i++) {
            for (int j = i+1; j < collisionTimes.length; j++) {
                if (collisionTimes[i][j]<min) {
                    min = collisionTimes[i][j];
                    imin = i;
                    jmin = j;
                }
            }
        }
        CollitedObject collitedObject1, collitedObject2;
        if(imin < this.particles.size())
            collitedObject1 = new CollitedObject(this.particles.get(imin));
        else if(imin - this.particles.size() < WALL_DIRECTION.values().length)
            collitedObject1 = new CollitedObject(new Wall(WALL_DIRECTION.values()[imin-this.particles.size()]));
        else if(imin == this.collisionTimes.length-2)
            collitedObject1 = new CollitedObject(grooveParticleBottom);
        else
            collitedObject1 = new CollitedObject(grooveParticleTop);

        if(jmin < this.particles.size())
            collitedObject2 = new CollitedObject(this.particles.get(jmin));
        else if(jmin - this.particles.size() < WALL_DIRECTION.values().length)
            collitedObject2 = new CollitedObject(new Wall(WALL_DIRECTION.values()[jmin-this.particles.size()]));
        else if(jmin == this.collisionTimes.length-2)
            collitedObject2 = new CollitedObject(grooveParticleBottom);
        else
            collitedObject2 = new CollitedObject(grooveParticleTop);

        this.updateTimeForFirstCollision(min, collitedObject1, collitedObject2);
        if(min<=0.0)
            System.out.println("tiempo min dio "+ min);
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
        return counter++==10;
        /*
        int totalParticles = particles.size();
        double limit = width / 2 - particles.get(0).getRadius();

        int leftSideParticlesCounter = 0;


        for(Particle particle : particles) {
            if(particle.getX()<limit)
                leftSideParticlesCounter++;
        }

         System.out.println(leftSideParticlesCounter);
        return ((double)leftSideParticlesCounter / totalParticles) >= 0.495
                && ((double)leftSideParticlesCounter / totalParticles) <= 0.505;

         */
    }

}
