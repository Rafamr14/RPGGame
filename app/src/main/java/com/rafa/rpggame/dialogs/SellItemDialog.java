package com.rafa.rpggame.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.rafa.rpggame.R;
import com.rafa.rpggame.activities.InventoryActivity;
import com.rafa.rpggame.managers.MarketManager;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.items.ConsumableItem;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.Item;
import com.rafa.rpggame.models.market.Market;

public class SellItemDialog extends DialogFragment {
    private Item item;
    private ImageView itemIcon;
    private TextView itemNameText;
    private TextView itemDescText;
    private EditText priceInput;
    private Button sellButton;
    private Button cancelButton;

    public SellItemDialog(Item item) {
        this.item = item;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sell_item, null);

        // Inicializar vistas
        itemIcon = view.findViewById(R.id.item_icon);
        itemNameText = view.findViewById(R.id.item_name);
        itemDescText = view.findViewById(R.id.item_description);
        priceInput = view.findViewById(R.id.price_input);
        sellButton = view.findViewById(R.id.sell_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Configurar información del ítem
        itemNameText.setText(item.getName());
        itemDescText.setText(item.getDescription());

        // Configurar icono según tipo de ítem
        if (item instanceof EquipableItem) {
            itemIcon.setImageResource(R.drawable.ic_item_equipment);
        } else if (item instanceof ConsumableItem) {
            itemIcon.setImageResource(R.drawable.ic_item_potion);
        } else {
            itemIcon.setImageResource(R.drawable.ic_item_generic);
        }

        // Establecer precio sugerido basado en el valor del ítem
        priceInput.setText(String.valueOf(item.getValue()));

        // Configurar eventos
        sellButton.setOnClickListener(v -> {
            try {
                int price = Integer.parseInt(priceInput.getText().toString());

                if (price <= 0) {
                    Toast.makeText(getActivity(), "Ingresa un precio válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserAccount userAccount = GameDataManager.getCurrentAccount();
                Market market = MarketManager.getMarket();

                boolean success = market.addListing(userAccount, item, price);

                if (success) {
                    Toast.makeText(getActivity(), "Ítem puesto a la venta", Toast.LENGTH_SHORT).show();
                    MarketManager.updateMarket();
                    GameDataManager.updateAccount();
                    dismiss();

                    // Actualizar la actividad de inventario
                    ((InventoryActivity) getActivity()).refreshInventory();
                } else {
                    Toast.makeText(getActivity(), "No se pudo poner el ítem a la venta", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Ingresa un precio válido", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}