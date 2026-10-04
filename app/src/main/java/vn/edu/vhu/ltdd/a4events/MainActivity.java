package vn.edu.vhu.ltdd.a4events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A231A290127";

    private EditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvBmi, tvPhanLoai;

    // Khai báo biến cho Bài NC2 (Lịch sử)
    private TextView tvLichSu;
    private ArrayList<String> danhSachLichSu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // ---- Ánh xạ view ----
        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        tvKetQua = findViewById(R.id.tvKetQua);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        tvBmi = findViewById(R.id.tvBmi);
        tvPhanLoai = findViewById(R.id.tvPhanLoai);

        // Ánh xạ view cho Bài NC2
        tvLichSu = findViewById(R.id.tvLichSu);

        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        Button btnXoa = findViewById(R.id.btnXoa);
        Button btnTinhBmi = findViewById(R.id.btnTinhBmi);

        // Ánh xạ view cho Bài NC1
        Button btnDaoDau = findViewById(R.id.btnDaoDau);
        Button btnPhanTram = findViewById(R.id.btnPhanTram);

        // ---- Cách 1: mỗi nút một listener bằng biểu thức lambda ----
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

        // ---- Cách 2: một listener dùng chung, phân biệt bằng id ----
        View.OnClickListener chung = v -> {
            int id = v.getId();
            if (id == R.id.btnNhan) {
                tinhToan('*');
            } else if (id == R.id.btnChia) {
                tinhToan('/');
            }
        };
        btnNhan.setOnClickListener(chung);
        btnChia.setOnClickListener(chung);

        btnXoa.setOnClickListener(v -> xoaTrang());
        btnTinhBmi.setOnClickListener(v -> tinhBmi());

        // ================= XỬ LÝ BÀI NC1 (± và %) =================
        View.OnClickListener listenerDon = v -> {
            // Xem con trỏ đang ở ô A hay ô B
            EditText edtDangChon = edtSoA.hasFocus() ? edtSoA : (edtSoB.hasFocus() ? edtSoB : null);
            if (edtDangChon == null || edtDangChon.getText().toString().isEmpty()) return;

            try {
                double so = Double.parseDouble(edtDangChon.getText().toString());
                if (v.getId() == R.id.btnDaoDau) {
                    so = so * -1;
                } else if (v.getId() == R.id.btnPhanTram) {
                    so = so / 100.0;
                }
                edtDangChon.setText(String.valueOf(so));
                edtDangChon.setSelection(edtDangChon.getText().length());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập đúng số", Toast.LENGTH_SHORT).show();
            }
        };
        btnDaoDau.setOnClickListener(listenerDon);
        btnPhanTram.setOnClickListener(listenerDon);
    }

    // =============== MÁY TÍNH ===============

    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText().toString().trim();
        String chuoiB = edtSoB.getText().toString().trim();

        if (chuoiA.isEmpty()) {
            edtSoA.setError(getString(R.string.err_empty));
            edtSoA.requestFocus();
            return;
        }
        if (chuoiB.isEmpty()) {
            edtSoB.setError(getString(R.string.err_empty));
            edtSoB.requestFocus();
            return;
        }

        double a, b;
        try {
            a = Double.parseDouble(chuoiA);
            b = Double.parseDouble(chuoiB);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Dữ liệu nhập không phải số: '" + chuoiA + "', '" + chuoiB + "'", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        if (phepToan == '/' && b == 0) {
            edtSoB.setError(getString(R.string.err_divide_zero));
            Toast.makeText(this, R.string.err_divide_zero, Toast.LENGTH_SHORT).show();
            return;
        }

        double ketQua;
        switch (phepToan) {
            case '+': ketQua = a + b; break;
            case '-': ketQua = a - b; break;
            case '*': ketQua = a * b; break;
            default:  ketQua = a / b; break;
        }

        tvKetQua.setText(String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, phepToan, b, ketQua));
        Log.d(TAG, "Phép tính: " + a + " " + phepToan + " " + b + " = " + ketQua);

        // ================= XỬ LÝ LƯU LỊCH SỬ (BÀI NC2) =================
        String phepTinh = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, phepToan, b, ketQua);
        danhSachLichSu.add(0, phepTinh); // Thêm lên đầu danh sách
        if (danhSachLichSu.size() > 5) {
            danhSachLichSu.remove(5); // Xóa bớt nếu quá 5
        }
        hienThiLichSu();
    }

    private void xoaTrang() {
        edtSoA.setText("");
        edtSoB.setText("");
        edtSoA.setError(null);
        edtSoB.setError(null);
        tvKetQua.setText(R.string.result_placeholder);
        edtSoA.requestFocus();
    }

    // Hàm hiển thị lịch sử (Bài NC2)
    private void hienThiLichSu() {
        StringBuilder sb = new StringBuilder();
        for (String s : danhSachLichSu) {
            sb.append(s).append("\n");
        }
        tvLichSu.setText(sb.toString());
    }

    // Xử lý xoay màn hình không mất dữ liệu (Bài NC2)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("LICH_SU", danhSachLichSu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<String> savedList = savedInstanceState.getStringArrayList("LICH_SU");
            if (savedList != null) {
                danhSachLichSu = savedList;
                hienThiLichSu();
            }
        }
    }

    // =============== BMI ===============

    private void tinhBmi() {
        try {
            double canNang = Double.parseDouble(edtCanNang.getText().toString().trim());
            double chieuCao = Double.parseDouble(edtChieuCao.getText().toString().trim());

            if (canNang <= 0 || chieuCao <= 0) {
                Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chieuCao > 3) {
                chieuCao = chieuCao / 100.0;
            }

            double bmi = canNang / (chieuCao * chieuCao);
            tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));
            tvPhanLoai.setText(phanLoai(bmi));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi nhập liệu BMI", e);
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    private String phanLoai(double bmi) {
        if (bmi < 18.5) return getString(R.string.bmi_under);
        if (bmi < 23) return getString(R.string.bmi_normal);
        if (bmi < 25) return getString(R.string.bmi_over);
        return getString(R.string.bmi_obese);
    }
}