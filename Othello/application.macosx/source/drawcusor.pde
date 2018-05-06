void draw_cursor() {
  //noCursor();
  if (mouseX>EdgeSIZE && mouseX<width-EdgeSIZE && mouseY>EdgeSIZE && mouseY<height-EdgeSIZE) {
    int x = (mouseX-EdgeSIZE)/SIZE;
    int y = (mouseY-EdgeSIZE)/SIZE;

    if (field[x][y]!=NONE)  return;

    if (black_turn) {
      fill(0);  //color black
      ellipse(mouseX, mouseY, STONE_SIZE, STONE_SIZE);
    } else {
      fill(255);  //color white
      ellipse(mouseX, mouseY, STONE_SIZE, STONE_SIZE);
    }
  }
}

