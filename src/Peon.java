import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.texture.Texture;

public class Peon extends GameObj {
	double speed;

	Peon(double[] loc, int degrees, double hit_r, Texture tex, boolean tx,
			int my_display_list, GLAutoDrawable drawable) {
		super(loc, degrees, hit_r, new float[] { 0.4f, 0.4f, 0.7f, 1.0f }, tex,
				tx, my_display_list, drawable);
		hp = 5;
		speed = 1.0;

		GLUquadric cyl = glu.gluNewQuadric();

		glu.gluQuadricTexture(cyl, tx);
		glu.gluQuadricDrawStyle(cyl, GLU.GLU_FILL); // smooth shaded
		glu.gluQuadricNormals(cyl, GLU.GLU_SMOOTH);

		gl.glNewList(my_display_list, GL2.GL_COMPILE);

		gl.glPushMatrix();
		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		glu.gluCylinder(cyl, hit_rad, hit_rad, 40.0 + (30 * Math.random()), 64,
				4);
		gl.glPopMatrix();

		// gl.glPushMatrix();
		// gl.glTranslated(0, 69.0, 0);
		// gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		// glu.gluDisk(top, 0.0, hit_rad, 16, 8);
		// gl.glPopMatrix();

		gl.glEndList();
	}

	@Override
	public void draw_self() {
		act();
		if (tx) {
			tex.enable(gl);
			tex.bind(gl);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_REPEAT);
			gl.glEnable(GL.GL_TEXTURE_2D);
		}

		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, mapColor, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, mapColor, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, mapColor, 0);

		gl.glPushMatrix();
		gl.glTranslated(x, y, z);
		gl.glRotated(deg, 0.0, 1.0, 0.0);
		gl.glCallList(my_display_list);
		gl.glPopMatrix();

		if (tx)
			gl.glDisable(GL.GL_TEXTURE_2D);
	}

	public void act() {
		this.move(speed);

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
			} else if (turning < 10) {
				turning = (int) (60 * Math.random() - 30);
			} else {
				turning = 0;
			}
		}
	}

	@Override
	void hit(Peon p) {
		// p.move(-3.0);
		p.turn(p.deg - deg);
		p.turning = 0;
	}

	@Override
	void hit(Projectile p) {

	}

	@Override
	void hit(Hero h) {
		the_game.score--;
	}

	@Override
	void hit(Boss b) {

	}

	@Override
	void hit(Goal l) {
		hit(1);
		l.mode = (l.mode++) % 3;
		l.list();
	}
}