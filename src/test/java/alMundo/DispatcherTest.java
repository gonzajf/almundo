package alMundo;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import alMundo.Empleado.TipoEmpleado;
import alMundo.Llamada.Estado;

public class DispatcherTest {
	
	private Llamada l1;
	private Llamada l2;
	private Llamada l3;
	private Llamada l4;
	private Empleado e1;
	private Empleado e2;
	private Empleado e3;
	private Empleado e4;
	private Empleado e5;
	private Empleado e6;
	private Empleado e7;
	private Dispatcher dispatcher;

	Queue<Llamada> llamadas;

	@Before
	public void setUp() {
		
		l1 = new Llamada("Nº 1");
		l2 = new Llamada("Nº 2");
		l3 = new Llamada("Nº 3");
		l4 = new Llamada("Nº 4");

		llamadas = new ConcurrentLinkedQueue<Llamada>();
		llamadas.add(l1);
		llamadas.add(l2);
		llamadas.add(l3);
		llamadas.add(l4);

		dispatcher = new Dispatcher();
		e1 = new Empleado(TipoEmpleado.OPERADOR, "GONZALO");
		e2 = new Empleado(TipoEmpleado.OPERADOR, "PEDRO");
		e3 = new Empleado(TipoEmpleado.OPERADOR, "DANIELA");
		e4 = new Empleado(TipoEmpleado.SUPERVISOR, "LUCHO");
		e5 = new Empleado(TipoEmpleado.DIRECTOR, "LU");
		e6 = new Empleado(TipoEmpleado.SUPERVISOR, "VANE");
		e7 = new Empleado(TipoEmpleado.OPERADOR, "CHARLY");

		dispatcher = new Dispatcher();
		dispatcher.agregarEmpleado(e4);
		dispatcher.agregarEmpleado(e5);
		dispatcher.agregarEmpleado(e1);
		dispatcher.agregarEmpleado(e2);
		dispatcher.agregarEmpleado(e3);
		dispatcher.agregarEmpleado(e6);
		dispatcher.agregarEmpleado(e7);
	}

	@Test
	public void atenderLlamadasTest() {
		despacharLlamadas();
		todasLasLlamadasFueronAtendidas();
	}

	@Test
	public void todasLasLlamadasSonAtendidasPorOperadores() {
		despacharLlamadas();
		dispatcher.getLlamadasAtendidas().stream().forEach(l -> l.getOperador().getTipoEmpleado().toString());
		Assert.assertTrue("Todas las llamadas son atendidas por operadores",
				dispatcher.getLlamadasAtendidas().stream()
						.allMatch(l -> l.getOperador().getTipoEmpleado().equals(TipoEmpleado.OPERADOR)));
		todasLasLlamadasFueronAtendidas();
	}

	@Test
	public void lleganMasDeDiezLlamadasYSePuedenAtender() {
		agregarLlamadas();
		despacharLlamadas();
		// Llegan 12 llamadas para un maximo de 10 que pueden ser atendidas simultaneamente.
		// Como hay 7 operadores 5 deberán ponerse en espera, pero finalmente serán atendidas.
		Assert.assertEquals(5, dispatcher.getLlamadasAtendidas().stream().filter(l -> l.estuvoEnEspera()).count(), 0);
		Assert.assertEquals(0, dispatcher.getLlamadasPendientes().size(), 0);
		todasLasLlamadasFueronAtendidas();
	}

	private void despacharLlamadas() {
		llamadas.forEach(l -> dispatcher.dispatchCall(l));
		dispatcher.esperarQueTerminenTodasLasLlamadas();
	}

	private void todasLasLlamadasFueronAtendidas() {
		Assert.assertTrue("Todas las llamadas fueron atendidas",
				dispatcher.getLlamadasAtendidas().stream().allMatch(l -> l.getEstado().equals(Estado.FINALIZADA)));
	}

	private void agregarLlamadas() {
		Llamada l5 = new Llamada("Nº 5");
		Llamada l6 = new Llamada("Nº 6");
		Llamada l7 = new Llamada("Nº 7");
		Llamada l8 = new Llamada("Nº 8");
		Llamada l9 = new Llamada("Nº 9");
		Llamada l10 = new Llamada("Nº 10");
		Llamada l11 = new Llamada("Nº11");
		Llamada l12 = new Llamada("Nº 12");
		llamadas.add(l5);
		llamadas.add(l6);
		llamadas.add(l7);
		llamadas.add(l8);
		llamadas.add(l9);
		llamadas.add(l10);
		llamadas.add(l11);
		llamadas.add(l12);
	}
}