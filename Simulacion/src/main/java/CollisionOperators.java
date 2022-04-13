import java.util.List;

public class CollisionOperators {
    public static void particleToParticle(List<Particle> particles, Particle particle1, Particle particle2) {
        int idx1 = particles.indexOf(particle1), idx2 = particles.indexOf(particle2);

//       //TODO calcular velocidades post choque elastico

        particles.set(idx1, particle1);
        particles.set(idx2, particle2);

    }
}
