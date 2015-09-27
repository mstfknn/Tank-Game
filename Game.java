import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enigma.console.TextAttributes;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;

public class Game {
	public static int sec = 0;
	public static int min = 0;
	public static int time=0;
	public static TextAttributes attrs;
	public static  enigma.console.Console cn = Enigma.getConsole("Mouse and Keyboard");
	public static int[][] maparr = new int[19][49];
	public static CircularQueue bst = new CircularQueue(8);
	public static Coordinate bcr=new Coordinate(0,0);
	public static  Boost [] crbst=new Boost[500];
	public static  int boostb=0;
	public static  int countb=0,countf=0,countl=0;

	public static Coordinate ptank1 = new Coordinate(1, 1);
	public static Coordinate ptank2 = new Coordinate(47, 17);
	public static Player pTanks1 = new Player(ptank1, 200, 3,">");
	public static Player pTanks2 = new Player(ptank2, 200, 3,"^");

	public static Coordinate etank1 = new Coordinate(1,16);
	public static Coordinate etank2 = new Coordinate(45, 1);
	public static Enemy eTanks1 = new Enemy(etank1, 200, 3,"<");
	public static Enemy eTanks2 = new Enemy(etank2, 200, 3,"v");
	public static int ply1bullet=3,ply2bullet=3,enmy1bullet=3,enmy2bullet=3;

	public static Stack psb = new Stack(1000);
	public static Stack temppsb = new Stack(1000);
	public static boolean flagpplayer1=true;
	public static boolean flagplayer2=true;
	public static TextMouseListener tmlis; 
	public static KeyListener klis; 

	// ------ Standard variables for mouse and keyboard ------
	public static int keypr;   // key pressed?
	public static int rkey;    // key   (for press/release)
	// ----------------------------------------------------


	public static void GamePlay() throws Exception {   // --- Contructor


		try {
			Fileop();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cn.getTextWindow().setCursorPosition(ptank1.getX(),ptank1.getY());
		TextAttributes text=new TextAttributes(Color.BLUE);
		cn.setTextAttributes(text);
		System.out.println(">");
		Bullet b=new Bullet(pTanks1.getCoordinate());
		for (int i = 0; i < 3; i++) {
			pTanks1.addBullet(b);
		}



		text=new TextAttributes(Color.GREEN);
		cn.getTextWindow().setCursorPosition(ptank2.getX(), ptank2.getY());
		cn.setTextAttributes(text);
		System.out.println("^");
		b=new Bullet(pTanks2.getCoordinate());
		for (int i = 0; i < 3; i++) {
			pTanks2.addBullet(b);
		}



		text=new TextAttributes(Color.RED);
		cn.getTextWindow().setCursorPosition(etank1.getX(), etank1.getY());
		cn.setTextAttributes(text);
		System.out.println("<");
		b=new Bullet(eTanks1.getCoordinate());
		for (int i = 0; i < 3; i++) {
			eTanks1.addBullet(b);
		}


		text=new TextAttributes(Color.YELLOW);
		cn.getTextWindow().setCursorPosition(etank2.getX(), etank2.getY());
		cn.setTextAttributes(text);
		System.out.println("v");
		b=new Bullet(eTanks2.getCoordinate());
		for (int i = 0; i < 3; i++) {
			eTanks2.addBullet(b);
		}



		klis=new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(keypr==0) {
					keypr=1;
					rkey=e.getKeyCode();
				}
			}
			public void keyReleased(KeyEvent e) {}
		};
		cn.getTextWindow().addKeyListener(klis);


		char ch='>',ch2='^';


