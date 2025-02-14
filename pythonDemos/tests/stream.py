import sys
import os

thisDir = os.path.dirname(os.path.abspath(__file__))
upDir = os.path.dirname(thisDir)
sys.path.append(upDir)

from body import Body
from simulation import Simulation
import numpy as np
import argparse

np.random.seed(10)

def get_args():
    '''drawBarnesHuts:bool = False,frames_per_second:int = 60'''
    parser = argparse.ArgumentParser(description='Run the simulation')
    parser.add_argument('--drawBH', type=bool, default=False, help='Draw Barnes-Hut tree')
    parser.add_argument('--fps', type=int, default=60, help='Frames per second')

    return parser.parse_args()

def main():
    np.random.seed(10)
    bodies = []

    # Create a line of small particles, coming from the left, one after the other moving at a constant speed.
    # they need to have spacing between them.
    # It will look like a gun shotting bullets
    # but obviously I haven't made the ability to spawn particles in yet.
    origin = (0,Simulation.HEIGHT/3)
    spacex = 5
    spacey = 0.01
    speed = 50
    mass = 0.00100
    n = 1000
    radius = 1
    for i in range(n):
        for j in range(10):
            x = origin[0] - i*(2*radius + spacex)
            y = origin[1] + j*(2*radius + spacey)
            b = Body(x, y, speed, 0, mass, (255, 0, 0))
            b.overRiddenRadius = radius
            bodies.append(b)

    # Initialize simulation
    args = get_args() 

    sim = Simulation(bodies, dt=0.1, radius=Simulation.WIDTH)
    sim.reCenter = False
    sim.gravitationalForce = False
    sim.collisionDetection = True
    sim.MASS_GROWTH = 0.1
    # sim.pruning = True
    # sim.localGravity = 0.5
    # sim.boundaryCollisions = True
    # drawBarnesHuts = args.drawBH
    # frames_per_second = args.fps
    sim.simulate(Theta=float('inf'), drawBarnesHuts=args.drawBH, frames_per_second=args.fps)

if __name__ == '__main__':
    main()
