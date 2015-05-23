package com.consultoriasolucao.appsolucaosistemas;



import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CodigodeAcesso extends Activity {

	private WebView webview;
	private EditText nomeEditText;
	private EditText txtnm_usuario;
	private TextView txtdslicenca;
	private DatabaseHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_codigodeacesso);
		this.nomeEditText = (EditText) findViewById(R.id.edtcodigoacesso);	
		this.txtnm_usuario = (EditText) findViewById(R.id.txtnm_usuario);
		
		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);	
		
	}
	
	public void InserirCodigo (View v) {	
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from licenca");
		ContentValues values = new ContentValues();
		values.put("dslicenca", this.nomeEditText.getText().toString());
		values.put("ds_usuario", this.txtnm_usuario.getText().toString());
		long resultado = db.insert("licenca", null, values);
		if(resultado != -1 )
		{
		  Toast.makeText(this,"Sucesso!",  Toast.LENGTH_SHORT).show();
		}else
		{
		  Toast.makeText(this, getString(R.string.erro_salvar),
		  Toast.LENGTH_SHORT).show();
		}
		
		this.nomeEditText.setText("");
		}

	public void VerificaCodigo(View view)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT _id, dslicenca FROM licenca",	null);
		cursor.moveToNext();
		
		if (cursor.getCount() !=0)
		{		
		  String id = cursor.getString(1);
		  this.txtdslicenca = (TextView) findViewById(R.id.txtcodacesso);
	      txtdslicenca.setText(id);
	      cursor.close();
		} else Toast.makeText(this, "Não existe código de acesso cadastrado", Toast.LENGTH_LONG).show();
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
	helper.close();
	super.onDestroy();
	}

}
