boolean computer() {
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
                    //角は優先度を高くする
                    field_point[x][y]=-50;
                  }
                  else if ( (x==1&&y==1) || (x==1&&y==6) || (x==6&&y==1) || (x==6&&y==6) || 
                    (x==0&&y==1) || (x==1&&y==0) || (x==0&&y==6) || (x==1&&y==7) ||  (x==6&&y==0) || (x==7&&y==1) || (x==6&&y==7) || (x==7&&y==6)) {
                    //角の近くは優先度を低くする
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

          //csvで保存する処理

          TableRow newRow = table.addRow();
          newRow.setInt("id", table.getRowCount());
          newRow.setString("turn", turn2string(black_turn));
          newRow.setInt("positionX", x);
          newRow.setInt("positionY", y);
          newRow.setString("point", counter(BLACK)+"vs"+counter(WHITE));
          saveTable(table, "data/result.csv");
          pass=0;

          //ターンをひっくり返す
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
