import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Othello extends PApplet {


//2620130546 \u65b0\u7d0d\u771f\u6b21\u90ce
//\u304a\u501f\u308a\u3057\u305f\u30d5\u30a9\u30f3\u30c8 http://www.fontspace.com/wlm-fonts/lets-go-digital


final int SIZE = 50;
final int STONE_SIZE = (int)(SIZE*0.7f);
final int NONE = 0;
final int BLACK = 1;
final int WHITE = 2;
final int EdgeSIZE=100;

int screen; //\u30b9\u30bf\u30fc\u30c8\u753b\u9762,\u30d7\u30ec\u30a4\u753b\u9762,\u7d42\u4e86\u753b\u9762\u306e\u3046\u3061\u3069\u308c\u304b\u3092\u5224\u5225\u3059\u308b\u305f\u3081\u306e\u5909\u6570
final int START=1;
final int PLAY=2;
final int FINISH=3;
int alpha_s=255; //\u30b9\u30bf\u30fc\u30c8\u30e1\u30cb\u30e5\u30fc\u306ekeyPresse~\u306e\u900f\u904e\u5ea6

int p1, p2=0; // \u5148\u624b\u3068\u5f8c\u624b\u304c\u4eba\u304b\u30b3\u30f3\u30d4\u30e5\u30fc\u30bf\u304b\u3069\u3046\u304b
final int PLAYER=1;
final int CPU=2;
      int forcs; //\u30b9\u30bf\u30fc\u30c8\u753b\u9762\u3067\u306ep1\u30fbp2\u306e\u30d5\u30a9\u30fc\u30ab\u30b9
final int Left=1;
final int Right=2;

int[][] field;

boolean black_turn = true; //true:\u9ed2\u306e\u30bf\u30fc\u30f3\u3000false:\u767d\u306e\u30bf\u30fc\u30f3
int currentStone; //\u73fe\u5728\u306e\u8272
float alpha_b, alpha_w=0; //\u30b9\u30b3\u30a2\u30dc\u30fc\u30c9\u306e\u900f\u660e\u5ea6
Table table;
Table newRow;

int pass=0; //pass\u304c\u9023\u7d9a\u3057\u305f\u56de\u6570\u3092\u8a18\u9332\u3059\u308b\u305f\u3081\u306e\u5909\u6570

/* AI\u306e\u305f\u3081\u306e\u5909\u6570 */
int [][] field_point; //\u8a55\u4fa1\u5024\u3000(\u90fd\u5408\u4e0a\u3001\u3053\u306e\u5024\u304c\u4f4e\u3044\u307b\u3069\u7f6e\u304f\u512a\u5148\u5ea6\u304c\u9ad8\u3044\u3068\u3044\u3046\u3053\u3068\u306b\u3057\u305f)
int [][] field_openpoint; //\u958b\u653e\u5ea6

/*undo\u6a5f\u80fd\u306e\u305f\u3081\u306e\u5909\u6570*/
int [][] prefield; //\u4e00\u6642\u7684\u306b1\u30bf\u30fc\u30f3\u524d\u306e\u76e4\u9762\u3092\u4fdd\u5b58\u3057\u3066\u304a\u304f
int keycount; //\uff12\u91cdUndo\u3092\u9632\u3050\u305f\u3081

public void setup() {
  screen=START;
  p1=PLAYER;
  p2=PLAYER;
  forcs=Left;


  table = new Table();

  table.addColumn("id");
  table.addColumn("turn");
  table.addColumn("positionX");
  table.addColumn("positionY");
  table.addColumn("point");



  //\u30c7\u30b8\u30bf\u30eb\u30d5\u30a9\u30f3\u30c8\u306e\u8aad\u307f\u8fbc\u307f
  PFont font = createFont("Let's go Digital", 24);
  textFont(font);
  size(8*SIZE+EdgeSIZE*2, 8*SIZE+EdgeSIZE*2);
  field = new int[8][8];
  prefield = new int[8][8];
  field_point = new int[8][8];
  field_openpoint = new int[8][8];
  for (int i=0; i<8; ++i) {
    for (int j=0; j<8; ++j) {
      field[i][j] = NONE;
      prefield[i][j] = NONE;
    }
  }

  // \u521d\u671f\u4f4d\u7f6e
  field[3][3] = BLACK;
  field[4][4] = BLACK;
  field[4][3] = WHITE;
  field[3][4] = WHITE;
  
  prefield[3][3] = BLACK;
  prefield[4][4] = BLACK;
  prefield[4][3] = WHITE;
  prefield[3][4] = WHITE;
  
}

public void draw() {
  
  //\u30b9\u30bf\u30fc\u30c8\u753b\u9762
  if (screen==START) {
    textAlign(CENTER);
    fill(0);
    rect(0, 0, width, height);
    fill(255);
    textSize(30);
    text("Othello", width/2, height/2);
    textSize(20);

    fill(255, alpha_s);
    text("Press 'SPACE' to Start", width/2, height/2+20);
    alpha_s-=3;
    if (alpha_s<100) {
      alpha_s=255;
    }

    noFill();
    stroke(255, 0, 0);
    strokeWeight(2);

    rectMode(CENTER);
    if (forcs==Left) {
      rect(width/2-100, height/2+92, 100, 100, 10);
    } else {
      rect(width/2+100, height/2+92, 100, 100, 10);
    }
    rectMode(CORNER);
    stroke(0);
    fill(255);
    text("\u25b2", width/2-100, height/2+70);

    if (p1==PLAYER) {
      text("player 1", width/2-100, height/2+100);
    } else {
      text("cpu", width/2-100, height/2+100);
    }
    text("\u25bc", width/2-100, height/2+130);

    text("\u25b2", width/2+100, height/2+70);

    if (p2==PLAYER) {
      text("player 2", width/2+100, height/2+100);
    } else {
      text("cpu", width/2+100, height/2+100);
    }
    text("\u25bc", width/2+100, height/2+130);

    text("VS", width/2, height/2+100);
  } 
  
  //\u30d7\u30ec\u30a4\u753b\u9762
  else if (screen==PLAY) {
    //\u4eca\u73fe\u5728\u306e\u8272\u3092\u30bf\u30fc\u30f3\u304b\u3089\u5c0e\u304f\u3002
    strokeWeight(1);



    if (p1==CPU) {
      if (currentStone==BLACK) {
        computer();
      }
    }

    if (p2==CPU) {
      if (currentStone==WHITE) {
        computer();
      }
    }

    background(10);
    fill(255);
    text("Press 'q' to go start menu ", 10, 20);
    text("Press 'p' to undo ", 10, 40);
    fill(0, 128, 0);
    rect(EdgeSIZE, EdgeSIZE, 8*SIZE, 8*SIZE);


    if (black_turn) currentStone=BLACK;
    else currentStone=WHITE;
    // lines
    stroke(0);
    for (int i=1; i<8; ++i) {
      line(i*SIZE+EdgeSIZE, EdgeSIZE, i*SIZE+EdgeSIZE, height-EdgeSIZE);
      line(EdgeSIZE, i*SIZE+EdgeSIZE, width-EdgeSIZE, i*SIZE+EdgeSIZE);
    }
    fill(0);
    ellipse(SIZE*2+EdgeSIZE, SIZE*2+EdgeSIZE, 10, 10);
    ellipse(SIZE*6+EdgeSIZE, SIZE*2+EdgeSIZE, 10, 10);
    ellipse(SIZE*2+EdgeSIZE, SIZE*6+EdgeSIZE, 10, 10);
    ellipse(SIZE*6+EdgeSIZE, SIZE*6+EdgeSIZE, 10, 10);
    
    
    //draw scoreBord
    
    //Black's Score
    if (black_turn) {
      alpha_b+=5; 
      alpha_w=0;
    }
    if (alpha_b>255) alpha_b=0;
    stroke(255, 0, 0, alpha_b);
    strokeWeight(5);
    fill(190);
    rect(width/2-200, EdgeSIZE+SIZE*8+20, 150, 50);
    fill(0);
    textSize(70);
    textAlign(CENTER);
    text(counter(BLACK), width/2-125, EdgeSIZE+SIZE*8+70);
    textSize(20);
    fill(255);
    textAlign(BASELINE);
    text("black", width/2-100, EdgeSIZE+SIZE*8+95);

    textAlign(CENTER);
    textSize(30);
    text("vs", width/2, EdgeSIZE+SIZE*8+80); 

    stroke(0);
    strokeWeight(0);

    //White's Score
    if (!black_turn) {
      alpha_w+=5;
      alpha_b=0;
    }
    if (alpha_w>255) alpha_w=0;
    stroke(255, 0, 0, alpha_w);
    strokeWeight(5);
    fill(190);
    rect(width/2+50, EdgeSIZE+SIZE*8+20, 150, 50);
    fill(0);
    textSize(70);
    textAlign(CENTER);
    text(counter(WHITE), width/2+125, EdgeSIZE+SIZE*8+70);
    textSize(20);
    fill(255);
    textAlign(BASELINE);
    text("white", width/2+50, EdgeSIZE+SIZE*8+95);

    strokeWeight(1);
    stroke(0);

    // draw stones
    boolean possible=false;

    noStroke();
    for (int i=0; i<8; i++) {
      for (int j=0; j<8; j++) {

        if (field[i][j]==BLACK) {
          fill(0);  //color black
          ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE, STONE_SIZE);
        } else if (field[i][j]==WHITE) {
          fill(255); // color white
          ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE, STONE_SIZE);
        }
        

        for (int dy=-1; dy<=1; dy++) {
          for (int dx=-1; dx<=1; dx++) {
            if ((dx==0&&dy==0)) {
            } else {
              if (checkOver(i+dx, j+dy)) {
                if (field[i][j]==NONE && field[i+dx][j+dy]==3-currentStone ) {
                  possible |= check(i, j, dx, dy); //\u7f6e\u304f\u5834\u6240\u304c\u4e00\u3064\u3067\u3082\u3042\u308b\u304b
                  
                  //\u7f6e\u3051\u308b\u5834\u6240\u306e\u63cf\u753b
                  if(black_turn&&check(i,j,dx,dy)){
                    fill(0,30);
                    ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE*0.5f, STONE_SIZE*0.5f);
                  }
                  else if(!black_turn&&check(i,j,dx,dy)){
                    fill(255,30);
                    ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE*0.5f, STONE_SIZE*0.5f);
                  }
                }
              }
            }
          }
        }
      }
    }
    if (!possible) {
      black_turn=!black_turn;
      println("\u30d1\u30b9");
      pass++;

      if (pass>=2) {
        println("\u304a\u308f\u308a");
        fill(255);
        textAlign(CENTER);
        textSize(40);
        if (counter(BLACK)>counter(WHITE)) {
          text("Black WIN !!", width/2, 60);
        } else if (counter(WHITE)>counter(BLACK)) {
          text("White WIN !!", width/2, 60);
        } else {
          text("DRAW", width/2, 60);
        }
        save("data/screenshot.png" );
        screen=FINISH;
      }
    }


    draw_cursor();

    
  }
  
  //\u7d42\u4e86\u753b\u9762
  if (screen==FINISH) {
    //\u4f55\u3082\u3057\u306a\u3044
  }
}






