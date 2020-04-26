package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

class Trozo {
	int fila, columna;

	public Trozo(int fila, int columna) {
		this.fila = fila;
		this.columna = columna;
	}
}

class Serpiente {
	List<Trozo>	trozos;
	int orientacion;
	boolean crecer;

	Serpiente(){
		trozos = new ArrayList<>();
		for (int i = 20; i > 15; i--) {
			trozos.add(new Trozo(10, i));
		}
		orientacion = 0;
	}

	Trozo cabeza(){
		return trozos.get(0);
	}

	Trozo cola(){
		return trozos.get(trozos.size()-1);
	}

	List<Trozo> cuerpo(){ return trozos.subList(1, trozos.size()-1); }

	public void avanzar() {

		if (orientacion == 90) {
			trozos.add(0, new Trozo(cabeza().fila + 1, cabeza().columna));
		} else if (orientacion == 0) {
			trozos.add(0, new Trozo(cabeza().fila, cabeza().columna + 1));
		} else if (orientacion == 270) {
			trozos.add(0, new Trozo(cabeza().fila - 1, cabeza().columna));
		} else if (orientacion == 180) {
			trozos.add(0, new Trozo(cabeza().fila, cabeza().columna - 1));
		}

		if (!crecer) {
			trozos.remove(trozos.size() - 1);
		}
		crecer = false;
	}

	void crecer(){
		crecer = true;
	}

	boolean come(Manzana manzana){
		return cabeza().fila == manzana.fila && cabeza().columna == manzana.columna;
	}

	boolean seCruza(){
		for (Trozo trozo: cuerpo()){
			if(cabeza().columna == trozo.columna && cabeza().fila == trozo.fila){
				return true;
			}
		}
		return false;
	}
}

class Manzana {
	int fila, columna;

	public Manzana(int fila, int columna) {
		this.fila = fila;
		this.columna = columna;
	}

	void mover(){
		fila = (int)(Math.random()*24);
		columna = (int)(Math.random()*32);
	}
}


public class SnakeGame extends ApplicationAdapter {
	ShapeRenderer sr;
	BitmapFont bitmapFont;
	int PIX = 20;

	Serpiente serpiente;
	Manzana manzana;

	float time;
	float alarm;

	boolean gameOver;
	
	@Override
	public void create () {
		bitmapFont = new BitmapFont();
		sr = new ShapeRenderer();
		sr.setColor(0,0,0,1);

		reset();
	}

	void reset(){
		serpiente = new Serpiente();
		manzana = new Manzana(10, 10);

		gameOver = true;
	}

	@Override
	public void render () {
		time += Gdx.graphics.getDeltaTime();

		if(gameOver){
			if(time > alarm){
				gameOver = false;
			}

			sr.begin(ShapeRenderer.ShapeType.Filled);
			sr.setColor(0.9f, 0.7f, 0.3f, 0.1f);
			sr.circle(320, 240, 100);
			sr.end();
			return;
		}

		Gdx.gl.glClearColor(0.8f, 0.85f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		if      (Gdx.input.isKeyPressed(Input.Keys.UP))    { serpiente.orientacion = 90; }
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { serpiente.orientacion = 0; }
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { serpiente.orientacion = 270; }
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { serpiente.orientacion = 180; }

		if(serpiente.come(manzana)){
			manzana.mover();
			serpiente.crecer();
		}

		if(serpiente.seCruza()){
			reset();
			alarm = time + 1.5f;
		}

		if (time > alarm) {
			alarm = time + 0.1f;

			serpiente.avanzar();
		}



		// cuadricula
//		sr.begin(ShapeRenderer.ShapeType.Line);
//		for (int i = 0; i < 480; i += PIX) {
//			for (int j = 0; j < 640; j += PIX) {
//				sr.line(j, 0, j, 480);
//				sr.line(0, i, 640, i);
//			}
//		}
//		sr.end();

		sr.begin(ShapeRenderer.ShapeType.Filled);

		sr.setColor(0.9f, 0.4f, 0.3f, 1);
		sr.circle(serpiente.cabeza().columna * PIX, serpiente.cabeza().fila * PIX, PIX / 2);

		sr.setColor(0.3f, 0.9f, 0.3f, 1);
		sr.circle(serpiente.cola().columna * PIX, serpiente.cola().fila * PIX, PIX / 2);

		sr.setColor(0.3f, 0.4f, 0.9f, 1);
		for (Trozo trozo : serpiente.cuerpo()) {
			sr.circle(trozo.columna * PIX, trozo.fila * PIX, PIX / 2);
		}

		sr.setColor(0.4f, 0.1f, 0.3f, 1);
		sr.circle(manzana.columna*PIX, manzana.fila*PIX, PIX/2);

		sr.end();
	}
}
