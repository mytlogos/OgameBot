package ogamebot.comp;

import ogamebot.concurrent.CountDown;
import ogamebot.units.astroObjects.Planet;
import ogamebot.units.building.Building;
import ogamebot.units.building.PlanetBuilding;
import ogamebot.units.units.Resource;
import tools.Condition;

import java.math.BigInteger;
import java.time.Duration;

/**
 *
 */
public class Production {
    private final Planet planet;

    private double met = 0;
    private double crys = 0;
    private double deut = 0;

    public Production(Planet planet) {
        Condition.check().nonNull(planet);
        this.planet = planet;
        CountDown.getWorker().getTimeProperty().addListener((observable, oldValue, newValue) -> recalculate());
    }


    public static Resource forDuration(Planet planet, int metLevel, int crysLevel, int deutLevel, Duration duration) {
        Condition.check().positive(metLevel, crysLevel, deutLevel);
        final long seconds = duration.getSeconds();

        final double metalMineProduction = PlanetBuilding.METALMINE.getProduction(planet, metLevel);
        final double crystalMineProduction = PlanetBuilding.CRYSTALMINE.getProduction(planet, crysLevel);
        final double deutSynthProduction = PlanetBuilding.DEUTSYNTH.getProduction(planet, deutLevel);

        final double producedMet = calcProducedResource(metalMineProduction, seconds);
        final double producedCrys = calcProducedResource(crystalMineProduction, seconds);
        final double producedDeut = calcProducedResource(deutSynthProduction, seconds);

        return new Resource((long) producedMet, (long) producedCrys, (long) producedDeut);
    }

    private static double calcProducedResource(double production, long seconds) {
        double factor = seconds / 3600d;
        return production * factor;
    }

    public Resource forDuration(Duration duration) {
        final long seconds = duration.getSeconds();

        final double producedMet = getProducedRess(seconds, PlanetBuilding.METALMINE);
        final double producedCrys = getProducedRess(seconds, PlanetBuilding.CRYSTALMINE);
        final double producedDeut = getProducedRess(seconds, PlanetBuilding.DEUTSYNTH);

        return new Resource((long) producedMet, (long) producedCrys, (long) producedDeut);
    }

    private double getProducedRess(long seconds, PlanetBuilding type) {
        final Building mine = planet.getBuilding(type);
        final double production = type.getProduction(planet, mine.getCounter());
        return calcProducedResource(production, seconds);
    }

    private void recalculate() {
        final double producedMet = getProducedRess(1, PlanetBuilding.METALMINE);
        final double producedCrys = getProducedRess(1, PlanetBuilding.CRYSTALMINE);
        final double producedDeut = getProducedRess(1, PlanetBuilding.DEUTSYNTH);

        this.met = met + producedMet;
        this.crys = crys + producedCrys;
        this.deut = deut + producedDeut;

        final Resource resource = planet.resourceProperty().get();

        BigInteger metal = resource.getMetal();
        BigInteger crystal = resource.getCrystal();
        BigInteger deuterium = resource.getDeut();

        boolean metUpdate = false;
        boolean crysUpdate = false;
        boolean deutUpdate = false;

        if (this.met > 1) {
            final long wholeValue = (long) this.met;
            metal = metal.add(BigInteger.valueOf(wholeValue));
            this.met = this.met - wholeValue;
            metUpdate = true;
        }

        if (this.crys > 1) {
            final long wholeValue = (long) this.crys;
            crystal = metal.add(BigInteger.valueOf(wholeValue));
            this.crys = this.crys - wholeValue;
            crysUpdate = true;
        }

        if (this.deut > 1) {
            final long wholeValue = (long) this.deut;
            deuterium = metal.add(BigInteger.valueOf(wholeValue));
            this.deut = this.deut - wholeValue;
            deutUpdate = true;
        }

        if (metUpdate || crysUpdate || deutUpdate) {
            planet.resourceProperty().set(new Resource(metal, crystal, deuterium));
        }
    }
}
