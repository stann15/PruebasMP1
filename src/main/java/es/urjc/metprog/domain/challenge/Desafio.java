package es.urjc.metprog.domain.challenge;

import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Desafio implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String nickDesafiante;
    private final String nickDesafiado;
    private final int apuesta;
    private final LocalDateTime fechaCreacion;
    private String nickOperador;
    private final List<String> presentesDesafiante;
    private final List<String> presentesDesafiado;
    private String motivoRechazo;
    private EstadoDesafio estado;

    public Desafio(String id, String nickDesafiante, String nickDesafiado, int apuesta) {
        Validaciones.noVacio(id, "id del desafio");
        Validaciones.noVacio(nickDesafiante, "nick desafiante");
        Validaciones.noVacio(nickDesafiado, "nick desafiado");
        Validaciones.minimo(apuesta, 0, "apuesta");
        this.id = id;
        this.nickDesafiante = nickDesafiante;
        this.nickDesafiado = nickDesafiado;
        this.apuesta = apuesta;
        this.fechaCreacion = LocalDateTime.now();
        this.presentesDesafiante = new ArrayList<>();
        this.presentesDesafiado = new ArrayList<>();
        this.estado = EstadoDesafio.pendiente();
    }

    public String getId() {
        return id;
    }

    public String getNickDesafiante() {
        return nickDesafiante;
    }

    public String getNickDesafiado() {
        return nickDesafiado;
    }

    public int getApuesta() {
        return apuesta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getNickOperador() {
        return nickOperador;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getNombreEstado() {
        return estado.nombre();
    }

    public boolean estaPendienteRevision() {
        return estado == EstadoDesafio.pendiente();
    }

    public boolean estaPendienteRespuesta() {
        return estado == EstadoDesafio.validado();
    }

    public boolean estaResuelto() {
        return estado == EstadoDesafio.resuelto() || estado == EstadoDesafio.rechazado();
    }

    public List<String> getPresentesDesafiante() {
        return Collections.unmodifiableList(presentesDesafiante);
    }

    public List<String> getPresentesDesafiado() {
        return Collections.unmodifiableList(presentesDesafiado);
    }

    public void validar(String nickOperador, List<String> presentesDesafiante, List<String> presentesDesafiado) {
        estado.validar(this);
        this.nickOperador = nickOperador;
        this.presentesDesafiante.clear();
        this.presentesDesafiante.addAll(presentesDesafiante);
        this.presentesDesafiado.clear();
        this.presentesDesafiado.addAll(presentesDesafiado);
        this.estado = EstadoDesafio.validado();
    }

    public void aceptar() {
        estado.aceptar(this);
        this.estado = EstadoDesafio.aceptado();
    }

    public void rechazar(String motivo) {
        estado.rechazar(this);
        this.motivoRechazo = motivo;
        this.estado = EstadoDesafio.rechazado();
    }

    public void resolver() {
        estado.resolver(this);
        this.estado = EstadoDesafio.resuelto();
    }

    public void rechazarAdministrativamente(String motivo) {
        if (!estaPendienteRevision()) {
            throw new DomainException("Solo se puede rechazar administrativamente un desafio pendiente.");
        }
        this.motivoRechazo = motivo;
        this.estado = EstadoDesafio.rechazado();
    }

    public int getPenalizacionRechazo() {
        return (int) Math.ceil(apuesta * 0.10);
    }

    @Override
    public String toString() {
        return "Desafio{id='" + id + "', desafiante='" + nickDesafiante + "', desafiado='" + nickDesafiado + "', apuesta=" + apuesta + ", estado=" + estado.nombre() + "}";
    }
}
