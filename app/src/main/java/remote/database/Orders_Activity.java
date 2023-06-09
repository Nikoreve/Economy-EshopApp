package remote.database;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eshopapplication.MainActivity;
import com.example.eshopapplication.Product_Inventory_Activity;
import com.example.eshopapplication.R;
import com.example.eshopapplication.SettingsActivity;
import com.example.eshopapplication.Supplier_Info_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import database.Product;

public class Orders_Activity extends AppCompatActivity {
    private final static String TAG = "remote.database (Orders Activity)";
    protected static FirebaseFirestore db;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView username_text, email_text, usernameTxt, emailTxt;
    //    TextInputEditText orderID, productID, customerName, orderDate, quantity; // msrp = recommended supplier price
    TextInputEditText orderID, productID, customerName, quantity; // msrp = recommended supplier price
    Button insertButton, deleteButton, updateButton;

    TextView displayOrderError, questionText;
    EditText orderDate;
    TextView toolbarTitle;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db = FirebaseFirestore.getInstance();

        toolbar = makeToolbar();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.dr_orders);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dr_database:
                        menuItem.setChecked(false);
                        startActivity(new Intent(Orders_Activity.this, MainActivity.class));
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.dr_orders:
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.dr_order_info:
                        menuItem.setChecked(true);
                        startActivity(new Intent(Orders_Activity.this, Order_Info_Activity.class));
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.dr_product_inventory:
                        menuItem.setChecked(true);
                        startActivity(new Intent(Orders_Activity.this, Product_Inventory_Activity.class));
                        return true;

                    case R.id.dr_supplier_info:
                        menuItem.setChecked(true);
                        startActivity(new Intent(Orders_Activity.this, Supplier_Info_Activity.class));
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.settings:
                        menuItem.setChecked(true);
                        startActivity(new Intent(Orders_Activity.this, SettingsActivity.class));
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });

        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseuser != null) {
            String userName = firebaseuser.getDisplayName();
            String userEmail = firebaseuser.getEmail();
            View menu_drawer_head = navigationView.getHeaderView(0);
            username_text = menu_drawer_head.findViewById(R.id.drawer_menu_header_username_text);
            email_text = menu_drawer_head.findViewById(R.id.drawer_menu_header_email_text);
            username_text.setText(userName);
            email_text.setText(userEmail);
        }

        displayOrderError = findViewById(R.id.orders_error_textview);
        questionText = findViewById(R.id.question_text);

        orderID = findViewById(R.id.orders_orderid_tiet);
        productID = findViewById(R.id.orders_productid_tiet);
        customerName = findViewById(R.id.orders_customernameid_tiet);
//        orderDate = findViewById(R.id.orders_orderdate_tiet);
        orderDate = findViewById(R.id.orders_orderdate_tiet);
        quantity = findViewById(R.id.orders_quantity_tiet);

        insertButton = findViewById(R.id.insert_button);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);

        orderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        Orders_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String yearString = Integer.toString(year); // WE ARE DOING THIS AND THE NEXT LINE
                        year = Integer.parseInt(yearString.substring(1)); // TO REMOVE THE FIRST 2 DIGITS FROM LIKE 2023 ->> 23
