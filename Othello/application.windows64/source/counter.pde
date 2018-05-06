//点数を数える関数 cは黒(1)か白(2)か
int counter(int c) {
  int count=0;
  for (int x=0; x<8; x++) {
    for (int y=0; y<8; y++) {
      if (field[x][y]==c) {
        count++;
      }
    }
  }
  return count;
}
