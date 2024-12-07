package com.pupuseriajenny.ordenes.ui.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.data.model.Producto;
import com.pupuseriajenny.ordenes.ui.listener.BebidaActionsListener;

import java.util.ArrayList;
import java.util.List;

public class BebidaAdapter extends RecyclerView.Adapter<BebidaAdapter.BebidaViewHolder> {

    private List<Producto> bebidaList;
    private final BebidaActionsListener actionsListener;

    public BebidaAdapter(List<Producto> bebidaList, BebidaActionsListener actionsListener) {
        this.bebidaList = bebidaList; // Asegurarse de que la lista no sea null
        this.actionsListener = actionsListener;
    }

    @NonNull
    @Override
    public BebidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bebida, parent, false);
        return new BebidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BebidaViewHolder holder, int position) {
        Producto bebida = bebidaList.get(position);
        holder.nombreBebida.setText(bebida.getNombreProducto());
        holder.precioBebida.setText("$" + bebida.getPrecioProducto());
        holder.imagenBebida.setImageResource(bebida.getImagenResourceId());
        holder.cantidadBebida.setText(String.valueOf(bebida.getCantidad()));

        // Acción para aumentar la cantidad
        holder.aumentarCantidad.setOnClickListener(v -> {
            bebida.setCantidad(bebida.getCantidad() + 1); // Aumentar cantidad
            holder.cantidadBebida.setText(String.valueOf(bebida.getCantidad())); // Actualizar UI
            actionsListener.actualizarBebidasSeleccionadas(bebida); // Notificar cambio
        });
        holder.cantidadBebida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int cantidad = Integer.parseInt(s.toString());
                    if (cantidad >= 0) {
                        bebida.setCantidad(cantidad);
                        actionsListener.actualizarBebidasSeleccionadas(bebida);
                    } else {
                        holder.cantidadBebida.setText("0"); // Revertir a 0 si el número es negativo
                    }
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, por ejemplo, el campo está vacío
                    holder.cantidadBebida.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Acción para disminuir la cantidad
        holder.restarCantidad.setOnClickListener(v -> {
            if (bebida.getCantidad() > 0) { // Verificar que la cantidad no sea negativa
                bebida.setCantidad(bebida.getCantidad() - 1); // Disminuir cantidad
                holder.cantidadBebida.setText(String.valueOf(bebida.getCantidad())); // Actualizar UI
                actionsListener.actualizarBebidasSeleccionadas(bebida); // Notificar cambio
            }
        });
    }

    @Override
    public int getItemCount() {
        return bebidaList.size();
    }

    // Método para actualizar la lista de bebidas en el adaptador
    public void actualizarLista(List<Producto> nuevaLista) {
        this.bebidaList = nuevaLista;
        notifyDataSetChanged(); // Notifica los cambios al RecyclerView
    }

    // ViewHolder para los elementos de la lista
    public class BebidaViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagenBebida;
        public TextView nombreBebida, precioBebida, cantidadBebida;
        public Button aumentarCantidad, restarCantidad;

        public BebidaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenBebida = itemView.findViewById(R.id.imgBebida);
            nombreBebida = itemView.findViewById(R.id.tvtBebida);
            precioBebida = itemView.findViewById(R.id.tvtPrecio);
            cantidadBebida = itemView.findViewById(R.id.cantidadBebida);
            aumentarCantidad = itemView.findViewById(R.id.aumentarCantidad);
            restarCantidad = itemView.findViewById(R.id.restarCantidad);
        }
    }

}