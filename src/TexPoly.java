import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

import com.jogamp.opengl.util.texture.Texture;

public class TexPoly {
	GL2 gl;
	float[] a, b, c, d;
	float texTop, texRight;
	Texture tex;
	boolean tx; // use texture, blending

	public TexPoly(GL2 gl, float[] a, float[] b, float[] c, float[] d,
			float texTop, float texRight, Texture tex, boolean tx) {
		this.gl = gl;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.texTop = texTop;
		this.texRight = texRight;
		this.tex = tex;
		this.tx = tx;
	}

	void draw_self() {
		if (tx) {
			gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE,
					GL.GL_REPLACE);
			tex.enable(gl);
			tex.bind(gl);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_REPEAT);
			gl.glEnable(GL.GL_TEXTURE_2D);
		}

		gl.glPushMatrix();
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3fv(a, 0);
		gl.glTexCoord2f(texTop, 0.0f);
		gl.glVertex3fv(b, 0);
		gl.glTexCoord2f(texTop, texRight);
		gl.glVertex3fv(c, 0);
		gl.glTexCoord2f(0.0f, texRight);
		gl.glVertex3fv(d, 0);
		gl.glEnd();
		gl.glPopMatrix();

		if (tx)
			gl.glDisable(GL.GL_TEXTURE_2D);
	}

	void draw_mini() {
		gl.glPushMatrix();
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glTranslated(0.0, 10.0, 0.0);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3fv(a, 0);
		gl.glVertex3fv(b, 0);
		gl.glVertex3fv(c, 0);
		gl.glVertex3fv(d, 0);
		gl.glEnd();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glPopMatrix();
	}
}
