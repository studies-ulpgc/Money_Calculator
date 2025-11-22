package software.ulpgc.moneycalculator.application.queen.view;

import software.ulpgc.moneycalculator.architecture.ui.ExchangeRateDateDisplay;

import javax.swing.*;
import java.awt.*;

public class ExchangeRateDateDisplayPanel extends JPanel implements ExchangeRateDateDisplay {

    private final JLabel output = new JLabel("Date: ");

    public ExchangeRateDateDisplayPanel() {
        setLayout(new BorderLayout());
        add(output, BorderLayout.CENTER);
    }

    @Override
    public void show(String date) {
        output.setText("Date: " + date);
    }
}