public void mousePressed() {
  int x = (mouseX-EdgeSIZE)/SIZE;
  int y = (mouseY-EdgeSIZE)/SIZE;
  
  for(int i=0; i<8 ;i++){
    for(int j=0;j<8; j++){
      prefield[i][j]=field[i][j];
    }
  }

  if (checkOver(x, y)) {

    if (field[x][y]==NONE) {

      boolean put=false;

      for (int dy=-1; dy<=1; dy++) {
        for (int dx=-1; dx<=1; dx++) {
          if ((dx==0&&dy==0)) {
            //\u4f55\u3082\u3057\u306a\u3044
          } else {
            if (checkOver(x+dx, y+dy)) {
              if (field[x+dx][y+dy]==3-currentStone && check(x+dx, y+dy, dx, dy) ) {
                field[x][y] = currentStone;

                int t=reverseCount(x, y, dx, dy, 0);
                for (int i=0; i<=t; i++) {
                  field[x+dx*i][y+dy*i]=currentStone;
                }
                put=true; //\u7f6e\u3044\u305f\u304b\u3069\u3046\u304b
                t=0;
              }
            }
          }
        }
      }

      if (put) { 
        keycount=0;

        //csv\u3067\u4fdd\u5b58\u3059\u308b\u51e6\u7406

        TableRow newRow = table.addRow();
        newRow.setInt("id", table.getRowCount());
        newRow.setString("turn", turn2string(black_turn));
        newRow.setInt("positionX", x);
        newRow.setInt("positionY", y);
        newRow.setString("point", counter(BLACK)+"vs"+counter(WHITE));
        saveTable(table, "data/result.csv");
        
        /*
        TableRow newRow = table2.addRow();
        newRow.setInt("positionX", x);
        newRow.setInt("positionY", y);
        
        saveTable(table, "data/new2.csv");
*/
        //\u30bf\u30fc\u30f3\u3092\u3072\u3063\u304f\u308a\u8fd4\u3059
        black_turn = !black_turn;
      }
    }
  }
}



