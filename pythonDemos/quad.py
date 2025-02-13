import pygame

class Quad:
    __slots__ = ['_xmid', '_ymid', '_length']
    def __init__(self, var1:float, var3:float, var5:float)->None:
        self._xmid = var1
        self._ymid = var3
        self._length = var5

    def length(self)->float:
        return self._length
    
    def contains(self, x:float, y:float)->bool:
        halfLen = self._length / 2.0
        return( (x <= self._xmid + halfLen) and
                (x > self._xmid - halfLen) and
                (y < self._ymid + halfLen) and
                (y >= self._ymid - halfLen))
    
    def NW(self)->'Quad':
        var1 = self._xmid - self._length / 4.0
        var3 = self._ymid - self._length / 4.0
        var5 = self._length / 2.0
        return Quad(var1, var3, var5)
    
    def NE(self)->'Quad':
        var1 = self._xmid + self._length / 4.0
        var3 = self._ymid - self._length / 4.0
        var5 = self._length / 2.0
        return Quad(var1, var3, var5)
    
    def SW(self)->'Quad':
        var1 = self._xmid - self._length / 4.0
        var3 = self._ymid + self._length / 4.0
        var5 = self._length / 2.0
        return Quad(var1, var3, var5)

    def SE(self)->'Quad':
        var1 = self._xmid + self._length / 4.0
        var3 = self._ymid + self._length / 4.0
        var5 = self._length / 2.0
        return Quad(var1, var3, var5)
    
    def draw(self, screen:pygame.display)->None:
        pygame.draw.rect(screen, (255,255,255), pygame.Rect(self._xmid - self._length / 2.0, self._ymid - self._length / 2.0, self._length, self._length), 1)
    
    def __str__(self)->str:       
        var1 = "\n"
        for var2 in range(int(self._length)):
            for var3 in range(int(self._length)):
                if var2 != 0 and var3 != 0 and var2 != self._length - 1 and var3 != self._length - 1:
                    var1 += " "
                else:
                    var1 += "*"
            var1 += "\n"
        return var1