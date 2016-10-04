import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;

public class Projectile extends GameObj {
	double t = 0.25, s = 0.25;
	int h = 0;

	public Projectile(double[] loc, int degrees, double hit_rad,
			float[] mapColor, Texture tex, int my_display_list,
			GLAutoDrawable drawable) {

		super(loc, degrees, hit_rad, mapColor, tex, false, my_display_list,
				drawable);
	}

	@Override
	void draw_self() {
		if (tx) {
			h++;
			t = 0.25 * (int) (Math.random() * 4);
			s = 0.25 * (int) (Math.random() * 4);

			tex.enable(gl);
			tex.bind(gl);

			gl.glTexEnvf(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE,
					GL.GL_REPLACE);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_REPEAT);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_DEPTH_TEST);

			gl.glPushMatrix();
			gl.glTranslated(x, y, z);
			gl.glRotated(
					Math.atan2((the_game.the_hero.x - x),
							(the_game.the_hero.z - z)) * 180.0 / Math.PI, 0.0,
					1.0, 0.0);

			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2d(s, t);
			gl.glVertex3d(-5, -5, 0.0);
			gl.glTexCoord2d(s + 0.25, t);
			gl.glVertex3d(5, -5, 0.0);
			gl.glTexCoord2d(s + 0.25, t + 0.25);
			gl.glVertex3d(5, 5, 0.0);
			gl.glTexCoord2d(s, t + 0.25);
			gl.glVertex3d(-5, 5, 0.0);
			gl.glEnd();

			// gl.glRotated(deg, 0.0, 1.0, 0.0);

			gl.glPopMatrix();

			gl.glEnable(GL.GL_DEPTH_TEST);

			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);

			if (h > 5)
				hp = -1;
		} else {
			for (int i = 0; i < 10 && hp > 0; i++)
				move(3.0);
		}
	}

	@Override
	void collision() {
		for (Peon v : the_game.peons) {
			if (Math.sqrt(Math.pow(Math.abs(x - v.x), 2)
					+ Math.pow(Math.abs(z - v.z), 2)) <= hit_rad + v.hit_rad) {
				this.hit(v);
			}
		}

		for (Goal l : the_game.goals) {
			if (Math.sqrt(Math.pow(Math.abs(x - l.x), 2)
					+ Math.pow(Math.abs(z - l.z), 2)) <= hit_rad + l.hit_rad) {
				this.hit(l);
			}
		}

		if (Math.sqrt(Math.pow(Math.abs(x - the_game.the_boss.x), 2)
				+ Math.pow(Math.abs(z - the_game.the_boss.z), 2)) <= hit_rad
				+ the_game.the_boss.hit_rad) {
			this.hit(the_game.the_boss);
		}
	}

	@Override
	void hit(int i) {
		hp = -1;
	}

	@Override
	void hit(double[] n) {
		hp = -1;
	}

	@Override
	void hit(Peon p) {
		if (!tx) {
			move(-3.0);
			tx = true;
			p.hp--;
			p.mapColor[0] += 0.1;
			p.mapColor[1] -= 0.1;
			p.mapColor[2] -= 0.1;
			p.speed += 0.4;
			p.turn(deg - p.deg + 60 + (int) (60 * Math.random()));
		}
	}

	@Override
	void hit(Projectile p) {
		if (!tx) {
			move(-3.0);
			tx = true;
			p.hp--;
		}
	}

	@Override
	void hit(Hero h) {
		if (!tx) {
			move(-3.0);
			tx = true;
			h.hp--;
		}
	}

	@Override
	void hit(Boss b) {
		if (!tx) {
			move(-3.0);
			tx = true;
			b.hp--;
			b.tex = (b.tex == b.tex1) ? b.tex2 : b.tex1;
		}
	}

	@Override
	void hit(Goal g) {
		if (!tx) {
			move(-3.0);
			tx = true;
			g.hp--;
		}
	}
}
