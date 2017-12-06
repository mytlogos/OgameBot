package ogamebot.units.building;

import ogamebot.comp.RequireAble;
import ogamebot.units.UnitType;
import ogamebot.units.astroObjects.CelestialBody;
import ogamebot.units.astroObjects.Moon;
import ogamebot.units.astroObjects.Planet;

/**
 *
 */
public interface BuildingType extends UnitType<Building>, RequireAble {
    static BuildingType[] getTypes(CelestialBody body) {
        if (body == null || body instanceof Planet) {
            return PlanetBuilding.values();
        } else if (body instanceof Moon) {
            return MoonBuilding.values();
        }
        return new BuildingType[0];
    }
}