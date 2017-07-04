void undo(){
 
  
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
    //対CPUのときはターンを変えない
     black_turn=black_turn;
     table.removeRow(table.getRowCount()-1); 
  }
  else{
    black_turn=!black_turn;
  }
  
  saveTable(table, "data/result.csv");

  }
}
 


