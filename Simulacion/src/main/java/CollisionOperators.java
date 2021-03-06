import java.util.List;

public class CollisionOperators {
    public static void particleToParticle(List<Particle> particles, Particle particle1, Particle particle2) {
        int idx1 = particles.indexOf(particle1), idx2 = particles.indexOf(particle2);

        double sigma = particle1.getRadius() + particle2.getRadius();
        double deltaVdeltaR = (particle2.getVx() - particle1.getVx())*(particle2.getX()-particle1.getX()) + (particle2.getVy() - particle1.getVy())*(particle2.getY()-particle1.getY());
        double J = (2*particle1.getMass()*particle2.getMass()*deltaVdeltaR) / (sigma*(particle1.getMass()+particle2.getMass()));
        double Jx = (J * (particle2.getX() - particle1.getX()))/sigma;
        double Jy = (J * (particle2.getY() - particle1.getY()))/sigma;

        particle1.setVx(particle1.getVx() + Jx/particle1.getMass());
        particle1.setVy(particle1.getVy() + Jy/particle1.getMass());
        particle2.setVx(particle2.getVx() - Jx/particle2.getMass());
        particle2.setVy(particle2.getVy() - Jy/particle2.getMass());

        particles.set(idx1, particle1);
        particles.set(idx2, particle2);

    }

    public static void particleToFixedParticle(List<Particle> particles, Particle particle, Particle fixedParticle) {

        int idx1 = particles.indexOf(particle);

        double d = Math.sqrt(Math.pow(particle.getX() - fixedParticle.getX(), 2) + Math.pow(particle.getY() - fixedParticle.getY(), 2));
        double cosAlpha = (particle.getX()-fixedParticle.getX())/ d;
        double sinAlpha = (particle.getY()-fixedParticle.getY())/ d;

        double nextVx = particle.getVx()*(-1*Math.pow(cosAlpha,2)+1*Math.pow(sinAlpha,2)) +
                particle.getVy()*(-2*sinAlpha*cosAlpha);
        double nextVy = particle.getVx()*(-2*sinAlpha*cosAlpha) +
                particle.getVy()*(-1*Math.pow(sinAlpha,2)+1*Math.pow(cosAlpha,2));

        particle.setVx(nextVx);
        particle.setVy(nextVy);

        particles.set(idx1,particle);


    }
    public static void particleToWall(List<Particle> particles,Particle particle, Wall wall) {
        int index = particles.indexOf(particle);
        switch (wall.getDirection()){
            case LEFT:
            case RIGHT:
                particle.setVx(-particle.getVx());
                break;
            case LOWER:
            case UPPER:
                particle.setVy(-particle.getVy());
                break;
            case LOWER_GROOVE:
            case UPPER_GROOVE:
                particle.setVx(-particle.getVx());
                break;
        }
        particles.set(index, particle);
    }
}
