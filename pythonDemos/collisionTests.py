from body import Body
from tree import Tree
from quad import Quad
import pygame
import numpy as np
# from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
# import os
import sys
from simulation import Simulation

sys.setrecursionlimit(3000)


if __name__ == "__main__":
    ## make a solar system
    np.random.seed(10)


    # # two particles, stationary initially
    bodies = []
    bodies.append(Body(1, 200, 50, 0, 1**12, (255, 0, 0)))
    bodies.append(Body(799, 200, -150, 0, 1**12, (0, 255, 0)))

    # print(bodies)

    sim = Simulation(bodies, dt =1, radius=Simulation.WIDTH)
    sim.simulate(Theta = float('inf'), drawBarnesHuts = False, frames_per_second = 60)