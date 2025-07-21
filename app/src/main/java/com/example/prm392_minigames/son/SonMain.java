package com.example.prm392_minigames.son;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_minigames.R;
import com.example.prm392_minigames.son.activities.QuizActivity;
import com.example.prm392_minigames.son.activities.ScoreActivity;
import com.example.prm392_minigames.son.adapters.CategoryAdapter;
import com.example.prm392_minigames.son.entities.Category;
import com.example.prm392_minigames.son.entities.Question;
import com.example.prm392_minigames.son.viewmodels.QuizViewModel;

import java.util.ArrayList;
import java.util.List;

public class SonMain extends AppCompatActivity {
    private QuizViewModel quizViewModel;
    private CategoryAdapter categoryAdapter;
    private EditText etSearch;
    private Spinner spinnerSort;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.son_main);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupSearchAndSort();
        maybePopulateDatabase();
//        loadInitialQuestions();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        spinnerSort = findViewById(R.id.spinner_sort);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                Intent intent = new Intent(SonMain.this, QuizActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            }

            @Override
            public void onViewScoreClick(Category category) {
                Intent intent = new Intent(SonMain.this, ScoreActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        quizViewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.setCategories(categories);
        });
    }

    private void setupSearchAndSort() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    quizViewModel.getAllCategories().observe(SonMain.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                } else {
                    quizViewModel.searchCategories(s.toString()).observe(SonMain.this, categories -> {
                        categoryAdapter.setCategories(categories);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String[] sortOptions = {"Name", "Questions", "Score"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        quizViewModel.getCategoriesSortedByName().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                    case 1:
                        quizViewModel.getCategoriesSortedByQuestions().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                    case 2:
                        quizViewModel.getCategoriesSortedByScore().observe(SonMain.this, categories -> {
                            categoryAdapter.setCategories(categories);
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void maybePopulateDatabase() {
        quizViewModel.getAllCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                populateDatabase();
            }
        });
    }

    private void populateDatabase() {
        Category[] categories = new Category[]{
                new Category("Toán học", "Kiểm tra kỹ năng toán học của bạn"),
                new Category("Khoa học", "Câu hỏi khoa học tổng quát"),
                new Category("Lịch sử", "Kiến thức lịch sử thế giới"),
                new Category("Văn học", "Tác phẩm và tác giả văn học"),
                new Category("Địa lý", "Địa danh, địa lý tự nhiên"),
                new Category("Thể thao", "Kiến thức thể thao tổng hợp"),
                new Category("Công nghệ", "IT, lập trình, điện tử"),
                new Category("Âm nhạc", "Nhạc lý, nhạc sĩ, bài hát")
        };

        for (int i = 0; i < categories.length; i++) {
            categories[i].setTotalQuestions(50);
            categories[i].setMaxScore(500);
            quizViewModel.insertCategory(categories[i]);
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000); // chờ categories insert xong

                // === Toán học (Category 1) ===
                quizViewModel.insertQuestion(new Question(1, "Căn bậc hai của 169 là?", "12", "13", "14", "15", 2, 10, "√169 = 13", "Tìm số nhân chính nó ra 169", false));
                quizViewModel.insertQuestion(new Question(1, "Đạo hàm của sin(x) là?", "cos(x)", "-cos(x)", "sin(x)", "-sin(x)", 1, 20, "d/dx[sin(x)] = cos(x)", "Công thức đạo hàm lượng giác", true));
                // ... thêm 48 câu hỏi tương tự (logic, đại số, xác suất, vi phân...)
                quizViewModel.insertQuestion(new Question(1, "Số nguyên tố lớn hơn 50 đầu tiên là?", "51", "53", "59", "61", 2, 20, "Số nguyên tố sau 47 là 53", "Không chia hết cho số nào ngoài 1 và chính nó", true));
                quizViewModel.insertQuestion(new Question(1, "Giải phương trình: x² - 4 = 0", "-2", "2", "±2", "0", 3, 10, "x² = 4 ⇒ x = ±2", "Căn bậc hai của 4", false));
                quizViewModel.insertQuestion(new Question(1, "∫x dx bằng?", "x² + C", "x²/2 + C", "ln|x| + C", "1/x + C", 2, 20, "Tích phân x là x²/2 + C", "Tăng bậc", true));
                quizViewModel.insertQuestion(new Question(1, "Số nào là bội số của 12?", "30", "36", "42", "44", 2, 10, "12 × 3 = 36", "Kiểm tra chia hết cho 12", false));
                quizViewModel.insertQuestion(new Question(1, "Số chẵn nhỏ nhất lớn hơn 100?", "102", "100", "104", "101", 1, 10, "Sau 100 là 102", "Số chia hết cho 2", false));
                quizViewModel.insertQuestion(new Question(1, "Giá trị đạo hàm tại x=0 của hàm f(x) = x³ là?", "0", "1", "3", "-1", 1, 20, "f’(x) = 3x², tại x=0 là 0", "Đạo hàm tại điểm", true));
                quizViewModel.insertQuestion(new Question(1, "Nếu P(A) = 0.6, P(B) = 0.5, P(A∩B) = 0.3 thì P(A∪B) là?", "0.9", "0.8", "1.1", "0.7", 2, 20, "P(A∪B) = P(A) + P(B) – P(A∩B) = 0.6+0.5–0.3=0.8", "Công thức xác suất hợp", true));
                quizViewModel.insertQuestion(new Question(1, "Hình lập phương có bao nhiêu mặt?", "4", "6", "8", "12", 2, 10, "Hình lập phương có 6 mặt vuông", "Tất cả các mặt đều vuông", false));
                quizViewModel.insertQuestion(new Question(1, "Tính đạo hàm hàm số y = ln(x)", "1/x", "ln(x)", "x", "e^x", 1, 20, "d/dx[ln(x)] = 1/x", "Hàm log tự nhiên", true));
                quizViewModel.insertQuestion(new Question(1, "Một năm nhuận có bao nhiêu ngày?", "365", "366", "364", "360", 2, 10, "Năm nhuận thêm ngày 29/2", "Năm chia hết cho 4", false));
                quizViewModel.insertQuestion(new Question(1, "Thể tích hình lập phương cạnh a là?", "a²", "a³", "2a", "a²/2", 2, 10, "V = a³", "Cạnh × Cạnh × Cạnh", false));
                quizViewModel.insertQuestion(new Question(1, "Giải phương trình: 2x – 5 = 9", "5", "6", "7", "8", 3, 10, "2x = 14 ⇒ x = 7", "Chuyển vế chia hai vế", false));
                quizViewModel.insertQuestion(new Question(1, "Giải hệ: x + y = 5; x - y = 1", "x=2, y=3", "x=3, y=2", "x=4, y=1", "x=5, y=0", 2, 20, "Giải bằng cộng/khử", "Hệ phương trình tuyến tính", true));
                quizViewModel.insertQuestion(new Question(1, "Hình tròn có bao nhiêu trục đối xứng?", "1", "2", "vô số", "4", 3, 20, "Mỗi đường kính là trục đối xứng", "Đối xứng qua tâm", true));
                quizViewModel.insertQuestion(new Question(1, "Số thực âm lớn nhất là?", "Không tồn tại", "-1", "0", "-∞", 1, 20, "Không có số thực âm lớn nhất", "Luôn có số lớn hơn gần 0", true));
                quizViewModel.insertQuestion(new Question(1, "Tổng các số chẵn nhỏ hơn 10?", "20", "30", "10", "40", 1, 10, "2+4+6+8=20", "Cộng dãy số chẵn", false));
                quizViewModel.insertQuestion(new Question(1, "Một hình vuông có chu vi 16 thì cạnh là?", "2", "4", "6", "8", 2, 10, "C = 4a ⇒ a = 16/4 = 4", "Chu vi hình vuông", false));
                quizViewModel.insertQuestion(new Question(1, "Giải: log₂(8) = ?", "2", "3", "4", "1", 2, 20, "2^3 = 8", "Cơ số 2", true));
                quizViewModel.insertQuestion(new Question(1, "Số lượng tập con của tập 3 phần tử?", "3", "6", "8", "9", 3, 20, "2^3 = 8", "Tập con = 2^n", true));
                quizViewModel.insertQuestion(new Question(1, "Đồ thị hàm y = x² có hình gì?", "Đường thẳng", "Parabol", "Elip", "Đường tròn", 2, 10, "Parabol mở lên", "Hàm bậc 2", false));
                quizViewModel.insertQuestion(new Question(1, "Giải: x² – 2x + 1 = 0", "x = 1", "x = 0", "x = -1", "x = ±1", 1, 10, "(x–1)² = 0", "Phương trình trùng nghiệm", false));
                quizViewModel.insertQuestion(new Question(1, "Hệ số góc đường thẳng y = 3x + 2 là?", "2", "3", "1", "0", 2, 10, "Hệ số của x là 3", "y = ax + b", false));
                quizViewModel.insertQuestion(new Question(1, "∫cos(x)dx = ?", "sin(x) + C", "-sin(x) + C", "cos(x) + C", "-cos(x) + C", 1, 20, "Đạo hàm của sin(x) là cos(x)", "Hàm lượng giác", true));
                quizViewModel.insertQuestion(new Question(1, "Tập xác định của hàm y = 1/(x-1)?", "x ≠ 1", "x ≠ 0", "x ≥ 1", "x < 1", 1, 20, "Mẫu ≠ 0 ⇒ x ≠ 1", "Không chia cho 0", true));
                quizViewModel.insertQuestion(new Question(1, "Đạo hàm của |x| là?", "1", "-1", "không xác định tại x=0", "x", 3, 20, "Không liên tục tại 0", "Hàm tuyệt đối", true));
                quizViewModel.insertQuestion(new Question(1, "1! + 2! + 3! = ?", "9", "10", "11", "12", 4, 10, "1 + 2 + 6 = 9", "Giai thừa cơ bản", false));
                quizViewModel.insertQuestion(new Question(1, "Số cách chọn 2 người từ 5 người?", "10", "5", "20", "15", 1, 20, "C(5,2) = 10", "Tổ hợp chập k", true));
                quizViewModel.insertQuestion(new Question(1, "Giá trị của lim x→0 sin(x)/x là?", "0", "1", "∞", "không xác định", 2, 20, "Định lý cơ bản giải tích", "Giới hạn lượng giác", true));
                quizViewModel.insertQuestion(new Question(1, "Nếu x + y = 10 và x = 6 thì y = ?", "4", "6", "8", "2", 1, 10, "10 - 6 = 4", "Thay vào phương trình", false));


                // === Khoa học (Category 2) ===
                quizViewModel.insertQuestion(new Question(2, "Đơn vị đo lực là?", "Newton", "Joule", "Pascal", "Watt", 1, 10, "F đo bằng Newton", "F = ma", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tố nhẹ nhất?", "Helium", "Oxygen", "Hydrogen", "Carbon", 3, 10, "Hydrogen chỉ có 1 proton", "Ký hiệu H", false));
                // ... thêm 48 câu (vật lý, sinh học, hoá học)
                // Thêm 48 câu hỏi khoa học sau vào phần === Khoa học (Category 2) ===

                quizViewModel.insertQuestion(new Question(2, "Công thức hóa học của nước là?", "H2O", "CO2", "NaCl", "CH4", 1, 10, "Nước gồm 2 nguyên tử H và 1 nguyên tử O", "Hợp chất phổ biến nhất", false));
                quizViewModel.insertQuestion(new Question(2, "Tốc độ ánh sáng trong chân không là?", "300.000 km/s", "150.000 km/s", "500.000 km/s", "100.000 km/s", 1, 15, "c ≈ 3×10⁸ m/s", "Hằng số vật lý cơ bản", false));
                quizViewModel.insertQuestion(new Question(2, "DNA có bao nhiều loại base?", "4", "3", "5", "6", 1, 10, "A, T, G, C", "Cơ sở di truyền", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tố có ký hiệu Au là?", "Vàng", "Bạc", "Đồng", "Sắt", 1, 10, "Au từ tiếng Latin Aurum", "Kim loại quý", false));
                quizViewModel.insertQuestion(new Question(2, "Loại tế bào nào không có nhân?", "Vi khuẩn", "Động vật", "Thực vật", "Nấm", 1, 15, "Prokaryote không có màng nhân", "Sinh học tế bào", false));
                quizViewModel.insertQuestion(new Question(2, "Định luật bảo toàn năng lượng thuộc nguyên lý nào?", "Nhiệt động lực học thứ nhất", "Nhiệt động lực học thứ hai", "Định luật Newton", "Định luật Ohm", 1, 20, "Năng lượng không tự sinh tự diệt", "Vật lý nhiệt", true));
                quizViewModel.insertQuestion(new Question(2, "Máu có màu đỏ do chất nào?", "Hemoglobin", "Chlorophyll", "Melanin", "Insulin", 1, 10, "Protein chứa sắt vận chuyển O2", "Sinh lý người", false));
                quizViewModel.insertQuestion(new Question(2, "Hành tinh nào gần Mặt trời nhất?", "Sao Thủy", "Sao Kim", "Trái Đất", "Sao Hỏa", 1, 10, "Mercury - hành tinh trong cùng", "Thiên văn học", false));
                quizViewModel.insertQuestion(new Question(2, "pH = 7 có tính chất gì?", "Trung tính", "Axit", "Bazơ", "Muối", 1, 10, "pH = 7 là trung tính", "Hóa học axit-bazơ", false));
                quizViewModel.insertQuestion(new Question(2, "Quá trình nào tạo ra oxygen?", "Quang hợp", "Hô hấp", "Lên men", "Tiêu hóa", 1, 10, "6CO2 + 6H2O → C6H12O6 + 6O2", "Sinh học thực vật", false));
                quizViewModel.insertQuestion(new Question(2, "Đơn vị đo cường độ dòng điện là?", "Ampere", "Volt", "Ohm", "Watt", 1, 10, "A - đơn vị SI", "Điện học cơ bản", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tố phổ biến nhất trong vũ trụ?", "Hydrogen", "Helium", "Oxygen", "Carbon", 1, 15, "Chiếm ~75% khối lượng vũ trụ", "Thiên văn vật lý", false));
                quizViewModel.insertQuestion(new Question(2, "Bộ phận nào điều khiển huyết áp?", "Thận", "Gan", "Phổi", "Dạ dày", 1, 15, "Thận tiết renin điều hòa huyết áp", "Sinh lý học", false));
                quizViewModel.insertQuestion(new Question(2, "Tế bào máu trắng có chức năng gì?", "Miễn dịch", "Vận chuyển O2", "Đông máu", "Tiêu hóa", 1, 10, "Bảo vệ cơ thể khỏi bệnh tật", "Hệ miễn dịch", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên lý Archimedes liên quan đến?", "Lực đẩy Archimedes", "Định luật Newton", "Định luật Ohm", "Định luật Boyle", 1, 15, "Vật chìm trong chất lỏng", "Vật lý chất lỏng", false));
                quizViewModel.insertQuestion(new Question(2, "Enzyme có vai trò gì?", "Xúc tác sinh học", "Kháng thể", "Hormone", "Vitamin", 1, 15, "Tăng tốc phản ứng sinh hóa", "Sinh hóa học", false));
                quizViewModel.insertQuestion(new Question(2, "Khí nào gây hiệu ứng nhà kính mạnh nhất?", "CO2", "O2", "N2", "H2", 1, 15, "Carbon dioxide hấp thụ hồng ngoại", "Môi trường", false));
                quizViewModel.insertQuestion(new Question(2, "Nước sôi ở nhiệt độ nào (1 atm)?", "100°C", "90°C", "110°C", "120°C", 1, 10, "Điểm sôi chuẩn của nước", "Vật lý nhiệt", false));
                quizViewModel.insertQuestion(new Question(2, "Gen là đoạn DNA mã hóa cho?", "Protein", "Lipid", "Carbohydrate", "Vitamin", 1, 15, "Gen → mRNA → Protein", "Di truyền học", false));
                quizViewModel.insertQuestion(new Question(2, "Lỗ đen có khối lượng tối thiểu?", "3 lần khối lượng Mặt trời", "1 lần khối lượng Mặt trời", "10 lần khối lượng Mặt trời", "0.5 lần khối lượng Mặt trời", 1, 25, "Giới hạn Chandrasekhar", "Thiên thể học", true));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tố nào cần thiết cho tuyến giáp?", "Iod", "Sắt", "Canxi", "Kẽm", 1, 15, "I tạo hormone thyroxin", "Dinh dưỡng học", false));
                quizViewModel.insertQuestion(new Question(2, "Tần số sóng âm thanh người nghe được?", "20Hz - 20kHz", "1Hz - 10kHz", "100Hz - 50kHz", "10Hz - 100kHz", 1, 15, "Dải tần âm thanh", "Vật lý sóng", false));
                quizViewModel.insertQuestion(new Question(2, "Cơ quan nào sản xuất insulin?", "Tụy", "Gan", "Thận", "Lách", 1, 15, "Tế bào beta trong tụy", "Nội tiết học", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tử có cấu trúc như thế nào?", "Nhân + electron", "Chỉ có proton", "Chỉ có neutron", "Proton + neutron", 1, 10, "Mô hình nguyên tử Bohr", "Vật lý nguyên tử", false));
                quizViewModel.insertQuestion(new Question(2, "Vitamin nào tan trong nước?", "Vitamin C", "Vitamin A", "Vitamin D", "Vitamin E", 1, 15, "C và B tan nước, A,D,E,K tan lipid", "Dinh dưỡng", false));
                quizViewModel.insertQuestion(new Question(2, "Định luật Mendel về?", "Di truyền", "Tiến hóa", "Sinh thái", "Phân loại", 1, 15, "Quy luật phân ly và tự do tổ hợp", "Di truyền cổ điển", false));
                quizViewModel.insertQuestion(new Question(2, "Hạt nhân nguyên tử chứa?", "Proton + Neutron", "Chỉ proton", "Proton + Electron", "Chỉ neutron", 1, 10, "Nucleon trong nhân", "Cấu trúc nguyên tử", false));
                quizViewModel.insertQuestion(new Question(2, "Quá trình nào tạo ATP?", "Hô hấp tế bào", "Quang hợp", "Tiêu hóa", "Bài tiết", 1, 20, "Glycolysis, Krebs, chuỗi electron", "Sinh hóa tế bào", true));
                quizViewModel.insertQuestion(new Question(2, "Tia X có tần số như thế nào?", "Cao hơn ánh sáng", "Thấp hơn ánh sáng", "Bằng ánh sáng", "Không có tần số", 1, 20, "Bức xạ điện từ năng lượng cao", "Vật lý bức xạ", true));
                quizViewModel.insertQuestion(new Question(2, "Hiện tượng cảm ứng điện từ do ai phát hiện?", "Faraday", "Newton", "Einstein", "Tesla", 1, 20, "Michael Faraday 1831", "Lịch sử khoa học", true));
                quizViewModel.insertQuestion(new Question(2, "Phản ứng phân hạch hạt nhân tạo ra?", "Năng lượng + hạt nhân nhỏ", "Chỉ năng lượng", "Chỉ hạt nhân mới", "Electron", 1, 20, "E = mc² của Einstein", "Vật lý hạt nhân", true));
                quizViewModel.insertQuestion(new Question(2, "Hormone nào điều hòa glucose?", "Insulin và Glucagon", "Chỉ insulin", "Adrenalin", "Testosterone", 1, 15, "Insulin giảm, glucagon tăng đường huyết", "Sinh lý nội tiết", false));
                quizViewModel.insertQuestion(new Question(2, "Đơn vị đo độ phóng xạ là?", "Becquerel", "Gray", "Sievert", "Curie", 1, 20, "Bq = phân rã/giây", "Vật lý hạt nhân", true));
                quizViewModel.insertQuestion(new Question(2, "Quá trình mitosis tạo ra?", "2 tế bào giống nhau", "4 tế bào khác nhau", "1 tế bào lớn", "Nhiều tế bào nhỏ", 1, 15, "Phân bào nguyên phân", "Sinh học tế bào", false));
                quizViewModel.insertQuestion(new Question(2, "Định luật Ohm là?", "V = I × R", "F = m × a", "E = m × c²", "P = F × v", 1, 15, "Điện áp = dòng × trở kháng", "Điện học", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên tố nào có 6 proton?", "Carbon", "Nitrogen", "Oxygen", "Boron", 1, 10, "C có số hiệu nguyên tử 6", "Bảng tuần hoàn", false));
                quizViewModel.insertQuestion(new Question(2, "Meiosis tạo ra?", "Giao tử", "Tế bào soma", "Hormone", "Enzyme", 1, 15, "Giảm phân tạo tinh trùng, trứng", "Sinh sản", false));
                quizViewModel.insertQuestion(new Question(2, "Ánh sáng có tính chất gì?", "Sóng và hạt", "Chỉ sóng", "Chỉ hạt", "Không phải sóng", 1, 20, "Lưỡng tính sóng-hạt", "Vật lý lượng tử", true));
                quizViewModel.insertQuestion(new Question(2, "Tế bào thực vật có gì mà động vật không?", "Thành tế bào", "Màng tế bào", "Nhân", "Ribosome", 1, 15, "Cellulose tạo thành tế bào", "Sinh học so sánh", false));
                quizViewModel.insertQuestion(new Question(2, "Nguyên lý bất định Heisenberg về?", "Vị trí và động lượng", "Năng lượng và thời gian", "Khối lượng và tốc độ", "Tất cả đều đúng", 4, 25, "Không thể đo chính xác đồng thời", "Cơ học lượng tử", true));
                quizViewModel.insertQuestion(new Question(2, "Hormone auxin ở thực vật có tác dụng?", "Kéo dài tế bào", "Nở hoa", "Rụng lá", "Tạo quả", 1, 20, "Auxin thúc đẩy sinh trưởng", "Sinh lý thực vật", true));
                quizViewModel.insertQuestion(new Question(2, "Chu kỳ bán rã là gì?", "Thời gian phân rã 50%", "Thời gian phân rã hoàn toàn", "Thời gian tạo đồng vị", "Thời gian phản ứng", 1, 20, "t½ - đặc trưng chất phóng xạ", "Hóa hạt nhân", true));
                quizViewModel.insertQuestion(new Question(2, "Enzyme pepsin hoạt động ở đâu?", "Dạ dày", "Ruột non", "Miệng", "Gan", 1, 15, "Môi trường axit dạ dày", "Tiêu hóa", false));
                quizViewModel.insertQuestion(new Question(2, "Định luật Boyle về?", "P × V = const", "P + V = const", "P - V = const", "P/V = const", 1, 15, "Áp suất tỷ lệ nghịch thể tích", "Vật lý khí", false));
                quizViewModel.insertQuestion(new Question(2, "Quá trình nào tạo ozone?", "Phóng điện trong O2", "Đốt cháy", "Quang hợp", "Hô hấp", 1, 20, "3O2 → 2O3 (có năng lượng)", "Hóa học khí quyển", true));
                quizViewModel.insertQuestion(new Question(2, "Thị lực màu đỏ-xanh bị khiếm khuyết do?", "Thiếu gen trên NST X", "Thiếu vitamin A", "Tổn thương não", "Tuổi già", 1, 20, "Daltonism - di truyền liên kết giới tính", "Di truyền y học", true));
                quizViewModel.insertQuestion(new Question(2, "Cơ chế miễn dịch tự nhiên đầu tiên là?", "Da và niêm mạc", "Kháng thể", "Tế bào T", "Vaccine", 1, 15, "Rào cản vật lý đầu tiên", "Miễn dịch học", false));
                quizViewModel.insertQuestion(new Question(2, "Hiệu ứng Doppler giải thích?", "Thay đổi tần số do chuyển động", "Khúc xạ ánh sáng", "Giao thoa sóng", "Phản xạ âm", 1, 20, "f' = f(v±vr)/(v±vs)", "Vật lý sóng nâng cao", true));

                // === Lịch sử (Category 3) ===
                quizViewModel.insertQuestion(new Question(3, "Trận chiến Điện Biên Phủ diễn ra năm?", "1952", "1953", "1954", "1955", 3, 10, "Chiến thắng lịch sử năm 1954", "Thực dân Pháp", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là tác giả Tuyên ngôn Độc lập Mỹ?", "Lincoln", "Jefferson", "Washington", "Adams", 2, 20, "Thomas Jefferson", "Ngày 4/7", true));
                // ... thêm 48 câu (cách mạng, chiến tranh, nhân vật lịch sử)
                quizViewModel.insertQuestion(new Question(3, "Chiến tranh thế giới thứ hai kết thúc năm nào?", "1944", "1945", "1946", "1947", 2, 10, "Chiến tranh kết thúc năm 1945", "Tháng 9/1945", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là vị vua đầu tiên của triều Nguyễn?", "Gia Long", "Minh Mạng", "Thiệu Trị", "Tự Đức", 1, 10, "Gia Long thống nhất đất nước năm 1802", "Nguyễn Ánh", false));
                quizViewModel.insertQuestion(new Question(3, "Bức tường Berlin sụp đổ năm nào?", "1987", "1988", "1989", "1990", 3, 10, "Sự kiện diễn ra năm 1989", "Đông - Tây Đức", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là người sáng lập ra Đảng Cộng sản Việt Nam?", "Lê Duẩn", "Trường Chinh", "Hồ Chí Minh", "Phạm Văn Đồng", 3, 20, "Nguyễn Ái Quốc sáng lập vào 1930", "Năm 1930", true));
                quizViewModel.insertQuestion(new Question(3, "Nền văn minh nào xây dựng kim tự tháp?", "La Mã", "Hy Lạp", "Ai Cập", "Ba Tư", 3, 10, "Kim tự tháp Giza là của Ai Cập", "Pharaoh", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là tổng thống Mỹ trong chiến tranh Việt Nam?", "Reagan", "Nixon", "Bush", "Obama", 2, 10, "Nixon rút quân khỏi Việt Nam", "Đàm phán Paris", false));
                quizViewModel.insertQuestion(new Question(3, "Cuộc cách mạng nào dẫn đến nền cộng hòa Pháp?", "Cách mạng công nghiệp", "Cách mạng tháng 10", "Cách mạng Pháp", "Cách mạng Mỹ", 3, 10, "Diễn ra 1789", "Chống lại vua Louis XVI", false));
                quizViewModel.insertQuestion(new Question(3, "Chiến tranh lạnh diễn ra giữa hai quốc gia nào?", "Mỹ và Anh", "Mỹ và Trung Quốc", "Mỹ và Liên Xô", "Mỹ và Đức", 3, 10, "Mỹ và Liên Xô đối đầu về chính trị", "Sau WWII", false));
                quizViewModel.insertQuestion(new Question(3, "Nhà Trần nổi tiếng với chiến thắng nào?", "Chi Lăng", "Bạch Đằng", "Đống Đa", "Hàm Tử", 2, 10, "Trần Hưng Đạo chỉ huy", "Chống Mông Nguyên", false));
                quizViewModel.insertQuestion(new Question(3, "Hiệp định Paris về Việt Nam được ký năm nào?", "1972", "1973", "1974", "1975", 2, 10, "Hiệp định ký đầu 1973", "Kết thúc chiến tranh", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là người thống nhất nước Ý?", "Mussolini", "Garibaldi", "Cavour", "Napoleon", 2, 10, "Garibaldi là nhà cách mạng Ý", "Phong trào Risorgimento", false));
                quizViewModel.insertQuestion(new Question(3, "Hội nghị Yalta gồm các lãnh đạo nước nào?", "Anh, Mỹ, Pháp", "Mỹ, Nga, Đức", "Anh, Mỹ, Liên Xô", "Pháp, Mỹ, Trung Quốc", 3, 20, "Churchill, Roosevelt, Stalin", "Tháng 2/1945", true));
                quizViewModel.insertQuestion(new Question(3, "Vua nào đã dời đô về Thăng Long?", "Lê Lợi", "Ngô Quyền", "Lý Công Uẩn", "Trần Nhân Tông", 3, 10, "Năm 1010", "Chiếu dời đô", false));
                quizViewModel.insertQuestion(new Question(3, "Chế độ phong kiến chấm dứt ở Việt Nam năm nào?", "1940", "1945", "1954", "1975", 2, 10, "Cách mạng tháng Tám thành công", "Bảo Đại thoái vị", false));
                quizViewModel.insertQuestion(new Question(3, "Cuộc khởi nghĩa Hai Bà Trưng chống lại ai?", "Nhà Hán", "Nhà Tống", "Nhà Nguyên", "Nhà Minh", 1, 10, "Chống đô hộ phương Bắc", "Tô Định", false));
                quizViewModel.insertQuestion(new Question(3, "Chiến tranh Triều Tiên bắt đầu năm nào?", "1949", "1950", "1951", "1952", 2, 10, "Bắt đầu tháng 6/1950", "Bắc và Nam Triều Tiên", false));
                quizViewModel.insertQuestion(new Question(3, "Năm nào Hồ Chí Minh đọc Tuyên ngôn độc lập?", "1944", "1945", "1946", "1947", 2, 10, "2/9/1945", "Quảng trường Ba Đình", false));
                quizViewModel.insertQuestion(new Question(3, "Sự kiện 11/9 xảy ra ở đâu?", "New York", "Washington", "Los Angeles", "Chicago", 1, 10, "Máy bay đâm vào Tháp đôi", "World Trade Center", false));
                quizViewModel.insertQuestion(new Question(3, "Chiến tranh Việt Nam kết thúc bằng sự kiện nào?", "Ký hiệp định Paris", "Mỹ rút quân", "Giải phóng miền Nam", "Đổi mới", 3, 10, "30/4/1975", "Xe tăng vào Dinh Độc Lập", false));
                quizViewModel.insertQuestion(new Question(3, "Đế chế Mông Cổ do ai sáng lập?", "Tamerlane", "Attila", "Genghis Khan", "Kubilai", 3, 10, "Thành Cát Tư Hãn", "Thế kỷ 13", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là hoàng đế cuối cùng của Trung Quốc?", "Tần Thủy Hoàng", "Khang Hy", "Phổ Nghi", "Ung Chính", 3, 10, "Phổ Nghi bị phế năm 1912", "Triều đại nhà Thanh", false));
                quizViewModel.insertQuestion(new Question(3, "Hội nghị Genève 1954 giải quyết vấn đề gì?", "Chia cắt Triều Tiên", "Chấm dứt chiến tranh Triều Tiên", "Chấm dứt chiến tranh Đông Dương", "Thành lập ASEAN", 3, 20, "Chia đôi Việt Nam tại vĩ tuyến 17", "1954", true));
                quizViewModel.insertQuestion(new Question(3, "Chiến dịch Hồ Chí Minh diễn ra năm nào?", "1973", "1974", "1975", "1976", 3, 10, "Giải phóng Sài Gòn", "Tháng 4/1975", false));
                quizViewModel.insertQuestion(new Question(3, "Lãnh đạo Liên Xô thời chiến tranh thế giới II là?", "Khrushchev", "Lenin", "Stalin", "Gorbachev", 3, 10, "Joseph Stalin", "Đồng minh chống phát xít", false));
                quizViewModel.insertQuestion(new Question(3, "Cuộc cách mạng tháng 10 Nga diễn ra năm?", "1916", "1917", "1918", "1919", 2, 10, "Lật đổ chế độ Sa hoàng", "Lenin lãnh đạo", false));
                quizViewModel.insertQuestion(new Question(3, "Lễ hội tưởng niệm chiến thắng Ngọc Hồi - Đống Đa?", "30/4", "2/9", "5/5", "Mùng 5 Tết", 4, 10, "Chiến thắng của Quang Trung", "Tết Kỷ Dậu 1789", false));
                quizViewModel.insertQuestion(new Question(3, "Ai là người đầu tiên đặt chân lên mặt trăng?", "Yuri Gagarin", "Buzz Aldrin", "Neil Armstrong", "John Glenn", 3, 20, "\"One small step for man...\"", "Apollo 11", true));
                quizViewModel.insertQuestion(new Question(3, "Phong trào Đông Du do ai khởi xướng?", "Phan Bội Châu", "Phan Chu Trinh", "Trương Vĩnh Ký", "Nguyễn Trường Tộ", 1, 10, "Kêu gọi thanh niên sang Nhật học", "Đầu TK 20", false));
                quizViewModel.insertQuestion(new Question(3, "Nước nào khởi đầu Cách mạng công nghiệp?", "Pháp", "Mỹ", "Anh", "Đức", 3, 10, "Anh phát triển máy hơi nước", "Thế kỷ 18", false));
                quizViewModel.insertQuestion(new Question(3, "Nhà sử học nổi tiếng của Việt Nam?", "Trần Hưng Đạo", "Lê Quý Đôn", "Ngô Sĩ Liên", "Nguyễn Du", 3, 10, "Tác giả Đại Việt sử ký toàn thư", "Thời Lê sơ", false));


                // === Văn học (Category 4) ===
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Truyện Kiều' do ai viết?", "Nguyễn Du", "Nguyễn Trãi", "Tố Hữu", "Xuân Diệu", 1, 10, "Nguyễn Du sáng tác Truyện Kiều", "Tác giả nổi tiếng thời Nguyễn", false));
                quizViewModel.insertQuestion(new Question(4, "Shakespeare viết vở nào?", "Hamlet", "Kiều", "Tắt đèn", "Số đỏ", 1, 10, "Hamlet là bi kịch nổi tiếng", "Nhà soạn kịch Anh", false));
                // ... thêm 48 câu (thơ, văn học dân gian, tác giả nước ngoài)
                // === Văn học (Category 4) - 48 câu hỏi bổ sung ===
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Số đỏ' của ai?", "Vũ Trọng Phụng", "Nam Cao", "Ngô Tất Tố", "Thạch Lam", 1, 10, "Vũ Trọng Phụng viết Số đỏ", "Tiểu thuyết hiện thực", false));
                quizViewModel.insertQuestion(new Question(4, "'Tắt đèn' là tác phẩm của?", "Ngô Tất Tố", "Nam Cao", "Kim Lân", "Thạch Lam", 1, 10, "Ngô Tất Tố sáng tác Tắt đèn", "Phản ánh đời sống nông thôn", false));
                quizViewModel.insertQuestion(new Question(4, "Truyện 'Chí Phèo' của ai?", "Nam Cao", "Vũ Trọng Phụng", "Kim Lân", "Nguyễn Tuân", 1, 10, "Nam Cao viết Chí Phèo", "Nhân vật bi kịch xã hội", false));
                quizViewModel.insertQuestion(new Question(4, "'Đời thừa' là tác phẩm của?", "Nam Cao", "Kim Lân", "Thạch Lam", "Nguyễn Tuân", 1, 10, "Nam Cao sáng tác Đời thừa", "Phản ánh xã hội cũ", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Hạnh phúc của một tang gia'?", "Thạch Lam", "Nam Cao", "Kim Lân", "Vũ Trọng Phụng", 1, 15, "Thạch Lam viết truyện ngắn này", "Phong cách lãng mạn", false));
                quizViewModel.insertQuestion(new Question(4, "'Vang bóng một thời' của ai?", "Nguyễn Tuân", "Nam Cao", "Thạch Lam", "Kim Lân", 1, 10, "Nguyễn Tuân viết Vang bóng một thời", "Ký sự nổi tiếng", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Nàng Kiều' thuộc thể loại?", "Truyện thơ", "Tiểu thuyết", "Kịch", "Tản văn", 1, 10, "Truyện Kiều là truyện thơ", "3254 câu thơ lục bát", false));
                quizViewModel.insertQuestion(new Question(4, "'Romeo và Juliet' của Shakespeare thuộc thể loại?", "Bi kịch", "Hài kịch", "Tiểu thuyết", "Thơ", 1, 10, "Romeo và Juliet là bi kịch tình yêu", "Tác phẩm kinh điển", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Đắc nhân tâm'?", "Dale Carnegie", "Napoleon Hill", "Stephen Covey", "Tony Robbins", 1, 15, "Dale Carnegie viết Đắc nhân tâm", "Sách self-help nổi tiếng", false));
                quizViewModel.insertQuestion(new Question(4, "'Chiến tranh và hòa bình' của ai?", "Tolstoy", "Dostoevsky", "Turgenev", "Chekhov", 1, 20, "Leo Tolstoy sáng tác", "Tiểu thuyết vĩ đại Nga", true));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Anna Karenina' của?", "Tolstoy", "Dostoevsky", "Pushkin", "Gogol", 1, 15, "Leo Tolstoy viết Anna Karenina", "Tiểu thuyết về tình yêu và xã hội", false));
                quizViewModel.insertQuestion(new Question(4, "'Tội ác và hình phạt' của ai?", "Dostoevsky", "Tolstoy", "Turgenev", "Chekhov", 1, 20, "Fyodor Dostoevsky sáng tác", "Tiểu thuyết tâm lý học", true));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Don Quixote'?", "Cervantes", "Lorca", "Machado", "Unamuno", 1, 15, "Miguel de Cervantes viết Don Quixote", "Tiểu thuyết Tây Ban Nha", false));
                quizViewModel.insertQuestion(new Question(4, "'Ông già và biển cả' của?", "Hemingway", "Steinbeck", "Faulkner", "Fitzgerald", 1, 15, "Ernest Hemingway sáng tác", "Giải Nobel Văn học", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Gatsby vĩ đại' của ai?", "F. Scott Fitzgerald", "Ernest Hemingway", "William Faulkner", "John Steinbeck", 1, 15, "F. Scott Fitzgerald viết", "Tiểu thuyết Mỹ kinh điển", false));
                quizViewModel.insertQuestion(new Question(4, "'1984' là tác phẩm của?", "George Orwell", "Aldous Huxley", "Ray Bradbury", "Isaac Asimov", 1, 15, "George Orwell viết 1984", "Tiểu thuyết phản địa đàng", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Tô-màn bác' (Uncle Tom)?", "Harriet Beecher Stowe", "Mark Twain", "William Faulkner", "Harper Lee", 1, 20, "Stowe viết về chế độ nô lệ", "Tiểu thuyết chống nô lệ", true));
                quizViewModel.insertQuestion(new Question(4, "'Những người khốn khổ' của ai?", "Victor Hugo", "Balzac", "Flaubert", "Zola", 1, 15, "Victor Hugo sáng tác Les Misérables", "Tiểu thuyết Pháp", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Madame Bovary' của?", "Flaubert", "Balzac", "Zola", "Maupassant", 1, 15, "Gustave Flaubert viết", "Chủ nghĩa hiện thực Pháp", false));
                quizViewModel.insertQuestion(new Question(4, "'Cô gái Đan Mạch' thuộc văn học?", "Đan Mạch", "Thụy Điển", "Na Uy", "Phần Lan", 1, 20, "Hans Christian Andersen", "Tác giả truyện cổ tích", true));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Odyssey'?", "Homer", "Virgil", "Ovid", "Sophocles", 1, 20, "Homer là thi sĩ Hy Lạp cổ", "Sử thi về Odysseus", true));
                quizViewModel.insertQuestion(new Question(4, "'Iliad' kể về cuộc chiến nào?", "Trojan", "Marathon", "Thermopylae", "Salamis", 1, 15, "Cuộc chiến thành Troy", "Sử thi Hy Lạp cổ", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Hoàng tử bé' của?", "Antoine de Saint-Exupéry", "Jules Verne", "Albert Camus", "André Malraux", 1, 10, "Saint-Exupéry viết Le Petit Prince", "Truyện thiếu nhi kinh điển", false));
                quizViewModel.insertQuestion(new Question(4, "'Chúa tể những chiếc nhẫn' của ai?", "J.R.R. Tolkien", "C.S. Lewis", "George R.R. Martin", "Terry Pratchett", 1, 15, "Tolkien sáng tác", "Tiểu thuyết fantasy kinh điển", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Harry Potter'?", "J.K. Rowling", "Stephenie Meyer", "Suzanne Collins", "Cassandra Clare", 1, 10, "Joanne Rowling viết", "Series phù thủy nổi tiếng", false));
                quizViewModel.insertQuestion(new Question(4, "'Pride and Prejudice' của ai?", "Jane Austen", "Charlotte Brontë", "Emily Brontë", "George Eliot", 1, 15, "Jane Austen sáng tác", "Tiểu thuyết lãng mạn Anh", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Jane Eyre' của?", "Charlotte Brontë", "Emily Brontë", "Jane Austen", "George Eliot", 1, 15, "Charlotte Brontë viết", "Tiểu thuyết Gothic", false));
                quizViewModel.insertQuestion(new Question(4, "'Đồi gió hú' của ai?", "Emily Brontë", "Charlotte Brontë", "Anne Brontë", "Jane Austen", 1, 20, "Emily Brontë sáng tác Wuthering Heights", "Tiểu thuyết Gothic kinh điển", true));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Sherlock Holmes'?", "Arthur Conan Doyle", "Agatha Christie", "Edgar Allan Poe", "Raymond Chandler", 1, 10, "Doyle tạo ra thám tử Sherlock", "Tiểu thuyết trinh thám", false));
                quizViewModel.insertQuestion(new Question(4, "'Murder on the Orient Express' của?", "Agatha Christie", "Arthur Conan Doyle", "Dorothy Sayers", "Raymond Chandler", 1, 15, "Christie viết về Hercule Poirot", "Tiểu thuyết trinh thám", false));
                quizViewModel.insertQuestion(new Question(4, "Nhân vật Quasimodo trong tác phẩm nào?", "Nhà thờ Đức Bà Paris", "Những người khốn khổ", "Hội trưởng Grandet", "Bà Bovary", 1, 15, "Victor Hugo tạo nhân vật Quasimodo", "Người gù nhà thờ", false));
                quizViewModel.insertQuestion(new Question(4, "'Alice ở xứ sở thần tiên' của ai?", "Lewis Carroll", "Roald Dahl", "Beatrix Potter", "A.A. Milne", 1, 10, "Lewis Carroll sáng tác", "Truyện thiếu nhi kinh điển", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Moby Dick' kể về?", "Cá voi trắng", "Cướp biển", "Đảo hoang", "Kho báu", 1, 15, "Herman Melville viết về cá voi Moby Dick", "Tiểu thuyết phiêu lưu", false));
                quizViewModel.insertQuestion(new Question(4, "'Frankenstein' của Mary Shelley thuộc thể loại?", "Khoa học viễn tưởng", "Lãng mạn", "Hài kịch", "Lịch sử", 1, 15, "Tiểu thuyết sci-fi đầu tiên", "Về quái vật nhân tạo", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Dracula'?", "Bram Stoker", "Mary Shelley", "Edgar Allan Poe", "H.P. Lovecraft", 1, 15, "Stoker tạo ra nhân vật ma cà rồng", "Tiểu thuyết kinh dị Gothic", false));
                quizViewModel.insertQuestion(new Question(4, "'Dr. Jekyll and Mr. Hyde' của?", "Robert Louis Stevenson", "Oscar Wilde", "Arthur Conan Doyle", "H.G. Wells", 1, 20, "Stevenson viết về hai nhân cách", "Tiểu thuyết tâm lý kinh dị", true));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm nào của Oscar Wilde?", "The Picture of Dorian Gray", "Dr. Jekyll and Mr. Hyde", "Frankenstein", "Dracula", 1, 15, "Wilde viết Chân dung Dorian Gray", "Tiểu thuyết duy mỹ", false));
                quizViewModel.insertQuestion(new Question(4, "'Cậu bé Tom Sawyer' của ai?", "Mark Twain", "Louisa May Alcott", "L. Frank Baum", "Robert Louis Stevenson", 1, 10, "Mark Twain (Samuel Clemens)", "Tiểu thuyết phiêu lưu thiếu nhi", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả 'Nàng Juliet 15 tuổi' trong văn học Việt?", "Lê Minh Khuê", "Nguyễn Huy Thiệp", "Dương Hương", "Võ Thị Hảo", 1, 20, "Lê Minh Khuê viết về tuổi trẻ", "Tiểu thuyết hiện đại", true));
                quizViewModel.insertQuestion(new Question(4, "'Cô gái đến từ hôm qua' của?", "Nguyễn Tuân", "Thạch Lam", "Nguyễn Minh Châu", "Phan Tấn Hải", 1, 20, "Nguyễn Tuân viết ký", "Văn xuôi trữ tình", true));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Người con gái Nam Xương' của?", "Nguyễn Duy", "Tố Hữu", "Chế Lan Viên", "Xuân Diệu", 1, 15, "Nguyễn Duy viết thơ", "Thơ đương đại", false));
                quizViewModel.insertQuestion(new Question(4, "'Tiếng thu' là thơ của ai?", "Xuân Diệu", "Chế Lan Viên", "Huy Cận", "Thế Lữ", 1, 10, "Xuân Diệu - nhà thơ tình yêu", "Thơ mới", false));
                quizViewModel.insertQuestion(new Question(4, "Tác giả bài thơ 'Đây thôn Vĩ Dạ'?", "Hàn Mặc Tử", "Xuân Diệu", "Chế Lan Viên", "Tản Đà", 1, 15, "Hàn Mặc Tử sáng tác", "Thơ siêu hiện thực", false));
                quizViewModel.insertQuestion(new Question(4, "'Gọi tên một vì sao' của ai?", "Chế Lan Viên", "Xuân Diệu", "Huy Cận", "Thế Lữ", 1, 15, "Chế Lan Viên viết thơ tình", "Thơ mới lãng mạn", false));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Vũ nữ Giang hồ' thuộc thể loại?", "Tiểu thuyết kiếm hiệp", "Truyện ngắn", "Kịch", "Thơ", 1, 15, "Tiểu thuyết võ hiệp Kim Dung", "Văn học kiếm hiệp", false));
                quizViewModel.insertQuestion(new Question(4, "'Anh hùng xạ điêu' của ai?", "Kim Dung", "Cổ Long", "Lương Vũ Sinh", "Hoàng Dị", 1, 10, "Kim Dung (Tra Lương Ung)", "Tiểu thuyết kiếm hiệp", false));
                quizViewModel.insertQuestion(new Question(4, "Truyện 'Alice in Wonderland' có bao nhiêu phần chính?", "2", "3", "4", "5", 1, 20, "Alice's Adventures và Through the Looking-Glass", "Lewis Carroll viết 2 phần", true));
                quizViewModel.insertQuestion(new Question(4, "Tác phẩm 'Metamorphosis' kể về?", "Người biến thành côn trùng", "Người biến thành động vật", "Người biến thành thần", "Người biến thành robot", 1, 20, "Kafka viết về Gregor Samsa", "Tiểu thuyết hiện đại", true));
                quizViewModel.insertQuestion(new Question(4, "'The Catcher in the Rye' của ai?", "J.D. Salinger", "Jack Kerouac", "William Faulkner", "Harper Lee", 1, 20, "Salinger viết về Holden Caulfield", "Tiểu thuyết thanh thiếu niên", true));

                // === Địa lý (Category 5) ===
                quizViewModel.insertQuestion(new Question(5, "Núi cao nhất thế giới?", "Everest", "Phanxipang", "An-pơ", "Kilimanjaro", 1, 10, "Everest cao 8848m", "Trên dãy Himalaya", false));
                quizViewModel.insertQuestion(new Question(5, "Sông dài nhất thế giới?", "Amazon", "Nile", "Mississippi", "Hồng Hà", 2, 10, "Sông Nile dài ~6650km", "Châu Phi", false));
                // ... thêm 48 câu (quốc gia, địa hình, biển đảo)
                quizViewModel.insertQuestion(new Question(5, "Châu lục nào lớn nhất thế giới?", "Châu Á", "Châu Âu", "Châu Phi", "Châu Mỹ", 1, 10, "Châu Á lớn nhất về diện tích và dân số", "Có Trung Quốc, Ấn Độ", false));
                quizViewModel.insertQuestion(new Question(5, "Biển nào lớn nhất thế giới?", "Địa Trung Hải", "Biển Đông", "Biển Ả Rập", "Biển Philippines", 1, 10, "Địa Trung Hải nằm giữa 3 châu lục", "Gần Ý, Ai Cập", false));
                quizViewModel.insertQuestion(new Question(5, "Sa mạc nào lớn nhất thế giới?", "Sahara", "Gobi", "Kalahari", "Arabian", 1, 10, "Sahara ở Bắc Phi", "Nóng và khô", false));
                quizViewModel.insertQuestion(new Question(5, "Quốc gia có nhiều dân nhất thế giới (2024)?", "Ấn Độ", "Trung Quốc", "Mỹ", "Indonesia", 1, 10, "Ấn Độ vượt Trung Quốc năm 2023", "Dân số hơn 1.4 tỷ", false));
                quizViewModel.insertQuestion(new Question(5, "Nước nào có diện tích lớn nhất thế giới?", "Mỹ", "Canada", "Nga", "Trung Quốc", 3, 10, "Nga rộng hơn 17 triệu km²", "Châu Âu và châu Á", false));
                quizViewModel.insertQuestion(new Question(5, "Thành phố nào đông dân nhất thế giới?", "Tokyo", "Delhi", "Shanghai", "Mexico City", 1, 10, "Tokyo có hơn 37 triệu người", "Thủ đô Nhật", false));
                quizViewModel.insertQuestion(new Question(5, "Nước nào có nhiều đảo nhất?", "Philippines", "Nhật Bản", "Indonesia", "Na Uy", 3, 10, "Indonesia có hơn 17.000 đảo", "Quốc gia Đông Nam Á", false));
                quizViewModel.insertQuestion(new Question(5, "Dãy núi nào dài nhất thế giới?", "Himalaya", "Alps", "Rocky", "Andes", 4, 10, "Andes dài hơn 7000 km", "Nam Mỹ", false));
                quizViewModel.insertQuestion(new Question(5, "Thủ đô của Canada là gì?", "Toronto", "Ottawa", "Vancouver", "Montreal", 2, 10, "Ottawa là thủ đô chính thức", "Không phải thành phố lớn nhất", false));
                quizViewModel.insertQuestion(new Question(5, "Sông nào chảy qua nhiều quốc gia nhất?", "Amazon", "Nile", "Danube", "Mekong", 3, 10, "Danube chảy qua 10 nước", "Châu Âu", false));
                quizViewModel.insertQuestion(new Question(5, "Ngọn núi cao nhất Việt Nam?", "Tà Chì Nhù", "Phanxipang", "Bạch Mộc Lương Tử", "Pusilung", 2, 10, "Phanxipang cao 3143m", "Nóc nhà Đông Dương", false));
                quizViewModel.insertQuestion(new Question(5, "Thủ đô của Australia?", "Melbourne", "Sydney", "Canberra", "Brisbane", 3, 10, "Canberra là thủ đô chính trị", "Không phải Sydney", false));
                quizViewModel.insertQuestion(new Question(5, "Thác nước cao nhất thế giới là?", "Niagara", "Iguazu", "Victoria", "Angel", 4, 20, "Angel cao 979m ở Venezuela", "Nam Mỹ", true));
                quizViewModel.insertQuestion(new Question(5, "Biển Đông tiếp giáp bao nhiêu quốc gia?", "4", "5", "6", "7", 4, 20, "7 nước bao gồm Việt Nam, TQ, Philippines...", "Biển tranh chấp", true));
                quizViewModel.insertQuestion(new Question(5, "Nước nào có nhiều múi giờ nhất?", "Mỹ", "Trung Quốc", "Pháp", "Nga", 3, 10, "Pháp có nhiều lãnh thổ hải ngoại", "Trên toàn cầu", false));
                quizViewModel.insertQuestion(new Question(5, "Kênh đào nối Địa Trung Hải và Hồng Hải?", "Panama", "Suez", "Corinth", "Bosporus", 2, 10, "Suez ở Ai Cập", "Hành lang thương mại", false));
                quizViewModel.insertQuestion(new Question(5, "Châu lục duy nhất không có người sinh sống thường xuyên?", "Châu Úc", "Châu Nam Cực", "Châu Phi", "Châu Âu", 2, 10, "Nam Cực có trạm nghiên cứu, không dân cư thường trú", "Lạnh giá", false));
                quizViewModel.insertQuestion(new Question(5, "Hồ nước ngọt lớn nhất thế giới?", "Hồ Superior", "Hồ Victoria", "Hồ Baikal", "Hồ Michigan", 1, 10, "Superior ở Bắc Mỹ", "Ngũ Đại Hồ", false));
                quizViewModel.insertQuestion(new Question(5, "Châu Á tiếp giáp bao nhiêu đại dương?", "1", "2", "3", "4", 3, 10, "Thái Bình Dương, Ấn Độ Dương, Bắc Băng Dương", "Lục địa rộng lớn", false));
                quizViewModel.insertQuestion(new Question(5, "Sông Mê Kông bắt nguồn từ đâu?", "Thái Lan", "Lào", "Trung Quốc", "Campuchia", 3, 10, "Từ cao nguyên Tây Tạng, Trung Quốc", "Chảy qua nhiều nước Đông Nam Á", false));
                quizViewModel.insertQuestion(new Question(5, "Biên giới dài nhất thế giới là giữa hai nước nào?", "Mỹ - Canada", "Nga - Trung Quốc", "Brazil - Argentina", "Ấn Độ - Pakistan", 1, 10, "Dài hơn 8000km", "Châu Mỹ", false));
                quizViewModel.insertQuestion(new Question(5, "Kênh đào Panama nối hai đại dương nào?", "Thái Bình & Đại Tây", "Ấn Độ & Thái Bình", "Đại Tây & Bắc Băng", "Thái Bình & Nam Đại Tây", 1, 10, "Giữa Trung Mỹ", "Quan trọng thương mại", false));
                quizViewModel.insertQuestion(new Question(5, "Lãnh thổ lớn nhất châu Phi?", "Ai Cập", "Nam Phi", "Sudan", "Algeria", 4, 10, "Algeria có diện tích lớn nhất", "Bắc Phi", false));
                quizViewModel.insertQuestion(new Question(5, "Châu lục nào có ít quốc gia nhất?", "Châu Âu", "Châu Phi", "Châu Nam Cực", "Châu Úc", 3, 10, "Nam Cực không có quốc gia nào", "Chỉ có trạm nghiên cứu", false));
                quizViewModel.insertQuestion(new Question(5, "Thủ đô của Brazil là?", "Rio de Janeiro", "Sao Paulo", "Brasília", "Salvador", 3, 10, "Brasília được xây mới làm thủ đô", "Không phải Rio", false));
                quizViewModel.insertQuestion(new Question(5, "Vịnh lớn nhất thế giới?", "Vịnh Hạ Long", "Vịnh Bengal", "Vịnh Mexico", "Vịnh Ba Tư", 2, 10, "Bengal ở Nam Á", "Lớn nhất về diện tích", false));
                quizViewModel.insertQuestion(new Question(5, "Dãy núi Himalaya nằm ở đâu?", "Nam Mỹ", "Châu Âu", "Châu Á", "Châu Phi", 3, 10, "Trải dài qua Nepal, Bhutan, Ấn Độ", "Everest nằm đây", false));
                quizViewModel.insertQuestion(new Question(5, "Hồ Baikal nổi tiếng vì điều gì?", "Lớn nhất", "Nông nhất", "Sâu nhất", "Đẹp nhất", 3, 10, "Baikal sâu ~1642m", "Siberia - Nga", false));
                quizViewModel.insertQuestion(new Question(5, "Quốc gia duy nhất nằm trên cả hai châu lục Á và Âu?", "Thổ Nhĩ Kỳ", "Nga", "Kazakhstan", "Ukraine", 1, 10, "Istanbul nằm ở cả 2 châu lục", "Eo Bosporus", false));
                quizViewModel.insertQuestion(new Question(5, "Biển nào lạnh nhất?", "Biển Đen", "Biển Caspi", "Biển Chukchi", "Biển Đỏ", 3, 20, "Chukchi nằm gần Bắc Băng Dương", "Gần Bắc Cực", true));


                // === Thể thao (Category 6) ===
                quizViewModel.insertQuestion(new Question(6, "World Cup đầu tiên tổ chức năm?", "1930", "1934", "1942", "1950", 1, 10, "Năm 1930 tại Uruguay", "Giải bóng đá lớn nhất", false));
                quizViewModel.insertQuestion(new Question(6, "VĐV nào có nhiều HCV Olympic nhất?", "Bolt", "Phelps", "Nadal", "Messi", 2, 20, "Michael Phelps (bơi) có 23 HCV", "Bơi lội Mỹ", true));
                // ... thêm 48 câu (bóng đá, Olympic, bóng chuyền, cờ vua...)
                // === Thể thao (Category 6) - 50 câu hỏi ===
                quizViewModel.insertQuestion(new Question(6, "World Cup đầu tiên tổ chức năm?", "1930", "1934", "1942", "1950", 1, 10, "Năm 1930 tại Uruguay", "Giải bóng đá lớn nhất", false));
                quizViewModel.insertQuestion(new Question(6, "VĐV nào có nhiều HCV Olympic nhất?", "Bolt", "Phelps", "Nadal", "Messi", 2, 20, "Michael Phelps (bơi) có 23 HCV", "Bơi lội Mỹ", true));
                quizViewModel.insertQuestion(new Question(6, "Trong bóng đá, thẻ đỏ có nghĩa gì?", "Cảnh cáo", "Rời sân", "Penalty", "Offside", 2, 10, "Thẻ đỏ buộc cầu thủ rời sân", "Phạt nặng nhất", false));
                quizViewModel.insertQuestion(new Question(6, "Sân bóng đá chuẩn dài bao nhiêu?", "90-120m", "100-110m", "80-100m", "110-130m", 2, 15, "FIFA quy định 100-110m", "Chiều dài sân", true));
                quizViewModel.insertQuestion(new Question(6, "Quả bóng tennis được làm từ chất liệu gì?", "Cao su và len", "Da và len", "Nhựa và vải", "Cao su và vải", 1, 10, "Lõi cao su bọc len", "Màu vàng đặc trưng", false));

                quizViewModel.insertQuestion(new Question(6, "Olympic mùa hè đầu tiên tổ chức ở đâu?", "Paris", "Athens", "London", "Rome", 2, 15, "Athens, Hy Lạp năm 1896", "Cái nôi thể thao", true));
                quizViewModel.insertQuestion(new Question(6, "Bóng chuyền có mấy người trong đội hình?", "5", "6", "7", "8", 2, 10, "Mỗi đội 6 người trên sân", "Thể thao tập thể", false));
                quizViewModel.insertQuestion(new Question(6, "Usain Bolt giữ kỷ lục chạy 100m?", "9.58s", "9.68s", "9.78s", "9.88s", 1, 20, "9.58 giây tại Berlin 2009", "Người nhanh nhất hành tinh", true));
                quizViewModel.insertQuestion(new Question(6, "Wimbledon là giải gì?", "Bóng đá", "Tennis", "Golf", "Cricket", 2, 10, "Giải tennis danh giá nhất", "Sân cỏ truyền thống", false));
                quizViewModel.insertQuestion(new Question(6, "Đội tuyển Brazil vô địch World Cup mấy lần?", "4", "5", "6", "3", 2, 15, "5 lần vô địch (1958, 1962, 1970, 1994, 2002)", "Nhiều nhất thế giới", true));

                quizViewModel.insertQuestion(new Question(6, "Trong bóng rổ NBA, một trận có mấy hiệp?", "2", "3", "4", "5", 3, 10, "4 hiệp, mỗi hiệp 12 phút", "Khác với FIBA", false));
                quizViewModel.insertQuestion(new Question(6, "Công thức 1 có mấy đội đua?", "10", "12", "15", "20", 1, 15, "10 đội, mỗi đội 2 tay đua", "20 tay đua tổng cộng", true));
                quizViewModel.insertQuestion(new Question(6, "Môn golf có mấy lỗ chuẩn?", "9", "18", "27", "36", 2, 10, "Sân golf chuẩn có 18 lỗ", "Par từ 3-5", false));
                quizViewModel.insertQuestion(new Question(6, "Pele được mệnh danh là gì?", "Vua bóng đá", "Hoàng tử", "Thần đồng", "Huyền thoại", 1, 10, "O Rei - Vua bóng đá", "Brazil vĩ đại", false));
                quizViewModel.insertQuestion(new Question(6, "Trong boxing, có mấy hạng cân chính?", "10", "17", "20", "25", 2, 20, "17 hạng cân chuyên nghiệp", "Từ minimumweight đến heavyweight", true));

                quizViewModel.insertQuestion(new Question(6, "SEA Games tổ chức mấy năm 1 lần?", "1", "2", "3", "4", 2, 10, "2 năm một lần", "Thể thao Đông Nam Á", false));
                quizViewModel.insertQuestion(new Question(6, "Cầu lông ra đời ở quốc gia nào?", "Trung Quốc", "Ấn Độ", "Anh", "Malaysia", 3, 15, "Phát triển từ trò battledore ở Anh", "Thế kỷ 19", true));
                quizViewModel.insertQuestion(new Question(6, "Đội bóng nào được gọi là 'Quỷ đỏ'?", "Liverpool", "Arsenal", "Man United", "Chelsea", 3, 10, "Manchester United", "Old Trafford", false));
                quizViewModel.insertQuestion(new Question(6, "Lionel Messi sinh năm nào?", "1985", "1987", "1989", "1991", 2, 15, "24/6/1987 tại Argentina", "La Pulga", true));
                quizViewModel.insertQuestion(new Question(6, "Trận chung kết World Cup 2018?", "Pháp vs Croatia", "Pháp vs Bỉ", "Croatia vs Anh", "Đức vs Argentina", 1, 10, "Pháp thắng Croatia 4-2", "Tại Nga", false));

                quizViewModel.insertQuestion(new Question(6, "Cristiano Ronaldo đến từ quốc gia nào?", "Brazil", "Argentina", "Bồ Đào Nha", "Tây Ban Nha", 3, 10, "Madeira, Bồ Đào Nha", "CR7", false));
                quizViewModel.insertQuestion(new Question(6, "Kỷ lục thế giới marathon nam?", "2:01:09", "2:02:15", "2:03:30", "2:05:00", 1, 25, "Eliud Kipchoge - 2:01:09", "Berlin 2022", true));
                quizViewModel.insertQuestion(new Question(6, "Trong bóng đá Mỹ, touchdown được mấy điểm?", "3", "6", "7", "10", 2, 15, "6 điểm cho touchdown", "Có thể cộng thêm 1-2", true));
                quizViewModel.insertQuestion(new Question(6, "Thể thao nào được gọi là 'môn thể thao của các vị vua'?", "Golf", "Tennis", "Đua ngựa", "Polo", 3, 15, "Đua ngựa - Sport of Kings", "Aristocratic sport", true));
                quizViewModel.insertQuestion(new Question(6, "Kích thước sân bóng rổ NBA?", "28x15m", "26x14m", "30x16m", "25x13m", 1, 20, "28.7 x 15.2 mét", "94 x 50 feet", true));

                quizViewModel.insertQuestion(new Question(6, "Đội tuyển Việt Nam từng vào tứ kết Asian Cup năm nào?", "2007", "2019", "2023", "Chưa từng", 2, 15, "Tứ kết Asian Cup 2019", "Thành tích lịch sử", true));
                quizViewModel.insertQuestion(new Question(6, "Môn thể thao nào có thuật ngữ 'ace'?", "Tennis", "Golf", "Bowling", "Cả ba đều có", 4, 20, "Tennis (giao bóng), Golf (hole-in-one), Bowling (strike)", "Thuật ngữ chung", true));
                quizViewModel.insertQuestion(new Question(6, "Đội bóng nào có biệt danh 'Los Blancos'?", "Barcelona", "Real Madrid", "Atletico", "Valencia", 2, 10, "Real Madrid - áo trắng", "Santiago Bernabeu", false));
                quizViewModel.insertQuestion(new Question(6, "MMA là viết tắt của gì?", "Mixed Martial Arts", "Modern Martial Arts", "Multiple Martial Arts", "Maximum Martial Arts", 1, 15, "Võ thuật tổng hợp", "UFC nổi tiếng", true));
                quizViewModel.insertQuestion(new Question(6, "Trong cờ vua, quân nào mạnh nhất?", "Hậu", "Xe", "Tượng", "Mã", 1, 10, "Quân hậu di chuyển tự do", "9 điểm giá trị", false));

                quizViewModel.insertQuestion(new Question(6, "Giải Premier League có mấy đội?", "18", "20", "22", "24", 2, 10, "20 đội bóng Anh", "Mùa giải 38 vòng", false));
                quizViewModel.insertQuestion(new Question(6, "Kỷ lục nhảy cao nam thế giới?", "2.40m", "2.45m", "2.50m", "2.55m", 2, 25, "Javier Sotomayor - 2.45m", "Cuba 1993", true));
                quizViewModel.insertQuestion(new Question(6, "Federer, Nadal, Djokovic gọi là?", "Big Three", "Golden Trio", "Tennis Kings", "Grand Masters", 1, 15, "Big Three của tennis", "Thống trị 20 năm", true));
                quizViewModel.insertQuestion(new Question(6, "Đội tuyển nữ Việt Nam vô địch SEA Games bao nhiêu lần?", "0", "1", "2", "3", 3, 20, "2 lần vô địch bóng đá nữ", "Thành tích xuất sắc", true));
                quizViewModel.insertQuestion(new Question(6, "Michael Jordan mặc áo số mấy?", "23", "24", "32", "33", 1, 10, "Số 23 huyền thoại", "Chicago Bulls", false));

                quizViewModel.insertQuestion(new Question(6, "Paralympic dành cho ai?", "Trẻ em", "Người khuyết tật", "Người cao tuổi", "Nghiệp dư", 2, 10, "VĐV khuyết tật", "Song song Olympic", false));
                quizViewModel.insertQuestion(new Question(6, "Công Phượng được mệnh danh là gì?", "Messi Việt Nam", "Ronaldo Việt Nam", "Hoàng tử", "Siêu sao", 1, 10, "Messi Việt Nam", "Kỹ thuật tinh tế", false));
                quizViewModel.insertQuestion(new Question(6, "Trong bơi lội, kiểu bơi nào nhanh nhất?", "Tự do", "Ngửa", "Ếch", "Bướm", 1, 10, "Bơi tự do (sải)", "Freestyle", false));
                quizViewModel.insertQuestion(new Question(6, "Giải Champions League có mấy đội tham dự?", "16", "24", "32", "48", 3, 15, "32 đội từ 2024", "Cúp C1 châu Âu", true));
                quizViewModel.insertQuestion(new Question(6, "Đội tuyển nào vô địch EURO 2021?", "Ý", "Anh", "Pháp", "Đức", 1, 15, "Ý thắng Anh trên penalty", "Wembley 2021", true));

                quizViewModel.insertQuestion(new Question(6, "Kobe Bryant mặc áo số nào cuối sự nghiệp?", "8", "24", "23", "33", 2, 15, "Số 24 giai đoạn sau", "Black Mamba", true));
                quizViewModel.insertQuestion(new Question(6, "Trong F1, pole position là gì?", "Vị trí xuất phát đầu tiên", "Vị trí cuối", "Vị trí giữa", "Vị trí ngẫu nhiên", 1, 15, "Xuất phát đầu tiên sau qualifying", "Lợi thế lớn", true));
                quizViewModel.insertQuestion(new Question(6, "Đội nào vô địch World Cup 2022?", "Pháp", "Argentina", "Croatia", "Brazil", 2, 10, "Argentina vô địch tại Qatar", "Messi cuối cùng đã có", false));
                quizViewModel.insertQuestion(new Question(6, "Serena Williams có mấy Grand Slam đơn?", "21", "23", "25", "27", 2, 20, "23 Grand Slam đơn nữ", "Kỷ lục thời Open Era", true));
                quizViewModel.insertQuestion(new Question(6, "Đội bóng nào có tên sân 'Camp Nou'?", "Real Madrid", "Barcelona", "Atletico", "Valencia", 2, 10, "FC Barcelona", "99,000 chỗ ngồi", false));

                quizViewModel.insertQuestion(new Question(6, "Neymar chuyển PSG với giá bao nhiêu?", "200 triệu euro", "222 triệu euro", "250 triệu euro", "300 triệu euro", 2, 20, "222 triệu euro năm 2017", "Kỷ lục chuyển nhượng", true));
                quizViewModel.insertQuestion(new Question(6, "Quần vợt có mấy set tối đa nam?", "3", "4", "5", "6", 3, 15, "Tối đa 5 set (3/5)", "Grand Slam nam", true));
                quizViewModel.insertQuestion(new Question(6, "Tiger Woods chơi môn gì?", "Tennis", "Golf", "Baseball", "Hockey", 2, 10, "Huyền thoại golf", "15 Major championships", false));
                quizViewModel.insertQuestion(new Question(6, "Đội nào được gọi là 'Blaugrana'?", "Real Madrid", "Barcelona", "Bayern Munich", "Chelsea", 2, 15, "Barcelona - xanh đỏ", "Màu áo truyền thống", true));
                quizViewModel.insertQuestion(new Question(6, "V-League Việt Nam có mấy đội?", "12", "14", "16", "18", 2, 10, "14 đội bóng", "Giải vô địch quốc gia", false));

                // === Công nghệ (Category 7) ===
                quizViewModel.insertQuestion(new Question(7, "HTML là viết tắt của?", "HyperText Markup Language", "HighText Machine Language", "HyperTool Markup Language", "HomeText Machine Language", 1, 10, "HTML là ngôn ngữ đánh dấu", "Dùng để tạo trang web", false));
                quizViewModel.insertQuestion(new Question(7, "Ngôn ngữ lập trình nào được dùng nhiều nhất 2023?", "Python", "Java", "C++", "Ruby", 1, 20, "Python phổ biến vì dễ học và linh hoạt", "Số liệu GitHub", true));
                // ... thêm 48 câu (lập trình, phần cứng, mạng, AI...)
                quizViewModel.insertQuestion(new Question(7, "CPU là viết tắt của?", "Central Process Unit", "Central Processing Unit", "Computer Personal Unit", "Control Processor Unit", 2, 10, "CPU = Central Processing Unit", "Bộ xử lý trung tâm", false));
                quizViewModel.insertQuestion(new Question(7, "Hệ điều hành mã nguồn mở phổ biến nhất?", "Windows", "Linux", "macOS", "Unix", 2, 10, "Linux được dùng nhiều trên server", "Mã nguồn mở", false));
                quizViewModel.insertQuestion(new Question(7, "RAM viết tắt của?", "Read And Modify", "Random Access Memory", "Run Active Memory", "Readable Active Memory", 2, 10, "RAM = Random Access Memory", "Bộ nhớ tạm thời", false));
                quizViewModel.insertQuestion(new Question(7, "AI viết tắt của?", "Advanced Internet", "Artificial Intelligence", "Auto Integration", "Array Input", 2, 10, "AI = Trí tuệ nhân tạo", "Dùng trong ChatGPT", false));
                quizViewModel.insertQuestion(new Question(7, "AI được huấn luyện bằng kỹ thuật nào?", "Data mining", "Hard coding", "Machine Learning", "Manual labeling", 3, 20, "AI học từ dữ liệu qua ML", "Học máy", true));
                quizViewModel.insertQuestion(new Question(7, "Phần mềm trình duyệt nào sau đây?", "Word", "Chrome", "Excel", "Photoshop", 2, 5, "Chrome là trình duyệt web", "Google phát triển", false));
                quizViewModel.insertQuestion(new Question(7, "Java được phát triển bởi công ty nào?", "Google", "Sun Microsystems", "Microsoft", "Apple", 2, 10, "Ban đầu bởi Sun, sau thuộc Oracle", "Công ty phần mềm", false));
                quizViewModel.insertQuestion(new Question(7, "C++ kế thừa từ ngôn ngữ nào?", "Java", "C", "Python", "Pascal", 2, 10, "C++ mở rộng từ C", "Ngôn ngữ cổ điển", false));
                quizViewModel.insertQuestion(new Question(7, "Hệ điều hành nào được dùng phổ biến trong smartphone?", "Linux", "Windows", "Android", "macOS", 3, 10, "Android dùng phổ biến trên điện thoại", "Do Google phát triển", false));
                quizViewModel.insertQuestion(new Question(7, "Một byte gồm bao nhiêu bit?", "4", "8", "16", "32", 2, 5, "1 byte = 8 bit", "Đơn vị đo dữ liệu", false));
                quizViewModel.insertQuestion(new Question(7, "DNS có vai trò gì trong mạng?", "Bảo mật", "Tăng tốc", "Gán IP", "Phân giải tên miền", 4, 15, "DNS dịch tên miền thành IP", "www.google.com → IP", true));
                quizViewModel.insertQuestion(new Question(7, "Ngôn ngữ chủ yếu dùng cho AI hiện nay?", "Python", "Java", "C#", "PHP", 1, 10, "Python có thư viện AI mạnh như TensorFlow", "Dễ học và phổ biến", false));
                quizViewModel.insertQuestion(new Question(7, "GPU thường được dùng cho?", "Lưu trữ", "Mạng", "Xử lý đồ họa", "Quản lý RAM", 3, 10, "GPU xử lý hình ảnh", "Card đồ họa", false));
                quizViewModel.insertQuestion(new Question(7, "Github dùng để?", "Soạn văn bản", "Chạy phần mềm", "Quản lý mã nguồn", "Tạo video", 3, 10, "GitHub là nền tảng Git hosting", "Lập trình viên hay dùng", false));
                quizViewModel.insertQuestion(new Question(7, "HTML dùng để?", "Tạo ảnh", "Viết code game", "Định dạng trang web", "Tạo ứng dụng di động", 3, 10, "HTML là ngôn ngữ đánh dấu web", "HyperText", false));
                quizViewModel.insertQuestion(new Question(7, "Thiết bị nào lưu trữ lâu dài nhất?", "RAM", "Cache", "HDD", "Register", 3, 10, "HDD dùng để lưu dữ liệu lâu dài", "Ổ đĩa cứng", false));
                quizViewModel.insertQuestion(new Question(7, "Phần mềm nào là IDE?", "Photoshop", "Chrome", "Visual Studio", "Notepad", 3, 10, "Visual Studio là môi trường lập trình", "Integrated Development Environment", false));
                quizViewModel.insertQuestion(new Question(7, "HTTP là gì?", "Giao thức truyền tệp", "Giao thức web", "Ngôn ngữ lập trình", "Bảo mật mạng", 2, 10, "HTTP = HyperText Transfer Protocol", "Truy cập trang web", false));
                quizViewModel.insertQuestion(new Question(7, "Ngôn ngữ nào được dùng trong Android?", "Swift", "Kotlin", "C#", "Ruby", 2, 15, "Kotlin thay thế Java trên Android", "Do JetBrains phát triển", true));
                quizViewModel.insertQuestion(new Question(7, "Thiết bị nào là input?", "Máy in", "Máy quét", "Màn hình", "Loa", 2, 5, "Scanner nhập dữ liệu vào máy tính", "Input = đầu vào", false));
                quizViewModel.insertQuestion(new Question(7, "IPv6 có bao nhiêu bit?", "32", "64", "128", "256", 3, 20, "IPv6 là địa chỉ IP mới dài 128 bit", "Thay IPv4", true));
                quizViewModel.insertQuestion(new Question(7, "Phần mềm nén phổ biến là?", "ZIP", "Word", "Excel", "Paint", 1, 5, "ZIP nén tập tin", "Giảm dung lượng", false));
                quizViewModel.insertQuestion(new Question(7, "Hệ quản trị cơ sở dữ liệu phổ biến?", "HTML", "MySQL", "Python", "Ubuntu", 2, 10, "MySQL quản lý dữ liệu dạng bảng", "Dùng trong website", false));
                quizViewModel.insertQuestion(new Question(7, "Tên gọi của tệp Java biên dịch?", "file.java", "file.obj", "file.class", "file.txt", 3, 10, "Java biên dịch thành .class", "Bytecode", false));
                quizViewModel.insertQuestion(new Question(7, "SSD nhanh hơn HDD do?", "Có đầu đọc nhanh", "Không dùng cơ học", "Chạy bằng nước", "Tự động sửa lỗi", 2, 15, "SSD dùng bộ nhớ flash", "Không có đĩa quay", true));
                quizViewModel.insertQuestion(new Question(7, "Hệ điều hành đầu tiên của Microsoft?", "Windows XP", "MS-DOS", "Windows 95", "Windows 3.1", 2, 15, "MS-DOS là hệ điều hành dòng lệnh", "Trước Windows", true));
                quizViewModel.insertQuestion(new Question(7, "IoT là gì?", "Internet of Tools", "Interface of Technology", "Internet of Things", "Intelligence of Tech", 3, 10, "IoT = thiết bị kết nối internet", "Ví dụ: nhà thông minh", false));
                quizViewModel.insertQuestion(new Question(7, "Phần mở rộng của file thực thi trên Windows?", ".exe", ".txt", ".html", ".doc", 1, 5, ".exe là file chạy", "Executable", false));
                quizViewModel.insertQuestion(new Question(7, "Tường lửa dùng để?", "Ngăn virus", "Tăng tốc độ mạng", "Lọc truy cập", "Phát WiFi", 3, 10, "Firewall kiểm soát lưu lượng", "Bảo mật mạng", false));
                quizViewModel.insertQuestion(new Question(7, "Trí tuệ nhân tạo mạnh nhất hiện nay?", "ChatGPT", "Bing", "Siri", "Google", 1, 15, "ChatGPT dựa trên GPT-4", "Mô hình AI tiên tiến", true));
                quizViewModel.insertQuestion(new Question(7, "USB viết tắt của?", "Universal Serial Bus", "Ultra Safe Buffer", "Unique Service Base", "User Secure Byte", 1, 10, "USB = chuẩn kết nối phổ biến", "Cổng cắm dữ liệu", false));
                quizViewModel.insertQuestion(new Question(7, "Phím tắt sao chép (copy)?", "Ctrl + A", "Ctrl + C", "Ctrl + V", "Ctrl + X", 2, 5, "Ctrl + C = copy", "Thao tác văn bản", false));
                quizViewModel.insertQuestion(new Question(7, "Framework frontend phổ biến?", "Laravel", "React", "Spring", "Node", 2, 10, "React là thư viện UI của Facebook", "Dùng với JavaScript", false));
                quizViewModel.insertQuestion(new Question(7, "Đơn vị đo tốc độ mạng?", "Hz", "Byte", "Mbps", "Watt", 3, 10, "Mbps = megabit per second", "Tốc độ truyền dữ liệu", false));
                quizViewModel.insertQuestion(new Question(7, "Linux phát hành đầu tiên bởi?", "Microsoft", "Torvalds", "Gates", "Jobs", 2, 15, "Linus Torvalds tạo ra Linux", "Mã nguồn mở", true));
                quizViewModel.insertQuestion(new Question(7, "Chip M1 do ai sản xuất?", "Intel", "AMD", "Apple", "Samsung", 3, 15, "Apple thiết kế chip M1", "ARM-based", true));
                quizViewModel.insertQuestion(new Question(7, "Giao thức bảo mật HTTPS dùng gì?", "AES", "SSL/TLS", "SSH", "DES", 2, 15, "HTTPS = HTTP + SSL/TLS", "Bảo mật web", true));
                quizViewModel.insertQuestion(new Question(7, "Blockchain là gì?", "Cơ sở dữ liệu tập trung", "Công nghệ tiền ảo", "Chuỗi khối lưu dữ liệu phân tán", "Dịch vụ web", 3, 20, "Blockchain lưu dữ liệu minh bạch", "Dùng trong Bitcoin", true));
                quizViewModel.insertQuestion(new Question(7, "Laptop dùng pin loại nào?", "Pin nước", "Lithium-ion", "Ni-Cd", "Acid", 2, 10, "Lithium-ion nhẹ và bền", "Sạc được nhiều lần", false));


                // === Âm nhạc (Category 8) ===
                quizViewModel.insertQuestion(new Question(8, "Nốt nhạc cao nhất là?", "Sol", "Fa", "La", "Si", 4, 10, "Si là nốt cao nhất trong 7 nốt cơ bản", "Do Re Mi Fa Sol La Si", false));
                quizViewModel.insertQuestion(new Question(8, "Beethoven thuộc thời kỳ nào?", "Hiện đại", "Cổ điển", "Lãng mạn", "Baroque", 2, 20, "Ông là nhạc sĩ cổ điển nổi tiếng", "Soạn giao hưởng", true));
                // ... thêm 48 câu (nhạc lý, nhạc sĩ Việt Nam, quốc tế, nhạc cụ...)
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ nào sử dụng phím đen và trắng?", "Đàn tranh", "Đàn piano", "Đàn bầu", "Sáo trúc", 2, 10, "Piano có 88 phím đen trắng", "Nhạc cụ phương Tây phổ biến", false));
                quizViewModel.insertQuestion(new Question(8, "Ai là ca sĩ Việt Nam nổi tiếng với hit 'Hồng nhan'?", "Sơn Tùng M-TP", "Đen Vâu", "Jack", "Erik", 3, 10, "Jack nổi lên với 'Hồng nhan'", "Ca khúc phát hành năm 2019", false));
                quizViewModel.insertQuestion(new Question(8, "Nốt nhạc đầu tiên trong thang âm là?", "Re", "Mi", "Do", "La", 3, 10, "Thang âm: Do-Re-Mi-Fa...", "Nốt mở đầu", false));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nào được mệnh danh là 'ông hoàng nhạc pop'?", "Elvis Presley", "Michael Jackson", "Freddie Mercury", "Prince", 2, 20, "Michael Jackson nổi danh với Thriller", "Vũ điệu moonwalk", true));
                quizViewModel.insertQuestion(new Question(8, "Bản giao hưởng số 9 là của nhạc sĩ nào?", "Mozart", "Beethoven", "Bach", "Tchaikovsky", 2, 20, "Symphony No.9 là tác phẩm kinh điển", "Chứa Ode to Joy", true));
                quizViewModel.insertQuestion(new Question(8, "Bản nhạc quốc ca Việt Nam tên là?", "Tổ quốc", "Tiến bước", "Tiến quân ca", "Hồn tử sĩ", 3, 10, "Tiến quân ca do Văn Cao sáng tác", "Quốc ca hiện tại", false));
                quizViewModel.insertQuestion(new Question(8, "Loại nhạc nào có nguồn gốc từ Mỹ gốc Phi?", "Jazz", "Pop", "Rock", "EDM", 1, 20, "Jazz xuất hiện đầu thế kỷ 20", "Louis Armstrong", true));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nào có biệt danh 'diva nhạc nhẹ Việt Nam'?", "Thanh Lam", "Hồng Nhung", "Mỹ Linh", "Trần Thu Hà", 3, 10, "Mỹ Linh nổi bật với kỹ thuật thanh nhạc tốt", "Chồng là Anh Quân", false));
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ dân tộc nào chỉ có một dây?", "Đàn tranh", "Đàn bầu", "Đàn nguyệt", "Đàn nhị", 2, 10, "Đàn bầu dùng kỹ thuật điều âm", "Dùng que và tay", false));
                quizViewModel.insertQuestion(new Question(8, "Ban nhạc huyền thoại The Beatles đến từ nước nào?", "Pháp", "Mỹ", "Anh", "Canada", 3, 10, "The Beatles từ Liverpool, Anh", "John Lennon, Paul McCartney", false));
                quizViewModel.insertQuestion(new Question(8, "Nhạc sĩ Trịnh Công Sơn nổi tiếng với dòng nhạc nào?", "Cách mạng", "Trữ tình", "Nhạc đỏ", "Thiếu nhi", 2, 10, "Trịnh nổi bật với nhạc tình sâu sắc", "Ca từ triết lý", false));
                quizViewModel.insertQuestion(new Question(8, "Tần số chuẩn của nốt La trung là?", "220Hz", "440Hz", "880Hz", "660Hz", 2, 20, "La = 440Hz là tiêu chuẩn quốc tế", "Âm chuẩn để lên dây nhạc cụ", true));
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ nào không thuộc bộ dây?", "Violin", "Guitar", "Piano", "Flute", 4, 10, "Flute là nhạc cụ hơi", "Không có dây rung", false));
                quizViewModel.insertQuestion(new Question(8, "Chất liệu chính làm nên sáo trúc là?", "Thép", "Gỗ", "Nhôm", "Tre", 4, 10, "Sáo trúc được làm từ cây tre", "Dùng trong dân ca", false));
                quizViewModel.insertQuestion(new Question(8, "Nhịp 3/4 thường gặp ở loại nhạc nào?", "March", "Waltz", "Rock", "Rumba", 2, 20, "Waltz sử dụng nhịp 3/4 uyển chuyển", "Xuất xứ từ Áo", true));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nào nổi tiếng với ca khúc 'Rolling in the Deep'?", "Rihanna", "Adele", "Taylor Swift", "Lana Del Rey", 2, 10, "Adele là diva nước Anh", "Giọng hát trầm ấm", false));
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ nào là đặc trưng của miền Trung Việt Nam?", "Đàn nguyệt", "Đàn tranh", "Đàn đá", "Đàn nhị", 2, 10, "Đàn tranh thường xuất hiện trong ca Huế", "Dây kim loại, gảy", false));
                quizViewModel.insertQuestion(new Question(8, "Tác giả bài 'Nối vòng tay lớn' là ai?", "Phạm Duy", "Trịnh Công Sơn", "Nguyễn Văn Tý", "Văn Cao", 2, 10, "Trịnh Công Sơn viết ca khúc này năm 1968", "Bài hát đoàn kết", false));
                quizViewModel.insertQuestion(new Question(8, "Ban nhạc rock Việt nào nổi bật với hit 'Tìm lại'?", "Microwave", "MTV", "Bức Tường", "Unlimited", 1, 10, "Microwave có phong cách rock hiện đại", "Xuất hiện sau 2000", false));
                quizViewModel.insertQuestion(new Question(8, "Rap Việt mùa 1 phát sóng năm nào?", "2018", "2019", "2020", "2021", 3, 10, "Rap Việt mùa đầu tiên là 2020", "Có Rhymastic, Karik", false));
                quizViewModel.insertQuestion(new Question(8, "Ai là người sáng tác 'Làng tôi'?", "Đặng Thế Phong", "Văn Cao", "Hoàng Quý", "Nguyễn Xuân Khoát", 2, 10, "Văn Cao sáng tác nhiều ca khúc cách mạng", "Cùng tác giả Quốc ca", false));
                quizViewModel.insertQuestion(new Question(8, "Bản 'Canon in D' nổi tiếng là của ai?", "Beethoven", "Pachelbel", "Mozart", "Bach", 2, 10, "Canon in D của Pachelbel", "Được chơi nhiều ở đám cưới", false));
                quizViewModel.insertQuestion(new Question(8, "Loại nhạc nào chủ yếu sử dụng âm điện tử?", "Classical", "Jazz", "EDM", "Folk", 3, 10, "EDM dùng synthesizer, loops", "Thịnh hành trong giới trẻ", false));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nào có hit 'Em gái mưa'?", "Hòa Minzy", "Bích Phương", "Hương Tràm", "Min", 3, 10, "Hương Tràm là quán quân The Voice", "Ca khúc ra năm 2017", false));
                quizViewModel.insertQuestion(new Question(8, "Hòa âm là gì?", "Điều chỉnh cao độ", "Ghép nhiều âm thanh", "Chơi nốt đơn", "Tăng nhịp", 2, 20, "Hòa âm là phối hợp âm thanh thành hợp âm", "Gắn với phối khí", true));
                quizViewModel.insertQuestion(new Question(8, "Thuật ngữ 'acapella' nghĩa là?", "Không có lời", "Chỉ dùng trống", "Hát không nhạc đệm", "Nhạc dân gian", 3, 20, "Acapella là hát chay không nhạc", "Thường trong nhóm vocal", true));
                quizViewModel.insertQuestion(new Question(8, "Ca khúc 'Happy Birthday' có mấy câu?", "2", "4", "6", "8", 2, 10, "Bài hát có 4 câu giống nhau", "Hát vào sinh nhật", false));
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ nào thuộc bộ gõ?", "Violin", "Guitar", "Trống", "Piano", 3, 10, "Trống là bộ gõ cơ bản", "Tạo tiết tấu", false));
                quizViewModel.insertQuestion(new Question(8, "Tempo là gì?", "Cường độ", "Tần số", "Tốc độ nhạc", "Nốt ngân", 3, 20, "Tempo quy định nhịp độ nhanh hay chậm", "BPM - beat per minute", true));
                quizViewModel.insertQuestion(new Question(8, "Ca khúc 'We Are The Champions' của ban nhạc nào?", "Queen", "U2", "Nirvana", "Linkin Park", 1, 10, "Queen do Freddie Mercury dẫn đầu", "Còn có 'Bohemian Rhapsody'", false));
                quizViewModel.insertQuestion(new Question(8, "Guitar có bao nhiêu dây?", "5", "6", "7", "4", 2, 10, "Guitar chuẩn có 6 dây", "EADGBE", false));
                quizViewModel.insertQuestion(new Question(8, "Ai sáng tác 'Gặp mẹ trong mơ'?", "Nguyễn Văn Chung", "Nguyễn Hải Phong", "Trần Tiến", "Chưa rõ", 4, 20, "Là nhạc nước ngoài lời Việt", "Không rõ tác giả chính", true));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nào là giám khảo The Voice Việt 2015?", "Hồ Ngọc Hà", "Mỹ Tâm", "Tuấn Hưng", "Đàm Vĩnh Hưng", 3, 10, "Tuấn Hưng từng ngồi ghế nóng", "Nam ca sĩ có nhiều fan", false));
                quizViewModel.insertQuestion(new Question(8, "Khái niệm 'nốt tròn' tương đương bao nhiêu nốt móc đơn?", "2", "4", "8", "16", 4, 20, "1 nốt tròn = 4 đen = 8 móc đơn = 16 móc kép", "Gấp đôi theo hệ nhịp", true));
                quizViewModel.insertQuestion(new Question(8, "Nhạc cụ nào chơi bằng vĩ?", "Piano", "Violin", "Guitar", "Trống", 2, 10, "Violin dùng vĩ kéo để tạo âm", "Nhạc cụ dây kéo", false));
                quizViewModel.insertQuestion(new Question(8, "Từ nào mô tả âm thanh nhỏ dần?", "Forte", "Piano", "Crescendo", "Decrescendo", 4, 20, "Decrescendo nghĩa là giảm âm lượng", "Dấu < hoặc chữ decresc.", true));
                quizViewModel.insertQuestion(new Question(8, "Tác phẩm nổi tiếng của Mozart là?", "Für Elise", "The Magic Flute", "Ode to Joy", "Bolero", 2, 20, "The Magic Flute là vở opera lớn", "Tên gốc: Die Zauberflöte", true));
                quizViewModel.insertQuestion(new Question(8, "Nhạc sĩ nào gắn liền với 'Người mẹ của tôi'?", "Xuân Giao", "Nguyễn Đức Toàn", "Phạm Tuyên", "Trịnh Công Sơn", 1, 10, "Xuân Giao là tác giả", "Ca khúc cảm động về mẹ", false));
                quizViewModel.insertQuestion(new Question(8, "Nhạc sĩ người Áo nổi tiếng thời kỳ cổ điển?", "Beethoven", "Mozart", "Bach", "Handel", 2, 10, "Mozart sinh tại Salzburg, Áo", "Thần đồng âm nhạc", false));
                quizViewModel.insertQuestion(new Question(8, "Giai điệu nào mở đầu bài 'Quốc ca Việt Nam'?", "Fa-Sol-La", "Mi-Sol-Do", "Do-Mi-Sol", "Sol-La-Do", 3, 20, "Mở đầu là Do-Mi-Sol", "Ngắn gọn, hào hùng", true));
                quizViewModel.insertQuestion(new Question(8, "Ca sĩ nổi tiếng với 'Bảy sắc cầu vồng'?", "Mỹ Linh", "Đông Nhi", "Ngô Kiến Huy", "Bảo Thy", 2, 10, "Đông Nhi thời teen pop", "2009", false));
                quizViewModel.insertQuestion(new Question(8, "Dấu hóa 'bémol' ký hiệu là gì?", "♯", "♭", "♮", "×", 2, 20, "♭ hạ nửa cung", "Ngược với ♯", true));
                quizViewModel.insertQuestion(new Question(8, "Ca khúc 'See You Again' gắn với bộ phim nào?", "Fast & Furious 7", "Avengers", "Titanic", "Inception", 1, 10, "Bài hát tưởng nhớ Paul Walker", "Wiz Khalifa ft. Charlie Puth", false));
                quizViewModel.insertQuestion(new Question(8, "Bản nhạc nào thường được chơi dịp Giáng sinh?", "Silent Night", "Summer Time", "Happy New Year", "Wedding March", 1, 10, "Silent Night là thánh ca nổi tiếng", "Gốc tiếng Đức", false));
                quizViewModel.insertQuestion(new Question(8, "Số dây đàn nhị Việt Nam là?", "1", "2", "3", "4", 2, 10, "Đàn nhị có 2 dây", "Nhạc cụ dân gian", false));
                quizViewModel.insertQuestion(new Question(8, "Bản giao hưởng nổi tiếng của Tchaikovsky?", "Symphony No.5", "Canon in D", "Symphony No.9", "Bolero", 1, 20, "Tác phẩm thể hiện đấu tranh nội tâm", "Nga thế kỷ 19", true));
                quizViewModel.insertQuestion(new Question(8, "Dòng nhạc EDM viết tắt là?", "Electronic Dance Music", "Extreme DJ Mix", "Electric Drum Melody", "Electronic Disco Mood", 1, 10, "EDM là dòng nhạc điện tử", "Thịnh hành tại festival", false));


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


//    private void populateDatabase() {
//        Category math = new Category("Toán học", "Kiểm tra kỹ năng toán học của bạn");
//        math.setTotalQuestions(10);
//        math.setMaxScore(100);
//        quizViewModel.insertCategory(math);
//
//        Category science = new Category("Khoa học", "Câu hỏi khoa học tổng quát");
//        science.setTotalQuestions(10);
//        science.setMaxScore(100);
//        quizViewModel.insertCategory(science);
//
//        Category history = new Category("Lịch sử", "Kiến thức lịch sử thế giới");
//        history.setTotalQuestions(10);
//        history.setMaxScore(100);
//        quizViewModel.insertCategory(history);
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//
//                // ==== Toán học ====
//                quizViewModel.insertQuestion(new Question(1, "12 x 8 bằng bao nhiêu?", "96", "84", "102", "88", 1, 10, "12 x 8 = 96", "Phép nhân cơ bản", false));
//                quizViewModel.insertQuestion(new Question(1, "Căn bậc hai của 144 là?", "10", "11", "12", "13", 3, 10, "√144 = 12", "Tìm số nhân chính nó bằng 144", false));
//                quizViewModel.insertQuestion(new Question(1, "(15 + 5) : 4 bằng?", "4", "5", "6", "8", 2, 10, "20 : 4 = 5", "Cộng trước, chia sau", false));
//                quizViewModel.insertQuestion(new Question(1, "Số nào là số nguyên tố?", "9", "15", "13", "21", 3, 10, "13 chỉ chia hết cho 1 và chính nó", "Số nguyên tố cơ bản", false));
//                quizViewModel.insertQuestion(new Question(1, "30% của 200 là?", "60", "50", "40", "70", 1, 10, "30% tức là 30/100 * 200 = 60", "Đổi % thành phân số", false));
//                quizViewModel.insertQuestion(new Question(1, "Đạo hàm của x² là?", "x", "2x", "x²", "2", 2, 20, "Đạo hàm của x² là 2x", "Quy tắc đạo hàm", true));
//                quizViewModel.insertQuestion(new Question(1, "Giá trị của số Pi (2 chữ số thập phân)?", "3.12", "3.14", "3.16", "3.18", 2, 20, "Pi ≈ 3.14", "Số vô tỉ bắt đầu bằng 3.14", true));
//                quizViewModel.insertQuestion(new Question(1, "Tích phân của 1/x là?", "ln|x| + C", "x", "1/x + C", "x² + C", 1, 20, "∫(1/x)dx = ln|x| + C", "Liên quan đến log", true));
//                quizViewModel.insertQuestion(new Question(1, "Số nguyên tố sau 47 là?", "49", "51", "53", "55", 3, 20, "Số nguyên tố tiếp theo là 53", "Kiểm tra chia hết", true));
//                quizViewModel.insertQuestion(new Question(1, "Giải phương trình: 2x + 3 = 11", "4", "5", "6", "7", 1, 20, "2x = 8 → x = 4", "Chuyển vế, chia hai vế", true));
//
//                // ==== Khoa học ====
//                quizViewModel.insertQuestion(new Question(2, "Hành tinh xanh là hành tinh nào?", "Sao Hỏa", "Sao Kim", "Trái Đất", "Sao Mộc", 3, 10, "Trái Đất do có nước và khí quyển", "Hành tinh của con người", false));
//                quizViewModel.insertQuestion(new Question(2, "Nhiệt độ sôi của nước là?", "90", "95", "100", "105", 3, 10, "100°C ở điều kiện bình thường", "Điều kiện tiêu chuẩn", false));
//                quizViewModel.insertQuestion(new Question(2, "Khí nào cây hấp thụ để quang hợp?", "Oxy", "CO2", "Nitơ", "Hydro", 2, 10, "Cây hấp thụ CO2", "Khí con người thải ra", false));
//                quizViewModel.insertQuestion(new Question(2, "Bộ phận nào bơm máu?", "Phổi", "Thận", "Tim", "Gan", 3, 10, "Tim bơm máu trong hệ tuần hoàn", "Cơ quan trung tâm", false));
//                quizViewModel.insertQuestion(new Question(2, "Bộ phận nào quang hợp?", "Rễ", "Thân", "Hoa", "Lá", 4, 10, "Lá có diệp lục hấp thụ ánh sáng", "Bề mặt màu xanh", false));
//                quizViewModel.insertQuestion(new Question(2, "Hạt nào không mang điện?", "Proton", "Neutron", "Electron", "Positron", 2, 20, "Neutron không mang điện", "Tên 'trung hòa'", true));
//                quizViewModel.insertQuestion(new Question(2, "Ký hiệu hoá học của vàng là?", "Go", "G", "Au", "Ag", 3, 20, "Au từ 'Aurum' (Latin)", "Xuất phát từ tiếng Latin", true));
//                quizViewModel.insertQuestion(new Question(2, "Định luật 3 Newton nói gì?", "F = ma", "Phản lực", "Quán tính", "Chậm phản ứng", 2, 20, "Mỗi lực có phản lực bằng và ngược chiều", "Hành động – phản ứng", true));
//                quizViewModel.insertQuestion(new Question(2, "Người có bao nhiêu nhiễm sắc thể?", "23", "46", "48", "22", 2, 20, "23 cặp = 46 chiếc", "Số cặp là 23", true));
//                quizViewModel.insertQuestion(new Question(2, "Chất tự nhiên nào cứng nhất?", "Sắt", "Kim cương", "Đá", "Thạch anh", 2, 20, "Kim cương cứng nhất", "Dùng trong đồ trang sức", true));
//
//                // ==== Lịch sử ====
//                quizViewModel.insertQuestion(new Question(3, "Ai là tổng thống đầu tiên của Mỹ?", "Lincoln", "Washington", "Jefferson", "Adams", 2, 10, "George Washington là người đầu tiên", "Tổng thống khai quốc", false));
//                quizViewModel.insertQuestion(new Question(3, "Chiến tranh nào kết thúc năm 1945?", "Thế giới 1", "Chiến tranh lạnh", "Thế giới 2", "Việt Nam", 3, 10, "WW2 kết thúc năm 1945", "Cuộc chiến với phát xít", false));
//                quizViewModel.insertQuestion(new Question(3, "Bức tường nào sụp đổ năm 1989?", "Vạn Lý", "Berlin", "London", "Paris", 2, 10, "Bức tường Berlin chấm dứt chia cắt Đức", "Chia Đông – Tây", false));
//                quizViewModel.insertQuestion(new Question(3, "Ai phát hiện châu Mỹ?", "Columbus", "Marco Polo", "Magellan", "Napoleon", 1, 10, "Columbus đến châu Mỹ năm 1492", "Người Ý dưới cờ Tây Ban Nha", false));
//                quizViewModel.insertQuestion(new Question(3, "Đế chế nào xây Colosseum?", "Hy Lạp", "La Mã", "Ba Tư", "Mông Cổ", 2, 10, "La Mã cổ đại xây Colosseum", "Ở Ý", false));
//                quizViewModel.insertQuestion(new Question(3, "Thủ tướng Anh thời WW2 là?", "Churchill", "Thatcher", "Blair", "Attlee", 1, 20, "Winston Churchill thời chiến", "Diễn giả nổi tiếng", true));
//                quizViewModel.insertQuestion(new Question(3, "Cách mạng Pháp bắt đầu năm nào?", "1789", "1776", "1804", "1815", 1, 20, "1789 là năm pháo đài Bastille bị tấn công", "Biểu tượng cách mạng", true));
//                quizViewModel.insertQuestion(new Question(3, "Nền văn minh nào phát minh bánh xe?", "Hy Lạp", "Lưỡng Hà", "Ai Cập", "Maya", 2, 20, "Lưỡng Hà cổ đại phát minh bánh xe", "Giữa sông Tigris và Euphrates", true));
//                quizViewModel.insertQuestion(new Question(3, "Ai bãi bỏ chế độ nô lệ Mỹ?", "Washington", "Lincoln", "Roosevelt", "Jefferson", 2, 20, "Abraham Lincoln ký giải phóng nô lệ", "Nội chiến Mỹ", true));
//                quizViewModel.insertQuestion(new Question(3, "Chiến tranh lạnh giữa ai?", "Mỹ – Nhật", "Đức – Anh", "Mỹ – Liên Xô", "Trung – Anh", 3, 20, "Chiến tranh lạnh không giao tranh trực tiếp", "Sau WW2", true));
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

//    private void loadInitialQuestions() {
//        AppDatabase.databaseWriteExecutor.execute(() -> {
//            QuestionDao questionDao = database.questionDao();
//
//            // Chỉ thêm nếu chưa có câu hỏi
//            if (questionDao.getAllQuestions().isEmpty()) {
//                List<Question> questions = new ArrayList<>();
//
//        // ======= Toán học (id = 1) =======
//        questions.add(new Question(1, "12 x 8 bằng bao nhiêu?", "96", "84", "102", "88", 1, 10,
//                "12 x 8 = 96 là phép nhân cơ bản.", "Sử dụng bảng cửu chương.", false));
//        questions.add(new Question(1, "Căn bậc hai của 144 là bao nhiêu?", "10", "11", "12", "13", 3, 10,
//                "12 x 12 = 144, nên căn bậc hai là 12.", "Số nào nhân chính nó bằng 144?", false));
//        questions.add(new Question(1, "(15 + 5) : 4 bằng bao nhiêu?", "4", "5", "6", "8", 2, 10,
//                "(15 + 5) = 20, rồi chia 4 = 5", "Cộng trước chia sau.", false));
//        questions.add(new Question(1, "Số nào là số nguyên tố?", "9", "15", "13", "21", 3, 10,
//                "13 chỉ chia hết cho 1 và chính nó.", "Chỉ chia hết cho 1 và chính nó.", false));
//        questions.add(new Question(1, "30% của 200 là bao nhiêu?", "60", "50", "40", "70", 1, 10,
//                "30% tức là 30/100 * 200 = 60", "Đổi phần trăm thành phân số rồi nhân.", false));
//
//        questions.add(new Question(1, "Đạo hàm của x² là gì?", "x", "2x", "x²", "2", 2, 20,
//                "Đạo hàm của x² theo quy tắc là 2x.", "Quy tắc đạo hàm cơ bản.", true));
//        questions.add(new Question(1, "Giá trị của số Pi đến hai chữ số thập phân là gì?", "3.12", "3.14", "3.16", "3.18", 2, 20,
//                "Pi ≈ 3.14 là giá trị xấp xỉ thông dụng.", "Nó bắt đầu bằng 3.141...", true));
//        questions.add(new Question(1, "Tích phân của 1/x là gì?", "ln|x| + C", "x", "1/x + C", "x² + C", 1, 20,
//                "∫(1/x)dx = ln|x| + C là kiến thức cơ bản giải tích.", "Liên quan đến log tự nhiên.", true));
//        questions.add(new Question(1, "Số nguyên tố tiếp theo sau 47 là gì?", "49", "51", "53", "55", 3, 20,
//                "53 là số nguyên tố tiếp theo sau 47.", "Kiểm tra chia hết cho 3, 5, 7.", true));
//        questions.add(new Question(1, "Giải phương trình: 2x + 3 = 11", "4", "5", "6", "7", 1, 20,
//                "2x = 8 → x = 4", "Chuyển vế và chia hai vế.", true));
//
//
//        // ======= Khoa học (id = 2) =======
//        questions.add(new Question(2, "Hành tinh nào được gọi là hành tinh xanh?", "Sao Hỏa", "Sao Kim", "Trái Đất", "Sao Mộc", 3, 10,
//                "Trái Đất có màu xanh do có nhiều nước.", "Là hành tinh chúng ta đang sống.", false));
//        questions.add(new Question(2, "Nhiệt độ sôi của nước (°C) là bao nhiêu?", "90", "95", "100", "105", 3, 10,
//                "Nước sôi ở 100°C ở điều kiện bình thường.", "Điều kiện tiêu chuẩn.", false));
//        questions.add(new Question(2, "Cây hấp thụ khí nào để quang hợp?", "Oxy", "Carbon Dioxide", "Nitơ", "Hydro", 2, 10,
//                "Cây hấp thụ CO2 để tạo ra oxy qua quang hợp.", "Khí mà con người thải ra.", false));
//        questions.add(new Question(2, "Bộ phận nào của cơ thể bơm máu?", "Phổi", "Thận", "Tim", "Gan", 3, 10,
//                "Tim bơm máu đi khắp cơ thể.", "Là trung tâm của hệ tuần hoàn.", false));
//        questions.add(new Question(2, "Bộ phận nào của cây thực hiện quang hợp?", "Rễ", "Thân", "Hoa", "Lá", 4, 10,
//                "Lá chứa diệp lục giúp hấp thu ánh sáng.", "Bề mặt phẳng và xanh.", false));
//
//        questions.add(new Question(2, "Hạt hạ nguyên tử nào không mang điện?", "Proton", "Neutron", "Electron", "Positron", 2, 20,
//                "Neutron là hạt không mang điện.", "Tên của nó cũng có nghĩa 'trung hoà'.", true));
//        questions.add(new Question(2, "Ký hiệu hoá học của vàng là gì?", "Go", "G", "Au", "Ag", 3, 20,
//                "Au là viết tắt từ tiếng Latin 'Aurum'.", "Xuất phát từ tên Latin.", true));
//        questions.add(new Question(2, "Định luật 3 Newton nói về điều gì?", "F = ma", "Phản lực", "Quán tính", "Chậm phản ứng", 2, 20,
//                "Mỗi lực tác động đều có phản lực bằng và ngược chiều.", "Hành động - phản ứng.", true));
//        questions.add(new Question(2, "Tế bào người có bao nhiêu nhiễm sắc thể?", "23", "46", "48", "22", 2, 20,
//                "Con người có 23 cặp = 46 nhiễm sắc thể.", "Số cặp là 23, tổng là?", true));
//        questions.add(new Question(2, "Chất tự nhiên nào cứng nhất?", "Sắt", "Kim cương", "Đá granite", "Thạch anh", 2, 20,
//                "Kim cương đứng đầu thang độ cứng Mohs.", "Dùng làm trang sức.", true));
//
//
//        // ======= Lịch sử (id = 3) =======
//        questions.add(new Question(3, "Ai là tổng thống đầu tiên của Hoa Kỳ?", "Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams", 2, 10,
//                "George Washington là tổng thống đầu tiên của Hoa Kỳ.", "Người khai quốc.", false));
//        questions.add(new Question(3, "Chiến tranh nào kết thúc năm 1945?", "Chiến tranh thế giới 1", "Chiến tranh lạnh", "Chiến tranh thế giới 2", "Chiến tranh Việt Nam", 3, 10,
//                "Chiến tranh thế giới thứ 2 kết thúc năm 1945.", "Cuộc chiến với Đức quốc xã.", false));
//        questions.add(new Question(3, "Bức tường nào sụp đổ năm 1989?", "Vạn Lý Trường Thành", "Bức tường Berlin", "Tường London", "Tường Paris", 2, 10,
//                "Bức tường Berlin sụp đổ chấm dứt chia cắt Đông - Tây Đức.", "Chia đôi nước Đức.", false));
//        questions.add(new Question(3, "Ai phát hiện ra châu Mỹ?", "Christopher Columbus", "Marco Polo", "Magellan", "Napoleon", 1, 10,
//                "Columbus đến châu Mỹ năm 1492.", "Người Ý đi thuyền dưới cờ Tây Ban Nha.", false));
//        questions.add(new Question(3, "Đế chế nào xây dựng Đấu trường La Mã (Colosseum)?", "Hy Lạp", "La Mã", "Ba Tư", "Mông Cổ", 2, 10,
//                "Colosseum là công trình của Đế chế La Mã cổ đại.", "Nằm ở Ý.", false));
//
//        questions.add(new Question(3, "Thủ tướng Anh trong Thế chiến 2 là ai?", "Churchill", "Thatcher", "Blair", "Attlee", 1, 20,
//                "Winston Churchill lãnh đạo Anh trong Thế chiến 2.", "Diễn giả nổi tiếng thời chiến.", true));
//        questions.add(new Question(3, "Cách mạng Pháp bắt đầu năm nào?", "1789", "1776", "1804", "1815", 1, 20,
//                "1789 là năm tấn công pháo đài Bastille.", "Gắn liền với sự kiện Bastille.", true));
//        questions.add(new Question(3, "Nền văn minh nào phát minh ra bánh xe?", "Hy Lạp", "Lưỡng Hà", "Ai Cập", "Maya", 2, 20,
//                "Lưỡng Hà cổ đại là nơi đầu tiên dùng bánh xe.", "Nằm giữa sông Tigris và Euphrates.", true));
//        questions.add(new Question(3, "Tổng thống nào bãi bỏ chế độ nô lệ tại Mỹ?", "Washington", "Lincoln", "Roosevelt", "Jefferson", 2, 20,
//                "Abraham Lincoln ký Tuyên bố Giải phóng Nô lệ.", "Giai đoạn nội chiến Hoa Kỳ.", true));
//        questions.add(new Question(3, "Chiến tranh lạnh chủ yếu giữa hai quốc gia nào?", "Mỹ và Nhật", "Đức và Anh", "Mỹ và Liên Xô", "Trung Quốc và Anh", 3, 20,
//                "Chiến tranh lạnh giữa Mỹ và Liên Xô, không giao tranh trực tiếp.", "Hai cường quốc sau Thế chiến 2.", true));
//
//        for (Question q : questions) {
//            questionDao.insert(q);
//        }
//    }
//
}
