package es.urjc.metprog.gui;

import es.urjc.metprog.app.SistemaFacade;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;
import es.urjc.metprog.domain.persistence.MovimientoOro;
import es.urjc.metprog.domain.ranking.EntradaRanking;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.Operador;
import es.urjc.metprog.domain.user.Usuario;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class FantasyCombatFrame extends javax.swing.JFrame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SistemaFacade sistema = new SistemaFacade();
    private final Ui.BackgroundPanel root = new Ui.BackgroundPanel();
    private String currentScreen = "dashboard";

    FantasyCombatFrame() {
        super("MetProg Combate Fantastico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1320, 820);
        setLocationRelativeTo(null);
        setContentPane(root);
        showPublic();
    }

    private void showPublic() {
        currentScreen = "public";
        root.removeAll();
        root.add(publicPanel(), BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    private JPanel publicPanel() {
        JPanel outer = Ui.transparent(new BorderLayout(32, 32));
        outer.setBorder(javax.swing.BorderFactory.createEmptyBorder(44, 58, 44, 58));

        JPanel hero = Ui.transparent(new BorderLayout(16, 18));
        JLabel title = Ui.title("MetProg Combate Fantastico", 34f);
        JLabel subtitle = Ui.small("Arena grafica para jugadores y operadores");
        Ui.ArenaPanel arena = new Ui.ArenaPanel();
        JPanel titleBlock = Ui.transparent(new BorderLayout(6, 6));
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);
        hero.add(titleBlock, BorderLayout.NORTH);
        hero.add(arena, BorderLayout.CENTER);

        JPanel auth = Ui.card();
        auth.setPreferredSize(new Dimension(420, 420));
        auth.add(Ui.title("Acceso", 26f), BorderLayout.NORTH);

        JTextField nick = Ui.textField();
        JPasswordField password = Ui.passwordField();
        Ui.Form form = new Ui.Form();
        form.addRow("Nick", nick);
        form.addRow("Password", password);

        JButton login = Ui.primaryButton("Iniciar sesion");
        JButton registerPlayer = Ui.secondaryButton("Registrar jugador");
        JButton registerOperator = Ui.secondaryButton("Registrar operador");
        login.addActionListener(event -> runAction(null, () -> {
            Usuario user = sistema.iniciarSesion(Ui.text(nick), Ui.password(password));
            if (user.isBloqueado()) {
                info("La cuenta esta bloqueada. Motivo: " + user.getMotivoBloqueo());
            }
            showApp("dashboard");
        }));
        registerPlayer.addActionListener(event -> registerPlayer());
        registerOperator.addActionListener(event -> registerOperator());

        JPanel buttons = Ui.transparent(new GridLayout(3, 1, 0, 10));
        buttons.add(login);
        buttons.add(registerPlayer);
        buttons.add(registerOperator);

        JPanel authBody = Ui.transparent(new BorderLayout(12, 16));
        authBody.add(form, BorderLayout.NORTH);
        authBody.add(buttons, BorderLayout.CENTER);
        auth.add(authBody, BorderLayout.CENTER);

        outer.add(hero, BorderLayout.CENTER);
        outer.add(auth, BorderLayout.EAST);
        return outer;
    }

    private void registerPlayer() {
        JTextField name = Ui.textField();
        JTextField nick = Ui.textField();
        JPasswordField password = Ui.passwordField();
        Ui.Form form = new Ui.Form();
        form.addRow("Nombre", name);
        form.addRow("Nick", nick);
        form.addRow("Password 8-12", password);
        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(form, BorderLayout.CENTER);
        if (Ui.confirmDialog(this, "Registrar jugador", root)) {
            runAction("Jugador registrado", () -> {
                Jugador player = sistema.registrarJugador(Ui.text(name), Ui.text(nick), Ui.password(password));
                info("Numero de registro: " + player.getNumeroRegistro());
            });
        }
    }

    private void registerOperator() {
        JTextField name = Ui.textField();
        JTextField nick = Ui.textField();
        JPasswordField password = Ui.passwordField();
        Ui.Form form = new Ui.Form();
        form.addRow("Nombre", name);
        form.addRow("Nick", nick);
        form.addRow("Password", password);
        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(form, BorderLayout.CENTER);
        if (Ui.confirmDialog(this, "Registrar operador", root)) {
            runAction("Operador registrado", () -> sistema.registrarOperador(Ui.text(name), Ui.text(nick), Ui.password(password)));
        }
    }

    private void showApp(String screen) {
        currentScreen = screen;
        root.removeAll();
        root.add(header(), BorderLayout.NORTH);
        root.add(nav(), BorderLayout.WEST);
        root.add(screenWrapper(screenPanel(screen)), BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    private JPanel header() {
        Usuario user = sistema.getUsuarioActual();
        JPanel header = Ui.transparent(new BorderLayout(16, 8));
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 26, 12, 26));

        JPanel title = Ui.transparent(new BorderLayout(4, 2));
        title.add(Ui.title("Arena MetProg", 24f), BorderLayout.NORTH);
        title.add(Ui.small(user.getRol() + " / " + user.getNick() + (user.isBloqueado() ? " / bloqueado" : "")), BorderLayout.SOUTH);

        JButton delete = Ui.dangerButton("Darse de baja");
        JButton logout = Ui.secondaryButton("Cerrar sesion");
        delete.addActionListener(event -> deleteAccount());
        logout.addActionListener(event -> {
            sistema.cerrarSesion();
            showPublic();
        });
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(delete);
        actions.add(logout);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel nav() {
        boolean operator = sistema.getUsuarioActual() instanceof Operador;
        JPanel nav = Ui.transparent(new BorderLayout());
        nav.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 20, 24, 12));
        nav.setPreferredSize(new Dimension(245, 1));

        JPanel stack = Ui.transparent(null);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        addNav(stack, "dashboard", "Panel");
        addNav(stack, operator ? "players" : "character", operator ? "Jugadores" : "Personaje");
        addNav(stack, "challenges", "Desafios");
        addNav(stack, "ranking", "Ranking");
        addNav(stack, "history", "Historial");
        if (!operator) {
            addNav(stack, "gold", "Oro");
        }
        addNav(stack, "notifications", "Notificaciones");
        stack.add(Box.createVerticalGlue());
        nav.add(stack, BorderLayout.CENTER);
        return nav;
    }

    private void addNav(JPanel stack, String screen, String label) {
        JButton button = screen.equals(currentScreen) ? Ui.primaryButton(label) : Ui.secondaryButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.addActionListener(event -> showApp(screen));
        stack.add(button);
        stack.add(Box.createVerticalStrut(10));
    }

    private JScrollPane screenWrapper(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel screenPanel(String screen) {
        try {
            return switch (screen) {
                case "players" -> playersPanel();
                case "character" -> characterPanel();
                case "challenges" -> challengesPanel();
                case "ranking" -> rankingPanel();
                case "history" -> historyPanel();
                case "gold" -> goldPanel();
                case "notifications" -> notificationsPanel();
                default -> dashboardPanel();
            };
        } catch (RuntimeException ex) {
            JPanel error = Ui.card();
            error.add(Ui.title("No se puede mostrar esta vista", 22f), BorderLayout.NORTH);
            error.add(Ui.small(ex.getMessage()), BorderLayout.CENTER);
            return pad(error);
        }
    }

    private JPanel dashboardPanel() {
        if (sistema.getUsuarioActual() instanceof Jugador player) {
            return playerDashboard(player);
        }
        return operatorDashboard();
    }

    private JPanel playerDashboard(Jugador player) {
        JPanel panel = page();
        List<Desafio> pending = sistema.listarDesafiosPendientesJugadorActual();
        Personaje character = player.getPersonaje();

        JPanel metrics = Ui.transparent(new GridLayout(1, 4, 12, 12));
        metrics.add(metric("Registro", player.getNumeroRegistro(), Ui.GOLD));
        metrics.add(metric("Desafios pendientes", String.valueOf(pending.size()), pending.isEmpty() ? Ui.GREEN : Ui.CRIMSON));
        metrics.add(metric("Estado", player.isBloqueado() ? "Bloqueado" : "Activo", player.isBloqueado() ? Ui.CRIMSON : Ui.TEAL));
        metrics.add(metric("Personaje", character == null ? "Sin crear" : character.getTipo().name(), character == null ? Ui.MUTED : Ui.BLUE));
        panel.add(metrics);

        JPanel grid = Ui.transparent(new GridLayout(1, 2, 14, 14));
        grid.add(characterCard(player));
        grid.add(pendingChallengesCard());
        panel.add(grid);
        panel.add(rankingPreviewCard());
        return pad(panel);
    }

    private JPanel operatorDashboard() {
        JPanel panel = page();
        List<Jugador> players = sistema.listarJugadores();
        List<Desafio> pending = sistema.listarDesafiosPendientesRevision();
        JPanel metrics = Ui.transparent(new GridLayout(1, 3, 12, 12));
        metrics.add(metric("Jugadores", String.valueOf(players.size()), Ui.BLUE));
        metrics.add(metric("Desafios a validar", String.valueOf(pending.size()), pending.isEmpty() ? Ui.GREEN : Ui.GOLD));
        metrics.add(metric("Ranking", String.valueOf(sistema.consultarRanking().size()), Ui.TEAL));
        panel.add(metrics);
        JPanel grid = Ui.transparent(new GridLayout(1, 2, 14, 14));
        grid.add(playersTableCard(players));
        grid.add(operatorPendingCard(pending));
        panel.add(grid);
        return pad(panel);
    }

    private JPanel characterPanel() {
        Jugador player = requirePlayer();
        JPanel panel = page();
        panel.add(characterCard(player));
        if (player.tienePersonaje()) {
            JTextArea details = Ui.textArea();
            details.setText(player.getPersonaje().resumenDetallado());
            details.setCaretPosition(0);
            panel.add(Ui.titled("Ficha completa", Ui.scroll(details)));
        }
        return pad(panel);
    }

    private JPanel characterCard(Jugador player) {
        JPanel card = Ui.card();
        card.add(Ui.title("Personaje", 20f), BorderLayout.NORTH);
        JPanel body = Ui.transparent(new BorderLayout(10, 10));
        if (!player.tienePersonaje()) {
            body.add(Ui.small("Todavia no hay personaje registrado."), BorderLayout.CENTER);
            JButton create = Ui.primaryButton("Crear personaje");
            create.addActionListener(event -> CharacterDialog.show(this, "Crear personaje")
                    .ifPresent(config -> runAction("Personaje creado", () -> sistema.crearPersonajeParaJugadorActual(config))));
            body.add(create, BorderLayout.SOUTH);
        } else {
            Personaje p = player.getPersonaje();
            JPanel metrics = Ui.transparent(new GridLayout(2, 3, 8, 8));
            metrics.add(metric("Nombre", p.getNombre(), Ui.GOLD));
            metrics.add(metric("Poder", String.valueOf(p.getPoder()), Ui.BLUE));
            metrics.add(metric("Salud", String.valueOf(p.getSaludActual()), Ui.GREEN));
            metrics.add(metric("Oro", String.valueOf(p.getOro()), Ui.GOLD));
            metrics.add(metric("Ataque eq.", String.valueOf(p.getAtaqueEquipoActivo()), Ui.CRIMSON));
            metrics.add(metric("Defensa eq.", String.valueOf(p.getDefensaEquipoActivo()), Ui.TEAL));
            JPanel buttons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton equip = Ui.secondaryButton("Equipo activo");
            JButton delete = Ui.dangerButton("Dar de baja personaje");
            equip.addActionListener(event -> EquipDialog.show(this, p)
                    .ifPresent(selection -> runAction("Equipo actualizado", () -> sistema.equiparJugadorActual(selection.weaponNames(), selection.armorName()))));
            delete.addActionListener(event -> {
                if (confirm("Dar de baja el personaje actual?")) {
                    runAction("Personaje eliminado", sistema::eliminarPersonajeActual);
                }
            });
            buttons.add(equip);
            buttons.add(delete);
            body.add(metrics, BorderLayout.CENTER);
            body.add(buttons, BorderLayout.SOUTH);
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel challengesPanel() {
        if (sistema.getUsuarioActual() instanceof Operador) {
            return operatorChallengesPanel();
        }
        return playerChallengesPanel();
    }

    private JPanel playerChallengesPanel() {
        JPanel panel = page();
        panel.add(pendingChallengesCard());
        panel.add(launchChallengeCard());
        panel.add(relatedChallengesCard());
        return pad(panel);
    }

    private JPanel pendingChallengesCard() {
        JPanel card = Ui.card();
        card.add(Ui.title("Desafios pendientes", 20f), BorderLayout.NORTH);
        DefaultTableModel model = Ui.model("Id", "Desafiante", "Apuesta", "Estado");
        List<Desafio> pending = sistema.listarDesafiosPendientesJugadorActual();
        for (Desafio desafio : pending) {
            model.addRow(new Object[]{desafio.getId(), desafio.getNickDesafiante(), desafio.getApuesta(), desafio.getNombreEstado()});
        }
        JTable table = Ui.table(model);
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(table), BorderLayout.CENTER);
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton accept = Ui.successButton("Aceptar");
        JButton reject = Ui.dangerButton("Rechazar");
        accept.addActionListener(event -> selectedId(table).ifPresent(id -> acceptChallenge(id)));
        reject.addActionListener(event -> selectedId(table).ifPresent(id -> {
            if (confirm("Rechazar este desafio aplica la penalizacion de oro. Continuar?")) {
                runAction("Desafio rechazado", () -> sistema.rechazarDesafio(id));
            }
        }));
        actions.add(accept);
        actions.add(reject);
        body.add(actions, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel launchChallengeCard() {
        JPanel card = Ui.card();
        card.add(Ui.title("Lanzar desafio", 20f), BorderLayout.NORTH);
        JTextField nick = Ui.textField();
        JTextField bet = Ui.numberField(0);
        Ui.Form form = new Ui.Form();
        form.addRow("Nick desafiado", nick);
        form.addRow("Apuesta", bet);
        JButton launch = Ui.primaryButton("Crear desafio");
        launch.addActionListener(event -> runAction("Desafio creado y pendiente de validacion", () -> sistema.lanzarDesafio(Ui.text(nick), Ui.intValue(bet, 0, 999999, "apuesta"))));
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(form, BorderLayout.CENTER);
        body.add(launch, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel relatedChallengesCard() {
        JPanel card = Ui.card();
        card.add(Ui.title("Mis desafios", 20f), BorderLayout.NORTH);
        DefaultTableModel model = Ui.model("Id", "Desafiante", "Desafiado", "Apuesta", "Estado");
        try {
            for (Desafio desafio : sistema.listarDesafiosRelacionadosActual()) {
                model.addRow(new Object[]{
                        desafio.getId(),
                        desafio.getNickDesafiante(),
                        desafio.getNickDesafiado(),
                        desafio.getApuesta(),
                        desafio.getNombreEstado()
                });
            }
            card.add(Ui.scroll(Ui.table(model)), BorderLayout.CENTER);
        } catch (DomainException ex) {
            card.add(Ui.small(ex.getMessage()), BorderLayout.CENTER);
        }
        return card;
    }

    private JPanel operatorChallengesPanel() {
        JPanel panel = page();
        panel.add(operatorPendingCard(sistema.listarDesafiosPendientesRevision()));
        return pad(panel);
    }

    private JPanel operatorPendingCard(List<Desafio> pending) {
        JPanel card = Ui.card();
        card.add(Ui.title("Validacion de desafios", 20f), BorderLayout.NORTH);
        DefaultTableModel model = Ui.model("Id", "Desafiante", "Desafiado", "Apuesta", "Fecha");
        for (Desafio desafio : pending) {
            model.addRow(new Object[]{
                    desafio.getId(),
                    desafio.getNickDesafiante(),
                    desafio.getNickDesafiado(),
                    desafio.getApuesta(),
                    DATE_FORMAT.format(desafio.getFechaCreacion())
            });
        }
        JTable table = Ui.table(model);
        JButton validate = Ui.primaryButton("Validar seleccionado");
        validate.addActionListener(event -> selectedId(table).ifPresent(id -> validateChallenge(id, pending)));
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(validate);
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(table), BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel playersPanel() {
        JPanel panel = page();
        panel.add(playersTableCard(sistema.listarJugadores()));
        return pad(panel);
    }

    private JPanel playersTableCard(List<Jugador> players) {
        JPanel card = Ui.card();
        card.add(Ui.title("Gestion de jugadores", 20f), BorderLayout.NORTH);
        DefaultTableModel model = Ui.model("Nick", "Nombre", "Registro", "Bloqueado", "Personaje", "Oro");
        for (Jugador player : players) {
            Personaje character = player.getPersonaje();
            model.addRow(new Object[]{
                    player.getNick(),
                    player.getNombre(),
                    player.getNumeroRegistro(),
                    player.isBloqueado() ? "Si" : "No",
                    character == null ? "Sin personaje" : character.getTipo() + " " + character.getNombre(),
                    character == null ? "-" : character.getOro()
            });
        }
        JTable table = Ui.table(model);

        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton replace = Ui.secondaryButton("Reemplazar personaje");
        JButton weapon = Ui.secondaryButton("Anadir arma");
        JButton armor = Ui.secondaryButton("Anadir armadura");
        JButton modifier = Ui.secondaryButton("Anadir modificador");
        JButton minion = Ui.secondaryButton("Anadir esbirro");
        JButton equip = Ui.secondaryButton("Configurar equipo");
        JButton block = Ui.dangerButton("Bloquear");
        JButton unblock = Ui.successButton("Desbloquear");
        JButton history = Ui.secondaryButton("Historial");

        replace.addActionListener(event -> selectedNick(table).ifPresent(nick ->
                CharacterDialog.show(this, "Reemplazar personaje de " + nick)
                        .ifPresent(config -> runAction("Personaje reemplazado", () -> sistema.reemplazarPersonajeJugador(nick, config)))));
        weapon.addActionListener(event -> selectedNick(table).ifPresent(nick ->
                DraftDialogs.weapon(this).ifPresent(draft -> runAction("Arma anadida", () -> sistema.addArmaAJugador(nick, draft.build())))));
        armor.addActionListener(event -> selectedNick(table).ifPresent(nick ->
                DraftDialogs.armor(this).ifPresent(draft -> runAction("Armadura anadida", () -> sistema.addArmaduraAJugador(nick, draft.build())))));
        modifier.addActionListener(event -> selectedNick(table).ifPresent(nick ->
                DraftDialogs.modifier(this, null).ifPresent(draft -> runAction("Modificador anadido", () -> sistema.addModificadorAJugador(nick, draft.build())))));
        minion.addActionListener(event -> selectedNick(table).ifPresent(nick ->
                DraftDialogs.minion(this).ifPresent(draft -> runAction("Esbirro anadido", () -> sistema.addEsbirroAJugador(nick, draft.build())))));
        equip.addActionListener(event -> selectedNick(table).ifPresent(this::equipPlayerAsOperator));
        block.addActionListener(event -> selectedNick(table).ifPresent(this::blockPlayer));
        unblock.addActionListener(event -> selectedNick(table).ifPresent(nick -> runAction("Jugador desbloqueado", () -> sistema.desbloquearJugador(nick))));
        history.addActionListener(event -> selectedNick(table).ifPresent(this::showHistoryDialog));

        actions.add(replace);
        actions.add(weapon);
        actions.add(armor);
        actions.add(modifier);
        actions.add(minion);
        actions.add(equip);
        actions.add(block);
        actions.add(unblock);
        actions.add(history);

        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(table), BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel rankingPanel() {
        JPanel panel = page();
        DefaultTableModel model = Ui.model("Pos.", "Jugador", "Personaje", "Oro", "Victorias", "Derrotas");
        for (EntradaRanking entry : sistema.consultarRanking()) {
            model.addRow(new Object[]{
                    entry.posicion(),
                    entry.nickJugador(),
                    entry.nombrePersonaje(),
                    entry.oroActual(),
                    entry.victorias(),
                    entry.derrotas()
            });
        }
        panel.add(Ui.titled("Ranking global", Ui.scroll(Ui.table(model))));
        return pad(panel);
    }

    private JPanel historyPanel() {
        JPanel panel = page();
        if (sistema.getUsuarioActual() instanceof Jugador) {
            panel.add(historyCard(sistema.listarHistorialJugadorActual()));
        } else {
            JTextField nick = Ui.textField();
            DefaultTableModel model = combatModel();
            JTable table = Ui.table(model);
            JButton search = Ui.primaryButton("Consultar");
            JButton detail = Ui.secondaryButton("Ver detalle");
            search.addActionListener(event -> runAction(null, () -> {
                model.setRowCount(0);
                for (RegistroCombate combat : sistema.listarHistorialDeJugador(Ui.text(nick))) {
                    addCombatRow(model, combat);
                }
            }));
            detail.addActionListener(event -> showSelectedCombat(table, model));
            hideObjectColumn(table);
            Ui.Form form = new Ui.Form();
            form.addRow("Nick jugador", nick);
            JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
            actions.add(search);
            actions.add(detail);
            JPanel body = Ui.transparent(new BorderLayout(8, 8));
            body.add(form, BorderLayout.NORTH);
            body.add(Ui.scroll(table), BorderLayout.CENTER);
            body.add(actions, BorderLayout.SOUTH);
            panel.add(Ui.titled("Historial por jugador", body));
        }
        return pad(panel);
    }

    private JPanel historyCard(List<RegistroCombate> combats) {
        DefaultTableModel model = combatModel();
        for (RegistroCombate combat : combats) {
            addCombatRow(model, combat);
        }
        JTable table = Ui.table(model);
        hideObjectColumn(table);
        JButton detail = Ui.secondaryButton("Ver detalle");
        detail.addActionListener(event -> showSelectedCombat(table, model));
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(table), BorderLayout.CENTER);
        body.add(Ui.row(detail), BorderLayout.SOUTH);
        return Ui.titled("Historial de combates", body);
    }

    private JPanel goldPanel() {
        JPanel panel = page();
        DefaultTableModel model = Ui.model("Fecha", "Concepto", "Delta", "Saldo");
        for (MovimientoOro move : sistema.listarMovimientosOroJugadorActual()) {
            model.addRow(new Object[]{
                    DATE_FORMAT.format(move.getFecha()),
                    move.getConcepto(),
                    move.getDelta(),
                    move.getSaldoResultante()
            });
        }
        panel.add(Ui.titled("Movimientos de oro", Ui.scroll(Ui.table(model))));
        return pad(panel);
    }

    private JPanel notificationsPanel() {
        JPanel panel = page();
        JTextArea area = Ui.textArea();
        List<String> notifications = sistema.verNotificacionesActuales();
        area.setText(notifications.isEmpty() ? "No hay notificaciones." : String.join(System.lineSeparator() + System.lineSeparator(), notifications));
        JButton clear = Ui.dangerButton("Limpiar bandeja");
        clear.addActionListener(event -> runAction("Bandeja vaciada", sistema::limpiarNotificacionesActuales));
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(area), BorderLayout.CENTER);
        body.add(Ui.row(clear), BorderLayout.SOUTH);
        panel.add(Ui.titled("Notificaciones", body));
        return pad(panel);
    }

    private JPanel rankingPreviewCard() {
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        DefaultTableModel model = Ui.model("Pos.", "Jugador", "Personaje", "Oro");
        List<EntradaRanking> ranking = sistema.consultarRanking();
        for (EntradaRanking entry : ranking.stream().limit(6).toList()) {
            model.addRow(new Object[]{entry.posicion(), entry.nickJugador(), entry.nombrePersonaje(), entry.oroActual()});
        }
        body.add(Ui.scroll(Ui.table(model)), BorderLayout.CENTER);
        JButton open = Ui.secondaryButton("Abrir ranking");
        open.addActionListener(event -> showApp("ranking"));
        body.add(Ui.row(open), BorderLayout.SOUTH);
        return Ui.titled("Ranking", body);
    }

    private void acceptChallenge(String id) {
        if (confirm("Quieres revisar tu equipo activo antes del combate?")) {
            Jugador player = requirePlayer();
            if (player.tienePersonaje()) {
                EquipDialog.show(this, player.getPersonaje())
                        .ifPresent(selection -> runAction("Equipo actualizado", () -> sistema.equiparJugadorActual(selection.weaponNames(), selection.armorName())));
            }
        }
        runAction("Combate ejecutado", () -> {
            RegistroCombate combat = sistema.aceptarDesafio(id);
            CombatDialog.show(this, combat);
        });
    }

    private void validateChallenge(String id, List<Desafio> pending) {
        Desafio challenge = pending.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow();
        Jugador challenger = sistema.buscarJugadorPublico(challenge.getNickDesafiante()).orElseThrow();
        Jugador challenged = sistema.buscarJugadorPublico(challenge.getNickDesafiado()).orElseThrow();
        List<String> challengerModifiers = modifierNames(challenger);
        List<String> challengedModifiers = modifierNames(challenged);
        Optional<List<String>> presentChallenger = DraftDialogs.modifierSelection(this, "Modificadores presentes de " + challenger.getNick(), challengerModifiers);
        if (presentChallenger.isEmpty()) {
            return;
        }
        Optional<List<String>> presentChallenged = DraftDialogs.modifierSelection(this, "Modificadores presentes de " + challenged.getNick(), challengedModifiers);
        if (presentChallenged.isEmpty()) {
            return;
        }
        runAction("Desafio procesado", () -> sistema.validarDesafio(id, presentChallenger.get(), presentChallenged.get()));
    }

    private void equipPlayerAsOperator(String nick) {
        Jugador player = sistema.buscarJugadorPublico(nick).orElseThrow(() -> new DomainException("No existe el jugador."));
        if (!player.tienePersonaje()) {
            error("El jugador no tiene personaje.");
            return;
        }
        EquipDialog.show(this, player.getPersonaje())
                .ifPresent(selection -> runAction("Equipo actualizado", () -> sistema.equiparPersonajeDeJugador(nick, selection.weaponNames(), selection.armorName())));
    }

    private void blockPlayer(String nick) {
        JTextField reason = Ui.textField();
        Ui.Form form = new Ui.Form();
        form.addRow("Motivo", reason);
        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(form, BorderLayout.CENTER);
        if (Ui.confirmDialog(this, "Bloquear jugador", root)) {
            runAction("Jugador bloqueado", () -> sistema.bloquearJugador(nick, Ui.text(reason)));
        }
    }

    private void showHistoryDialog(String nick) {
        JPanel panel = historyCard(sistema.listarHistorialDeJugador(nick));
        Ui.viewDialog(this, "Historial de " + nick, panel);
    }

    private void showSelectedCombat(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Selecciona un combate.");
            return;
        }
        RegistroCombate combat = (RegistroCombate) model.getValueAt(table.convertRowIndexToModel(row), 6);
        CombatDialog.show(this, combat);
    }

    private void hideObjectColumn(JTable table) {
        if (table.getColumnModel().getColumnCount() > 6) {
            table.removeColumn(table.getColumnModel().getColumn(6));
        }
    }

    private DefaultTableModel combatModel() {
        return Ui.model("Fecha", "Desafiante", "Desafiado", "Rondas", "Vencedor", "Oro", "_obj");
    }

    private void addCombatRow(DefaultTableModel model, RegistroCombate combat) {
        model.addRow(new Object[]{
                DATE_FORMAT.format(combat.getFecha()),
                combat.getNickDesafiante(),
                combat.getNickDesafiado(),
                combat.getRondas(),
                combat.esEmpate() ? "EMPATE" : combat.getVencedor(),
                combat.getOroGanado(),
                combat
        });
    }

    private List<String> modifierNames(Jugador player) {
        if (!player.tienePersonaje()) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        names.addAll(player.getPersonaje().getFortalezas().stream().map(Modificador::getNombre).toList());
        names.addAll(player.getPersonaje().getDebilidades().stream().map(Modificador::getNombre).toList());
        return names;
    }

    private Optional<String> selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Selecciona una fila.");
            return Optional.empty();
        }
        return Optional.of(String.valueOf(table.getValueAt(row, 0)));
    }

    private Optional<String> selectedNick(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Selecciona un jugador.");
            return Optional.empty();
        }
        return Optional.of(String.valueOf(table.getValueAt(row, 0)));
    }

    private void deleteAccount() {
        if (confirm("Eliminar definitivamente la cuenta actual?")) {
            runAction(null, () -> {
                sistema.darBajaCuentaActual();
                showPublic();
            });
        }
    }

    private JPanel metric(String label, String value, java.awt.Color color) {
        JPanel panel = Ui.card();
        JLabel title = Ui.small(label);
        JLabel number = Ui.title(value, 18f);
        number.setForeground(color);
        number.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title, BorderLayout.NORTH);
        panel.add(number, BorderLayout.CENTER);
        return panel;
    }

    private JPanel page() {
        JPanel panel = Ui.transparent(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel pad(JPanel panel) {
        JPanel padded = Ui.transparent(new BorderLayout());
        padded.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 26, 26));
        for (Component child : panel.getComponents()) {
            if (child instanceof JPanel childPanel) {
                childPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                childPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, childPanel.getPreferredSize().height + 220));
            }
        }
        padded.add(panel, BorderLayout.NORTH);
        return padded;
    }

    private Jugador requirePlayer() {
        Usuario user = sistema.getUsuarioActual();
        if (user instanceof Jugador player) {
            return player;
        }
        throw new DomainException("La operacion requiere una sesion de jugador.");
    }

    private void runAction(String success, UiAction action) {
        try {
            action.run();
            if (success != null && !success.isBlank()) {
                info(success);
                if (sistema.haySesionActiva()) {
                    showApp(currentScreen.equals("public") ? "dashboard" : currentScreen);
                }
            }
        } catch (DomainException | IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (Exception ex) {
            error("Error inesperado: " + ex.getMessage());
        }
    }

    private boolean confirm(String message) {
        return Ui.yesNoDialog(this, "Confirmar", message);
    }

    private void info(String message) {
        Ui.messageDialog(this, "Operacion completada", message);
    }

    private void error(String message) {
        Ui.messageDialog(this, "Aviso", message);
    }

    @FunctionalInterface
    private interface UiAction {
        void run();
    }
}
