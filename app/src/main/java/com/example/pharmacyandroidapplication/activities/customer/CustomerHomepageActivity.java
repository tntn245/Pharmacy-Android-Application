package com.example.pharmacyandroidapplication.activities.customer;

import static com.example.pharmacyandroidapplication.utils.AndroidUtil.showToast;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.pharmacyandroidapplication.R;
import com.example.pharmacyandroidapplication.activities.ChatActivity;
import com.example.pharmacyandroidapplication.activities.LoginActivity;
import com.example.pharmacyandroidapplication.adapters.HomeCategoryAdapter;
import com.example.pharmacyandroidapplication.adapters.HomeProductAdapter;
import com.example.pharmacyandroidapplication.databinding.ActivityMainBinding;
import com.example.pharmacyandroidapplication.models.Category;
import com.example.pharmacyandroidapplication.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomerHomepageActivity extends AppCompatActivity {
    ImageButton imageSearch;
    EditText editTextSearch;
    GridView categoryGV;
    GridView productGV;
    ArrayList<Category> CategoryArrayList;
    ArrayList<Product> ProductArrayList;
    String userID;
    String categoryID = "";

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_homepage);

        userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getToken();

        ScrollView scrollView = findViewById(R.id.scroll_view);
        CardView searchBar = findViewById(R.id.search_bar);
        ImageView searchIcon = findViewById(R.id.ic_search);
        categoryGV = findViewById(R.id.rcv_category);
        productGV = findViewById(R.id.rcv_shopping);
        imageSearch = findViewById(R.id.image_search);

        openDrawer();
        closeDrawer();
        loadProductFromFirebase();
        loadCategoryFromFirebase();


        editTextSearch = findViewById(R.id.edit_text_search);
        editTextSearch.setText("");

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
//                    searchBar.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.VISIBLE);

                    // Lấy margin của thanh search
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchBar.getLayoutParams();
                    int startMarginTop = params.topMargin;
                    int startMarginBottom = params.bottomMargin;
                    int startHeight = searchBar.getHeight();

                    // Tạo một ValueAnimator để thay đổi chiều cao của thanh search
                    ValueAnimator animator = ValueAnimator.ofInt(startHeight, 0); // startHeight là chiều cao ban đầu, endHeight là chiều cao cuối cùng sau khi thu nhỏ
                    animator.setDuration(500); // Thiết lập thời gian thực hiện animation

                    // Listener để cập nhật chiều cao của thanh search và layout cha
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            // Lấy giá trị chiều cao hiện tại từ ValueAnimator
                            int animatedValue = (int) animation.getAnimatedValue();

                            // Cập nhật chiều cao của thanh search
                            searchBar.getLayoutParams().height = animatedValue;

                            // Cập nhật margin của thanh search
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchBar.getLayoutParams();
                            params.topMargin = (int) (startMarginTop * ((float) animatedValue / startHeight));
                            params.bottomMargin = (int) (startMarginBottom * ((float) animatedValue / startHeight));
                            searchBar.setLayoutParams(params);

                            searchBar.requestLayout();
                        }
                    });
                    animator.start();
                } else if (scrollY < oldScrollY) {
//                    searchBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
//                    searchBar.setVisibility(View.VISIBLE);
//                    searchIcon.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Click Product
        productGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy giá trị của item được click
                Product productDetails = ProductArrayList.get(position);
                // Truyền giá trị của item qua layout tiếp theo để hiển thị
                Intent intent = new Intent(CustomerHomepageActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_id", productDetails.getId());
                startActivity(intent);
            }
        });

        // Click Bottom nav
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    return true;
                } else if (itemId == R.id.support) {
                    // Xử lý khi người dùng chọn trang tư vấn
                    Intent intent = new Intent(CustomerHomepageActivity.this, ChatActivity.class);
                    intent.putExtra("userID", "zDVjeEon70POnmT25BdJbEmB5jG3");
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.cart) {
                    // Xử lý khi người dùng chọn trang giỏ hàng
                    Intent CartIntent = new Intent(CustomerHomepageActivity.this, CartActivity.class);
                    startActivity(CartIntent);
                    return true;
                } else if (itemId == R.id.profile) {
                    // Xử lý khi người dùng chọn trang tài khoản
                    Intent intent = new Intent(CustomerHomepageActivity.this, UserActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    private void performSearch() {
        String searchQuery = editTextSearch.getText().toString().trim();

        Intent intent = new Intent(CustomerHomepageActivity.this, ShoppingPageActivity.class);
        intent.putExtra("categoryID", "");
        intent.putExtra("categoryName", "");
        intent.putExtra("searchQuery", searchQuery);
        startActivity(intent);
    }
    private void loadCategoryFromFirebase() {
        DatabaseReference categoryRef = database.getReference("category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryArrayList = new ArrayList<Category>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    boolean flagValid = Boolean.TRUE.equals(snapshot.child("flag_valid").getValue(Boolean.class));
                    if(flagValid){
                        Category category = new Category(id,name, flagValid);
                        CategoryArrayList.add(category);
                    }
                }
                // Click Category
                HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(CustomerHomepageActivity.this, CategoryArrayList);
                categoryGV.setAdapter(categoryAdapter);
                categoryAdapter.setOnButtonClickListener(new HomeCategoryAdapter.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(int position, String text) {
                        navCategory(text);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void navCategory(String categoryName){
        DatabaseReference categoryRef = database.getReference("category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);
                    if(name.equals(categoryName)){
                        categoryID = id;
                        break;
                    }
                }

                Intent intent = new Intent(CustomerHomepageActivity.this, ShoppingPageActivity.class);
                intent.putExtra("categoryID", categoryID);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("searchQuery", "");
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadProductFromFirebase() {
        DatabaseReference productsRef = database.getReference("product");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProductArrayList = new ArrayList<Product>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu từ snapshot và tạo đối tượng Product
//                    String productName = snapshot.child("name").getValue(String.class);
//                    int productPrice = snapshot.child("price").getValue(Integer.class);
//                    String productImg = snapshot.child("img").getValue(String.class);
//                    String id = snapshot.child("id").getValue(String.class);
//                    String id_category = snapshot.child("id_category").getValue(String.class);
//                    String unit = snapshot.child("unit").getValue(String.class);
//                    String uses = snapshot.child("uses").getValue(String.class);
//                    String ingredient = snapshot.child("ingredient").getValue(String.class);
//                    Boolean prescription = Boolean.TRUE.equals(snapshot.child("prescription").getValue(Boolean.class));
                    String productName = snapshot.hasChild("name") ? snapshot.child("name").getValue(String.class) : "Unknown";
                    int productPrice = snapshot.hasChild("price") ? snapshot.child("price").getValue(Integer.class) : 0;
                    String productImg = snapshot.hasChild("img") ? snapshot.child("img").getValue(String.class) : "";
                    String id = snapshot.hasChild("id") ? snapshot.child("id").getValue(String.class) : "";
                    String id_category = snapshot.hasChild("id_category") ? snapshot.child("id_category").getValue(String.class) : "";
                    String unit = snapshot.hasChild("unit") ? snapshot.child("unit").getValue(String.class) : "";
                    String uses = snapshot.hasChild("uses") ? snapshot.child("uses").getValue(String.class) : "";
                    String ingredient = snapshot.hasChild("ingredient") ? snapshot.child("ingredient").getValue(String.class) : "";
                    Boolean prescription = snapshot.hasChild("prescription") && Boolean.TRUE.equals(snapshot.child("prescription").getValue(Boolean.class));

                    Product product = new Product(id,id_category,productImg, productName,0,productPrice,unit,uses, ingredient, prescription);
                    // Sau đó, thêm sản phẩm vào danh sách productList
                    ProductArrayList.add(product);
                }

                // Sau khi lấy được danh sách sản phẩm, tạo adapter và gán cho GridView
                HomeProductAdapter productAdapter = new HomeProductAdapter(CustomerHomepageActivity.this, ProductArrayList);
                productGV.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openDrawer() {
        ImageView openDrawerButton = findViewById(R.id.icon_drawer_menu);
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START); // Mở Drawer từ trái sang
            }
        });

    }

    private void closeDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Xử lý các sự kiện khi người dùng chọn các mục trong Drawer Navigation
                int id = item.getItemId();

                if (id == R.id.nav_home_drawer) {
                    Intent intent = new Intent(CustomerHomepageActivity.this, CustomerHomepageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.nav_support_drawer) {
                    Intent intent = new Intent(CustomerHomepageActivity.this, ChatActivity.class);
                    intent.putExtra("userID", "zDVjeEon70POnmT25BdJbEmB5jG3");
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_shopping_drawer) {
                    Intent intent = new Intent(CustomerHomepageActivity.this, ShoppingPageActivity.class);
                    intent.putExtra("categoryID", "");
                    intent.putExtra("categoryName", "");
                    intent.putExtra("searchQuery", "");
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_cart_drawer) {
                    Intent intent = new Intent(CustomerHomepageActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_info_drawer) {
                    // Xử lý khi người dùng chọn trang tài khoản
                    Intent intent = new Intent(CustomerHomepageActivity.this, UserActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_logout_drawer) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(CustomerHomepageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                // Sau khi xử lý xong, đóng Drawer Navigation
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START); // Đóng Drawer từ trái sang
                return true;
            }
        });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference("accounts").child(userID);

        // Tạo một Map để chứa dữ liệu cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("fcmToken", token);

        // Cập nhật token trong Realtime Database
        userReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> showToast(CustomerHomepageActivity.this, "Token updated successfully"))
                .addOnFailureListener(e -> showToast(CustomerHomepageActivity.this, "Unable to update token"));
    }

}