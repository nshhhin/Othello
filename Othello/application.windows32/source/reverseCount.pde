
int reverseCount(int x, int y, int dx, int dy, int count) {
  if (field[x+dx][y+dy]==currentStone) {
    return count;
  } else if (field[x+dx][y+dy]==NONE) {
    return 0;
  } else {
    return reverseCount(x+dx, y+dy, dx, dy, count+1);
  }
}
