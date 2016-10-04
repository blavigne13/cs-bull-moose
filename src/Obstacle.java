import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Obstacle {
	float l, r, b, t;

	public Obstacle(float l, float r, float b, float t) {
		this.l = l;
		this.r = r;
		this.b = b;
		this.t = t;
	}

	void draw_self(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(l, 0.0f, b);
		gl.glVertex3f(r, 0.0f, b);
		gl.glVertex3f(r, the_game.WALLHEIGHT, b);
		gl.glVertex3f(l, the_game.WALLHEIGHT, b);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glVertex3f(r, 0.0f, b);
		gl.glVertex3f(r, 0.0f, t);
		gl.glVertex3f(r, the_game.WALLHEIGHT, t);
		gl.glVertex3f(r, the_game.WALLHEIGHT, b);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glVertex3f(r, 0.0f, t);
		gl.glVertex3f(l, 0.0f, t);
		gl.glVertex3f(l, the_game.WALLHEIGHT, t);
		gl.glVertex3f(r, the_game.WALLHEIGHT, t);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glVertex3f(l, 0.0f, t);
		gl.glVertex3f(l, 0.0f, b);
		gl.glVertex3f(l, the_game.WALLHEIGHT, b);
		gl.glVertex3f(l, the_game.WALLHEIGHT, t);
		gl.glEnd();
	}
}
