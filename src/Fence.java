import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

public class Fence {
	GLAutoDrawable drawable;
	GLU glu;
	GL2 gl;
	int my_display_list;
	GLUquadric a, p;

	Fence(int display_list, GLAutoDrawable drawable) {
		this.gl = drawable.getGL().getGL2();
		this.glu = the_game.glu;
		this.my_display_list = display_list;

		this.a = glu.gluNewQuadric();
		this.p = glu.gluNewQuadric();

		gl.glNewList(my_display_list, GL2.GL_COMPILE);

		glu.gluQuadricDrawStyle(a, GLU.GLU_FILL); // smooth shaded
		glu.gluQuadricNormals(a, GLU.GLU_SMOOTH);

		gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gl.glTranslated(the_game.ARENASIZE, 0.0, 0.0);
		gen();
		gl.glTranslated(-the_game.ARENASIZE, 0.0, 0.0);
		gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gen();
		gl.glTranslated(-the_game.ARENASIZE, 0.0, 0.0);
		gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gen();
		gl.glTranslated(-the_game.ARENASIZE, 0.0, 0.0);
		gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gen();

		gl.glEndList();
	}

	void gen() {
		gl.glPushMatrix();

		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, the_game.gray, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, the_game.gray, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, the_game.black, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_EMISSION, the_game.green, 0);

		gl.glTranslated(0, 48.0, 0);
		glu.gluCylinder(a, 1.337, 1.337, the_game.ARENASIZE, 16, 64);
		gl.glTranslated(0, -13.0, 0);
		glu.gluCylinder(a, 1.337, 1.337, the_game.ARENASIZE, 16, 64);
		gl.glTranslated(0, -13.0, 0);
		glu.gluCylinder(a, 1.337, 1.337, the_game.ARENASIZE, 16, 64);
		gl.glTranslated(0, -13.0, 0);
		glu.gluCylinder(a, 1.337, 1.337, the_game.ARENASIZE, 16, 64);
		gl.glTranslated(0, -13.0, 0);

		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_EMISSION, the_game.black, 0);

		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, the_game.gray, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, the_game.gray, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, the_game.white, 0);

		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
		glu.gluCylinder(p, 3.0, 3.0, 55.0, 64, 16);
		gl.glTranslated(-the_game.ARENASIZE / 4.0, 0.0, 0.0);
		glu.gluCylinder(p, 3.0, 3.0, 55.0, 64, 16);
		gl.glTranslated(-the_game.ARENASIZE / 4.0, 0.0, 0.0);
		glu.gluCylinder(p, 3.0, 3.0, 55.0, 64, 16);
		gl.glTranslated(-the_game.ARENASIZE / 4.0, 0.0, 0.0);
		glu.gluCylinder(p, 3.0, 3.0, 55.0, 64, 16);

		gl.glPopMatrix();
	}

	public void draw_self() {

		gl.glPushMatrix();
		// gl.glTranslated(x, y, z);
		gl.glCallList(my_display_list);
		gl.glPopMatrix();

	}
}
