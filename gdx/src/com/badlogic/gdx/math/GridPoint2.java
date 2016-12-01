/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.math;

import java.io.Serializable;
import java.util.Iterator;

/** A point in a 2D grid, with integer x and y coordinates
 * 
 * @author badlogic */
public class GridPoint2 implements Serializable {
	private static final long serialVersionUID = -4019969926331717380L;

	public int x;
	public int y;

	/** Constructs a new 2D grid point. */
	public GridPoint2 () {
	}

	/** Constructs a new 2D grid point.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate */
	public GridPoint2 (int x, int y) {
		this.x = x;
		this.y = y;
	}

	/** Copy constructor
	 * 
	 * @param point The 2D grid point to make a copy of. */
	public GridPoint2 (GridPoint2 point) {
		this.x = point.x;
		this.y = point.y;
	}

	/** Sets the coordinates of this 2D grid point to that of another.
	 * 
	 * @param point The 2D grid point to copy the coordinates of.
	 * 
	 * @return this 2D grid point for chaining. */
	public GridPoint2 set (GridPoint2 point) {
		this.x = point.x;
		this.y = point.y;
		return this;
	}

	/** Sets the coordinates of this 2D grid point.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * 
	 * @return this 2D grid point for chaining. */
	public GridPoint2 set (int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * @param other The other point
	 * @return the squared distance between this point and the other point.
	 */
	public float dst2 (GridPoint2 other) {
		int xd = other.x - x;
		int yd = other.y - y;

		return xd * xd + yd * yd;
	}

	/**
	 * @param x The x-coordinate of the other point
	 * @param y The y-coordinate of the other point
	 * @return the squared distance between this point and the other point.
	 */
	public float dst2 (int x, int y) {
		int xd = x - this.x;
		int yd = y - this.y;

		return xd * xd + yd * yd;
	}

	/**
	 * @param other The other point
	 * @return the distance between this point and the other vector.
	 */
	public float dst (GridPoint2 other) {
		int xd = other.x - x;
		int yd = other.y - y;

		return (float)Math.sqrt(xd * xd + yd * yd);
	}

	/**
	 * @param x The x-coordinate of the other point
	 * @param y The y-coordinate of the other point
	 * @return the distance between this point and the other point.
	 */
	public float dst (int x, int y) {
		int xd = x - this.x;
		int yd = y - this.y;

		return (float)Math.sqrt(xd * xd + yd * yd);
	}

	/**
	 * Adds another 2D grid point to this point.
	 *
	 * @param other The other point
	 * @return this 2d grid point for chaining.
	 */
	public GridPoint2 add (GridPoint2 other) {
		x += other.x;
		y += other.y;
		return this;
	}

	/**
	 * Adds another 2D grid point to this point.
	 *
	 * @param x The x-coordinate of the other point
	 * @param y The y-coordinate of the other point
	 * @return this 2d grid point for chaining.
	 */
	public GridPoint2 add (int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Subtracts another 2D grid point from this point.
	 *
	 * @param other The other point
	 * @return this 2d grid point for chaining.
	 */
	public GridPoint2 sub (GridPoint2 other) {
		x -= other.x;
		y -= other.y;
		return this;
	}

	/**
	 * Subtracts another 2D grid point from this point.
	 *
	 * @param x The x-coordinate of the other point
	 * @param y The y-coordinate of the other point
	 * @return this 2d grid point for chaining.
	 */
	public GridPoint2 sub (int x, int y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * @return a copy of this grid point
	 */
	public GridPoint2 cpy () {
		return new GridPoint2(this);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || o.getClass() != this.getClass()) return false;
		GridPoint2 g = (GridPoint2)o;
		return this.x == g.x && this.y == g.y;
	}

	@Override
	public int hashCode () {
		final int prime = 53;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}

	@Override
	public String toString () {
		return "(" + x + ", " + y + ")";
	}

	/** Iterates over a rectangular area of grid points */
	public class GridPoint2RectangleIterator implements Iterator<GridPoint2> {
		protected int x, x1, x2, y, y1, y2;

		/**
		 * @param upper Starting point. Must be the upper left point of the rectangle.
		 * @param lower Ending point. Must be the lower right point of the rectangle.
		 */
		public GridPoint2RectangleIterator (GridPoint2 upper, GridPoint2 lower) {
			this(upper.x, upper.y, lower.x, lower.y);
			reset();
		}

		/**
		 * @param x1 Must be less than x2
		 * @param y1 Must be greater than y2
		 * @param x2 Must be greater than x1
		 * @param y2 Must be less than y1
		 */
		public GridPoint2RectangleIterator (int x1, int y1, int x2, int y2) {
			if (x1 > x2 || y1 < y2) {
				throw new IllegalArgumentException("Upper bound must be to the upper left of lower bound.");
			}
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			reset();
		}

		public GridPoint2RectangleIterator reset() {
			y = y2 - 1;
			x = x1 - 1;
			return this;
		}

		/**
		 * Creates a new GridPoint2, which causes garbage.
		 * next(GridPoint2 fillIn) is recommended over this method.
		 */
		@Override
		public GridPoint2 next() {
			GridPoint2 fillIn = new GridPoint2();
			return next(fillIn);
		}

		public GridPoint2 next(GridPoint2 fillIn) {
			x++;
			if (x >= x2) {
				y--;
				x = x1;
			}
			fillIn.set(x, y);
			return fillIn;
		}

		@Override
		public boolean hasNext() {
			return !(y == y1 && x + 1 >= x2);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
