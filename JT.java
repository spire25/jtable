import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

class JT extends JFrame{
	Container cp;
	JTable t;
	JScrollPane sp;
	JComboBox tCombo, colCombo;
	JTextField text1;
	JPanel p1, p2, p3, p31,p32;
	JButton bInsert, bUpdate, bDelete;
	//JTextField tf[];				// 칼럼 개수만큼 텍스트 박스 만들기
	Vector<JTextField> tf = new Vector<JTextField>();
	DefaultTableModel model;

	Vector<String> tableNames = new Vector<String>();
	Vector<String> columnNames = new Vector<String>();
	Vector<Vector> rowData = new Vector<Vector>();
	int cc;
	String tname;

	String url = "jdbc:oracle:thin:@127.0.0.1:1521:JAVA";
	Connection con;
	Statement stmt;

	boolean insertFlag = true;

	JT(){
		//tableNames = new Vector<String>();
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, "scott", "tiger");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//			getTname();
//			getTContent("select * from "+tableNames.get(0)); //pln(tableNames.get(0));
			init();
		}catch(ClassNotFoundException cnfe){
			pln("(1) Excep: " + cnfe);
		}catch(SQLException se){
			pln("(2) Excep: " + se);
		}
	}
	void init(){
		// component 생성
		cp = getContentPane();
		model = new DefaultTableModel();
		t = new JTable(model);
//		getTContent("select * from DEPT");
		model.setDataVector(rowData, columnNames);
		sp = new JScrollPane(t);
		p1 = new JPanel(new GridLayout(1,2));
		p3 = new JPanel(new BorderLayout());
		p31 = new JPanel(new GridLayout(1,cc));// 여기 열 값은 t의 columnCount()로
		p32 = new JPanel(new FlowLayout());

		tCombo = new JComboBox();
		colCombo = new JComboBox();				// JComboBox(Vector name)
		text1 = new JTextField();				// keyboard 이벤트 추가해주기
		bInsert = new JButton("추가");
		bUpdate = new JButton("수정");
		bDelete = new JButton("삭제");

		bInsert.setPreferredSize(new Dimension(120, 30));
		bUpdate.setPreferredSize(new Dimension(120, 30));
		bDelete.setPreferredSize(new Dimension(120, 30));

		// event 추가
		bInsert.addMouseListener(new MyHandler(this));
		bUpdate.addMouseListener(new MyHandler(this));
		bDelete.addMouseListener(new MyHandler(this));
		tCombo.addActionListener(new MyHandler(this));
		text1.addKeyListener(new MyHandler(this));
		t.addMouseListener(new MyHandler(this));
		//t.addDocumentListener(new MyHandler(this));
		
		// 컨테이너에 추가
		cp.add(p1, BorderLayout.NORTH);
		cp.add(sp, BorderLayout.CENTER);
		cp.add(p3, BorderLayout.SOUTH);
		p3.add(p31, BorderLayout.NORTH);
		p3.add(p32, BorderLayout.SOUTH);
		p1.add(tCombo);p1.add(colCombo);p1.add(text1);
		p32.add(bInsert);p32.add(bUpdate);p32.add(bDelete);
		addTextField();
		
		getTname();
		getTContent("select * from "+tableNames.get(1)); //pln(tableNames.get(1));
		setTCombo();
		setUI();
	}
	void setUI(){
		setTitle("JTable Test1");
		setSize(900, 600);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	void addTextField(){ // 칼럼 개수만큼 텍스트필드 생성 및 배치
		cc = t.getColumnCount();
		//tf = new JTextField[cc];
		tf.clear();
		String pkColName = getPkColumn();
		int pkColIndex = getPkIndex(pkColName);
		pln("pk 없을 경우 보자: "+ pkColIndex);

		for(int i=0; i<cc; i++){
			tf.add(new JTextField());
			p31.add(tf.get(i));
			if(pkColIndex == i) 
				tf.get(pkColIndex).setEnabled(false);
			cp.revalidate();
			cp.repaint();
		}
	}
	void setTCombo(){
		tCombo.removeAllItems();
		tCombo.addItem("검색할 테이블을 선택해주세요.");
		for(int i=0; i<rc; i++){
			tCombo.addItem(tableNames.get(i));
		}
		setColCombo();
		cp.revalidate();
		cp.repaint();
	}
	void setColCombo(){
		colCombo.removeAllItems();
		colCombo.addItem("검색할 칼럼을 선택해주세요.");
		for(int i=0; i<cc; i++){
			colCombo.addItem(columnNames.get(i));
		}
		cp.revalidate();
		cp.repaint();
	}

	int rc;
	void getTname(){
		// 나중에 table이름 갱신해야할때 -> 
		String sql = "select TNAME from TAB";
		ResultSet rs = null;
		tableNames.clear();
		try{
			rs = stmt.executeQuery(sql);
			rs.last();     
			rc = rs.getRow();
			rs.beforeFirst();
			//pln("row cnt"+rc);
			//tableNames = new String[rc];

			int i = 0;
			//tableNames.add("Table Name");
			while(rs.next()){
				String tname = rs.getString("TNAME");
				//pln("tname "+tname+", i "+i);
				//tableNames[i] = tname;
				tableNames.add(tname);
				//pln(tableNames[i]);
				i++;
			}
//			tCombo.addItem("검색할 테이블을 선택해주세요.");
//			tCombo.setSelectedIndex(rc);
		}catch(SQLException se){
			pln("getTname() Excep: " + se);
		}
	}
	void getTContent(String sql){
		ResultSet rs=null;
		try{
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			cc = rsmd.getColumnCount();
            for(int i=1; i<=cc; i++){
				String cn = rsmd.getColumnName(i);
				columnNames.add(cn);
			}
			
			while(rs.next()){
				Vector<String> v = new Vector<String>();
				for(int i=1; i<=cc; i++){
					String data = rs.getString(i);
					v.add(data);
				}
				rowData.add(v);
			}
			//pln(columnNames.toString());
			//pln(rowData.toString());
		}catch(SQLException se){
			pln("ddlSql() 예외: "+se);
		}
	}

	String getPkColumn(){
		//String selected = tCombo.getSelectedItem().toString();
		String selected = (String)tCombo.getSelectedItem();
		String sql = "SELECT column_name FROM all_cons_columns WHERE constraint_name = (SELECT constraint_name FROM all_constraints WHERE UPPER(table_name) = UPPER('"+selected+"') AND CONSTRAINT_TYPE = 'P')";
		//pln(selected);
		ResultSet rs;
		String pkColName="";
		try{
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colsCount = rsmd.getColumnCount();
			while(rs.next()){
				for(int i=1; i<=colsCount; i++){
					pkColName = rs.getString(i);
					pln("pkColName: "+pkColName);
				}
			}
			rs.close();
		} catch(SQLException se){
			pln("getPkColumn() 예외: "+se);
		}
		return pkColName;
	}
	int getPkIndex(String pkColName){
		int pkColIndex = 0;
		for(int i=0; i<columnNames.size(); i++){
			String colNames = columnNames.get(i);
			//pln("colNames: "+colNames);
			if(colNames.equalsIgnoreCase(pkColName)){
				pkColIndex = i;
				break;
				//pln("pkColIndex 번호: "+pkColIndex);
			}
			pkColIndex = -1;
			//pln("pk 없는 경우 반환 -1:" + i);

		}
		return pkColIndex;
	}
	
	// ??
//	void sqlExcute(String sql){
//		String[] sqlArr = sql.split(" ");
//		// DDL
//		if(sqlArr[0].equalsIgnoreCase("create") || sqlArr[0].equalsIgnoreCase("drop") || sqlArr[0].equalsIgnoreCase("alter") || sqlArr[0].equalsIgnoreCase("rename") || sqlArr[0].equalsIgnoreCase("truncate")){
//			try{
//				stmt.execute(sql);	// create table 존재하는 테이블이면 막기 추가
//				pln(sqlArr[0]+"문이 실행되었습니다.");
//			} catch(SQLException se){
//				pln(sqlArr[0]+"문 실행실패: " + se);
//			}
//		} else if(sqlArr[0].equalsIgnoreCase("insert") || sqlArr[0].equalsIgnoreCase("update") || sqlArr[0].equalsIgnoreCase("delete")){
//			try{ // DML
//				int i = stmt.executeUpdate(sql);
//				if(i>0) pln(i+"개의 row 수정("+sqlArr[0]+"문)");
//				else pln("입력되지 않았습니다.");
//			} catch(SQLException se){
//				pln(sqlArr[0]+"문 수정실패: " + se);
//			}
//		}else if(sqlArr[0].equalsIgnoreCase("select")){
//			//selectT(sql);
//		} else if(sql.equalsIgnoreCase("exit")){
//			closeAll();
//			System.exit(0);
//		}else{
//			pln("다른 명령어를 입력해주세요.");
//		}
//		
//	}
//	void selectT(String sql){
//		ResultSet rs;
//		try{
//			rs = stmt.executeQuery(sql);
//			ResultSetMetaData rsmd = rs.getMetaData();
//			cc = rsmd.getColumnCount();
//            for(int i=1; i<=cc; i++){
//				String cn = rsmd.getColumnName(i);
//				columnNames.add(cn);
//			}
//			
//			while(rs.next()){
//				Vector<String> v = new Vector<String>();
//				for(int i=1; i<=cc; i++){
//					String data = rs.getString(i);
//					v.add(data);
//				}
//				rowData.add(v);
//			}
//			rs.close();
//		} catch(SQLException se){
//			pln("selectT() 예외: "+se);
//		}
//	}

	void closeAll(){
		try{
			stmt.close();
			con.close();
		} catch(SQLException se){
			pln("closeAlll() 예외" + se);
		}
	}
	void p(String str){
		System.out.print(str);
	}
	void pln(String str){
		System.out.println(str);
	}
	public static void main(String[] args) {
		new JT();
	}
}



