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
	//JTextField tf[];				// Į�� ������ŭ �ؽ�Ʈ �ڽ� �����
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
		// component ����
		cp = getContentPane();
		model = new DefaultTableModel();
		t = new JTable(model);
//		getTContent("select * from DEPT");
		model.setDataVector(rowData, columnNames);
		sp = new JScrollPane(t);
		p1 = new JPanel(new GridLayout(1,2));
		p3 = new JPanel(new BorderLayout());
		p31 = new JPanel(new GridLayout(1,cc));// ���� �� ���� t�� columnCount()��
		p32 = new JPanel(new FlowLayout());

		tCombo = new JComboBox();
		colCombo = new JComboBox();				// JComboBox(Vector name)
		text1 = new JTextField();				// keyboard �̺�Ʈ �߰����ֱ�
		bInsert = new JButton("�߰�");
		bUpdate = new JButton("����");
		bDelete = new JButton("����");

		bInsert.setPreferredSize(new Dimension(120, 30));
		bUpdate.setPreferredSize(new Dimension(120, 30));
		bDelete.setPreferredSize(new Dimension(120, 30));

		// event �߰�
		bInsert.addMouseListener(new MyHandler(this));
		bUpdate.addMouseListener(new MyHandler(this));
		bDelete.addMouseListener(new MyHandler(this));
		tCombo.addActionListener(new MyHandler(this));
		text1.addKeyListener(new MyHandler(this));
		t.addMouseListener(new MyHandler(this));
		//t.addDocumentListener(new MyHandler(this));
		
		// �����̳ʿ� �߰�
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
	void addTextField(){ // Į�� ������ŭ �ؽ�Ʈ�ʵ� ���� �� ��ġ
		cc = t.getColumnCount();
		//tf = new JTextField[cc];
		tf.clear();
		String pkColName = getPkColumn();
		int pkColIndex = getPkIndex(pkColName);
		pln("pk ���� ��� ����: "+ pkColIndex);

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
		tCombo.addItem("�˻��� ���̺��� �������ּ���.");
		for(int i=0; i<rc; i++){
			tCombo.addItem(tableNames.get(i));
		}
		setColCombo();
		cp.revalidate();
		cp.repaint();
	}
	void setColCombo(){
		colCombo.removeAllItems();
		colCombo.addItem("�˻��� Į���� �������ּ���.");
		for(int i=0; i<cc; i++){
			colCombo.addItem(columnNames.get(i));
		}
		cp.revalidate();
		cp.repaint();
	}

	int rc;
	void getTname(){
		// ���߿� table�̸� �����ؾ��Ҷ� -> 
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
//			tCombo.addItem("�˻��� ���̺��� �������ּ���.");
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
			pln("ddlSql() ����: "+se);
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
			pln("getPkColumn() ����: "+se);
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
				//pln("pkColIndex ��ȣ: "+pkColIndex);
			}
			pkColIndex = -1;
			//pln("pk ���� ��� ��ȯ -1:" + i);

		}
		return pkColIndex;
	}
	
	// ??
