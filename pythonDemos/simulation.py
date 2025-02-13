from body import Body
from tree import Tree
from quad import Quad
import pygame
import numpy as np

class Simulation:
    WIDTH:int = 800
    HEIGHT:int = 800
    WALL_DAMPING:float = 0.4

    __placingParticle:bool = False
    def __init__(self, bodies:list[Body], dt:float, radius:float) -> None:
        self.bodies = bodies
        self.dt = dt
        self.radius = radius
        self.N = len(bodies)

        #default options
        self.reCenter = True
        self.gravitationalForce = True
        self.collisionDetection = True
        self.boundaryCollisions = False
        self._localGravity:float = None

        self._algorithm = "Barnes-Hut"
    
    @property
    def algorithm(self)->str:
        return self._algorithm

    @algorithm.setter
    def algorithm(self, value:str)->None:
        if value not in ["Barnes-Hut","Brute Force"]:
            raise ValueError("Algorithm must be either 'Barnes-Hut' or 'Brute Force'")
        self._algorithm = value

    @property
    def localGravity(self)->float:
        return self._localGravity
    
    @localGravity.setter
    def localGravity(self, value:float)->None:
        if value < 0:
            raise ValueError("Local gravity must be positive)")
        self._localGravity = value

    def simulate(self,Theta,drawBarnesHuts:bool = False,frames_per_second:int = 60) -> None:
        pygame.init()
        screen = pygame.display.set_mode((Simulation.WIDTH, Simulation.HEIGHT),pygame.RESIZABLE)
        clock = pygame.time.Clock()
        font = pygame.font.SysFont("Arial", 18)

        self.running = True
        # self.last_added_time = 0

        while self.running:
            # handle events like adding bodies, quitting, etc.
            self.handlePygameEvents()

            radius = max(screen.get_width(), screen.get_height())
            quad = Quad(screen.get_width()/2, screen.get_height()/2, radius) #:TODO generalize to rectangles
            tree = Tree(quad,Theta = Theta)
            # Insert all bodies into the tree
            for body in self.bodies:
                if body.inQuad(quad):
                    tree.insert(body)
            # Update forces and positions and draw
            self.__draw(screen,tree,drawBarnesHuts)

            # Display FPS
            fps = clock.get_fps()
            fps_text = font.render(f"FPS: {fps:.2f}", True, pygame.Color('white'))
            numBodies_text = font.render(f"Number of bodies: {tree.numBodies}", True, pygame.Color('white'))
            screen.blit(fps_text, (10, 10))
            screen.blit(numBodies_text, (10, 30))
            pygame.display.flip()
            clock.tick(frames_per_second)

        pygame.quit()
    
    def handlePygameEvents(self) -> None:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.running = False

            elif event.type == pygame.MOUSEBUTTONDOWN:
                self.__start_pos = pygame.mouse.get_pos()
                self.__placingParticle = True
                self.__startPlacingParticleTime = pygame.time.get_ticks()

            elif event.type == pygame.MOUSEBUTTONUP and self.__placingParticle:
                x1,y1 = self.__start_pos
                x2,y2 = pygame.mouse.get_pos()
                dx = x2 - x1
                dy = y2 - y1
                # slingshot effect
                dx = -dx 
                dy = -dy
                
                # mass
                finishTime = pygame.time.get_ticks()
                mass = (finishTime - self.__startPlacingParticleTime)*10**10
                # add body
                newBody = Body(x1, y1, dx, dy, mass, (0, 0, 255))
                newBody.overRiddenRadius = max(5,newBody.scaledRadius)
                self.bodies.append(newBody)
                
                # close
                self.__placingParticle = False
       
    def __draw(self,screen:pygame.display,tree:Tree,drawBarnesHuts:bool)->None:
        screen.fill((0, 0, 0))
        if drawBarnesHuts:
            tree.draw(screen)
        #update forces and positions
        if self.gravitationalForce:
            if self.algorithm == "Brute Force":
                self.__bruteForceGravity()
            elif self.algorithm == "Barnes-Hut":
                self.__bhGravity(tree)
        
        # collision detection
        if self.collisionDetection:
            self.__bhHandleCollisions(tree)
        #boundary collisions
        if self.boundaryCollisions:
            self.__handleBoundaryCollisions(screen)
        #local gravity
        if self.localGravity:
            self.__handleLocalGravity()
        # Centre of mass recentering. If the system run's away, you still see it. 
        if self.reCenter:
            self.__reCentre(screen)
        # Update forces and positions
        for body in self.bodies:
            body.update(self.dt)
        #draw user input:
        if self.__placingParticle and self.__start_pos:
            x1,y1 = self.__start_pos
            x2,y2 = pygame.mouse.get_pos()
            pygame.draw.line(screen, (255, 255, 255), (x1, y1), (x2, y2), 2)
            mass = (pygame.time.get_ticks() - self.__startPlacingParticleTime)*10**10
            radius = mass**(1/3)
            radius = min(25,max(5, radius/1000))
            pygame.draw.circle(screen, (0, 0, 255), (x1, y1), radius)

        # Draw bodies
        for body in self.bodies:              
            body.draw(screen)  
           
    
    def __bhGravity(self, tree:Tree)->None:
        for body in self.bodies:
            body.resetForce()
            tree.updateForce(body)
    
    def __bruteForceGravity(self)->None:
        for i in range(self.N):
            self.bodies[i].resetForce()

        for i in range(self.N):
            for j in range(i+1,self.N):
                self.bodies[i].addForce(self.bodies[j])
                self.bodies[j].addForce(self.bodies[i])
    
    def __bhHandleCollisions(self,tree:Tree)->None:
        for body in self.bodies:
            tree.updateCollisions(body,threshold=tree.numBodies/10)

    def __reCentre(self,screen:pygame.display)->None:
        # Cetnre of mass recentering. If the system run's away, you still see it. 
        offset = max([body.scaledRadius+5 for body in self.bodies])
        com_x = sum(body._rx*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies) + offset
        com_y = sum(body._ry*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies) + offset

        for body in self.bodies:
            body._rx += (screen.get_width()/2 - com_x)
            body._ry += (screen.get_height()/2 - com_y)

    def __handleBoundaryCollisions(self,screen:pygame.display)->None:
        for body in self.bodies:
            if body._rx - body.scaledRadius < 0 or body._rx + body.scaledRadius > screen.get_width():
                correctDirection = 1 if body._rx - body.scaledRadius < 0 else -1
                body._vx = abs(body._vx) * correctDirection * self.WALL_DAMPING
                #teleport to the edge
                if body._rx - body.scaledRadius < 0:
                    body._rx = body.scaledRadius
                else:
                    body._rx = screen.get_width() - body.scaledRadius
                
            if body._ry - body.scaledRadius < 0 or body._ry + body.scaledRadius > screen.get_height():
                correctDirection = 1 if body._ry - body.scaledRadius < 0 else -1
                body._vy = abs(body._vy) * correctDirection * self.WALL_DAMPING

                #teleport to the edge
                if body._ry - body.scaledRadius < 0:
                    body._ry = body.scaledRadius
                else:
                    body._ry = screen.get_height() - body.scaledRadius


    def __handleLocalGravity(self)->None:
        for body in self.bodies:
            body._vy += self.localGravity
    


