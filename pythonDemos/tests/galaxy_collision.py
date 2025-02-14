import sys
import os

thisDir = os.path.dirname(os.path.abspath(__file__))
upDir = os.path.dirname(thisDir)
sys.path.append(upDir)


from body import Body
from simulation import Simulation

import numpy as np

import sys
sys.setrecursionlimit(3000)

'''
Two suns, each with 1000 particles orbiting them at orbit velocity.
start in bottom left and top right corners
'''

def main():
    bodies = []
    centre = np.array([Simulation.WIDTH / 2, Simulation.HEIGHT / 2])
    SUN1: Body = Body(0, 0, 0.0, 0.0, 10**13, (255, 0, 0))
    bodies.append(SUN1)
    widthFactor = min(Simulation.WIDTH / 2, Simulation.HEIGHT / 2)
    rings = [widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875]
    colors = [(255, 255, 255), (255, 255, 0), (0, 255, 255), (255, 0, 255), (0, 255, 0), (0, 0, 255)]

    nrings = len(rings)
    randomDistribution = sorted(np.random.randn(1000)) # sort it first to make collisions happen quicker
    for i in range(100):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 15 * randomDistribution[i]
        x = radius * np.cos(theta)
        y = radius * np.sin(theta)
        # x = np.float16(x)
        # y = np.float16(y)
        # make velocity perpendicular to the radius
        v = np.sqrt(SUN1.G * SUN1._mass / radius**3)
        # v=0.0
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        # vx = np.float16(vx)
        # vy = np.float16(vy)
        body = Body(x, y, vx, vy, 1, colors[i%nrings])
        bodies.append(body)


    SUN2: Body = Body(Simulation.WIDTH, Simulation.HEIGHT, 0.0, 0.0, 10**13, (255, 0, 0))
    bodies.append(SUN2)
    widthFactor = min(Simulation.WIDTH / 2, Simulation.HEIGHT / 2)
    rings = [widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875]
    colors = [(255, 255, 255), (255, 255, 0), (0, 255, 255), (255, 0, 255), (0, 255, 0), (0, 0, 255)]

    nrings = len(rings)
    randomDistribution = sorted(np.random.randn(1000)) # sort it first to make collisions happen quicker
    for i in range(100):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 15 * randomDistribution[i]
        x = Simulation.WIDTH + radius * np.cos(theta)
        y = Simulation.HEIGHT + radius * np.sin(theta)
        # x = np.float16(x)
        # y = np.float16(y)
        # make velocity perpendicular to the radius
        v = np.sqrt(SUN2.G * SUN2._mass / radius**3)
        # v=0.0
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        # vx = np.float16(vx)
        # vy = np.float16(vy)
        body = Body(x, y, vx, vy, 1, colors[i%nrings])
        bodies.append(body)

    sim = Simulation(bodies,dt=1,radius=Simulation.WIDTH)
    sim.simulate(Theta=1,drawBarnesHuts=False,frames_per_second=60)

if __name__ == "__main__":
    main()