		int p1x=pTanks1.getCoordinate().getX(),p1y=pTanks1.getCoordinate().getY();
		int p2x=pTanks2.getCoordinate().getX(),p2y=pTanks2.getCoordinate().getY();
		bst = new CircularQueue(8);
		bst=Boosts(bst);
		while(true){
			Thread.sleep(350);
			time++;
			enemyTank_1();
			enemyTank_2();
			text=new TextAttributes(Color.GRAY);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(52, 20);
			System.out.println("Time: "+time);

			if(time%10==0){
				bcr=new Coordinate(0,0);
				bcr=coordinate();

				text=new TextAttributes(Color.YELLOW);
				cn.setTextAttributes(text);
				cn.getTextWindow().setCursorPosition(bcr.getX(), bcr.getY());
				System.out.println(bst.peek());

				if(bst.peek().toString().equals("B")){
					countb--;
					addBoost("B");

				}

				else if(bst.peek().toString().equals("F")){
					countf--;
					addBoost("F");

				}
				else if(bst.peek().toString().equals("L")){
					countl--;
					addBoost("L");

				}

				bst.dequeue();
				bst=Boosts(bst);

			}	
			for (int i = 10; i < 18; i++) {


				cn.getTextWindow().setCursorPosition(i, 21);
				System.out.print(bst.peek());
				bst.enqueue(bst.dequeue());
			}



			Display();


			if(keypr==1) { 
				if(flagpplayer1==true){
					if(rkey==KeyEvent.VK_W || rkey==KeyEvent.VK_S||rkey==KeyEvent.VK_A||rkey==KeyEvent.VK_D||rkey==KeyEvent.VK_SPACE) {
						if(rkey==KeyEvent.VK_W){
							playerTank_1();
							ch2='^';
							pTanks1.setDirection("^");
						}
						if(rkey==KeyEvent.VK_S){
							playerTank_1();
							ch2='v';
							pTanks1.setDirection("v");
						}
						if(rkey==KeyEvent.VK_A){
							playerTank_1();
							ch2='<';
							pTanks1.setDirection("<");
						}
						if(rkey==KeyEvent.VK_D){
							playerTank_1();
							ch2='>';
							pTanks1.setDirection(">");
						}
						if(rkey==KeyEvent.VK_SPACE)
							playerTank_1();
					}
				}

				if(flagplayer2==true){
					if(rkey==KeyEvent.VK_UP || rkey==KeyEvent.VK_DOWN||rkey==KeyEvent.VK_LEFT||rkey==KeyEvent.VK_RIGHT||rkey==KeyEvent.VK_ENTER) {
						if(rkey==KeyEvent.VK_UP){
							playerTank_2();
							ch2='^';
							pTanks2.setDirection("^");
						}
						if(rkey==KeyEvent.VK_DOWN){
							playerTank_2();
							ch2='v';
							pTanks2.setDirection("v");
						}
						if(rkey==KeyEvent.VK_LEFT){
							playerTank_2();
							ch2='<';
							pTanks2.setDirection("<");
						}
						if(rkey==KeyEvent.VK_RIGHT){
							playerTank_2();
							ch2='>';
							pTanks2.setDirection(">");
						}
						if(rkey==KeyEvent.VK_ENTER)
							playerTank_2();
					}
				}

				p1x=pTanks1.getCoordinate().getX();
				p1y=pTanks1.getCoordinate().getY();

			}
			keypr=0;
		}
	}


	public static void Fileop()throws IOException {//file operation, importing map

		BufferedReader reader=null;
		try{
			File map =new File("map.txt"); 
			reader=new BufferedReader(new FileReader(map));
			String line;
			String[][] mapp=new String[19][49];
			int count=0;
			while ((line = reader.readLine()) != null) {
				for (int j = 0; j < maparr[0].length; j++) {
					mapp[count][j]=line.substring(j, j+1);
				}
				count++;
				System.out.println(line);
			}

			for (int i = 0; i < 19; i++) {
				for (int j = 0; j < 49; j++) {
					if (mapp[i][j].equals("#")) 
						maparr[i][j] = 1;
					if (mapp[i][j].equals(" ")){
						maparr[i][j] = 0;
					}
				}

			}

		}
		catch(Exception e){
			System.out.println("File Not Found..");
		}


	}

	public static void Display() throws IOException, InterruptedException{// display function

		TextAttributes text=new TextAttributes(Color.BLUE);

		cn.setTextAttributes(text);
		cn.getTextWindow().setCursorPosition(52, 0);
		System.out.println("Player Tank 1");
		cn.getTextWindow().setCursorPosition(52, 1);
		System.out.println("Fuel  :"+pTanks1.getFuel());
		cn.getTextWindow().setCursorPosition(52, 2);
		System.out.println("Bullet:"+ply1bullet);
		cn.getTextWindow().setCursorPosition(52, 3);
		System.out.println("Life  :"+pTanks1.getLife());


		text=new TextAttributes(Color.GREEN);
		cn.setTextAttributes(text);
		cn.getTextWindow().setCursorPosition(52, 5);
		System.out.println("Player Tank 2");
		cn.getTextWindow().setCursorPosition(52, 6);
		System.out.println("Fuel  :"+pTanks2.getFuel());
		cn.getTextWindow().setCursorPosition(52, 7);
		System.out.println("Bullet:"+ply2bullet);
		cn.getTextWindow().setCursorPosition(52, 8);
		System.out.println("Life  :"+pTanks2.getLife());

		text=new TextAttributes(Color.RED);
		cn.setTextAttributes(text);
		cn.getTextWindow().setCursorPosition(52, 10);
		System.out.println("Computer Tank 1");
		cn.getTextWindow().setCursorPosition(52, 11);
		System.out.println("Fuel  :"+eTanks1.getFuel());
		cn.getTextWindow().setCursorPosition(52, 12);
		System.out.println("Bullet:"+enmy1bullet);
		cn.getTextWindow().setCursorPosition(52, 13);
		System.out.println("Life  :"+eTanks1.getLife());

		text=new TextAttributes(Color.YELLOW);
		cn.setTextAttributes(text);
		cn.getTextWindow().setCursorPosition(52, 15);
		System.out.println("Computer Tank 2");
		cn.getTextWindow().setCursorPosition(52, 16);
		System.out.println("Fuel  :"+eTanks2.getFuel());
		cn.getTextWindow().setCursorPosition(52, 17);
		System.out.println("Bullet:"+enmy2bullet);
		cn.getTextWindow().setCursorPosition(52, 18);
		System.out.println("Life  :"+eTanks2.getLife());
		cn.getTextWindow().setCursorPosition(0, 20);
		System.out.println("          --------"      );
		cn.getTextWindow().setCursorPosition(0, 21);
		System.out.println("Boost: >   " );
		cn.getTextWindow().setCursorPosition(0, 22);
		System.out.println("          --------"      );

	}
	public static CircularQueue Boosts(CircularQueue bst){//for boost queue
		Random rnd = new Random();

		int a =0;


		while(!bst.isFull()) {


			a = rnd.nextInt(10)+1;

			if (( a == 1 ||  a==2 || a==3 )){

				bst.enqueue('B');
				countb++;}
			else if((a == 4 || a == 5 || a==6 || a==7 || a==8 || a==9)){
				bst.enqueue('F');
				countf++;}
			else if(a == 10){
				bst.enqueue('L');
				countl++;}
		}
		return bst;

	}
	public static Coordinate coordinate(){// for coordination
		boolean flag=false;
		Random rnd = new Random();
		int x=0 ;
		int y=0 ;

		while(flag==false)
		{
			x = rnd.nextInt(17)+1;
			y = rnd.nextInt(47)+1;
			if(maparr[x][y]!=1 && pTanks1.getCoordinate().getX()!=x && pTanks1.getCoordinate().getY()!=y && pTanks2.getCoordinate().getX()!=x && pTanks2.getCoordinate().getY()!=y && eTanks1.getCoordinate().getX()!=x && eTanks1.getCoordinate().getY()!=y  && pTanks1.getCoordinate().getY()!=y && pTanks2.getCoordinate().getX()!=x && pTanks2.getCoordinate().getY()!=y)
			{
				flag=true;
				bcr=new Coordinate(y,x);

			}
			else if(maparr[x][y]==1)
			{
				flag=false;
			}
		}




		return bcr;
	}

	public static void CreateBoost(){//creating boosts
		cn.getTextWindow().setCursorPosition(bcr.getX(), bcr.getY());
		System.out.println(bst.peek());
		String bz = new String();
		bz = String.valueOf(bst.peek());

		if(bz=="B"){
			countb--;
			addBoost("B");

		}

		else if(bz=="F"){
			countf--;
			addBoost("F");

		}
		else if(bst.peek().equals("L")){
			countl--;
			addBoost("L");

		}

		bst.dequeue();
		bst=Boosts(bst);


		for (int i = 10; i < 18; i++) {


			cn.getTextWindow().setCursorPosition(i, 21);
			System.out.print(bst.peek());
			bst.enqueue(bst.dequeue());
		}


	}


	public static void addBoost(String b){//boostlist
		crbst[boostb]=new Boost(bcr, b);
		boostb++;
	}


	public static void playerTank_1(){//player tank 1
		cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());  //bir önceki konumunu silmek için
		cn.getTextWindow().output(" ",new TextAttributes(Color.BLACK,Color.BLACK));


		attrs = new TextAttributes(Color.BLUE, Color.BLACK);
		cn.setTextAttributes(attrs);
		if(rkey==KeyEvent.VK_W){
			pTanks1.getCoordinate().setY(pTanks1.getCoordinate().getY()-1); 
			if(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()] == 1 ||( pTanks2.getCoordinate().getX() == pTanks1.getCoordinate().getX() && pTanks1.getCoordinate().getY() == pTanks2.getCoordinate().getY()) &&( pTanks1.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks1.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks1.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks1.getCoordinate().getX()==eTanks2.getCoordinate().getX())
					) pTanks1.getCoordinate().setY(pTanks1.getCoordinate().getY()+1); //duvar varsa hareket engellenmiþ oalcak		
			else{
				pTanks1.setFuel(pTanks1.getFuel()-1);

				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()]=1;
				maparr[pTanks1.getCoordinate().getY()+1][pTanks1.getCoordinate().getX()]=0;
			}

			cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
			System.out.println('^');


		}
		if(rkey==KeyEvent.VK_S){
			pTanks1.getCoordinate().setY(pTanks1.getCoordinate().getY()+1);
			if(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()] == 1 || ( pTanks2.getCoordinate().getX() == pTanks1.getCoordinate().getX() && pTanks1.getCoordinate().getY() == pTanks2.getCoordinate().getY()&&( pTanks1.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks1.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks1.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks1.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					)pTanks1.getCoordinate().setY(pTanks1.getCoordinate().getY()-1);      //duvar varsa hareket engellenmiþ oalcak
			else{
				pTanks1.setFuel(pTanks1.getFuel()-1);

				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()]=1;
				maparr[pTanks1.getCoordinate().getY()-1][pTanks1.getCoordinate().getX()]=0;
			}


			cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
			System.out.println('v');



		}
		if(rkey==KeyEvent.VK_A){
			pTanks1.getCoordinate().setX(pTanks1.getCoordinate().getX()-1);
			if(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()] == 1 || ( pTanks2.getCoordinate().getX() == pTanks1.getCoordinate().getX() && pTanks1.getCoordinate().getY() == pTanks2.getCoordinate().getY()&&( pTanks1.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks1.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks1.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks1.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					)pTanks1.getCoordinate().setX(pTanks1.getCoordinate().getX()+1); //duvar varsa hareket engellenmiþ oalcak

			else{
				pTanks1.setFuel(pTanks1.getFuel()-1);
				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()]=1;
				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()+1]=0;
			}


			cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
			System.out.println('<');


		}
		if(rkey==KeyEvent.VK_D){
			pTanks1.getCoordinate().setX(pTanks1.getCoordinate().getX()+1);
			if(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()] == 1 || ( pTanks2.getCoordinate().getX() == pTanks1.getCoordinate().getX() && pTanks1.getCoordinate().getY() == pTanks2.getCoordinate().getY()&&( pTanks1.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks1.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks1.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks1.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					)pTanks1.getCoordinate().setX(pTanks1.getCoordinate().getX()-1); //duvar varsa hareket engellenmiþ oalcak
			else{
				pTanks1.setFuel(pTanks1.getFuel()-1);
				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()]=1;
				maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()-1]=0;
			}


			cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());

			System.out.println('>');



			attrs = new TextAttributes(Color.WHITE, Color.BLACK);
			cn.setTextAttributes(attrs);
		}

		if(rkey==KeyEvent.VK_SPACE){

			// Bullet yoksa if conditiona girmez. Tankın önceki konumunu ekranda gösteren cod bu if'e bağlı
			if(ply1bullet>0){

				if(pTanks1.getDirection()=="^"){
					int i=1;
					while(maparr[pTanks1.getCoordinate().getY()-i][pTanks1.getCoordinate().getX()]!=1){
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY()-i);
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
						System.out.println('^');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY()-i);
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('^');//Tankın önceki konumunu ekranda gösteren cod
				}

				if(pTanks1.getDirection()==">"){
					int i=1;
					while(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()+i]!=1){
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX()+i, pTanks1.getCoordinate().getY());
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
						System.out.println('>');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX()+i, pTanks1.getCoordinate().getY());
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('>');
				}

				if(pTanks1.getDirection()=="v"){
					int i=1;
					while(maparr[pTanks1.getCoordinate().getY()+i][pTanks1.getCoordinate().getX()]!=1){
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY()+i);
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
						System.out.println('v');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY()+i);
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('v');

				}


				if(pTanks1.getDirection()=="<"){
					int i=1;
					while(maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()-i]!=1){
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX()-i, pTanks1.getCoordinate().getY());
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
						System.out.println('<');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX()-i, pTanks1.getCoordinate().getY());
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('<');
				}
			}
			if(ply1bullet==0){
				if(pTanks1.getDirection()=="^"){
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('^');
				}
				if(pTanks1.getDirection()=="v"){
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('v');
				}
				if(pTanks1.getDirection()=="<"){
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('<');
				}
				if(pTanks1.getDirection()==">"){
					cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
					System.out.println('>');
				}
			}

			if(ply1bullet>0)
				ply1bullet--; 
		}
		for (int i = 0; i < boostb; i++) {
			if(crbst[i].getCoordinate().getX()==pTanks1.getCoordinate().getX() && crbst[i].getCoordinate().getY()==pTanks1.getCoordinate().getY()){
				if(crbst[i].getType().equals("B")){
					ply1bullet=ply1bullet+3;
					crbst[i].setType(" ");
					break;
				}
				if(crbst[i].getType().equals("L")){
					pTanks1.setLife(pTanks1.getLife()+1);
					crbst[i].setType(" ");
					break;
				}
				if(crbst[i].getType().equals("F")){
					pTanks1.setFuel(pTanks1.getFuel()+200);
					crbst[i].setType(" ");
					break;
				}
			}
		}

	}
	public static void playerTank_2(){//player tank2
		cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());  //bir önceki konumunu silmek için
		cn.getTextWindow().output(" ",new TextAttributes(Color.BLACK,Color.BLACK));


		attrs = new TextAttributes(Color.GREEN, Color.BLACK);
		cn.setTextAttributes(attrs);
		if(rkey==KeyEvent.VK_UP){
			pTanks2.getCoordinate().setY(pTanks2.getCoordinate().getY()-1); 
			if(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()] == 1 ||( pTanks1.getCoordinate().getX() == pTanks2.getCoordinate().getX() && pTanks2.getCoordinate().getY() == pTanks1.getCoordinate().getY()&&( pTanks2.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks2.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks2.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks2.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					) pTanks2.getCoordinate().setY(pTanks2.getCoordinate().getY()+1); //duvar varsa hareket engellenmiþ oalcak		
			else{
				pTanks2.setFuel(pTanks2.getFuel()-1);
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()]=1;
				maparr[pTanks2.getCoordinate().getY()+1][pTanks2.getCoordinate().getX()]=0;
			}

			cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
			System.out.println('^');

		}
		if(rkey==KeyEvent.VK_DOWN){
			pTanks2.getCoordinate().setY(pTanks2.getCoordinate().getY()+1);

			if(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()] == 1 || ( pTanks1.getCoordinate().getX() == pTanks2.getCoordinate().getX() && pTanks2.getCoordinate().getY() == pTanks1.getCoordinate().getY()&&( pTanks2.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks2.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks2.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks2.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					){pTanks2.getCoordinate().setY(pTanks2.getCoordinate().getY()-1);  }    //duvar varsa hareket engellenmiþ oalcak
			else{
				pTanks2.setFuel(pTanks2.getFuel()-1);
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()]=1;
				maparr[pTanks2.getCoordinate().getY()-1][pTanks2.getCoordinate().getX()]=0;
			}


			cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
			System.out.println('v');



		}
		if(rkey==KeyEvent.VK_LEFT){
			pTanks2.getCoordinate().setX(pTanks2.getCoordinate().getX()-1);
			if(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()] == 1 || ( pTanks1.getCoordinate().getX() == pTanks2.getCoordinate().getX() && pTanks2.getCoordinate().getY() == pTanks1.getCoordinate().getY()&&( pTanks2.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks2.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks2.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks2.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					)pTanks2.getCoordinate().setX(pTanks2.getCoordinate().getX()+1); //duvar varsa hareket engellenmiþ oalcak
			else{
				pTanks2.setFuel(pTanks2.getFuel()-1);
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()]=1;
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()+1]=0;
			}



			cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
			System.out.println('<');


		}
		if(rkey==KeyEvent.VK_RIGHT){
			pTanks2.getCoordinate().setX(pTanks2.getCoordinate().getX()+1);
			if(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()] == 1 || ( pTanks1.getCoordinate().getX() == pTanks2.getCoordinate().getX() && pTanks2.getCoordinate().getY() == pTanks1.getCoordinate().getY()&&( pTanks2.getCoordinate().getY()==eTanks1.getCoordinate().getY()&&pTanks2.getCoordinate().getX()==eTanks1.getCoordinate().getX() &&pTanks2.getCoordinate().getY()==eTanks2.getCoordinate().getY()&& pTanks2.getCoordinate().getX()==eTanks2.getCoordinate().getX()))
					)pTanks2.getCoordinate().setX(pTanks2.getCoordinate().getX()-1); //duvar varsa hareket engellenmiþ oalcak

			else{
				pTanks2.setFuel(pTanks2.getFuel()-1);
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()]=1;
				maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()-1]=0;
			}

			cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());

			System.out.println('>');


			attrs = new TextAttributes(Color.WHITE, Color.BLACK);
			cn.setTextAttributes(attrs);
		}

		if(rkey==KeyEvent.VK_ENTER){

			// Bullet yoksa if conditiona girmez. Tankın önceki konumunu ekranda gösteren cod bu if'e bağlı
			if(ply2bullet>0){

				if(pTanks2.getDirection()=="^"){
					int i=1;
					while(maparr[pTanks2.getCoordinate().getY()-i][pTanks2.getCoordinate().getX()]!=1){
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY()-i);
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
						System.out.println('^');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY()-i);
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('^');//Tankın önceki konumunu ekranda gösteren cod
				}

				if(pTanks2.getDirection()==">"){
					int i=1;
					while(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()+i]!=1){
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX()+i, pTanks2.getCoordinate().getY());
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
						System.out.println('>');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX()+i, pTanks2.getCoordinate().getY());
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('>');
				}

				if(pTanks2.getDirection()=="v"){
					int i=1;
					while(maparr[pTanks2.getCoordinate().getY()+i][pTanks2.getCoordinate().getX()]!=1){
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY()+i);
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
						System.out.println('v');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY()+i);
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('v');

				}


				if(pTanks2.getDirection()=="<"){
					int i=1;
					while(maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()-i]!=1){
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX()-i, pTanks2.getCoordinate().getY());
						System.out.println('o');
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
						System.out.println('<');
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX()-i, pTanks2.getCoordinate().getY());
						System.out.println(' ');
						i++;
					}
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('<');
				}
			}
			if(ply2bullet==0){
				if(pTanks2.getDirection()=="^"){
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('^');
				}
				if(pTanks2.getDirection()=="v"){
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('v');
				}
				if(pTanks2.getDirection()=="<"){
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('<');
				}
				if(pTanks2.getDirection()==">"){
					cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
					System.out.println('>');
				}
			}

			if(ply2bullet>0)
				ply2bullet--; 
		}
		for (int i = 0; i < boostb; i++) {
			if(crbst[i].getCoordinate().getX()==pTanks2.getCoordinate().getX() && crbst[i].getCoordinate().getY()==pTanks2.getCoordinate().getY()){
				if(crbst[i].getType().equals("B")){
					ply2bullet=ply2bullet+3;
					crbst[i].setType(" ");
					break;
				}
				if(crbst[i].getType().equals("L")){
					pTanks2.setLife(pTanks2.getLife()+1);
					crbst[i].setType(" ");
					break;
				}
				if(crbst[i].getType().equals("F")){
					pTanks2.setFuel(pTanks2.getFuel()+200);
					crbst[i].setType(" ");
					break;
				}
			}
		}
	}

	public static void enemyTank_1() throws InterruptedException{//computer tank1

		Random rnd = new Random();
		int drc = rnd.nextInt(100)+1;
		drc=drc%5;
		while(drc==0)
		{
			drc = rnd.nextInt(100)+1;
			drc=drc%5;
		}
		if(drc==1)
		{
			eTanks1.setDirection("<");
			TextAttributes	text=new TextAttributes(Color.RED);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
			System.out.println(eTanks1.getDirection());

		}
		else if(drc==2)
		{eTanks1.setDirection("v");
		cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
		System.out.println(eTanks1.getDirection());

		}
		else if(drc==3)
		{
			eTanks1.setDirection("^");
			TextAttributes	text=new TextAttributes(Color.RED);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
			System.out.println(eTanks1.getDirection());

		}
		else{
			eTanks1.setDirection(">");
			TextAttributes	text=new TextAttributes(Color.RED);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
			System.out.println(eTanks1.getDirection());

		}


		int b1x=eTanks1.getCoordinate().getX();
		int b1y=eTanks1.getCoordinate().getY(); 
		for (int i = 0; i < boostb; i++) {
			if(crbst[i].getCoordinate().getX()==b1x && crbst[i].getCoordinate().getY()==b1y){
				if(crbst[i].getType().equals("B"))
					enmy1bullet+=3;
				else if(crbst[i].getType().equals("F"))
					eTanks1.setFuel(eTanks1.getFuel()+200);


				else if(crbst[i].getType().equals("L"))
					eTanks1.setLife(eTanks1.getLife()+1);
				Coordinate coor=new Coordinate(0,0);
				crbst[i].setCoordinate(coor);crbst[i].setType("e");
			}
		}

		if(eTanks1.getDirection()==">"){
			if(eTanks1.getFuel()<=80)
			{
				PathFinding1(">", eTanks1);
			}
			if(eTanks1.getDirection()==">" && maparr[eTanks1.getCoordinate().getY()][eTanks1.getCoordinate().getX()+1]==0 /*&& eTanks1.getCoordinate().getY()!=pTanks1.getCoordinate().getY() && eTanks1.getCoordinate().getX()+1!=pTanks1.getCoordinate().getX()&& eTanks1.getCoordinate().getY()!=pTanks2.getCoordinate().getY() && eTanks1.getCoordinate().getX()+1!=pTanks2.getCoordinate().getX()*/ ){
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.RED);
				cn.setTextAttributes(text);
				eTanks1.getCoordinate().setX(eTanks1.getCoordinate().getX()+1);
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(eTanks1.getDirection());
				eTanks1.setFuel(eTanks1.getFuel()-1);



				if(enmy1bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if(b1y==pTanks2.getCoordinate().getY()&&pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks2.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x++;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();

							while(b1x!=pTanks2.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);


							if(pTanks2.getLife()<=0){
								flagplayer2=false;
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
								System.out.println("BBB");
								maparr[pTanks2.getCoordinate().getY()][pTanks2.getCoordinate().getX()]=0;
							}
						}
					}
					if(b1y==pTanks1.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks1.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x++;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1x!=pTanks1.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());
							pTanks1.setLife(pTanks1.getLife()-1);
							if(pTanks1.getFuel()<=80)
							{
								PathFinding1(">", eTanks1);
							}

							if(pTanks1.getLife()<=0){
								flagpplayer1=false;
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println("AAA");
								maparr[pTanks1.getCoordinate().getY()][pTanks1.getCoordinate().getX()]=0;
							}
							enmy1bullet--;
							Thread.sleep(2000);
						}

					}
				}
			}




		}
		if(eTanks1.getDirection()=="<"){
			if(eTanks1.getFuel()<=80)
			{
				PathFinding1("<", eTanks1);
			}
			if(eTanks1.getDirection()=="<" && maparr[eTanks1.getCoordinate().getY()][eTanks1.getCoordinate().getX()-1]==0 /* && eTanks1.getCoordinate().getY()!=pTanks1.getCoordinate().getY() && eTanks1.getCoordinate().getX()-1!=pTanks1.getCoordinate().getX()&& eTanks1.getCoordinate().getY()!=pTanks2.getCoordinate().getY() && eTanks1.getCoordinate().getX()-1!=pTanks2.getCoordinate().getX()*/){
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.RED);
				cn.setTextAttributes(text);
				eTanks1.getCoordinate().setX(eTanks1.getCoordinate().getX()-1);
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(eTanks1.getDirection());
				eTanks1.setFuel(eTanks1.getFuel()-1);





				if(enmy1bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){

					if(b1y==pTanks1.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks1.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x--;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1x!=pTanks1.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);	
							} 


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.RED);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);
							if(pTanks1.getFuel()<=80)
							{
								PathFinding1("<", eTanks1);
							}

							if(pTanks1.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}
						}

					}
					if(b1y==pTanks2.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks2.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x--;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1x!=pTanks2.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);

							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
							enmy1bullet--;
							Thread.sleep(2000);
						}

					}


				}
			}


		}
		if(eTanks1.getDirection()=="^"){
			if(eTanks1.getFuel()<=80)
			{
				PathFinding1("^", eTanks1);
			}
			if(eTanks1.getDirection()=="^" && maparr[(eTanks1.getCoordinate().getY()-1)][eTanks1.getCoordinate().getX()]==0 /*&& eTanks1.getCoordinate().getY()-1!=pTanks1.getCoordinate().getY() && eTanks1.getCoordinate().getX()!=pTanks1.getCoordinate().getX()&& eTanks1.getCoordinate().getY()-1!=pTanks2.getCoordinate().getY() && eTanks1.getCoordinate().getX()!=pTanks2.getCoordinate().getX() */){
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.RED);
				cn.setTextAttributes(text);
				eTanks1.getCoordinate().setY(eTanks1.getCoordinate().getY()-1);
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(eTanks1.getDirection());
				eTanks1.setFuel(eTanks1.getFuel()-1);

				if(enmy1bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if( b1x==pTanks2.getCoordinate().getX() && pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks2.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y--;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1y!=pTanks2.getCoordinate().getY() && maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);
							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
						}
					}
					if(b1x==pTanks1.getCoordinate().getX()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks1.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y--;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1y!=pTanks1.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);
							if(pTanks1.getFuel()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}
							enmy1bullet--;
							Thread.sleep(2000);
						}

					}

				}
			}




		}
		if(eTanks1.getDirection()=="v"){
			if(eTanks1.getFuel()<=80)
			{
				PathFinding1("v", eTanks1);
			}
			if(eTanks1.getDirection()=="v" && maparr[(eTanks1.getCoordinate().getY()+1)][eTanks1.getCoordinate().getX()]==0 /* && eTanks1.getCoordinate().getY()+1!=pTanks1.getCoordinate().getY() && eTanks1.getCoordinate().getX()!=pTanks1.getCoordinate().getX()&& eTanks1.getCoordinate().getY()+1!=pTanks2.getCoordinate().getY() && eTanks1.getCoordinate().getX()!=pTanks2.getCoordinate().getX() */){

				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.RED);
				cn.setTextAttributes(text);
				eTanks1.getCoordinate().setY(eTanks1.getCoordinate().getY()+1);
				cn.getTextWindow().setCursorPosition(eTanks1.getCoordinate().getX(), eTanks1.getCoordinate().getY());
				System.out.println(eTanks1.getDirection());
				eTanks1.setFuel(eTanks1.getFuel()-1);



				if(enmy1bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if(b1x==pTanks2.getCoordinate().getX()&&pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks2.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y++;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1y!=pTanks2.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);
							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
						}
					}
					if(b1x==pTanks1.getCoordinate().getX()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks1.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y++;
							}
						}
						if(flag==true){
							b1x=eTanks1.getCoordinate().getX();
							b1y=eTanks1.getCoordinate().getY();
							while(b1y!=pTanks1.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.RED);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);

							if(pTanks1.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}

							enmy1bullet--;
							Thread.sleep(2000);
						}


					}



				}
			}



		}

	}

	public static void enemyTank_2() throws InterruptedException{//computer tank2

		Random rnd = new Random();
		int drc = rnd.nextInt(100)+1;
		drc=drc%5;
		while(drc==0)
		{
			drc = rnd.nextInt(100)+1;
			drc=drc%5;
		}
		if(drc==1)
		{
			eTanks2.setDirection("<");
			TextAttributes	text=new TextAttributes(Color.YELLOW);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
			System.out.println(eTanks2.getDirection());

		}
		else if(drc==2)
		{eTanks2.setDirection("v");
		cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
		System.out.println(eTanks2.getDirection());

		}
		else if(drc==3)
		{
			eTanks2.setDirection("^");
			TextAttributes	text=new TextAttributes(Color.YELLOW);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
			System.out.println(eTanks2.getDirection());

		}
		else{
			eTanks2.setDirection(">");
			TextAttributes	text=new TextAttributes(Color.YELLOW);
			cn.setTextAttributes(text);
			cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
			System.out.println(eTanks2.getDirection());

		}


		int b1x=eTanks2.getCoordinate().getX();
		int b1y=eTanks2.getCoordinate().getY(); 

		for (int i = 0; i < boostb; i++) {
			if(crbst[i].getCoordinate().getX()==b1x && crbst[i].getCoordinate().getY()==b1y){
				if(crbst[i].getType().equals("B"))
					enmy2bullet+=3;
				else if(crbst[i].getType().equals("F"))
					eTanks2.setFuel(eTanks2.getFuel()+200);
				else if(crbst[i].getType().equals("L"))
					eTanks2.setLife(eTanks2.getLife()+1);
				Coordinate coor=new Coordinate(0,0);
				crbst[i].setCoordinate(coor);crbst[i].setType("e");
			}
		}


		if(eTanks2.getDirection()==">"){
			if(eTanks2.getFuel()<=180)
			{
				PathFinding1(">", eTanks2);
			}

			if(eTanks2.getDirection()==">" && maparr[eTanks2.getCoordinate().getY()][eTanks2.getCoordinate().getX()+1]==0 /*&& eTanks2.getCoordinate().getY()!=pTanks1.getCoordinate().getY() && eTanks2.getCoordinate().getX()+1!=pTanks1.getCoordinate().getX()&& eTanks2.getCoordinate().getY()!=pTanks2.getCoordinate().getY() && eTanks2.getCoordinate().getX()+1!=pTanks2.getCoordinate().getX()*/ ){
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.YELLOW);
				cn.setTextAttributes(text);
				eTanks2.getCoordinate().setX(eTanks2.getCoordinate().getX()+1);
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(eTanks2.getDirection());
				eTanks2.setFuel(eTanks2.getFuel()-1);


				if(enmy2bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if(b1y==pTanks2.getCoordinate().getY()&&pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks2.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x++;
							}
						}
						if(flag==true){
							b1x=eTanks2.getCoordinate().getX();
							b1y=eTanks2.getCoordinate().getY(); 

							while(b1x!=pTanks2.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);

							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(),pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
						}
					}
					if(b1y==pTanks1.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks1.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x++;
							}
						}
						if(flag==true){
							b1x=eTanks2.getCoordinate().getX();
							b1y=eTanks2.getCoordinate().getY(); 
							while(b1x!=pTanks1.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());
							pTanks1.setLife(pTanks1.getLife()-1);

							if(pTanks1.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}
							enmy2bullet--;
							Thread.sleep(2000);
						}

					}
				}
			}




		}
		if(eTanks2.getDirection()=="<"){
			if(eTanks2.getFuel()<=180)
			{
				PathFinding1("<", eTanks2);
			}

			if(eTanks2.getDirection()=="<" && maparr[eTanks2.getCoordinate().getY()][eTanks2.getCoordinate().getX()-1]==0 /* && eTanks2.getCoordinate().getY()!=pTanks1.getCoordinate().getY() && eTanks2.getCoordinate().getX()-1!=pTanks1.getCoordinate().getX()&& eTanks2.getCoordinate().getY()!=pTanks2.getCoordinate().getY() && eTanks2.getCoordinate().getX()-1!=pTanks2.getCoordinate().getX()*/){
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.YELLOW);
				cn.setTextAttributes(text);
				eTanks2.getCoordinate().setX(eTanks2.getCoordinate().getX()-1);
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(eTanks2.getDirection());
				eTanks2.setFuel(eTanks2.getFuel()-1);





				if(enmy2bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){

					if(b1y==pTanks1.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks1.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x--;
							}
						}
						if(flag==true){
							b1x=pTanks1.getCoordinate().getX();
							b1y=pTanks1.getCoordinate().getY();
							while(b1x!=pTanks1.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);	
							} 


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.YELLOW);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);

							if(pTanks1.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}
						}

					}
					if(b1y==pTanks2.getCoordinate().getY()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1x!=pTanks2.getCoordinate().getX()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1x--;
							}
						}
						if(flag==true){
							b1x=eTanks2.getCoordinate().getX();
							b1y=eTanks2.getCoordinate().getY();
							while(b1x!=pTanks2.getCoordinate().getX()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1x--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);

							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
							enmy2bullet--;
							Thread.sleep(2000);
						}

					}


				}
			}


		}
		if(eTanks2.getDirection()=="^"){
			if(eTanks2.getFuel()<=180)
			{
				PathFinding1("^", eTanks2);
			}

			if(eTanks2.getDirection()=="^" && maparr[(eTanks2.getCoordinate().getY()-1)][eTanks2.getCoordinate().getX()]==0 /*&& eTanks2.getCoordinate().getY()-1!=pTanks1.getCoordinate().getY() && eTanks2.getCoordinate().getX()!=pTanks1.getCoordinate().getX()&& eTanks2.getCoordinate().getY()-1!=pTanks2.getCoordinate().getY() && eTanks2.getCoordinate().getX()!=pTanks2.getCoordinate().getX() */){
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.YELLOW);
				cn.setTextAttributes(text);
				eTanks2.getCoordinate().setY(eTanks2.getCoordinate().getY()-1);
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(eTanks2.getDirection());
				eTanks2.setFuel(eTanks2.getFuel()-1);

				if(enmy2bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if( b1x==pTanks2.getCoordinate().getX() && pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks2.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y--;
							}
						}
						if(flag==true){
							b1x=pTanks1.getCoordinate().getX();
							b1y=pTanks1.getCoordinate().getY();
							while(b1y!=pTanks2.getCoordinate().getY() && maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);
							if(pTanks2.getLife()<=0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
						}
					}
					if(b1x==pTanks1.getCoordinate().getX()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks1.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y--;
							}
						}
						if(flag==true){
							b1x=eTanks2.getCoordinate().getX();
							b1y=eTanks2.getCoordinate().getY();
							while(b1y!=pTanks1.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y--;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);
							if(pTanks1.getFuel()<=0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}
							enmy2bullet--;
							Thread.sleep(2000);
						}

					}

				}
			}




		}
		if(eTanks2.getDirection()=="v"){
			if(eTanks2.getFuel()<=180)
			{
				PathFinding1("v", eTanks2);
			}

			if(eTanks2.getDirection()=="v" && maparr[(eTanks2.getCoordinate().getY()+1)][eTanks2.getCoordinate().getX()]==0 /* && eTanks2.getCoordinate().getY()+1!=pTanks1.getCoordinate().getY() && eTanks2.getCoordinate().getX()!=pTanks1.getCoordinate().getX()&& eTanks2.getCoordinate().getY()+1!=pTanks2.getCoordinate().getY() && eTanks2.getCoordinate().getX()!=pTanks2.getCoordinate().getX() */){

				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(" ");
				TextAttributes	text=new TextAttributes(Color.YELLOW);
				cn.setTextAttributes(text);
				eTanks2.getCoordinate().setY(eTanks2.getCoordinate().getY()+1);
				cn.getTextWindow().setCursorPosition(eTanks2.getCoordinate().getX(), eTanks2.getCoordinate().getY());
				System.out.println(eTanks2.getDirection());
				eTanks2.setFuel(eTanks2.getFuel()-1);



				if(enmy2bullet!=0&&(((b1y==pTanks1.getCoordinate().getY()&&Math.abs(b1x-pTanks1.getCoordinate().getX())<=12)||(b1x==pTanks1.getCoordinate().getX()&&Math.abs(b1y-pTanks1.getCoordinate().getY())<=12))||((b1x==pTanks2.getCoordinate().getX()&&Math.abs(b1y-pTanks2.getCoordinate().getY())<=12)||(b1y==pTanks2.getCoordinate().getY()&&Math.abs(b1x-pTanks2.getCoordinate().getX())<=12)))){
					if(b1x==pTanks2.getCoordinate().getX()&&pTanks2.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks2.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y++;
							}
						}
						if(flag==true){
							b1x=pTanks1.getCoordinate().getX();
							b1y=pTanks1.getCoordinate().getY();
							while(b1y!=pTanks2.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.GREEN);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
							System.out.println(pTanks2.getDirection());
							pTanks2.setLife(pTanks2.getLife()-1);
							if(pTanks2.getLife()==0){
								cn.getTextWindow().setCursorPosition(pTanks2.getCoordinate().getX(), pTanks2.getCoordinate().getY());
								System.out.println(" ");
							}
						}
					}
					if(b1x==pTanks1.getCoordinate().getX()&&pTanks1.getLife()!=0){
						boolean flag =true;
						while(b1y!=pTanks1.getCoordinate().getY()){
							if(maparr[b1y][b1x]==1){
								flag=false;
								break;
							}
							else{
								flag=true;
								b1y++;
							}
						}
						if(flag==true){
							b1x=eTanks2.getCoordinate().getX();
							b1y=eTanks2.getCoordinate().getY();
							while(b1y!=pTanks1.getCoordinate().getY()&& maparr[b1y][b1x]==0){
								TextAttributes	txt=new TextAttributes(Color.YELLOW);
								cn.setTextAttributes(txt);
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println(" ");
								b1y++;
								cn.getTextWindow().setCursorPosition(b1x, b1y);
								System.out.println("o");
								Thread.sleep(125);
							}


							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(" ");
							text=new TextAttributes(Color.BLUE);
							cn.setTextAttributes(text);
							cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
							System.out.println(pTanks1.getDirection());

							pTanks1.setLife(pTanks1.getLife()-1);

							if(pTanks1.getLife()==0){
								cn.getTextWindow().setCursorPosition(pTanks1.getCoordinate().getX(), pTanks1.getCoordinate().getY());
								System.out.println(" ");
							}

							enmy2bullet--;
							Thread.sleep(2000);
						}


					}



				}
			}



		}

	}

	public static void PathFinding1(String l, Enemy en){//path finding

		String b = new String();
		int y = 0;
		PathCoor en1 = new PathCoor (en.getCoordinate(),"s");	
		psb.push(en1);
		Stack temp = new Stack(1000);
		for (int i = 0; i < crbst.length; i++) {
			if (crbst[i].getType() == l) {
				y = i;
			}
			break;
		}
		while(crbst[y].getCoordinate().getX() == en1.getCoordinate().getX() && crbst[y].getCoordinate().getY()==en1.getCoordinate().getY()){
			temp.push(psb.peek());

			if(!((((PathCoor)temp.peek()).getCoordinate().getY()+1) == 1)){
				PathCoor en2 = new PathCoor(en1.getCoordinate().getX(),(en1.getCoordinate().getY()+1), "d");
				psb.push(en2);
			}
			if (!((((PathCoor)temp.peek()).getCoordinate().getY()-1) == 1)){
				PathCoor en3 = new PathCoor(en1.getCoordinate().getX(),(en1.getCoordinate().getY()-1), "u");
				psb.push(en3);
			}

			if(!((((PathCoor)temp.peek()).getCoordinate().getX()-1) == 1)){
				PathCoor en4 = new PathCoor(en1.getCoordinate().getX()-1,(en1.getCoordinate().getY()), "l");
				psb.push(en4);
			}
			if(!((((PathCoor)temp.peek()).getCoordinate().getX()+1) == 1)){
				PathCoor en5 = new PathCoor(en1.getCoordinate().getX()+1,(en1.getCoordinate().getY()), "r");
				psb.push(en5);
			}
			if(((((PathCoor)temp.peek()).getCoordinate().getX()) ==  (((PathCoor)psb.peek()).getCoordinate().getX())) && ((((PathCoor)temp.peek()).getCoordinate().getY())==(((PathCoor)psb.peek()).getCoordinate().getY())) && ((((PathCoor)temp.peek()).getDirection()).equals(((((PathCoor)psb.peek()).getDirection()))))){
				String pk = new String();
				pk =((((PathCoor)psb.peek()).getDirection()));
				while(!(((PathCoor)psb.peek()).getDirection().equals(pk))){
					psb.pop();
				}
			}
		}
		//Coordinate co =new Coordinate(0, 0);
		//PathCoor c=new PathCoor(co,"l");
		int a = psb.size();
		for (int i = 0; i < a; i++){

			PathCoor p = new PathCoor();
			p = (PathCoor) psb.peek();

			cn.getTextWindow().setCursorPosition(p.getCoordinate().getX(),p.getCoordinate().getY());
			System.out.println(".");
			temppsb.push(psb.pop());



		}


	}


}


