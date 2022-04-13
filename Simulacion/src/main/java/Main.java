import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        try {
            Config config = new Config("static_input.txt", "dynamic_input.txt");
            Environment environment = new Environment(config.getParticles(), config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());
            saveState(environment.getState());
            environment.recalculateCollisions(environment.getState()); //With all the particles, 1st time
            while(!environment.stopCriteria()) {
                environment.evolve();
                saveState(environment.getState());
                
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void saveState(List<Particle> particles) {
        System.out.println(particles);
    }
}
