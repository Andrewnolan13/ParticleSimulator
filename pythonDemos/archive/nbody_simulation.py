import pygame
import numpy as np
import random
from utils import Body
from quadtree import Quadtree, Quad

# Constants
WIDTH, HEIGHT = np.float64(800.0), np.float64(600.0)
NUM_PARTICLES = 100
G = 6.67430e-11  # Gravitational constant
DT = 0.1  # Time step

class Particle(Body):
    def __init__(self, pos, vel, mass, radius):
        super().__init__(pos, vel, mass, radius)

    def update(self, force):
        acc = force / self.mass
        self.vel += acc * DT
        self.pos += self.vel * DT

def calculate_force(p1, p2):
    r = p2.pos - p1.pos
    dist_sq = np.dot(r, r)
    if dist_sq == 0:
        return np.zeros(2)
    force_mag = G * p1.mass * p2.mass / dist_sq
    force_dir = r / np.sqrt(dist_sq)
    return force_mag * force_dir

def main():
    pygame.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    clock = pygame.time.Clock()

    particles = [Particle(
        pos=[np.float64(random.uniform(0, WIDTH)), np.float64(random.uniform(0, HEIGHT))],
        vel=[np.float64(random.uniform(-1, 1)), np.float64(random.uniform(-1, 1))],
        mass=np.float64(random.uniform(1e10, 1e12)),
        radius=2.0
    ) for _ in range(NUM_PARTICLES)]

    quadtree = Quadtree(theta=1.0, epsilon=1.0)

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

        screen.fill((0, 0, 0))

        quad = Quad.new_containing(particles)
        quadtree.clear(quad)

        for particle in particles:
            quadtree.insert(particle.pos, particle.mass)

        quadtree.propagate()

        forces = [quadtree.acc(particle.pos) for particle in particles]

        for i, particle in enumerate(particles):
            particle.update(forces[i])
            pygame.draw.circle(screen, (255, 255, 255), particle.pos.astype(int), particle.radius)

        pygame.display.flip()
        clock.tick(60)

    pygame.quit()

if __name__ == "__main__":
    print("Running n-body simulation")
    main()
    print("Simulation finished")
