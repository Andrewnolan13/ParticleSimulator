from multiprocessing import Process, Manager

class Body:
    def __init__(self, mass, x, y):
        self.mass = mass
        self.x = x
        self.y = y

    def __repr__(self):
        return f"Body(mass={self.mass}, x={self.x}, y={self.y})"

def update_body(body_list, index, dx, dy):
    body_list[index].x += dx
    body_list[index].y += dy

if __name__ == "__main__":
    with Manager() as manager:
        bodies = manager.list([Body(10, 0, 0), Body(20, 5, 5)])  # Shared list
        
        p1 = Process(target=update_body, args=(bodies, 0, 2, 3))  # Move first body
        p2 = Process(target=update_body, args=(bodies, 1, -1, -1))  # Move second body

        p1.start()
        p2.start()
        p1.join()
        p2.join()

        print(list(bodies))  # Updated positions
