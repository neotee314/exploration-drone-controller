package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;

@Service
@RequiredArgsConstructor
public class MapVisualizationService {

    private final PlanetRepository planetRepository;

    public String generateMapHtml() {
        var planets = planetRepository.findAll();

        if (planets.isEmpty()) {
            return """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Planet Map</title>
                    </head>
                    <body>
                        <h1>No planets found</h1>
                    </body>
                    </html>
                    """;
        }

        var positions = calculatePositions(planets);
        return generateHtml(planets, positions);
    }

    private Map<Planet, Position> calculatePositions(List<Planet> planets) {
        var positions = new HashMap<Planet, Position>();

        var startPlanet = planets.stream()
                .filter(Planet::isOrigin)
                .findFirst()
                .orElse(planets.getFirst());

        var queue = new ArrayDeque<Planet>();

        positions.put(startPlanet, new Position(0, 0));
        queue.add(startPlanet);

        while (!queue.isEmpty()) {
            var current = queue.poll();
            var currentPosition = positions.get(current);

            addPosition(current.getNorth(), new Position(currentPosition.x(), currentPosition.y() + 1), positions, queue);
            addPosition(current.getSouth(), new Position(currentPosition.x(), currentPosition.y() - 1), positions, queue);
            addPosition(current.getEast(), new Position(currentPosition.x() + 1, currentPosition.y()), positions, queue);
            addPosition(current.getWest(), new Position(currentPosition.x() - 1, currentPosition.y()), positions, queue);
        }

        return positions;
    }

    private void addPosition(Planet planet, Position position, Map<Planet, Position> positions, Queue<Planet> queue) {
        if (planet == null || positions.containsKey(planet)) {
            return;
        }

        positions.put(planet, position);
        queue.add(planet);
    }

    private String generateHtml(List<Planet> planets, Map<Planet, Position> positions) {
        var minX = positions.values().stream().mapToInt(Position::x).min().orElse(0);
        var maxX = positions.values().stream().mapToInt(Position::x).max().orElse(0);
        var minY = positions.values().stream().mapToInt(Position::y).min().orElse(0);
        var maxY = positions.values().stream().mapToInt(Position::y).max().orElse(0);

        var cellSize = 260;
        var nodeWidth = 190;
        var nodeHeight = 130;
        var padding = 180;

        var width = (maxX - minX + 1) * cellSize + padding * 2;
        var height = (maxY - minY + 1) * cellSize + padding * 2;

        var svg = new StringBuilder();

        svg.append("""
                <svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d">
                """.formatted(width, height, width, height));

        svg.append("""
                <defs>
                    <marker id="arrow" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto" markerUnits="strokeWidth">
                        <path d="M0,0 L0,6 L9,3 z" fill="#94a3b8"/>
                    </marker>
                </defs>
                """);

        appendConnections(svg, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);
        appendPlanets(svg, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);

        svg.append("</svg>");

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Exploration Drone Planet Map</title>

                    <style>
                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            background: #020617;
                            color: #f8fafc;
                            font-family: Arial, sans-serif;
                        }

                        .header {
                            padding: 24px 32px;
                            background: #0f172a;
                            border-bottom: 1px solid #334155;
                        }

                        .header h1 {
                            margin: 0 0 8px 0;
                            font-size: 26px;
                        }

                        .header p {
                            margin: 0;
                            color: #94a3b8;
                        }

                        .map {
                            padding: 30px;
                            overflow: auto;
                        }

                        svg {
                            display: block;
                            background: #0f172a;
                            border: 1px solid #334155;
                            border-radius: 16px;
                            min-width: max-content;
                        }

                        .planet {
                            fill: #1e293b;
                            stroke: #64748b;
                            stroke-width: 3;
                        }

                        .station {
                            fill: #451a03;
                            stroke: #f59e0b;
                            stroke-width: 4;
                        }

                        .planet-name {
                            fill: #f8fafc;
                            font-size: 15px;
                            font-weight: bold;
                        }

                        .planet-info {
                            fill: #cbd5e1;
                            font-size: 12px;
                        }

