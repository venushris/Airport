/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package airport.view;

import javax.swing.JOptionPane;
import airport.model.*;
import airport.controller.*;
import airport.observer.Observer; // Import the Observer interface
import airport.response.*;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 *
 * @author edangulo
 */
public class AirportFrame extends javax.swing.JFrame implements Observer { // Implement Observer

    /**
     * Creates new form AirportFrame
     */
    private int x, y;
    private final PassengerController passengerController;
    private final PlaneController planeController;
    private final LocationController locationController;
    private final FlightController flightController;

    public AirportFrame(PassengerController pc,
                        PlaneController plc,
                        LocationController lc,
                        FlightController fc) {
        initComponents();
        this.passengerController = pc;
        this.planeController     = plc;
        this.locationController  = lc;
        this.flightController    = fc;

        // Register this frame as an observer for each controller
        this.passengerController.registerObserver(this);
        this.planeController.registerObserver(this);
        this.locationController.registerObserver(this);
        this.flightController.registerObserver(this);

        loadInitialData();

        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);

        this.generateMonths();
        this.generateDays();
        this.generateHours();
        this.generateMinutes();
        this.blockPanels();
    }

    /**
     * Implementation of the Observer pattern's update method.
     * This method is called by subjects (controllers) when data changes.
     * @param dataType A string indicating which type of data was updated
     * (e.g., "passenger", "plane", "flight", "location").
     */
    @Override
    public void update(String dataType) {
        if (dataType == null) return;

        switch (dataType) {
            case "passenger":
                if (tableAllPassengers.isShowing()) { // Only refresh if the tab is active/visible or relevant
                    btnRefreshAllPassengersActionPerformed(null);
                }
                // If a specific user is selected, their "My Flights" might need an update if addPassengerToFlight was called
                if (comboSelectUser.getSelectedIndex() > 0 && tableMyFlights.isShowing()) {
                    btnRefreshMyFlightsActionPerformed(null);
                }
                break;
            case "plane":
                if (tableAllPlanes.isShowing()) {
                    btnRefreshAllPlanesActionPerformed(null);
                }
                break;
            case "location":
                if (tableAllLocations.isShowing()) {
                    btnRefreshAllLocationsActionPerformed(null);
                }
                break;
            case "flight":
                if (tableAllFlights.isShowing()) {
                    btnRefreshAllFlightsActionPerformed(null);
                }
                // If a specific user is selected, their "My Flights" might need an update
                if (comboSelectUser.getSelectedIndex() > 0 && tableMyFlights.isShowing()) {
                    btnRefreshMyFlightsActionPerformed(null);
                }
                break;
            default:
                // Unknown data type, or no specific table to refresh for it
                break;
        }
    }


    /**
     * Carga pasajeros, aviones, localizaciones y vuelos desde los controllers
     * y los inserta en los JComboBox correspondientes.
     */
    private void loadInitialData() {
        // 1) Pasajeros → comboSelectUser
        Response<List<Passenger>> rp = passengerController.getAllPassengers();
        if (rp.isSuccess()) {
            for (Passenger p : rp.getData()) {
                comboSelectUser.addItem(String.valueOf(p.getId()));
            }
        }

        // 2) Aviones → comboFlightPlane
        Response<List<Plane>> rpl = planeController.getAllPlanes();
        if (rpl.isSuccess()) {
            for (Plane pl : rpl.getData()) {
                comboFlightPlane.addItem(pl.getId());
            }
        }

        // 3) Localizaciones → comboFlightDepartureLocation, comboFlightArrivalLocation, comboFlightScaleLocation
        Response<List<Location>> rloc = locationController.getAllLocations();
        if (rloc.isSuccess()) {
            for (Location loc : rloc.getData()) {
                String id = loc.getAirportId();
                comboFlightDepartureLocation.addItem(id);
                comboFlightArrivalLocation .addItem(id);
                comboFlightScaleLocation   .addItem(id);
            }
        }

        // 4) Vuelos → comboFlight_AddToFlight, comboFlightId_DelayFlight
        Response<List<Flight>> rfl = flightController.getAllFlights();
        if (rfl.isSuccess()) {
            for (Flight f : rfl.getData()) {
                String id = f.getId();
                comboFlight_AddToFlight   .addItem(id);
                comboFlightId_DelayFlight .addItem(id);
            }
        }
    }

    private void blockPanels() {
        //9, 11
        for (int i = 1; i < jTabbedPane1.getTabCount(); i++) {
            if (i != 9 && i != 11) {
                jTabbedPane1.setEnabledAt(i, false);
            }
        }
    }

    private void generateMonths() {
        for (int i = 1; i < 13; i++) {
            comboPassengerBirthMonth.addItem("" + i);
            comboFlightDepartureMonth.addItem("" + i);
            comboUpdatePassengerBirthMonth.addItem("" + i);
        }
    }

    private void generateDays() {
        for (int i = 1; i < 32; i++) {
            comboPassengerBirthDay.addItem("" + i);
            comboFlightDepartureDay.addItem("" + i);
            comboUpdatePassengerBirthDay.addItem("" + i);
        }
    }

    private void generateHours() {
        for (int i = 0; i < 24; i++) {
            comboFlightDepartureHour.addItem("" + i);
            comboFlightArrivalDurationHour.addItem("" + i);
            comboFlightScaleDurationHour.addItem("" + i);
            comboDelayFlightHours.addItem("" + i);
        }
    }

    private void generateMinutes() {
        for (int i = 0; i < 60; i++) {
            comboFlightDepartureMinute.addItem("" + i);
            comboFlightArrivalDurationMinute.addItem("" + i);
            comboFlightScaleDurationMinute.addItem("" + i);
            comboDelayFlightMinutes.addItem("" + i);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new airport.view.PanelRound();
        panelRound2 = new airport.view.PanelRound();
        jButton13 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelAdministration = new javax.swing.JPanel();
        radioUserTypeUser = new javax.swing.JRadioButton();
        radioUserTypeAdmin = new javax.swing.JRadioButton();
        comboSelectUser = new javax.swing.JComboBox<>();
        panelPassengerRegistration = new javax.swing.JPanel();
        lblPassengerCountry = new javax.swing.JLabel();
        lblPassengerId = new javax.swing.JLabel();
        lblPassengerFirstName = new javax.swing.JLabel();
        lblPassengerLastName = new javax.swing.JLabel();
        lblPassengerBirthDate = new javax.swing.JLabel();
        lblPassengerPhoneCodePrefix = new javax.swing.JLabel();
        txtPassengerPhoneCode = new javax.swing.JTextField();
        txtPassengerId = new javax.swing.JTextField();
        txtPassengerBirthYear = new javax.swing.JTextField();
        txtPassengerCountry = new javax.swing.JTextField();
        txtPassengerPhoneNumber = new javax.swing.JTextField();
        lblPassengerPhone = new javax.swing.JLabel();
        lblPassengerBirthDateSeparator1 = new javax.swing.JLabel();
        txtPassengerLastName = new javax.swing.JTextField();
        lblPassengerPhoneSeparator = new javax.swing.JLabel();
        comboPassengerBirthMonth = new javax.swing.JComboBox<>();
        txtPassengerFirstName = new javax.swing.JTextField();
        lblPassengerBirthDateSeparator2 = new javax.swing.JLabel();
        comboPassengerBirthDay = new javax.swing.JComboBox<>();
        btnRegisterPassenger = new javax.swing.JButton();
        panelPlaneRegistration = new javax.swing.JPanel();
        lblPlaneId = new javax.swing.JLabel();
        txtPlaneId = new javax.swing.JTextField();
        lblPlaneBrand = new javax.swing.JLabel();
        txtPlaneBrand = new javax.swing.JTextField();
        txtPlaneModel = new javax.swing.JTextField();
        lblPlaneModel = new javax.swing.JLabel();
        txtPlaneMaxCapacity = new javax.swing.JTextField();
        lblPlaneMaxCapacity = new javax.swing.JLabel();
        txtPlaneAirline = new javax.swing.JTextField();
        lblPlaneAirline = new javax.swing.JLabel();
        btnCreatePlane = new javax.swing.JButton();
        panelLocationRegistration = new javax.swing.JPanel();
        lblLocationAirportId = new javax.swing.JLabel();
        txtLocationAirportId = new javax.swing.JTextField();
        lblLocationAirportName = new javax.swing.JLabel();
        txtLocationAirportName = new javax.swing.JTextField();
        txtLocationAirportCity = new javax.swing.JTextField();
        lblLocationAirportCity = new javax.swing.JLabel();
        lblLocationAirportCountry = new javax.swing.JLabel();
        txtLocationAirportCountry = new javax.swing.JTextField();
        txtLocationAirportLatitude = new javax.swing.JTextField();
        lblLocationAirportLatitude = new javax.swing.JLabel();
        lblLocationAirportLongitude = new javax.swing.JLabel();
        txtLocationAirportLongitude = new javax.swing.JTextField();
        btnCreateLocation = new javax.swing.JButton();
        panelFlightRegistration = new javax.swing.JPanel();
        lblFlightId = new javax.swing.JLabel();
        txtFlightId = new javax.swing.JTextField();
        lblFlightPlane = new javax.swing.JLabel();
        comboFlightPlane = new javax.swing.JComboBox<>();
        comboFlightDepartureLocation = new javax.swing.JComboBox<>();
        lblFlightDepartureLocation = new javax.swing.JLabel();
        comboFlightArrivalLocation = new javax.swing.JComboBox<>();
        lblFlightArrivalLocation = new javax.swing.JLabel();
        lblFlightScaleLocation = new javax.swing.JLabel();
        comboFlightScaleLocation = new javax.swing.JComboBox<>();
        lblFlightDurationScale = new javax.swing.JLabel();
        lblFlightDurationArrival = new javax.swing.JLabel();
        lblFlightDepartureDate = new javax.swing.JLabel();
        txtFlightDepartureYear = new javax.swing.JTextField();
        lblFlightDateSeparator1 = new javax.swing.JLabel();
        comboFlightDepartureMonth = new javax.swing.JComboBox<>();
        lblFlightDateSeparator2 = new javax.swing.JLabel();
        comboFlightDepartureDay = new javax.swing.JComboBox<>();
        lblFlightTimeSeparator1 = new javax.swing.JLabel();
        comboFlightDepartureHour = new javax.swing.JComboBox<>();
        lblFlightTimeSeparator2 = new javax.swing.JLabel();
        comboFlightDepartureMinute = new javax.swing.JComboBox<>();
        comboFlightArrivalDurationHour = new javax.swing.JComboBox<>();
        lblFlightArrivalDurationSeparator = new javax.swing.JLabel();
        comboFlightArrivalDurationMinute = new javax.swing.JComboBox<>();
        lblFlightScaleDurationSeparator = new javax.swing.JLabel();
        comboFlightScaleDurationHour = new javax.swing.JComboBox<>();
        comboFlightScaleDurationMinute = new javax.swing.JComboBox<>();
        btnCreateFlight = new javax.swing.JButton();
        panelUpdateInfo = new javax.swing.JPanel();
        lblUpdatePassengerId = new javax.swing.JLabel();
        txtUpdatePassengerId = new javax.swing.JTextField();
        lblUpdatePassengerFirstName = new javax.swing.JLabel();
        txtUpdatePassengerFirstName = new javax.swing.JTextField();
        lblUpdatePassengerLastName = new javax.swing.JLabel();
        txtUpdatePassengerLastName = new javax.swing.JTextField();
        lblUpdatePassengerBirthDate = new javax.swing.JLabel();
        txtUpdatePassengerBirthYear = new javax.swing.JTextField();
        comboUpdatePassengerBirthMonth = new javax.swing.JComboBox<>();
        comboUpdatePassengerBirthDay = new javax.swing.JComboBox<>();
        txtUpdatePassengerPhoneNumber = new javax.swing.JTextField();
        lblUpdatePassengerPhoneSeparator = new javax.swing.JLabel();
        txtUpdatePassengerPhoneCode = new javax.swing.JTextField();
        lblUpdatePassengerPhoneCodePrefix = new javax.swing.JLabel();
        lblUpdatePassengerPhone = new javax.swing.JLabel();
        lblUpdatePassengerCountry = new javax.swing.JLabel();
        txtUpdatePassengerCountry = new javax.swing.JTextField();
        btnUpdatePassengerInfo = new javax.swing.JButton();
        panelAddToFlight = new javax.swing.JPanel();
        txtPassengerId_AddToFlight = new javax.swing.JTextField();
        lblPassengerId_AddToFlight = new javax.swing.JLabel();
        lblFlight_AddToFlight = new javax.swing.JLabel();
        comboFlight_AddToFlight = new javax.swing.JComboBox<>();
        btnAddPassengerToFlight = new javax.swing.JButton();
        panelShowMyFlights = new javax.swing.JPanel();
        scrollPaneMyFlights = new javax.swing.JScrollPane();
        tableMyFlights = new javax.swing.JTable();
        btnRefreshMyFlights = new javax.swing.JButton();
        panelShowPassengers = new javax.swing.JPanel();
        scrollPaneAllPassengers = new javax.swing.JScrollPane();
        tableAllPassengers = new javax.swing.JTable();
        btnRefreshAllPassengers = new javax.swing.JButton();
        panelShowFlights = new javax.swing.JPanel();
        scrollPaneAllFlights = new javax.swing.JScrollPane();
        tableAllFlights = new javax.swing.JTable();
        btnRefreshAllFlights = new javax.swing.JButton();
        panelShowPlanes = new javax.swing.JPanel();
        btnRefreshAllPlanes = new javax.swing.JButton();
        scrollPaneAllPlanes = new javax.swing.JScrollPane();
        tableAllPlanes = new javax.swing.JTable();
        panelShowLocations = new javax.swing.JPanel();
        scrollPaneAllLocations = new javax.swing.JScrollPane();
        tableAllLocations = new javax.swing.JTable();
        btnRefreshAllLocations = new javax.swing.JButton();
        panelDelayFlight = new javax.swing.JPanel();
        comboDelayFlightHours = new javax.swing.JComboBox<>();
        lblDelayFlightHours = new javax.swing.JLabel();
        lblFlightId_DelayFlight = new javax.swing.JLabel();
        comboFlightId_DelayFlight = new javax.swing.JComboBox<>();
        lblDelayFlightMinutes = new javax.swing.JLabel();
        comboDelayFlightMinutes = new javax.swing.JComboBox<>();
        btnDelayFlight = new javax.swing.JButton();
        panelRound3 = new airport.view.PanelRound();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelRound1.setRadius(40);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelRound2MouseDragged(evt);
            }
        });
        panelRound2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelRound2MousePressed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jButton13.setText("X");
        jButton13.setBorderPainted(false);
        jButton13.setContentAreaFilled(false);
        jButton13.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
                panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                                .addContainerGap(1083, Short.MAX_VALUE)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))
        );
        panelRound2Layout.setVerticalGroup(
                panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelRound2Layout.createSequentialGroup()
                                .addComponent(jButton13)
                                .addGap(0, 12, Short.MAX_VALUE))
        );

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1150, -1));

        jTabbedPane1.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N

        panelAdministration.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        radioUserTypeUser.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        radioUserTypeUser.setText("User");
        radioUserTypeUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioUserTypeUserActionPerformed(evt);
            }
        });
        panelAdministration.add(radioUserTypeUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 230, -1, -1));

        radioUserTypeAdmin.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        radioUserTypeAdmin.setText("Administrator");
        radioUserTypeAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioUserTypeAdminActionPerformed(evt);
            }
        });
        panelAdministration.add(radioUserTypeAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 164, -1, -1));

        comboSelectUser.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboSelectUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select User" }));
        comboSelectUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSelectUserActionPerformed(evt);
            }
        });
        panelAdministration.add(comboSelectUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 300, 130, -1));

        jTabbedPane1.addTab("Administration", panelAdministration);

        panelPassengerRegistration.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPassengerCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerCountry.setText("Country:");
        panelPassengerRegistration.add(lblPassengerCountry, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, -1, -1));

        lblPassengerId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerId.setText("ID:");
        panelPassengerRegistration.add(lblPassengerId, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        lblPassengerFirstName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerFirstName.setText("First Name:");
        panelPassengerRegistration.add(lblPassengerFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

        lblPassengerLastName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerLastName.setText("Last Name:");
        panelPassengerRegistration.add(lblPassengerLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, -1, -1));

        lblPassengerBirthDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerBirthDate.setText("Birthdate:");
        panelPassengerRegistration.add(lblPassengerBirthDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, -1, -1));

        lblPassengerPhoneCodePrefix.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerPhoneCodePrefix.setText("+");
        panelPassengerRegistration.add(lblPassengerPhoneCodePrefix, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 340, 20, -1));

        txtPassengerPhoneCode.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerPhoneCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 50, -1));

        txtPassengerId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerId, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 130, -1));

        txtPassengerBirthYear.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerBirthYear, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, 90, -1));

        txtPassengerCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerCountry, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, 130, -1));

        txtPassengerPhoneNumber.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerPhoneNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 340, 130, -1));

        lblPassengerPhone.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerPhone.setText("Phone:");
        panelPassengerRegistration.add(lblPassengerPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, -1, -1));

        lblPassengerBirthDateSeparator1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerBirthDateSeparator1.setText("-");
        panelPassengerRegistration.add(lblPassengerBirthDateSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 280, 30, -1));

        txtPassengerLastName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 130, -1));

        lblPassengerPhoneSeparator.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerPhoneSeparator.setText("-");
        panelPassengerRegistration.add(lblPassengerPhoneSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 340, 30, -1));

        comboPassengerBirthMonth.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboPassengerBirthMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));
        panelPassengerRegistration.add(comboPassengerBirthMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 280, -1, -1));

        txtPassengerFirstName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPassengerRegistration.add(txtPassengerFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 130, -1));

        lblPassengerBirthDateSeparator2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerBirthDateSeparator2.setText("-");
        panelPassengerRegistration.add(lblPassengerBirthDateSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 280, 30, -1));

        comboPassengerBirthDay.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboPassengerBirthDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));
        panelPassengerRegistration.add(comboPassengerBirthDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 280, -1, -1));

        btnRegisterPassenger.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRegisterPassenger.setText("Register");
        btnRegisterPassenger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterPassengerActionPerformed(evt);
            }
        });
        panelPassengerRegistration.add(btnRegisterPassenger, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 480, -1, -1));

        jTabbedPane1.addTab("Passenger registration", panelPassengerRegistration);

        panelPlaneRegistration.setLayout(null);

        lblPlaneId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPlaneId.setText("ID:");
        panelPlaneRegistration.add(lblPlaneId);
        lblPlaneId.setBounds(53, 96, 22, 25);

        txtPlaneId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPlaneRegistration.add(txtPlaneId);
        txtPlaneId.setBounds(180, 93, 130, 31);

        lblPlaneBrand.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPlaneBrand.setText("Brand:");
        panelPlaneRegistration.add(lblPlaneBrand);
        lblPlaneBrand.setBounds(53, 157, 50, 25);

        txtPlaneBrand.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPlaneRegistration.add(txtPlaneBrand);
        txtPlaneBrand.setBounds(180, 154, 130, 31);

        txtPlaneModel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPlaneRegistration.add(txtPlaneModel);
        txtPlaneModel.setBounds(180, 213, 130, 31);

        lblPlaneModel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPlaneModel.setText("Model:");
        panelPlaneRegistration.add(lblPlaneModel);
        lblPlaneModel.setBounds(53, 216, 55, 25);

        txtPlaneMaxCapacity.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPlaneRegistration.add(txtPlaneMaxCapacity);
        txtPlaneMaxCapacity.setBounds(180, 273, 130, 31);

        lblPlaneMaxCapacity.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPlaneMaxCapacity.setText("Max Capacity:");
        panelPlaneRegistration.add(lblPlaneMaxCapacity);
        lblPlaneMaxCapacity.setBounds(53, 276, 109, 25);

        txtPlaneAirline.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        panelPlaneRegistration.add(txtPlaneAirline);
        txtPlaneAirline.setBounds(180, 333, 130, 31);

        lblPlaneAirline.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPlaneAirline.setText("Airline:");
        panelPlaneRegistration.add(lblPlaneAirline);
        lblPlaneAirline.setBounds(53, 336, 70, 25);

        btnCreatePlane.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreatePlane.setText("Create");
        btnCreatePlane.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreatePlaneActionPerformed(evt);
            }
        });
        panelPlaneRegistration.add(btnCreatePlane);
        btnCreatePlane.setBounds(490, 480, 120, 40);

        jTabbedPane1.addTab("Airplane registration", panelPlaneRegistration);

        lblLocationAirportId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportId.setText("Airport ID:");

        txtLocationAirportId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblLocationAirportName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportName.setText("Airport name:");

        txtLocationAirportName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtLocationAirportCity.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblLocationAirportCity.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportCity.setText("Airport city:");

        lblLocationAirportCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportCountry.setText("Airport country:");

        txtLocationAirportCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        txtLocationAirportLatitude.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblLocationAirportLatitude.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportLatitude.setText("Airport latitude:");

        lblLocationAirportLongitude.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblLocationAirportLongitude.setText("Airport longitude:");

        txtLocationAirportLongitude.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        btnCreateLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateLocation.setText("Create");
        btnCreateLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateLocationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLocationRegistrationLayout = new javax.swing.GroupLayout(panelLocationRegistration);
        panelLocationRegistration.setLayout(panelLocationRegistrationLayout);
        panelLocationRegistrationLayout.setHorizontalGroup(
                panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                .addGroup(panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                                .addGap(52, 52, 52)
                                                .addGroup(panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblLocationAirportId)
                                                        .addComponent(lblLocationAirportName)
                                                        .addComponent(lblLocationAirportCity)
                                                        .addComponent(lblLocationAirportCountry)
                                                        .addComponent(lblLocationAirportLatitude)
                                                        .addComponent(lblLocationAirportLongitude))
                                                .addGap(80, 80, 80)
                                                .addGroup(panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtLocationAirportLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLocationAirportId, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLocationAirportName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLocationAirportCity, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLocationAirportCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtLocationAirportLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                                .addGap(515, 515, 515)
                                                .addComponent(btnCreateLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(515, 515, 515))
        );
        panelLocationRegistrationLayout.setVerticalGroup(
                panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addGroup(panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                                .addComponent(lblLocationAirportId)
                                                .addGap(36, 36, 36)
                                                .addComponent(lblLocationAirportName)
                                                .addGap(34, 34, 34)
                                                .addComponent(lblLocationAirportCity)
                                                .addGap(35, 35, 35)
                                                .addComponent(lblLocationAirportCountry)
                                                .addGap(35, 35, 35)
                                                .addComponent(lblLocationAirportLatitude))
                                        .addGroup(panelLocationRegistrationLayout.createSequentialGroup()
                                                .addComponent(txtLocationAirportId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(30, 30, 30)
                                                .addComponent(txtLocationAirportName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(28, 28, 28)
                                                .addComponent(txtLocationAirportCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(29, 29, 29)
                                                .addComponent(txtLocationAirportCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(29, 29, 29)
                                                .addComponent(txtLocationAirportLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(44, 44, 44)
                                .addGroup(panelLocationRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblLocationAirportLongitude)
                                        .addComponent(txtLocationAirportLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                                .addComponent(btnCreateLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47))
        );

        jTabbedPane1.addTab("Location registration", panelLocationRegistration);

        lblFlightId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightId.setText("ID:");

        txtFlightId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblFlightPlane.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightPlane.setText("Plane:");

        comboFlightPlane.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightPlane.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Plane" }));

        comboFlightDepartureLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightDepartureLocation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        lblFlightDepartureLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDepartureLocation.setText("Departure location:");

        comboFlightArrivalLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightArrivalLocation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        lblFlightArrivalLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightArrivalLocation.setText("Arrival location:");

        lblFlightScaleLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightScaleLocation.setText("Scale location:");

        comboFlightScaleLocation.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightScaleLocation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        lblFlightDurationScale.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDurationScale.setText("Duration:");

        lblFlightDurationArrival.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDurationArrival.setText("Duration:");

        lblFlightDepartureDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDepartureDate.setText("Departure date:");

        txtFlightDepartureYear.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblFlightDateSeparator1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDateSeparator1.setText("-");

        comboFlightDepartureMonth.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightDepartureMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));

        lblFlightDateSeparator2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightDateSeparator2.setText("-");

        comboFlightDepartureDay.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightDepartureDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        lblFlightTimeSeparator1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightTimeSeparator1.setText("-");

        comboFlightDepartureHour.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightDepartureHour.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        lblFlightTimeSeparator2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightTimeSeparator2.setText("-");

        comboFlightDepartureMinute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightDepartureMinute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        comboFlightArrivalDurationHour.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightArrivalDurationHour.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        lblFlightArrivalDurationSeparator.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightArrivalDurationSeparator.setText("-");

        comboFlightArrivalDurationMinute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightArrivalDurationMinute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        lblFlightScaleDurationSeparator.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightScaleDurationSeparator.setText("-");

        comboFlightScaleDurationHour.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightScaleDurationHour.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        comboFlightScaleDurationMinute.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightScaleDurationMinute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        btnCreateFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnCreateFlight.setText("Create");
        btnCreateFlight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateFlightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFlightRegistrationLayout = new javax.swing.GroupLayout(panelFlightRegistration);
        panelFlightRegistration.setLayout(panelFlightRegistrationLayout);
        panelFlightRegistrationLayout.setHorizontalGroup(
                panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addComponent(lblFlightScaleLocation)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(comboFlightScaleLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFlightRegistrationLayout.createSequentialGroup()
                                                .addComponent(lblFlightArrivalLocation)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(comboFlightArrivalLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addComponent(lblFlightDepartureLocation)
                                                .addGap(46, 46, 46)
                                                .addComponent(comboFlightDepartureLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFlightId)
                                                        .addComponent(lblFlightPlane))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtFlightId)
                                                        .addComponent(comboFlightPlane, 0, 130, Short.MAX_VALUE))))
                                .addGap(45, 45, 45)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblFlightDurationScale)
                                        .addComponent(lblFlightDurationArrival)
                                        .addComponent(lblFlightDepartureDate))
                                .addGap(18, 18, 18)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addComponent(txtFlightDepartureYear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addGap(20, 20, 20)
                                                                .addComponent(comboFlightDepartureMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(lblFlightDateSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(14, 14, 14)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFlightDateSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addGap(20, 20, 20)
                                                                .addComponent(comboFlightDepartureDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addGap(20, 20, 20)
                                                                .addComponent(comboFlightDepartureHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(lblFlightTimeSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(14, 14, 14)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFlightTimeSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addGap(20, 20, 20)
                                                                .addComponent(comboFlightDepartureMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(30, 30, 30))
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addComponent(comboFlightArrivalDurationHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(14, 14, 14)
                                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblFlightArrivalDurationSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                                .addGap(20, 20, 20)
                                                                                .addComponent(comboFlightArrivalDurationMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                .addComponent(comboFlightScaleDurationHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(14, 14, 14)
                                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblFlightScaleDurationSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                                                .addGap(20, 20, 20)
                                                                                .addComponent(comboFlightScaleDurationMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFlightRegistrationLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCreateFlight, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(530, 530, 530))
        );
        panelFlightRegistrationLayout.setVerticalGroup(
                panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addGap(3, 3, 3)
                                                .addComponent(lblFlightId))
                                        .addComponent(txtFlightId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFlightPlane)
                                        .addComponent(comboFlightPlane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(comboFlightDepartureHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblFlightTimeSeparator1)
                                        .addComponent(lblFlightTimeSeparator2)
                                        .addComponent(comboFlightDepartureMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panelFlightRegistrationLayout.createSequentialGroup()
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblFlightDepartureLocation)
                                                                .addComponent(comboFlightDepartureLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(lblFlightDepartureDate))
                                                        .addComponent(txtFlightDepartureYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(comboFlightDepartureMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblFlightDateSeparator1)
                                                        .addComponent(lblFlightDateSeparator2)
                                                        .addComponent(comboFlightDepartureDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(38, 38, 38)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblFlightArrivalLocation)
                                                                .addComponent(comboFlightArrivalLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(lblFlightDurationArrival))
                                                        .addComponent(comboFlightArrivalDurationHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblFlightArrivalDurationSeparator)
                                                        .addComponent(comboFlightArrivalDurationMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(34, 34, 34)
                                                .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(comboFlightScaleDurationHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblFlightScaleDurationSeparator)
                                                        .addComponent(comboFlightScaleDurationMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(panelFlightRegistrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lblFlightScaleLocation)
                                                                .addComponent(comboFlightScaleLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(lblFlightDurationScale)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                                .addComponent(btnCreateFlight, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))
        );

        jTabbedPane1.addTab("Flight registration", panelFlightRegistration);

        lblUpdatePassengerId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerId.setText("ID:");

        txtUpdatePassengerId.setEditable(false);
        txtUpdatePassengerId.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtUpdatePassengerId.setEnabled(false);

        lblUpdatePassengerFirstName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerFirstName.setText("First Name:");

        txtUpdatePassengerFirstName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblUpdatePassengerLastName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerLastName.setText("Last Name:");

        txtUpdatePassengerLastName.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblUpdatePassengerBirthDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerBirthDate.setText("Birthdate:");

        txtUpdatePassengerBirthYear.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        comboUpdatePassengerBirthMonth.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboUpdatePassengerBirthMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));

        comboUpdatePassengerBirthDay.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboUpdatePassengerBirthDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        txtUpdatePassengerPhoneNumber.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblUpdatePassengerPhoneSeparator.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerPhoneSeparator.setText("-");

        txtUpdatePassengerPhoneCode.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lblUpdatePassengerPhoneCodePrefix.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerPhoneCodePrefix.setText("+");

        lblUpdatePassengerPhone.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerPhone.setText("Phone:");

        lblUpdatePassengerCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblUpdatePassengerCountry.setText("Country:");

        txtUpdatePassengerCountry.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        btnUpdatePassengerInfo.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnUpdatePassengerInfo.setText("Update");
        btnUpdatePassengerInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePassengerInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUpdateInfoLayout = new javax.swing.GroupLayout(panelUpdateInfo);
        panelUpdateInfo.setLayout(panelUpdateInfoLayout);
        panelUpdateInfoLayout.setHorizontalGroup(
                panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                .addGap(72, 72, 72)
                                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerId)
                                                                .addGap(108, 108, 108)
                                                                .addComponent(txtUpdatePassengerId, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerFirstName)
                                                                .addGap(41, 41, 41)
                                                                .addComponent(txtUpdatePassengerFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerLastName)
                                                                .addGap(43, 43, 43)
                                                                .addComponent(txtUpdatePassengerLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerBirthDate)
                                                                .addGap(55, 55, 55)
                                                                .addComponent(txtUpdatePassengerBirthYear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(30, 30, 30)
                                                                .addComponent(comboUpdatePassengerBirthMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(34, 34, 34)
                                                                .addComponent(comboUpdatePassengerBirthDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerPhone)
                                                                .addGap(56, 56, 56)
                                                                .addComponent(lblUpdatePassengerPhoneCodePrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, 0)
                                                                .addComponent(txtUpdatePassengerPhoneCode, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(20, 20, 20)
                                                                .addComponent(lblUpdatePassengerPhoneSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, 0)
                                                                .addComponent(txtUpdatePassengerPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                                .addComponent(lblUpdatePassengerCountry)
                                                                .addGap(63, 63, 63)
                                                                .addComponent(txtUpdatePassengerCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                                .addGap(507, 507, 507)
                                                .addComponent(btnUpdatePassengerInfo)))
                                .addContainerGap(555, Short.MAX_VALUE))
        );
        panelUpdateInfoLayout.setVerticalGroup(
                panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUpdateInfoLayout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerId)
                                        .addComponent(txtUpdatePassengerId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(39, 39, 39)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerFirstName)
                                        .addComponent(txtUpdatePassengerFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerLastName)
                                        .addComponent(txtUpdatePassengerLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerBirthDate)
                                        .addComponent(txtUpdatePassengerBirthYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboUpdatePassengerBirthMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboUpdatePassengerBirthDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerPhone)
                                        .addComponent(lblUpdatePassengerPhoneCodePrefix)
                                        .addComponent(txtUpdatePassengerPhoneCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblUpdatePassengerPhoneSeparator)
                                        .addComponent(txtUpdatePassengerPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29)
                                .addGroup(panelUpdateInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUpdatePassengerCountry)
                                        .addComponent(txtUpdatePassengerCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addComponent(btnUpdatePassengerInfo)
                                .addGap(113, 113, 113))
        );

        jTabbedPane1.addTab("Update info", panelUpdateInfo);

        txtPassengerId_AddToFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        txtPassengerId_AddToFlight.setEnabled(false);

        lblPassengerId_AddToFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblPassengerId_AddToFlight.setText("ID:");

        lblFlight_AddToFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlight_AddToFlight.setText("Flight:");

        comboFlight_AddToFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlight_AddToFlight.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flight" }));

        btnAddPassengerToFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnAddPassengerToFlight.setText("Add");
        btnAddPassengerToFlight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPassengerToFlightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAddToFlightLayout = new javax.swing.GroupLayout(panelAddToFlight);
        panelAddToFlight.setLayout(panelAddToFlightLayout);
        panelAddToFlightLayout.setHorizontalGroup(
                panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAddToFlightLayout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addGroup(panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblPassengerId_AddToFlight)
                                        .addComponent(lblFlight_AddToFlight))
                                .addGap(79, 79, 79)
                                .addGroup(panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(comboFlight_AddToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPassengerId_AddToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(829, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAddToFlightLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddPassengerToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(509, 509, 509))
        );
        panelAddToFlightLayout.setVerticalGroup(
                panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelAddToFlightLayout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelAddToFlightLayout.createSequentialGroup()
                                                .addGap(3, 3, 3)
                                                .addComponent(lblPassengerId_AddToFlight))
                                        .addComponent(txtPassengerId_AddToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addGroup(panelAddToFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFlight_AddToFlight)
                                        .addComponent(comboFlight_AddToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 288, Short.MAX_VALUE)
                                .addComponent(btnAddPassengerToFlight, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85))
        );

        jTabbedPane1.addTab("Add to flight", panelAddToFlight);

        tableMyFlights.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tableMyFlights.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                },
                new String [] {
                        "ID", "Departure Date", "Arrival Date"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneMyFlights.setViewportView(tableMyFlights);

        btnRefreshMyFlights.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshMyFlights.setText("Refresh");
        btnRefreshMyFlights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshMyFlightsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelShowMyFlightsLayout = new javax.swing.GroupLayout(panelShowMyFlights);
        panelShowMyFlights.setLayout(panelShowMyFlightsLayout);
        panelShowMyFlightsLayout.setHorizontalGroup(
                panelShowMyFlightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowMyFlightsLayout.createSequentialGroup()
                                .addGap(269, 269, 269)
                                .addComponent(scrollPaneMyFlights, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(291, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelShowMyFlightsLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRefreshMyFlights)
                                .addGap(527, 527, 527))
        );
        panelShowMyFlightsLayout.setVerticalGroup(
                panelShowMyFlightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowMyFlightsLayout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(scrollPaneMyFlights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                .addComponent(btnRefreshMyFlights)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Show my flights", panelShowMyFlights);

        tableAllPassengers.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tableAllPassengers.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "ID", "Name", "Birthdate", "Age", "Phone", "Country", "Num Flight"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneAllPassengers.setViewportView(tableAllPassengers);

        btnRefreshAllPassengers.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshAllPassengers.setText("Refresh");
        btnRefreshAllPassengers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAllPassengersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelShowPassengersLayout = new javax.swing.GroupLayout(panelShowPassengers);
        panelShowPassengers.setLayout(panelShowPassengersLayout);
        panelShowPassengersLayout.setHorizontalGroup(
                panelShowPassengersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowPassengersLayout.createSequentialGroup()
                                .addGroup(panelShowPassengersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelShowPassengersLayout.createSequentialGroup()
                                                .addGap(489, 489, 489)
                                                .addComponent(btnRefreshAllPassengers))
                                        .addGroup(panelShowPassengersLayout.createSequentialGroup()
                                                .addGap(47, 47, 47)
                                                .addComponent(scrollPaneAllPassengers, javax.swing.GroupLayout.PREFERRED_SIZE, 1078, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelShowPassengersLayout.setVerticalGroup(
                panelShowPassengersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelShowPassengersLayout.createSequentialGroup()
                                .addContainerGap(72, Short.MAX_VALUE)
                                .addComponent(scrollPaneAllPassengers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefreshAllPassengers)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Show all passengers", panelShowPassengers);

        tableAllFlights.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tableAllFlights.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "ID", "Departure Airport ID", "Arrival Airport ID", "Scale Airport ID", "Departure Date", "Arrival Date", "Plane ID", "Number Passengers"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneAllFlights.setViewportView(tableAllFlights);

        btnRefreshAllFlights.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshAllFlights.setText("Refresh");
        btnRefreshAllFlights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAllFlightsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelShowFlightsLayout = new javax.swing.GroupLayout(panelShowFlights);
        panelShowFlights.setLayout(panelShowFlightsLayout);
        panelShowFlightsLayout.setHorizontalGroup(
                panelShowFlightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowFlightsLayout.createSequentialGroup()
                                .addGroup(panelShowFlightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelShowFlightsLayout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(scrollPaneAllFlights, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelShowFlightsLayout.createSequentialGroup()
                                                .addGap(521, 521, 521)
                                                .addComponent(btnRefreshAllFlights)))
                                .addContainerGap(21, Short.MAX_VALUE))
        );
        panelShowFlightsLayout.setVerticalGroup(
                panelShowFlightsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowFlightsLayout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(scrollPaneAllFlights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefreshAllFlights)
                                .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Show all flights", panelShowFlights);

        btnRefreshAllPlanes.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshAllPlanes.setText("Refresh");
        btnRefreshAllPlanes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAllPlanesActionPerformed(evt);
            }
        });

        tableAllPlanes.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "ID", "Brand", "Model", "Max Capacity", "Airline", "Number Flights"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneAllPlanes.setViewportView(tableAllPlanes);

        javax.swing.GroupLayout panelShowPlanesLayout = new javax.swing.GroupLayout(panelShowPlanes);
        panelShowPlanes.setLayout(panelShowPlanesLayout);
        panelShowPlanesLayout.setHorizontalGroup(
                panelShowPlanesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowPlanesLayout.createSequentialGroup()
                                .addGroup(panelShowPlanesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelShowPlanesLayout.createSequentialGroup()
                                                .addGap(508, 508, 508)
                                                .addComponent(btnRefreshAllPlanes))
                                        .addGroup(panelShowPlanesLayout.createSequentialGroup()
                                                .addGap(145, 145, 145)
                                                .addComponent(scrollPaneAllPlanes, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(189, Short.MAX_VALUE))
        );
        panelShowPlanesLayout.setVerticalGroup(
                panelShowPlanesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelShowPlanesLayout.createSequentialGroup()
                                .addContainerGap(45, Short.MAX_VALUE)
                                .addComponent(scrollPaneAllPlanes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(btnRefreshAllPlanes)
                                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Show all planes", panelShowPlanes);

        tableAllLocations.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Airport ID", "Airport Name", "City", "Country"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPaneAllLocations.setViewportView(tableAllLocations);

        btnRefreshAllLocations.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnRefreshAllLocations.setText("Refresh");
        btnRefreshAllLocations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAllLocationsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelShowLocationsLayout = new javax.swing.GroupLayout(panelShowLocations);
        panelShowLocations.setLayout(panelShowLocationsLayout);
        panelShowLocationsLayout.setHorizontalGroup(
                panelShowLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelShowLocationsLayout.createSequentialGroup()
                                .addGroup(panelShowLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelShowLocationsLayout.createSequentialGroup()
                                                .addGap(508, 508, 508)
                                                .addComponent(btnRefreshAllLocations))
                                        .addGroup(panelShowLocationsLayout.createSequentialGroup()
                                                .addGap(226, 226, 226)
                                                .addComponent(scrollPaneAllLocations, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(272, Short.MAX_VALUE))
        );
        panelShowLocationsLayout.setVerticalGroup(
                panelShowLocationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelShowLocationsLayout.createSequentialGroup()
                                .addContainerGap(48, Short.MAX_VALUE)
                                .addComponent(scrollPaneAllLocations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnRefreshAllLocations)
                                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Show all locations", panelShowLocations);

        comboDelayFlightHours.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboDelayFlightHours.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        lblDelayFlightHours.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDelayFlightHours.setText("Hours:");

        lblFlightId_DelayFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblFlightId_DelayFlight.setText("ID:");

        comboFlightId_DelayFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboFlightId_DelayFlight.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID" }));

        lblDelayFlightMinutes.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lblDelayFlightMinutes.setText("Minutes:");

        comboDelayFlightMinutes.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        comboDelayFlightMinutes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        btnDelayFlight.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        btnDelayFlight.setText("Delay");
        btnDelayFlight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelayFlightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDelayFlightLayout = new javax.swing.GroupLayout(panelDelayFlight);
        panelDelayFlight.setLayout(panelDelayFlightLayout);
        panelDelayFlightLayout.setHorizontalGroup(
                panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelDelayFlightLayout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelDelayFlightLayout.createSequentialGroup()
                                                .addComponent(lblDelayFlightMinutes)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(comboDelayFlightMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelDelayFlightLayout.createSequentialGroup()
                                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFlightId_DelayFlight)
                                                        .addComponent(lblDelayFlightHours))
                                                .addGap(79, 79, 79)
                                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(comboDelayFlightHours, 0, 105, Short.MAX_VALUE)
                                                        .addComponent(comboFlightId_DelayFlight, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(820, 820, 820))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDelayFlightLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDelayFlight)
                                .addGap(531, 531, 531))
        );
        panelDelayFlightLayout.setVerticalGroup(
                panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelDelayFlightLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFlightId_DelayFlight)
                                        .addComponent(comboFlightId_DelayFlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDelayFlightHours)
                                        .addComponent(comboDelayFlightHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(panelDelayFlightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDelayFlightMinutes)
                                        .addComponent(comboDelayFlightMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
                                .addComponent(btnDelayFlight)
                                .addGap(33, 33, 33))
        );

        jTabbedPane1.addTab("Delay flight", panelDelayFlight);

        panelRound1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 41, 1150, 620));

        javax.swing.GroupLayout panelRound3Layout = new javax.swing.GroupLayout(panelRound3);
        panelRound3.setLayout(panelRound3Layout);
        panelRound3Layout.setHorizontalGroup(
                panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1150, Short.MAX_VALUE)
        );
        panelRound3Layout.setVerticalGroup(
                panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 36, Short.MAX_VALUE)
        );

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 660, 1150, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelRound1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelRound1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void panelRound2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void panelRound2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_panelRound2MouseDragged

    //----------------------------------------------------------------------------
// ROLE = Administrator
//----------------------------------------------------------------------------
    private void radioUserTypeAdminActionPerformed(java.awt.event.ActionEvent evt) {
        // Si estaba marcado User, desmarcarlo y reset combo user
        if (radioUserTypeUser.isSelected()) {
            radioUserTypeUser.setSelected(false);
            comboSelectUser.setSelectedIndex(0);
        }
        // Habilitar todas las pestañas excepto 5,6,7
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            boolean enable = (i != 5 && i != 6 && i != 7);
            jTabbedPane1.setEnabledAt(i, enable);
        }
        // Si el usuario estaba en una pestaña prohibida, regresar a Administration (0)
        int sel = jTabbedPane1.getSelectedIndex();
        if (sel == 5 || sel == 6 || sel == 7) {
            jTabbedPane1.setSelectedIndex(0);
        }
    }

    //----------------------------------------------------------------------------
// ROLE = User
//----------------------------------------------------------------------------
    private void radioUserTypeUserActionPerformed(java.awt.event.ActionEvent evt) {
        // Si estaba marcado Admin, desmarcarlo
        if (radioUserTypeAdmin.isSelected()) {
            radioUserTypeAdmin.setSelected(false);
        }
        // Definimos el conjunto de pestañas permitidas para User
        // 0=Admin, 5=Update Info, 6=Add to Flight, 7=Show my Flights,
        // 9=Show all Flights, 11=Show all Locations
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            boolean enable = (i == 0 || i == 5 || i == 6 || i == 7 || i == 9 || i == 11);
            jTabbedPane1.setEnabledAt(i, enable);
        }
    }

    private void btnRegisterPassengerActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            long id     = Long.parseLong(txtPassengerId.getText().trim());
            String fn   = txtPassengerFirstName.getText().trim();
            String ln   = txtPassengerLastName.getText().trim();
            int  y      = Integer.parseInt(txtPassengerBirthYear.getText().trim());
            int  m      = Integer.parseInt(comboPassengerBirthMonth.getSelectedItem().toString());
            int  d      = Integer.parseInt(comboPassengerBirthDay.getSelectedItem().toString());
            int  code   = Integer.parseInt(txtPassengerPhoneCode.getText().trim());
            long ph     = Long.parseLong(txtPassengerPhoneNumber.getText().trim());
            String ctr  = txtPassengerCountry.getText().trim();

            Response<Passenger> resp = passengerController.registerPassenger(
                    id, fn, ln, y, m, d, code, ph, ctr
            );

            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // limpiar campos...
                txtPassengerId.setText("");
                txtPassengerFirstName.setText("");
                txtPassengerLastName.setText("");
                txtPassengerBirthYear.setText("");
                comboPassengerBirthMonth.setSelectedIndex(0);
                comboPassengerBirthDay.setSelectedIndex(0);
                txtPassengerPhoneCode.setText("");
                txtPassengerPhoneNumber.setText("");
                txtPassengerCountry.setText("");
                // actualizar combo Admin
                comboSelectUser.addItem(String.valueOf(resp.getData().getId()));
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise que los campos numéricos estén correctos",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnRegisterPassengerActionPerformed


    private void btnCreatePlaneActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String id   = txtPlaneId.getText().trim();
            String br   = txtPlaneBrand.getText().trim();
            String mo   = txtPlaneModel.getText().trim();
            int cap     = Integer.parseInt(txtPlaneMaxCapacity.getText().trim());
            String al   = txtPlaneAirline.getText().trim();

            Response<Plane> resp = planeController.createPlane(
                    id, br, mo, cap, al
            );
            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // limpiar
                txtPlaneId.setText("");
                txtPlaneBrand.setText("");
                txtPlaneModel.setText("");
                txtPlaneMaxCapacity.setText("");
                txtPlaneAirline.setText("");
                // combo Vuelo
                comboFlightPlane.addItem(resp.getData().getId());
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise que Max Capacity sea un entero válido",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void btnCreateLocationActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String id   = txtLocationAirportId.getText().trim();
            String nm   = txtLocationAirportName.getText().trim();
            String ci   = txtLocationAirportCity.getText().trim();
            String co   = txtLocationAirportCountry.getText().trim();
            double la   = Double.parseDouble(txtLocationAirportLatitude.getText().trim());
            double lo   = Double.parseDouble(txtLocationAirportLongitude.getText().trim());

            Response<Location> resp = locationController.createLocation(
                    id, nm, ci, co, la, lo
            );
            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // limpiar
                txtLocationAirportId.setText("");
                txtLocationAirportName.setText("");
                txtLocationAirportCity.setText("");
                txtLocationAirportCountry.setText("");
                txtLocationAirportLatitude.setText("");
                txtLocationAirportLongitude.setText("");
                // combos Flight
                comboFlightDepartureLocation.addItem(resp.getData().getAirportId());
                comboFlightArrivalLocation  .addItem(resp.getData().getAirportId());
                comboFlightScaleLocation    .addItem(resp.getData().getAirportId());
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise latitud/longitud",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnCreateLocationActionPerformed

    private void btnCreateFlightActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String fid    = txtFlightId.getText().trim();
            String pid    = comboFlightPlane.getSelectedItem().toString();
            String depId  = comboFlightDepartureLocation.getSelectedItem().toString();
            String arrId  = comboFlightArrivalLocation.getSelectedItem().toString();
            String scaId  = comboFlightScaleLocation.getSelectedItem().toString();

            int y         = Integer.parseInt(txtFlightDepartureYear.getText().trim());
            int mo        = Integer.parseInt(comboFlightDepartureMonth.getSelectedItem().toString());
            int d         = Integer.parseInt(comboFlightDepartureDay.getSelectedItem().toString());
            int h         = Integer.parseInt(comboFlightDepartureHour.getSelectedItem().toString());
            int min       = Integer.parseInt(comboFlightDepartureMinute.getSelectedItem().toString());
            int arrH      = Integer.parseInt(comboFlightArrivalDurationHour.getSelectedItem().toString());
            int arrM      = Integer.parseInt(comboFlightArrivalDurationMinute.getSelectedItem().toString());
            int scH       = Integer.parseInt(comboFlightScaleDurationHour.getSelectedItem().toString());
            int scM       = Integer.parseInt(comboFlightScaleDurationMinute.getSelectedItem().toString());

            Response<Flight> resp = flightController.createFlight(
                    fid, pid, depId, arrId, scaId,
                    y, mo, d,
                    h, min,
                    arrH, arrM,
                    scH, scM
            );

            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // limpiar
                txtFlightId.setText("");
                txtFlightDepartureYear .setText("");
                comboFlightDepartureMonth .setSelectedIndex(0);
                comboFlightDepartureDay   .setSelectedIndex(0);
                comboFlightDepartureHour  .setSelectedIndex(0);
                comboFlightDepartureMinute.setSelectedIndex(0);
                comboFlightArrivalDurationHour   .setSelectedIndex(0);
                comboFlightArrivalDurationMinute .setSelectedIndex(0);
                comboFlightScaleDurationHour     .setSelectedIndex(0);
                comboFlightScaleDurationMinute   .setSelectedIndex(0);
                // combos Add & Delay
                comboFlight_AddToFlight   .addItem(resp.getData().getId());
                comboFlightId_DelayFlight .addItem(resp.getData().getId());
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise formato de campos numéricos",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnCreateFlightActionPerformed

    private void btnUpdatePassengerInfoActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            long id     = Long.parseLong(txtUpdatePassengerId.getText());
            String fn   = txtUpdatePassengerFirstName.getText();
            String ln   = txtUpdatePassengerLastName.getText();
            int  y      = Integer.parseInt(txtUpdatePassengerBirthYear.getText());
            int  m      = Integer.parseInt(comboUpdatePassengerBirthMonth.getItemAt(comboUpdatePassengerBirthMonth.getSelectedIndex()));
            int  d      = Integer.parseInt(comboUpdatePassengerBirthDay.getItemAt(comboUpdatePassengerBirthDay.getSelectedIndex()));
            int  code   = Integer.parseInt(txtUpdatePassengerPhoneCode.getText());
            long ph     = Long.parseLong(txtUpdatePassengerPhoneNumber.getText());
            String ctr  = txtUpdatePassengerCountry.getText();

            Response<Passenger> resp = passengerController.updatePassenger(
                    id, fn, ln, y, m, d, code, ph, ctr
            );

            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // opcional: refrescar comboSelectUser
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise que todos los campos numéricos estén bien formados",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnUpdatePassengerInfoActionPerformed

    private void btnAddPassengerToFlightActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            long pid = Long.parseLong(txtPassengerId_AddToFlight.getText());
            String fid = comboFlight_AddToFlight.getItemAt(comboFlight_AddToFlight.getSelectedIndex());

            Response<Flight> resp = flightController.addPassengerToFlight(fid, pid);
            if (resp.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this,
                        resp.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Revise que el ID de pasajero esté bien formado",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnAddPassengerToFlightActionPerformed

    private void btnDelayFlightActionPerformed(java.awt.event.ActionEvent evt) {
        String fid = comboFlightId_DelayFlight.getItemAt(comboFlightId_DelayFlight.getSelectedIndex());
        int hrs = Integer.parseInt(comboDelayFlightHours.getItemAt(comboDelayFlightHours.getSelectedIndex()));
        int mins = Integer.parseInt(comboDelayFlightMinutes.getItemAt(comboDelayFlightMinutes.getSelectedIndex()));

        Response<Flight> resp = flightController.delayFlight(fid, hrs, mins);
        if (resp.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnDelayFlightActionPerformed

    private void btnRefreshMyFlightsActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (comboSelectUser.getSelectedIndex() == 0 && radioUserTypeUser.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, seleccione un usuario para ver sus vuelos.",
                        "Usuario no seleccionado",
                        JOptionPane.WARNING_MESSAGE);
                DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
                model.setRowCount(0); // Clear table if no user is selected
                return;
            }
            long pid = Long.parseLong(comboSelectUser.getItemAt(comboSelectUser.getSelectedIndex()));
            Response<List<Flight>> resp = flightController.getFlightsByPassenger(pid);
            if (resp.isSuccess()) {
                DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
                model.setRowCount(0);
                for (Flight f : resp.getData()) {
                    model.addRow(new Object[]{
                            f.getId(),
                            f.getDepartureDate(),
                            f.calculateArrivalDate()
                    });
                }
            } else {
                DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
                model.setRowCount(0); // Clear table on error too or if passenger has no flights
                // Optionally show error only if it's not a NOT_FOUND for a passenger with no flights
                if (resp.getStatus() != StatusCode.NOT_FOUND && !(resp.getStatus() == StatusCode.OK && resp.getData().isEmpty())) {
                    JOptionPane.showMessageDialog(this,
                            resp.getMessage(),
                            "Error al obtener mis vuelos",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un Pasajero válido",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE
            );
            DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
            model.setRowCount(0);
        }  catch (ArrayIndexOutOfBoundsException ex) {
            // This can happen if "Select User" (index 0) is chosen and parsing is attempted.
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un usuario válido de la lista.",
                    "Usuario no válido",
                    JOptionPane.WARNING_MESSAGE);
            DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
            model.setRowCount(0);
        }
    }
    //GEN-LAST:event_btnRefreshMyFlightsActionPerformed

    private void btnRefreshAllPassengersActionPerformed(java.awt.event.ActionEvent evt) {
        Response<List<Passenger>> resp = passengerController.getAllPassengers();
        if (resp.isSuccess()) {
            DefaultTableModel model = (DefaultTableModel) tableAllPassengers.getModel();
            model.setRowCount(0);
            for (Passenger p : resp.getData()) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getFullname(),
                        p.getBirthDate(),
                        p.calculateAge(),
                        p.generateFullPhone(),
                        p.getCountry(),
                        p.getNumFlights()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Error al obtener pasajeros",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnRefreshAllPassengersActionPerformed

    private void btnRefreshAllFlightsActionPerformed(java.awt.event.ActionEvent evt) {
        Response<List<Flight>> resp = flightController.getAllFlights();
        if (resp.isSuccess()) {
            DefaultTableModel model = (DefaultTableModel) tableAllFlights.getModel();
            model.setRowCount(0);
            for (Flight f : resp.getData()) {
                model.addRow(new Object[]{
                        f.getId(),
                        f.getDepartureLocation().getAirportId(),
                        f.getArrivalLocation().getAirportId(),
                        f.getScaleLocation() == null ? "" : f.getScaleLocation().getAirportId(),
                        f.getDepartureDate(),
                        f.calculateArrivalDate(),
                        f.getPlane().getId(),
                        f.getNumPassengers()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Error al obtener vuelos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //GEN-LAST:event_btnRefreshAllFlightsActionPerformed

    private void btnRefreshAllPlanesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAllPlanesActionPerformed
        // 1) Invocar al controller
        Response<List<Plane>> resp = planeController.getAllPlanes();

        // 2) Procesar respuesta
        if (resp.isSuccess()) {
            DefaultTableModel model = (DefaultTableModel) tableAllPlanes.getModel();
            model.setRowCount(0);

            for (Plane plane : resp.getData()) {
                model.addRow(new Object[]{
                        plane.getId(),
                        plane.getBrand(),
                        plane.getModel(),
                        plane.getMaxCapacity(),
                        plane.getAirline(),
                        plane.getNumFlights()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Error al obtener aviones",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnRefreshAllPlanesActionPerformed


    private void btnRefreshAllLocationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAllLocationsActionPerformed
        // 1) Invocar al controller
        Response<List<Location>> resp = locationController.getAllLocations();

        // 2) Procesar respuesta
        if (resp.isSuccess()) {
            DefaultTableModel model = (DefaultTableModel) tableAllLocations.getModel();
            model.setRowCount(0);

            for (Location loc : resp.getData()) {
                model.addRow(new Object[]{
                        loc.getAirportId(),
                        loc.getAirportName(),
                        loc.getAirportCity(),
                        loc.getAirportCountry()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resp.getMessage(),
                    "Error al obtener localizaciones",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnRefreshAllLocationsActionPerformed


    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void comboSelectUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSelectUserActionPerformed
        try {
            String id = comboSelectUser.getSelectedItem().toString();
            if (! id.equals(comboSelectUser.getItemAt(0))) { // Check if "Select User" is not selected
                txtUpdatePassengerId.setText(id);
                txtPassengerId_AddToFlight.setText(id);
                // Automatically refresh "My Flights" if the tab is visible for the selected user
                if (radioUserTypeUser.isSelected() && jTabbedPane1.getSelectedComponent() == panelShowMyFlights) {
                    btnRefreshMyFlightsActionPerformed(null);
                }
            }
            else{
                txtUpdatePassengerId.setText("");
                txtPassengerId_AddToFlight.setText("");
                // Clear "My Flights" table if "Select User" is chosen
                DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
                model.setRowCount(0);
            }
        } catch (Exception e) {
            // Catch potential null pointer if item is null, though unlikely with current setup
            txtUpdatePassengerId.setText("");
            txtPassengerId_AddToFlight.setText("");
            DefaultTableModel model = (DefaultTableModel) tableMyFlights.getModel();
            model.setRowCount(0);
        }
    }//GEN-LAST:event_comboSelectUserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddPassengerToFlight;
    private javax.swing.JButton btnCreateFlight;
    private javax.swing.JButton btnCreateLocation;
    private javax.swing.JButton btnCreatePlane;
    private javax.swing.JButton btnDelayFlight;
    private javax.swing.JButton btnRefreshAllFlights;
    private javax.swing.JButton btnRefreshAllLocations;
    private javax.swing.JButton btnRefreshAllPassengers;
    private javax.swing.JButton btnRefreshAllPlanes;
    private javax.swing.JButton btnRefreshMyFlights;
    private javax.swing.JButton btnRegisterPassenger;
    private javax.swing.JButton btnUpdatePassengerInfo;
    private javax.swing.JComboBox<String> comboDelayFlightHours;
    private javax.swing.JComboBox<String> comboDelayFlightMinutes;
    private javax.swing.JComboBox<String> comboFlightArrivalDurationHour;
    private javax.swing.JComboBox<String> comboFlightArrivalDurationMinute;
    private javax.swing.JComboBox<String> comboFlightArrivalLocation;
    private javax.swing.JComboBox<String> comboFlightDepartureDay;
    private javax.swing.JComboBox<String> comboFlightDepartureHour;
    private javax.swing.JComboBox<String> comboFlightDepartureLocation;
    private javax.swing.JComboBox<String> comboFlightDepartureMinute;
    private javax.swing.JComboBox<String> comboFlightDepartureMonth;
    private javax.swing.JComboBox<String> comboFlightId_DelayFlight;
    private javax.swing.JComboBox<String> comboFlightPlane;
    private javax.swing.JComboBox<String> comboFlightScaleDurationHour;
    private javax.swing.JComboBox<String> comboFlightScaleDurationMinute;
    private javax.swing.JComboBox<String> comboFlightScaleLocation;
    private javax.swing.JComboBox<String> comboFlight_AddToFlight;
    private javax.swing.JComboBox<String> comboPassengerBirthDay;
    private javax.swing.JComboBox<String> comboPassengerBirthMonth;
    private javax.swing.JComboBox<String> comboSelectUser;
    private javax.swing.JComboBox<String> comboUpdatePassengerBirthDay;
    private javax.swing.JComboBox<String> comboUpdatePassengerBirthMonth;
    private javax.swing.JButton jButton13;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDelayFlightHours;
    private javax.swing.JLabel lblDelayFlightMinutes;
    private javax.swing.JLabel lblFlightArrivalDurationSeparator;
    private javax.swing.JLabel lblFlightArrivalLocation;
    private javax.swing.JLabel lblFlightDateSeparator1;
    private javax.swing.JLabel lblFlightDateSeparator2;
    private javax.swing.JLabel lblFlightDepartureDate;
    private javax.swing.JLabel lblFlightDepartureLocation;
    private javax.swing.JLabel lblFlightDurationArrival;
    private javax.swing.JLabel lblFlightDurationScale;
    private javax.swing.JLabel lblFlightId;
    private javax.swing.JLabel lblFlightId_DelayFlight;
    private javax.swing.JLabel lblFlightPlane;
    private javax.swing.JLabel lblFlightScaleDurationSeparator;
    private javax.swing.JLabel lblFlightScaleLocation;
    private javax.swing.JLabel lblFlightTimeSeparator1;
    private javax.swing.JLabel lblFlightTimeSeparator2;
    private javax.swing.JLabel lblFlight_AddToFlight;
    private javax.swing.JLabel lblLocationAirportCity;
    private javax.swing.JLabel lblLocationAirportCountry;
    private javax.swing.JLabel lblLocationAirportId;
    private javax.swing.JLabel lblLocationAirportLatitude;
    private javax.swing.JLabel lblLocationAirportLongitude;
    private javax.swing.JLabel lblLocationAirportName;
    private javax.swing.JLabel lblPassengerBirthDate;
    private javax.swing.JLabel lblPassengerBirthDateSeparator1;
    private javax.swing.JLabel lblPassengerBirthDateSeparator2;
    private javax.swing.JLabel lblPassengerCountry;
    private javax.swing.JLabel lblPassengerFirstName;
    private javax.swing.JLabel lblPassengerId;
    private javax.swing.JLabel lblPassengerId_AddToFlight;
    private javax.swing.JLabel lblPassengerLastName;
    private javax.swing.JLabel lblPassengerPhone;
    private javax.swing.JLabel lblPassengerPhoneCodePrefix;
    private javax.swing.JLabel lblPassengerPhoneSeparator;
    private javax.swing.JLabel lblPlaneAirline;
    private javax.swing.JLabel lblPlaneBrand;
    private javax.swing.JLabel lblPlaneId;
    private javax.swing.JLabel lblPlaneMaxCapacity;
    private javax.swing.JLabel lblPlaneModel;
    private javax.swing.JLabel lblUpdatePassengerBirthDate;
    private javax.swing.JLabel lblUpdatePassengerCountry;
    private javax.swing.JLabel lblUpdatePassengerFirstName;
    private javax.swing.JLabel lblUpdatePassengerId;
    private javax.swing.JLabel lblUpdatePassengerLastName;
    private javax.swing.JLabel lblUpdatePassengerPhone;
    private javax.swing.JLabel lblUpdatePassengerPhoneCodePrefix;
    private javax.swing.JLabel lblUpdatePassengerPhoneSeparator;
    private javax.swing.JPanel panelAddToFlight;
    private javax.swing.JPanel panelAdministration;
    private javax.swing.JPanel panelDelayFlight;
    private javax.swing.JPanel panelFlightRegistration;
    private javax.swing.JPanel panelLocationRegistration;
    private javax.swing.JPanel panelPassengerRegistration;
    private javax.swing.JPanel panelPlaneRegistration;
    private airport.view.PanelRound panelRound1;
    private airport.view.PanelRound panelRound2;
    private airport.view.PanelRound panelRound3;
    private javax.swing.JPanel panelShowFlights;
    private javax.swing.JPanel panelShowLocations;
    private javax.swing.JPanel panelShowMyFlights;
    private javax.swing.JPanel panelShowPassengers;
    private javax.swing.JPanel panelShowPlanes;
    private javax.swing.JPanel panelUpdateInfo;
    private javax.swing.JRadioButton radioUserTypeAdmin;
    private javax.swing.JRadioButton radioUserTypeUser;
    private javax.swing.JScrollPane scrollPaneAllFlights;
    private javax.swing.JScrollPane scrollPaneAllLocations;
    private javax.swing.JScrollPane scrollPaneAllPassengers;
    private javax.swing.JScrollPane scrollPaneAllPlanes;
    private javax.swing.JScrollPane scrollPaneMyFlights;
    private javax.swing.JTable tableAllFlights;
    private javax.swing.JTable tableAllLocations;
    private javax.swing.JTable tableAllPassengers;
    private javax.swing.JTable tableAllPlanes;
    private javax.swing.JTable tableMyFlights;
    private javax.swing.JTextField txtFlightDepartureYear;
    private javax.swing.JTextField txtFlightId;
    private javax.swing.JTextField txtLocationAirportCity;
    private javax.swing.JTextField txtLocationAirportCountry;
    private javax.swing.JTextField txtLocationAirportId;
    private javax.swing.JTextField txtLocationAirportLatitude;
    private javax.swing.JTextField txtLocationAirportLongitude;
    private javax.swing.JTextField txtLocationAirportName;
    private javax.swing.JTextField txtPassengerBirthYear;
    private javax.swing.JTextField txtPassengerCountry;
    private javax.swing.JTextField txtPassengerFirstName;
    private javax.swing.JTextField txtPassengerId;
    private javax.swing.JTextField txtPassengerId_AddToFlight;
    private javax.swing.JTextField txtPassengerLastName;
    private javax.swing.JTextField txtPassengerPhoneCode;
    private javax.swing.JTextField txtPassengerPhoneNumber;
    private javax.swing.JTextField txtPlaneAirline;
    private javax.swing.JTextField txtPlaneBrand;
    private javax.swing.JTextField txtPlaneId;
    private javax.swing.JTextField txtPlaneMaxCapacity;
    private javax.swing.JTextField txtPlaneModel;
    private javax.swing.JTextField txtUpdatePassengerBirthYear;
    private javax.swing.JTextField txtUpdatePassengerCountry;
    private javax.swing.JTextField txtUpdatePassengerFirstName;
    private javax.swing.JTextField txtUpdatePassengerId;
    private javax.swing.JTextField txtUpdatePassengerLastName;
    private javax.swing.JTextField txtUpdatePassengerPhoneCode;
    private javax.swing.JTextField txtUpdatePassengerPhoneNumber;
    // End of variables declaration//GEN-END:variables
}
