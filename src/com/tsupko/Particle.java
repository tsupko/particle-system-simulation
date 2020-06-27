/*
 * Copyright (c) All rights reserved.
 */
package com.tsupko;

import libraries.StdDraw;
import libraries.StdRandom;

import java.awt.*;

/**
 * @author Alexander Tsupko (alexander.tsupko@outlook.com)
 * @since July 22, 2016
 * @version 1.1.0
 */
class Particle {
    private static final double BOLTZMANN = 1.3806503E-23;
    private static final int DIMENSION = 2;

    private double rx;
    private double ry;
    private double vx;
    private double vy;
    private final double radius;
    private final double mass;
    private final Color color;
    private int count;

    public Particle(double rx, double ry, double vx, double vy, double radius, double mass, Color color) {
        this.rx = rx;
        this.ry = ry;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
        this.color = color;
    }

    public Particle() {
        radius = 0.01;
        rx = StdRandom.uniform(0.0 + radius, 1.0 - radius);
        ry = StdRandom.uniform(0.0 + radius, 1.0 - radius);
        vx = StdRandom.uniform(-0.005, 0.005);
        vy = StdRandom.uniform(-0.005, 0.005);
        mass = 0.5;
        color = Color.BLACK;
    }

    public int getCount() {
        return count;
    }

    public void move(double dt) {
        rx += vx * dt;
        ry += vy * dt;
    }

    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(rx, ry, radius);
    }

    public double timeToHit(Particle that) {
        if (this == that) return Double.POSITIVE_INFINITY;
        double dx = that.rx - this.rx;
        double dy = that.ry - this.ry;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double drdv = dx * dvx + dy * dvy;
        if (drdv > 0) return Double.POSITIVE_INFINITY;
        double drdr = dx * dx + dy * dy;
        double dvdv = dvx * dvx + dvy * dvy;
        double sigma = this.radius + that.radius;
        double d = (drdv * drdv) - dvdv * (drdr - sigma * sigma);
        if (d < 0) return Double.POSITIVE_INFINITY;
        return -(drdv + Math.sqrt(d)) / dvdv;
    }

    public double timeToHitVerticalWall() {
        if (vx > 0) return (1.0 - radius - rx) / vx;
        else if (vx < 0) return (radius - rx) / vx;
        else return Double.POSITIVE_INFINITY;
    }

    public double timeToHitHorizontalWall() {
        if (vy > 0) return (1.0 - radius - ry) / vy;
        else if (vy < 0) return (radius - ry) / vy;
        else return Double.POSITIVE_INFINITY;
    }

    public void bounceOff(Particle that) {
        double dx  = that.rx - this.rx;
        double dy  = that.ry - this.ry;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double drdv = dx * dvx + dy * dvy; // dr dot dv
        double dist = this.radius + that.radius; // distance between particle centers at collision
        // magnitude of normal force
        double magnitude = 2 * this.mass * that.mass * drdv / ((this.mass + that.mass) * dist);
        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;
        // update velocities according to normal force
        this.vx += fx / this.mass;
        this.vy += fy / this.mass;
        that.vx -= fx / that.mass;
        that.vy -= fy / that.mass;
        // update collision counts
        this.count++;
        that.count++;
    }

    public void bounceOffVerticalWall() {
        vx = -vx;
        count++;
    }

    public void bounceOffHorizontalWall() {
        vy = -vy;
        count++;
    }

    public double temperature() {
        return mass * (vx * vx + vy * vy) / (DIMENSION * BOLTZMANN);
    }
}
