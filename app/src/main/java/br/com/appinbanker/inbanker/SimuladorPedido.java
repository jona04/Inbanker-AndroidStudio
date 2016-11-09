package br.com.appinbanker.inbanker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

import br.com.appinbanker.inbanker.util.MaskMoney;
import br.com.appinbanker.inbanker.util.Validador;

public class SimuladorPedido extends AppCompatActivity {

    Button btnCalendar,btn_verificar;
    EditText et_valor,et_calendario;
    String id, nome, url_img, valor_normal;

    // Variable for storing current date and time
    private int mYear, mMonth, mDay,dias_pagamento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simulador_pedido);

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        if(parametro!=null){
            id = parametro.getString("id");
            nome = parametro.getString("nome");
            url_img = parametro.getString("url_img");
        }else{
            finish();
        }

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        Picasso.with(getBaseContext()).load(url_img).into(img);

        TextView tv = (TextView) findViewById(R.id.nome_amigo);
        tv.setText(tv.getText().toString()+nome);

        et_calendario = (EditText) findViewById(R.id.et_calendario);

        et_valor = (EditText) findViewById(R.id.et_valor);
        valor_normal = et_valor.getText().toString();
        et_valor.addTextChangedListener(MaskMoney.insert(et_valor));

        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btn_verificar = (Button) findViewById(R.id.btn_verificar);

        et_calendario.setEnabled(false);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mostraCalendario();

            }
        });

        btn_verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean campos = true;

                boolean campo_valor = Validador.validateNotNull(et_valor.getText().toString());
                if(!campo_valor) {
                    et_valor.setError("Campo vazio");
                    et_valor.setFocusable(true);
                    et_valor.requestFocus();

                    campos = false;
                }
                boolean campo_calendario = Validador.validateNotNull(et_calendario.getText().toString());
                if(!campo_calendario) {
                    et_calendario.setError("Campo vazio");
                    et_calendario.setFocusable(true);
                    et_calendario.requestFocus();

                    campos = false;
                }

                if(campos) {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                    DateTime hoje = new DateTime();
                    DateTime vencimento = fmt.parseDateTime(et_calendario.getText().toString());

                    Days d = Days.daysBetween(hoje, vencimento);
                    dias_pagamento = d.getDays();

                    Intent it = new Intent(SimuladorPedido.this, SimuladorResultado.class);
                    Bundle b = new Bundle();
                    b.putString("id", id);
                    b.putString("nome", nome);
                    b.putString("valor", et_valor.getText().toString());
                    b.putString("url_img", url_img);
                    b.putString("vencimento", et_calendario.getText().toString());
                    b.putInt("dias", dias_pagamento);
                    it.putExtras(b);
                    startActivity(it);

                    Log.i("Scrip", "valor normal = " + valor_normal);

                }
            }
        });
    }

    public void mostraCalendario(){
        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(SimuladorPedido.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                        // Display Selected date in textbox
                        et_calendario.setText(data);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

}
