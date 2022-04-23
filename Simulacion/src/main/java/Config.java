import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {
    private double particleRadius;
    private double mass;
    private double widthLength;
    private double heightLength;
    private double grooveLength;
    private int numberParticles;

    private double particleVelocity;


    List<Particle> particles = new ArrayList<>();

    private void parseStaticInput(String staticInputFilename) throws Exception {
        File staticInputFile = new File(staticInputFilename);
        Scanner staticReader = new Scanner(staticInputFile);

        if(staticReader.hasNextLine())
            particleRadius = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Particle radius must be in line 1");

        if(staticReader.hasNextLine())
            mass = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Mass must be in line 2");

        if(staticReader.hasNextLine())
            widthLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Width must be in line 3");

        if(staticReader.hasNextLine())
            heightLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Height must be in line 4");

        if(staticReader.hasNextLine())
            grooveLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Groove length must be in line 5");

        if(staticReader.hasNextLine())
            numberParticles = Integer.parseInt(staticReader.nextLine());
        else
            throw new Exception("Number of particles must be in line 6");

        if(staticReader.hasNextLine())
            particleVelocity = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Particle Velocity must be in line 7");

        staticReader.close();


    }


    public Config (String staticInputFilename, String dynamicInputFilename) throws Exception {

        parseStaticInput(staticInputFilename);

        File dynamicInputFile = new File(dynamicInputFilename);
        Scanner dynamicReader = new Scanner(dynamicInputFile);


        if(dynamicReader.hasNextLine()) {
            dynamicReader.nextLine(); // t0
        } else {
            throw new Exception("t0 not found");
        }

        String row;
        String[] parts;
        while(dynamicReader.hasNextLine()) {
            row = dynamicReader.nextLine();
            parts = row.split(" ");

            particles.add(new Particle(Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    particleRadius,
                    mass,
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    false));
        }
        dynamicReader.close();
    }



    public Config (String staticInputFilename) throws Exception {
        parseStaticInput(staticInputFilename);
    }

    public double getParticleRadius() {
        return particleRadius;
    }

    public double getMass() {
        return mass;
    }

    public double getWidthLength() {
        return widthLength;
    }

    public double getHeightLength() {
        return heightLength;
    }

    public double getGrooveLength() {
        return grooveLength;
    }

    public int getNumberParticles() {
        return numberParticles;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public double getParticleVelocity() {
        return particleVelocity;
    }
    public void setParticleVelocity(double velocity) {
        this.particleVelocity=velocity;
    }
}