if __name__ == "__main__":
    ## make a solar system
    np.random.seed(10)

    bodies = []
    centre = np.array([Simulation.WIDTH / 2, Simulation.HEIGHT / 2])
    SUN: Body = Body(centre[0]+50, centre[1]+50, 0.0, 0.0, 10**13, (255, 0, 0))
    bodies.append(SUN)
    widthFactor = min(Simulation.WIDTH / 2, Simulation.HEIGHT / 2)
    nrings = 3
    # rings = [widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875]
    rings = [widthFactor*(i+1)/(nrings+1) for i in range(nrings)][::-1]
    masses = [10**(0.5*(i)) for i in range(nrings)]
    # make nrings distinct colors
    # colors = [(255, 255 * i // nrings, 255 * (nrings - i) // nrings) for i in range(nrings)]
    colors = [(255,255,255) for i in range(nrings)]
    for i in range(20):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 1* np.random.randn()
        x = SUN._rx + radius * np.cos(theta)
        y = SUN._ry + radius * np.sin(theta)
        v = np.sqrt(SUN.G * SUN._mass / radius)
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
        body = Body(x, y, vx, vy, 10**10, (0, 255, 0))
        bodies.append(body)

    for i in range(1000):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 1* np.random.randn()
        x = SUN._rx + radius * np.cos(theta)
        y = SUN._ry + radius * np.sin(theta)
        v = np.sqrt(SUN.G * SUN._mass / radius)
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
        body = Body(x, y, vx, vy, 1, colors[i%nrings])
        bodies.append(body)    

    # single orbiting mass
    theta = 2 * np.pi * np.random.random()
    radius = 400
    x = SUN._rx + radius * np.cos(theta)
    y = SUN._ry + radius * np.sin(theta)
    v = np.sqrt(SUN.G * SUN._mass / radius)
    vx = -v * np.sin(theta)
    vy = v * np.cos(theta)
    mass = 10**0
    body = Body(x, y, vx, vy, mass, (0, 0, 255))
    bodies.append(body)


    # # two particles, stationary initially
    # bodies = []
    # bodies.append(Body(1, 200, 50, 0, 1**12, (255, 0, 0)))
    # bodies.append(Body(799, 200, -150, 0, 1**12, (0, 255, 0)))

    # print(bodies)

    sim = Simulation(bodies, dt =1, radius=Simulation.WIDTH)
    sim.collisionDetection = False
    # sim.algorithm = 'Brute Force'
    sim.simulate(Theta = float('inf'),drawBarnesHuts = False, frames_per_second = 30)