//	void sqlExcute(String sql){
//		String[] sqlArr = sql.split(" ");
//		// DDL
//		if(sqlArr[0].equalsIgnoreCase("create") || sqlArr[0].equalsIgnoreCase("drop") || sqlArr[0].equalsIgnoreCase("alter") || sqlArr[0].equalsIgnoreCase("rename") || sqlArr[0].equalsIgnoreCase("truncate")){
//			try{
//				stmt.execute(sql);	// create table �����ϴ� ���̺��̸� ���� �߰�
//				pln(sqlArr[0]+"���� ����Ǿ����ϴ�.");
//			} catch(SQLException se){
//				pln(sqlArr[0]+"�� �������: " + se);
//			}
//		} else if(sqlArr[0].equalsIgnoreCase("insert") || sqlArr[0].equalsIgnoreCase("update") || sqlArr[0].equalsIgnoreCase("delete")){
//			try{ // DML
//				int i = stmt.executeUpdate(sql);
//				if(i>0) pln(i+"���� row ����("+sqlArr[0]+"��)");
//				else pln("�Էµ��� �ʾҽ��ϴ�.");
//			} catch(SQLException se){
//				pln(sqlArr[0]+"�� ��������: " + se);
//			}
//		}else if(sqlArr[0].equalsIgnoreCase("select")){
//			//selectT(sql);
//		} else if(sql.equalsIgnoreCase("exit")){
//			closeAll();
//			System.exit(0);
//		}else{
//			pln("�ٸ� ��ɾ �Է����ּ���.");
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
//			pln("selectT() ����: "+se);
//		}
//	}

	void closeAll(){
		try{
			stmt.close();
			con.close();
		} catch(SQLException se){
			pln("closeAlll() ����" + se);
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



// ���� ���콺, Ű����(�����, ������) �ٲ㼭 ����غ���..
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

	// KeyListener�������̽� ������ key�̺�Ʈ �޼ҵ� ���� ���
	public void keyTyped(KeyEvent e){ // �˻� ���
		String word = "";
		if( e.paramString().indexOf("Backspace") != -1){
			word = jt.text1.getText();
		}else if(e.getKeyCode() == 0){ // enter ������ �˻�
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
	public void keyPressed(KeyEvent e) {}// ��� Ű�� ���������� ��ҹ��� ������ ���Ѵ�.

	// mouseAdapter
	public void mouseClicked(MouseEvent e){
		Object obj = e.getSource(); 
		if(obj == jt.bInsert){			// insert
			pln("insert");
			
			String pkColName = jt.getPkColumn();
			int pkIndex = jt.getPkIndex(pkColName); // -1�ΰ��..
//			String pkText = jt.tf.get(pkIndex).getText();
//			pln(pkText);
			if(jt.insertFlag){
				if(pkIndex >= 0){
					jt.tf.get(pkIndex).setEnabled(true);
				}
				jt.insertFlag = false; // ��� Ǯ����
			} else{
				// insert into DEPT values (90, 'help', 'seoul');
				String selectedT = jt.tCombo.getSelectedItem().toString();
				String insert_sql = "insert into " + selectedT + " values(";
				String sql = "select DATA_TYPE from ALL_TAB_COLUMNS where TABLE_NAME = '"+selectedT+"'";

				ResultSet rs;
				try{
					// ����� ��: ������, ������, ��¥��(DATE��)/ (LOB ���߿� ����)
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
								throw new Exception("��� null���� ��� ���� ���� �� �����ϴ�.(���â)");
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
						pln("���� �Ϸ�");
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
						pln("insert�� �����߽��ϴ�.");
						jt.tf.get(pkIndex).setEnabled(false);
						jt.insertFlag = true;
					}
				} catch(SQLException se){
					pln("insert ����: "+ se);
					pln("insert�� �����߽��ϴ�.(�⺻Ű, ����Ű, ������ Ÿ���� Ȯ���� �ּ���.)���â ����ֱ�...");
					jt.tf.get(pkIndex).setEnabled(false);
					jt.insertFlag = true;
				} catch(Exception ee){
					pln("��� null���� ��� ���� ���� �� �����ϴ�.(���â)....");
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
			
			// update sql�� �����
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

				// update ����
				int j = jt.stmt.executeUpdate(update_sql);
				if(j>0){
					String sql2 = "select * from "+selectedT+" order by "+pkColName;		
					jt.columnNames.clear();
					jt.rowData.clear();
					jt.getTContent(sql2);
					jt.model.setDataVector(jt.rowData, jt.columnNames);
					pln("update ����");
					clearTf(jt.t.getColumnCount());
				} else {
					pln("update ���� �˸�â");
				}
			} catch(SQLException se){
				System.out.println("updated ����: "+se);
				pln("update ���� �˸�â");
			}

		}else if(obj == jt.bDelete){	// delete
			pln("delete");
			// delete DD where NO=3;
			String selectedT = jt.tCombo.getSelectedItem().toString();
			String pkColName = jt.getPkColumn();
			int pkIndex = jt.getPkIndex(pkColName);

			String del_sql = "delete " + selectedT + " where " +pkColName+"="+jt.tf.get(pkIndex).getText();
			pln(del_sql);

			// update ����
			try{
				int j = jt.stmt.executeUpdate(del_sql);
				if(j>0){
					String sql2 = "select * from "+selectedT+" order by "+pkColName;		
					jt.columnNames.clear();
					jt.rowData.clear();
					jt.getTContent(sql2);
					jt.model.setDataVector(jt.rowData, jt.columnNames);
					pln("delete ����");
					clearTf(jt.t.getColumnCount());
				} else {
					pln("delete ���� �˸�â");
				}
			} catch(SQLException se){
				System.out.println("delete ����: "+se);
				pln("delete ���� �˸�â ����...");
			}
		} else if(obj == jt.t){			// jtable click�� tf[]�� �� �޾ƿ���
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
	
	// insert() �̺�Ʈ���� ȣ���ϴ� �޼ҵ�
	String columnType(String colType, String tfText){
		String str = "";
		//pln("length() : "+ tfText.length());

		if(tfText.equalsIgnoreCase("null")){
			str = "null";
		} else if((colType.contains("CHAR")) || (colType.contains("LONG")) || (colType.contains("COLB"))){
			pln("������");
			if(tfText.length() == 0){
				str = "null";
			} else{
				str = "'"+tfText+"'";
			}
		} else if((colType.contains("NUMBER")) || (colType.contains("FLOAT")) || (colType.contains("DOUBLE"))){
			pln("������");
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
				pln("SYSDATE �׳� ��ȯ");
				str = tfText;
			}else if(tfText.equalsIgnoreCase("SYSTIMESTAMP")){
				pln("SYSTIMESTAMP �׳� ��ȯ");
				str = tfText;
			}else{
				pln("Ȭ����ǥ �ٿ� ��ȯ");
				str = "'"+tfText+"'";
			}
		} else if((tfText.length()) == 0){
			pln("null");
			str = "null";
		}
		return str;
	}

	// event �Ŀ� tf[] �����ֱ�
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
		JOptionPane.showMessageDialog(null, "���콺 Ŀ���� ���Ծ��!!");		//Component parentComponent : �޽���â�� � Frame ���� �������� �� ������ ����. ���� null �� ���
	}
	public void mouseExited(MouseEvent e){
		JOptionPane.showMessageDialog(null, "���콺 Ŀ���� �������!!");
	}
	public void mousePressed(MouseEvent e){} // ������ ������ ��
	public void mouseReleased(MouseEvent e){} // ���콺 ������ ���縻�� ����
};*/



/*
=== �߰��� ��
1. where, group by, oreder by �� �ֱ�

2. �ϴ� �ؽ�Ʈ �ʵ� pk�� ���Ƶα�
   selectPk �Ѵ��� = >�ʵ帷�� => �� �ʵ尪 ��������

3. exit(0) ��ư -> 

�ؾ��� ��
1. pk col name ���ϱ� --> �ذ�
2. pk col index ���ϱ� --> �ذ�
3. ComboBox �ѱ� �� ���� �ø��� -------------->> ����(columnNames�� �ѱ� �߰���..) --> �ذ�
   - new Combo()
   - addItem("�˻�")
   - for (){addItem.vector[i]}
4. exit(0) ��ư -> stmt, con close() ������ֱ�
5. �ϴ� �ؽ�Ʈ �ʵ� pk�� ���Ƶα�
   selectPk �Ѵ��� = >�ʵ帷�� => �� �ʵ尪 �������� --> �ذ�


JT.java
�ؾ��� ��
	1.coloum�� pk�� textField ��Ȱ��ȭ --> �ذ�
		1) pk col name ���ϱ�
		2) pk col index ���ϱ�

	2.  primary key �ƴ� Į���� tf ��Ȱ��ȭ �� --> �ϴ� �� �׷��� ����

	3. ComboBox �ѱ� �� ���� �ø��� --> �ذ�
	   - new Combo()
	   - addItem("�˻�")
	   - for (){addItem.vector[i]}
	4. ���̺�� ���� Į���޺��ڽ� ���� ������Ʈ --> �ذ�
		1) �޺��ڽ� colName, tName �޼ҵ� �и�
5. exit ��ư �����
	- ��ư ������ view �����ϰ� �ݰ�
	- xŰ ������ �����ϰ� ������
6. update
	if(BINARY_FLOAT, BINARY_DOUBLE, NUMBER(P,S))
	1) ������ ������ ����ǥX
	2) ������, ��¥��, ���� ������ Ÿ�� Ȭ����ǥ �ٿ��ֱ�
	7. insert
		1) primary key ��Ȱ��ȭ �ڽ� �����ִ� ��� --> �ذ�
		2) ���ڿ��� ���鳪���� null --> �ذ�
		3) �߰� ������ �ؿ� �ؽ�Ʈ �ڽ� ����
			- pk�� ���� 
			- tf �� ������
			- pk text�� �޼ҵ�γѰ���
			- �׸��� pk�ؽ�Ʈ ���ؼ� ������ ������� ���Ƽ� insert�ȵȴٰ�
			- �ٸ��� �׳� ���� ����
		4) ���� null�̸� ���� �ʰ� ���� --> �ذ�
8. ���� search
	1) �����
9. create 
   create table cc ()
   create table cc()
10. drop -> ignore / create / where
	11. �˻� ��� backspace �����ϱ� --> �ذ�
12. Vector<String> colType �����ؼ� table select �� �� Ÿ�� ��� ??
13. ����Ű Į���� tf���� �޺��ڽ��� ������ �� �ְ�....(�̰� ���� ��ưڴ�)
	14. �ѱ��� �ѱ������� ������ ���� �ν��� �ȵǼ� �˻��� ������ ���� --> �ذ�
		- �˻� ���ʹ����� �˻��ϴ��ϵ��� ���� --> �ذ�


select * from ���̸�
create view ���̸� as select * from EMP
=======================================================================
����
1. getPrimaryKey()
-> �����⸸ ������ �޼ҵ�


	System.out.println("No. of columns : " + rsmd.getColumnCount());
	System.out.println("Column name of 1st column : " + rsmd.getColumnName(2));
	System.out.println("Column type of 1st column : " + rsmd.getColumnTypeName(2));



2. primarykey Į���� ã��
 SELECT column_name FROM all_cons_columns WHERE constraint_name = (
  SELECT constraint_name FROM all_constraints 
  WHERE UPPER(table_name) = UPPER('ACC') AND CONSTRAINT_TYPE = 'P'
);
+

3. table DATA_TYPE ã��
	SELECT DATA_TYPE FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = 'DEPT';

4. ���ڿ��� Ư�� ���ڰ� ���ԵǾ� �ִ��� Ȯ��
	1) String contains(): boolean ��ȯ / ��ҹ��� ����
	2) String indexOf() : index ��ȯ / ��ҹ��� ����

5.
	1) SELECT data_type from all_tab_columns where table_name='tnames' and column_name = '';






https://javafactory.tistory.com/633
*/