

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
    private double totalImpulse;



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
        this.totalImpulse=0;


        for(int i = 0; i < this.collisionTimes.length; i++)
            for(int j = 0; j < this.collisionTimes.length; j++)
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

        double ret =-1*(deltaVdeltaR + Math.sqrt(d))/(deltaVdeltaV);
        if(ret<=0.0)
            System.out.println("tiempo negativo");
        return ret;

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
                    return Math.abs((particle.getY()-particle.getRadius())/particle.getVy());
                return Double.POSITIVE_INFINITY;
            case RIGHT:
                if (particle.getVx()>0)
                    return (width-particle.getRadius()-particle.getX())/particle.getVx();
                return Double.POSITIVE_INFINITY;
            case LEFT:
                if (particle.getVx()<0)
                    return Math.abs((particle.getX()-particle.getRadius())/particle.getVx());
                return Double.POSITIVE_INFINITY;
            case UPPER_GROOVE:
                if (particle.getX() > width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2 && particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                if(particle.getX() < width/2 - particle.getRadius())
                    time=(width/2 - particle.getRadius() - particle.getX())/particle.getVx();
                else if(particle.getX() > width/2 + particle.getRadius())
                    time=(width/2 + particle.getRadius() - particle.getX())/particle.getVx();//Math.abs((particle.getX() - width/2 - particle.getRadius())/particle.getVx());
                else
                    return Double.POSITIVE_INFINITY;
                finalY=particle.getY()+particle.getVy()*time;
                if (finalY<height&&finalY>height-(height-grooveLength)/2) {
                    if(time < 0)
                        System.out.println("UPER GROOVE. x="+particle.getX()+";Y="+particle.getY()+";Vx="+particle.getVx()+";Vy="+particle.getVy()); //TODO sacar prints
                    return time;
                }

                return Double.POSITIVE_INFINITY;
            case LOWER_GROOVE:
                if (particle.getX()>width/2&&particle.getVx()>0)
                    return Double.POSITIVE_INFINITY;
                if (particle.getX()<width/2&&particle.getVx()<0)
                    return Double.POSITIVE_INFINITY;
                if(particle.getX() < width/2 - particle.getRadius())
                    time=(width/2 - particle.getRadius() - particle.getX())/particle.getVx();
                else if(particle.getX() > width/2 + particle.getRadius())
                    time=(width/2 + particle.getRadius() - particle.getX())/particle.getVx();
                else
                    return Double.POSITIVE_INFINITY;
                finalY=particle.getY()+particle.getVy()*time;
                if (finalY>0&&finalY<(height-grooveLength)/2) {
                    if(time < 0)
                        System.out.println("UPER GROOVE. id=" +particles.indexOf(particle)+ "x="+particle.getX()+";Y="+particle.getY()+";Vx="+particle.getVx()+";Vy="+particle.getVy()); //TODO sacar prints
                    return time;
                }


                return Double.POSITIVE_INFINITY;
            default:
                throw new RuntimeException("invalid direction");
        }
    }




    int a =0;
    public void recalculateCollisions(List<Particle> particlesToRecalculate) {
        a++;
        try {
            int idxAux;
            double time;
            Particle particle1, particle2;
            for (int i = 0; i < particlesToRecalculate.size(); i++) {
                //choques entre particulas
                particle1 = particlesToRecalculate.get(i);
                for (int j = 0; j < this.collisionTimes.length - WALL_DIRECTION.values().length-2; j++) {
                    if(!particle1.equals(this.particles.get(j))) {
                        idxAux = this.particles.indexOf(particle1);
                        particle2 = this.particles.get(j);
                        time = this.timeToParticlesCollision(particle1, particle2);
                        if(time<=0.0)
                            System.out.println("t negativo con particulas");
                        this.collisionTimes[idxAux][j] = time;
                        this.collisionTimes[j][idxAux] = time;

                    }
                }


                //considerar las particulas de radio 0 del groove
                idxAux = this.particles.indexOf(particle1);
                time = this.timeToParticlesCollision(particle1, grooveParticleBottom);
                if(time<=0.0)
                    System.out.println("t negativo con groove bottom");
                this.collisionTimes[idxAux][this.particles.size() + WALL_DIRECTION.values().length] = time;
                this.collisionTimes[this.particles.size() + WALL_DIRECTION.values().length][idxAux] = time;

                time = this.timeToParticlesCollision(particle1, grooveParticleTop);
                if(time<=0.0)
                    System.out.println("t negativo con groove top");
                this.collisionTimes[idxAux][this.particles.size() + WALL_DIRECTION.values().length+1] = time;
                this.collisionTimes[this.particles.size() + WALL_DIRECTION.values().length+1][idxAux] = time;


                //choques con las paredes
                for (int j = 0; j < WALL_DIRECTION.values().length; j++) {
                    time = timeToWallCollision(particle1,new Wall(WALL_DIRECTION.values()[j]));
                    if(time<0.0)
                        System.out.println("t negativo con wall: " + time);
                    else if(time==0.0)
                        System.out.println("t cero");
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
            for (int j = 0; j < collisionTimes.length; j++) {
                if (collisionTimes[i][j]>=0.0 && collisionTimes[i][j]<min) {
                    min = collisionTimes[i][j];
                    imin = i;
                    jmin = j;
                }
            }
        }
        CollitedObject collitedObject1, collitedObject2;
        if(imin < this.particles.size())
            collitedObject1 = new CollitedObject(this.particles.get(imin));
        else if(imin - this.particles.size() < WALL_DIRECTION.values().length) {
            collitedObject1 = new CollitedObject(new Wall(WALL_DIRECTION.values()[imin - this.particles.size()]));
        }
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
        if(min<0.0)
            System.out.println("tiempo min dio "+ min);
        return min;
    }

    public void evolve(double tc) {

        //Calculamos nuevas posiciones de acuerdo a ecuaciones de MRU hasta tc
        Particle particle;
        double umbralError = 0.000000000000001;
        for(int i = 0; i < this.particles.size(); i++) {
            particle = this.particles.get(i);
            particle.setX(particle.getX() + particle.getVx()*tc);
            particle.setY(particle.getY() + particle.getVy()*tc);
            List<Particle> collitedParticles = this.getParticlesToRecalculate();


            if(Math.abs(particle.getX() - particle.getRadius()) < umbralError) //Pared izquierda
                particle.setX(particle.getRadius());
            if(Math.abs(particle.getX() - width + particle.getRadius()) < umbralError) //Pared derecha
                particle.setX(width-particle.getRadius());
            if(Math.abs(particle.getY() - particle.getRadius()) < umbralError) //Pared inferior
                particle.setY(particle.getRadius());
            if(Math.abs(particle.getY() + particle.getRadius() - height) < umbralError) //Pared superior
                particle.setY(height-particle.getRadius());



            if ((particle.getY()>0&&particle.getY()<(height-grooveLength)/2) || (particle.getY()<height&&particle.getY()>height-(height-grooveLength)/2)) {
                if(Math.abs(particle.getX() + particle.getRadius() - width/2) < umbralError) //Tabique izquierda
                    particle.setX(width/2 - particle.getRadius());
                if(Math.abs(particle.getX() - particle.getRadius() - width/2) < umbralError) //Tabique derecha
                    particle.setX(width/2 + particle.getRadius());
            }



            //Estos chequeos son para verificar errores de redondeo de la computadora
            if(particle.getX()<particle.getRadius())
                System.out.println("particula "+i +" dio con x izq " + particle.getX());
            if(particle.getX() > width - particle.getRadius())
                System.out.println("particula "+i +" dio con x der " + particle.getX());
            if(particle.getY()<particle.getRadius())
                System.out.println("particula "+i +" dio con y neg " + particle.getY());
            if(particle.getY() > height - particle.getRadius())
                System.out.println("particula "+i +" dio con y neg " + particle.getY());

            if ((particle.getY()>0&&particle.getY()<(height-grooveLength)/2) || (particle.getY()<height&&particle.getY()>height-(height-grooveLength)/2)) {
                if((particle.getX() > width/2 - particle.getRadius()) && (particle.getX() < width/2 + particle.getRadius())) { //Tabique
                    System.out.println("particula mal " + i + ". dio con x " + particle.getX() + " " + particle.getY());
                    System.out.println(collitedParticles.contains(particle));
                }
            }

            this.particles.set(i, particle);
        }
        for(int i =0;i<collisionTimes.length;i++){
            for(int j=0;j<collisionTimes.length;j++){
                if(collisionTimes[i][j]==0.0)
                    continue;
                collisionTimes[i][j]-=timeForFirstCollision;
            }
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


    public void addImpulse() {

        if (!llegoAl50){
            this.timeTo50+=timeForFirstCollision;
            return;
        }
        if (collitedObject1.getObjectType() != Wall.class && collitedObject2.getObjectType() != Wall.class)
            return;



        Particle particle;
        Wall wall;
        if(collitedObject2.getObjectType() == Wall.class) {
            particle = (Particle) collitedObject1.getObject();
            wall = (Wall) collitedObject2.getObject();
        }
        else {
            particle = (Particle) collitedObject2.getObject();
            wall = (Wall) collitedObject1.getObject();
        }
        switch (wall.getDirection()){
            case LEFT:
            case RIGHT:
            case LOWER_GROOVE:
            case UPPER_GROOVE:
                totalImpulse+=Math.abs(particle.getMass()*particle.getVx()*2);
                break;
            case LOWER:
            case UPPER:
                totalImpulse+=Math.abs(particle.getMass()*particle.getVy()*2);
                break;
        }

    }
    public double getTotalImpulse(){
        return this.totalImpulse;
    }

    public int getCantParticlesLeftSide() {
        double limit = width / 2 - particles.get(0).getRadius();

        int leftSideParticlesCounter = 0;


        for(Particle particle : particles) {
            if(particle.getX()<limit)
                leftSideParticlesCounter++;
        }
        return leftSideParticlesCounter;
    }

    int counterTo50 = 0;
    int counter = 0;
    boolean llegoAl50 = false;

    double timeTo50=0;

    public boolean stopCriteria() {
        counter++;
        int totalParticles = particles.size();
        int leftSideParticlesCounter = this.getCantParticlesLeftSide();
        double fraccionIzq = ((double)leftSideParticlesCounter / totalParticles);
        if(fraccionIzq <= 0.5) {
            if(!llegoAl50)
                counterTo50 = counter;
            llegoAl50 = true;
        }


        return llegoAl50 && counter > counterTo50 * 2;


        //return  >= 0.495
                //&& ((double)leftSideParticlesCounter / totalParticles) <= 0.55;


    }
}
