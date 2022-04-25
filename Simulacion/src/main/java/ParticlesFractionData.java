import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
Registra la evolución de la fracción de particulas a izquierda.
Lee la configuracion de static_input.txt
Genera evolucion_distribucion.csv
 */

public class ParticlesFractionData {
    public static void main(String[] args) throws FileNotFoundException {

        List<Particle> particleList; //Auxiliar
        Environment environment;
        int auxCantParticlesLeft;
        double tc;
        OutputStream os = new FileOutputStream("evolucion_distribucion.csv");
        try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            Config config = new Config("static_input.txt");


            particleList = ParticlesGenerator.generate(config);
            environment = new Environment(particleList, config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());
            pw.println("t,fraccion");
            pw.println("0.0,1.0");

            double totalTimePassed = 0;
            while (!environment.stopCriteria()) {
                environment.recalculateCollisions(environment.getParticlesToRecalculate());
                tc = environment.timeToNextCollision();
                environment.evolve(tc);
                totalTimePassed += tc;
                environment.calculateNewVelocities();
                auxCantParticlesLeft = environment.getCantParticlesLeftSide();
                pw.println(totalTimePassed+","+(double) auxCantParticlesLeft / config.getNumberParticles());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }



}