public void keyPressed() {
  if (screen==START &&(keyCode == UP || keyCode == DOWN) && forcs==Left) {
    p1=3-p1;
  } else if ( screen==START && (keyCode == UP || keyCode == DOWN) && forcs==Right) {
    p2=3-p2;
  }

  if (screen==START && keyCode == LEFT) {
    forcs=Left;
  } else if (screen==START && keyCode == RIGHT ) {
    forcs=Right;
  }
  if (key==' ' &&screen==START) {
    screen=PLAY;
  }

  if ( key == 'q' && (screen==FINISH || screen==PLAY)) {

    black_turn=true;
    pass=0;
    currentStone=0;
    for (int i=0; i<8; ++i) {
      for (int j=0; j<8; ++j) {
        field[i][j] = NONE;
        field_point[i][j] = NONE;
        field_openpoint[i][j] = NONE;
      }
    }
    field[3][3] = BLACK;
    field[4][4] = BLACK;
    field[4][3] = WHITE;
    field[3][4] = WHITE;
  
  screen=START;
  }
  if(key=='p' && screen==PLAY && !(p1==CPU&&p2==CPU)){
    undo();
  }
    
}




public boolean check(int x, int y, int dx, int dy) {
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
public boolean checkOver(int x, int y) {
  return x>=0 && x<8 && y>=0 && y<8;
}

public boolean computer() {
  boolean put=false;
  int min=100;
  for (int y=0; y<8; y++) {
    for (int x=0; x<8; x++) {
      field_point[x][y] =100 ;
    }
  }

  for (int y=0; y<8; y++) {
    for (int x=0; x<8; x++) {

      field_openpoint[x][y] = 0;
      if (field[x][y]==NONE) {
        for (int dy=-1; dy<=1; dy++) {
          for (int dx=-1; dx<=1; dx++) {
            if ( (dx==0 && dy==0) ) {
            } else {
              if (checkOver(x+dx, y+dy)) {
                if (field[x+dx][y+dy]==3-currentStone && check(x+dx, y+dy, dx, dy) ) {
                  if ( (x==0&&y==0) || (x==0&&y==7) || (x==7&&y==0) || (x==7&&y==7) ) {
                    //\u89d2\u306f\u512a\u5148\u5ea6\u3092\u9ad8\u304f\u3059\u308b
                    field_point[x][y]=-50;
                  }
                  else if ( (x==1&&y==1) || (x==1&&y==6) || (x==6&&y==1) || (x==6&&y==6) || 
                    (x==0&&y==1) || (x==1&&y==0) || (x==0&&y==6) || (x==1&&y==7) ||  (x==6&&y==0) || (x==7&&y==1) || (x==6&&y==7) || (x==7&&y==6)) {
                    //\u89d2\u306e\u8fd1\u304f\u306f\u512a\u5148\u5ea6\u3092\u4f4e\u304f\u3059\u308b
                    field_point[x][y]=10;
                  } else {
                    field_point[x][y]=0;
                  }
                  int t=reverseCount(x, y, dx, dy, 0);
                  for (int i=1; i<=t; i++) {

                    open_point(x+dx*i, y+dy*i);
                  }
                  for (int i=0; i<8; i++) {
                    for (int j=0; j<8; j++) {
                      if (field_openpoint[i][j]==1) {
                        field_point[x][y]++;       
                        field_openpoint[i][j]=0;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        println(x+","+y+":" +field_point[x][y]);

        if (field_point[x][y]<min) {
          min=field_point[x][y];
        }
      }
    }
  }


  for (int y=0; y<8; y++) {
    for (int x=0; x<8; x++) {

      if (field_point[x][y]==min) {





        for (int dy=-1; dy<=1; dy++) {
          for (int dx=-1; dx<=1; dx++) {
            if ((dx==0&&dy==0)) {
              //\u4f55\u3082\u3057\u306a\u3044
            } else {
              if (checkOver(x+dx, y+dy)) {
                if (field[x+dx][y+dy]==3-currentStone && check(x+dx, y+dy, dx, dy) ) {
                  field[x][y] = currentStone;

                  int t=reverseCount(x, y, dx, dy, 0);
                  for (int i=0; i<=t; i++) {
                    field[x+dx*i][y+dy*i]=currentStone;
                  }
                  put=true; //\u7f6e\u3044\u305f\u304b\u3069\u3046\u304b
                  t=0;
                }
              }
            }
          }
        }

        if (put) { 

          //csv\u3067\u4fdd\u5b58\u3059\u308b\u51e6\u7406

          TableRow newRow = table.addRow();
          newRow.setInt("id", table.getRowCount());
          newRow.setString("turn", turn2string(black_turn));
          newRow.setInt("positionX", x);
          newRow.setInt("positionY", y);
          newRow.setString("point", counter(BLACK)+"vs"+counter(WHITE));
          saveTable(table, "data/result.csv");
          pass=0;

          //\u30bf\u30fc\u30f3\u3092\u3072\u3063\u304f\u308a\u8fd4\u3059
          black_turn = !black_turn;
          println("--------------------");
        }
        delay(500);


        return true;
      }
    }
  }
  return false;
}
//\u70b9\u6570\u3092\u6570\u3048\u308b\u95a2\u6570 c\u306f\u9ed2(1)\u304b\u767d(2)\u304b
public int counter(int c) {
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
public void draw_cursor() {
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

// field[x][y]\u306e\u958b\u653e\u5ea6\u3092\u8fd4\u3059\u95a2\u6570


public boolean open_point(int x, int y) {
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


public int reverseCount(int x, int y, int dx, int dy, int count) {
  if (field[x+dx][y+dy]==currentStone) {
    return count;
  } else if (field[x+dx][y+dy]==NONE) {
    return 0;
  } else {
    return reverseCount(x+dx, y+dy, dx, dy, count+1);
  }
}

//boolean black_turn\u304b\u3089String\u3067\u9ed2\u304b\u767d\u304b\u8fd4\u3059\u95a2\u6570
public String turn2string(boolean t) {
  if (t) return "BLACK";
  else return "WHITE";
}  

public void undo(){
 
  
  if(!(counter(BLACK)==2&&counter(WHITE)==2) &&keycount<1 ){
 keycount++;
  for(int x=0;x<8;x++){
    for(int y=0;y<8;y++){
      field[x][y]=prefield[x][y];
    }
  }
  
  table = loadTable("data/result.csv", "header");
  table.removeRow(table.getRowCount()-1);
  
  
  if(p1==CPU || p2==CPU){
    //\u5bfeCPU\u306e\u3068\u304d\u306f\u30bf\u30fc\u30f3\u3092\u5909\u3048\u306a\u3044
     black_turn=black_turn;
     table.removeRow(table.getRowCount()-1); 
  }
  else{
    black_turn=!black_turn;
  }
  
  saveTable(table, "data/result.csv");

  }
}
 


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Othello" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
