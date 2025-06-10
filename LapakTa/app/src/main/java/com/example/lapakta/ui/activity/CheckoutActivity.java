package com.example.lapakta.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.lapakta.R;
import com.example.lapakta.data.local.CartManager;
import com.example.lapakta.data.model.CartItem;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inisialisasi Views
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        EditText etAddress = findViewById(R.id.etAddress);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        TextView tvOrderSummary = findViewById(R.id.tvOrderSummary);
        TextView tvFinalTotal = findViewById(R.id.tvFinalTotal);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Tampilkan ringkasan pesanan
        displayOrderSummary(tvOrderSummary, tvFinalTotal);

        btnPlaceOrder.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (fullName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Harap isi semua informasi pengiriman!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
            RadioButton selectedPaymentRadioButton = findViewById(selectedPaymentId);
            String paymentMethod = selectedPaymentRadioButton.getText().toString();

            // Tampilkan dialog konfirmasi akhir
            showOrderConfirmationDialog(fullName, paymentMethod);
        });
    }

    private void displayOrderSummary(TextView summaryView, TextView totalView) {
        List<CartItem> cartItems = CartManager.getCartItems(this);
        StringBuilder summaryBuilder = new StringBuilder();
        double totalPrice = 0;

        for (CartItem item : cartItems) {
            double subtotal = item.getProduct().getPrice() * item.getQuantity();
            summaryBuilder.append(item.getQuantity())
                    .append("x ")
                    .append(item.getProduct().getTitle())
                    .append(" - Rp ")
                    .append(String.format("%,.2f", subtotal))
                    .append("\n");
            totalPrice += subtotal;
        }

        summaryView.setText(summaryBuilder.toString());
        totalView.setText("Total Akhir: Rp " + String.format("%,.2f", totalPrice));
    }

    private void showOrderConfirmationDialog(String customerName, String paymentMethod) {
        new AlertDialog.Builder(this)
                .setTitle("Pesanan Diterima!")
                .setMessage("Terima kasih, " + customerName + "! Pesanan Anda akan segera diproses. Metode pembayaran: " + paymentMethod)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Kosongkan keranjang dan kembali ke halaman utama
                    CartManager.clearCart(this);
                    Toast.makeText(this, "Terima kasih telah berbelanja!", Toast.LENGTH_LONG).show();
                    // Kembali ke MainActivity
                    finish(); // Menutup CheckoutActivity
                })
                .setCancelable(false) // User tidak bisa menutup dialog ini tanpa menekan OK
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