//                                dateEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        if (dayOfMonth < 10 && monthOfYear < 10)
                            orderDate.setText("0" + dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                        else if (dayOfMonth < 10)
                            orderDate.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        else if (monthOfYear < 10)
                            orderDate.setText("" + dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                        else
                            orderDate.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayOrderError.setText("");
                if (orderID.getText().length() == 0) {
                    orderID.setError("You must fill this field");
                }
                if (productID.getText().length() == 0) {
                    productID.setError("You must fill this field");
                }
                if (customerName.getText().length() == 0) {
                    customerName.setError("You must fill this field");
                }
                if (orderDate.getText().length() == 0) {
                    orderDate.setError("You must fill this field");
                }
                if (quantity.getText().length() == 0) {
                    quantity.setError("You must fill this field");
                }
                int var_orderID = 0;
                try {
                    var_orderID = Integer.parseInt(orderID.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }
                int var_productID = 0;
                try {
                    var_productID = Integer.parseInt(productID.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }
                String var_customerName = customerName.getText().toString();
                String var_orderDate = orderDate.getText().toString();
                int var_quantity = 0;
                try {
                    var_quantity = Integer.parseInt(quantity.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }

                boolean flagProductStock = false;
                final boolean[] flagOrderID = {false};
                int currentQuantityProduct = 0;
                boolean flagProductID = false;
                try {
                    List<Product> aproduct = MainActivity.myAppDatabase.myDao().getProduct();
                    for (Product i : aproduct) {
                        int var_productID_for_check = i.getPid();
                        if (var_productID_for_check == var_productID) {
                            flagProductID = true;  // THA GINEI true APO TH STIGMH POY TO var_productID YPARXEI STHN VASH DHLADH STON PINAKA product
                            currentQuantityProduct = i.getStock();
                            int finalStock = currentQuantityProduct - var_quantity;
                            if (finalStock < 0)
                                flagProductStock = true; // THA GINEI true APO TH STIGMH POY TO stock TOY PRODUCT EINAI MIKROTERO APO TO quantity POY THELOYME NA PARAGEILOYME
                            Log.i(TAG, "TO flagProductID egine true");
                            break;
                        }
                    }

                    if (!flagProductID || flagProductStock) throw new Exception("Exception thrown");

                    documentReference = db.collection("Orders").document(orderID.getText().toString());

                    int finalVar_orderID = var_orderID;
                    int finalVar_productID = var_productID;
                    int finalVar_quantity = var_quantity;
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    orderID.setError("The certain order ID has been recorded.\nPlease give another order ID");
                                    Log.d("TAG", "TO flagOrderID yparhei idi ");
                                } else {
                                    Orders order = new Orders();
                                    order.setOrderID(finalVar_orderID);
                                    order.setProductID(finalVar_productID);
                                    order.setCustomerName(var_customerName);
                                    order.setOrderDate(var_orderDate);
                                    order.setQuantity(finalVar_quantity);

                                    db.collection("Orders").document("" + finalVar_orderID).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Order added: ", Toast.LENGTH_LONG).show();

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
                                                NotificationManager manager = getSystemService(NotificationManager.class);
                                                manager.createNotificationChannel(channel);
                                            }
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Orders_Activity.this, "myCh").setSmallIcon(R.drawable.warning).setContentTitle("Notification").setContentText("Order successfully added!");

                                            notification = builder.build();
                                            notificationManagerCompat = NotificationManagerCompat.from(Orders_Activity.this);

                                            if (ActivityCompat.checkSelfPermission(Orders_Activity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            notificationManagerCompat.notify(1, notification);


                                            String currentPrName = "Unknown", currentPrCategory = "Unknown";
                                            double currentPrPrice = 0.0;
                                            int currentPrID = 0, currentPrStock = 0;
                                            byte[] currentPrImage = null;
                                            Bitmap currentPrBitmap = null;
                                            List<Product> bproduct = MainActivity.myAppDatabase.myDao().getProduct();
                                            for (Product i : bproduct) {
                                                int var_productID_for_check = i.getPid();
                                                if (var_productID_for_check == finalVar_productID) {
                                                    currentPrID = i.getPid();
                                                    currentPrName = i.getName();
                                                    currentPrCategory = i.getCategory();
                                                    currentPrPrice = i.getPrice();
                                                    currentPrStock = i.getStock() - finalVar_quantity;
                                                    currentPrImage = i.getImage();
                                                    currentPrBitmap = BitmapFactory.decodeByteArray(currentPrImage, 0, currentPrImage.length);

                                                    Product configproduct = new Product();
                                                    configproduct.setPid(currentPrID);
                                                    configproduct.setName(currentPrName);
                                                    configproduct.setCategory(currentPrCategory);
                                                    configproduct.setPrice(currentPrPrice);
                                                    configproduct.setStock(currentPrStock);
                                                    configproduct.setImage(currentPrImage);

                                                    MainActivity.myAppDatabase.myDao().updateProduct(configproduct);
                                                    Log.i(TAG, "TO flagProductID egine true");
                                                    break;
                                                }
                                            }
                                            orderID.setText("");
                                            productID.setText("");
                                            customerName.setText("");
                                            orderDate.setText("");
                                            quantity.setText("");
                                            setErrorMessagesToNull();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // EDW THA MPAINEI ENA NOTIFICATION APOTYXIAS
//                                    Toast.makeText(getApplicationContext(), "Order error: " + finalVar_orderID, Toast.LENGTH_LONG).show();
                                            Toast.makeText(getApplicationContext(), "Order error: ", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    });

                    if (orderID.getText().length() == 0 || flagOrderID[0] || productID.getText().length() == 0 || var_customerName.isEmpty() || var_orderDate.isEmpty() || !var_orderDate.matches("\\d{2}-\\d{2}-\\d{2}") || quantity.getText().toString().equals("0") || quantity.getText().toString().isEmpty() || (var_quantity > currentQuantityProduct) || !flagProductID)
                        throw new Exception("Exception thrown");

                } catch (Exception e) {
                    String message = e.getMessage();
                    Log.i(TAG, e.getMessage());
                    //EDW THA VALOYME NA ERHETAI NOTIFICATION OTI EINAI APOTYXHS H PROSTHIKI TOY NEOY PROIONTOS
                    Toast.makeText(getApplicationContext(), "The productID or the supplier or the supplyDate you submitted is not registered\n or some other error.", Toast.LENGTH_LONG).show();

                    if (productID.getText().toString().isEmpty())
                        productID.setError("You must fill this field");
                    else if (!flagProductID)
                        productID.setError("The productID you filled is not registered");
                    if (var_orderDate.isEmpty()) orderDate.setError("You must fill this field");
                    else if (!var_orderDate.matches("\\d{2}-\\d{2}-\\d{2}"))
                        orderDate.setError("Required format is dd-mm-yy");
                    if (quantity.getText().toString().isEmpty())
                        quantity.setError("You must fill this field");
                    else if (quantity.getText().toString().equals("0"))
                        quantity.setError("The value of stock must be at least 1");
                    else if (currentQuantityProduct == 0)
                        quantity.setError("There are no stock of the selected product\n");
                    else if (flagProductStock) {
                        quantity.setError("The apotheke has only " + currentQuantityProduct + " stock of the selected product" + "\nPlease make an order with maximum  " + currentQuantityProduct + " stock");
                    }

                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayOrderError.setText("");
                productID.setError(null);

                int var_orderID = 0;
                try {
                    var_orderID = Integer.parseInt(orderID.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }
                int var_productID = 0;
                try {
                    var_productID = Integer.parseInt(productID.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }
                String var_customerName = customerName.getText().toString();
                String var_orderDate = orderDate.getText().toString();
                int var_quantity = 0;
                try {
                    var_quantity = Integer.parseInt(quantity.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }

                documentReference = db.collection("Orders").document(orderID.getText().toString());

                int finalVar_orderID = var_orderID;
                int finalVar_productID = var_productID;
                int finalVar_quantity = var_quantity;
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (!documentSnapshot.exists()) {
                                orderID.setError("The certain order ID doesn't exist.\nPlease give an existing order ID to update");
                            } else {
                                boolean flagProductStock = false;  // AYTH H METAVLHTH GINETAI TRUE OTAN TO stock TOY product PROKYPTEI NA EINAI ARNITIKO ME THN DIAMORFOSH TOY UPDATE
                                try {
                                    Orders beforeUpdateOrder = documentSnapshot.toObject(Orders.class);
                                    Integer currentOrderIdOfOrder = beforeUpdateOrder.getOrderID();
                                    Integer currentProductIdOfOrder = beforeUpdateOrder.getProductID();
                                    Log.d(TAG + "currentProductIdOfOrder", "" + currentProductIdOfOrder);
                                    String currentOrderDateOfOrder = beforeUpdateOrder.getOrderDate();
                                    String currentCustomerNameOfOrder = beforeUpdateOrder.getCustomerName();
                                    // to oldQuantity tha mporoyse na legetai currentOldQuantityOrder
                                    Integer oldQuantity = beforeUpdateOrder.getQuantity();  // PAIRNOYME TO QUANTITY APO TO ORDER POY PROYPIRXE (PRIN TO UPDATE TOY)
                                    List<Product> bproduct = MainActivity.myAppDatabase.myDao().getProduct();
                                    for (Product i : bproduct) {
                                        int var_productID_for_check = i.getPid();
                                        if (var_productID_for_check == finalVar_productID) {
                                            if (i.getStock() + oldQuantity - finalVar_quantity < 0) {
                                                flagProductStock = true;  // TRUE OTAN H TIMH TOY STOCK TOY product GINETAI ARNHTIKH PRAGMA POY DN EINAI EPITHYMITO KAI TRWEI EXCEPTION META
                                                displayOrderError.setText("Cannot update the order due to lack of stock\nCurrent stock of selected product:" + i.getStock() + "\nOldQuantity order: " + oldQuantity + "\nNewQuantity order:" + finalVar_quantity);
                                            }
                                            break;
                                        }
                                    }

                                    if ((currentProductIdOfOrder != finalVar_productID)) {
                                        productID.setError("The given productID doesn't correspond appropriately to the given orderID");
                                        throw new Exception("Exception Thrown");
                                    } else if (flagProductStock) {
                                        throw new Exception("Exception Thrown");
                                    }

                                    Orders order = new Orders();
                                    order.setOrderID(finalVar_orderID);
//                                    if(productID.getText().length() == 0)
                                    order.setProductID(currentProductIdOfOrder);
//                                        order.setProductID(finalVar_productID);
                                    if (customerName.getText().length() == 0)
                                        order.setCustomerName(currentCustomerNameOfOrder);
                                    else order.setCustomerName(var_customerName);
                                    if (orderDate.getText().length() == 0)
                                        order.setOrderDate(currentOrderDateOfOrder);
                                    else order.setOrderDate(var_orderDate);
                                    if (quantity.getText().length() == 0)
                                        order.setQuantity(oldQuantity);
                                    else order.setQuantity(finalVar_quantity);

                                    db.collection("Orders").document("" + finalVar_orderID).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // EDW THA MPAINEI ENA NOTIFICATION EPITYXIAS
                                            Toast.makeText(getApplicationContext(), "Order " + finalVar_orderID + " updated: ", Toast.LENGTH_LONG).show();

                                            String currentPrName = "Unknown", currentPrCategory = "Unknown";
                                            double currentPrPrice = 0.0;
                                            int currentPrID = 0, currentPrStock = 0;
                                            byte[] currentPrImage = null;
                                            Bitmap currentPrBitmap = null;
                                            List<Product> bproduct = MainActivity.myAppDatabase.myDao().getProduct();
                                            for (Product i : bproduct) {
                                                int var_productID_for_check = i.getPid();
                                                if (var_productID_for_check == finalVar_productID) {
                                                    currentPrID = i.getPid();
//                                                    currentPrID = currentProductIdOfOrder;
                                                    currentPrName = i.getName();
                                                    currentPrCategory = i.getCategory();
                                                    currentPrPrice = i.getPrice();
                                                    currentPrStock = i.getStock() + oldQuantity - finalVar_quantity;
                                                    currentPrImage = i.getImage();
                                                    currentPrBitmap = BitmapFactory.decodeByteArray(currentPrImage, 0, currentPrImage.length);

                                                    Product configproduct = new Product();
                                                    configproduct.setPid(currentPrID);
                                                    configproduct.setName(currentPrName);
                                                    configproduct.setCategory(currentPrCategory);
                                                    configproduct.setPrice(currentPrPrice);
                                                    configproduct.setStock(currentPrStock);
                                                    configproduct.setImage(currentPrImage);

                                                    MainActivity.myAppDatabase.myDao().updateProduct(configproduct);
                                                    Log.i(TAG, "TO flagProductID egine true");
                                                    break;
                                                }
                                            }
                                            orderID.setText("");
                                            productID.setText("");
                                            customerName.setText("");
                                            orderDate.setText("");
                                            quantity.setText("");
                                            setErrorMessagesToNull();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // EDW THA MPAINEI ENA NOTIFICATION APOTYXIAS
                                            Toast.makeText(getApplicationContext(), "Order update error: ", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    String message = e.getMessage();
                                    //ANTISTOIXA MPROYME NA VALOYME TO NOTIFICATION GIA UNSUCCESFUL YLOPOIHSH
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show(); // AYTO THA FYGEI
                                    boolean flagProductID = false;
                                    List<Product> aproduct = MainActivity.myAppDatabase.myDao().getProduct();
                                    for (Product i : aproduct) {
                                        int var_productID_for_check = i.getPid();
                                        if (var_productID_for_check == finalVar_productID) {
                                            flagProductID = true;  // THA GINEI true APO TH STIGMH POY TO var_productID YPARXEI STHN VASH DHLADH STON PINAKA product
                                            Log.i(TAG, "TO flagProductID egine true");
                                            break;
                                        }
                                    }
                                    if (orderID.length() == 0)
                                        orderID.setError("You must fill this field");
                                    if (!flagProductID)
                                        productID.setError("The given productID is not registered");
                                }
                            }
                        }
                    }
                });
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOrderError.setText("");
                productID.setError(null);

                int var_orderID = 0;
                try {
                    var_orderID = Integer.parseInt(orderID.getText().toString());
                } catch (NumberFormatException exception) {
                    System.out.println("Could not parse" + exception);
                }

                try {
                    if (orderID.getText().length() == 0) throw new Exception("Exception thrown");

                    documentReference = db.collection("Orders").document(orderID.getText().toString());
                    int finalVar_orderID = var_orderID;
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    Orders beforeDeleteOrder = documentSnapshot.toObject(Orders.class);
                                    Integer currentOrderIdOfOrder = beforeDeleteOrder.getOrderID();
                                    Integer currentProductIdOfOrder = beforeDeleteOrder.getProductID();
                                    String currentOrderDateOfOrder = beforeDeleteOrder.getOrderDate();
                                    String currentCustomerNameOfOrder = beforeDeleteOrder.getCustomerName();
                                    Integer currentQuantityOfOrder = beforeDeleteOrder.getQuantity();

                                    Orders order = new Orders();
                                    order.setOrderID(finalVar_orderID);

                                    new AlertDialog.Builder(Orders_Activity.this).setTitle("Delete Order").setMessage("Are you sure, you want to delete the order with orderID: " + finalVar_orderID + "?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.collection("Orders").document("" + finalVar_orderID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // EDW THA MPAINEI ENA NOTIFICATION EPITYXIAS

                                                    Toast.makeText(getApplicationContext(), "Order deleted: ", Toast.LENGTH_LONG).show();
                                                    orderID.setText("");
                                                    productID.setText("");
                                                    customerName.setText("");
                                                    orderDate.setText("");
                                                    quantity.setText("");
                                                    setErrorMessagesToNull();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // EDW THA MPAINEI ENA NOTIFICATION APOTYXIAS
                                                    Toast.makeText(getApplicationContext(), "Order delete error: ", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }).setNegativeButton("Cancel", null).setIcon(R.drawable.warning).show();
                                } else {
                                    displayOrderError.setText("The order you want to delete doesn't exist");
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    String message = e.getMessage();
                    //ANTISTOIXA MPROYME NA VALOYME TO NOTIFICATION GIA UNSUCCESFUL YLOPOIHSH
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show(); // AYTO THA FYGEI
                    if (orderID.getText().length() == 0)
                        orderID.setError("You must fill this field");
                }

            }
        });


//        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer((GravityCompat.START));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Toolbar makeToolbar() {
        toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_button);
        actionBar.setTitle("");
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_title_to_orders_activity);
        return toolbar;
    }

    public void setErrorMessagesToNull() {
        if (orderID.getText().length() == 0) orderID.setError(null);
        if (productID.getText().length() == 0) productID.setError(null);
        if (customerName.getText().length() == 0) customerName.setError(null);
        if (orderDate.getText().length() == 0) orderDate.setError(null);
        if (quantity.getText().length() == 0) quantity.setError(null);
    }
}