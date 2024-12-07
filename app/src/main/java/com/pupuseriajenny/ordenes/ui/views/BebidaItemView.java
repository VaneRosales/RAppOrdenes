package com.pupuseriajenny.ordenes.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;

import com.pupuseriajenny.ordenes.R;

public class BebidaItemView extends LinearLayout {

    private TextView tvtBebida;
    private TextView tvtPrecio;
    private ImageView imgBebida;
    private Button restarCantidad;
    private Button aumentarCantidad;
    private EditText cantidadBebida;

    public BebidaItemView(Context context) {
        super(context);
        init(context);
    }

    public BebidaItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BebidaItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_bebida, this, true);
        tvtBebida = findViewById(R.id.tvtBebida);
        tvtPrecio = findViewById(R.id.tvtPrecio);
        imgBebida = findViewById(R.id.imgBebida);
        restarCantidad = findViewById(R.id.restarCantidad);
        aumentarCantidad = findViewById(R.id.aumentarCantidad);
        cantidadBebida = findViewById(R.id.cantidadBebida);
    }

    // Configurar la vista con los datos de la bebida
    public void setBebidaInfo(String nombre, String precio, int imagenResId) {
        tvtBebida.setText(nombre);
        tvtPrecio.setText("Precio: " + precio);
        imgBebida.setImageResource(imagenResId);
    }

    // Obtener la cantidad actual
    public int getCantidad() {
        String cantidadTexto = cantidadBebida.getText().toString();
        return cantidadTexto.isEmpty() ? 0 : Integer.parseInt(cantidadTexto);
    }

    // Incrementar la cantidad
    public void incrementarCantidad() {
        int cantidad = getCantidad();
        cantidadBebida.setText(String.valueOf(cantidad + 1));
    }

    // Decrementar la cantidad
    public void decrementarCantidad() {
        int cantidad = getCantidad();
        if (cantidad > 1) {
            cantidadBebida.setText(String.valueOf(cantidad - 1));
        }
    }

    // Configurar listeners para los botones
    public void setOnRestarListener(OnClickListener listener) {
        restarCantidad.setOnClickListener(listener);
    }

    public void setOnAumentarListener(OnClickListener listener) {
        aumentarCantidad.setOnClickListener(listener);
    }
}
