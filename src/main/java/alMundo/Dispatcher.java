package alMundo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import alMundo.Empleado.TipoEmpleado;

public class Dispatcher {

	private static int MAXIMA_CANTIDAD_LLAMADAS_SIMULTANEAS = 10;
	private ExecutorService service = Executors.newFixedThreadPool(MAXIMA_CANTIDAD_LLAMADAS_SIMULTANEAS);
	private BlockingQueue<Empleado> empleados = new LinkedBlockingQueue<>();
	private Queue<Llamada> llamadasPendientes = new ConcurrentLinkedQueue<Llamada>();
	private List<Llamada> llamadasAtendidas = new ArrayList<>();
	private int lineasActivas = 0;

	private Logger logger = Logger.getLogger(Dispatcher.class.toString());

	public void dispatchCall(Llamada llamada) {
		if (sePuedeAtenderLaLlamada()) {
			atenderLlamada(llamada);
		} else {
			ponerLlamadaEnEspera(llamada);
		}
	}

	private boolean sePuedeAtenderLaLlamada() {
		return hayLineasDisponibles() && hayEmpleadosDisponibles();
	}

	private boolean hayLineasDisponibles() {
		return lineasActivas < MAXIMA_CANTIDAD_LLAMADAS_SIMULTANEAS;
	}

	private void atenderLlamada(Llamada llamada) {
		aumentarLineasActivas();
		Empleado empleado = asignarNuevoEmpleado();
		logger.log(Level.INFO, empleado.getId() + " asignado a: " + llamada.getId());
		service.execute(() -> atender(empleado, llamada));
	}

	private void atender(Empleado empleado, Llamada llamada) {
		empleado.atender(llamada);
		agregarLlamadaAtendida(llamada);
		disminuirLineasActivas();
		despacharLlamadasPendientes();
	}

	private void despacharLlamadasPendientes() {
		if (hayLlamadasPendientes()) {
			Llamada llamada = llamadasPendientes.poll();
			logger.log(Level.INFO, "Despachando llamada pendiente " + llamada.getId());
			atenderLlamada(llamada);
		}
		else {
			finalizar();
		}
	}

	private boolean hayLlamadasPendientes() {
		return !llamadasPendientes.isEmpty();
	}

	private boolean hayEmpleadosDisponibles() {
		return empleados.stream().anyMatch(e -> e.estaDisponible());
	}

	private synchronized Empleado asignarNuevoEmpleado() {
		
		Optional<Empleado> empleado = filtrarPor(empleados, TipoEmpleado.OPERADOR);

		if (!empleado.isPresent()) {
			empleado = filtrarPor(empleados, TipoEmpleado.SUPERVISOR);
		}
		
		if (!empleado.isPresent()) {
			empleado = filtrarPor(empleados, TipoEmpleado.DIRECTOR);
		}
		return empleado.get();
	}

	private Optional<Empleado> filtrarPor(BlockingQueue<Empleado> empleados, TipoEmpleado tipo) {
		return empleados.stream().filter(e -> e.estaDisponible() && e.getTipoEmpleado().equals(tipo))
				.map(e -> e.ocuparse())
				.findFirst();
	}

	public void finalizar() {
		service.shutdown();
	}

	public void esperarQueTerminenTodasLasLlamadas() {
		try {
			service.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Ocurrió un error intentanto despachar las llamadas.");
		}
	}

	private void ponerLlamadaEnEspera(Llamada llamada) {
		llamada.ponerseEnEspera();
		llamadasPendientes.add(llamada);
	}

	private void agregarLlamadaAtendida(Llamada llamada) {
		llamadasAtendidas.add(llamada);
	}

	public Collection<Llamada> getLlamadasAtendidas() {
		return llamadasAtendidas;
	}

	public Collection<Llamada> getLlamadasPendientes() {
		return llamadasPendientes;
	}

	public void agregarEmpleado(Empleado e) {
		empleados.add(e);
	}

	private synchronized void aumentarLineasActivas() {
		lineasActivas++;
	}

	private synchronized void disminuirLineasActivas() {
		lineasActivas--;
	}
}