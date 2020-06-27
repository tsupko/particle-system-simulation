# Particle System Simulation
Simulation of the system of the ideal gas particles, i.e. molecules or atoms.

## Usage

For compilation and execution, use the following command-line commands:

`$ javac com.tsupko.ParticleSystem.java`

`$ java com.tsupko.ParticleSystem N 10000`

where *N* is the number of particles to generate for the system,
and the second argument is the amount of time in seconds to run the simulation.

The recommended range for *N* is from a few to about 400.

## Output

The simulation creates a visual animation of the system of ideal gas with the particles colliding with each other,
and the walls according to the laws of elastic collisions and the laws of conservation of momenta and energy.

Additionally, you might want to redirect the output to the text file `temperature.txt` via the following:

`$ java com.tsupko.ParticleSystem N 10000 > temperature.txt`

The expected behavior is that the temperature should be almost constant, thus implying the system to be isothermal.
