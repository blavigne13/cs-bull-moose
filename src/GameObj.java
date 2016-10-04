import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.texture.Texture;

public abstract class GameObj {
	public String name = "";
	float[] mapColor;
	int hp = Integer.MAX_VALUE;

	public double x, y, z; // position
	public int deg; // Degree measure of direction
	public double xdir, zdir; // Vector measure of direction

	public double hit_rad; // Radius of hit box, to detect collision
	public double col_rad; // radius for wall collision

	int turning = 0;
	double[] ref = { 0.0, 0.0, 0.0 }; // reflection vector

	Texture tex;
	boolean tx; // use texture?

	int my_display_list;
	GLAutoDrawable drawable;
	GLU glu;
	GL2 gl;

	GameObj(double[] loc, int degrees, double hit_rad, float[] mapColor, Texture tex, boolean tx, int my_display_list,
			GLAutoDrawable drawable) {

		this.x = loc[0];
		this.y = loc[1];
		this.z = loc[2];

		this.deg = 0;
		this.xdir = 0;
		this.zdir = 0;
		this.turn(degrees);

		this.hit_rad = hit_rad;
		this.col_rad = hit_rad + 5.0;

		this.tex = tex;
		this.tx = tx;
		this.mapColor = mapColor;

		this.my_display_list = my_display_list;
		this.drawable = drawable;

		this.gl = drawable.getGL().getGL2();
		this.glu = the_game.glu;
	}

	void reflect(double[] v, double[] n) {
		// -2*(V dot N)*N + V

		// System.out.println(Arrays.toString(v));
		// System.out.println(Arrays.toString(n));
		double vn = v[0] * n[0] + v[1] * n[1] + v[2] * n[2];

		// System.out.println(vn);

		v[0] -= 2 * vn * n[0];
		v[1] -= 2 * vn * n[1];
		v[2] -= 2 * vn * n[2];

		// System.out.println(Arrays.toString(v));
		// reduceToUnit(v);
		// System.out.println(Arrays.toString(v));
	}

	void reduceToUnit(double v[]) {
		double length;

		length = Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));

		if (length == 0.0) {
			length = 1.0;
		}

		v[0] /= length;
		v[1] /= length;
		v[2] /= length;
	}

	void turn(int degrees_rotation) {
		deg = (360 + deg + degrees_rotation) % 360;
		xdir = Math.cos((deg) * Math.PI / 180.0);
		zdir = Math.sin((deg) * Math.PI / 180.0);
	}

	void move(double spd) { // Pass in negative spd for backward motion
		x += spd * xdir;
		if (x <= col_rad) {
			x -= spd * xdir * 2;
			this.hit(new double[] { 1.0, 0.0, 0.0 });
		} else if (x >= the_game.ARENASIZE - col_rad) {
			x -= spd * xdir * 2;
			this.hit(new double[] { 1.0, 0.0, 0.0 });
		}

		z += spd * zdir;
		if (-z <= col_rad) {
			z -= spd * zdir * 2;
			this.hit(new double[] { 0.0, 0.0, 1.0 });
		} else if (-z >= the_game.ARENASIZE - col_rad) {
			z -= spd * zdir * 2;
			this.hit(new double[] { 0.0, 0.0, 1.0 });
		}
		collision();
	}

	void strafe(double spd) { // Pass in negative spd for left motion
		x -= spd * zdir;
		if (x <= col_rad || x >= the_game.ARENASIZE - col_rad) {
			x += spd * zdir;
			this.hit(new double[] { -1.0, 0.0, 1.0 });
		}

		z += spd * xdir;
		if (-z <= col_rad || -z >= the_game.ARENASIZE - col_rad) {
			z -= spd * xdir;
			this.hit(new double[] { 1.0, 0.0, -1.0 });
		}
		collision();
	}

	void collision() {
		for (Peon v : the_game.peons) {
			if (this != v && Math.sqrt(Math.pow(Math.abs(x - v.x), 2) + Math.pow(Math.abs(z - v.z), 2)) <= col_rad
					+ v.col_rad) {
				this.hit(v);
				// System.out.println(this.getClass().getName() + " hit "
				// + v.getClass().getName());
			}
		}

		for (Goal g : the_game.goals) {
			if (this != g && Math.sqrt(Math.pow(Math.abs(x - g.x), 2) + Math.pow(Math.abs(z - g.z), 2)) <= col_rad
					+ g.col_rad * 2) {
				this.hit(g);
				// System.out.println(this.getClass() + " hit " + l.getClass());
			}
		}

		if (this != the_game.the_boss && Math.sqrt(Math.pow(Math.abs(x - the_game.the_boss.x), 2)
				+ Math.pow(Math.abs(z - the_game.the_boss.z), 2)) <= col_rad + the_game.the_boss.col_rad * 2) {
			this.hit(the_game.the_boss);
			// System.out.println(this.getClass() + " hit "
			// + the_game.the_boss.getClass());
		}
	}

	void draw_mini() {
		gl.glColor3fv(mapColor, 0);
		GLUquadric top = glu.gluNewQuadric();

		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		glu.gluQuadricDrawStyle(top, GLU.GLU_FILL);
		glu.gluQuadricNormals(top, GLU.GLU_NONE);

		gl.glPushMatrix();
		gl.glTranslated(x, 100.0, z);
		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		glu.gluDisk(top, 0.0, hit_rad + 10.0, 16, 1);
		gl.glPopMatrix();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
	}

	void hit(double[] n) {
		ref[0] = xdir;
		ref[1] = 0.0;
		ref[2] = zdir;

		reflect(ref, n);
		turn((int) (Math.atan2(ref[2], ref[0]) * 180.0 / Math.PI) - deg);

		// System.out.println("d: " + deg);
		// move(1.337);
	}

	void hit(int i) {
		ref[0] = xdir;
		ref[1] = 0.0;
		ref[2] = zdir;

		switch (i) {
		case 2:
		case 1:
			reflect(ref, new double[] { 1.0, 0.0, 0.0 });
			turn((int) (Math.atan2(ref[2], ref[0]) * 180.0 / Math.PI) - deg);
			break;
		case -2:
		case -1:
			reflect(ref, new double[] { 0.0, 0.0, 1.0 });
			turn((int) (Math.atan2(ref[2], ref[0]) * 180.0 / Math.PI) - deg);
			break;
		}
		// System.out.println("d: " + deg);
		// move(1.337);
	}

	abstract void draw_self();

	abstract void hit(Peon p);

	abstract void hit(Projectile p);

	abstract void hit(Hero h);

	abstract void hit(Boss b);

	abstract void hit(Goal l);
}
