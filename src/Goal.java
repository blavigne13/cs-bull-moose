import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;

public class Goal extends GameObj {
	boolean antialias = true;
	OffObj offObj;
	float objMat[] = { 0.8f, 0.0f, 0.0f, 1.0f }; // Red
	float specref[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // mat spec reflectance
	int specexp = 128; // Initialize to max falloff factor
	int mode;
	double size;

	Goal(double[] loc, int degrees, double hit_rad, float[] mapColor,
			Texture tex, boolean useTex, int my_display_list,
			GLAutoDrawable drawable, String off, int mode, double size) {

		super(loc, degrees, hit_rad, mapColor, tex, useTex, my_display_list,
				drawable);

		this.name = off;
		this.mode = mode;
		this.size = size;
		this.offObj = new OffObj("off/" + off + ".off", true);
		this.offObj.load_off_file();

		list();
	}

	void list() {
		gl.glNewList(my_display_list, GL2.GL_COMPILE);

		gl.glScaled(size, size, size);

		for (int i = 0; i < offObj.num_faces; i++) { // For each face
			float xPlane[] = { 1.0f / Math.abs(offObj.x_right - offObj.x_left),
					0.0f, 0.0f, 0.0f };
			float yPlane[] = { 0.0f, // 0.0f,
					1.0f / Math.abs(offObj.y_bottom - offObj.y_top), 0.0f, 0.0f };
			float zPlane[] = { 0.0f, 0.0f,
					1.0f / Math.abs(offObj.z_far - offObj.z_near), 0.0f };

			// xPlane = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
			// yPlane = new float[] { 0.0f, 1.0f, 0.0f, 0.0f };
			// zPlane = new float[] { 0.0f, 0.0f, 1.0f, 0.0f };
			// zPlane = new float[] {
			// 1.0f / Math.abs(offObj.x_right - offObj.x_left),
			// 1.0f / Math.abs(offObj.y_bottom - offObj.y_top),
			// 1.0f / Math.abs(offObj.z_far - offObj.z_near), 0.0f };

			switch (mode) {
			case 0:
				gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE,
						GL2.GL_OBJECT_LINEAR);
				gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE,
						GL2.GL_OBJECT_LINEAR);

				gl.glTexGenfv(GL2.GL_S, GL2.GL_OBJECT_PLANE, yPlane, 0);
				gl.glTexGenfv(GL2.GL_T, GL2.GL_OBJECT_PLANE, zPlane, 0);

				break;
			case 1:
				gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
				gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
				gl.glTexGenfv(GL2.GL_S, GL2.GL_EYE_PLANE, yPlane, 0);
				gl.glTexGenfv(GL2.GL_T, GL2.GL_EYE_PLANE, zPlane, 0);
				break;
			case 2:
				gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
				gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
				break;
			}

			gl.glEnable(GL2.GL_TEXTURE_GEN_S);
			gl.glEnable(GL2.GL_TEXTURE_GEN_T);
			
			gl.glBegin(GL2.GL_POLYGON);
			for (int j = 0; j < offObj.num_verts_in_face[i]; j++) { // for each vert
				gl.glNormal3fv(offObj.normal_to_face[i], 0);
				int n = offObj.verts_in_face[i][j];
				gl.glVertex3d(offObj.vertices[n][0], offObj.vertices[n][1],
						offObj.vertices[n][2]);
			}
			gl.glEnd();

			gl.glDisable(GL2.GL_TEXTURE_GEN_S);
			gl.glDisable(GL2.GL_TEXTURE_GEN_T);
		}
		gl.glEndList();
	}

	@Override
	void draw_self() {
		GL2 gl = drawable.getGL().getGL2();

		tex.enable(gl);
		tex.bind(gl);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glPushMatrix();
		gl.glTranslated(x, y, z);
		gl.glCallList(my_display_list);
		gl.glPopMatrix();

		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	@Override
	void hit(Peon by) {
		// TODO Auto-generated method stub

	}

	@Override
	void hit(Projectile by) {
		// TODO Auto-generated method stub

	}

	@Override
	void hit(Hero by) {
		// TODO Auto-generated method stub

	}

	@Override
	void hit(Boss b) {
		// TODO Auto-generated method stub

	}

	@Override
	void hit(Goal l) {
		// TODO Auto-generated method stub

	}
}