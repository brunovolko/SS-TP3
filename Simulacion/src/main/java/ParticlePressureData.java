import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ParticlePressureData {
    public static void main(String[] args) throws Exception {
        List<Particle> particleList; //Auxiliar
        Environment environment;
        int auxCantParticlesLeft;
        double tc;
        OutputStream os = new FileOutputStream("pressure-results.csv");
        try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            Config config = new Config("static_input.txt");


            pw.println("presion");
            pw.println(String.format("N particles: %d,Velocity: %g",config.getNumberParticles(),config.getParticleVelocity() ));

            for (int i = 0; i < 10; i++) {
                particleList = ParticlesGenerator.generate(config);
                environment = new Environment(particleList, config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());

                double totalTimePassed = 0;
                while (!environment.stopCriteria()) {
                    environment.recalculateCollisions(environment.getParticlesToRecalculate());
                    tc = environment.timeToNextCollision();
                    environment.evolve(tc);
                    totalTimePassed += tc;
                    environment.addImpulse();
                    environment.calculateNewVelocities();
                }
                double pressureAnalisisTime=totalTimePassed-environment.timeTo50;
                double totalPerimeter=config.getHeightLength()*2+config.getWidthLength()*2+(config.getHeightLength()-config.getGrooveLength());

                double pressure=(environment.getTotalImpulse()/pressureAnalisisTime)/totalPerimeter;
                pw.println(pressure);
                System.out.println("round complete");

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
