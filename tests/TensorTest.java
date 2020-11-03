
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import show.ezkz.casprzak.core.functions.GeneralFunction;
import show.ezkz.casprzak.core.functions.binary.Pow;
import show.ezkz.casprzak.core.functions.commutative.Product;
import show.ezkz.casprzak.core.functions.endpoint.Constant;
import show.ezkz.casprzak.core.functions.endpoint.Variable;
import show.ezkz.casprzak.core.functions.unitary.trig.normal.Cos;
import show.ezkz.casprzak.core.functions.unitary.trig.normal.Sin;
import tensors.*;
import tensors.ArrayTensor;
import tensors.elementoperations.ElementAccessor;
import tensors.elementoperations.ElementProduct;
import tensors.elementoperations.ElementSum;
import tensors.elementoperations.ElementWrapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tensors.TensorTools.*;
import static show.ezkz.casprzak.core.tools.defaults.DefaultFunctions.*;

public class TensorTest {

	@BeforeAll
	static void beforeAll() {
		DefaultSpaces.initialize();
	}

	@Test
	void undirectedTest() {
		NestedArray<?, Integer> test = NestedArray.nest(new Object[][]{
				{1, 2},
				{3, 4}
		});
		System.out.println(test);
		assertEquals(2, test.getAtIndex(0, 1));
		assertEquals(2, test.getRank());
		test.setAtIndex(-1, 0, 0);
		assertEquals(-1, test.getAtIndex(0, 0));
		Nested<?, Integer> test2 = test.modifyWith(i -> -2 * i);
		assertEquals(-4, test2.getAtIndex(0, 1));
	}

	@Test
	void directedTest() {
		DirectedNested<?, Integer> test = DirectedNestedArray.direct(
				new Object[][]{
						{1, 2},
						{3, 4}
				}, new boolean[]{true, false}
		);
		System.out.println(test);
		assertEquals(2, test.getAtIndex(0, 1));
		assertTrue(test.getDirection());
	}

	@Test
	void tensorTest() {
		Tensor test = ArrayTensor.tensor(
				new Object[][]{
						{ONE, TWO},
						{new Constant(3), new Product(TWO, E)}
				},
				true, false);
		System.out.println(test);
		assertEquals(TWO, test.getAtIndex(0, 1));
		assertTrue(test.getDirection());
		DirectedNested<?, GeneralFunction> test2d = DirectedNestedArray.direct(NestedArray.nest(
				new Object[][]{
						{ONE, ONE},
						{ONE, TWO}
				}), new boolean[]{true, false});
		Tensor test2 = ArrayTensor.tensor(test2d);
		DirectedNested<?, ?> sum = TensorTools.createFrom(
				List.of("a", "b"),
				new boolean[]{true, false},
				2,
				new ElementSum(indexTensor(test, "a", "b"), indexTensor(test2, "a", "b"))
		);
		assertEquals(sum.getAtIndex(1, 0), new Constant(4));
		System.out.println(sum);
	}

	@Test
	void elementTest() {
		Tensor id2u = ArrayTensor.tensor(
				new Object[][]{
						{ZERO, ONE},
						{ONE, ZERO}
				},
				false, false
		);
		Tensor id2d = ArrayTensor.tensor(
				new Object[][]{
						{ZERO, ONE},
						{ONE, ZERO}
				},
				true, true
		);
		System.out.println(createFrom(List.of("a", "b"), new boolean[]{true, false}, 2,
				new ElementProduct(new ElementWrapper(id2u, "a", "m"), new ElementWrapper(id2d, "m", "b"))
				));
	}

	@Test
	void scalarTest1() {
		Tensor C = ArrayTensor.tensor(
				new Object[]{
						ONE,
						TWO
				},
				true
		);
		Tensor R = ArrayTensor.tensor(
				new Object[]{
						TEN, ONE
				},
				false
		);
		System.out.println(createFrom(List.of(), new boolean[]{}, 2,
				new ElementProduct(new ElementWrapper(R, "\\mu"), new ElementWrapper(C, "\\mu"))
		));
	}

	@Test
	void scalarTest2() {
		Tensor C = ArrayTensor.tensor(
				new Object[]{
						TEN, ONE
				},
				true
		);
		Tensor metric = ArrayTensor.tensor(
				new Object[][]{
						{NEGATIVE_ONE, ZERO},
						{ZERO, ONE}
				},
				false, false
		);
		System.out.println(createFrom(List.of("\\nu"), new boolean[]{false}, 2,
				new ElementProduct(new ElementWrapper(C, "\\mu"), new ElementWrapper(metric, "\\mu", "\\nu"))
		));
	}

	@Test
	void christoffel() {
		Space space = DefaultSpaces.s2;
		System.out.println(space.christoffel);
	}

	@Test
	void cov1() {
		Space space = DefaultSpaces.cartesian2d;
		Tensor tensor = ArrayTensor.tensor(
				new Object[]{square(new Variable("x")), new Product(TWO, new Variable("y"))},
				true
		);
		assertEquals(space.covariantDerivative("a", tensor, "b"), ArrayTensor.tensor(
				new Object[][]{
						{new Product(TWO, new Variable("x")), ZERO},
						{ZERO, TWO}
				},
				false, true
				)
		);

	}

	@Test
	void cov2() {
		Space space = DefaultSpaces.spherical;
		Tensor tensor = ArrayTensor.tensor(
				new Object[]{new Product(new Variable("k"), new Variable("q"), new Pow(NEGATIVE_TWO, new Variable("r"))), ZERO, ZERO},
				true
		);
		Tensor expected = ArrayTensor.tensor(
				new Object[][]{
						{new Product(NEGATIVE_TWO, new Variable("k"), new Variable("q"), new Pow(new Constant(-3), new Variable("r"))), ZERO, ZERO},
						{ZERO, new Product(new Variable("k"), new Variable("q"), new Pow(new Constant(-3), new Variable("r"))), ZERO},
						{ZERO, ZERO, new Product(new Variable("k"), new Variable("q"), new Pow(new Constant(-3), new Variable("r")))}
				},
				false, true
		);
		assertEquals(expected, space.covariantDerivative("\\mu", tensor, "\\nu"));
	}

	@Test
	void cov3() {
		Space space = DefaultSpaces.cartesian2d;
		Tensor tensor = ArrayTensor.tensor(
				new Object[][]{
						{square(new Variable("x")), new Product(TWO, new Variable("y"))},
						{ZERO, new Sin(new Variable("x"))}
				},
				true, false
		);
		assertEquals(space.covariantDerivative("a", tensor, "b", "c"), ArrayTensor.tensor(
				new Object[][][]{
						{
								{new Product(TWO, new Variable("x")), ZERO},
								{ZERO, new Cos(new Variable("x"))}
						},
						{
								{ZERO, TWO},
								{ZERO, ZERO}
						}
				},
				false, true, false
				)
		);

	}

	@Test
	void access1() {
		Tensor vector = ArrayTensor.tensor(
				new Object[]{new Product(new Variable("k"), new Variable("q"), new Pow(NEGATIVE_TWO, new Variable("r"))), ZERO, ZERO},
				true
		);
		System.out.println(TensorTools.createFrom(
				List.of("\\beta", "a"),
				new boolean[]{false, true},
				3,
				new ElementProduct(
						TensorTools.indexTensor(vector, "\\alpha"),
						TensorTools.indexTensor(DefaultSpaces.spherical.christoffel, "\\alpha", "a", "\\beta")
				)
		));
	}

}
