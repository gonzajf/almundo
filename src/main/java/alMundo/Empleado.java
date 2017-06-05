package alMundo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Empleado {
	
	public enum TipoEmpleado {
		OPERADOR, SUPERVISOR, DIRECTOR
	};

	public enum Estado {
		DISPONIBLE, OCUPADO
	};

	private TipoEmpleado tipoEmpleado;
	private Estado estado;
	private String id;
	private Logger logger = Logger.getLogger(Empleado.class.toString());
	
	public Empleado(TipoEmpleado tipo, String id) {
		this.tipoEmpleado = tipo;
		this.estado = Estado.DISPONIBLE;
		this.id = id;
	}

	public TipoEmpleado getTipoEmpleado() {
		return tipoEmpleado;
	}

	public boolean estaDisponible() {
		return estado == Estado.DISPONIBLE;
	}

	public Estado getEstado() {
		return estado;
	}

	public void atender(Llamada llamada) {
		logger.log(Level.INFO, this.id + " atendiendo llamada " + llamada.getId());
		llamada.atenderPor(this);
		this.estado = Estado.DISPONIBLE;
		logger.log(Level.INFO, this.id + " finalizando llamada " + llamada.getId());
	}

	public Empleado ocuparse() {
		this.estado = Estado.OCUPADO;
		return this;
	}

	public String getId() {
		return id;
	}
}