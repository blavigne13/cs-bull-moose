import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.*;

import com.jogamp.opengl.util.texture.Texture;

public class Hero extends GameObj {
	Texture cross;

	Hero(double[] loc, int degrees, double hit_rad, float[] mapColor,
			Texture environment, Texture crosshair, int my_display_list,
			GLAutoDrawable drawable) {

		super(loc, degrees, hit_rad, mapColor, environment, true,
				my_display_list, drawable);

		this.cross = crosshair;

		GLUquadric cyl = glu.gluNewQuadric();
		glu.gluQuadricTexture(cyl, true);

		gl.glNewList(my_display_list, GL2.GL_COMPILE);
		glu.gluCylinder(cyl, hit_rad, hit_rad, 10.0, 15, 5);
		gl.glEndList();
	}

	@Override
	void draw_self() {
		gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE,
				GL.GL_REPLACE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);

		tex.enable(gl);
		tex.bind(gl);

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_DEPTH_TEST); // disable depth and backface cull for
		gl.glDisable(GL.GL_CULL_FACE); // the environment map

		gl.glPushMatrix();

		gl.glTranslated(x, y, z);
		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		gl.glCallList(my_display_list);

		gl.glPopMatrix();

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_TEXTURE_2D);
		// System.out.println(x + "," + z);
	}

	void draw_crosshair() {
		// crosshair
		cross.enable(gl);
		cross.bind(gl);

		gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE,
				GL.GL_REPLACE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

		gl.glEnable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);

		gl.glEnable(GL.GL_TEXTURE_2D);
		// gl.glDisable(GL2.GL_DEPTH_TEST);
		// gl.glDisable(GL2.GL_CULL_FACE);

		gl.glPushMatrix();
		gl.glTranslated(x + xdir * 10, y + 2.2, z + zdir * 10);
		gl.glRotated(270 - deg, 0.0, 1.0, 0.0);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2d(0.0, 0.0);
		gl.glVertex3d(-0.3, 0, 0);
		gl.glTexCoord2d(1.0, 0.0);
		gl.glVertex3d(0.3, 0, 0);
		gl.glTexCoord2d(1.0, 1.0);
		gl.glVertex3d(0.3, 0.6, 0);
		gl.glTexCoord2d(0.0, 1.0);
		gl.glVertex3d(-0.3, 0.6, 0);
		gl.glEnd();

		gl.glPopMatrix();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_BLEND);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	@Override
	void draw_mini() {
		gl.glColor3fv(mapColor, 0);

		gl.glDisable(GLLightingFunc.GL_LIGHTING);

		gl.glPushMatrix();
		gl.glTranslated(x, 100.0, z);
		gl.glRotated(-deg + 90, 0.0, 1.0, 0.0);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3d(0.0, 0.0, col_rad * 2); // top
		gl.glVertex3d(col_rad + 5, 0.0, 0.0); // mid r
		gl.glVertex3d(col_rad, 0.0, -col_rad * 2); // bottom r
		gl.glVertex3d(-col_rad, 0.0, -col_rad * 2); // bottom l
		gl.glVertex3d(-col_rad - 5, 0.0, 0.0); // mid l
		gl.glEnd();

		gl.glPopMatrix();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
	}

	@Override
	void hit(Projectile p) {
	}

	@Override
	void hit(Hero h) {
	}

	@Override
	void hit(Peon p) {
		p.move(-3.0);
		p.turn(180);
		p.turning = 0;
	}

	@Override
	void hit(Boss b) {
		the_game.score = 0;
	}

	@Override
	void hit(Goal l) {

	}
}
