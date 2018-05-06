// field[x][y]の開放度を返す関数


boolean open_point(int x, int y) {
  for (int dy=-1; dy<=1; dy++) {
    for (int dx=-1; dx<=1; dx++) {
      if (checkOver(x+dx, y+dy)) {
        if (field[x+dx][y+dy]==NONE) {
          field_openpoint[x+dx][y+dy]=1;
        }
      }
    }
  }
  return true;
}

