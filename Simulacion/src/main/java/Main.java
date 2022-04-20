import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static int t = 0;

    public static void main(String[] args) {
        File file = new File("dynamic_output.txt");
        try(PrintWriter pw = new PrintWriter(file)) {
            Config config = new Config("static_input.txt", "dynamic_input.txt");
            Environment environment = new Environment(config.getParticles(), config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());


            saveState(pw, 0, environment.getState());
            double tc;
            double totalTimePassed=0;
            while(!environment.stopCriteria()) {
                environment.recalculateCollisions(environment.getParticlesToRecalculate());
                tc = environment.timeToNextCollision();
                environment.evolve(tc);
                totalTimePassed+=tc;
                saveState(pw, totalTimePassed, environment.getState());
                environment.calculateNewVelocities();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
//        timetoEqualization(args);
    }

    public static void timetoEqualization(String[] args) {
        File results= new File("results.txt");
        try(PrintWriter pw = new PrintWriter(results)) {
            for (int i = 0; i < 10; i++) {
                ParticlesGenerator.main(args);
                Config config = new Config("static_input.txt", "dynamic_input.txt");
                if (i==0){
                    pw.println("Number of particles "+config.getNumberParticles());
                    pw.println("Groove size "+config.getGrooveLength());
                }
                Environment environment = new Environment(config.getParticles(), config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());
                double tc;
                double totalTimePassed=0;
                while(!environment.stopCriteria()) {
                    environment.recalculateCollisions(environment.getParticlesToRecalculate());
                    tc = environment.timeToNextCollision();
                    environment.evolve(tc);
                    totalTimePassed+=tc;
                    environment.calculateNewVelocities();
                }
                pw.println(totalTimePassed);
                System.out.println("Round "+i+" complete");

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }

    private static void saveState(PrintWriter pw, double tc, List<Particle> particles) {
        pw.println(particles.size());
        pw.println("Lattice=\"0.24 0.0 0.0 0.0 0.09 0.0 0.0 0.0 0.0\" Origin=\"0.0 0.0 0.0\" Properties=pos:R:2:velo:R:2:radius:R:1:mass:R:1");
        for(Particle particle : particles)
            pw.println(particle.getX() + " " + particle.getY() + " " + particle.getVx() + " " + particle.getVy()+" "+particle.getRadius()+" "+particle.getMass());
        t++;
    }
}