// 차후 마우스, 키보드(어댑터, 리스너) 바꿔서 사용해보기..
class MyHandler extends MouseAdapter implements ActionListener, KeyListener{
	JT jt;
	MyHandler(JT jt){
		this.jt = jt;
	}
	
	public void actionPerformed(ActionEvent e){ // tCombo event
		Object obj = e.getSource(); 
		String pkColName = jt.getPkColumn();
		if(obj == jt.tCombo){
			jt.columnNames.clear();
			jt.rowData.clear();
			jt.p31.removeAll();
			//JComboBox jcb =(JComboBox)e.getSource();
			//String selected = jcb.getSelectedItem().toString();
			String selected = jt.tCombo.getSelectedItem().toString();
			String sql = "select * from "+selected+" order by "+pkColName;
			pln(selected);
			//pln(sql);
			jt.getTContent(sql);
			jt.model.setDataVector(jt.rowData, jt.columnNames);
		}
		jt.addTextField();
		jt.setColCombo();
	}

	// KeyListener인터페이스 때문에 key이벤트 메소드 전부 사용
	public void keyTyped(KeyEvent e){ // 검색 기능
		String word = "";
		if( e.paramString().indexOf("Backspace") != -1){
			word = jt.text1.getText();
		}else if(e.getKeyCode() == 0){ // enter 눌러도 검색
			word = jt.text1.getText();
			System.out.println("enter : "+word);
		}else{
			word = jt.text1.getText()+e.getKeyChar();
			System.out.println("else : "+word);
		}
		pln(word);
		//pln(jt.text1.getText()+e.getKeyChar());
		//Object obj = e.getSource(); 

		String tSelected = jt.tCombo.getSelectedItem().toString();
		String colSelected = jt.colCombo.getSelectedItem().toString();
		String pkColName = jt.getPkColumn();
		String sql = "select * from "+tSelected+" where "+colSelected+" like '%"+word+"%'";
		if(word.length()==0){
			sql = "select * from "+tSelected+" order by "+pkColName;
		}
		
		jt.columnNames.clear();
		jt.rowData.clear();
		jt.getTContent(sql);
		//pln(colSelected);
		//pln(tSelected);
		jt.model.setDataVector(jt.rowData, jt.columnNames);
	}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}// 모든 키에 반응하지만 대소문자 구분을 못한다.

	// mouseAdapter
	public void mouseClicked(MouseEvent e){
		Object obj = e.getSource(); 
		if(obj == jt.bInsert){			// insert
			pln("insert");
			
			String pkColName = jt.getPkColumn();
			int pkIndex = jt.getPkIndex(pkColName); // -1인경우..
//			String pkText = jt.tf.get(pkIndex).getText();
//			pln(pkText);
			if(jt.insertFlag){
				if(pkIndex >= 0){
					jt.tf.get(pkIndex).setEnabled(true);
				}
				jt.insertFlag = false; // 잠금 풀어짐
			} else{
				// insert into DEPT values (90, 'help', 'seoul');
				String selectedT = jt.tCombo.getSelectedItem().toString();
				String insert_sql = "insert into " + selectedT + " values(";
				String sql = "select DATA_TYPE from ALL_TAB_COLUMNS where TABLE_NAME = '"+selectedT+"'";

				ResultSet rs;
				try{
					// 고려할 것: 문자형, 숫자형, 날짜형(DATE만)/ (LOB 나중에 구현)
					rs = jt.stmt.executeQuery(sql);
					int i = 0;
					int nullCount = 0;
					while(rs.next()){
						String colType = rs.getString(1);
						String tfText = jt.tf.get(i).getText();
						String aAttr = columnType(colType, tfText);
						
						if(aAttr.equalsIgnoreCase("null")){
							++nullCount;
							if(nullCount == jt.t.getColumnCount()){
								throw new Exception("모두 null값인 경우 값을 넣을 수 없습니다.(경고창)");
							}
						}

						if(i == (jt.tf.size()-1)){
							insert_sql = insert_sql + aAttr + ")";
						} else{
							insert_sql = insert_sql + aAttr + ", ";
						}
						i++;	
						//pln("ss"+jt.tf.size());
					}
					pln(insert_sql);

					int j = jt.stmt.executeUpdate(insert_sql);
					if(j>0){
						pln("삽입 완료");
						jt.insertFlag = true;

						String sql2 = "select * from "+selectedT+" order by "+pkColName;		
						jt.columnNames.clear();
						jt.rowData.clear();
						jt.getTContent(sql2);
						jt.model.setDataVector(jt.rowData, jt.columnNames);
						if(pkIndex>=0){
							jt.tf.get(pkIndex).setEnabled(false);
						}
						clearTf(jt.t.getColumnCount());
					} else if(j<=0){
						pln("insert에 실패했습니다.");
						jt.tf.get(pkIndex).setEnabled(false);
						jt.insertFlag = true;
					}
				} catch(SQLException se){
					pln("insert 예외: "+ se);
					pln("insert에 실패했습니다.(기본키, 참조키, 데이터 타입을 확인해 주세요.)경고창 띄워주기...");
					jt.tf.get(pkIndex).setEnabled(false);
					jt.insertFlag = true;
				} catch(Exception ee){
					pln("모두 null값인 경우 값을 넣을 수 없습니다.(경고창)....");
					return;
				}
			}
		}else if(obj == jt.bUpdate){	// update
			pln("update");
			String pkColName = jt.getPkColumn();
			int pkIndex = jt.getPkIndex(pkColName);

			String selectedT = jt.tCombo.getSelectedItem().toString();
			String update_sql = "update " + selectedT + " set ";
			String sql = "select DATA_TYPE from ALL_TAB_COLUMNS where TABLE_NAME = '"+selectedT+"'";
			
			// update sql문 만들기
			ResultSet rs = null;
			try{
				rs = jt.stmt.executeQuery(sql);
				int i = 0;
				while(rs.next()){
					String colType = rs.getString(1);
					String tfText = jt.tf.get(i).getText();
					String upAttr = columnType(colType, tfText);
					
					if(i == pkIndex){
						update_sql = update_sql;
					}else if(i == (jt.tf.size()-1)){
						update_sql = update_sql + jt.columnNames.get(i)+"="+ upAttr+" where "+pkColName+"="+jt.tf.get(pkIndex).getText();
					}else{
						update_sql = update_sql + jt.columnNames.get(i)+"="+ upAttr+", ";
					}
					i++;
				}
				System.out.println(update_sql);

				// update 실행
				int j = jt.stmt.executeUpdate(update_sql);
				if(j>0){
					String sql2 = "select * from "+selectedT+" order by "+pkColName;		
					jt.columnNames.clear();
					jt.rowData.clear();
					jt.getTContent(sql2);
					jt.model.setDataVector(jt.rowData, jt.columnNames);
					pln("update 성공");
					clearTf(jt.t.getColumnCount());
				} else {
					pln("update 실패 알림창");
				}
			} catch(SQLException se){
				System.out.println("updated 예외: "+se);
				pln("update 실패 알림창");
			}

		}else if(obj == jt.bDelete){	// delete
			pln("delete");
			// delete DD where NO=3;
			String selectedT = jt.tCombo.getSelectedItem().toString();
			String pkColName = jt.getPkColumn();
			int pkIndex = jt.getPkIndex(pkColName);

			String del_sql = "delete " + selectedT + " where " +pkColName+"="+jt.tf.get(pkIndex).getText();
			pln(del_sql);

			// update 실행
			try{
				int j = jt.stmt.executeUpdate(del_sql);
				if(j>0){
					String sql2 = "select * from "+selectedT+" order by "+pkColName;		
					jt.columnNames.clear();
					jt.rowData.clear();
					jt.getTContent(sql2);
					jt.model.setDataVector(jt.rowData, jt.columnNames);
					pln("delete 성공");
					clearTf(jt.t.getColumnCount());
				} else {
					pln("delete 실패 알림창");
				}
			} catch(SQLException se){
				System.out.println("delete 예외: "+se);
				pln("delete 실패 알림창 띄우기...");
			}
		} else if(obj == jt.t){			// jtable click시 tf[]에 값 받아오기
			//pln("table");
			int cc = jt.t.getColumnCount();
			int selectedRow = jt.t.getSelectedRow();
			pln(""+selectedRow); // 0~..
			for(int i=0; i<cc; i++){
				Object data=jt.t.getValueAt(selectedRow, i);
				jt.tf.get(i).setText((String)data);
				//String data=jt.t.getValueAt(selectedRow, i).toString();
				//jt.tf[i].setText(data);
			}
		}
	}
	
	// insert() 이벤트에서 호출하는 메소드
	String columnType(String colType, String tfText){
		String str = "";
		//pln("length() : "+ tfText.length());

		if(tfText.equalsIgnoreCase("null")){
			str = "null";
		} else if((colType.contains("CHAR")) || (colType.contains("LONG")) || (colType.contains("COLB"))){
			pln("문자형");
			if(tfText.length() == 0){
				str = "null";
			} else{
				str = "'"+tfText+"'";
			}
		} else if((colType.contains("NUMBER")) || (colType.contains("FLOAT")) || (colType.contains("DOUBLE"))){
			pln("숫자형");
			if(tfText.length() == 0){
				str = "null";
			} else{
				str = tfText;
			}
		} else if((colType.contains("DATE")) || (colType.contains("TIMESTAMP"))){
			if(tfText.length() == 0){
				str = "null";
			}
			if(tfText.equalsIgnoreCase("SYSDATE")){
				pln("SYSDATE 그냥 반환");
				str = tfText;
			}else if(tfText.equalsIgnoreCase("SYSTIMESTAMP")){
				pln("SYSTIMESTAMP 그냥 반환");
				str = tfText;
			}else{
				pln("홑따옴표 붙여 반환");
				str = "'"+tfText+"'";
			}
		} else if((tfText.length()) == 0){
			pln("null");
			str = "null";
		}
		return str;
	}

	// event 후에 tf[] 공백주기
	void clearTf(int columnCount){
		for(int i=0; i<columnCount; i++){
			jt.tf.get(i).setText("");
		}
	}
	void p(String str){
		System.out.print(str);
	}
	void pln(String str){
		System.out.println(str);
	}
} // end of class



