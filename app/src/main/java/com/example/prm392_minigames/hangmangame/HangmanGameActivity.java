        package com.example.prm392_minigames.hangmangame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private Button btnGuess, btnNewGame, btnScores, btnHint;
    private ImageButton btnAddWord;

    private List<GameWord> wordsList;
    private GameWord currentWord;
    private StringBuilder displayWord;
    private int score = 0;
    private int wrongGuesses = 0;
    private int maxWrongGuesses = 6;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private List<Character> revealedLetters; // Track revealed letters
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
        btnHint = findViewById(R.id.btnHint);
        btnAddWord = findViewById(R.id.btnAddWord);

        btnGuess.setOnClickListener(v -> makeGuess());
        btnNewGame.setOnClickListener(v -> startNewGame());
        btnScores.setOnClickListener(v -> showScores());
        btnHint.setOnClickListener(v -> showHint());
        btnAddWord.setOnClickListener(v -> openAddWordActivity());
    }

    private void setupGameWords() {
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
        if (currentQuestionIndex >= 10) { // Chỉ kiểm tra giới hạn 10 câu
            endGame(false); // Gọi endGame với trạng thái thắng
            return;
        }

        currentWord = wordsList.get(currentQuestionIndex);
        displayWord = new StringBuilder();
        revealedLetters = new ArrayList<>();
        wrongGuesses = 0;
        gameWon = false;

        for (int i = 0; i < currentWord.getWord().length(); i++) {
            displayWord.append("_ ");
        }

        updateDisplay();
        etGuess.setText("");
        etGuess.setEnabled(true);
        btnGuess.setEnabled(true);
        btnHint.setEnabled(score > 0);
    }

    private void makeGuess() {
        String guess = etGuess.getText().toString().trim().toUpperCase();

        if (guess.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập một từ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (guess.equals(currentWord.getWord())) {
            gameWon = true;
            correctAnswers++;
            score += 10;
            displayWord = new StringBuilder(currentWord.getWord().replaceAll(".", "$0 "));
            Toast.makeText(this, "Chính xác! +10 điểm", Toast.LENGTH_SHORT).show();

            tvWord.postDelayed(() -> {
                currentQuestionIndex++;
                nextQuestion();
            }, 2000);

            etGuess.setEnabled(false);
            btnGuess.setEnabled(false);
            btnHint.setEnabled(false);
        } else {
            wrongGuesses++;
            score = Math.max(0, score - 2);
            Toast.makeText(this, "Sai rồi! -2 điểm", Toast.LENGTH_SHORT).show();

            if (wrongGuesses >= maxWrongGuesses) {
                endGame(true); // Gọi endGame với trạng thái thua
                return;
            }
        }

        updateDisplay();
        etGuess.setText("");
    }

    private void showHint() {
        if (score <= 0) {
            Toast.makeText(this, "Bạn không đủ điểm để xem gợi ý!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (revealedLetters.size() >= currentWord.getWord().length()) {
            Toast.makeText(this, "Không còn chữ nào để gợi ý!", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        int index;
        char letter;

        do {
            index = random.nextInt(currentWord.getWord().length());
            letter = currentWord.getWord().charAt(index);
        } while (revealedLetters.contains(letter));

        revealedLetters.add(letter);
        for (int i = 0; i < currentWord.getWord().length(); i++) {
            if (currentWord.getWord().charAt(i) == letter) {
                displayWord.setCharAt(i * 2, letter);
            }
        }

        score = Math.max(0, score - 2);
        Toast.makeText(this, "Gợi ý: Chữ '" + letter + "' đã được mở! -2 điểm", Toast.LENGTH_SHORT).show();
        btnHint.setEnabled(false);

        if (!displayWord.toString().contains("_")) {
            gameWon = true;
            correctAnswers++;
            score += 10;
            Toast.makeText(this, "Từ đã hoàn thành! +10 điểm", Toast.LENGTH_SHORT).show();

            tvWord.postDelayed(() -> {
                currentQuestionIndex++;
                nextQuestion();
            }, 2000);

            etGuess.setEnabled(false);
            btnGuess.setEnabled(false);
            btnHint.setEnabled(false);
        }

        updateDisplay();
    }

    private void updateDisplay() {
        tvWord.setText(displayWord.toString());
        tvScore.setText("Điểm: " + score);
        tvWrongGuesses.setText("Sai: " + wrongGuesses + "/" + maxWrongGuesses);
        tvQuestion.setText("Gợi ý: " + currentWord.getHint());
        tvProgress.setText("Tiến độ: " + (currentQuestionIndex + 1) + "/10");

        btnHint.setEnabled(score > 0);
    }

    private void endGame(boolean isGameOver) {
        etGuess.setEnabled(false);
        btnGuess.setEnabled(false);
        btnHint.setEnabled(false);

        String playerName = "Player_" + System.currentTimeMillis();

        if (correctAnswers == 10 && !isGameOver) {
            score += 50;
            Toast.makeText(this, "Chúc mừng! Hoàn thành 10/10 câu! Bonus +50 điểm", Toast.LENGTH_LONG).show();
            tvWord.setText("CONGRATULATIONS");
        } else {
            Toast.makeText(this, "Trò chơi kết thúc! Điểm cuối: " + score +
                    "\nSố câu đúng: " + correctAnswers + "/10", Toast.LENGTH_LONG).show();
            tvWord.setText("GAME OVER");
        }

        dbHelper.addScore(playerName, score);

        tvProgress.setText("Hoàn thành: " + correctAnswers + "/10");
    }

    private void showScores() {
        Intent intent = new Intent(this, GameScoreActivity.class);
        startActivity(intent);
    }

    private void openAddWordActivity() {
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }
}
