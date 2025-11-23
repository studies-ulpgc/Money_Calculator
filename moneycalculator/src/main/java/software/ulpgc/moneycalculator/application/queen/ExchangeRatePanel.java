package software.ulpgc.moneycalculator.application.queen;

import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ExchangeRatePanel extends JPanel {
    private double rate = 0;
    private String date = "";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setRate(ExchangeRate exchangeRate) {
        this.rate = exchangeRate.rate();
        this.date = exchangeRate.date().format(FORMATTER);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        graphics.setColor(Color.BLUE);
        int barHeight = (int) (rate * 50);
        graphics.fillRect(10, getHeight() - barHeight - 30, 50, barHeight);

        graphics.setColor(Color.BLACK);
        graphics.drawString("Exchange rate: " + rate, 10, 20);
        graphics.drawString("Date: " + date, 10, 40);
    }
}
