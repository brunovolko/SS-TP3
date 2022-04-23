import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticlePressureData {
    public static void main(String[] args) throws Exception {
        List<Particle> particleList; //Auxiliar
        Environment environment;
        int auxCantParticlesLeft;
        double tc;
        Map<Integer,List<Double>> results = new HashMap<>();
        OutputStream os = new FileOutputStream("pressure-results.csv");
        try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            Config config = new Config("static_input.txt");

            double aux = config.getParticleVelocity();

            //pw.println("presion");
            //pw.println(String.format("N particles: %d,Velocity: %g",config.getNumberParticles(),config.getParticleVelocity() ));

            int limit = 8;
            for(int iteration = 0;iteration<limit;iteration++) {
                config.setParticleVelocity(aux+iteration*0.01);
                System.out.println(config.getParticleVelocity());
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
                    double pressureAnalisisTime = totalTimePassed - environment.timeTo50;
                    double totalPerimeter = config.getHeightLength() * 2 + config.getWidthLength() * 2 + (config.getHeightLength() - config.getGrooveLength());

                    double pressure = (environment.getTotalImpulse() / pressureAnalisisTime) / totalPerimeter;
                    results.putIfAbsent(iteration, new ArrayList<>());
                    results.get(iteration).add(pressure);
                    System.out.println("round complete");

                }



            }
            System.out.println(results);

            for(int i =1;i<=limit-1;i++)
                pw.printf("p%d,",i);
            pw.println("p"+limit);

            for (int i = 0; i <limit ; i++) {
                for(int j=0;j<limit-1;j++){
                    pw.print(results.get(j).get(i)+",");
                }
                pw.println(results.get(limit-1).get(i));
            }





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
