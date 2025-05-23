import sys
import os
thisDir = os.path.dirname(os.path.abspath(__file__))
upDir = os.path.dirname(thisDir)
sys.path.append(upDir)

from body import Body
from tree import Tree
from quad import Quad
from simulation import Simulation
import pygame
import numpy as np
import argparse

np.random.seed(10)

def get_args():
    '''drawBarnesHuts:bool = False,frames_per_second:int = 60'''
    parser = argparse.ArgumentParser(description='Run the simulation')
    parser.add_argument('--drawBH', type=bool, default=False, help='Draw Barnes-Hut tree')
    parser.add_argument('--fps', type=int, default=60, help='Frames per second')

    return parser.parse_args()

# def main():
#     #make one large particle, travelling from left to right.
#     #make 100 small particles uniformally dsitrbuted between 3*WIDTH/8 and 5*WIDTH/8
#     # and 3*HEIGHT/8 and 5*HEIGHT/8
#     np.random.seed(10)

#     Large = Body(0, 400, 100, 0, 10, (255, 0, 0))
#     bodies = [Large]

#     integer = 64

#     for i in range(100):
#         x = (integer/2-1)*Simulation.WIDTH/integer + np.random.random() * Simulation.WIDTH/integer*2
#         y = (integer/2-1)*Simulation.HEIGHT/integer + np.random.random() * Simulation.HEIGHT/integer*2
#         b = Body(x, y, 0, 0, 1, (0, 0, 255))
#         bodies.append(b)
    
#     sim = Simulation(bodies, dt =0.1, radius=Simulation.WIDTH)
#     sim.reCenter = False
#     sim.simulate(Theta = float('inf'), drawBarnesHuts = False, frames_per_second = 60)    

def main():
    # Create one large moving particle
    Large = Body(0, 350, 50, 0, 100, (255, 0, 0))
    Large.overRiddenRadius = 10
    bodies = [Large]

    # Create 5 small squares in a horizontal line
    num_squares = 5
    spacing = Simulation.WIDTH / (num_squares + 1)  # Space them evenly
    integer = 64
    square_size = Simulation.WIDTH / integer * 2  # Size based on original distribution

    for i in range(num_squares):
        x = (i + 1) * spacing  # Evenly space the squares
        y = Simulation.HEIGHT / 2 - 50  # Center vertically
        for _ in range(50):  # Each square consists of 20 small particles
            px = x + np.random.uniform(-square_size / 2, square_size / 2)
            py = y + np.random.uniform(-square_size / 2, square_size / 2)
            b = Body(px, py, 0, 0, 0.01, (0, 0, 255))  # Small blue particles
            b.overRiddenRadius = 4
            bodies.append(b)

    # Initialize simulation
    args = get_args() 

    sim = Simulation(bodies, dt=0.1, radius=Simulation.WIDTH)
    sim.reCenter = False
    sim.gravitationalForce = True
    sim.collisionDetection = True
    # drawBarnesHuts = args.drawBH
    # frames_per_second = args.fps
    sim.simulate(Theta=float('inf'), drawBarnesHuts=args.drawBH, frames_per_second=args.fps)

if __name__ == '__main__':
    main()