                        .connection {
                            stroke: #64748b;
                            stroke-width: 4;
                            marker-end: url(#arrow);
                        }

                        .direction {
                            fill: #f8fafc;
                            font-size: 13px;
                            font-weight: bold;
                        }

                        .legend {
                            padding: 20px 32px;
                            background: #0f172a;
                            border-top: 1px solid #334155;
                            color: #cbd5e1;
                        }
                    </style>
                </head>

                <body>
                    <div class="header">
                        <h1>Exploration Drone Planet Map</h1>
                        <p>%d planets generated</p>
                    </div>

                    <div class="map">
                        %s
                    </div>

                    <div class="legend">
                        <strong>Directions:</strong>
                        N = North |
                        S = South |
                        E = East |
                        W = West
                    </div>
                </body>
                </html>
                """.formatted(planets.size(), svg);
    }

    private void appendConnections(StringBuilder svg, Map<Planet, Position> positions, int minX, int maxY, int cellSize, int padding, int nodeWidth, int nodeHeight) {
        var processed = new HashSet<String>();

        for (var entry : positions.entrySet()) {
            var source = entry.getKey();
            var position = entry.getValue();

            var sourceX = getX(position, minX, cellSize, padding);
            var sourceY = getY(position, maxY, cellSize, padding);

            appendConnection(svg, processed, source, source.getNorth(), sourceX, sourceY, CompassPoint.NORTH, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);
            appendConnection(svg, processed, source, source.getSouth(), sourceX, sourceY, CompassPoint.SOUTH, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);
            appendConnection(svg, processed, source, source.getEast(), sourceX, sourceY, CompassPoint.EAST, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);
            appendConnection(svg, processed, source, source.getWest(), sourceX, sourceY, CompassPoint.WEST, positions, minX, maxY, cellSize, padding, nodeWidth, nodeHeight);
        }
    }

    private void appendConnection(StringBuilder svg, Set<String> processed, Planet source, Planet target, int sourceX, int sourceY, CompassPoint direction, Map<Planet, Position> positions, int minX, int maxY, int cellSize, int padding, int nodeWidth, int nodeHeight) {
        if (target == null || !positions.containsKey(target)) {
            return;
        }

        var key = source.getId().value() + "-" + target.getId().value();

        if (!processed.add(key)) {
            return;
        }

        var targetPosition = positions.get(target);
        var targetX = getX(targetPosition, minX, cellSize, padding);
        var targetY = getY(targetPosition, maxY, cellSize, padding);

        var start = calculateEdgePoint(sourceX, sourceY, direction, nodeWidth, nodeHeight);
        var end = calculateEdgePoint(targetX, targetY, direction.oppositeDirection(), nodeWidth, nodeHeight);

        var labelX = (sourceX + targetX) / 2;
        var labelY = (sourceY + targetY) / 2;

        svg.append("""
                <line class="connection" x1="%d" y1="%d" x2="%d" y2="%d"/>
                <text class="direction" x="%d" y="%d" text-anchor="middle">%s</text>
                """.formatted(start.x(), start.y(), end.x(), end.y(), labelX, labelY - 8, direction));
    }

    private Position calculateEdgePoint(int centerX, int centerY, CompassPoint direction, int width, int height) {
        return switch (direction) {
            case NORTH -> new Position(centerX, centerY - height / 2);
            case SOUTH -> new Position(centerX, centerY + height / 2);
            case EAST -> new Position(centerX + width / 2, centerY);
            case WEST -> new Position(centerX - width / 2, centerY);
        };
    }

    private void appendPlanets(StringBuilder svg, Map<Planet, Position> positions, int minX, int maxY, int cellSize, int padding, int nodeWidth, int nodeHeight) {
        for (var entry : positions.entrySet()) {
            var planet = entry.getKey();
            var position = entry.getValue();

            var centerX = getX(position, minX, cellSize, padding);
            var centerY = getY(position, maxY, cellSize, padding);

            var x = centerX - nodeWidth / 2;
            var y = centerY - nodeHeight / 2;

            var cssClass = planet.isOrigin() ? "planet station" : "planet";

            svg.append("""
                    <rect class="%s" x="%d" y="%d" width="%d" height="%d" rx="14"/>

                    <text class="planet-name" x="%d" y="%d" text-anchor="middle">%s</text>

                    <text class="planet-info" x="%d" y="%d" text-anchor="middle">ID: %s</text>

                    <text class="planet-info" x="%d" y="%d" text-anchor="middle">Type: %s</text>

                    <text class="planet-info" x="%d" y="%d" text-anchor="middle">Uranium: %s</text>

                    <text class="planet-info" x="%d" y="%d" text-anchor="middle">Visited: %s</text>

                    <text class="planet-info" x="%d" y="%d" text-anchor="middle">Mined: %s</text>
                    """.formatted(
                    cssClass,
                    x,
                    y,
                    nodeWidth,
                    nodeHeight,
                    centerX,
                    y + 25,
                    escape(planet.getName()),
                    centerX,
                    y + 47,
                    escape(planet.getId().value()),
                    centerX,
                    y + 67,
                    planet.getPlanetType(),
                    centerX,
                    y + 87,
                    planet.getUranium().getAmount(),
                    centerX,
                    y + 107,
                    planet.isVisited(),
                    centerX,
                    y + 124,
                    planet.checkIfMined()
            ));
        }
    }

    private int getX(Position position, int minX, int cellSize, int padding) {
        return padding + (position.x() - minX) * cellSize + cellSize / 2;
    }

    private int getY(Position position, int maxY, int cellSize, int padding) {
        return padding + (maxY - position.y()) * cellSize + cellSize / 2;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record Position(int x, int y) {
    }
}