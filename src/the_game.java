import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class the_game extends JFrame implements GLEventListener, KeyListener, MouseInputListener {
	private static final long serialVersionUID = 1L;
	static final double sneak = 0.75, walk = 1.5, sprint = 3.0;
	static final float[] SPAWN = { 0.0f };

	static final float WALLHEIGHT = 100.0f; // Some playing field parameters
	static final float ARENASIZE = 1000.0f;
	static final float EYEHEIGHT = 30.0f;

	static final float[][] ARENA_VERTS = { { -ARENASIZE, 0.0f, ARENASIZE }, { -ARENASIZE, 0.0f, -ARENASIZE * 2 },
			{ ARENASIZE * 2, 0.0f, ARENASIZE }, { ARENASIZE * 2, 0.0f, -ARENASIZE * 2 },
			{ -ARENASIZE, WALLHEIGHT, ARENASIZE }, { -ARENASIZE, WALLHEIGHT, -ARENASIZE * 2 },
			{ ARENASIZE * 2, WALLHEIGHT, ARENASIZE }, { ARENASIZE * 2, WALLHEIGHT, -ARENASIZE * 2 } };

	static final String env = "env.png";
	static final String floor = "magma.jpg";
	static final String vil = "magma.jpg";
	static final String boss = "moose1.png";
	static final String boss2 = "teddy.png";
	static final String wall = "env.png";
	static final String cross = "crosshair.png";
	static final String hit = "hit.png";
	static final String helix = "concrete.jpg";

	static GLU glu;
	static GLUT glut;
	static GLCapabilities caps;
	static FPSAnimator animator;
	static GLJPanel canvas;
	static Robot num5;

	static double upx = 0.0, upy = 1.0, upz = 0.0; // gluLookAt params
	static double fov = 60.0; // gluPerspective params
	static double near = 1.0;
	static double far = 10000.0;
	double aspect;

	static int width = 1024; // canvas size
	static int height = 768;
	static int mapOffset = width / 8;
	static int centerX = 0; // canvas center
	static int centerY = 0;
	static int prevX = 0;

	static boolean antialias = true;

	static float ga[] = { 0.05f, 0.05f, 0.05f, 1.0f }; // global ambient

	static float lp0[] = { ARENA_VERTS[3][0], 1000.0f, ARENA_VERTS[3][2], 1.0f }; // pos
	static float la0[] = { 0.05f, 0.05f, 0.05f, 1.0f }; // light 0 ambient
	static float ld0[] = { 0.3f, 0.2f, 0.2f, 1.0f }; // light 0 diffuse
	static float ls0[] = { 0.4f, 0.3f, 0.3f, 1.0f }; // light 0 specular

	static float lp1[] = { 0.0f, 0.0f, 0.0f, 1.0f }; // light 1 position
	static float la1[] = { 0.2f, 0.2f, 0.4f, 1.0f }; // light 1 ambient
	static float ld1[] = { 0.4f, 0.4f, 0.9f, 1.0f }; // light 1 diffuse
	static float ls1[] = { 0.2f, 0.2f, 0.4f, 1.0f }; // light 1 specular

	static float ma[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // material ambient
	static float md[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // material diffuse
	static float ms[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // material specular
	static int me = 13; // shininess exponent

	static float red[] = { 1.0f, 0.0f, 0.0f, 1.0f }; // pure red
	static float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f }; // pure blue
	static float yellow[] = { 1.0f, 1.0f, 0.0f, 1.0f }; // pure yellow
	static float black[] = { 0.0f, 0.0f, 0.0f, 1.0f }; // pure black
	static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f }; // pure white
	static float gray[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // pure gray
	static float green[] = { 0.0f, 1.0f, 0.0f, 1.0f }; // pure green

	static int displayListBase;

	static Hero the_hero;
	static Boss the_boss;
	static ArrayList<Peon> peons;
	static ArrayList<Goal> goals;
	static HashSet<Projectile> projectiles;
	static ArrayList<TexPoly> arena;
	static Fence fence;

	Texture hitTex;

	static int score = 0;
	static double move, strafe, speed;
	static int turn;
	static boolean fire = false;
	static long startTime, time;

	public the_game() {
		super("the_game");
	}

	public static void main(String[] args) {
		move = strafe = 0.0;
		turn = 0;
		speed = walk;
		startTime = System.currentTimeMillis();

		peons = new ArrayList<Peon>();
		goals = new ArrayList<Goal>();
		projectiles = new HashSet<Projectile>();
		arena = new ArrayList<TexPoly>();

		caps = new GLCapabilities(GLProfile.getGL2GL3());
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		canvas = new GLJPanel();

		the_game myself = new the_game();
		canvas.addGLEventListener(myself);
		canvas.addKeyListener(myself);
		canvas.addMouseListener(myself);
		canvas.addMouseMotionListener(myself);
		// canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		animator = new FPSAnimator(canvas, 60);

		JFrame frame = new JFrame("the_game");
		frame.setSize(width, height);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(canvas);
		frame.setVisible(true);

		frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
				new Point(0, 0), "null"));

		try {
			num5 = new Robot();
		} catch (AWTException e) {
			System.err.println("Number 5 is not alive. :(\n");
			e.printStackTrace();
		}
		myself.run();
	}

	public void run() {
		animator.start();
	}

	double[] loc(double i, double j) {
		return new double[] { i * ARENASIZE / 10.0, 0.0, j * -ARENASIZE / 10.0 };
	}

	double[] loc(double i, double j, double y) {
		return new double[] { i * ARENASIZE / 10.0, y, j * -ARENASIZE / 10.0 };
	}

	Texture loadTex(String file) {
		Texture t = null;
		String format = "";
		
		switch (file.substring(file.lastIndexOf('.')).toLowerCase()) {
		case ".jpg":
			format = TextureIO.JPG;
			break;
		case ".png":
			format = TextureIO.PNG;
			break;
		case ".gif":
			format = TextureIO.GIF;
			break;
		default:
			System.out.println("Invalid texture file: " + file);
			break;
		}

		try {
			t = TextureIO.newTexture(getClass().getClassLoader().getResourceAsStream(file), false, format);
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}

		return t;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		gl.glEnable(GL.GL_CULL_FACE); // Why? because we are in a box
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);

		if (antialias) {
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		} else {
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		}

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glEnable(GLLightingFunc.GL_LIGHT1);

		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, la0, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, ld0, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, ls0, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lp0, 0);

		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_AMBIENT, la1, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE, ld1, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPECULAR, ls1, 0);
		gl.glLightf(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_CONSTANT_ATTENUATION, 1.0f);
		// gl.glLightf(GLLightingFunc.GL_LIGHT1,
		// GLLightingFunc.GL_LINEAR_ATTENUATION, 0.001f);
		gl.glLightf(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_QUADRATIC_ATTENUATION, 0.00003f);
		gl.glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, ga, 0);

		aspect = (double) width / (double) height;
		displayListBase = gl.glGenLists(13);

		the_hero = new Hero(loc(1, 1, 27.5f), 13, 13.0, gray, loadTex(env), loadTex(cross), displayListBase + 1,
				drawable);
		the_boss = new Boss(loc(9, 9), 0, 26.0, blue, loadTex(boss), loadTex(boss2), true, displayListBase + 2,
				drawable);
		fence = new Fence(displayListBase + 3, drawable);
		arena.add(new TexPoly(gl, ARENA_VERTS[2], ARENA_VERTS[3], ARENA_VERTS[1], ARENA_VERTS[0], 15.0f, 15.0f,
				loadTex(floor), true));
		goals.add(new Goal(loc(7, 3, 5.0), 0, 20.0, yellow, loadTex(floor), true, displayListBase + 4, drawable, "pear",
				2, 3.0));
		goals.add(new Goal(loc(3, 7, 5.0), 0, 20.0, yellow, loadTex(helix), true, displayListBase + 5, drawable,
				"helix2", 0, 3.0));
		goals.add(new Goal(loc(5, 5, -5.0), 0, 20.0, yellow, loadTex(floor), true, displayListBase + 6, drawable,
				"volks", 1, 0.75));

		peons.add(new Peon(loc(2, 8), 0, 6.0, null, false, displayListBase + 10, drawable));
		peons.add(new Peon(loc(4, 6), 30, 8.0, null, false, displayListBase + 11, drawable));
		peons.add(new Peon(loc(6, 4), 350, 8.0, null, false, displayListBase + 12, drawable));
		peons.add(new Peon(loc(8, 2), 40, 9.0, null, false, displayListBase + 13, drawable));
		peons.add(new Peon(loc(2, 2), 70, 10.0, null, false, displayListBase + 14, drawable));
		peons.add(new Peon(loc(4, 4), 290, 10.0, null, false, displayListBase + 15, drawable));
		peons.add(new Peon(loc(6, 6), 220, 12.0, null, false, displayListBase + 16, drawable));

		hitTex = loadTex(hit);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		mapOffset = width / 5;
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);

		recenter();
		the_hero.move(move * speed);
		the_hero.strafe(strafe * speed);
		the_hero.turn(turn);

		// first-person
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(fov, aspect, near, far);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(the_hero.x, EYEHEIGHT, the_hero.z, the_hero.x + the_hero.xdir, EYEHEIGHT,
				the_hero.z + the_hero.zdir, upx, upy, upz);

		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION, the_game.lp1, 0);

		the_hero.draw_self(); // must draw first, for env map
		fence.draw_self();
		for (TexPoly v : arena)
			v.draw_self();
		if (fire) {
			projectiles.add(new Projectile(new double[] { the_hero.x, 30.0, the_hero.z }, the_hero.deg, 1.0, white,
					hitTex, displayListBase + 0, drawable));
			fire = false;
		}

		Iterator<?> it = peons.iterator();
		for (Peon p; it.hasNext();) {
			p = (Peon) it.next();
			p.draw_self();
			if (p.hp <= 0)
				it.remove();
		}
		it = goals.iterator();
		for (Goal p; it.hasNext();) {
			p = (Goal) it.next();
			p.draw_self();
			if (p.hp <= 0)
				it.remove();
		}
		it = projectiles.iterator();
		for (Projectile p; it.hasNext();) {
			p = (Projectile) it.next();
			p.draw_self();
			if (p.hp <= 0)
				it.remove();
		}
		the_boss.draw_self();
		the_hero.draw_crosshair(); // draw last

		// end first-person

		// minimap
		gl.glViewport(width - mapOffset, height - mapOffset, mapOffset, mapOffset);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-500, 500, -500, 500, 0, 200);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(ARENASIZE / 2, 100.0, -ARENASIZE / 2, ARENASIZE / 2, 0.0, -ARENASIZE / 2, 0.0, 0.0, -1.0);

		the_hero.draw_mini();
		for (TexPoly v : arena)
			v.draw_mini();
		for (Projectile p : projectiles)
			p.draw_mini();
		for (Peon v : peons)
			v.draw_mini();
		for (Goal l : goals)
			l.draw_mini();
		the_boss.draw_mini();
		// end minimap

		// timer
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		gl.glViewport(width - mapOffset, height - mapOffset - 30, mapOffset, 30);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluOrtho2D(-1.0, 1.0, -1.0, 1.0);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glColor3f(0.9f, 0.9f, 0.9f);
		gl.glRasterPos2f(-0.9f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, getTime());

		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d(-1.0, -1.0);
		gl.glVertex2d(1.0, -1.0);
		gl.glVertex2d(1.0, 1.0);
		gl.glVertex2d(-1.0, 1.0);
		gl.glEnd();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glFlush();
	}

	String getTime() {
		time = peons.size() > 0 ? System.currentTimeMillis() : time;
		return "Time: " + Long.toString((time - startTime) / 60000) + ":"
				+ Long.toString(((time - startTime) / 1000) % 60) + "." + Long.toString((time - startTime) % 1000);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		width = w;
		height = h;
		aspect = (double) width / (double) height;

		// find center
		centerX = canvas.getLocationOnScreen().x + width / 2;
		centerY = canvas.getLocationOnScreen().y + height / 2;
		recenter();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { // Nothing
																										// for
																										// us
																										// to
																										// do
																										// here
	}

	void recenter() {
		num5.mouseMove(centerX, centerY);
		prevX = centerX;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyTyped(KeyEvent key) {
	}

	@Override
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread() {
				@Override
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		case KeyEvent.VK_S: // Move backward
			move = -0.67;
			break;
		case KeyEvent.VK_W: // Move forward
			move = 1.33;
			break;
		case KeyEvent.VK_A: // strafe left
			strafe = -1.0;
			break;
		case KeyEvent.VK_D: // strafe right
			strafe = 1.0;
			break;
		case KeyEvent.VK_SHIFT: // gogogo!
			speed = sprint;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_S: // Move backward
			move = 0.0;
			break;
		case KeyEvent.VK_W: // Move forward
			move = 0.0;
			break;
		case KeyEvent.VK_A: // strafe left
			strafe = 0.0;
			break;
		case KeyEvent.VK_D: // strafe right
			strafe = 0.0;
			break;
		case KeyEvent.VK_SHIFT: // gogogo!
			speed = walk;
		default:
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		centerX = canvas.getLocationOnScreen().x + width / 2;
		centerY = canvas.getLocationOnScreen().y + height / 2;
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
		case 1:
			fire = true;
			break;
		case 3:
			fov = 20.0;
			speed = sneak;
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case 1:
			break;
		case 3:
			fov = 60.0;
			speed = walk;
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println(e.getXOnScreen() + "," + e.getYOnScreen());
		turn = (e.getXOnScreen() - prevX) / 10;
	}
}
