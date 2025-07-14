package com.example.prm392_minigames.hangmangame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_minigames.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HangmanGameActivity extends AppCompatActivity {
    private GameDatabaseHelper dbHelper;
    private TextView tvWord, tvScore, tvWrongGuesses, tvQuestion, tvProgress;
    private EditText etGuess;
    private Button btnGuess, btnNewGame, btnScores;

    private List<GameWord> wordsList;
    private GameWord currentWord;
    private StringBuilder displayWord;
    private int score = 0;
    private int wrongGuesses = 0;
    private int maxWrongGuesses = 6;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private List<Character> guessedLetters;
    private boolean gameWon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman_game);

        dbHelper = new GameDatabaseHelper(this);
        initializeViews();
        setupGameWords();
        startNewGame();
    }

    private void initializeViews() {
        tvWord = findViewById(R.id.tvWord);
        tvScore = findViewById(R.id.tvScore);
        tvWrongGuesses = findViewById(R.id.tvWrongGuesses);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        etGuess = findViewById(R.id.etGuess);
        btnGuess = findViewById(R.id.btnGuess);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnScores = findViewById(R.id.btnScores);

        btnGuess.setOnClickListener(v -> makeGuess());
        btnNewGame.setOnClickListener(v -> startNewGame());
        btnScores.setOnClickListener(v -> showScores());
    }

    private void setupGameWords() {
        // Thêm từ mẫu vào database nếu chưa có
        if (!dbHelper.hasWords()) {
            String[] words = {
                    "CAT", "Một loài động vật nuôi phổ biến, thích săn chuột",
                    "DOG", "Loài vật trung thành, bạn của con người",
                    "ELEPHANT", "Động vật lớn nhất trên cạn, có vòi dài",
                    "TIGER", "Loài mèo lớn hung dữ, có sọc vằn",
                    "MONKEY", "Loài động vật leo trèo, thông minh",
                    "VIETNAM", "Quốc gia Đông Nam Á, thủ đô là Hà Nội",
                    "JAPAN", "Quốc gia đảo, nổi tiếng với văn hóa samurai",
                    "BRAZIL", "Quốc gia Nam Mỹ, nổi tiếng với bóng đá",
                    "FRANCE", "Quốc gia châu Âu, có tháp Eiffel",
                    "AUSTRALIA", "Quốc gia châu Đại Dương, có kangaroo",
                    "LION", "Vua của các loài thú, sống ở châu Phi",
                    "PANDA", "Loài gấu nổi tiếng với màu đen trắng",
                    "CHINA", "Quốc gia đông dân nhất thế giới",
                    "CANADA", "Quốc gia Bắc Mỹ, nổi tiếng với lá phong",
                    "RUSSIA", "Quốc gia lớn nhất thế giới về diện tích"
            };

            for (int i = 0; i < words.length; i += 2) {
                dbHelper.addWord(words[i], words[i + 1]);
            }
        }
    }

    private void startNewGame() {
        score = 0;
        currentQuestionIndex = 0;
        correctAnswers = 0;
        wordsList = dbHelper.getAllWords();

        if (wordsList.isEmpty()) {
            Toast.makeText(this, "Không có từ nào trong database!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trộn danh sách từ
        shuffleWords();
        nextQuestion();
    }

    private void shuffleWords() {
        Random random = new Random();
        for (int i = wordsList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            GameWord temp = wordsList.get(i);
            wordsList.set(i, wordsList.get(j));
            wordsList.set(j, temp);
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex >= wordsList.size() || currentQuestionIndex >= 10) {
            endGame();
            return;
        }

        currentWord = wordsList.get(currentQuestionIndex);
        displayWord = new StringBuilder();
        guessedLetters = new ArrayList<>();
        wrongGuesses = 0;
        gameWon = false;

        // Khởi tạo từ hiển thị với dấu gạch dưới
        for (int i = 0; i < currentWord.getWord().length(); i++) {
            displayWord.append("_ ");
        }

        updateDisplay();
        etGuess.setText("");
        etGuess.setEnabled(true);
        btnGuess.setEnabled(true);
    }

    private void makeGuess() {
        String guess = etGuess.getText().toString().trim().toUpperCase();

        if (guess.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập một chữ cái!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (guess.length() != 1) {
            Toast.makeText(this, "Chỉ nhập một chữ cái!", Toast.LENGTH_SHORT).show();
            return;
        }

        char letter = guess.charAt(0);

        if (guessedLetters.contains(letter)) {
            Toast.makeText(this, "Bạn đã đoán chữ này rồi!", Toast.LENGTH_SHORT).show();
            return;
        }

        guessedLetters.add(letter);

        if (currentWord.getWord().contains(guess)) {
            // Đúng
            for (int i = 0; i < currentWord.getWord().length(); i++) {
                if (currentWord.getWord().charAt(i) == letter) {
                    displayWord.setCharAt(i * 2, letter);
                }
            }

            // Kiểm tra xem đã hoàn thành từ chưa
            if (!displayWord.toString().contains("_")) {
                gameWon = true;
                correctAnswers++;
                score += 10; // Cộng 10 điểm cho mỗi từ đúng
                Toast.makeText(this, "Chính xác! +10 điểm", Toast.LENGTH_SHORT).show();

                // Chuyển sang câu tiếp theo sau 2 giây
                tvWord.postDelayed(() -> {
                    currentQuestionIndex++;
                    nextQuestion();
                }, 2000);

                etGuess.setEnabled(false);
                btnGuess.setEnabled(false);
            }
        } else {
            // Sai
            wrongGuesses++;
            score = Math.max(0, score - 2); // Trừ 2 điểm cho mỗi lần đoán sai
            Toast.makeText(this, "Sai rồi! -2 điểm", Toast.LENGTH_SHORT).show();

            if (wrongGuesses >= maxWrongGuesses) {
                Toast.makeText(this, "Bạn đã thua! Từ đúng là: " + currentWord.getWord(), Toast.LENGTH_LONG).show();

                // Chuyển sang câu tiếp theo sau 3 giây
                tvWord.postDelayed(() -> {
                    currentQuestionIndex++;
                    nextQuestion();
                }, 3000);

                etGuess.setEnabled(false);
                btnGuess.setEnabled(false);
            }
        }

        updateDisplay();
        etGuess.setText("");
    }

    private void updateDisplay() {
        tvWord.setText(displayWord.toString());
        tvScore.setText("Điểm: " + score);
        tvWrongGuesses.setText("Sai: " + wrongGuesses + "/" + maxWrongGuesses);
        tvQuestion.setText("Gợi ý: " + currentWord.getHint());
        tvProgress.setText("Tiến độ: " + (currentQuestionIndex + 1) + "/10");
    }

    private void endGame() {
        etGuess.setEnabled(false);
        btnGuess.setEnabled(false);

        String playerName = "Player_" + System.currentTimeMillis(); // Tên unique

        // Bonus điểm nếu trả lời đúng tất cả 10 câu
        if (correctAnswers == 10) {
            score += 50; // Bonus 50 điểm
            Toast.makeText(this, "Xuất sắc! Hoàn thành 10/10 câu! Bonus +50 điểm", Toast.LENGTH_LONG).show();
        }

        // Lưu điểm vào database
        dbHelper.addScore(playerName, score);

        Toast.makeText(this, "Trò chơi kết thúc! Điểm cuối: " + score +
                "\nSố câu đúng: " + correctAnswers + "/10", Toast.LENGTH_LONG).show();

        tvWord.setText("GAME OVER");
        tvProgress.setText("Hoàn thành: " + correctAnswers + "/10");
    }

    private void showScores() {
        Intent intent = new Intent(this, GameScoreActivity.class);
        startActivity(intent);
    }
}

// Class riêng để lưu trữ từ và gợi ý
class GameWord {
    private String word;
    private String hint;

    public GameWord(String word, String hint) {
        this.word = word;
        this.hint = hint;
    }

    public String getWord() {
        return word;
    }

    public String getHint() {
        return hint;
    }
}