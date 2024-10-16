package com.example.bresmithsnhu360;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  private SQLiteDatabase userDatabase;
  private SQLiteDatabase inventoryDatabase;
  private GridView dataGridView;
  private SimpleCursorAdapter adapter;
  private EditText usernameEditText, passwordEditText;
  private Button loginButton, createAccountButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize UI elements for user login
    usernameEditText = findViewById(R.id.usernameEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    loginButton = findViewById(R.id.loginButton);
    createAccountButton = findViewById(R.id.createAccountButton);

    // Open or create the user database
    userDatabase = openOrCreateDatabase("UserDB", MODE_PRIVATE, null);
    // Create a table if not exists
    userDatabase.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");

    // Add text change listener to enable/disable login button based on input fields
    usernameEditText.addTextChangedListener(textWatcher);
    passwordEditText.addTextChangedListener(textWatcher);

    // Set up click listeners for login and create account buttons
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        login();
      }
    });

    createAccountButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createAccount();
      }
    });

    // Initialize UI elements for inventory
    dataGridView = findViewById(R.id.dataGridView);
    Button addButton = findViewById(R.id.addButton);

    // Open or create the inventory database
    inventoryDatabase = openOrCreateDatabase("InventoryDB", MODE_PRIVATE, null);
    // Create a table if not exists
    inventoryDatabase.execSQL("CREATE TABLE IF NOT EXISTS inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT)");

    // Populate GridView with inventory items
    refreshGridView();

    // Set up click listener for the Add Item button
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addItem();
      }
    });
  }

  // TextWatcher to enable/disable login button based on input fields
  private TextWatcher textWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
      loginButton.setEnabled(!usernameEditText.getText().toString().isEmpty() &&
              !passwordEditText.getText().toString().isEmpty());
    }
  };

  // Method to handle user login
  private void login() {
    String username = usernameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();

    Cursor cursor = userDatabase.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
    if (cursor.getCount() > 0) {
      // User authenticated, perform login action
      Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
    } else {
      // Authentication failed
      Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
    }
    cursor.close();
  }

  // Method to handle account creation
  private void createAccount() {
    String username = usernameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();

    ContentValues values = new ContentValues();
    values.put("username", username);
    values.put("password", password);

    long rowId = userDatabase.insert("users", null, values);
    if (rowId != -1) {
      // Account created successfully
      Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
    } else {
      // Account creation failed
      Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show();
    }
  }

  // Method to add an item to the inventory database
  private void addItem() {
    ContentValues values = new ContentValues();
    values.put("item", "New Item");

    long rowId = inventoryDatabase.insert("inventory", null, values);
    if (rowId != -1) {
      // Item added successfully
      Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
      refreshGridView(); // Refresh GridView to display the new item
    } else {
      // Failed to add item
      Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
    }
  }

  // Method to refresh GridView with inventory items
  private void refreshGridView() {
    Cursor cursor = inventoryDatabase.rawQuery("SELECT * FROM inventory", null);
    if (cursor != null) {
      // Create an adapter to display inventory items in the GridView
      adapter = new SimpleCursorAdapter(
              this,
              android.R.layout.simple_list_item_1,
              cursor,
              new String[]{"item"},
              new int[]{android.R.id.text1},
              0);

      // Set the adapter for the GridView
      dataGridView.setAdapter(adapter);
    }
  }

  @Override
  protected void onDestroy() {
    // Close the databases when the activity is destroyed
    if (userDatabase != null && userDatabase.isOpen()) {
      userDatabase.close();
    }
    if (inventoryDatabase != null && inventoryDatabase.isOpen()) {
      inventoryDatabase.close();
    }
    super.onDestroy();
  }
}
