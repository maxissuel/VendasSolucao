package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class LancaPedido extends Activity implements OnItemClickListener {

	TextView txtcd_pedido;
	TextView txttt_pedido;
	TextView txtcd_cli;
	TextView txtds_obs;
	TextView txtcd_tabelapreco;
		Button btdt_lancamento;
	Button btsalvaPedido;
	private Spinner spnds_formapgto;
	
	Button btcd_cli;
	Integer dia, mes, ano;
	Date dt_lancamento;
	private SimpleDateFormat dateFormat;
	private DatabaseHelper helper;
	public static final String EXTRA_CD_CLI = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_CLI";
	public static final String EXTRA_CD_PEDIDO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PEDIDO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancapedido);
		helper = new DatabaseHelper(this);

		this.txtcd_pedido = (TextView) findViewById(R.id.txtcd_pedido);
		this.txttt_pedido = (TextView) findViewById(R.id.txttt_pedido);
		this.txtds_obs = (TextView) findViewById(R.id.txtds_obs);
		this.txtcd_cli = (TextView) findViewById(R.id.txtcd_cli);
		this.txtcd_tabelapreco = (TextView) findViewById(R.id.edtcd_tabelapreco);
				this.btcd_cli = (Button) findViewById(R.id.btcd_cli);
		this.btsalvaPedido = (Button) findViewById(R.id.btsalvapedido); 	

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ds_formapgto,android.R.layout.simple_spinner_item);
		spnds_formapgto = (Spinner) findViewById(R.id.spnds_formapgto);
		spnds_formapgto.setAdapter(adapter1);
		
					 
		
		
		
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		btdt_lancamento = (Button) findViewById(R.id.btdt_lancamento);
		btdt_lancamento.setText(dia + "/" + (mes + 1) + "/" + ano);


		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_CD_PEDIDO)) 
		{//caso seja edição então carregando os campos
			
			
			SQLiteDatabase dbexe = helper.getReadableDatabase();
			Cursor cursor = dbexe.rawQuery(
					"SELECT _id, cd_cli, dt_lancamento, vl_total,ds_obs, ds_formapgto, cd_tabelapreco,vl_desconto from pedido where _id="+ intent.getStringExtra(EXTRA_CD_PEDIDO), null);
			cursor.moveToNext();
			txtcd_pedido.setText(cursor.getString(0));
			txtds_obs.setText(cursor.getString(4));
			
			txtcd_cli.setText(cursor.getString(1));
			txtcd_tabelapreco.setText(cursor.getString(6).toString());
			
			//verificando qual item do sppiner foi selecinado									 	
			int spinnerPosition = adapter1.getPosition(cursor.getString(5));
			spnds_formapgto.setSelection(spinnerPosition);
			
			
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor1 = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor1.moveToFirst();
			btcd_cli.setText(cursor1.getString(1).toString());
			
			cursor1.close();
			
			long dataChegada = cursor.getLong(2);
			Date dataChegadaDate = new Date(dataChegada);
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String periodo = dateFormat.format(dataChegadaDate);
			btdt_lancamento.setText(periodo);
			atualizavalorespedido(txtcd_pedido.getText().toString());
			
			
				
			}		
		
	}

	public void selecionarDataLancamento(View view) {
		showDialog(view.getId());

	}

	private OnDateSetListener listener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			ano = year;
			mes = monthOfYear;
			dia = dayOfMonth;

			btdt_lancamento.setText(dia + "/" + (mes + 1) + "/" + ano);

		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (R.id.btdt_lancamento == id) {
			return new DatePickerDialog(this, listener, ano, mes, dia);
		}

		return null;
	}

	public void consultaCliente(View view) {
		if (txtcd_cli.getText().toString().equals("") ) //caso não seja consulta de cliente por código
		{
		Intent intent = new Intent(this, ConsultaCliente.class);
		intent.putExtra(EXTRA_CD_CLI, true);
		startActivityForResult(intent, 1);
		}
		else
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			
			
			Cursor cursor = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			
			btcd_cli.setText(cursor.getString(1).toString());				
		}
		
		
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
		{
					
			txtcd_cli.setText(data.getStringExtra(EXTRA_CD_CLI));
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			btcd_cli.setText(cursor.getString(1).toString());
			
			
		}
		
	}

	

	public void salvaPedido(View view)
	{
		Boolean flagvalida = true;
		if (btcd_cli.getText().toString().equals("Cliente")) {
			Toast.makeText(this, "Entre com o cliente!", Toast.LENGTH_SHORT)
					.show();
			flagvalida = false;
		}

		if (flagvalida) {
			SQLiteDatabase db = helper.getWritableDatabase();

	
			

			
			
			// primeiro tem que verificar se ja foi inserido o registro do
			// pedido
			
				ContentValues values = new ContentValues();
				dt_lancamento = ConvertToDate(btdt_lancamento.getText()
						.toString());
				values.put("dt_lancamento", dt_lancamento.getTime());
				values.put("vl_bruto", 0);
				values.put("vl_desconto", 0);
				values.put("vl_total", 0);
				values.put("cd_cli", txtcd_cli.getText().toString());
				values.put("ds_obs", txtds_obs.getText().toString());
				values.put("ds_formapgto", spnds_formapgto.getSelectedItem().toString());
				values.put("cd_tabelapreco", txtcd_tabelapreco.getText().toString());
										
				if (txtcd_pedido.getText().equals("")) {
				long resultado = db.insert("pedido", null, values);
				txtcd_pedido.setText(resultado+"");
				} else db.update("pedido",  values, "_id ="+txtcd_pedido.getText().toString(),null);
				
				
				
				btsalvaPedido.setVisibility(View.INVISIBLE);	
				

			

		
			atualizavalorespedido(txtcd_pedido.getText().toString());
			
		}// fim if flagvalida
	}

	
	
	public void atualizavalorespedido(String cd_pedido) {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"select coalesce(sum(vl_iten*qt_iten),0) from itenspedido  where cd_pedido= "
						+ cd_pedido, null);
		c.moveToFirst();
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("vl_total", c.getDouble(0));
		DecimalFormat df = new DecimalFormat(",##0.00");
		txttt_pedido.setText(df.format(c.getDouble(0))+"");
		
		
	}



	private Date ConvertToDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;
	}

	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_itenspedido, menu);
		
	}

	
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}
	

}
