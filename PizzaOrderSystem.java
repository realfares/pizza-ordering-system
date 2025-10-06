package com.mycompany.pizzaordersystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.text.DecimalFormat;

public class PizzaOrderSystem extends JFrame {
    // Enhanced color scheme
    private static final Color PRIMARY_COLOR = new Color(231, 76, 60); // Red
    private static final Color SECONDARY_COLOR = new Color(241, 196, 15); // Yellow
    private static final Color ACCENT_COLOR = new Color(46, 204, 113); // Green
    private static final Color DARK_COLOR = new Color(44, 62, 80); // Dark blue
    private static final Color LIGHT_COLOR = new Color(236, 240, 241); // Light gray
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Dark gray
    private static final Color CARD_COLOR = new Color(255, 255, 255); // White
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.ITALIC, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font ITEM_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font PRICE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private JPanel cartPanel;
    private JPanel cartListContainer;
    private final Map<String, Integer> cartItems = new HashMap<>();
    private Map<String, ImageIcon> pizzaImages = new HashMap<>();
    private Map<String, Double> pizzaPrices = new HashMap<>();
    private Map<String, List<String>> pizzaCustomizations = new HashMap<>();
    private Map<String, Integer> pizzaRatings = new HashMap<>();
    private final String[][] menuItems = {
        {"MARGHERITA", "4.936", "Classic tomato, mozzarella, and basil", "margherita.jpg"},
        {"PEPPERONI", "5.696", "Spicy pepperoni with extra cheese", "pepperoni.jpg"},
        {"VEGGIE DELIGHT", "5.316", "Mixed veggies with goat cheese", "veggie.jpg"},
        {"TRUFFLE SPECIAL", "7.216", "White sauce with truffle oil", "truffle.jpg"},
        {"HAWAIIAN", "6.076", "Ham and pineapple combo", "hawaiian.jpg"},
        {"BBQ CHICKEN", "6.356", "BBQ sauce with grilled chicken", "bbq_chicken.jpg"},
        {"MEDITERRANEAN", "5.896", "Olives, feta, and sun-dried tomatoes", "mediterranean.jpg"},
        {"BUFFALO RANCH", "6.756", "Spicy buffalo sauce with ranch", "buffalo_ranch.jpg"}
    };

    private double total = 0.0;
    private JLabel totalLabel;
    private JLabel greetingLabel;
    private JPanel mainPanel;
    private Clip addToCartSound;
    private Clip checkoutSound;
    private Clip buttonClickSound;
    private DecimalFormat priceFormat = new DecimalFormat("0.000");
    private JTabbedPane tabbedPane;
    
    // New fields for enhanced features
    private JPanel dealsPanel;
    private JPanel favoritesPanel;
    private Timer confettiTimer;
    private boolean isDarkMode = false;
    private JLabel userGreetingLabel;
    private String currentUser = "Guest";
    private Map<String, String> userDetails = new HashMap<>();

