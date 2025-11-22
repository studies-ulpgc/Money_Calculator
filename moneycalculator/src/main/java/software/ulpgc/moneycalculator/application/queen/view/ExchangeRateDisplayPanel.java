package software.ulpgc.moneycalculator.application.queen.view;

import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;
import software.ulpgc.moneycalculator.architecture.ui.ExchangeRateDisplay;

import javax.swing.*;
import java.awt.*;

public class ExchangeRateDisplayPanel extends JPanel implements ExchangeRateDisplay {

    private final JLabel output = new JLabel("Rate: ");

    public ExchangeRateDisplayPanel() {
        setLayout(new BorderLayout());
        add(output, BorderLayout.CENTER);
    }

    @Override
    public void show(ExchangeRate rate) {
        output.setText("Rate: " + rate.rate());
    }
}

