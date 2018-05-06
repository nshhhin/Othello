boolean check(int x, int y, int dx, int dy) {
  if (x+dx>=0 && x+dx<8 && y+dy>=0 && y+dy<8) {

    if (field[x+dx][y+dy]==currentStone) {
      return true;
    } else if (field[x+dx][y+dy]==NONE) {
      return false;
    } else {
      return check(x+dx, y+dy, dx, dy);
    }
  }
  return false;
}