    public PizzaOrderSystem() {
        pizzaImages = new HashMap<>();
        pizzaPrices = new HashMap<>();
        pizzaCustomizations = new HashMap<>();
        pizzaRatings = new HashMap<>();
        
        try {
            loadPizzaImages();
            loadSounds();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading resources: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        initializeUI();
    }

    private void initializeUI() {
        setTitle("PIZZA PARTY ORDER SYSTEM");
        setSize(1200, 850); // Increased window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container with improved layout
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color1 = isDarkMode ? new Color(60, 60, 60) : new Color(247, 247, 247);
                Color color2 = isDarkMode ? new Color(40, 40, 40) : new Color(230, 230, 230);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        container.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create tabbed pane for menu organization
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(isDarkMode ? DARK_COLOR : LIGHT_COLOR);
        tabbedPane.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);

        // Menu tab
        JPanel menuTab = createMenuTab();
        tabbedPane.addTab("Menu", createIcon("menu.png", 20, 20), menuTab);

        // Deals tab
        dealsPanel = createDealsPanel();
        tabbedPane.addTab("Deals", createIcon("discount.png", 20, 20), dealsPanel);

        // Favorites tab
        favoritesPanel = createFavoritesPanel();
        tabbedPane.addTab("Favorites", createIcon("star.png", 20, 20), favoritesPanel);

        container.add(tabbedPane, BorderLayout.CENTER);

        container.add(createTotalPanel(), BorderLayout.SOUTH);

        initializeCartPanel();
        container.add(cartPanel, BorderLayout.EAST);

        add(container);
    }
private JPanel createLogoPanel() {
    JPanel logoPanel = new JPanel();
    logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
    logoPanel.setOpaque(false);
    logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));

    try {
        // Load logo image (replace "logo.png" with your actual logo file)
        InputStream is = getClass().getResourceAsStream("/images/logo.png");
        if (is != null) {
            BufferedImage logoImage = ImageIO.read(is);
            ImageIcon logoIcon = new ImageIcon(logoImage.getScaledInstance(300, 100, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(logoLabel);
        } else {
            // Fallback if logo not found
            JLabel titleLabel = new JLabel("PIZZA PARTY");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            titleLabel.setForeground(PRIMARY_COLOR);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(titleLabel);
        }
    } catch (IOException e) {
        e.printStackTrace();
        // Fallback if logo loading fails
        JLabel titleLabel = new JLabel("PIZZA PARTY");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(titleLabel);
    }

    return logoPanel;
}
    private JPanel createMenuTab() {
        JPanel menuTab = new JPanel(new BorderLayout());
        menuTab.setOpaque(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        for (String[] item : menuItems) {
            pizzaPrices.put(item[0], Double.valueOf(item[1]));
            pizzaCustomizations.put(item[0], new ArrayList<>());
            pizzaRatings.put(item[0], 0);
        }

        // Create a panel for menu items with rounded corners
        JPanel menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setOpaque(false);
        menuItemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String[] item : menuItems) {
            menuItemsPanel.add(createMenuItemPanel(item[0], item[1], item[2], item[3]));
            menuItemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JScrollPane scrollPane = new JScrollPane(menuItemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        menuTab.add(scrollPane, BorderLayout.CENTER);

        return menuTab;
    }
    
    private JPanel createDealsPanel() {
    JPanel dealsPanel = new JPanel();
    dealsPanel.setLayout(new BoxLayout(dealsPanel, BoxLayout.Y_AXIS));
    dealsPanel.setOpaque(false);
    dealsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // Family Deal
    JPanel familyDeal = createDealPanel(
        "Family Feast", 
        "19.999", 
        "2 Large Pizzas + 2 Sides + 4 Drinks", 
        "Perfect for family gatherings", 
        "family_deal.jpg"
    );
    
    // Couple's Special
    JPanel coupleDeal = createDealPanel(
        "Couple's Special", 
        "12.499", 
        "1 Medium Pizza + 1 Side + 2 Drinks", 
        "Romantic dinner for two", 
        "couple_deal.jpg"
    );
    
    // Lunch Combo
    JPanel lunchDeal = createDealPanel(
        "Lunch Combo", 
        "8.750", 
        "1 Personal Pizza + 1 Drink", 
        "Quick and delicious lunch", 
        "lunch_deal.jpg"
    );

    dealsPanel.add(familyDeal);
    dealsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    dealsPanel.add(coupleDeal);
    dealsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    dealsPanel.add(lunchDeal);

    JScrollPane scrollPane = new JScrollPane(dealsPanel);
    scrollPane.setBorder(null);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(scrollPane, BorderLayout.CENTER);
    return wrapper;
}

private JPanel createFavoritesPanel() {
    JPanel favoritesPanel = new JPanel();
    favoritesPanel.setLayout(new BoxLayout(favoritesPanel, BoxLayout.Y_AXIS));
    favoritesPanel.setOpaque(false);
    favoritesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JLabel infoLabel = new JLabel("<html><div style='text-align:center; color:#666;'>Rate pizzas to add them to your favorites!</div></html>");
    infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    favoritesPanel.add(infoLabel);

    JScrollPane scrollPane = new JScrollPane(favoritesPanel);
    scrollPane.setBorder(null);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(scrollPane, BorderLayout.CENTER);
    return wrapper;
}

private JPanel createDealPanel(String title, String price, String items, String desc, String imageName) {
    JPanel dealPanel = new JPanel(new BorderLayout(15, 0));
    dealPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220, 100)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15))
    );
    dealPanel.setBackground(new Color(255, 255, 255, 200));
    dealPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

    // Add shadow effect
    dealPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(2, 2, 4, 4),
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220, 100)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        )
    ));

    // Deal image
    JLabel imageLabel = new JLabel();
    try {
        InputStream is = getClass().getResourceAsStream("/images/" + imageName);
        if (is != null) {
            BufferedImage img = ImageIO.read(is);
            ImageIcon icon = new ImageIcon(img.getScaledInstance(200, 120, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setIcon(createPlaceholderIcon(200, 120));
        }
    } catch (IOException e) {
        imageLabel.setIcon(createPlaceholderIcon(200, 120));
    }
    imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    dealPanel.add(imageLabel, BorderLayout.WEST);

    // Text content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setOpaque(false);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel priceLabel = new JLabel("OMR " + price);
    priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    priceLabel.setForeground(ACCENT_COLOR);
    priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel itemsLabel = new JLabel(items);
    itemsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    itemsLabel.setForeground(DARK_COLOR);
    itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel descLabel = new JLabel(desc);
    descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    descLabel.setForeground(new Color(100, 100, 100));
    descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    contentPanel.add(priceLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    contentPanel.add(itemsLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    contentPanel.add(descLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

    // Buttons panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
    buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.setOpaque(false);
    buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JButton addToCartBtn = createStyledButton("Add to Cart", ACCENT_COLOR, 100, 30);
    addToCartBtn.addActionListener(e -> {
        playButtonClick();
        addToCart(title, Double.parseDouble(price));
    });

    buttonPanel.add(addToCartBtn);
    contentPanel.add(buttonPanel);

    dealPanel.add(contentPanel, BorderLayout.CENTER);

    return dealPanel;
}

    private JPanel createMenuItemPanel(String name, String price, String desc, String imageName) {
        JPanel itemPanel = new JPanel(new BorderLayout(15, 0));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220, 100)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15))
        );
        itemPanel.setBackground(new Color(255, 255, 255, 200));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Add shadow effect
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220, 100)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        ));

        // Pizza image
        JLabel imageLabel = new JLabel();
        if (pizzaImages.containsKey(name)) {
            imageLabel.setIcon(pizzaImages.get(name));
        } else {
            imageLabel.setIcon(createPlaceholderIcon(150, 100));
        }
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        itemPanel.add(imageLabel, BorderLayout.WEST);

        // Text content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Create a panel for name and rating
        JPanel nameRatingPanel = new JPanel();
        nameRatingPanel.setLayout(new BoxLayout(nameRatingPanel, BoxLayout.X_AXIS));
        nameRatingPanel.setOpaque(false);
        nameRatingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(ITEM_FONT);
        nameLabel.setForeground(DARK_COLOR);

        // Add some space between name and rating
        nameRatingPanel.add(nameLabel);
        nameRatingPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Add rating stars
        JPanel ratingPanel = createRatingPanel(name);
        nameRatingPanel.add(ratingPanel);

        contentPanel.add(nameRatingPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Create a panel for price and buttons
        JPanel priceButtonPanel = new JPanel();
        priceButtonPanel.setLayout(new BoxLayout(priceButtonPanel, BoxLayout.X_AXIS));
        priceButtonPanel.setOpaque(false);
        priceButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("OMR " + price);
        priceLabel.setFont(PRICE_FONT);
        priceLabel.setForeground(PRIMARY_COLOR);
        priceButtonPanel.add(priceLabel);

        // Add space between price and buttons
        priceButtonPanel.add(Box.createHorizontalGlue());

        // Add buttons
        JButton addToCartBtn = createStyledButton("Add to Cart", ACCENT_COLOR, 110, 35);
        addToCartBtn.addActionListener(e -> {
            playButtonClick();
            addToCart(name, Double.parseDouble(price));
        });

        JButton customizeBtn = createStyledButton("Customize", SECONDARY_COLOR, 100, 35);
        customizeBtn.addActionListener(e -> {
            playButtonClick();
            showCustomizationDialog(name, () -> addToCart(name, pizzaPrices.get(name)));
        });

        priceButtonPanel.add(customizeBtn);
        priceButtonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        priceButtonPanel.add(addToCartBtn);

        contentPanel.add(priceButtonPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(DESC_FONT);
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(descLabel);

        itemPanel.add(contentPanel, BorderLayout.CENTER);

        return itemPanel;
    }

    private JPanel createRatingPanel(String pizzaName) {
        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        ratingPanel.setOpaque(false);
        
        int currentRating = pizzaRatings.getOrDefault(pizzaName, 0);
        
        for (int i = 1; i <= 5; i++) {
            JLabel star = new JLabel();
            star.setIcon(i <= currentRating ? 
                createIcon("star_filled.png", 16, 16) : 
                createIcon("star_empty.png", 16, 16));
            star.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            final int rating = i;
            star.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    pizzaRatings.put(pizzaName, rating);
                    updateFavoritesPanel();
                    // Recreate the rating panel to show updated stars
                    Container parent = ratingPanel.getParent();
                    parent.remove(ratingPanel);
                    parent.add(createRatingPanel(pizzaName), parent.getComponentCount() - 2);
                    parent.revalidate();
                    parent.repaint();
                }
            });
            
            ratingPanel.add(star);
        }
        
        return ratingPanel;
    }

    private void updateFavoritesPanel() {
        favoritesPanel.removeAll();
        
        // Add header
        JLabel header = new JLabel("Your Favorite Pizzas");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(DARK_COLOR);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        favoritesPanel.add(header);
        favoritesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add rated pizzas
        boolean hasFavorites = false;
        for (Map.Entry<String, Integer> entry : pizzaRatings.entrySet()) {
            if (entry.getValue() >= 4) { // Only show highly rated pizzas
                hasFavorites = true;
                String pizzaName = entry.getKey();
                double price = pizzaPrices.get(pizzaName);
                
                JPanel favPanel = new JPanel(new BorderLayout(10, 0));
                favPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                favPanel.setBackground(new Color(255, 255, 255, 150));
                favPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                
                // Pizza name and rating
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);
                
                JLabel nameLabel = new JLabel(pizzaName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                nameLabel.setForeground(DARK_COLOR);
                
                JPanel starsPanel = new JPanel();
                starsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
                starsPanel.setOpaque(false);
                for (int i = 0; i < entry.getValue(); i++) {
                    starsPanel.add(new JLabel(createIcon("star_filled.png", 12, 12)));
                }
                
                infoPanel.add(nameLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(starsPanel);
                
                // Price and add button
                JPanel actionPanel = new JPanel(new BorderLayout());
                actionPanel.setOpaque(false);
                
                JLabel priceLabel = new JLabel("OMR " + priceFormat.format(price));
                priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                priceLabel.setForeground(PRIMARY_COLOR);
                
                JButton addBtn = createStyledButton("Add", ACCENT_COLOR, 60, 25);
                addBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                addBtn.addActionListener(e -> {
                    playButtonClick();
                    addToCart(pizzaName, price);
                });
                
                actionPanel.add(priceLabel, BorderLayout.WEST);
                actionPanel.add(addBtn, BorderLayout.EAST);
                
                favPanel.add(infoPanel, BorderLayout.CENTER);
                favPanel.add(actionPanel, BorderLayout.EAST);
                
                favoritesPanel.add(favPanel);
                favoritesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        if (!hasFavorites) {
            JLabel infoLabel = new JLabel("<html><div style='text-align:center; color:#666;'>Rate pizzas with 4+ stars to add them here!</div></html>");
            infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            favoritesPanel.add(infoLabel);
        }
        
        favoritesPanel.revalidate();
        favoritesPanel.repaint();
    }

    private JButton createStyledButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint rounded background
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(width, height));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }

    private JPanel createTotalPanel() {
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        totalPanel.setBackground(DARK_COLOR);

        totalLabel = new JLabel("TOTAL: OMR 0.000");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton checkoutBtn = createStyledButton("CHECKOUT", PRIMARY_COLOR, 120, 40);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkoutBtn.addActionListener(e -> {
            playButtonClick();
            showConfetti();
        });

        JButton clearBtn = createStyledButton("CLEAR CART", new Color(189, 195, 199), 100, 35);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.addActionListener(e -> {
            playButtonClick();
            clearCart();
        });

        buttonPanel.add(checkoutBtn);
        buttonPanel.add(clearBtn);

        totalPanel.add(totalLabel);
        totalPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        totalPanel.add(buttonPanel);

        return totalPanel;
    }

    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to clear your cart?", 
            "Clear Cart", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            total = 0.0;
            updateCartUI();
        }
    }

