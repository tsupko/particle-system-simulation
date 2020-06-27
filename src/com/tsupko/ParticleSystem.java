/*
 * Copyright (c) All rights reserved.
 */
package com.tsupko;

import libraries.StdDraw;

import java.awt.*;
import java.util.PriorityQueue;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 * @since July 22, 2016
 * @version 1.1.0
 */
public class ParticleSystem {
    private PriorityQueue<Event> pq = null; // the priority queue of the events (collisions)
    private final Particle[] particles; // the array of the particles in the system
    private double time = 0.0; // the starting moment of time

    private ParticleSystem(Particle[] particles) {
        this.particles = particles.clone(); // defensive copy
    }

    /**
     * Class {@code Event} serves as a high-level abstraction of any collision in the system.
     * Events are compared by the time they occur.
     * If the variables {@code countA} and {@code countB} in {@code isValid} method
     * differ from those created in the constructor, the events are invalidated,
     * meaning that the particle has changed its trajectory during the time between those two calls,
     * and cannot hit either the walls or another particles as was planned in the time of creation.
     */
    private static class Event implements Comparable<Event> {
        private final double time;
        private final Particle a;
        private final Particle b;
        private final int countA;
        private final int countB;

        private Event(double time, Particle a, Particle b) {
            this.time = time;
            this.a = a;
            this.b = b;
            if (a != null) countA = a.getCount(); else countA = -1;
            if (b != null) countB = b.getCount(); else countB = -1;
        }

        @Override
        public int compareTo(Event that) {
            return Double.compare(this.time, that.time);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        private boolean isValid() {
            if (a != null && a.getCount() != countA) return false;
            return b == null || b.getCount() == countB;
        }
    }

    /**
     * Method {@code predict(Particle, double)} serves for filling the priority queue with the predicted collisions
     * between a given particle {@code a} and either the walls or any another particle.
     * If the event cannot be foreseen within the provided time {@code limit}, it is not included into the queue.
     *
     * @param a the particle to predict the events for
     * @param limit the time limit within which the events occur that should be placed onto the priority queue
     */
    private void predict(Particle a, double limit) {
        if (a == null) return;
        for (Particle particle : particles) {
            double dt = a.timeToHit(particle);
            if (time + dt < limit) pq.add(new Event(time + dt, a, particle));
        }
        double dtX = a.timeToHitVerticalWall();
        double dtY = a.timeToHitHorizontalWall();
        if (time + dtX < limit) pq.add(new Event(time + dtX, a, null));
        if (time + dtY < limit) pq.add(new Event(time + dtY, null, a));
    }

    /**
     * Method {@code redraw(double)} serves for redrawing all the particles when the associated call is issued,
     * or the ordinary redrawing is needed, specified by the {@code frequency} instance variable.
     * Additionally, the temperature of the system is recalculated with every call to this method.
     *
     * @param limit the time limit - if the current moment exceeds this parameter, the simulation stops
     */
    private void redraw(double limit) {
        StdDraw.clear();
        for (Particle particle : particles) {
            particle.draw();
        }
        StdDraw.show(20);
        if (time < limit) {
            System.out.println(temperature());
            // the frequency of redrawing - the greater the number, the slower the simulation
            double frequency = 0.5;
            pq.add(new Event(time + 1.0 / frequency, null, null));
        } else System.exit(0);
    }

    /**
     * Method {@code simulate(double)} is the main method of the simulation.
     * Initially, it fills the priority queue with the predicted collisions.
     * For each event in the queue, it is checked for validity, the moment of time advances,
     * and the bouncing off occurs, changing the positions and velocities of the particles.
     * Finally, new predictions are inserted onto the priority queue.
     *
     * @param limit the time limit - the duration of the simulation
     */
    private void simulate(double limit) {
        pq = new PriorityQueue<>();
        for (Particle particle : particles) {
            predict(particle, limit);
        }
        pq.add(new Event(time, null, null));
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (!event.isValid()) continue;
            Particle a = event.a;
            Particle b = event.b;
            for (Particle particle : particles) {
                particle.move(event.time - time);
            }
            time = event.time;
            if (a != null && b != null) a.bounceOff(b);
            else if (a != null) a.bounceOffVerticalWall();
            else if (b != null) b.bounceOffHorizontalWall();
            else redraw(limit);
            predict(a, limit);
            predict(b, limit);
        }
    }

    /**
     * Method {@code temperature()} serves as the auxiliary method for calculating the temperature of the system.
     *
     * @return the temperature of the system
     */
    private double temperature() {
        double temperature = 0.0;
        for (Particle particle : particles) {
            temperature += particle.temperature();
        }
        return temperature / particles.length; // calculate temperature of the system
    }

    /**
     * The entry point of the program.
     *
     * @param args the number of particles to generate (recommended range: 1 to 400)
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            int number = Integer.parseInt(args[0]);
            Particle[] particles = new Particle[number + 1];
            // regular particles
            for (int i = 0; i < number; i++) {
                particles[i] = new Particle(); // create a random particle
            }
            // massive Brownian particle
            particles[number] = new Particle(0.5, 0.5, 0.0, 0.0, 0.1, 500, Color.RED);
            ParticleSystem system = new ParticleSystem(particles);
            system.simulate(Double.parseDouble(args[1])); // time to run simulation in seconds
        }
    }
}
