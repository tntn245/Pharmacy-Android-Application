package com.example.pharmacyandroidapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pharmacyandroidapplication.R;
import com.example.pharmacyandroidapplication.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Cart> mCart;
    private DatabaseReference cartRef; // Thêm biến DatabaseReference
    private String Uid;
    public CartAdapter(Context context, ArrayList<Cart> cart) {
        this.mContext = context;
        this.mCart = cart;
        // Khởi tạo DatabaseReference tới giỏ hàng trong Firebase
        this.Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.cartRef = FirebaseDatabase.getInstance().getReference("cart").child(this.Uid);
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View cartView = inflater.inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(cartView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Cart cart = mCart.get(position);
        holder.tv_name.setText(cart.getName_product());
        holder.tv_quanlity.setText(String.valueOf(cart.getQuanlity()));
        holder.tv_price.setText(String.valueOf(cart.getPrice_product()));
        holder.unit.setText(cart.getUnit());
        Glide.with(this.mContext)
                .load(cart.getImg())
                .into(holder.imgv);
        holder.check.setChecked(cart.isChecked());

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = mCart.get(position).getQuanlity();
                quantity++;
                mCart.get(position).setQuanlity(quantity);
                holder.tv_quanlity.setText(String.valueOf(quantity));
                // Cập nhật số lượng trong Firebase
                updateQuantityInFirebase(cart.getId_product(), quantity);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog(cart, position);
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = mCart.get(position).getQuanlity();
                if (quantity > 1) {
                    quantity--;
                    mCart.get(position).setQuanlity(quantity);
                    holder.tv_quanlity.setText(String.valueOf(quantity));
                    // Cập nhật số lượng trong Firebase
                    updateQuantityInFirebase(cart.getId_product(), quantity);
                } else if (quantity == 1) {
                    showConfirmDeleteDialog(cart, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCart.size();
    }

    public Cart getItem(int position) {
        return mCart.get(position);
    }

    public CartAdapter getAdapter() {
        return this;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgv;
        private TextView tv_name;
        private TextView tv_price;
        private TextView tv_quanlity;
        private CheckBox check;
        private Button increase;
        private Button decrease;
        private TextView unit;
        private ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgv = itemView.findViewById(R.id.img_item_cart);
            tv_name = itemView.findViewById(R.id.item_cart_name);
            tv_price = itemView.findViewById(R.id.item_cart_price);
            tv_quanlity = itemView.findViewById(R.id.item_cart_quanlity);
            check = itemView.findViewById(R.id.item_check_box);
            increase = itemView.findViewById(R.id.item_cart_increase);
            decrease = itemView.findViewById(R.id.item_cart_decrease);
            delete = itemView.findViewById(R.id.delete);
            unit = itemView.findViewById(R.id.unit);
        }
    }

    public void setChecked(int position, boolean checked) {
        Cart cart = mCart.get(position);
        cart.setChecked(checked);
        notifyItemChanged(position);
    }

    private void showConfirmDeleteDialog(Cart cartItem, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_delete_product_cart, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng chọn "Xóa"
                mCart.remove(position);
                notifyItemRemoved(position);
                // Xóa sản phẩm khỏi Firebase
                deleteFromFirebase(cartItem.getId_product());
                dialog.dismiss();
                Toast.makeText(mContext, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updateQuantityInFirebase(String cartItemId, int quantity) {
        cartRef.child(cartItemId).child("quantity").setValue(quantity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Cập nhật số lượng thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFromFirebase(String cartItemId) {
        cartRef.child(cartItemId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Đã xóa sản phẩm khỏi Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Lỗi khi xóa sản phẩm khỏi Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}