private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, PRIMARY_COLOR.darker());
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    headerPanel.setLayout(new BorderLayout());
    headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

    // Left side - Logo and title
    JPanel logoTitlePanel = new JPanel(new BorderLayout(10, 0));
    logoTitlePanel.setOpaque(false);

    // Load and add logo
    try {
        InputStream is = getClass().getResourceAsStream("/images/logo.png");
        if (is != null) {
            BufferedImage logoImage = ImageIO.read(is);
            ImageIcon logoIcon = new ImageIcon(logoImage.getScaledInstance(85, 85, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoTitlePanel.add(logoLabel, BorderLayout.WEST);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Title panel
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
    titlePanel.setOpaque(false);

    JLabel titleLabel = new JLabel("Napuli Oven");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titleLabel.setForeground(Color.black);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Fresh ingredients, authentic taste, delivered fast");
    subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
    subtitleLabel.setForeground( Color.black);
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    titlePanel.add(titleLabel);
    titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
    titlePanel.add(subtitleLabel);

    logoTitlePanel.add(titlePanel, BorderLayout.CENTER);

    // Right side - User controls
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    controlPanel.setOpaque(false);

    userGreetingLabel = new JLabel("Hello, " + currentUser + "!");
    userGreetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    userGreetingLabel.setForeground(Color.WHITE);

    JButton loginBtn = createStyledButton("Login", SECONDARY_COLOR, 80, 30);
    loginBtn.addActionListener(e -> showLoginDialog());

    JButton themeBtn = createStyledButton(isDarkMode ? "☀️ Light" : "🌙 Dark", DARK_COLOR, 100, 30);
    themeBtn.addActionListener(e -> toggleTheme());

    controlPanel.add(userGreetingLabel);
    controlPanel.add(loginBtn);
    controlPanel.add(themeBtn);

    headerPanel.add(logoTitlePanel, BorderLayout.WEST);
    headerPanel.add(controlPanel, BorderLayout.EAST);

    return headerPanel;
}
    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.setSize(350, 300); // Increased height
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new BorderLayout());
        loginDialog.getContentPane().setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);

        JLabel titleLabel = new JLabel("Enter Your Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        
        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Destination field
        JLabel destinationLabel = new JLabel("Delivery Address:");
        destinationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        destinationLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        
        JTextField destinationField = new JTextField(20);
        destinationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        destinationField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton submitBtn = createStyledButton("Submit", ACCENT_COLOR, 120, 35);
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String destination = destinationField.getText().trim();
            
            if (!name.isEmpty() && !email.isEmpty() && !destination.isEmpty()) {
                if (isValidEmail(email)) {
                    currentUser = name;
                    userGreetingLabel.setText("Hello, " + currentUser + "!");
                    
                    // Store user details
                    userDetails.put("email", email);
                    userDetails.put("destination", destination);
                    
                    loginDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a valid email address", 
                        "Invalid Email", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all fields", 
                    "Incomplete Information", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        formPanel.add(destinationLabel);
        formPanel.add(destinationField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        formPanel.add(submitBtn);

        loginDialog.add(formPanel, BorderLayout.CENTER);
        loginDialog.setVisible(true);
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initializeCartPanel() {
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setPreferredSize(new Dimension(350, getHeight()));
        cartPanel.setBackground(isDarkMode ? new Color(50, 50, 50) : Color.WHITE);
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 220, 100)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        greetingLabel = new JLabel("<html><div style='text-align:center;'>Welcome to <b>Pizza Party!</b><br><small>Ready to build your dream pizza?</small></div></html>");
        greetingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greetingLabel.setForeground(isDarkMode ? Color.WHITE : TEXT_COLOR);
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        greetingLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        cartPanel.add(greetingLabel);

        JLabel cartTitle = new JLabel("YOUR CART");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartTitle.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        cartPanel.add(cartTitle);

        cartListContainer = new JPanel();
        cartListContainer.setLayout(new BoxLayout(cartListContainer, BoxLayout.Y_AXIS));
        cartListContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cartListContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        cartPanel.add(scrollPane);

        JLabel emptyCartLabel = new JLabel("<html><div style='text-align:center;'><i>Your cart is empty</i><br>Add some delicious pizzas!</div></html>");
        emptyCartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyCartLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyCartLabel.setForeground(isDarkMode ? Color.LIGHT_GRAY : Color.GRAY);
        cartListContainer.add(emptyCartLabel);
    }

    private void addToCart(String itemName, double price) {
        cartItems.put(itemName, cartItems.getOrDefault(itemName, 0) + 1);
        total += price;
        updateCartUI();
        playAddSound();
        
        // Show a small notification
        showToastNotification(itemName + " added to cart!");
    }

    private void showToastNotification(String message) {
        JDialog toast = new JDialog();
        toast.setUndecorated(true);
        toast.setSize(300, 50);
        toast.setLocationRelativeTo(this);
        toast.setAlwaysOnTop(true);
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 50, 50, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        
        toast.add(panel);
        toast.setVisible(true);
        
        new Timer(2000, e -> {
            toast.dispose();
            ((Timer)e.getSource()).stop();
        }).start();
    }

    private void updateCartUI() {
        cartListContainer.removeAll();
        
        if (cartItems.isEmpty()) {
            JLabel emptyCartLabel = new JLabel("<html><div style='text-align:center;'><i>Your cart is empty</i><br>Add some delicious pizzas!</div></html>");
            emptyCartLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyCartLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyCartLabel.setForeground(isDarkMode ? Color.LIGHT_GRAY : Color.GRAY);
            cartListContainer.add(emptyCartLabel);
        } else {
            for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
                String name = entry.getKey();
                int quantity = entry.getValue();

                JPanel cartItemPanel = new JPanel(new BorderLayout());
                cartItemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                cartItemPanel.setOpaque(false);

                JLabel nameLabel = new JLabel(name + " x" + quantity);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                nameLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);

                JLabel priceLabel = new JLabel("OMR " + priceFormat.format(pizzaPrices.get(name) * quantity));
                priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                priceLabel.setForeground(isDarkMode ? Color.LIGHT_GRAY : new Color(100, 100, 100));

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);
                infoPanel.add(nameLabel);
                infoPanel.add(priceLabel);

                JButton removeBtn = new JButton("−");
                removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                removeBtn.setContentAreaFilled(false);
                removeBtn.setBorderPainted(false);
                removeBtn.setForeground(PRIMARY_COLOR);
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(e -> {
                    playButtonClick();
                    int qty = cartItems.get(name);
                    if (qty <= 1) {
                        cartItems.remove(name);
                    } else {
                        cartItems.put(name, qty - 1);
                    }
                    total -= pizzaPrices.get(name);
                    updateCartUI();
                });

                cartItemPanel.add(infoPanel, BorderLayout.CENTER);
                cartItemPanel.add(removeBtn, BorderLayout.EAST);
                cartListContainer.add(cartItemPanel);
                cartListContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        totalLabel.setText(String.format("TOTAL: OMR %.3f", total));
        cartListContainer.revalidate();
        cartListContainer.repaint();
    }

    private void showCustomizationDialog(String pizzaName, Runnable onAddToCart) {
        JDialog dialog = new JDialog(this, "Customize Your " + pizzaName, true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        optionsPanel.setBackground(isDarkMode ? new Color(60, 60, 60) : Color.WHITE);

        JLabel titleLabel = new JLabel("Customize " + pizzaName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(titleLabel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Display pizza image
        if (pizzaImages.containsKey(pizzaName)) {
            JLabel pizzaImageLabel = new JLabel(pizzaImages.get(pizzaName));
            pizzaImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            optionsPanel.add(pizzaImageLabel);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JLabel priceLabel = new JLabel("Base Price: OMR " + pizzaPrices.get(pizzaName));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(PRIMARY_COLOR);
        optionsPanel.add(priceLabel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Size options
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setOpaque(false);
        sizePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sizeLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        
        ButtonGroup sizeGroup = new ButtonGroup();
        JRadioButton smallBtn = new JRadioButton("Small");
        JRadioButton mediumBtn = new JRadioButton("Medium (+OMR 1.000)");
        JRadioButton largeBtn = new JRadioButton("Large (+OMR 2.000)");
        
        smallBtn.setSelected(true);
        styleRadioButton(smallBtn);
        styleRadioButton(mediumBtn);
        styleRadioButton(largeBtn);
        
        sizeGroup.add(smallBtn);
        sizeGroup.add(mediumBtn);
        sizeGroup.add(largeBtn);
        
        sizePanel.add(sizeLabel);
        sizePanel.add(smallBtn);
        sizePanel.add(mediumBtn);
        sizePanel.add(largeBtn);
        
        optionsPanel.add(sizePanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Toppings options
        JLabel toppingsLabel = new JLabel("Extra Toppings:");
        toppingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toppingsLabel.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        toppingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(toppingsLabel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JCheckBox extraCheese = createStyledCheckBox("Extra Cheese (+OMR 0.760)");
        JCheckBox pepperoni = createStyledCheckBox("Pepperoni (+OMR 0.850)");
        JCheckBox mushrooms = createStyledCheckBox("Mushrooms (+OMR 0.650)");
        JCheckBox olives = createStyledCheckBox("Olives (+OMR 0.550)");
        JCheckBox jalapenos = createStyledCheckBox("Jalapeños (+OMR 0.600)");

        optionsPanel.add(extraCheese);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(pepperoni);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(mushrooms);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(olives);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        optionsPanel.add(jalapenos);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel totalLabel = new JLabel("Total: OMR " + pizzaPrices.get(pizzaName));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        optionsPanel.add(totalLabel);

        ActionListener priceUpdater = e -> {
            double total = pizzaPrices.get(pizzaName);
            if (mediumBtn.isSelected()) total += 1.000;
            if (largeBtn.isSelected()) total += 2.000;
            if (extraCheese.isSelected()) total += 0.760;
            if (pepperoni.isSelected()) total += 0.850;
            if (mushrooms.isSelected()) total += 0.650;
            if (olives.isSelected()) total += 0.550;
            if (jalapenos.isSelected()) total += 0.600;
            totalLabel.setText("Total: OMR " + String.format("%.3f", total));
        };

        smallBtn.addActionListener(priceUpdater);
        mediumBtn.addActionListener(priceUpdater);
        largeBtn.addActionListener(priceUpdater);
        extraCheese.addActionListener(priceUpdater);
        pepperoni.addActionListener(priceUpdater);
        mushrooms.addActionListener(priceUpdater);
        olives.addActionListener(priceUpdater);
        jalapenos.addActionListener(priceUpdater);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.setOpaque(false);
        
        if (onAddToCart != null) {
            JButton addButton = createStyledButton("Add to Cart", ACCENT_COLOR, 120, 35);
            addButton.addActionListener(e -> {
                List<String> customizations = new ArrayList<>();
                if (mediumBtn.isSelected()) customizations.add("Medium Size");
                if (largeBtn.isSelected()) customizations.add("Large Size");
                if (extraCheese.isSelected()) customizations.add("Extra Cheese");
                if (pepperoni.isSelected()) customizations.add("Pepperoni");
                if (mushrooms.isSelected()) customizations.add("Mushrooms");
                if (olives.isSelected()) customizations.add("Olives");
                if (jalapenos.isSelected()) customizations.add("Jalapeños");
                
                pizzaCustomizations.put(pizzaName, customizations);
                pizzaPrices.put(pizzaName, Double.parseDouble(totalLabel.getText().substring(10)));
                
                dialog.dispose();
                onAddToCart.run();
            });
            buttonPanel.add(addButton);
        }

        JButton cancelButton = createStyledButton("Cancel", new Color(189, 195, 199), 100, 35);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(optionsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radioButton.setForeground(isDarkMode ? Color.WHITE : DARK_COLOR);
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(false);
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setForeground(isDarkMode ? Color.WHITE : TEXT_COLOR);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        return checkBox;
    }

    private void showConfetti() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "<html><div style='text-align:center;'>Your cart is empty!<br>Add some delicious pizzas first.</div></html>", 
                "Empty Cart", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (userDetails.isEmpty() || !userDetails.containsKey("email") || !userDetails.containsKey("destination")) {
            JOptionPane.showMessageDialog(this, 
                "<html><div style='text-align:center;'>Please login and provide your contact information first.</div></html>", 
                "Login Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        playCheckoutSound();
        
        // Create order summary
        StringBuilder summary = new StringBuilder("<html><div style='text-align:center;'><h2>Order Summary</h2><br>");
        summary.append("<table align='center' cellpadding='5'>");
        summary.append("<tr><th align='left'>Item</th><th align='right'>Qty</th><th align='right'>Price</th></tr>");
        
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            double price = pizzaPrices.get(name) * qty;
            
            summary.append("<tr>")
                  .append("<td align='left'>").append(name).append("</td>")
                  .append("<td align='right'>").append(qty).append("</td>")
                  .append("<td align='right'>OMR ").append(priceFormat.format(price)).append("</td>")
                  .append("</tr>");
            
            // Add customizations if any
            List<String> customizations = pizzaCustomizations.get(name);
            if (customizations != null && !customizations.isEmpty()) {
                summary.append("<tr><td colspan='3' align='left' style='font-size:smaller; color:#666;'>")
                      .append("&nbsp;&nbsp;• ").append(String.join(", ", customizations))
                      .append("</td></tr>");
            }
        }
        
        summary.append("<tr><td colspan='3'><hr></td></tr>")
              .append("<tr><td align='left'><b>Total</b></td><td></td><td align='right'><b>OMR ")
              .append(priceFormat.format(total)).append("</b></td></tr>")
              .append("</table><br>");
        
        // Add user details
        summary.append("<div style='text-align:left; margin-left:20%;'>")
              .append("<b>Delivery to:</b> ").append(userDetails.get("destination")).append("<br>")
              .append("<b>Confirmation will be sent to:</b> ").append(userDetails.get("email"))
              .append("</div><br><br>Thank you for your order, ").append(currentUser).append("!<br><br>🍕🎉</div></html>");
        
        JDialog confettiDialog = new JDialog(this, "Order Confirmed!", true);
        confettiDialog.setSize(500, 650);
        confettiDialog.setLocationRelativeTo(this);
        confettiDialog.setUndecorated(true);
        confettiDialog.setShape(new RoundRectangle2D.Double(0, 0, 500, 650, 30, 30));
        
        JPanel confettiPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw gradient background
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_COLOR, getWidth(), getHeight(), ACCENT_COLOR.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Draw confetti
                for (int i = 0; i < 100; i++) {
                    g2d.setColor(new Color(
                        (int)(Math.random() * 255),
                        (int)(Math.random() * 255),
                        (int)(Math.random() * 255),
                        200 + (int)(Math.random() * 55)
                    ));
                    int size = 5 + (int)(Math.random() * 15);
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    
                    if (Math.random() > 0.5) {
                        g2d.fillRect(x, y, size, size);
                    } else {
                        g2d.fillOval(x, y, size, size);
                    }
                }
            }
        };
        
        JLabel message = new JLabel(summary.toString(), SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(Color.WHITE);
        message.setBorder(new EmptyBorder(40, 20, 20, 20));
        confettiPanel.add(message, BorderLayout.CENTER);
        
        JButton closeBtn = createStyledButton("OK", Color.WHITE, 100, 35);
        closeBtn.setForeground(ACCENT_COLOR);
        closeBtn.addActionListener(e -> {
            playButtonClick();
            confettiDialog.dispose();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);
        confettiPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        confettiDialog.add(confettiPanel);
        
        confettiTimer = new Timer(100, e -> confettiPanel.repaint());
        confettiTimer.start();
        
        confettiDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                confettiTimer.stop();
                cartItems.clear();
                total = 0.0;
                updateCartUI();
            }
        });
        
        confettiDialog.setVisible(true);
    }

    private void playButtonClick() {
        if (buttonClickSound != null) {
            buttonClickSound.setFramePosition(0);
            buttonClickSound.start();
        }
    }

    private void playAddSound() {
        if (addToCartSound != null) {
            addToCartSound.setFramePosition(0);
            addToCartSound.start();
        }
    }

    private void playCheckoutSound() {
        if (checkoutSound != null) {
            checkoutSound.setFramePosition(0);
            checkoutSound.start();
        }
    }

    private void loadPizzaImages() throws IOException {
        pizzaImages = new HashMap<>();
        for (String[] item : menuItems) {
            String imageName = item[3];
            try {
                InputStream is = getClass().getResourceAsStream("/images/" + imageName);
                if (is != null) {
                    BufferedImage img = ImageIO.read(is);
                    // Create rounded corners for images
                    BufferedImage rounded = new BufferedImage(150, 100, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = rounded.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, 150, 100, 20, 20);
                    g2.setComposite(AlphaComposite.SrcIn);
                    g2.drawImage(img.getScaledInstance(150, 100, Image.SCALE_SMOOTH), 0, 0, null);
                    g2.dispose();
                    
                    pizzaImages.put(item[0], new ImageIcon(rounded));
                } else {
                    pizzaImages.put(item[0], createPlaceholderIcon(150, 100));
                }
            } catch (IOException e) {
                pizzaImages.put(item[0], createPlaceholderIcon(150, 100));
            }
        }
    }

    private ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2d.setColor(LIGHT_COLOR);
        g2d.fillRoundRect(0, 0, width, height, 20, 20);
        
        // Draw pizza icon
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(width/4, height/4, width/2, height/2);
        
        // Draw pizza slices
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawLine(width/2, height/4, width/2, height*3/4);
        g2d.drawLine(width/4, height/2, width*3/4, height/2);
        g2d.drawLine(width/3, height/3, width*2/3, height*2/3);
        g2d.drawLine(width*2/3, height/3, width/3, height*2/3);
        
        // Draw text
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createIcon(String path, int width, int height) {
        try {
            InputStream is = getClass().getResourceAsStream("/icons/" + path);
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createPlaceholderIcon(width, height);
    }

    private void loadSounds() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        // These would be loaded from actual sound files in a real application
        // Here we just initialize them with empty clips
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        
        // Button click sound
        buttonClickSound = AudioSystem.getClip();
        
        // Add to cart sound
        addToCartSound = AudioSystem.getClip();
        
        // Checkout sound
        checkoutSound = AudioSystem.getClip();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            PizzaOrderSystem frame = new PizzaOrderSystem();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        });
    }
}