package Controllers;




import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
import engine.*;

public class DominionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private Speler huidigeSpeler;
	
	
    public DominionServlet() {
        super();
    }
    private void kopen(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
    	JSONObject jsonObj = new JSONObject();
    	String gekozenKaart = request.getParameter("kaart");
    	System.out.println(engine.geefHuidigeSpeler().geefAflegStapel().size());
    	
    	System.out.print(gekozenKaart);
    	int index = 0;

		for (int i = 0; i < engine.geefLijstKaartenVanHetSpel().size(); i++) {
			if (gekozenKaart.equals(engine.geefLijstKaartenVanHetSpel().get(i).geefNaam())) {
				index = i;
				engine.brengGekochteKaartNaarAflegstapel(engine.geefLijstKaartenVanHetSpel().get(i));
			}
		int kost = engine.geefLijstKaartenVanHetSpel().get(index).geefKost();
		
		engine.geefHuidigeSpeler().verminderGeld(kost);
		engine.geefHuidigeSpeler().verminderAankoop(1);
		System.out.println(engine.geefHuidigeSpeler().geefAflegStapel().size());
		jsonObj.put("naam", gekozenKaart);
		jsonObj.put("kost", kost);
		
		response.getWriter().write(jsonObj.toString());
		}
		
    }
    
  
    private void spelersToevoegen(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {	
    	JSONObject jsonObj = new JSONObject();
    	String spelerNaam = request.getParameter("speler1");
    	String spelerNaam2 = request.getParameter("speler2");
    	
    	String spelers[] = {spelerNaam, spelerNaam2};
    	engine.maakSpelersAan(spelers);
    	//this.huidigeSpeler = engine.geefHuidigeSpeler();
		jsonObj.put("Speler1", spelers[0]);
		jsonObj.put("Speler2", spelers[1]);
		response.getWriter().write(jsonObj.toString());
		
    }
	
    private void geefKaartenInHandVanDeHuidigeSpeler(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
    	JSONArray arrayObj = new JSONArray();
    	
    	
		for(int i=0; i<engine.geefHuidigeSpeler().geefKaartenInHand().size();i++){
			arrayObj.put(i, engine.geefHuidigeSpeler().geefKaartenInHand().get(i).geefNaam());
		}
		
		response.getWriter().write(arrayObj.toString());
    }
    
   

    private void genereerActieKaart(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
    	
    	JSONArray arrayObj = new JSONArray();
    	
    	List<Kaart> actieKaarten = engine.actiekaartenGenereren();
		for(int i=0; i <10;i++){
			arrayObj.put(i, actieKaarten.get(i).geefNaam());
		}
		response.getWriter().write(arrayObj.toString());
    }
   

	 private void geefInfoOverDeKaart(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
		 JSONArray arrayObj = new JSONArray();
    	String gekozenKaart = request.getParameter("kaart");
    	String info ;
    
    	
    	for (int i = 0; i < engine.geefLijstKaartenVanHetSpel().size() ; i++) {
    		
			if (gekozenKaart.equals( engine.geefLijstKaartenVanHetSpel().get(i).geefNaam())) {
				System.out.println(engine.geefLijstKaartenVanHetSpel().get(i).geefInfo());
				info = engine.geefLijstKaartenVanHetSpel().get(i).geefInfo();
				arrayObj.put(0,info);
				break;
				
			}
			
    	}
    
    	
    	response.getWriter().write(arrayObj.toString());
    	
    	
    }
	 private void huidigeWaarden(HttpServletRequest request, HttpServletResponse response,SpelFuncties engine) throws ServletException, IOException {
	    	JSONArray arrayObj = new JSONArray();
	    	
	    	arrayObj.put(0,"Speler: "+engine.geefHuidigeSpeler().geefNaam());
	    	arrayObj.put(1,"Geld: "+engine.geefHuidigeSpeler().geefGeld());
	    	arrayObj.put(2,"Aankoop: "+engine.geefHuidigeSpeler().geefAankoop());
	    	arrayObj.put(3,"Acties: "+engine.geefHuidigeSpeler().geefActie());
	    	response.getWriter().write(arrayObj.toString());
	    }
	 private void geefHuidigSpeelVeld(HttpServletRequest request, HttpServletResponse response, SpelFuncties engine) throws ServletException, IOException {
	    	JSONArray arrayObj = new JSONArray();
	    	
			for(int i=0; i< engine.geefHuidigeSpeler().geefSpeelGebied().size();i++){
				arrayObj.put(i, engine.geefHuidigeSpeler().geefSpeelGebied().get(i).geefNaam());
			}
			response.getWriter().write(arrayObj.toString());
			
	    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		//response.setContentType("application/json");
		
		SpelFuncties gameEngine = (SpelFuncties) request.getServletContext().getAttribute("SpelFuncties");
		if(gameEngine == null)
		{
			gameEngine = new SpelFuncties();
			request.getServletContext().setAttribute("SpelFuncties", gameEngine);
			
		}
		
		switch(request.getParameter("operation"))
		{
		case "spelerToevoegen":
			spelersToevoegen(request, response, gameEngine);
			break;
			
		case "geefKaartenInHand":
			geefKaartenInHandVanDeHuidigeSpeler(request, response, gameEngine);
			break;
		case "actieKaartenGeneren":
			genereerActieKaart(request, response, gameEngine);
			break;
		case "stopBeurt":
			gameEngine.brengAlleKaartenNaarAflegstapel();
			gameEngine.spelNogNietBeëindigd();
			gameEngine.geefHuidigeSpeler().herstelWaarden();
			gameEngine.volgendeSpeler();
			
			break;
		case "kaartenKopen":
			kopen(request, response, gameEngine);
			break;
		case "huidigeWaarden":
			
			huidigeWaarden(request,response,gameEngine);
			break;
		case "infoOphalen":
			geefInfoOverDeKaart(request,response,gameEngine);
			break;
		case "toonSpeelVeld":
			gameEngine.brengAlleGeldkaartenUitHandNaarSpeelGebied();
			geefHuidigSpeelVeld(request, response, gameEngine);
			break;
		case "trekKaartInHand":
			gameEngine.trekKaartVanTrekStapel(5);
			
		
		default:
			break;
		}	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().append("hello world vanuit post");
	}
}