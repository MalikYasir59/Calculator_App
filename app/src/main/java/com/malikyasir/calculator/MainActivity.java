package com.malikyasir.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultTextView;
    private TextView calculationTextView;
    private String currentExpression = "";
    private String currentNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.result_view);
        calculationTextView = findViewById(R.id.calculation_view);

        int[] buttonIds = new int[]{
                R.id.button_ac, R.id.button29, R.id.button34,
                R.id.button45, R.id.button24, R.id.button30, R.id.button35,
                R.id.button46, R.id.button25, R.id.button31, R.id.button36,
                R.id.button47, R.id.button26, R.id.button32, R.id.button37,
                R.id.button48, R.id.button28, R.id.button33, R.id.button38
        };

        for (int id : buttonIds) {
            View button = findViewById(id);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button b;
        if (v instanceof Button) {
            b = (Button) v;
        } else if (v instanceof ImageButton) {
            b = new Button(this);
            b.setText("Delete");
        } else {
            return;
        }

        String buttonText = b.getText().toString();

        if (buttonText.matches("\\d")) { // If the button text is a number
            currentNumber += buttonText;
            currentExpression += buttonText;
            resultTextView.setText(currentNumber);
            calculationTextView.setText(currentExpression);
        } else if (buttonText.equals("AC")) {
            currentNumber = "";
            currentExpression = "";
            resultTextView.setText("0");
            calculationTextView.setText("");
        } else if (buttonText.equals("=")) {
            if (!currentExpression.isEmpty()) {
                try {
                    double evalResult = evaluateExpression(currentExpression);
                    currentNumber = formatResult(evalResult);
                    resultTextView.setText(currentNumber);
                } catch (Exception e) {
                    resultTextView.setText("Error");
                }
            }
        } else if (buttonText.equals("Delete")) {
            if (currentNumber.length() > 0) {
                currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                resultTextView.setText(currentNumber);
                calculationTextView.setText(currentExpression);
            }
        } else if (buttonText.equals("%")) {
            if (!currentNumber.isEmpty()) {
                double percentValue = Double.parseDouble(currentNumber) / 100;
                currentNumber = String.valueOf(percentValue);
                currentExpression += currentNumber;
                resultTextView.setText(currentNumber);
                calculationTextView.setText(currentExpression);
            }
        } else {
            if (!currentNumber.isEmpty()) {
                currentExpression += buttonText;
                currentNumber = "";
            } else {
                currentExpression += buttonText;
            }
            calculationTextView.setText(currentExpression);
        }
    }

    private String formatResult(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%s", value);
        }
    }

    private double evaluateExpression(String expression) throws Exception {
        return new ExpressionEvaluator().evaluate(expression);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Map the Enter key to the "=" button functionality
            Button equalsButton = findViewById(R.id.button38);
            equalsButton.performClick();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}

class ExpressionEvaluator {
    // This method evaluates a mathematical expression
    public double evaluate(String expression) throws Exception {
        return evaluatePostfix(convertToPostfix(expression));
    }

    private String convertToPostfix(String expression) {
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                postfix.append(c);
            } else if (c == '!') {
                postfix.append('!');
            } else if (isOperator(c)) {
                postfix.append(' ');
                while (!stack.isEmpty() && hasPrecedence(c, stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(' ');
            postfix.append(stack.pop());
        }

        return postfix.toString();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷' || c == '√' || c == '^' || c == '!' || c == '%';
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op2 == '(' || op2 == ')')) {
            return false;
        }
        if ((op1 == '×' || op1 == '÷' || op1 == '%') && (op2 == '+' || op2 == '-')) {
            return false;
        } else {
            return true;
        }
    }

    private double evaluatePostfix(String postfix) throws Exception {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (token.equals("!")) {
                double a = stack.pop();
                stack.push(factorial(a));
            } else if (isOperator(token.charAt(0))) {
                double b = stack.pop();
                double a = stack.isEmpty() ? 0 : stack.pop();
                stack.push(applyOperation(token.charAt(0), a, b));
            } else {
                stack.push(Double.parseDouble(token));
            }
        }

        return stack.pop();
    }

    private double factorial(double n) {
        if (n == 0) return 1;
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private double applyOperation(char op, double a, double b) throws Exception {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '×':
                return a * b;
            case '÷':
                return a / b;
            case '%':
                return a * (b / 100);
            default:
                throw new Exception("Invalid operator");
        }
    }
}

