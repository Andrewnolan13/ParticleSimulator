import threading
import time
from renderer import Renderer
from simulation import Simulation
import numpy as np

PAUSED = threading.Event()
UPDATE_LOCK = threading.Lock()
SPAWN = []
BODIES = []
QUADTREE = []

def render(simulation):
    with UPDATE_LOCK:
        for body in SPAWN:
            simulation.bodies.append(body)
        SPAWN.clear()

        with threading.Lock():
            BODIES.clear()
            BODIES.extend(simulation.bodies)

        with threading.Lock():
            QUADTREE.clear()
            QUADTREE.extend(simulation.quadtree.nodes)

    # This is a placeholder for actual render logic:
    print("Rendering simulation...")

def main():
    config = {
        "window_mode": "Windowed",
        "width": 900,
        "height": 900
    }

    simulation = Simulation()

    def simulation_thread():
        while True:
            if PAUSED.is_set():
                time.sleep(0.01)
            else:
                simulation.step()
            render(simulation)

    threading.Thread(target=simulation_thread, daemon=True).start()

    Renderer.run(config)  # Assuming Renderer.run is similar to `quarkstrom::run` in functionality

if __name__ == "__main__":
    main()
