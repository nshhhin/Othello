
//2620130546 新納真次郎
//お借りしたフォント http://www.fontspace.com/wlm-fonts/lets-go-digital


final int SIZE = 50;
final int STONE_SIZE = (int)(SIZE*0.7);
final int NONE = 0;
final int BLACK = 1;
final int WHITE = 2;
final int EdgeSIZE=100;

int screen; //スタート画面,プレイ画面,終了画面のうちどれかを判別するための変数
final int START=1;
final int PLAY=2;
final int FINISH=3;
int alpha_s=255; //スタートメニューのkeyPresse~の透過度

int p1, p2=0; // 先手と後手が人かコンピュータかどうか
final int PLAYER=1;
final int CPU=2;
      int forcs; //スタート画面でのp1・p2のフォーカス
final int Left=1;
final int Right=2;

int[][] field;

boolean black_turn = true; //true:黒のターン　false:白のターン
int currentStone; //現在の色
float alpha_b, alpha_w=0; //スコアボードの透明度
Table table;
Table newRow;

int pass=0; //passが連続した回数を記録するための変数

/* AIのための変数 */
int [][] field_point; //評価値　(都合上、この値が低いほど置く優先度が高いということにした)
int [][] field_openpoint; //開放度

/*undo機能のための変数*/
int [][] prefield; //一時的に1ターン前の盤面を保存しておく
int keycount; //２重Undoを防ぐため

void setup() {
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



  //デジタルフォントの読み込み
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

  // 初期位置
  field[3][3] = BLACK;
  field[4][4] = BLACK;
  field[4][3] = WHITE;
  field[3][4] = WHITE;
  
  prefield[3][3] = BLACK;
  prefield[4][4] = BLACK;
  prefield[4][3] = WHITE;
  prefield[3][4] = WHITE;
  
}

void draw() {
  
  //スタート画面
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
    text("▲", width/2-100, height/2+70);

    if (p1==PLAYER) {
      text("player 1", width/2-100, height/2+100);
    } else {
      text("cpu", width/2-100, height/2+100);
    }
    text("▼", width/2-100, height/2+130);

    text("▲", width/2+100, height/2+70);

    if (p2==PLAYER) {
      text("player 2", width/2+100, height/2+100);
    } else {
      text("cpu", width/2+100, height/2+100);
    }
    text("▼", width/2+100, height/2+130);

    text("VS", width/2, height/2+100);
  } 
  
  //プレイ画面
  else if (screen==PLAY) {
    //今現在の色をターンから導く。
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
                  possible |= check(i, j, dx, dy); //置く場所が一つでもあるか
                  
                  //置ける場所の描画
                  if(black_turn&&check(i,j,dx,dy)){
                    fill(0,30);
                    ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE*0.5, STONE_SIZE*0.5);
                  }
                  else if(!black_turn&&check(i,j,dx,dy)){
                    fill(255,30);
                    ellipse((i*2+1)*SIZE/2+EdgeSIZE, (j*2+1)*SIZE/2+EdgeSIZE, STONE_SIZE*0.5, STONE_SIZE*0.5);
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
      println("パス");
      pass++;

      if (pass>=2) {
        println("おわり");
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
  
  //終了画面
  if (screen==FINISH) {
    //何もしない
  }
}






void mousePressed() {
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
            //何もしない
          } else {
            if (checkOver(x+dx, y+dy)) {
              if (field[x+dx][y+dy]==3-currentStone && check(x+dx, y+dy, dx, dy) ) {
                field[x][y] = currentStone;

                int t=reverseCount(x, y, dx, dy, 0);
                for (int i=0; i<=t; i++) {
                  field[x+dx*i][y+dy*i]=currentStone;
                }
                put=true; //置いたかどうか
                t=0;
              }
            }
          }
        }
      }

      if (put) { 
        keycount=0;

        //csvで保存する処理

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
        //ターンをひっくり返す
        black_turn = !black_turn;
      }
    }
  }
}



void keyPressed() {
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




