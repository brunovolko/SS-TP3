import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesGenerator {
    private static boolean noColitions(List<Particle> particles, Particle newParticle) {
        for (Particle particle : particles) {
            if(particle.isTouching(newParticle))
                return false;
        }
        return true;
    }

    private static void generateDynamicInput(List<Particle> particles) {
        File file = new File("dynamic_input.txt");
        try(PrintWriter pw = new PrintWriter(file)){
            pw.println("t0");
            int aux=0;
            for(Particle particle : particles){
                pw.println(particle.getX() + " " + particle.getY() + " " + particle.getVx() + " " + particle.getVy());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Config config = new Config("static_input.txt");
            //El 0,0 es abajo a la izquierda

            List<Particle> generatedParticles = generate(config);
            generateDynamicInput(generatedParticles);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Particle> generate(Config config) {
        double limiteSuperiorParaCentro = config.getHeightLength() - config.getParticleRadius();
        double limiteInferiorParaCentro = 0 + config.getParticleRadius();
        double limiteIzquierdoParaCentro = 0 + config.getParticleRadius();
        double limiteDerechoParaCentro = config.getWidthLength()/2 - config.getParticleRadius();

        List<Particle> generatedParticles = new ArrayList<>(config.getNumberParticles());
        Random random = new Random(System.currentTimeMillis());
        Particle newParticle;
        double newx, newy, degree, newvx, newvy;
        while(generatedParticles.size() < config.getNumberParticles()) {
            newx = random.nextDouble()*(limiteDerechoParaCentro - limiteIzquierdoParaCentro) + limiteIzquierdoParaCentro;
            newy = random.nextDouble()*(limiteSuperiorParaCentro - limiteInferiorParaCentro) + limiteInferiorParaCentro;
            degree = random.nextDouble()*2*Math.PI;
            newvx = 0.01 * Math.cos(degree);
            newvy = 0.01 * Math.sin(degree);
            newParticle = new Particle(newx, newy, config.getParticleRadius(), config.getMass(), newvx, newvy, false);
            if(noColitions(generatedParticles, newParticle))
                generatedParticles.add(newParticle);
        }
        return generatedParticles;
    }
}
