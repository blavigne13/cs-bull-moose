import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.util.texture.Texture;

public class Boss extends GameObj {
	float[] ma = { 0.5f, 0.5f, 0.5f };
	double[] a, b, c, d;
	Texture tex1, tex2;

	Boss(double[] loc, int degrees, double hit_rad, float[] mapColor,
			Texture tex1, Texture tex2, boolean tx, int my_display_list,
			GLAutoDrawable drawable) {

		super(loc, degrees, hit_rad, mapColor, tex1, tx, my_display_list,
				drawable);

		this.tex1 = tex1;
		this.tex2 = tex2;

		this.a = new double[] { -hit_rad * 2, 0.0, 0.0 };
		this.b = new double[] { hit_rad * 2, 0.0, 0.0 };
		this.c = new double[] { hit_rad * 2, hit_rad * 4, 0.0 };
		this.d = new double[] { -hit_rad * 2, hit_rad * 4, 0.0 };

		gl.glNewList(my_display_list, GL2.GL_COMPILE);
		gl.glPushMatrix();
		// gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2d(0.0, 0.0);
		gl.glVertex3dv(a, 0);
		gl.glTexCoord2d(1.0, 0.0);
		gl.glVertex3dv(b, 0);
		gl.glTexCoord2d(1.0, 1.0);
		gl.glVertex3dv(c, 0);
		gl.glTexCoord2d(0.0, 1.0);
		gl.glVertex3dv(d, 0);
		gl.glEnd();

		gl.glPopMatrix();
		gl.glEndList();
	}

	@Override
	void draw_self() {
		act();

		gl.glDisable(GLLightingFunc.GL_LIGHT0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, the_game.yellow, 0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, the_game.gray, 0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, the_game.black, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_EMISSION, ma, 0);
		gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE,
				GL2ES1.GL_MODULATE);

		if (tx) {
			tex.enable(gl);
			tex.bind(gl);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_REPEAT);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
		}

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPushMatrix();

		gl.glTranslated(x, y, z);
		gl.glRotated(
				Math.atan2((the_game.the_hero.x - x), (the_game.the_hero.z - z))
						* 180.0 / Math.PI + 0.0, 0.0, 1.0, 0.0);
		// gl.glRotated(deg, 0.0, 1.0, 0.0);
		gl.glCallList(my_display_list);

		gl.glPopMatrix();

		if (tx) {
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_EMISSION, the_game.black, 0);
	}

	public void act() {
		this.move(2.0);

		if (turning < 0) {
			turn(Math.max(turning, -3));
			turning += 1;
		} else if (turning > 0) {
			turn(Math.min(turning, 3));
			turning -= 1;
		} else {
			turning = (int) (1000 * Math.random());
			if (turning < 1) {
				deg = (int) (360 * Math.random());
				turning = 0;
			} else if (turning < 5) {
				turning = (int) (60 * Math.random() - 30);
			} else {
				turning = 0;
			}
		}

		the_game.lp1[0] = (float) x;
		the_game.lp1[2] = (float) z;
	}

	@Override
	void hit(Peon p) {
		p.move(-6.0);
		p.turn((int) (this.deg - p.deg * 1.5));
		turning = 0;
	}

	@Override
	void hit(Projectile p) {

	}

	@Override
	void hit(Hero h) {

	}

	@Override
	void hit(Boss b) {
	}

	@Override
	void hit(Goal l) {

	}
}
