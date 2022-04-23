import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class TimeToBalanceData {
    private static final int runsNum = 20;
    public static void main(String[] args) {

        List<Particle> particleList; //Auxiliar
        Environment environment;
        double tc;
        try(PrintWriter pw = new PrintWriter("time_to_balance.csv")) {
            Config config = new Config("static_input.txt");

            pw.println("tiempo");

            for (int i = 0; i < runsNum; i++) {

                particleList = ParticlesGenerator.generate(config);
                environment = new Environment(particleList, config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());

                double totalTimePassed = 0;
                while (!environment.stopCriteria()) {
                    environment.recalculateCollisions(environment.getParticlesToRecalculate());
                    tc = environment.timeToNextCollision();
                    environment.evolve(tc);
                    totalTimePassed += tc;
                    environment.calculateNewVelocities();
                }
                pw.println(totalTimePassed);
                System.out.println(i+1); //TODO sacar print
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