/*MouseListener listener = new MouseListener(){
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){
		JOptionPane.showMessageDialog(null, "마우스 커서가 들어왔어요!!");		//Component parentComponent : 메시지창이 어떤 Frame 에서 보이지게 될 것인지 지정. 보통 null 을 사용
	}
	public void mouseExited(MouseEvent e){
		JOptionPane.showMessageDialog(null, "마우스 커서가 나갔어요!!");
	}
	public void mousePressed(MouseEvent e){} // 눌렀다 떼어질 때
	public void mouseReleased(MouseEvent e){} // 마우스 눌리면 떼든말든 실행
};*/



/*
=== 추가할 것
1. where, group by, oreder by 절 넣기

2. 하단 텍스트 필드 pk면 막아두기
   selectPk 한다음 = >필드막고 => 그 필드값 가져오기

3. exit(0) 버튼 -> 

해야할 것
1. pk col name 구하기 --> 해결
2. pk col index 구하기 --> 해결
3. ComboBox 한글 맨 위로 올리기 -------------->> 문제(columnNames에 한글 추가됨..) --> 해결
   - new Combo()
   - addItem("검색")
   - for (){addItem.vector[i]}
4. exit(0) 버튼 -> stmt, con close() 만들어주기
5. 하단 텍스트 필드 pk면 막아두기
   selectPk 한다음 = >필드막고 => 그 필드값 가져오기 --> 해결


JT.java
해야할 것
	1.coloum이 pk인 textField 비활성화 --> 해결
		1) pk col name 구하기
		2) pk col index 구하기

	2.  primary key 아닌 칼럼의 tf 비활성화 됨 --> 일단 난 그런적 없다

	3. ComboBox 한글 맨 위로 올리기 --> 해결
	   - new Combo()
	   - addItem("검색")
	   - for (){addItem.vector[i]}
	4. 테이블명에 따라 칼럼콤보박스 내용 업데이트 --> 해결
		1) 콤보박스 colName, tName 메소드 분리
5. exit 버튼 만들기
	- 버튼 누르면 view 삭제하고 닫고
	- x키 눌러도 삭제하고 나가기
6. update
	if(BINARY_FLOAT, BINARY_DOUBLE, NUMBER(P,S))
	1) 숫자형 데이터 따옴표X
	2) 문자형, 날짜형, 이진 데이터 타입 홑따옴표 붙여주기
	7. insert
		1) primary key 비활성화 박스 열어주는 기능 --> 해결
		2) 숫자에서 공백나오면 null --> 해결
		3) 추가 누르고 밑에 텍스트 박스 리셋
			- pk값 저장 
			- tf 쫙 열어줘
			- pk text를 메소드로넘겨줘
			- 그리고 pk텍스트 비교해서 같으면 오류띄워 같아서 insert안된다고
			- 다르면 그냥 삽입 ㄱㄱ
		4) 전부 null이면 들어가지 않게 막기 --> 해결
8. 이중 search
	1) 뷰생성
9. create 
   create table cc ()
   create table cc()
10. drop -> ignore / create / where
	11. 검색 기능 backspace 무시하기 --> 해결
12. Vector<String> colType 생성해서 table select 할 때 타입 담기 ??
13. 참조키 칼럼은 tf말고 콤보박스로 선택할 수 있게....(이건 많이 어렵겠다)
	14. 한글은 한글조합이 끝나기 전엔 인식이 안되서 검색에 지장이 생김 --> 해결
		- 검색 엔터눌러도 검색하능하도록 변경 --> 해결


select * from 뷰이름
create view 뷰이름 as select * from EMP
=======================================================================
참고
1. getPrimaryKey()
-> 전방향만 가능한 메소드


	System.out.println("No. of columns : " + rsmd.getColumnCount());
	System.out.println("Column name of 1st column : " + rsmd.getColumnName(2));
	System.out.println("Column type of 1st column : " + rsmd.getColumnTypeName(2));



2. primarykey 칼럼명 찾기
 SELECT column_name FROM all_cons_columns WHERE constraint_name = (
  SELECT constraint_name FROM all_constraints 
  WHERE UPPER(table_name) = UPPER('ACC') AND CONSTRAINT_TYPE = 'P'
);
+

3. table DATA_TYPE 찾기
	SELECT DATA_TYPE FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = 'DEPT';

4. 문자열에 특정 문자가 포함되어 있는지 확인
	1) String contains(): boolean 반환 / 대소문자 가림
	2) String indexOf() : index 반환 / 대소문자 가림

5.
	1) SELECT data_type from all_tab_columns where table_name='tnames' and column_name = '';






https://javafactory.tistory.com/633
*/