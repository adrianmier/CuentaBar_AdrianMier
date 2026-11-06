package com.example.cuentabar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //Declaración de variables globales
    private AutoCompleteTextView actCamarero;
    private EditText etTotal;
    private CheckBox chPropina;
    private TextView tvPropinaPorcentaje, tvResultado;
    private SeekBar sbPropina;
    private RadioGroup rgMetodoPago;
    private RadioButton rbEfectivo, rbTarjeta;
    private RatingBar rbCalificacion;
    private Button botonCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        actCamarero = findViewById(R.id.actCamarero);
        etTotal = findViewById(R.id.etTotal);
        chPropina = findViewById(R.id.chPropina);
        tvPropinaPorcentaje = findViewById(R.id.tvPropina_porcentaje);
        sbPropina = findViewById(R.id.sbPropina);
        rgMetodoPago = findViewById(R.id.rgMetodo_pago);
        rbEfectivo = findViewById(R.id.rbEfectivo);
        rbTarjeta = findViewById(R.id.rbTarjeta);
        rbCalificacion = findViewById(R.id.rbCalificacion);
        botonCalcular = findViewById(R.id.botonCalcular);
        tvResultado = findViewById(R.id.tvResultado);

        //Guardamos el array en java de la lista de camareros que habíamos declarado antes en strings.xml.
        String[] camareros = getResources().getStringArray(R.array.lista_camareros);
        //Recibimos el array, convertimos cada nombre en un elemnto visual de una lista y filtramos los datos a medoda que el usuario escribe y mostramos sugerencias.
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, camareros);
        //Asignamos el adaptador al camarero
        actCamarero.setAdapter(adaptador);

        sbPropina.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Cambiamos el texto de propina porcentaje cogiendo el string que teníamos guardado en el xml.
                tvPropinaPorcentaje.setText(getString(R.string.tvPropina_porcentaje, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        botonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cogemos lo que haya escrito, lo pasamos a string y le quitamos los espacios que le sobran.
                String textoTotal = etTotal.getText().toString().trim();

                //Si el texto que ha ingresado esta vacio mostramos la string de cuenta nula y convertimos el color del texto de resultado a rojo
                if (textoTotal.isEmpty()){
                    tvResultado.setText(R.string.cuenta_nula);
                    tvResultado.setTextColor(Color.RED);
                    return;
                }

                try{
                    //Convertimos el textoTotal a número
                    double totalCuenta = Double.parseDouble(textoTotal);
                    //Si total cuenta es < o = ejecutamos el texto de error por cuenta negativa y convertimos el color del texto de resultado en rojo
                    if (totalCuenta<=0){
                        tvResultado.setText(R.string.cuenta_negativa);
                        tvResultado.setTextColor(Color.RED);
                        return;
                    }
                    //Si todo es correcto convertimos el color del texto de resultado a negro
                    tvResultado.setTextColor(Color.BLACK);

                    double propina = 0;
                    double totalFinal = 0;
                    //Si chPropina esta marca total final se le suma la propina
                    if (chPropina.isChecked()){
                        int porcentaje = sbPropina.getProgress();
                        propina = totalCuenta * (porcentaje / 100.00);
                    }
                    totalFinal = totalCuenta + propina;

                    //Cambiamos el método de pago comprobando cuál de los dos RadioButton está marcado
                    String metodoPago = "";
                    if (rbEfectivo.isChecked()) {
                        metodoPago = getString(R.string.rbEfectivo);
                    } else {
                        metodoPago = getString(R.string.rbTarjeta);
                    }

                    //Obtenemos la cantidad de estrellas que ha puesto
                    float estrellas = rbCalificacion.getRating();

                    //Obtenemos el nombre del camarero
                    String nombreCamarero = actCamarero.getText().toString().trim();
                    if (nombreCamarero.isEmpty()){
                        nombreCamarero="No hay camarero";
                    }

                    //Redondeo a 2 decimales
                    double totalCuentaRedondeado = Math.round(totalCuenta * 100.0) / 100.0;
                    double propinaRedondeada = Math.round(propina * 100.0) / 100.0;
                    double totalFinalRedondeado = Math.round(totalFinal * 100.0) / 100.0;

                    String resultadoCuenta = "El camarero " + nombreCamarero +
                            " ha atendido una mesa con una cuenta total de " + totalCuentaRedondeado + "€" +
                            " (propina de " + propinaRedondeada + "€). " +
                            "El total a pagar es " + totalFinalRedondeado + "€" +
                            " pagado mediante " + metodoPago +
                            " con una calificación de " + estrellas + " estrellas.";
                    tvResultado.setText(resultadoCuenta);

                }catch (NumberFormatException e){
                    //Si el resultado no se puede convertir a número ejecutamos el texto de error por cuenta inválida y convertimos el color del texto de resultado a rojo
                    tvResultado.setTextColor(Color.RED);
                    tvResultado.setText(R.string.cuenta_invalida);
                }

            }
        });

    }
}