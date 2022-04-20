import javafx.util.Pair;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Statistics {
    private static final int runsNum = 20;
    private static final double frame = 2;
    public static void main(String[] args) {
        List<Pair<Double, Pair<Double, Double>>> outputRegistries = new ArrayList<>(); //Time, Percentage
        List<HashMap<Double, Double>> runs = new ArrayList<>(runsNum);

        List<Particle> particleList; //Auxiliar
        Environment environment;
        int auxCantParticlesLeft;
        double tc, avg, dev;
        try(PrintWriter pw = new PrintWriter("evolucion_distribucion.csv")) {
            Config config = new Config("static_input.txt");

            for (int i = 0; i < runsNum; i++) {

                particleList = ParticlesGenerator.generate(config);
                environment = new Environment(particleList, config.getWidthLength(), config.getHeightLength(), config.getGrooveLength());
                runs.add(new HashMap<>());
                runs.get(i).put(0.0, 1.0);
                double totalTimePassed = 0;
                while (!environment.stopCriteria()) {
                    environment.recalculateCollisions(environment.getParticlesToRecalculate());
                    tc = environment.timeToNextCollision();
                    environment.evolve(tc);
                    totalTimePassed += tc;
                    environment.calculateNewVelocities();
                    auxCantParticlesLeft = environment.getCantParticlesLeftSide();
                    runs.get(i).put(Math.ceil(totalTimePassed/frame)*frame, (double) auxCantParticlesLeft / config.getNumberParticles());

                }
            }


            //Llenamos agujeros
            double auxMax;
            for (HashMap<Double, Double> run : runs) {
                auxMax = run.keySet().stream().mapToDouble(v -> v).max().orElseThrow(NoSuchFieldException::new);
                for (Double i = frame; i <= auxMax; i += frame) {
                    if (!run.containsKey(i)) {
                        run.put(i, run.get(i - frame));
                    }
                }
            }
            List<Double> values;
            for (double t = 0.0; ; t += frame) { //Iteramos por cada t
                values = new ArrayList<>();
                for (HashMap<Double, Double> run : runs)
                    if (run.containsKey(t))
                        values.add(run.get(t));
                if (values.size() == 0 && t != 0)
                    break;
                avg = values.stream().mapToDouble(v -> v).average().orElseThrow(Exception::new);

                double finalAvg = avg;
                dev = Math.sqrt(values.stream()
                        .map(i -> i - finalAvg)
                        .map(i -> i * i)
                        .mapToDouble(i -> i).average().orElseThrow(Exception::new));

                outputRegistries.add(new Pair<>(t, new Pair<>(avg, dev)));
            }

            pw.println("t,promedio,desvio");
            for (Pair<Double, Pair<Double, Double>> entry : outputRegistries)
                pw.println(entry.getKey() + "," + entry.getValue().getKey() + "," + entry.getValue().getValue());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }



}
