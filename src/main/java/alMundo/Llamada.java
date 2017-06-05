package alMundo;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Llamada {

	public enum Estado {
		NUEVA, EN_ESPERA, SIENDO_ATENDIDA, FINALIZADA
	}

	private static int MAXIMA_DURACION = 10000;
	private static int MINIMA_DURACION = 5000;
	Random random = new Random();
	private String id;
	private int duracion;
	private Estado estado;
	private Empleado operador;
	private boolean estuvoEnEspera;
	private Logger logger = Logger.getLogger(Llamada.class.toString());

	public Llamada(String string) {
		id = string;
		duracion = asignarDuracion();
		estado = Estado.NUEVA;
		estuvoEnEspera = false;
	}

	public int getDuracion() {
		return duracion;
	}

	private int asignarDuracion() {
		return random.nextInt(MAXIMA_DURACION - MINIMA_DURACION) + MINIMA_DURACION;
	}

	public void atenderPor(Empleado empleado) {
		operador = empleado;
		estado = Estado.SIENDO_ATENDIDA;
		logger.log(Level.INFO, "Empezó llamada " + this.id + " de duración " + this.getDuracion());

		try {
			TimeUnit.MILLISECONDS.sleep(duracion);
			logger.log(Level.INFO, "Empezó llamada " + this.id + " de duración " + this.getDuracion());

		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Ocurrió un error al atender la llamada. " + e.toString());
		}
		estado = Estado.FINALIZADA;
		logger.log(Level.INFO, "Empezó llamada " + this.id + " de duración " + this.getDuracion());
	}

	public String getId() {
		return id;
	}

	public Estado getEstado() {
		return estado;
	}

	public Empleado getOperador() {
		return operador;
	}

	public void ponerseEnEspera() {
		estado = Estado.EN_ESPERA;
		estuvoEnEspera = true;
	}

	public boolean estuvoEnEspera() {
		return estuvoEnEspera;
	}
}