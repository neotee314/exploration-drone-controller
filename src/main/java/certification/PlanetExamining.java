package certification;


import com.neotee.exploration_drone_controller.domainprimitives.Uranium;

import java.util.UUID;

public interface PlanetExamining {
    /**
     * By calling this method by some external system (e.g. by HICCUP's sensor mapping system),
     * your system is told about newly detected planets.
     * @param planetId - the planet id where the neighbours have been examined.
     * @param northNeighbourOrNull - UUID of the northern neighbour, or
     *                               null if there is no northern neighbour, but a energy field instead.
     * @param eastNeighbourOrNull - see above
     * @param southNeighbourOrNull - see above
     * @param westNeighbourOrNull - see above
     */
    public void neighboursDetected( UUID planetId,
            UUID northNeighbourOrNull, UUID eastNeighbourOrNull,
            UUID southNeighbourOrNull, UUID westNeighbourOrNull );

    /**
     * By calling this method by some external system (e.g. by HICCUP's sensor mapping system),
     * your system is told about newly detected uranium.
     * @param planetId - the id of the planet id that has been examined for uranium.
     * @param uranium - the amount of uranium on this planet (can be 0).
     */
    void uraniumDetected( UUID planetId, Uranium uranium );
}
