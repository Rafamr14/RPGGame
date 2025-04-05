package com.rafa.rpggame.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.ItemAdapter;
import com.rafa.rpggame.adapters.MarketListingAdapter;
import com.rafa.rpggame.managers.MarketManager;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.character.CharacterRole;
import com.rafa.rpggame.models.items.Item;
import com.rafa.rpggame.models.items.ItemRarity;
import com.rafa.rpggame.models.market.Market;
import com.rafa.rpggame.models.market.MarketListing;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MarketActivity extends AppCompatActivity {
    private UserAccount userAccount;
    private Market market;

    private TabHost tabHost;
    private TextView coinsText;

    // Tab de búsqueda
    private EditText searchEditText;
    private Spinner raritySpinner;
    private Spinner classSpinner;
    private EditText minLevelEdit;
    private EditText maxPriceEdit;
    private Button searchButton;
    private ListView marketListView;
    private Button buyButton;

    // Tab de venta
    private ListView inventoryListView;
    private EditText priceEditText;
    private Button sellButton;

    // Tab de mis ventas
    private ListView myListingsView;
    private Button cancelListingButton;

    // Tab de personajes (Nueva)
    private ListView characterMarketListView;
    private Button buyCharacterButton;

    private MarketListingAdapter marketAdapter;
    private ItemAdapter inventoryAdapter;
    private MarketListingAdapter myListingsAdapter;
    private CharacterTemplateAdapter characterAdapter;

    private MarketListing selectedListing;
    private Item selectedInventoryItem;
    private CharacterTemplate selectedCharacterTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        // Obtener datos
        userAccount = UserAccountManager.getCurrentAccount();
        market = MarketManager.getMarket();

        // Inicializar vistas
        tabHost = findViewById(R.id.market_tab_host);
        coinsText = findViewById(R.id.coins_text);

        // Configurar tabs
        tabHost.setup();

        TabHost.TabSpec charactersTab = tabHost.newTabSpec("characters");
        charactersTab.setIndicator("Personajes");
        charactersTab.setContent(R.id.characters_tab);
        tabHost.addTab(charactersTab);

        TabHost.TabSpec buyTab = tabHost.newTabSpec("buy");
        buyTab.setIndicator("Comprar");
        buyTab.setContent(R.id.buy_tab);
        tabHost.addTab(buyTab);

        TabHost.TabSpec sellTab = tabHost.newTabSpec("sell");
        sellTab.setIndicator("Vender");
        sellTab.setContent(R.id.sell_tab);
        tabHost.addTab(sellTab);

        TabHost.TabSpec myListingsTab = tabHost.newTabSpec("myListings");
        myListingsTab.setIndicator("Mis Ventas");
        myListingsTab.setContent(R.id.my_listings_tab);
        tabHost.addTab(myListingsTab);

        // Inicializar vistas de cada tab
        initCharactersTab();
        initBuyTab();
        initSellTab();
        initMyListingsTab();

        updateUI();
    }

    private void initCharactersTab() {
        characterMarketListView = findViewById(R.id.character_market_list_view);
        buyCharacterButton = findViewById(R.id.buy_character_button);

        // Crear una lista de personajes disponibles para comprar
        List<CharacterTemplate> availableCharacters = createAvailableCharacters();

        // Crear adaptador para mostrar los personajes en venta
        characterAdapter = new CharacterTemplateAdapter(this, availableCharacters);
        characterMarketListView.setAdapter(characterAdapter);
        characterMarketListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCharacterTemplate = availableCharacters.get(position);

            // Solo habilitar el botón si no está ya comprado
            boolean alreadyOwned = characterAdapter.isCharacterOwned(selectedCharacterTemplate);
            buyCharacterButton.setEnabled(!alreadyOwned);

            if (alreadyOwned) {
                Toast.makeText(this, "Ya posees este personaje", Toast.LENGTH_SHORT).show();
            }
        });

        // Manejar la selección de personaje
        characterMarketListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCharacterTemplate = availableCharacters.get(position);
            buyCharacterButton.setEnabled(true);
        });

        // Manejar la compra del personaje
        buyCharacterButton.setOnClickListener(v -> {
            if (selectedCharacterTemplate != null) {
                if (userAccount.getCoins() >= selectedCharacterTemplate.getPrice()) {
                    // Comprar el personaje
                    userAccount.reduceCoins(selectedCharacterTemplate.getPrice());

                    // Crear el personaje y añadirlo a la cuenta
                    Character newCharacter = new Character(
                            selectedCharacterTemplate.getName(),
                            selectedCharacterTemplate.getCharacterClass(),
                            selectedCharacterTemplate.getRole()
                    );
                    userAccount.addCharacter(newCharacter);

                    // Seleccionar automáticamente si es el primer personaje
                    if (userAccount.getCharacters().size() == 1) {
                        userAccount.setSelectedCharacter(newCharacter);
                    }

                    // Guardar cambios
                    UserAccountManager.updateAccount();

                    Toast.makeText(this, "¡Personaje comprado con éxito!", Toast.LENGTH_SHORT).show();

                    // Actualizar la interfaz inmediatamente
                    characterAdapter.notifyDataSetChanged();
                    updateUI();
                } else {
                    Toast.makeText(this, "No tienes suficientes monedas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBuyTab() {
        searchEditText = findViewById(R.id.search_edit_text);
        raritySpinner = findViewById(R.id.rarity_spinner);
        classSpinner = findViewById(R.id.class_spinner);
        minLevelEdit = findViewById(R.id.min_level_edit);
        maxPriceEdit = findViewById(R.id.max_price_edit);
        searchButton = findViewById(R.id.search_button);
        marketListView = findViewById(R.id.market_list_view);
        buyButton = findViewById(R.id.buy_button);

        // Configurar spinners
        ArrayAdapter<ItemRarity> rarityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ItemRarity.values());
        rarityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raritySpinner.setAdapter(rarityAdapter);

        ArrayAdapter<CharacterClass> classAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CharacterClass.values());
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        // Configurar adaptador de listados
        marketAdapter = new MarketListingAdapter(this, new ArrayList<>());
        marketListView.setAdapter(marketAdapter);

        // Eventos
        marketListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedListing = marketAdapter.getItem(position);
            buyButton.setEnabled(true);
        });

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            ItemRarity minRarity = (ItemRarity) raritySpinner.getSelectedItem();
            CharacterClass forClass = (CharacterClass) classSpinner.getSelectedItem();

            int minLevel = 0;
            int maxPrice = 0;

            try {
                if (!minLevelEdit.getText().toString().isEmpty()) {
                    minLevel = Integer.parseInt(minLevelEdit.getText().toString());
                }

                if (!maxPriceEdit.getText().toString().isEmpty()) {
                    maxPrice = Integer.parseInt(maxPriceEdit.getText().toString());
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            List<MarketListing> results = market.searchListings(query, minRarity, minLevel, maxPrice, forClass);
            marketAdapter.updateListings(results);
            selectedListing = null;
            buyButton.setEnabled(false);
        });

        buyButton.setOnClickListener(v -> {
            if (selectedListing != null) {
                boolean success = selectedListing.purchase(userAccount);

                if (success) {
                    Toast.makeText(this, "Compra exitosa", Toast.LENGTH_SHORT).show();
                    // Actualizar listas
                    searchButton.performClick();
                    updateUI();
                } else {
                    Toast.makeText(this, "No se pudo realizar la compra", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSellTab() {
        inventoryListView = findViewById(R.id.inventory_list_view);
        priceEditText = findViewById(R.id.price_edit_text);
        sellButton = findViewById(R.id.sell_button);

        // Verificar si el usuario tiene un personaje seleccionado
        if (userAccount.getSelectedCharacter() != null) {
            inventoryAdapter = new ItemAdapter(this, userAccount.getSelectedCharacter().getInventory());
            inventoryListView.setAdapter(inventoryAdapter);

            inventoryListView.setOnItemClickListener((parent, view, position, id) -> {
                selectedInventoryItem = inventoryAdapter.getItem(position);
                updateSellButton();
            });

            sellButton.setOnClickListener(v -> {
                if (selectedInventoryItem != null) {
                    try {
                        int price = Integer.parseInt(priceEditText.getText().toString());

                        if (price <= 0) {
                            Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean success = market.addListing(userAccount, selectedInventoryItem, price);

                        if (success) {
                            Toast.makeText(this, "Ítem puesto a la venta", Toast.LENGTH_SHORT).show();
                            updateUI();
                        } else {
                            Toast.makeText(this, "No se pudo poner el ítem a la venta", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Si no hay personaje seleccionado, mostrar un mensaje
            Toast.makeText(this, "Primero debes comprar un personaje", Toast.LENGTH_SHORT).show();
            inventoryAdapter = new ItemAdapter(this, new ArrayList<>());
            inventoryListView.setAdapter(inventoryAdapter);
            sellButton.setEnabled(false);
        }
    }

    private void initMyListingsTab() {
        myListingsView = findViewById(R.id.my_listings_view);
        cancelListingButton = findViewById(R.id.cancel_listing_button);

        List<MarketListing> myListings = market.getListingsBySeller(userAccount);
        myListingsAdapter = new MarketListingAdapter(this, myListings);
        myListingsView.setAdapter(myListingsAdapter);

        myListingsView.setOnItemClickListener((parent, view, position, id) -> {
            selectedListing = myListingsAdapter.getItem(position);
            cancelListingButton.setEnabled(true);
        });

        cancelListingButton.setOnClickListener(v -> {
            if (selectedListing != null) {
                boolean success = market.cancelListing(userAccount, selectedListing);

                if (success) {
                    Toast.makeText(this, "Venta cancelada", Toast.LENGTH_SHORT).show();
                    updateUI();
                } else {
                    Toast.makeText(this, "No se pudo cancelar la venta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        market.cleanExpiredListings();
        updateUI();
    }

    private void updateUI() {
        coinsText.setText("Monedas: " + userAccount.getCoins());

        // Actualizar listas
        if (userAccount.getSelectedCharacter() != null) {
            inventoryAdapter.updateItems(userAccount.getSelectedCharacter().getInventory());
        }

        List<MarketListing> myListings = market.getListingsBySeller(userAccount);
        myListingsAdapter.updateListings(myListings);

        // Restablecer selecciones
        selectedInventoryItem = null;
        selectedListing = null;
        selectedCharacterTemplate = null;
        buyButton.setEnabled(false);
        cancelListingButton.setEnabled(false);
        buyCharacterButton.setEnabled(false);
        updateSellButton();

        // Guardar cambios
        UserAccountManager.updateAccount();
        MarketManager.updateMarket();
    }

    private void updateSellButton() {
        boolean canSell = selectedInventoryItem != null &&
                !priceEditText.getText().toString().isEmpty() &&
                userAccount.getSelectedCharacter() != null;
        sellButton.setEnabled(canSell);
    }

    // Clase interna para definir una plantilla de personaje
    private class CharacterTemplate {
        private String name;
        private CharacterClass characterClass;
        private CharacterRole role;
        private int price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CharacterClass getCharacterClass() {
            return characterClass;
        }

        public void setCharacterClass(CharacterClass characterClass) {
            this.characterClass = characterClass;
        }

        public CharacterRole getRole() {
            return role;
        }

        public void setRole(CharacterRole role) {
            this.role = role;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }

    // Adaptador para mostrar las plantillas de personajes
    private class CharacterTemplateAdapter extends ArrayAdapter<CharacterTemplate> {
        private Context context;
        private List<CharacterTemplate> characters;

        public CharacterTemplateAdapter(Context context, List<CharacterTemplate> characters) {
            super(context, R.layout.character_template_item, characters);
            this.context = context;
            this.characters = characters;
        }

        // Dentro de la clase CharacterTemplateAdapter, actualizamos el método getView:
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.character_template_item, parent, false);
            }

            CharacterTemplate template = characters.get(position);

            ImageView avatarView = convertView.findViewById(R.id.character_avatar);
            TextView nameText = convertView.findViewById(R.id.character_name);
            TextView classText = convertView.findViewById(R.id.character_class);
            TextView priceText = convertView.findViewById(R.id.character_price);

            // Configurar avatar según clase
            switch (template.getCharacterClass()) {
                case WARRIOR:
                    avatarView.setImageResource(R.drawable.avatar_warrior);
                    break;
                case MAGE:
                    avatarView.setImageResource(R.drawable.avatar_mage);
                    break;
                case ROGUE:
                    avatarView.setImageResource(R.drawable.avatar_rogue);
                    break;
                case CLERIC:
                    avatarView.setImageResource(R.drawable.avatar_cleric);
                    break;
                default:
                    avatarView.setImageResource(R.drawable.avatar_default);
                    break;
            }

            nameText.setText(template.getName());
            classText.setText(template.getCharacterClass().toString() + " - " + template.getRole().toString());

            // Verificar si este personaje ya está comprado
            boolean alreadyOwned = isCharacterOwned(template);
            if (alreadyOwned) {
                priceText.setText("Ya adquirido");
                priceText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                convertView.setAlpha(0.7f); // Atenuar el elemento
            } else {
                priceText.setText(template.getPrice() + " monedas");
                priceText.setTextColor(context.getResources().getColor(android.R.color.black));
                convertView.setAlpha(1.0f); // Normal
            }

            return convertView;
        }

        // Método para verificar si un personaje ya está comprado
        private boolean isCharacterOwned(CharacterTemplate template) {
            for (Character character : userAccount.getCharacters()) {
                if (character.getName().equals(template.getName()) &&
                        character.getCharacterClass() == template.getCharacterClass() &&
                        character.getRole() == template.getRole()) {
                    return true;
                }
            }
            return false;
        }

    }

    // Método para crear la lista de personajes disponibles
    private List<CharacterTemplate> createAvailableCharacters() {
        List<CharacterTemplate> templates = new ArrayList<>();

        // Guerrero tanque
        CharacterTemplate warriorTank = new CharacterTemplate();
        warriorTank.setName("Guerrero Tanque");
        warriorTank.setCharacterClass(CharacterClass.WARRIOR);
        warriorTank.setRole(CharacterRole.TANK);
        warriorTank.setPrice(500);
        templates.add(warriorTank);

        // Guerrero DPS
        CharacterTemplate warriorDPS = new CharacterTemplate();
        warriorDPS.setName("Guerrero DPS");
        warriorDPS.setCharacterClass(CharacterClass.WARRIOR);
        warriorDPS.setRole(CharacterRole.DPS);
        warriorDPS.setPrice(500);
        templates.add(warriorDPS);

        // Mago DPS
        CharacterTemplate mageDPS = new CharacterTemplate();
        mageDPS.setName("Mago");
        mageDPS.setCharacterClass(CharacterClass.MAGE);
        mageDPS.setRole(CharacterRole.DPS);
        mageDPS.setPrice(500);
        templates.add(mageDPS);

        // Pícaro DPS
        CharacterTemplate rogueDPS = new CharacterTemplate();
        rogueDPS.setName("Pícaro");
        rogueDPS.setCharacterClass(CharacterClass.ROGUE);
        rogueDPS.setRole(CharacterRole.DPS);
        rogueDPS.setPrice(500);
        templates.add(rogueDPS);

        // Clérigo sanador
        CharacterTemplate clericHealer = new CharacterTemplate();
        clericHealer.setName("Clérigo Sanador");
        clericHealer.setCharacterClass(CharacterClass.CLERIC);
        clericHealer.setRole(CharacterRole.HEALER);
        clericHealer.setPrice(500);
        templates.add(clericHealer);

        return templates;
    }
}