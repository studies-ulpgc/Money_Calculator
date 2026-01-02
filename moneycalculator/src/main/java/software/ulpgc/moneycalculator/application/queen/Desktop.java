package software.ulpgc.moneycalculator.application.queen;

import software.ulpgc.moneycalculator.architecture.control.Command;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;
import software.ulpgc.moneycalculator.architecture.model.Money;
import software.ulpgc.moneycalculator.architecture.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.awt.event.KeyEvent.VK_ENTER;

public class Desktop extends JFrame implements ExchangeRateDisplay {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<Currency> currencies;

    private JTextField inputAmountField;
    private JTextField outputAmountField;
    private JComboBox<Currency> inputCurrencyCombo;
    private JComboBox<Currency> outputCurrencyCombo;
    private JLabel rateInfoLabel;
    private JLabel inverseRateInfoLabel;

    public Desktop(List<Currency> currencies) {
        this.currencies = currencies;
        setupFrame();

        this.add(getJPanel_principal(), BorderLayout.CENTER);
        this.add(getJPanel_boton_y_tasas(), BorderLayout.SOUTH);
    }

    private JPanel getJPanel_boton_y_tasas() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.add(createCalculateButton(), BorderLayout.NORTH);
        footer.add(createBottomInfoPanel(), BorderLayout.CENTER);
        return footer;
    }

    private JPanel getJPanel_principal() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 10));
        mainPanel.setBorder(new EmptyBorder(20, 30, 10, 30));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createCurrencySelectorBlock(true));
        mainPanel.add(createCurrencySelectorBlock(false));
        mainPanel.add(createAmountInputBlock());
        mainPanel.add(createAmountOutputBlock());
        return mainPanel;
    }

    private void setupFrame() {
        this.setTitle("Money Calculator Pro");
        this.setSize(800, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private Component createCalculateButton() {
        JButton btn = new JButton("EXCHANGE");
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.addActionListener(e -> executeExchange());

        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.add(btn);
        return p;
    }

    private JPanel createCurrencySelectorBlock(boolean isInput) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(isInput ? "FROM:" : "TO:"));

        JComboBox<Currency> combo = new JComboBox<>(new DefaultComboBoxModel<>(currencies.toArray(new Currency[0])));
        combo.setEditable(true);
        combo.setRenderer(new CurrencyListRenderer());

        buscador((JTextField) combo.getEditor().getEditorComponent(), combo);

        if (isInput) inputCurrencyCombo = combo;
        else outputCurrencyCombo = combo;

        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private void buscador(JTextField editor, JComboBox<Currency> combo) {
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) return;

                String text = editor.getText().toUpperCase();
                List<Currency> filtered = currencies.stream()
                        .filter(c -> c.code().toUpperCase().contains(text) || c.country().toUpperCase().contains(text))
                        .collect(Collectors.toList());
                actualizar_modelo(filtered, text);
            }

            private void actualizar_modelo(List<Currency> filtered, String text) {
                DefaultComboBoxModel<Currency> model = new DefaultComboBoxModel<>(filtered.toArray(new Currency[0]));
                combo.setModel(model);
                editor.setText(text);
                combo.setPopupVisible(!filtered.isEmpty());
            }
        });
    }

    private JPanel createAmountInputBlock() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        inputAmountField = new JTextField("1");
        inputAmountField.setFont(new Font("SansSerif", Font.PLAIN, 50));
        inputAmountField.setHorizontalAlignment(JTextField.CENTER);

        inputAmountField.addActionListener(e -> executeExchange());

        panel.add(inputAmountField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAmountOutputBlock() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        outputAmountField = new JTextField("0.00");
        outputAmountField.setEditable(false);
        outputAmountField.setFont(new Font("SansSerif", Font.PLAIN, 45));
        outputAmountField.setHorizontalAlignment(JTextField.CENTER);
        outputAmountField.setOpaque(false);
        panel.add(outputAmountField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 40, 30, 40));
        rateInfoLabel = new JLabel("Introduce un valor para calcular");
        inverseRateInfoLabel = new JLabel("");
        panel.add(rateInfoLabel);
        panel.add(inverseRateInfoLabel);
        return panel;
    }

    private void executeExchange() {
        if (commands.containsKey("exchange") && !inputAmountField.getText().isEmpty()) {
            try {
                commands.get("exchange").execute();
            } catch (Exception e) {
                System.err.println("Error en la conexión");
            }
        }
    }


    public MoneyDialog moneyDialog() {
        return () -> {
            String text = inputAmountField.getText().replace(",", ".");
            double amount = text.isEmpty() ? 0 : Double.parseDouble(text);
            return new Money(amount, (Currency) inputCurrencyCombo.getSelectedItem());
        };
    }

    public CurrencyDialog currencyDialog() {
        return () -> (Currency) outputCurrencyCombo.getSelectedItem();
    }

    public MoneyDisplay moneyDisplay() {
        return money -> SwingUtilities.invokeLater(() ->
                outputAmountField.setText(String.format("%.2f", money.amount())));
    }

    @Override
    public void show(ExchangeRate rate) {
        SwingUtilities.invokeLater(() -> {
            rateInfoLabel.setText("1 " + rate.from().code() + " = " + rate.rate() + " " + rate.to().code());
            inverseRateInfoLabel.setText("1 " + rate.to().code() + " = " + String.format("%.2f", 1/rate.rate()) + " " + rate.from().code());
        });
    }

    public void addCommand(String name, Command command) {
        this.commands.put(name, command);
    }

    private static class CurrencyListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Currency) {
                Currency c = (Currency) value;
                label.setText(c.code() + " " + c.country());
            }
            return label;
        }
    }

}