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
            double tc=0;
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
            System.out.println(ex.getMessage());
        }
    }

    private static void saveState(PrintWriter pw, double tc, List<Particle> particles) {
        pw.println("t"+t + " " + tc);
        for(Particle particle : particles)
            pw.println(particle.getX() + " " + particle.getY() + " " + particle.getVx() + " " + particle.getVy());
        t++;
    }
}
